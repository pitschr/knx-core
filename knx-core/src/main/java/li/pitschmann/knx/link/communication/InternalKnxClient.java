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

package li.pitschmann.knx.link.communication;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import li.pitschmann.knx.link.ChannelIdAware;
import li.pitschmann.knx.link.Configuration;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;
import li.pitschmann.knx.link.body.SearchRequestBody;
import li.pitschmann.knx.link.body.SearchResponseBody;
import li.pitschmann.knx.link.body.Status;
import li.pitschmann.knx.link.body.dib.ServiceTypeFamily;
import li.pitschmann.knx.link.body.hpai.HPAI;
import li.pitschmann.knx.link.body.tunnel.ConnectionRequestInformation;
import li.pitschmann.knx.link.communication.communicator.AbstractChannelCommunicator;
import li.pitschmann.knx.link.communication.communicator.ControlAndDataChannelCommunicator;
import li.pitschmann.knx.link.communication.communicator.ControlChannelCommunicator;
import li.pitschmann.knx.link.communication.communicator.DataChannelCommunicator;
import li.pitschmann.knx.link.communication.communicator.DescriptionChannelCommunicator;
import li.pitschmann.knx.link.communication.communicator.MulticastChannelCommunicator;
import li.pitschmann.knx.link.communication.task.ConnectResponseTask;
import li.pitschmann.knx.link.communication.task.ConnectionStateResponseTask;
import li.pitschmann.knx.link.communication.task.DescriptionResponseTask;
import li.pitschmann.knx.link.communication.task.DisconnectRequestTask;
import li.pitschmann.knx.link.communication.task.DisconnectResponseTask;
import li.pitschmann.knx.link.communication.task.RoutingIndicationTask;
import li.pitschmann.knx.link.communication.task.SearchResponseTask;
import li.pitschmann.knx.link.communication.task.TunnelingAckTask;
import li.pitschmann.knx.link.communication.task.TunnelingRequestTask;
import li.pitschmann.knx.link.exceptions.KnxBodyNotReceivedException;
import li.pitschmann.knx.link.exceptions.KnxChannelIdNotReceivedException;
import li.pitschmann.knx.link.exceptions.KnxCommunicationException;
import li.pitschmann.knx.link.exceptions.KnxDescriptionNotReceivedException;
import li.pitschmann.knx.link.exceptions.KnxDiscoveryNotReceivedException;
import li.pitschmann.knx.link.exceptions.KnxNoTunnelingException;
import li.pitschmann.knx.link.exceptions.KnxWrongChannelIdException;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.link.plugin.Plugin;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Executors;
import li.pitschmann.utils.Networker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

/**
 * Abstract KNX Client class containing essential KNX communication ways to retrieve device information from
 * KNX Net/IP device and connection management.
 *
 * @author PITSCHR
 */
public final class InternalKnxClient implements KnxClient {
    private static final Logger log = LoggerFactory.getLogger(InternalKnxClient.class);
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Lock lock = new ReentrantLock();
    private final KnxEventPool eventPool = new KnxEventPool();
    private final KnxStatisticImpl statistics = new KnxStatisticImpl();
    private final KnxStatusPoolImpl statusPool = new KnxStatusPoolImpl();
    private final Configuration config;
    private final ExecutorService pluginExecutor;
    private List<AbstractChannelCommunicator> channelCommunicators = Collections.emptyList();
    private ExecutorService channelExecutor;
    private HPAI controlHPAI;
    private HPAI dataHPAI;
    private int channelId = -1;
    private InetSocketAddress remoteEndpoint;

    /**
     * KNX client constructor (package protected)
     *
     * @param config an instance of {@link Configuration}
     */
    InternalKnxClient(final @Nonnull Configuration config) {
        log.trace("Abstract KNX Client constructor");
        // configuration
        this.config = Objects.requireNonNull(config);

        // executors with fixed threads for communication and subscription
        this.pluginExecutor = Executors.newFixedThreadPool(config.getPluginExecutorPoolSize(), true);
        log.info("Plugin Executor created with size of {}: {}", config.getPluginExecutorPoolSize(), this.pluginExecutor);
        log.info("Observer Plugins: {}", this.config.getObserverPlugins());
        log.info("Extension Plugins: {}", this.config.getExtensionPlugins());
    }

