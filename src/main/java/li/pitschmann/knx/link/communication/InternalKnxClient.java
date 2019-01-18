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

import li.pitschmann.knx.link.*;
import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.body.dib.*;
import li.pitschmann.knx.link.body.hpai.*;
import li.pitschmann.knx.link.body.tunnel.*;
import li.pitschmann.knx.link.communication.communicator.*;
import li.pitschmann.knx.link.communication.task.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.knx.link.plugin.*;
import li.pitschmann.utils.*;
import org.slf4j.*;

import javax.annotation.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

/**
 * Abstract KNX Client class containing essential KNX communication ways to retrieve device information from
 * KNX Net/IP router and connection management.
 *
 * @author PITSCHR
 */
public final class InternalKnxClient implements KnxClient {
    private static final Logger LOG = LoggerFactory.getLogger(InternalKnxClient.class);
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Lock lock = new ReentrantLock();
    private final KnxEventPool eventPool = new KnxEventPool();
    private final KnxStatusPool statusPool = new KnxStatusPool();
    private final DefaultKnxStatistic statistics;
    private final Configuration config;
    private final ExecutorService communicationExecutor;
    private final ExecutorService pluginExecutor;
    private ExecutorService channelExecutor;
    private HPAI controlHPAI;
    private HPAI dataHPAI;
    private int channelId;
    private DataChannelCommunicator dataChannelCommunicator;
    private ControlChannelCommunicator controlChannelCommunicator;

    /**
     * KNX client constructor (package protected)
     *
     * @param config an instance of {@link Configuration}
     */
    InternalKnxClient(final Configuration config) {
        LOG.trace("Abstract KNX Client constructor");
        // configuration
        this.config = config;

        // statistics
        this.statistics = new DefaultKnxStatistic();

        // executors with fixed threads for communication and subscription
        this.communicationExecutor = Executors.newFixedThreadPool(config.getCommunicationExecutorPoolSize());
        LOG.info("Communication Executor created with size of {}: {}", config.getCommunicationExecutorPoolSize(), this.communicationExecutor);
        this.pluginExecutor = Executors.newFixedThreadPool(config.getPluginExecutorPoolSize());
        LOG.info("Plugin Executor created with size of {}: {}", config.getPluginExecutorPoolSize(), this.pluginExecutor);
        LOG.info("Observer Plugins: {}", this.config.getObserverPlugins());
        LOG.info("Extension Plugins: {}", this.config.getExtensionPlugins());
    }

    /**
     * Starts the services and notifies the plug-ins about initialization
     */
    protected final void start() {
        // notifies all plug-ins about initialization
        this.notifyPlugins(this, this.config.getAllPlugins(), Plugin::onInitialization);

        // validate
        try {
            if (this.verify()) {
                LOG.info("Verification passed. Starting KNX services.");
                this.startServices();
            } else {
                throw new KnxNoTunnellingException(
                        "The remote device doesn't support TUNNELLING. Please choose a remote device that supports TUNNELLING.");
            }
        } catch (final Exception ex) {
            LOG.error("Exception caught on 'start()' method.", ex);
            this.notifyPluginsError(ex);
            this.close();
            throw ex;
        }
    }

    @Override
    public final Configuration getConfig() {
        return this.config;
    }

    @Override
    public KnxStatistic getStatistic() {
        return this.statistics.getUnmodifiableStatistic();
    }

    /**
     * Verify if the retrieved {@link DescriptionResponseBody} returned by the KNX Net/IP router
     * is applicable for current client implementation.
     * <p/>
     * It will just check if the KNX Net/IP router supports tunnelling.
     *
     * @return {@code true} if tunneling is supported by KNX Net/IP router and we can proceed with connect, otherwise {@code false}.
     */
    private boolean verify() {
        LOG.trace("Call 'verify()' method.");
        final DescriptionResponseBody descriptionResponseBody = this.fetchDescriptionFromRouter();

        // get supported device families
        final List<ServiceTypeFamilyVersion> serviceFamilies = descriptionResponseBody.getSupportedDeviceFamilies().getServiceFamilies();
        LOG.debug("Supported device families: {}", serviceFamilies);

        // check if the remote device accepts TUNNELLING
        return serviceFamilies.stream().anyMatch(f -> f.getFamily() == ServiceTypeFamily.TUNNELLING);
    }

