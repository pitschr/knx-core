/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2019 Pitschmann Christoph
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package li.pitschmann.test;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.common.primitives.Bytes;
import li.pitschmann.knx.link.*;
import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.body.hpai.*;
import li.pitschmann.knx.link.communication.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.knx.link.header.*;
import li.pitschmann.utils.*;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

/**
 * KNX Mock Server for testing purposes to simulate a KNX communication.
 *
 * @author PITSCHR
 */
public final class KnxMockServer implements Callable<KnxMockServer> {
    private static final Logger LOG = LoggerFactory.getLogger(KnxMockServer.class);
    private final String[] knxCommands;
    private final BlockingQueue<Body> inbox = new LinkedBlockingDeque<>();
    private final BlockingQueue<Body> outbox = new LinkedBlockingDeque<>();
    private boolean ready;
    private boolean completed;
    private long lastHeartbeat;
    private HPAI controlHPAI;
    private HPAI dataHPAI;
    private Exception exception;
    private DatagramSocket socket;
    private List<Body> receivedBodies = new ArrayList<>();
    private List<Body> sentBodies = new ArrayList<>();
    private KnxClient client;

    /**
     * KNX Mock Server with given String array of KNX commands that should be interacted by the KNX Mock Server.
     *
     * @param knxCommands
     */
    public KnxMockServer(final String[] knxCommands) {
        this.knxCommands = knxCommands;
    }

    /**
     * Creates a new instance of {@link KnxClient} and returns it with proper local host address and port.
     *
     * @return new instance of {@link KnxClient}
     */
    public KnxClient newKnxClient() {
        return this.client = new DefaultKnxClient(newConfigBuilder().build());
    }

    /**
     * Creates a new instance of {@link Configuration.Builder} for further usage
     *
     * @return An instance of {@link Configuration.Builder}
     */
    public Configuration.Builder newConfigBuilder() {
        Preconditions.checkArgument(getPort() > 0, "Knx Client cannot be returned when port is not defined.");
        Preconditions.checkArgument(this.ready, "Knx Client cannot be returned when KnxMockServer is not ready.");

        // provide a different configuration (e.g. timeouts are too long for tests)
        return Configuration.create(Networker.getLocalhost(), getPort())
                .setting("timeout.request.description", "1000") // 1s instead of 10s
                .setting("timeout.request.connect", "1000") // 1s instead of 10s
                .setting("timeout.request.disconnect", "1000") // 1s instead of 10s
                .setting("timeout.request.connectionstate", "1000") // 1s instead of 10s
                .setting("interval.connectionstate", "6000") // 6s instead of 60s
                .setting("timeout.alive.connectionstate", "12000") // 12s instead of 120s
                ;
    }

    public boolean isReady() {
        return this.ready;
    }

    public boolean hasException() {
        return this.exception != null;
    }