    /**
     * Starts the services and notifies the plug-ins about initialization
     */
    protected final void start() {
        Preconditions.checkState(!this.closed.get(), "It seems the KNX client is already running.");

        try {
            // if remote control address is multicast address, then we know that we want to use the routing feature
            if (config.isRoutingEnabled()) {
                startRouting();
            }
            // otherwise use the tunneling
            else {
                startTunneling();
            }
        } catch (final Exception ex) {
            log.error("Exception caught on 'start()' method.", ex);
            this.notifyPluginsError(ex);
            this.close();
            throw ex;
        }
    }

    /**
     * Starts KNX communication via Routing
     */
    private final void startRouting() {
        log.trace("Method 'startRouting()' called");

        this.remoteEndpoint = new InetSocketAddress(config.getRemoteControlAddress(), config.getRemoteControlPort());
        log.debug("Endpoint from KNX multi cast is taken: {}", this.remoteEndpoint);

        log.info("Routing is used. Starting KNX services.");
        this.startServices();
    }

    /**
     * Starts KNX communication via Tunneling
     */
    private final void startTunneling() {
        log.trace("Method 'startTunneling()' called");

        // check if endpoint is defined - if not, look up for an available KNX Net/IP device
        if (config.getRemoteControlAddress() == null) {
            final var discoveryResponse = this.fetchDiscoveryFromKNX();
            this.remoteEndpoint = Networker.toInetSocketAddress(discoveryResponse.getControlEndpoint());
            log.debug("Endpoint from discovery is taken: {} ({})", this.remoteEndpoint, discoveryResponse.getDeviceInformation().getDeviceFriendlyName());
        } else {
            this.remoteEndpoint = new InetSocketAddress(config.getRemoteControlAddress(), config.getRemoteControlPort());
            log.debug("Endpoint from configuration is taken: {}", this.remoteEndpoint);
        }

        if (this.verifyTunnelingSupport()) {
            log.info("Tunneling is used. Verification passed. Starting KNX services.");
            this.startServices();
        } else {
            throw new KnxNoTunnelingException(
                    "The remote device doesn't support TUNNELING. Please choose a remote device that supports TUNNELING.");
        }
    }

    @Nonnull
    @Override
    public final Configuration getConfig() {
        return this.config;
    }

    @Nonnull
    @Override
    public KnxStatisticImpl getStatistic() {
        return this.statistics;
    }

    @Nonnull
    @Override
    public KnxStatusPoolImpl getStatusPool() {
        return this.statusPool;
    }

    /**
     * Returns the remote endpoint. If specified by the config explicitly, the endpoint from config is taken,
     * otherwise the endpoint has been discovered by the KNX client automatically.
     *
     * @return An instance of {@link InetSocketAddress}, it cannot be {@code null}
     */
    @Nonnull
    public InetSocketAddress getRemoteEndpoint() {
        return Objects.requireNonNull(this.remoteEndpoint);
    }

    /**
     * Verify if the retrieved {@link DescriptionResponseBody} returned by the KNX Net/IP device
     * is applicable for current client implementation.
     * <p/>
     * It will just check if the KNX Net/IP device supports tunneling.
     *
     * @return {@code true} if tunneling is supported by KNX Net/IP device and we can proceed with connect, otherwise {@code false}.
     */
    private boolean verifyTunnelingSupport() {
        log.trace("Call 'verifyTunnelingSupport()' method.");
        final var descriptionResponseBody = this.fetchDescriptionFromKNX();

        // get supported device families
        final var serviceFamilies = descriptionResponseBody.getSupportedDeviceFamilies().getServiceFamilies();
        log.debug("Supported device families: {}", serviceFamilies);

        // check if the remote device accepts TUNNELING
        return serviceFamilies.stream().anyMatch(f -> f.getFamily() == ServiceTypeFamily.TUNNELING);
    }

