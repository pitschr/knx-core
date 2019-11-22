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

import li.pitschmann.knx.link.ChannelIdAware;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
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
import li.pitschmann.knx.link.communication.communicator.CommunicatorFactory;
import li.pitschmann.knx.link.config.Config;
import li.pitschmann.knx.link.config.ConfigConstants;
import li.pitschmann.knx.link.config.ConfigValue;
import li.pitschmann.knx.link.exceptions.KnxBodyNotReceivedException;
import li.pitschmann.knx.link.exceptions.KnxChannelIdNotReceivedException;
import li.pitschmann.knx.link.exceptions.KnxCommunicationException;
import li.pitschmann.knx.link.exceptions.KnxDescriptionNotReceivedException;
import li.pitschmann.knx.link.exceptions.KnxDiscoveryNotReceivedException;
import li.pitschmann.knx.link.exceptions.KnxNoTunnelingException;
import li.pitschmann.knx.link.exceptions.KnxWrongChannelIdException;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.link.plugin.PluginManager;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Executors;
import li.pitschmann.utils.Networker;
import li.pitschmann.utils.Preconditions;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract KNX Client class containing essential KNX communication ways to retrieve device information from
 * KNX Net/IP device and connection management.
 *
 * @author PITSCHR
 */
public final class InternalKnxClient implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(InternalKnxClient.class);
    private final AtomicBoolean closed = new AtomicBoolean(true);
    private final Lock lock = new ReentrantLock();
    private final InternalKnxEventPool eventPool = new InternalKnxEventPool();
    private final InternalKnxStatistic statistics = new InternalKnxStatistic();
    private final InternalKnxStatusPool statusPool = new InternalKnxStatusPool();
    private final PluginManager pluginManager;
    private final Config config;
    private State state = State.NOT_STARTED;
    private List<AbstractChannelCommunicator> channelCommunicators = Collections.emptyList();
    private ExecutorService channelExecutor;
    private HPAI controlHPAI;
    private HPAI dataHPAI;
    private int channelId = -1;
    private InetSocketAddress remoteEndpoint;

    /**
     * KNX client constructor (package protected)
     *
     * @param config an instance of {@link Config}
     */
    InternalKnxClient(final @Nonnull Config config) {
        log.trace("Abstract KNX Client constructor");
        this.config = Objects.requireNonNull(config);
        this.pluginManager = new PluginManager(config);

        // In case of forced shutdown (e.g. CTRL+C) we should try to close the client properly
        Runtime.getRuntime().addShutdownHook(new Thread(() -> close()));
    }

    /**
     * Starts the services and notifies the plug-ins about initialization
     */
    protected void start() {
        this.lock.lock();
        try {
            Preconditions.checkState(this.closed.get(), "It seems the KNX client is already running.");
            this.closed.set(false);

            this.state = State.START_REQUEST;

            // if remote control address is multicast address, then we know that we want to use the routing feature
            if (config.isRoutingEnabled()) {
                startRouting();
            }
            // otherwise use the tunneling
            else {
                startTunneling();
            }

            this.state = State.STARTED;

            // inform plugins about client start with small delay to
            // allow some breathe between initialization and start
            Sleeper.milliseconds(100);
            pluginManager.notifyClientStart();
        } catch (final Exception ex) {
            log.error("Exception caught on 'start()' method.", ex);
            this.notifyError(ex);
            this.close();
            throw ex;
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Starts KNX communication via Routing
     */
    private void startRouting() {
        log.trace("Method 'startRouting()' called");

        this.remoteEndpoint = new InetSocketAddress(config.getRemoteControlAddress(), config.getRemoteControlPort());
        log.debug("Endpoint from KNX multi cast is taken: {}", this.remoteEndpoint);

        log.info("Routing is used. Starting KNX services.");
        this.startServices();
    }

    /**
     * Starts KNX communication via Tunneling
     */
    private void startTunneling() {
        log.trace("Method 'startTunneling()' called");

        // check if endpoint is defined - if not, look up for an available KNX Net/IP device
        if (config.getRemoteControlAddress().isAnyLocalAddress()) {
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
        // some pre-checks
        Preconditions.checkState(this.channelId == -1);
        Preconditions.checkState(this.controlHPAI == null);
        Preconditions.checkState(this.dataHPAI == null);

        if (this.config.isRoutingEnabled()) {
            // Routing is enabled -> communication will be done via multi cast
            this.channelCommunicators = List.of(CommunicatorFactory.newRoutingChannelCommunicator(this));

            this.controlHPAI = HPAI.useDefault();
            this.dataHPAI = HPAI.useDefault();
        } else if (this.config.isNatEnabled()) {
            // NAT is enabled -> only one communicator for control and data related packets
            this.channelCommunicators = List.of(CommunicatorFactory.newControlAndDataChannelCommunicator(this));

            this.controlHPAI = HPAI.useDefault();
            this.dataHPAI = HPAI.useDefault();
        } else {
            // NAT is not enabled -> two communicators (one for control, and one for data related packets)
            final var controlChannelCommunicator = CommunicatorFactory.newControlChannelCommunicator(this);
            final var dataChannelCommunicator = CommunicatorFactory.newDataChannelCommunicator(this);
            this.channelCommunicators = List.of(dataChannelCommunicator, controlChannelCommunicator);

            this.controlHPAI = HPAI.of(controlChannelCommunicator.getChannel());
            this.dataHPAI = HPAI.of(dataChannelCommunicator.getChannel());
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
            log.info("No channel ID required because of routing");
        } else {
            this.channelId = this.fetchChannelIdFromKNX();
            log.info("Channel ID received: {}", this.channelId);

            // after obtaining channel id - start monitor as well
            this.channelExecutor.submit(new ConnectionStateMonitor(this));
        }

        // do not accept more services anymore!
        this.channelExecutor.shutdown();
        log.info("Channel Executor created: {}", this.channelExecutor);
    }

    @Override
    public void close() {
        log.trace("Method 'close()' called.");

        // already closed?
        if (this.closed.getAndSet(true)) {
            log.debug("Already closed. Do nothing!");
            return;
        }

        this.lock.lock();
        try {
            this.state = State.STOP_REQUEST;
            log.info("Client will be stopped.");

            this.stopServices();
        } finally {
            // notifies the extension plug-ins about shutdown
            pluginManager.notifyClientShutdown();
            pluginManager.close();
            log.info("Plugin Manager closed.");

            this.state = State.NOT_STARTED;
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
                    final var responseBody = this.send(requestBody, getConfig(ConfigConstants.Disconnect.REQUEST_TIMEOUT)).get();
                    if (responseBody != null) {
                        log.debug("Disconnect Response Body retrieved: {}", responseBody);
                    } else {
                        throw new KnxBodyNotReceivedException(DisconnectResponseBody.class);
                    }
                } catch (KnxBodyNotReceivedException | InterruptedException | ExecutionException ex) {
                    log.debug("No Disconnect Response Body retrieved. Continue with disconnect.");
                    isOk = false;
                    Thread.currentThread().interrupt();
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
            log.info("Channel Communicator stopped gracefully?: {}", isOk);

            // shutdown executor now
            isOk &= Closeables.shutdownQuietly(this.channelExecutor, 0, TimeUnit.SECONDS);
            log.info("KNX Services stopped gracefully?: {}", isOk);

            // some time buffer for OS to close the underlying network bindings to avoid
            // "Address already in use" when restarting the client immediately.
            Sleeper.milliseconds(100);
        }
    }

    /**
     * Returns the state of {@link InternalKnxClient}.
     *
     * @return the current state
     */
    @Nonnull
    public State getState() {
        return state;
    }

    @Nonnull
    public Config getConfig() {
        return this.config;
    }

    @Nonnull
    public <T> T getConfig(final @Nonnull ConfigValue<T> configValue) {
        return getConfig().getValue(configValue);
    }

    @Nonnull
    public InternalKnxStatistic getStatistic() {
        return this.statistics;
    }

    @Nonnull
    public InternalKnxStatusPool getStatusPool() {
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

    @Nonnull
    public HPAI getControlHPAI() {
        return this.controlHPAI == null ? HPAI.useDefault() : this.controlHPAI;
    }

    @Nonnull
    public HPAI getDataHPAI() {
        return this.dataHPAI == null ? HPAI.useDefault() : this.dataHPAI;
    }

    @Nonnull
    public InternalKnxEventPool getEventPool() {
        return this.eventPool;
    }

    @Nonnull
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public int getChannelId() {
        return this.channelId;
    }

    public void send(final @Nonnull Body body) {
        this.getChannelCommunicator(body).send(body);
    }

    @Nonnull
    public <U extends ResponseBody> CompletableFuture<U> send(final @Nonnull RequestBody requestBody, final long msTimeout) {
        return this.getChannelCommunicator(requestBody).send(requestBody, msTimeout);
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
     * Notifies all listeners{@link ObserverPlugin} about incoming {@link Body}
     *
     * @param body any KNX body
     */
    public void notifyIncomingBody(final @Nonnull Body body) {
        statistics.onIncomingBody(body);
        pluginManager.notifyIncomingBody(body);
    }

    /**
     * Notifies all listeners about outgoing {@link Body}
     *
     * @param body any KNX body
     */
    public void notifyOutgoingBody(final @Nonnull Body body) {
        statistics.onOutgoingBody(body);
        pluginManager.notifyOutgoingBody(body);
    }

    /**
     * Notifies all {@link ObserverPlugin} about throwable during incoming or outgoing {@link Body}
     *
     * @param throwable an instance of {@link Throwable} to be sent to plug-ins
     */
    public void notifyError(final @Nonnull Throwable throwable) {
        statistics.onError(throwable);
        pluginManager.notifyError(throwable);
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
    public boolean verifyChannelId(final @Nonnull Body body) {
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
        final var communicator = CommunicatorFactory.newDescriptionChannelCommunicator(this);

        // Create executor service for description communication
        final var es = Executors.newSingleThreadExecutor(true);
        es.execute(communicator);
        es.shutdown();

        // send description request
        final var requestBody = DescriptionRequestBody.useDefault();
        log.debug("Request for description: {}", requestBody);

        // It opens a new channel for description communication and processing. Afterwards it will be shutdown.
        try (communicator) {
            final var responseBody = communicator.<DescriptionResponseBody>send(requestBody, getConfig(ConfigConstants.Description.REQUEST_TIMEOUT)).get();
            Preconditions.checkNonNull(responseBody, "No description response received for request: {}", requestBody);
            return responseBody;
        } catch (final Exception ex) {
            log.error("Exception during fetch description from KNX Net/IP device", ex);
            throw new KnxDescriptionNotReceivedException(requestBody);
        } finally {
            Closeables.shutdownQuietly(es);
        }
    }

    /**
     * Returns the discovery response body containing available KNX Net/IP devices including device information,
     * supported device capabilities.
     *
     * @return First {@link SearchResponseBody} (subsequent should be requested by {@link InternalKnxEventPool}),
     * otherwise {@link KnxDiscoveryNotReceivedException} will be thrown
     */
    @Nonnull
    private SearchResponseBody fetchDiscoveryFromKNX() {
        log.trace("Method 'fetchDiscoveryFromKNX()' called.");

        // Search request / response is one-time task to auto-find all available KNX Net/IP device
        final var communicator = CommunicatorFactory.newDiscoveryChannelCommunicator(this);

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
            responseBody = communicator.<SearchResponseBody>send(requestBody, getConfig(ConfigConstants.Search.REQUEST_TIMEOUT)).get();
            Preconditions.checkNonNull(responseBody, "No search response received for request: {}", requestBody);
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
            responseBody = this.<ConnectResponseBody>send(requestBody, getConfig(ConfigConstants.Connect.REQUEST_TIMEOUT)).get();
            // check status if we got response with NO_ERROR status
            Preconditions.checkNonNull(responseBody, "No connect response received for request: {}", requestBody);
            Preconditions.checkState(responseBody.getStatus() == Status.E_NO_ERROR,
                    "Connect Response with error state received: {}", responseBody);
            return responseBody.getChannelId();
        } catch (final Exception ex) {
            log.error("Exception during fetch channel id from KNX Net/IP device", ex);
            throw new KnxChannelIdNotReceivedException(requestBody, responseBody, ex);
        }
    }

    /**
     * States of Internal KNX Client
     */
    public enum State {
        /**
         * The KNX Client is not started (or has been stopped)
         * <p/>
         * Next state is: {@link #START_REQUEST}
         */
        NOT_STARTED,
        /**
         * The start has been requested and KNX Client may not communicate actively with KNX Net/IP device yet.
         * <p/>
         * Next State is either: {@link #STARTED} if successfully, otherwise {@link #STOP_REQUEST} if the
         * communication cannot be established for some reasons.
         */
        START_REQUEST,
        /**
         * The communication has been established and the KNX Client is actively communicating with the
         * KNX Net/IP device.
         * <p/>
         * Next State is: {@link #STOP_REQUEST}
         */
        STARTED,
        /**
         * The communication has been stopped. This can be happen successfully, or also because of failure.
         * <p/>
         * Next State is: {@link #NOT_STARTED} as soon the stop procedure is completed.
         */
        STOP_REQUEST
    }
}
