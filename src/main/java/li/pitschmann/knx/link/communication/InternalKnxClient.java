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
import li.pitschmann.knx.link.ChannelIdAware;
import li.pitschmann.knx.link.Configuration;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.ControlChannelRelated;
import li.pitschmann.knx.link.body.DataChannelRelated;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;
import li.pitschmann.knx.link.body.Status;
import li.pitschmann.knx.link.body.dib.ServiceTypeFamily;
import li.pitschmann.knx.link.body.hpai.HPAI;
import li.pitschmann.knx.link.body.hpai.HostProtocol;
import li.pitschmann.knx.link.body.tunnel.ConnectionRequestInformation;
import li.pitschmann.knx.link.communication.communicator.AbstractChannelCommunicator;
import li.pitschmann.knx.link.communication.communicator.ControlChannelCommunicator;
import li.pitschmann.knx.link.communication.communicator.DataChannelCommunicator;
import li.pitschmann.knx.link.communication.communicator.DescriptionChannelCommunicator;
import li.pitschmann.knx.link.communication.task.ConnectResponseTask;
import li.pitschmann.knx.link.communication.task.ConnectionStateResponseTask;
import li.pitschmann.knx.link.communication.task.DescriptionResponseTask;
import li.pitschmann.knx.link.communication.task.DisconnectRequestTask;
import li.pitschmann.knx.link.communication.task.DisconnectResponseTask;
import li.pitschmann.knx.link.communication.task.TunnelingAckTask;
import li.pitschmann.knx.link.communication.task.TunnelingRequestTask;
import li.pitschmann.knx.link.exceptions.KnxBodyNotReceivedException;
import li.pitschmann.knx.link.exceptions.KnxChannelIdNotReceivedException;
import li.pitschmann.knx.link.exceptions.KnxCommunicationException;
import li.pitschmann.knx.link.exceptions.KnxDescriptionNotReceivedException;
import li.pitschmann.knx.link.exceptions.KnxNoTunnelingException;
import li.pitschmann.knx.link.exceptions.KnxWrongChannelIdException;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.link.plugin.Plugin;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.WrappedMdcRunnable;
import li.pitschmann.utils.WrappedMdcSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private static final Logger LOG = LoggerFactory.getLogger(InternalKnxClient.class);
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Lock lock = new ReentrantLock();
    private final KnxEventPool eventPool = new KnxEventPool();
    private final KnxStatisticImpl statistics;
    private final KnxStatusPoolImpl statusPool;
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
        this.statistics = new KnxStatisticImpl();
        // status pool
        this.statusPool = new KnxStatusPoolImpl();

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
        // validate
        try {
            if (this.verify()) {
                LOG.info("Verification passed. Starting KNX services.");
                this.startServices();
            } else {
                throw new KnxNoTunnelingException(
                        "The remote device doesn't support TUNNELING. Please choose a remote device that supports TUNNELING.");
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
    public KnxStatisticImpl getStatistic() {
        return this.statistics;
    }

    @Override
    public KnxStatusPoolImpl getStatusPool() {
        return this.statusPool;
    }

    /**
     * Verify if the retrieved {@link DescriptionResponseBody} returned by the KNX Net/IP device
     * is applicable for current client implementation.
     * <p/>
     * It will just check if the KNX Net/IP device supports tunneling.
     *
     * @return {@code true} if tunneling is supported by KNX Net/IP device and we can proceed with connect, otherwise {@code false}.
     */
    private boolean verify() {
        LOG.trace("Call 'verify()' method.");
        final var descriptionResponseBody = this.fetchDescriptionFromKNX();

        // get supported device families
        final var serviceFamilies = descriptionResponseBody.getSupportedDeviceFamilies().getServiceFamilies();
        LOG.debug("Supported device families: {}", serviceFamilies);

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

            // communicators, HPAIs, KNX event pool
            this.controlChannelCommunicator = newControlChannelCommunicator();
            this.dataChannelCommunicator = newDataChannelCommunciator();
            this.controlHPAI = HPAI.of(HostProtocol.IPV4_UDP, (DatagramChannel) controlChannelCommunicator.getChannel());
            this.dataHPAI = HPAI.of(HostProtocol.IPV4_UDP, (DatagramChannel) dataChannelCommunicator.getChannel());

            this.channelId = -1;

            // logging
            final var endpoint = this.config.getEndpoint();
            LOG.info("Remote Endpoint (KNX Net/IP)     : {}:{}", endpoint.getAddress().getHostAddress(), endpoint.getPort());
            LOG.info("Local Endpoint  (Control Channel): {}:{}", this.controlHPAI.getAddress().getHostAddress(), this.controlHPAI.getPort());
            LOG.info("Local Endpoint  (Data Channel)   : {}:{}", this.dataHPAI.getAddress().getHostAddress(), this.dataHPAI.getPort());

            // channel executors
            // 1) Control Channel Receiver,
            // 2) Data Channel Receiver and
            // 3) Connection State Monitor
            this.channelExecutor = Executors.newFixedThreadPool(3);
            this.channelExecutor.execute(new WrappedMdcRunnable(controlChannelCommunicator));
            this.channelExecutor.execute(new WrappedMdcRunnable(dataChannelCommunicator));

            // get channel for further communications
            this.channelId = this.fetchChannelIdFromKNX();
            LOG.info("Channel ID received: {}", this.channelId);

            // after obtaining channel id - start monitor as well
            this.channelExecutor.submit(new WrappedMdcRunnable(this.createConnectionStateMonitor()));

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
        final var communicator = new DescriptionChannelCommunicator(this);
        communicator.subscribe(new WrappedMdcSubscriber<>(new DescriptionResponseTask(this)));
        return communicator;
    }

    /**
     * Creates a new instance of {@link ControlChannelCommunicator} for control channel communication
     * and forwards the KNX packets to subscribed tasks.
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link ConnectResponseTask} receiving the response after {@link ConnectRequestBody}, only once time</li>
     * <li>{@link ConnectionStateResponseTask} receiving connection health status from KNX Net/IP device</li>
     * <li>{@link DisconnectRequestTask} when disconnect is initiated by the KNX Net/IP device</li>
     * <li>{@link DisconnectResponseTask} as answer from KNX Net/IP device when disconnect is initiated by the
     * client</li>
     * </ul>
     *
     * @return {@link ControlChannelCommunicator}
     */
    private ControlChannelCommunicator newControlChannelCommunicator() {
        final var communicator = new ControlChannelCommunicator(this);
        communicator.subscribe(new WrappedMdcSubscriber<>(new ConnectResponseTask(this)));
        communicator.subscribe(new WrappedMdcSubscriber<>(new ConnectionStateResponseTask(this)));
        communicator.subscribe(new WrappedMdcSubscriber<>(new DisconnectRequestTask(this)));
        communicator.subscribe(new WrappedMdcSubscriber<>(new DisconnectResponseTask(this)));
        return communicator;
    }

    /**
     * Creates a new instance of {@link DataChannelCommunicator} for data channel communication
     * and forwards the KNX packets to subscribed tasks.
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link TunnelingRequestTask} when KNX Net/IP device notifies the client about a change from a remote KNX
     * device</li>
     * <li>{@link TunnelingAckTask} as answer from KNX Net/IP device when sending a data packet</li>
     * </ul>
     *
     * @return {@link DataChannelCommunicator}
     */
    private DataChannelCommunicator newDataChannelCommunciator() {
        final var communicator = new DataChannelCommunicator(this);
        communicator.subscribe(new WrappedMdcSubscriber<>(new TunnelingRequestTask(this)));
        communicator.subscribe(new WrappedMdcSubscriber<>(new TunnelingAckTask(this)));
        return communicator;
    }

    /**
     * Registers the {@link ConnectionStateMonitor} to send {@link ConnectionStateRequestBody} frequently to the
     * KNX Net/IP device and monitors the health status.
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
        } else {
            LOG.info("Client will be closed.");
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
        var isOk = true;
        try {
            // Check if there is already a disconnect request present in the event pool.
            // 1) If exists, disconnect request came from KNX Net/IP device -> no disconnect request to sent
            // 2) If NOT exists, client is closing the communication and send disconnect request to KNX Net/IP device
            if (this.channelId > 0 && !this.eventPool.disconnectEvent().hasRequest()) {
                LOG.trace("Control channel is still connected. Send disconnect request.");
                // create body
                final var requestBody = DisconnectRequestBody.create(this.channelId, this.controlHPAI);
                try {
                    final var responseBody = this.send(requestBody, config.getTimeoutDisconnectRequest()).get();
                    if (responseBody != null) {
                        LOG.debug("Disconnect Response Body retrieved: {}", responseBody);
                    } else {
                        throw new KnxBodyNotReceivedException(DisconnectResponseBody.class);
                    }
                } catch (KnxBodyNotReceivedException | InterruptedException | ExecutionException ex) {
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
    protected <O, P extends Plugin> void notifyPlugins(final O obj, final List<P> plugins, BiConsumer<P, O> consumer) {
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
     * The {@link #channelId} is fetched and set during initialization of connection with KNX Net/IP device.
     *
     * @param body any KNX body to be verified
     * @return {@code true} if channel id is valid for current KNX client, otherwise {@link KnxWrongChannelIdException} is thrown.
     * @throws KnxWrongChannelIdException when channel id is not valid
     */
    public final boolean verifyChannelId(final Body body) {
        // if body is channel id aware then verify the channel id, otherwise skip it
        if (ChannelIdAware.class.isAssignableFrom(body.getClass())) {
            final var channelIdAwareBody = (ChannelIdAware) body;
            final var actualChannelId = channelIdAwareBody.getChannelId();
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
     * Returns the description response body from KNX Net/IP device containing device information, supported device
     * capabilities.
     *
     * @return {@link DescriptionResponseBody}
     */
    private DescriptionResponseBody fetchDescriptionFromKNX() {
        LOG.trace("Method 'fetchDescriptionFromKNX()' called.");

        // Description request / response is one-time task before establishing communication to KNX Net/IP device
        final var communicator = newDescriptionChannelCommunicator();

        // Create executor service for description communication
        final var es = Executors.newSingleThreadExecutor();
        es.execute(new WrappedMdcRunnable(communicator));
        es.shutdown();

        // send description request
        final var descriptionRequestBody = DescriptionRequestBody.create();
        LOG.debug("Request for description: {}", descriptionRequestBody);

        // It opens a new channel for description communication and processing. Afterwards it will be shutdown.
        try (communicator) {
            final var descriptionResponseBody = communicator.<DescriptionResponseBody>send(descriptionRequestBody, config.getTimeoutDescriptionRequest()).get();
            // check status
            Preconditions.checkState(descriptionResponseBody != null, descriptionResponseBody);
            return descriptionResponseBody;
        } catch (final Exception ex) {
            LOG.error("Exception during fetch description from KNX Net/IP device", ex);
            throw new KnxDescriptionNotReceivedException(descriptionRequestBody);
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
        LOG.trace("Method 'fetchChannelIdFromKNX()' called.");

        // create connect request and send it
        final var cri = ConnectionRequestInformation.create();
        final var connectRequestBody = ConnectRequestBody.create(this.controlHPAI, this.dataHPAI, cri);
        LOG.debug("Request for connect: {}", connectRequestBody);

        try {
            final var connectResponseBody = this.<ConnectResponseBody>send(connectRequestBody, config.getTimeoutConnectRequest()).get();
            // check status
            Preconditions.checkState(connectResponseBody != null && connectResponseBody.getStatus() == Status.E_NO_ERROR, connectResponseBody);
            return connectResponseBody.getChannelId();
        } catch (final Exception ex) {
            LOG.error("Exception during fetch channel id from KNX Net/IP device", ex);
            throw new KnxChannelIdNotReceivedException(connectRequestBody);
        }
    }

    @Override
    public final void send(final Body body) {
        this.getChannelCommunciator(body).send(body);
    }

    @Override
    public final <T extends ResponseBody> CompletableFuture<T> send(final RequestBody requestBody, final long msTimeout) {
        return this.getChannelCommunciator(requestBody).send(requestBody, msTimeout);
    }
}
