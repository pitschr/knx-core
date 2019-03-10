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

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import li.pitschmann.knx.link.Configuration;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.BodyFactory;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ControlChannelRelated;
import li.pitschmann.knx.link.body.DataChannelRelated;
import li.pitschmann.knx.link.body.hpai.HPAI;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.exceptions.KnxCommunicationException;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.knx.link.header.Header;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Networker;
import li.pitschmann.utils.Sleeper;
import li.pitschmann.utils.WrappedMdcRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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
                .setting("executor.pool.communication", "3") // 3 instead of 10
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

    public String getReceivedBodiesAsString() {
        return this.getReceivedBodies().stream().map(b -> b.getClass().getSimpleName()).collect(Collectors.joining(System.lineSeparator()));
    }

    public List<Body> getSentBodies() {
        return Collections.unmodifiableList(this.sentBodies);
    }

    public String getSentBodiesAsString() {
        return this.getSentBodies().stream().map(b -> b.getClass().getSimpleName()).collect(Collectors.joining(System.lineSeparator()));
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
        final var sw = Stopwatch.createStarted();

        final var knxActions = Lists.<KnxMockServerAction>newLinkedList();
        knxActions.add(KnxMockServerWaitAction.NEXT); // KNX Mock Server is always waiting for first request from client
        for (final var knxCommand : knxCommands) {
            knxActions.addAll(KnxMockServerAction.parse(knxCommand));
        }
        final var actionRunnable = new ActionRunnable(knxActions);

        if (LOG.isDebugEnabled()) {
            final var sb = new StringBuilder(knxActions.size() * 100);
            sb.append("Number of KNX commands: ")
                    .append(knxActions.size())
                    .append(" (original: ")
                    .append(knxCommands.length)
                    .append(')')
                    .append(System.lineSeparator());
            for (var i = 0; i < knxActions.size(); i++) {
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
        final var heartbeatMonitor = Executors.newSingleThreadExecutor();
        heartbeatMonitor.submit(new WrappedMdcRunnable(new HeartbeatMonitorRunnable()));
        heartbeatMonitor.shutdown();

        // start KNX Action runnable
        final var actionExecutor = Executors.newSingleThreadExecutor();
        actionExecutor.execute(new WrappedMdcRunnable(actionRunnable));
        actionExecutor.shutdown();

        try (final var selector = Selector.open();
             final var channel = newDatagramChannel()) {
            // prepare channel for non-blocking and register to selector
            channel.register(selector, channel.validOps());
            LOG.trace("Channel {} registered to selector: {}", channel, selector);

            this.socket = channel.socket();
            this.ready = true;
            LOG.debug("Server Channel created and listening on port: {}", getPort());

            var i = 0;
            // iterate until current thread is interrupted
            while (!actionRunnable.isCompleted() && !Thread.currentThread().isInterrupted()) {
                // Give few time to breathe. If there is a high load of 10'000+ packets per second the
                // receiver buffer on KNX Mock Server may be full which results into a PortUnreachableException
                // on KNX client side because KNX Mock Server is not accepting more packets.
                if (i++ % 10 == 0) {
                    Sleeper.milliseconds(1);
                }

                try {
                    selector.select();
                    final var selectedKeys = selector.selectedKeys().iterator();
                    while (selectedKeys.hasNext()) {
                        final var key = selectedKeys.next();
                        selectedKeys.remove();

                        if (key.isValid() && key.isReadable()) {
                            this.read(key);
                        }
                        if (key.isValid() && key.isWritable() && !this.outbox.isEmpty()) {
                            this.send(key);
                        }
                    }
                } catch (final Throwable ioe) {
                    LOG.error("Throwable during KnxMockServer", ioe);
                    throw ioe;
                    // break loop due exception
                }
            }

            LOG.info("Stop KNX Mock Server initiated (isCompleted={}, isInterrupted={}, duration={} ms)", actionRunnable.isCompleted(), Thread.currentThread().isInterrupted(), sw.elapsed(TimeUnit.MILLISECONDS));
            this.completed = true;
            this.ready = false;

            // sleep bit to get e.g. DisconnectResponse from client
            Sleeper.seconds(1);
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
            final var channel = DatagramChannel.open();
            channel.configureBlocking(false);
            final var socket = channel.socket();
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
        final var byteBuffer = ByteBuffer.allocate(0xFF);
        final var channel = (DatagramChannel) key.channel();
        final var address = channel.receive(byteBuffer);

        // not available yet - simply cancel this method
        if (address == null) {
            return;
        }
        key.attach(address);

        final var body = BodyFactory.valueOf(byteBuffer.array());

        // fetch HPAI
        if (body instanceof ConnectRequestBody) {
            final var connectReqBody = (ConnectRequestBody) body;
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
        final var body = this.outbox.take();

        final byte[] packetBytes;
        // Special handling for Erroneous body
        if (body instanceof KnxMockServerSendAction.ErroneousBody) {
            packetBytes = body.getRawData();
        }
        // Non-Errorneous body
        else {
            // header
            final var header = Header.create(body);
            final var headerRawData = header.getRawData();

            // body
            final var bodyRawData = body.getRawData();

            // packet: header + body
            packetBytes = Bytes.concat(headerRawData, bodyRawData);
        }

        final var byteBuffer = ByteBuffer.wrap(packetBytes);
        final var channel = (DatagramChannel) key.channel();

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
     * Waits until the mock server is completed (and quit gracefully). Waiting up to 30s seconds only!
     *
     * @throws KnxMockServerQuitException in case it takes longer than 30 seconds to avoid an infinity loop.
     */
    public void waitForCompletion() {
        final var maxMs = 30000; // 30s
        final var start = System.currentTimeMillis();
        // wait until mock server is finished
        while (!this.completed && !Thread.interrupted()) {
            Sleeper.milliseconds(100);
            LOG.trace("Waiting for completion");
            if (System.currentTimeMillis() > start + maxMs) {
                LOG.error("It took too long, I'll abort the waitForCompletion() method!\n" +
                        "Received bodies:\n" +
                        "-------------------\n" +
                        "{}\n" +
                        "Sent bodies:\n" +
                        "-------------------\n" +
                        "{}", this.getReceivedBodiesAsString(), this.getSentBodiesAsString());
                throw new KnxMockServerQuitException();
            }
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
        var found = false;
        var occurrences = 0;
        for (final var body : new ArrayList<>(bodies)) {
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

            while (System.currentTimeMillis() - lastHeartbeat < 10000 && !Thread.currentThread().isInterrupted()) {
                Sleeper.milliseconds(300);
            }
            Closeables.closeQuietly(socket);

            // Client already closed?
            if (!client.isClosed()) {
                Closeables.closeQuietly(client);
                LOG.debug("Client closed by KNX Mock Server.");
            }
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
            for (final var knxAction : knxActions) {
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
                final var duration = waitAction.getDuration();
                final var timeUnit = waitAction.getTimeUnit();
                LOG.debug("WAIT DELAY ({}/{}): {} {}", knxActionsIndex, knxActions.size(), duration, timeUnit.name());
                // delay
                Thread.sleep(timeUnit.toMillis(duration));
            } else if (waitAction.getWaitType() == KnxMockServerWaitAction.WaitType.TYPE) {
                final var expectedServiceType = waitAction.getServiceType();
                LOG.debug("WAIT FOR TYPE ({}/{}): {}", knxActionsIndex, knxActions.size(), expectedServiceType);

                // wait until the packet with expected service type is received
                var receivedCorrectServiceType = false;
                do {
                    Body body = null;
                    while ((inbox.isEmpty() || (body = inbox.take()) == null) && !Thread.currentThread().isInterrupted()) {
                        Sleeper.milliseconds(10);
                    }

                    if (body != null) {
                        final var receivedServiceType = body.getServiceType();

                        receivedCorrectServiceType = receivedServiceType == expectedServiceType;
                        if (!receivedCorrectServiceType) {
                            LOG.trace("Packet received, but will be ignored (got: {}, expected: {})", receivedServiceType, expectedServiceType);
                        }
                    }
                } while (!receivedCorrectServiceType && !Thread.currentThread().isInterrupted());
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
            final var body = sendAction.getBody();
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