    /**
     * Initializes the KNX client for the communication. Updates connection data and start services which are
     * necessary for the communication.
     */
    private void startServices() {
        this.lock.lock();
        try {

            // communicators, HPAIs, KNX event pool
            this.controlChannelCommunicator = newControlChannelCommunicator();
            this.dataChannelCommunicator = newDataChannelCommunciator();
            this.controlHPAI = HPAI.of(HostProtocol.IPV4_UDP, (DatagramChannel) controlChannelCommunicator.getChannel());
            this.dataHPAI = HPAI.of(HostProtocol.IPV4_UDP, (DatagramChannel) dataChannelCommunicator.getChannel());

            this.channelId = -1;

            // logging
            final InetSocketAddress routerAddress = this.config.getRouterEndpoint();
            LOG.info("Remote Endpoint (Router) : {}:{}", routerAddress.getAddress().getHostAddress(), routerAddress.getPort());
            LOG.info("Local Endpoint  (Control): {}:{}", this.controlHPAI.getAddress().getHostAddress(), this.controlHPAI.getPort());
            LOG.info("Local Endpoint  (Data)   : {}:{}", this.dataHPAI.getAddress().getHostAddress(), this.dataHPAI.getPort());

            // channel executors
            // 1) Control Channel Receiver,
            // 2) Data Channel Receiver and
            // 3) Connection State Monitor
            this.channelExecutor = Executors.newFixedThreadPool(3);
            this.channelExecutor.execute(controlChannelCommunicator);
            this.channelExecutor.execute(dataChannelCommunicator);

            // get channel for further communications
            this.fetchChannelIdFromRouter();

            // after obtaining channel id - start monitor as well
            this.channelExecutor.submit(this.createConnectionStateMonitor());

            // do not accept more services anymore!
            this.channelExecutor.shutdown();
            LOG.info("Channel Executor created: {}", this.channelExecutor);

            // notifies the extension plug-in about start of service / communication
            this.notifyPlugins(this, this.config.getExtensionPlugins(), (p, c) -> p.onStart());
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Creates a new instance of {@link DescriptionChannelCommunicator} for description channel communication
     * and forwards the KNX packets to subscribed tasks.
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link DescriptionResponseTask} receiving the description, only once</li>
     * </ul>
     *
     * @return {@link ControlChannelCommunicator}
     */
    private DescriptionChannelCommunicator newDescriptionChannelCommunicator() {
        final DescriptionChannelCommunicator communicator = new DescriptionChannelCommunicator(this);
        communicator.subscribe(new DescriptionResponseTask(this));
        return communicator;
    }

    /**
     * Creates a new instance of {@link ControlChannelCommunicator} for control channel communication
     * and forwards the KNX packets to subscribed tasks.
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link ConnectResponseTask} receiving the response after {@link ConnectRequestBody}, only once time</li>
     * <li>{@link ConnectionStateResponseTask} receiving connection health status from KNX Net/IP router</li>
     * <li>{@link DisconnectRequestTask} when disconnect is initiated by the KNX Net/IP router</li>
     * <li>{@link DisconnectResponseTask} as answer from KNX Net/IP router when disconnect is initiated by the
     * client</li>
     * </ul>
     *
     * @return {@link ControlChannelCommunicator}
     */
    private ControlChannelCommunicator newControlChannelCommunicator() {
        final ControlChannelCommunicator communicator = new ControlChannelCommunicator(this);
        communicator.subscribe(new ConnectResponseTask(this));
        communicator.subscribe(new ConnectionStateResponseTask(this));
        communicator.subscribe(new DisconnectRequestTask(this));
        communicator.subscribe(new DisconnectResponseTask(this));
        return communicator;
    }

    /**
     * Creates a new instance of {@link DataChannelCommunicator} for data channel communication
     * and forwards the KNX packets to subscribed tasks.
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link TunnellingRequestTask} when KNX Net/IP router notifies the client about a change from a remote KNX
     * device</li>
     * <li>{@link TunnellingAckTask} as answer from KNX Net/IP router when sending a data packet</li>
     * </ul>
     *
     * @return {@link DataChannelCommunicator}
     */
    private DataChannelCommunicator newDataChannelCommunciator() {
        final DataChannelCommunicator communicator = new DataChannelCommunicator(this);
        communicator.subscribe(new TunnellingRequestTask(this));
        communicator.subscribe(new TunnellingAckTask(this));
        return communicator;
    }

    /**
     * Registers the {@link ConnectionStateMonitor} to send {@link ConnectionStateRequestBody} frequently to the
     * KNX Net/IP router and monitors the health status.
     * <p>
     * No subscribers.
     *
     * @return {@link ConnectionStateMonitor}
     */
    private ConnectionStateMonitor createConnectionStateMonitor() {
        return new ConnectionStateMonitor(this);
    }

    @Override
    public final void close() {
        LOG.trace("Method 'close()' called.");

        // already closed?
        if (this.closed.getAndSet(true)) {
            LOG.debug("Already closed. Do nothing!");
            return;
        }

        this.lock.lock();
        try {
            // notifies the extension plug-ins about shutdown
            this.notifyPlugins(this, this.config.getExtensionPlugins(), (p, c) -> p.onShutdown());

            this.stopServices();
        } finally {
            this.lock.unlock();
        }

        LOG.trace("Method 'close()' completed.");
    }

    /**
     * Closes the KNX communication, services and channels
     */
    private void stopServices() {
        LOG.trace("Method 'stopServices()' called");
        boolean isOk = true;
        try {
            // Check if there is already a disconnect request present in the event pool.
            // 1) If exists, disconnect request came from KNX Net/IP router -> no disconnect request to sent
            // 2) If NOT exists, client is closing the communication and send disconnect request to KNX Net/IP router
            if (this.channelId > 0 && !this.eventPool.disconnectEvent().hasRequest()) {
                LOG.trace("Control channel is still connected. Send disconnect request.");
                // create body
                final DisconnectRequestBody requestBody = DisconnectRequestBody.create(this.channelId, this.controlHPAI);
                final DisconnectResponseBody responseBody = this.sendAndWait(requestBody, config.getTimeoutDisconnectRequest());
                if (responseBody != null) {
                    LOG.debug("Disconnect Response Body retrieved: {}", responseBody);
                } else {
                    LOG.debug("No Disconnect Response Body retrieved. Continue with disconnect.");
                    isOk = false;
                }
            }
        } finally {
            // close communicators
            isOk &= Closeables.closeQuietly(controlChannelCommunicator);
            isOk &= Closeables.closeQuietly(dataChannelCommunicator);
            LOG.info("Channel Communicator stopped gracefully. Status: {}", isOk);

            // shutdown executors now
            isOk &= Closeables.shutdownQuietly(this.communicationExecutor, 0, TimeUnit.SECONDS);
            isOk &= Closeables.shutdownQuietly(this.channelExecutor, 0, TimeUnit.SECONDS);
            isOk &= Closeables.shutdownQuietly(this.pluginExecutor, 10, TimeUnit.SECONDS);
            LOG.info("KNX Services stopped gracefully. Status: {}", isOk);
        }
    }

    public final HPAI getControlHPAI() {
        return this.controlHPAI;
    }

    public final HPAI getDataHPAI() {
        return this.dataHPAI;
    }

    public final KnxEventPool getEventPool() {
        return this.eventPool;
    }

    public KnxStatusPool getStatusPool() {
        return this.statusPool;
    }

    public final int getChannelId() {
        return this.channelId;
    }

    public final boolean isClosed() {
        return this.closed.get();
    }

    private AbstractChannelCommunicator getChannelCommunciator(final Body body) {
        if (body instanceof DataChannelRelated) {
            return this.dataChannelCommunicator;
        } else if (body instanceof ControlChannelRelated) {
            return this.controlChannelCommunicator;
        } else {
            throw new IllegalArgumentException("No channel relation defined for body. I do not know to which channel communicator the body belongs to: " + body);
        }
    }

    /**
     * Notifies all {@link ObserverPlugin} about incoming {@link Body}
     *
     * @param body any KNX body
     */
    public void notifyPluginsIncomingBody(final Body body) {
        this.statistics.onIncomingBody(body);
        this.notifyPlugins(body, this.config.getObserverPlugins(), ObserverPlugin::onIncomingBody);
    }

    /**
     * Notifies all {@link ObserverPlugin} about outgoing {@link Body}
     *
     * @param body any KNX body
     */
    public void notifyPluginsOutgoingBody(final Body body) {
        this.statistics.onOutgoingBody(body);
        this.notifyPlugins(body, this.config.getObserverPlugins(), ObserverPlugin::onOutgoingBody);
    }

    /**
     * Notifies all {@link ObserverPlugin} about throwable during incoming or outgoing {@link Body}
     *
     * @param throwable an instance of {@link Throwable} to be sent to plug-ins
     */
    public void notifyPluginsError(final Throwable throwable) {
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
    private <O, P extends Plugin> void notifyPlugins(final O obj, final List<P> plugins, BiConsumer<P, O> consumer) {
        if (this.pluginExecutor.isShutdown()) {
            LOG.warn("Could not send to plug-ins because plugin executor is shutdown already: {}",
                    obj instanceof Throwable ? ((Throwable) obj).getMessage() : obj);
        } else {
            for (final P plugin : plugins) {
                CompletableFuture.runAsync(() -> {
                    LOG.trace("Send to plugin: {}", plugin);
                    try {
                        consumer.accept(plugin, obj);
                    } catch (final Exception ex) {
                        LOG.debug("Exception during notifyPlugins(T, List<Plugin>, BiConsumer)", ex);
                    }
                }, this.pluginExecutor);
            }
        }
    }

    /**
     * Verifies if the {@link Body} response if it meets the {@link #channelId}. The channel id check is skipped when
     * given {@link Body} doesn't implement the {@link ChannelIdAware} interface.
     * <p/>
     * The {@link #channelId} is fetched and set during initialization of connection with KNX Net/IP router.
     *
     * @param body any KNX body to be verified
     * @return {@code true} if channel id is valid for current KNX client, otherwise {@link KnxWrongChannelIdException} is thrown.
     * @throws KnxWrongChannelIdException when channel id is not valid
     */
    public final boolean verifyChannelId(final Body body) {
        // if body is channel id aware then verify the channel id, otherwise skip it
        if (ChannelIdAware.class.isAssignableFrom(body.getClass())) {
            final ChannelIdAware channelIdAwareBody = (ChannelIdAware) body;
            final int actualChannelId = channelIdAwareBody.getChannelId();
            // skip check for ConnectResponseBody because it is the first time where we get channelId
            if (body instanceof ConnectResponseBody) {
                LOG.debug("Channel ID in ConnectResponseBody isn't be checked because it is the first response with channel id.");
            }
            // verify if channel id is the expected one
            else if (actualChannelId != this.channelId) {
                LOG.warn("Wrong Channel ID received for body: {}", body);
                throw new KnxWrongChannelIdException(channelIdAwareBody, this.channelId);
            }
        }
        return true;
    }

    /**
     * Returns the description response body from KNX Net/IP router containing device information, supported device
     * capabilities.
     *
     * @return {@link DescriptionResponseBody}
     */
    private DescriptionResponseBody fetchDescriptionFromRouter() {
        LOG.trace("Method 'fetchDescriptionFromRouter()' called.");

        // Description request / response is one-time task before establishing communication to KNX Net/IP Router
        final DescriptionChannelCommunicator communicator = newDescriptionChannelCommunicator();

        // Create executor service for description communication
        final ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(communicator);
        es.shutdown();

        // It opens a new channel for description communication and processing. Afterwards it will be shutdown.
        try (communicator) {
            // send description request
            final DescriptionRequestBody requestBody = DescriptionRequestBody.create();
            LOG.debug("Request for description: {}", requestBody);

            final DescriptionResponseBody responseBody = communicator.sendAndWait(requestBody, config.getTimeoutDescriptionRequest());
            if (responseBody != null) {
                LOG.debug("Description response received: {}", responseBody);
                return responseBody;
            } else {
                LOG.error("Description response not received for request: {}", requestBody);
                throw new KnxBodyNotReceivedException(DescriptionResponseBody.class);
            }
        } finally {
            Closeables.shutdownQuietly(es);
        }
    }

    /**
     * Fetches the channel id from KNX Net/IP router.
     *
     * @throws KnxCommunicationException in case channel id could not be fetched due an error or timeout
     */
    private void fetchChannelIdFromRouter() {
        LOG.trace("Method 'fetchChannelIdFromRouter()' called.");

        // create connect request and send it
        final ConnectionRequestInformation cri = ConnectionRequestInformation.create();
        final ConnectRequestBody connectRequestBody = ConnectRequestBody.create(this.controlHPAI, this.dataHPAI, cri);
        final ConnectResponseBody connectResponseBody = this.sendAndWait(connectRequestBody, config.getTimeoutConnectRequest());

        // check status and return channel id
        if (connectResponseBody != null && connectResponseBody.getStatus() == Status.E_NO_ERROR) {
            this.channelId = connectResponseBody.getChannelId();
            LOG.info("Channel ID received: {}", this.channelId);
        } else {
            throw new KnxChannelIdNotReceivedException(connectResponseBody);
        }
    }

    @Override
    public final void send(final Body body) {
        this.getChannelCommunciator(body).send(body);
    }

    @Override
    public final <T extends ResponseBody> Future<T> send(final RequestBody requestBody, final long msTimeout) {
        return this.getChannelCommunciator(requestBody).send(requestBody, msTimeout);
    }

    @Override
    public final @Nullable
    <T extends ResponseBody> T sendAndWait(final RequestBody requestBody, final long msTimeout) {
        try {
            final Future<T> future = send(requestBody, msTimeout);
            return future.get();
        } catch (final Exception ex) {
            LOG.warn("Exception thrown during send and wait asynchronously.", ex);
            return null;
        }
    }
}
