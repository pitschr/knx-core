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

package li.pitschmann.knx.core.test;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.DisconnectResponseBody;
import li.pitschmann.knx.core.body.address.IndividualAddress;
import li.pitschmann.knx.core.body.hpai.HPAI;
import li.pitschmann.knx.core.communication.BaseKnxClient;
import li.pitschmann.knx.core.communication.DefaultKnxClient;
import li.pitschmann.knx.core.config.ConfigBuilder;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.parser.KnxprojParser;
import li.pitschmann.knx.core.utils.Closeables;
import li.pitschmann.knx.core.utils.Executors;
import li.pitschmann.knx.core.utils.Networker;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.net.InetAddress;
import java.nio.channels.MembershipKey;
import java.nio.channels.MulticastChannel;
import java.nio.channels.Selector;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * KNX Mock Server
 * <p/>
 * This mock server is used to test and simulate the communication between
 * KNX client and KNX Net/IP device.
 */
public final class MockServer implements Runnable, Closeable {
    private static final Logger log = LoggerFactory.getLogger(MockServer.class);
    private static final AtomicInteger globalChannelIdPool = new AtomicInteger();
    private final AtomicInteger tunnelingRequestSequence = new AtomicInteger();
    private final BlockingQueue<Body> outbox = new LinkedBlockingDeque<>();
    private final List<Body> receivedBodies = Collections.synchronizedList(new LinkedList<>());
    private final List<Body> sentBodies = Collections.synchronizedList(new LinkedList<>());
    private final MockServerChannel serverChannel;
    private final MockServerTest mockServerAnnotation;
    private final ExecutorService executorService;
    private InetAddress multicastAddress;
    private HPAI hpai;
    private IndividualAddress individualAddress;
    private int channelId;
    private boolean ready;
    private boolean cancel;
    private Throwable throwable;
    private DefaultKnxClient client;
    private List<MembershipKey> membershipKeys;

    private MockServer(final MockServerTest mockServerAnnotation) {
        this.mockServerAnnotation = mockServerAnnotation;

        this.serverChannel = new MockServerDatagramChannel(mockServerAnnotation);

        this.executorService = Executors.newSingleThreadExecutor(true);
        this.executorService.execute(this);
        this.executorService.shutdown();
    }

    /**
     * Creates the KNX Mock Server and start it immediately based on test case configuration ( {@link MockServerTest} )
     *
     * @param mockServerAnnotation
     * @return started KNX Mock Server
     */
    public static MockServer createStarted(final @Nonnull MockServerTest mockServerAnnotation) {
        return new MockServer(Objects.requireNonNull(mockServerAnnotation));
    }