    /**
     * Initializes the KNX client for the communication. Updates connection data and start services which are
     * necessary for the communication.
     */
    private void startServices() {
        this.lock.lock();
        try {
            // some pre-checks
            Preconditions.checkState(this.channelId == -1);
            Preconditions.checkState(this.controlHPAI == null);
            Preconditions.checkState(this.dataHPAI == null);

            if (this.config.isRoutingEnabled()) {
                // Routing is enabled -> communication will be done via multi cast
                final var multicastChannelCommunicator = new MulticastChannelCommunicator(this);
                subscribersForMultiChannel().forEach(multicastChannelCommunicator::subscribe);

                this.controlHPAI = HPAI.useDefault();
                this.dataHPAI = HPAI.useDefault();

                this.channelCommunicators = ImmutableList.of(multicastChannelCommunicator);
            } else if (this.config.isNatEnabled()) {
                // NAT is enabled -> only one communicator for control and data related packets
                final var controlAndDataChannelCommunicator = new ControlAndDataChannelCommunicator(this);
                subscribersForDataChannel().forEach(controlAndDataChannelCommunicator::subscribe);
                subscribersForControlChannel().forEach(controlAndDataChannelCommunicator::subscribe);

                this.controlHPAI = HPAI.useDefault();
                this.dataHPAI = HPAI.useDefault();

                this.channelCommunicators = ImmutableList.of(controlAndDataChannelCommunicator);
            } else {
                // NAT is not enabled -> two communicators (one for control, and one for data related packets)
                final var controlChannelCommunicator = new ControlChannelCommunicator(this);
                subscribersForControlChannel().forEach(controlChannelCommunicator::subscribe);
                final var dataChannelCommunicator = new DataChannelCommunicator(this);
                subscribersForDataChannel().forEach(dataChannelCommunicator::subscribe);

                this.controlHPAI = HPAI.of(controlChannelCommunicator.getChannel());
                this.dataHPAI = HPAI.of(dataChannelCommunicator.getChannel());

                this.channelCommunicators = ImmutableList.of(dataChannelCommunicator, controlChannelCommunicator);
            }

            // logging
            log.info("Remote Endpoint (KNX Net/IP)     : {}:{}", this.remoteEndpoint.getAddress().getHostAddress(), this.remoteEndpoint.getPort());
            log.info("Local Endpoint  (Control Channel): {}:{}", this.controlHPAI.getAddress().getHostAddress(), this.controlHPAI.getPort());
            log.info("Local Endpoint  (Data Channel)   : {}:{}", this.dataHPAI.getAddress().getHostAddress(), this.dataHPAI.getPort());
            log.info("Routing Enabled                  : {}", this.config.isRoutingEnabled());
            log.info("NAT Enabled                      : {}", this.config.isNatEnabled());

            // channel executors
            this.channelExecutor = Executors.newFixedThreadPool(3, true);
            this.channelCommunicators.forEach(channelExecutor::execute);

            // get channel for further communications
            if (this.config.isRoutingEnabled()) {
                log.info("No channel ID because of routing");
            } else {
                this.channelId = this.fetchChannelIdFromKNX();
                log.info("Channel ID received: {}", this.channelId);

                // after obtaining channel id - start monitor as well
                this.channelExecutor.submit(this.createConnectionStateMonitor());
            }

            // do not accept more services anymore!
            this.channelExecutor.shutdown();
            log.info("Channel Executor created: {}", this.channelExecutor);

            // notifies the extension plug-in about start of service / communication
            this.notifyPlugins(this, this.config.getExtensionPlugins(), (p, c) -> p.onStart());
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Returns a list of {@link Flow.Subscriber} that is suited for description channel communicators
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link DescriptionResponseTask} receiving the response after {@link DescriptionRequestBody}</li>
     * client</li>
     * </ul>
     *
     * @return unmodifiable list of subscribers
     */
    private List<Flow.Subscriber<Body>> subscribersForDescriptionChannel() {
        return Collections.singletonList(new DescriptionResponseTask(this));
    }

    /**
     * Returns a list of {@link Flow.Subscriber} that is suited for control channel communicators
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link ConnectResponseTask} receiving the response after {@link ConnectRequestBody}</li>
     * <li>{@link ConnectionStateResponseTask} receiving connection health status from KNX Net/IP device</li>
     * <li>{@link DisconnectRequestTask} when disconnect is initiated by the KNX Net/IP device</li>
     * <li>{@link DisconnectResponseTask} as answer from KNX Net/IP device when disconnect is initiated by the
     * client</li>
     * </ul>
     *
     * @return unmodifiable list of subscribers
     */
    @Nonnull
    private List<Flow.Subscriber<Body>> subscribersForControlChannel() {
        final var subscribers = new ArrayList<Flow.Subscriber<Body>>();
        subscribers.add(new ConnectResponseTask(this));
        subscribers.add(new ConnectionStateResponseTask(this));
        subscribers.add(new DisconnectRequestTask(this));
        subscribers.add(new DisconnectResponseTask(this));
        return Collections.unmodifiableList(subscribers);
    }

    /**
     * Returns a list of {@link Flow.Subscriber} that is suited for data channel communicators
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link TunnelingRequestTask} when KNX Net/IP device notifies the client about a change from a remote KNX
     * device</li>
     * <li>{@link TunnelingAckTask} as answer from KNX Net/IP device when sending a data packet</li>
     * </ul>
     *
     * @return unmodifiable list of subscribers
     */
    @Nonnull
    private List<Flow.Subscriber<Body>> subscribersForDataChannel() {
        final var subscribers = new ArrayList<Flow.Subscriber<Body>>();
        subscribers.add(new TunnelingRequestTask(this));
        subscribers.add(new TunnelingAckTask(this));
        return Collections.unmodifiableList(subscribers);
    }

    /**
     * Returns a list of {@link Flow.Subscriber} that is suited for multicast channel communicators
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link RoutingIndicationTask} when KNX Net/IP device notifies the client about a change from a remote KNX
     * device</li>
     * </ul>
     *
     * @return unmodifiable list of subscribers
     */
    @Nonnull
    private List<Flow.Subscriber<Body>> subscribersForMultiChannel() {
        final var subscribers = new ArrayList<Flow.Subscriber<Body>>();
        subscribers.add(new SearchResponseTask(this));
        subscribers.add(new RoutingIndicationTask(this));
        return Collections.unmodifiableList(subscribers);
    }

    /**
     * Registers the {@link ConnectionStateMonitor} to send {@link ConnectionStateRequestBody} frequently to the
     * KNX Net/IP device and monitors the health status.
     * <p>
     * No subscribers.
     *
     * @return {@link ConnectionStateMonitor}
     */
    @Nonnull
    private ConnectionStateMonitor createConnectionStateMonitor() {
        return new ConnectionStateMonitor(this);
    }

    @Override
    public final void close() {
        log.trace("Method 'close()' called.");

        // already closed?
        if (this.closed.getAndSet(true)) {
            log.debug("Already closed. Do nothing!");
            return;
        } else {
            log.info("Client will be closed.");
        }

        this.lock.lock();
        try {
            // notifies the extension plug-ins about shutdown
            this.notifyPlugins(this, this.config.getExtensionPlugins(), (p, c) -> p.onShutdown());

            this.stopServices();
        } finally {
            this.lock.unlock();
        }

        log.trace("Method 'close()' completed.");
    }

    /**
     * Closes the KNX communication, services and channels
     */
    private void stopServices() {
        log.trace("Method 'stopServices()' called");
        var isOk = true;
        try {
            // Check if there is already a disconnect request present in the event pool.
            // 1) If exists, disconnect request came from KNX Net/IP device -> no disconnect request to sent
            // 2) If NOT exists, client is closing the communication and send disconnect request to KNX Net/IP device
            if (this.channelId > 0 && !this.eventPool.disconnectEvent().hasRequest()) {
                log.trace("Control channel is still connected. Send disconnect request.");
                // create body
                final var requestBody = DisconnectRequestBody.of(this.channelId, this.controlHPAI);
                try {
                    final var responseBody = this.send(requestBody, config.getTimeoutDisconnectRequest()).get();
                    if (responseBody != null) {
                        log.debug("Disconnect Response Body retrieved: {}", responseBody);
                    } else {
                        throw new KnxBodyNotReceivedException(DisconnectResponseBody.class);
                    }
                } catch (KnxBodyNotReceivedException | InterruptedException | ExecutionException ex) {
                    log.debug("No Disconnect Response Body retrieved. Continue with disconnect.");
                    isOk = false;
                }
            }
        } finally {
            // resets the communication information
            this.channelId = -1;
            this.controlHPAI = null;
            this.dataHPAI = null;

            // close channel communicators
            for (final var channelCommunicator : this.channelCommunicators) {
                isOk &= Closeables.closeQuietly(channelCommunicator);
            }
            log.info("Channel Communicator stopped gracefully. Status: {}", isOk);

            // shutdown executors now
            isOk &= Closeables.shutdownQuietly(this.channelExecutor, 0, TimeUnit.SECONDS);
            isOk &= Closeables.shutdownQuietly(this.pluginExecutor, 10, TimeUnit.SECONDS);
            log.info("KNX Services stopped gracefully. Status: {}", isOk);
        }
    }

    public final HPAI getControlHPAI() {
        return this.controlHPAI;
    }

    public final HPAI getDataHPAI() {
        return this.dataHPAI;
    }

    @Nonnull
    public final KnxEventPool getEventPool() {
        return this.eventPool;
    }

    public final int getChannelId() {
        return this.channelId;
    }

    public final boolean isClosed() {
        return this.closed.get();
    }

    /**
     * Finds the responsible channel communicator for the given {@code body}
     *
     * @param body
     * @return responsible channel communicator, otherwise {@link IllegalArgumentException} if no suitable communicator was found
     */
    @Nonnull
    private AbstractChannelCommunicator getChannelCommunicator(final @Nonnull Body body) {
        for (final var channelCommunicator : channelCommunicators) {
            if (channelCommunicator.isCompatible(body)) {
                return channelCommunicator;
            }
        }
        throw new IllegalArgumentException("No channel relation defined for body. I do not know to which channel communicator the body belongs to: " + body);
    }

    /**
     * Notifies all {@link ObserverPlugin} about incoming {@link Body}
     *
     * @param body any KNX body
     */
    public void notifyPluginsIncomingBody(final @Nonnull Body body) {
        this.statistics.onIncomingBody(body);
        this.notifyPlugins(body, this.config.getObserverPlugins(), ObserverPlugin::onIncomingBody);
    }

    /**
     * Notifies all {@link ObserverPlugin} about outgoing {@link Body}
     *
     * @param body any KNX body
     */
    public void notifyPluginsOutgoingBody(final @Nonnull Body body) {
        this.statistics.onOutgoingBody(body);
        this.notifyPlugins(body, this.config.getObserverPlugins(), ObserverPlugin::onOutgoingBody);
    }

    /**
     * Notifies all {@link ObserverPlugin} about throwable during incoming or outgoing {@link Body}
     *
     * @param throwable an instance of {@link Throwable} to be sent to plug-ins
     */
    public void notifyPluginsError(final @Nonnull Throwable throwable) {
        this.statistics.onError(throwable);
        this.notifyPlugins(throwable, this.config.getObserverPlugins(), ObserverPlugin::onError);
    }

    /**
     * Notifies the registered plug-ins about {@code <O>}.
     *
     * @param obj      object to be sent to plug-ins
     * @param plugins  list of plug-ins to be notified
     * @param consumer consumer defining which method should be called
     */
    protected <O, P extends Plugin> void notifyPlugins(final @Nonnull O obj,
                                                       final @Nonnull List<P> plugins,
                                                       final @Nonnull BiConsumer<P, O> consumer) {
        if (this.pluginExecutor.isShutdown()) {
            log.warn("Could not send to plug-ins because plugin executor is shutdown already: {}",
                    obj instanceof Throwable ? ((Throwable) obj).getMessage() : obj);
        } else {
            for (final P plugin : plugins) {
                CompletableFuture.runAsync(() -> {
                    log.trace("Send to plugin: {}", plugin);
                    try {
                        consumer.accept(plugin, obj);
                    } catch (final Exception ex) {
                        log.debug("Exception during notifyPlugins(T, List<Plugin>, BiConsumer)", ex);
                    }
                }, this.pluginExecutor);
            }
        }
    }

    /**
     * Verifies if the {@link Body} response if it meets the {@link #channelId}. The channel id check is skipped when
     * given {@link Body} doesn't implement the {@link ChannelIdAware} interface.
     * <p/>
     * The {@link #channelId} is fetched and set during initialization of connection with KNX Net/IP device.
     *
     * @param body any KNX body to be verified
     * @return {@code true} if channel id is valid for current KNX client, otherwise {@link KnxWrongChannelIdException} is thrown.
     * @throws KnxWrongChannelIdException when channel id is not valid
     */
    public final boolean verifyChannelId(final @Nonnull Body body) {
        // if body is channel id aware then verify the channel id, otherwise skip it
        if (ChannelIdAware.class.isAssignableFrom(body.getClass())) {
            final var channelIdAwareBody = (ChannelIdAware) body;
            final var actualChannelId = channelIdAwareBody.getChannelId();
            // skip check for ConnectResponseBody because it is the first time where we get channelId
            if (body instanceof ConnectResponseBody) {
                log.debug("Channel ID in ConnectResponseBody isn't be checked because it is the first response with channel id.");
            }
            // verify if channel id is the expected one
            else if (actualChannelId != this.channelId) {
                log.warn("Wrong Channel ID received for body: {}", body);
                throw new KnxWrongChannelIdException(channelIdAwareBody, this.channelId);
            }
        }
        return true;
    }

    /**
     * Returns the description response body from KNX Net/IP device containing device information, supported device
     * capabilities.
     *
     * @return {@link DescriptionResponseBody}, otherwise {@link KnxDescriptionNotReceivedException} will be thrown
     */
    @Nonnull
    private DescriptionResponseBody fetchDescriptionFromKNX() {
        log.trace("Method 'fetchDescriptionFromKNX()' called.");

        // Description request / response is one-time task before establishing communication to KNX Net/IP device
        final var communicator = new DescriptionChannelCommunicator(this);
        subscribersForDescriptionChannel().forEach(communicator::subscribe);

        // Create executor service for description communication
        final var es = Executors.newSingleThreadExecutor(true);
        es.execute(communicator);
        es.shutdown();

        // send description request
        final var request = DescriptionRequestBody.useDefault();
        log.debug("Request for description: {}", request);

        // It opens a new channel for description communication and processing. Afterwards it will be shutdown.
        try (communicator) {
            final var response = communicator.<DescriptionResponseBody>send(request, config.getTimeoutDescriptionRequest()).get();
            // check status
            Preconditions.checkState(response != null, response);
            return response;
        } catch (final Exception ex) {
            log.error("Exception during fetch description from KNX Net/IP device", ex);
            throw new KnxDescriptionNotReceivedException(request);
        } finally {
            Closeables.shutdownQuietly(es);
        }
    }

    /**
     * Returns the discovery response body containing available KNX Net/IP devices including device information,
     * supported device capabilities.
     *
     * @return First {@link SearchResponseBody} (subsequent should be requested by {@link KnxEventPool}),
     * otherwise {@link KnxDiscoveryNotReceivedException} will be thrown
     */
    @Nonnull
    private SearchResponseBody fetchDiscoveryFromKNX() {
        log.trace("Method 'fetchDiscoveryFromKNX()' called.");

        // Search request / response is one-time task to auto-find all available KNX Net/IP device
        final var communicator = new MulticastChannelCommunicator(this);
        subscribersForMultiChannel().forEach(communicator::subscribe);

        // Create executor service for discovery communication
        final var es = Executors.newSingleThreadExecutor(true);
        es.execute(communicator);
        es.shutdown();

        // send search request
        final var requestBody = SearchRequestBody.of(HPAI.of(communicator.getChannel()));
        log.debug("Request for search: {}", requestBody);

        // It opens a new channel for discovery communication and processing. Afterwards it will be shutdown.
        SearchResponseBody responseBody = null;
        try (communicator) {
            responseBody = communicator.<SearchResponseBody>send(requestBody, config.getTimeoutDiscoveryRequest()).get();
            // check status
            Preconditions.checkState(responseBody != null, responseBody);
            return responseBody;
        } catch (final Exception ex) {
            log.error("Exception during fetch discovery frames from KNX Net/IP device", ex);
            throw new KnxDiscoveryNotReceivedException(requestBody, responseBody, ex);
        } finally {
            Closeables.shutdownQuietly(es);
        }
    }

    /**
     * Fetches the channel id from KNX Net/IP device.
     *
     * @return the channel id retrieved from KNX Net/ip device
     * @throws KnxCommunicationException in case channel id could not be fetched due an error or timeout
     */
    private int fetchChannelIdFromKNX() {
        log.trace("Method 'fetchChannelIdFromKNX()' called.");

        // create connect request and send it
        final var cri = ConnectionRequestInformation.useDefault();
        final var requestBody = ConnectRequestBody.of(this.controlHPAI, this.dataHPAI, cri);
        log.debug("Request for connect: {}", requestBody);

        ConnectResponseBody responseBody = null;
        try {
            responseBody = this.<ConnectResponseBody>send(requestBody, config.getTimeoutConnectRequest()).get();
            // check status if we got response with NO_ERROR status
            Preconditions.checkState(responseBody != null && responseBody.getStatus() == Status.E_NO_ERROR, responseBody);
            return responseBody.getChannelId();
        } catch (final Exception ex) {
            log.error("Exception during fetch channel id from KNX Net/IP device", ex);
            throw new KnxChannelIdNotReceivedException(requestBody, responseBody, ex);
        }
    }

    @Override
    public final void send(final @Nonnull Body body) {
        this.getChannelCommunicator(body).send(body);
    }

    @Nonnull
    @Override
    public final <U extends ResponseBody> CompletableFuture<U> send(final @Nonnull RequestBody requestBody, final long msTimeout) {
        return this.getChannelCommunicator(requestBody).send(requestBody, msTimeout);
    }
}
