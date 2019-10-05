package li.pitschmann.knx.link.communication.communicator;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.communication.task.ConnectResponseTask;
import li.pitschmann.knx.link.communication.task.ConnectionStateResponseTask;
import li.pitschmann.knx.link.communication.task.DescriptionResponseTask;
import li.pitschmann.knx.link.communication.task.DisconnectRequestTask;
import li.pitschmann.knx.link.communication.task.DisconnectResponseTask;
import li.pitschmann.knx.link.communication.task.RoutingIndicationTask;
import li.pitschmann.knx.link.communication.task.SearchResponseTask;
import li.pitschmann.knx.link.communication.task.TunnelingAckTask;
import li.pitschmann.knx.link.communication.task.TunnelingRequestTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Flow;

/**
 * Factory for channel communicators
 * <p/>
 * Discovery: {@link #newDiscoveryChannelCommunicator(InternalKnxClient)}<br/>
 * Description: {@link #newDescriptionChannelCommunicator(InternalKnxClient)}<br/>
 * Tunneling (without NAT): {@link #newControlChannelCommunicator(InternalKnxClient)}} and {@link #newDataChannelCommunicator(InternalKnxClient)}<br/>
 * Tunnelling (with NAT): {@link #newControlAndDataChannelCommunicator(InternalKnxClient)}<br/>
 * Routing: {@link #newRoutingChannelCommunicator(InternalKnxClient)}<br/>
 */
public final class CommunicatorFactory {
    private static final Logger log = LoggerFactory.getLogger(CommunicatorFactory.class);

    private CommunicatorFactory() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Creates new {@link DescriptionChannelCommunicator} for description related packet communications
     *
     * @return communicator
     */
    @Nonnull
    public static DescriptionChannelCommunicator newDescriptionChannelCommunicator(final @Nonnull InternalKnxClient knxClient) {
        final var communicator = new DescriptionChannelCommunicator(knxClient);
        getDescriptionChannelTasks(knxClient).forEach(communicator::subscribe);
        return communicator;
    }

    /**
     * Creates new {@link ControlChannelCommunicator} for control related packet communications
     *
     * @return communicator
     */
    @Nonnull
    public static ControlChannelCommunicator newControlChannelCommunicator(final @Nonnull InternalKnxClient knxClient) {
        final var communicator = new ControlChannelCommunicator(knxClient);
        getControlChannelTasks(knxClient).forEach(communicator::subscribe);
        return communicator;
    }

    /**
     * Creates new {@link DataChannelCommunicator} for data related packet communications
     *
     * @return communicator
     */
    @Nonnull
    public static DataChannelCommunicator newDataChannelCommunicator(final @Nonnull InternalKnxClient knxClient) {
        final var communicator = new DataChannelCommunicator(knxClient);
        getDataChannelTasks(knxClient).forEach(communicator::subscribe);
        return communicator;
    }

    /**
     * Creates new {@link ControlAndDataChannelCommunicator} for control <strong>AND</strong> data
     * related packet communications
     *
     * @return communicator
     */
    @Nonnull
    public static ControlAndDataChannelCommunicator newControlAndDataChannelCommunicator(final @Nonnull InternalKnxClient knxClient) {
        final var communicator = new ControlAndDataChannelCommunicator(knxClient);
        getDataChannelTasks(knxClient).forEach(communicator::subscribe);
        getControlChannelTasks(knxClient).forEach(communicator::subscribe);
        return communicator;
    }

    /**
     * Creates new {@link MulticastChannelCommunicator} for discovery related packet communications
     *
     * @return communicator
     */
    @Nonnull
    public static MulticastChannelCommunicator newDiscoveryChannelCommunicator(final @Nonnull InternalKnxClient knxClient) {
        final var communicator = new MulticastChannelCommunicator(knxClient);
        getDiscoveryChannelTasks(knxClient).forEach(communicator::subscribe);
        return communicator;
    }

    /**
     * Creates new {@link MulticastChannelCommunicator} for routing related packet communications
     *
     * @return communicator
     */
    @Nonnull
    public static MulticastChannelCommunicator newRoutingChannelCommunicator(final @Nonnull InternalKnxClient knxClient) {
        final var communicator = new MulticastChannelCommunicator(knxClient);
        getRoutingChannelTasks(knxClient).forEach(communicator::subscribe);
        return communicator;
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
     * @param knxClient
     * @return unmodifiable list of subscribers
     */
    private static List<Flow.Subscriber<Body>> getDescriptionChannelTasks(final @Nonnull InternalKnxClient knxClient) {
        return Collections.singletonList(new DescriptionResponseTask(knxClient));
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
     * @param knxClient
     * @return list of subscribers
     */
    @Nonnull
    private static List<Flow.Subscriber<Body>> getControlChannelTasks(final @Nonnull InternalKnxClient knxClient) {
        final var subscribers = new ArrayList<Flow.Subscriber<Body>>(4);
        subscribers.add(new ConnectResponseTask(knxClient));
        subscribers.add(new ConnectionStateResponseTask(knxClient));
        subscribers.add(new DisconnectRequestTask(knxClient));
        subscribers.add(new DisconnectResponseTask(knxClient));
        return subscribers;
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
     * @param knxClient
     * @return list of subscribers
     */
    @Nonnull
    private static List<Flow.Subscriber<Body>> getDataChannelTasks(final @Nonnull InternalKnxClient knxClient) {
        final var subscribers = new ArrayList<Flow.Subscriber<Body>>(2);
        subscribers.add(new TunnelingRequestTask(knxClient));
        subscribers.add(new TunnelingAckTask(knxClient));
        return subscribers;
    }

    /**
     * Returns a list of {@link Flow.Subscriber} that is suited for discovery channel communicators
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link SearchResponseTask} when we want to look up for KNX Net/IP devices</li>
     * </ul>
     *
     * @param knxClient
     * @return list of subscribers
     */
    @Nonnull
    private static List<Flow.Subscriber<Body>> getDiscoveryChannelTasks(final @Nonnull InternalKnxClient knxClient) {
        return Collections.singletonList(new SearchResponseTask(knxClient));
    }

    /**
     * Returns a list of {@link Flow.Subscriber} that is suited for routing channel communicators
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link RoutingIndicationTask} when KNX Net/IP device notifies the client about a change from a remote KNX
     * device</li>
     * </ul>
     *
     * @param knxClient
     * @return list of subscribers
     */
    @Nonnull
    private static List<Flow.Subscriber<Body>> getRoutingChannelTasks(final @Nonnull InternalKnxClient knxClient) {
        return Collections.singletonList(new RoutingIndicationTask(knxClient));
    }

}