    @Override
    public void run() {
        log.info("*** KNX Mock Server [main] START ***");

        // set individual address
        individualAddress = IndividualAddress.of(15, 15, 242);

        // generate channel id [0 .. 255]
        this.channelId = globalChannelIdPool.incrementAndGet() % 256;
        log.info("Mock Server Channel ID: {}", this.channelId);

        // join multicast (used for e.g. discovery, routing, ...)
        multicastAddress = Networker.getByAddress(224, 0, 1, channelId);
        log.debug("Multicast Address: {}", multicastAddress);
        this.membershipKeys = Networker.joinChannels((MulticastChannel) this.serverChannel.getChannel(), multicastAddress);
        log.debug("Membership Keys: {}", membershipKeys);

        // Start executor service heartbeat monitor
        final var executorService = Executors.newSingleThreadExecutor(true);
        final var heartbeatMonitor = new MockServerHeartbeatMonitor(this);
        executorService.submit(heartbeatMonitor);
        executorService.shutdown();

        final var publisher = new SubmissionPublisher<Body>();
        // Subscribe KNX Mock Server Logic (mandatory)
        publisher.subscribe(Executors.wrapSubscriberWithMDC(new MockServerCommunicator(this, mockServerAnnotation)));

        // Subscribe KNX Mock Server Project Logic if KNX project path is defined
        if (!mockServerAnnotation.projectPath().isEmpty()) {
            final var xmlProjectPath = Paths.get(mockServerAnnotation.projectPath());
            Preconditions.checkArgument(Files.exists(xmlProjectPath), "Project file doesn't exists at: {}", xmlProjectPath);
            final var projectLogic = new MockServerProjectLogic(this, KnxprojParser.parse(xmlProjectPath));
            publisher.subscribe(Executors.wrapSubscriberWithMDC(projectLogic));
        }

        try (final var selector = Selector.open();
             this.serverChannel) {
            // channels, HPAIs
            this.hpai = HPAI.of(serverChannel.getChannel());
            log.info("Mock Server HPAI: {}", this.hpai);

            // prepare channel for non-blocking and register to selector
            serverChannel.getChannel().register(selector, this.serverChannel.getChannel().validOps());
            log.debug("Channel {} registered to selector: {}", this.serverChannel, selector);
            log.debug("Server Channel created and listening on port: {}", getPort());

            // mark mock server as "ready" for communication
            this.ready = true;

            while (!isCancelled()) {
                selector.select();
                final var selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    final var key = selectedKeys.next();
                    selectedKeys.remove();

                    // receive
                    if (key.isValid() && key.isReadable()) {
                        final var body = serverChannel.read(key);
                        heartbeatMonitor.ping();
                        this.receivedBodies.add(body);
                        publisher.submit(body);
                        if (log.isDebugEnabled()) {
                            log.debug("RECEIVED BODY from channel '{}': {}", key.channel(), body);
                        }
                    }
                    // send
                    if (key.isValid() && key.isWritable() && !this.outbox.isEmpty()) {
                        final var body = this.outbox.take();
                        serverChannel.send(key, body);
                        heartbeatMonitor.ping();
                        this.sentBodies.add(body);

                        if (log.isDebugEnabled()) {
                            log.debug("SENT BODY to channel '{}': {}", key.channel(), body);
                        }

                        // special behavior for disconnect as the disconnect
                        // was initiated by KNX mock server
                        if (body instanceof DisconnectRequestBody) {
                            log.debug("Stopping KNX mock server, because disconnect request packet was sent: {}", body);
                            CompletableFuture
                                    // wait until answer from client
                                    .runAsync(() -> this.waitForReceivedServiceType(ServiceType.DISCONNECT_RESPONSE))
                                    // then cancel it
                                    .thenRun(() -> this.cancel());
                        } else if (body instanceof DisconnectResponseBody) {
                            log.debug("Stopping KNX mock server, because disconnect response packet was sent: {}", body);
                            this.cancel();
                        }
                    }

                }
            }
        } catch (final Throwable t) {
            log.error("Throwable during KNX mock server", t);
            throwable = t;
        } finally {
            // drop membership keys if it exists
            if (this.membershipKeys != null) {
                membershipKeys.stream().forEach(key -> key.drop());
            }

            Closeables.shutdownQuietly(executorService);
            Closeables.closeQuietly(publisher);
            log.info("*** KNX Mock Server [main] END ***");
        }
    }

    /**
     * Return HPAI of KNX mock server
     *
     * @return HPAI
     */
    public HPAI getHPAI() {
        return hpai;
    }

    /**
     * Returns the channel id that is used by KNX mock server
     *
     * @return channel id
     */
    public int getChannelId() {
        return this.channelId;
    }

    /**
     * Returns the multicast address used by the KNX mock server
     *
     * @return multicast address
     */
    @Nonnull
    public InetAddress getMulticastAddress() {
        return Objects.requireNonNull(this.multicastAddress);
    }

    /**
     * Returns the port used by KNX mock server
     *
     * @return the port number
     */
    public int getPort() {
        return this.serverChannel.getPort();
    }

    /**
     * Returns if the KNX mock server is ready
     *
     * @return {@code true} if mock server is ready, otherwise {@code false}
     */
    public boolean isReady() {
        return this.ready;
    }

    /**
     * Cancels the KNX mock server and all KNX mock server related threads
     * are subject to be stopped immediately.
     */
    public void cancel() {
        this.cancel = true;
    }

    /**
     * Returns if the KNX mock server has been cancelled or interrupted.
     * But the KNX mock server may be still running until the thread is done.
     *
     * @return {@code true} if cancelled/interrupted, otherwise {@code false}
     */
    public boolean isCancelled() {
        return cancel || Thread.currentThread().isInterrupted();
    }

    /**
     * Waits for completion of KNX mock server and client (up to 30 seconds)
     *
     * @return {@code true} if mock server was completed gracefully, otherwise {@code false}
     */
    public boolean waitDone() {
        final var notInterrupted = Sleeper.milliseconds(100, () -> this.executorService.isTerminated(), 30000);

        if (!notInterrupted) {
            log.error("It took too long for waitDone(), here are bodies which were received/sent:\n" +
                    "Received bodies:\n" +
                    "-------------------\n" +
                    "{}\n" +
                    "Sent bodies:\n" +
                    "-------------------\n" +
                    "{}", this.getReceivedBodiesAsString(), this.getSentBodiesAsString());
        }

        return notInterrupted;
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
    public boolean waitForReceivedServiceType(final ServiceType serviceType, final int occurrence) {
        final var notInterrupted = Sleeper.milliseconds(100, () -> !isCancelled() && this.contains(new ArrayList<>(this.receivedBodies), serviceType, occurrence), 30000);

        if (!notInterrupted) {
            log.error("It took too long for 'waitForReceivedServiceType', here are bodies which were received/sent:\n" +
                    "Received bodies:\n" +
                    "-------------------\n" +
                    "{}\n" +
                    "Sent bodies:\n" +
                    "-------------------\n" +
                    "{}", this.getReceivedBodiesAsString(), this.getSentBodiesAsString());
        }

        return notInterrupted;
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
        final var count = bodies.stream().filter(body -> body.getServiceType() == serviceType).limit(occurrence).count();
        return count == occurrence;
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
     * Asserts if given iterable of {@link Body} classes have been received by the KNX mock server
     *
     * @param bodyClasses
     */
    public final void assertReceivedPackets(final Iterable<Class<? extends Body>> bodyClasses) {
        // set mock server as closed because we are verifying the received packets
        this.close();
        final var receivedBodiesCopy = this.getReceivedBodies();
        assertThat(receivedBodiesCopy).hasSameSizeAs(bodyClasses);
        assertThat(this.throwable).isNull(); // no throwable thrown during execution

        // verify if we are getting correct bodies
        final List<Class<? extends Body>> receivedBodyClasses = receivedBodiesCopy.stream().map(Body::getClass).collect(Collectors.toList());
        assertThat(bodyClasses).hasSameElementsAs(receivedBodyClasses);
    }

    /**
     * Returns the next sequence number for {@link li.pitschmann.knx.core.body.TunnelingRequestBody}.
     * Each call of this method will increment the
     *
     * @return next sequence number
     */
    public int getAndIncrementTunnelingRequestSequence() {
        // range of sequence is [0 .. 255]
        // after 255 it will start again with 0
        return tunnelingRequestSequence.getAndUpdate(i -> (i + 1) % 256);
    }

    /**
     * Returns the individual address of KNX mock server
     *
     * @return individual address
     */
    public IndividualAddress getIndividualAddress() {
        return individualAddress;
    }

    /**
     * Returns the list of received bodies (from KNX client to KNX mock server)
     *
     * @return unmodifiable list of received bodies
     */
    public List<Body> getReceivedBodies() {
        return Collections.unmodifiableList(new ArrayList<>(this.receivedBodies));
    }

    /**
     * Returns list of received bodies as a string
     *
     * @return string of received bodies, comma-separated
     */
    public String getReceivedBodiesAsString() {
        return this.receivedBodies.stream().map(b -> b.getClass().getSimpleName()).collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Returns the list of sent bodies (from KNX mock server to KNX client)
     *
     * @return unmodifiable list of sent bodies
     */
    public List<Body> getSentBodies() {
        return Collections.unmodifiableList(new ArrayList<>(this.sentBodies));
    }

    /**
     * Returns list of sent bodies as a string
     *
     * @return string of sent bodies, comma-separated
     */
    public String getSentBodiesAsString() {
        return this.sentBodies.stream().map(b -> b.getClass().getSimpleName()).collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Creates a new instance of {@link BaseKnxClient} for testing purposes and returns
     * it with local host address and port that is used by KNX mock server.
     *
     * @return new instance of {@link BaseKnxClient}
     */
    public DefaultKnxClient createTestClient() {
        Preconditions.checkArgument(this.client == null, "We already created a test client. Please reuse it!");
        return this.client = DefaultKnxClient.createStarted(newConfigBuilder().build());
    }

    /**
     * Returns the existing instance of {@link BaseKnxClient} for testing purposes
     *
     * @return current instance of {@link BaseKnxClient}, otherwise {@link IllegalStateException}
     */
    public DefaultKnxClient getTestClient() {
        Preconditions.checkState(this.client != null, "Please call 'createTestClient()' first");
        return this.client;
    }

    /**
     * Creates a new instance of {@link ConfigBuilder} for further usage
     *
     * @return An instance of {@link ConfigBuilder}
     */
    public ConfigBuilder newConfigBuilder() {
        Preconditions.checkArgument(getPort() > 0, "Knx Client cannot be returned when port is not defined.");

        final ConfigBuilder configBuilder;
        if (mockServerAnnotation.useRouting()) {
            final var address = getMulticastAddress();
            final var port = getPort();
            configBuilder = ConfigBuilder.create(address, port);
            log.info("Routing service will be used for mock server. Endpoint: {}:{}", address, port);
        } else if (mockServerAnnotation.useDiscovery()) {
            final var port = getPort();
            configBuilder = ConfigBuilder.create(Networker.getAddressUnbound(), port);
            log.info("Discovery service will be used for mock server. Endpoint: 0.0.0.0:{}", port);
        } else {
            final var address = Networker.getLocalHost();
            final var port = getPort();
            configBuilder = ConfigBuilder.create(address, port);
            log.info("Discovery service will NOT be used for mock server. Endpoint: {}:{}", address, port);
        }

        // provide a different configuration (e.g. timeouts are too long for tests)
        return configBuilder
                .setting(CoreConfigs.Multicast.ADDRESS, this.getMulticastAddress())
                .setting(CoreConfigs.Multicast.PORT, 0) // use random local port for multicast testing
                .setting(CoreConfigs.Multicast.TIME_TO_LIVE, 0) // consider local only (no pass by any router)
                .setting(CoreConfigs.Plugin.EXECUTOR_POOL_SIZE, 3) // 3 instead of 10
                .setting(CoreConfigs.Communication.EXECUTOR_POOL_SIZE, 3) // 3 instead of 10
                .setting(CoreConfigs.Search.REQUEST_TIMEOUT, 2000L) // 2s instead of 10s
                .setting(CoreConfigs.Description.REQUEST_TIMEOUT, 2000L) // 2s instead of 10s
                .setting(CoreConfigs.Connect.REQUEST_TIMEOUT, 2000L) // 2s instead of 10s
                .setting(CoreConfigs.Disconnect.REQUEST_TIMEOUT, 2000L) // 2s instead of 10s
                .setting(CoreConfigs.ConnectionState.REQUEST_TIMEOUT, 2000L) // 2s instead of 10s
                .setting(CoreConfigs.ConnectionState.CHECK_INTERVAL, 6000L) // 6s instead of 60s
                .setting(CoreConfigs.ConnectionState.HEARTBEAT_TIMEOUT, 12000L) // 12s instead of 120s
                .setting(CoreConfigs.PROJECT_PATH, Paths.get(mockServerAnnotation.projectPath()))
                ;
    }

    /**
     * Adds {@link Body} to outbox queue
     *
     * @param body
     */
    public void addToOutbox(final @Nonnull Body body) {
        this.outbox.add(body);
    }

    @Override
    public void close() {
        this.cancel();
        this.executorService.shutdownNow();
    }
}