    public Exception getException() {
        return this.exception;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public List<Body> getReceivedBodies() {
        return Collections.unmodifiableList(this.receivedBodies);
    }

    public List<Body> getSentBodies() {
        return Collections.unmodifiableList(this.sentBodies);
    }

    public int getPort() {
        return this.socket == null ? 0 : this.socket.getLocalPort();
    }

    public HPAI getClientControlHPAI() {
        return controlHPAI;
    }

    public HPAI getClientDataHPAI() {
        return dataHPAI;
    }

    /**
     * Instantiates a {@link DatagramSocket} and listens to it and send KNX packets from knx actions
     * incrementally. The KNX mock server will quit as soon all KNX packets have been sent to client.
     *
     * @return {@link KnxMockServer} itself
     * @throws KnxException in case there was an issue with communication
     */
    @Override
    public KnxMockServer call() {
        Preconditions.checkArgument(!(this.ready || this.completed), "This method can be invoked only once time!");

        final List<KnxMockServerAction> knxActions = Lists.newLinkedList();
        knxActions.add(KnxMockServerWaitAction.NEXT); // KNX Mock Server is always waiting for first request from client
        for (var knxCommand : knxCommands) {
            knxActions.addAll(KnxMockServerAction.parse(knxCommand));
        }
        ActionRunnable actionRunnable = new ActionRunnable(knxActions);

        if (LOG.isDebugEnabled()) {
            final StringBuilder sb = new StringBuilder(knxActions.size() * 100);
            sb.append("Number of KNX commands: ")
                    .append(knxActions.size())
                    .append(" (original: ")
                    .append(knxCommands.length)
                    .append(')')
                    .append(System.lineSeparator());
            for (int i = 0; i < knxActions.size(); i++) {
                sb.append("  KNX Mock Action (")
                        .append(i + 1)
                        .append('/')
                        .append(knxActions.size())
                        .append("): ")
                        .append(knxActions.get(i))
                        .append(System.lineSeparator());
            }
            LOG.debug(sb.toString());
        }

        // start heartbeat monitor
        ExecutorService heartbeatMonitor = Executors.newSingleThreadExecutor();
        heartbeatMonitor.submit(new HeartbeatMonitorRunnable());
        heartbeatMonitor.shutdown();

        // start KNX Action runnable
        ExecutorService actionExecutor = Executors.newSingleThreadExecutor();
        actionExecutor.execute(actionRunnable);
        actionExecutor.shutdown();

        try (Selector selector = Selector.open();
             DatagramChannel channel = newDatagramChannel()) {
            // prepare channel for non-blocking and register to selector
            channel.register(selector, channel.validOps());
            LOG.trace("Channel {} registered to selector: {}", channel, selector);

            this.socket = channel.socket();
            this.ready = true;
            LOG.debug("Server Channel created and listening on port: {}", getPort());

            int i = 0;
            // iterate until current thread is interrupted
            while (!actionRunnable.isCompleted() && !Thread.interrupted()) {
                // Give few time to breathe. If there is a high load of 10'000+ packets per second the
                // receiver buffer on KNX Mock Server may be full which results into a PortUnreachableException
                // on KNX client side because KNX Mock Server is not accepting more packets.
                if (i++ % 10 == 0) {
                    Sleeper.milliseconds(1);
                }

                try {
                    selector.select();
                    final Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                    while (selectedKeys.hasNext()) {
                        final SelectionKey key = selectedKeys.next();
                        selectedKeys.remove();

                        if (key.isValid() && key.isReadable()) {
                            this.read(key);
                        }
                        if (key.isValid() && key.isWritable() && !this.outbox.isEmpty()) {
                            this.send(key);
                        }
                    }
                } catch (final Throwable ioe) {
                    LOG.error("IOException", ioe);
                    throw ioe;
                    // break loop due exception
                }
            }

            this.completed = true;
            this.ready = false;
            LOG.info("Stop KNX Mock Server initiated");

            long end = System.currentTimeMillis() + 1000;
            while (true) {
                if (System.currentTimeMillis() > end) {
                    break;
                }
            }
        } catch (final KnxMockServerQuitException quitException) {
            LOG.debug("Quit signal received from 'handleActions' method.");
        } catch (final Exception exception) {
            LOG.error("Exception thrown by KNX Mock Server", exception);
            this.exception = exception;
        } finally {
            Closeables.shutdownQuietly(heartbeatMonitor);
            Closeables.shutdownQuietly(actionExecutor);
            LOG.info("KNX Mock Server stopped.");
        }

        return this;
    }

    /**
     * Creates a new {@link DatagramChannel}
     *
     * @return
     */
    private DatagramChannel newDatagramChannel() {
        try {
            final DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            final DatagramSocket socket = channel.socket();
            socket.bind(new InetSocketAddress(0));
            socket.setSoTimeout(3000);
            return channel;
        } catch (final IOException e) {
            throw new KnxCommunicationException("Exception occurred during creating server socket channel", e);
        }
    }

    /**
     * Reads the next packets from {@link SelectionKey}
     *
     * @param key
     * @throws IOException
     */
    private void read(final SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(0xFF);
        DatagramChannel channel = (DatagramChannel) key.channel();
        SocketAddress address = channel.receive(byteBuffer);

        // not available yet - simply cancel this method
        if (address == null) {
            return;
        }
        key.attach(address);

        Body body = BodyFactory.valueOf(byteBuffer.array());

        // fetch HPAI
        if (body instanceof ConnectRequestBody) {
            final ConnectRequestBody connectReqBody = (ConnectRequestBody) body;
            // fetch the control and data ports
            controlHPAI = connectReqBody.getControlEndpoint();
            LOG.debug("Control HPAI: {}", controlHPAI);
            dataHPAI = connectReqBody.getDataEndpoint();
            LOG.debug("Data HPAI: {}", dataHPAI);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("RECEIVED BODY from address '{}': {}", address, body);
        }

        this.inbox.add(body);
        this.receivedBodies.add(body);
        this.lastHeartbeat = System.currentTimeMillis();
    }

    /**
     * Sends the next packet from {@link #outbox} to client
     *
     * @param key
     * @throws IOException
     */
    private void send(final SelectionKey key) throws IOException, InterruptedException {
        final Body body = this.outbox.take();

        final byte[] packetBytes;
        // Special handling for Erroneous body
        if (body instanceof KnxMockServerSendAction.ErroneousBody) {
            packetBytes = body.getRawData();
        }
        // Non-Errorneous body
        else {
            // header
            final Header header = Header.create(body);
            final byte[] headerRawData = header.getRawData();

            // body
            final byte[] bodyRawData = body.getRawData();

            // packet: header + body
            packetBytes = Bytes.concat(headerRawData, bodyRawData);
        }

        final ByteBuffer byteBuffer = ByteBuffer.wrap(packetBytes);
        final DatagramChannel channel = (DatagramChannel) key.channel();

        final SocketAddress address;
        if (body instanceof KnxMockServerSendAction.WrongChannelBody) {
            // to be sent to specific channel
            KnxMockServerSendAction.WrongChannelBody.ChannelType channelType = ((KnxMockServerSendAction.WrongChannelBody) body).getChannelType();
            switch (channelType) {
                case CONTROL:
                    address = new InetSocketAddress(controlHPAI.getAddress(), controlHPAI.getPort());
                    break;
                case DATA:
                    address = new InetSocketAddress(dataHPAI.getAddress(), dataHPAI.getPort());
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        } else if (body instanceof DataChannelRelated && dataHPAI != null) {
            address = new InetSocketAddress(dataHPAI.getAddress(), dataHPAI.getPort());
        } else if (body instanceof ControlChannelRelated && controlHPAI != null) {
            address = new InetSocketAddress(controlHPAI.getAddress(), controlHPAI.getPort());
        } else {
            // otherwise just use the sender form previous received diagram
            address = (SocketAddress) key.attachment();
        }
        channel.send(byteBuffer, address);

        this.sentBodies.add(body);
        this.lastHeartbeat = System.currentTimeMillis();

        LOG.debug("SEND BODY to address '{}': {}", address, body);
    }

    /**
     * Waits until the mock server is completed (and quitted).
     */
    public void waitForCompletion() {
        // wait until mock server is finished
        while (!this.completed && !Thread.interrupted()) {
            Sleeper.milliseconds(100);
            LOG.trace("Waiting for completion");
        }
    }

    /**
     * Waits until the packet with {@link ServiceType} is received
     *
     * @param serviceType
     */
    public void waitForReceivedServiceType(final ServiceType serviceType) {
        waitForReceivedServiceType(serviceType, 1);
    }

    /**
     * Waits until the packet with {@link ServiceType} is received
     *
     * @param serviceType
     * @param occurrence
     */
    public void waitForReceivedServiceType(final ServiceType serviceType, final int occurrence) {
        // wait until N-th occurrence of service type is in received packets
        while (!this.completed && !this.contains(this.receivedBodies, serviceType, occurrence) && !Thread.interrupted()) {
            Sleeper.milliseconds(100);
            LOG.trace("Waiting for service type: {}", serviceType);
        }
    }

    /**
     * Returns {@code true} when given {@code N-th} {@link ServiceType} is in list of {@link Body}
     *
     * @param bodies
     * @param serviceType
     * @param occurrence  use {@code 1} if you want to expect the first occurrence of {@link ServiceType}
     * @return {@code true} if found (and on N-th occurrence), otherwise {@code false}
     */
    private boolean contains(final List<Body> bodies, final ServiceType serviceType, final int occurrence) {
        // wait until mock server is finished
        boolean found = false;
        int occurrences = 0;
        for (final Body body : new ArrayList<>(bodies)) {
            if (body instanceof KnxMockServerSendAction.CorruptedBody) {
                LOG.warn("Skip unknown body: {}", body);
            } else if (body.getServiceType() == serviceType && ++occurrences == occurrence) {
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Asserts if given iterable of {@link Body} classes have been received by the KNX mock server
     *
     * @param bodyClasses
     */
    public final void assertReceivedPackets(final Iterable<Class<? extends Body>> bodyClasses) {
        assertThat(this.receivedBodies).hasSameSizeAs(bodyClasses);
        assertThat(this.isCompleted()).isTrue();
        assertThat(this.hasException()).isFalse();

        // verify if we are getting correct bodies
        final List<Class<? extends Body>> receivedBodyClasses = this.receivedBodies.stream().map(Body::getClass).collect(Collectors.toList());
        assertThat(bodyClasses).hasSameElementsAs(receivedBodyClasses);
    }

    /**
     * Asserts if given array of {@link Body} classes have been received by the KNX mock server
     *
     * @param bodyClasses
     */
    @SafeVarargs
    public final void assertReceivedPackets(final Class<? extends Body>... bodyClasses) {
        assertReceivedPackets(List.of(bodyClasses));
    }

    /**
     * Runnable for heartbeat monitoring in case KNX client doesn't respond as expected.
     */
    private class HeartbeatMonitorRunnable implements Runnable {

        @Override
        public void run() {
            LOG.trace("*** START HEARTBEAT MONITOR ***");
            lastHeartbeat = System.currentTimeMillis();

            while (System.currentTimeMillis() - lastHeartbeat < 10000 && !Thread.interrupted()) {
                Sleeper.milliseconds(300);
            }
            Closeables.closeQuietly(socket);
            Closeables.closeQuietly(client);
            LOG.debug("Client closed by KNX Mock Server.");
            LOG.trace("*** END HEARTBEAT MONITOR ***");
        }
    }

    private class ActionRunnable implements Runnable {
        private final Logger LOG = LoggerFactory.getLogger(ActionRunnable.class);
        private final List<KnxMockServerAction> knxActions;
        private int knxActionsIndex;
        private boolean completed;

        public ActionRunnable(final List<KnxMockServerAction> knxActions) {
            this.knxActions = Collections.unmodifiableList(knxActions);
        }

        @Override
        public void run() {
            LOG.trace("*** Start KnxMockServer#ActionRunnable ***");
            for (final KnxMockServerAction knxAction : knxActions) {
                knxActionsIndex++;
                // no action
                if (knxAction instanceof KnxMockServerNoAction) {
                    LOG.debug("NO ACTION ({}/{})", knxActionsIndex, knxActions.size());
                }
                // wait
                else if (knxAction instanceof KnxMockServerWaitAction) {
                    try {
                        doWait((KnxMockServerWaitAction) knxAction);
                    } catch (final InterruptedException ie) {
                        break;
                    }
                }
                // send packet
                else if (knxAction instanceof KnxMockServerSendAction) {
                    doSend((KnxMockServerSendAction) knxAction);
                }
                // unsupported action type
                else {
                    throw new IllegalArgumentException("Unsupported KNX mock action type received: " + knxAction);
                }
            }
            LOG.trace("*** End KnxMockServer#ActionRunnable ***");

            this.completed = true;
        }

        /**
         * Performs wait action. Either
         * <ol>
         * <li>Waiting for next packet ( {@link KnxMockServerWaitAction.WaitType#NEXT} )</li>
         * <li>Waiting for time / delay ( {@link KnxMockServerWaitAction.WaitType#DELAY} )</li>
         * <li>Waiting for a specific packet / service type ( {@link KnxMockServerWaitAction.WaitType#TYPE} )</li>
         * </ol>
         *
         * @param waitAction
         * @throws InterruptedException
         */
        private void doWait(final KnxMockServerWaitAction waitAction) throws InterruptedException {
            if (waitAction.getWaitType() == KnxMockServerWaitAction.WaitType.NEXT) {
                LOG.debug("WAIT FOR NEXT ({}/{})", knxActionsIndex, knxActions.size());
                // wait until inbox is not empty
                while ((inbox.isEmpty() || inbox.take() == null) && !Thread.interrupted()) {
                    Sleeper.milliseconds(10);
                }
            } else if (waitAction.getWaitType() == KnxMockServerWaitAction.WaitType.DELAY) {
                final long duration = waitAction.getDuration();
                final TimeUnit timeUnit = waitAction.getTimeUnit();
                LOG.debug("WAIT DELAY ({}/{}): {} {}", knxActionsIndex, knxActions.size(), duration, timeUnit.name());
                // delay
                Thread.sleep(timeUnit.toMillis(duration));
            } else if (waitAction.getWaitType() == KnxMockServerWaitAction.WaitType.TYPE) {
                final ServiceType expectedServiceType = waitAction.getServiceType();
                LOG.debug("WAIT FOR TYPE ({}/{}): {}", knxActionsIndex, knxActions.size(), expectedServiceType);

                // wait until the packet with expected service type is received
                boolean receivedCorrectServiceType = false;
                do {
                    Body body = null;
                    while ((inbox.isEmpty() || (body = inbox.take()) == null) && !Thread.interrupted()) {
                        Sleeper.milliseconds(10);
                    }

                    if (body != null) {
                        ServiceType receivedServiceType = body.getServiceType();

                        receivedCorrectServiceType = receivedServiceType == expectedServiceType;
                        if (!receivedCorrectServiceType) {
                            LOG.trace("Packet received, but will be ignored (got: {}, expected: {})", receivedServiceType, expectedServiceType);
                        }
                    }
                } while (!receivedCorrectServiceType && !Thread.interrupted());
                LOG.debug("Packet with expected service type received: {}", expectedServiceType);
            }
        }

        /**
         * Performs send action.
         * <p>
         * The bytes are read from
         *
         * @param sendAction
         */
        private void doSend(final KnxMockServerSendAction sendAction) {
            Body body = sendAction.getBody();
            LOG.debug("SEND ACTION ({}/{}): {}", knxActionsIndex, knxActions.size(), body);
            outbox.add(body);
        }

        public boolean isCompleted() {
            return completed;
        }
    }

    /**
     * Special Exception to inform the caller method to quit the KNX mock server.
     */
    private class KnxMockServerQuitException extends RuntimeException {
    }
}
