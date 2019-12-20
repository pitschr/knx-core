package li.pitschmann.knx.core.communication.communicator;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.communication.InternalKnxClient;
import li.pitschmann.knx.core.communication.task.ConnectResponseTask;
import li.pitschmann.knx.core.communication.task.ConnectionStateResponseTask;
import li.pitschmann.knx.core.communication.task.DescriptionResponseTask;
import li.pitschmann.knx.core.communication.task.DisconnectRequestTask;
import li.pitschmann.knx.core.communication.task.DisconnectResponseTask;
import li.pitschmann.knx.core.communication.task.RoutingIndicationTask;
import li.pitschmann.knx.core.communication.task.SearchResponseTask;
import li.pitschmann.knx.core.communication.task.TunnelingAckTask;
import li.pitschmann.knx.core.communication.task.TunnelingRequestTask;

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
    private CommunicatorFactory() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Creates new {@link DescriptionChannelCommunicator} for description related packet communications
     *
     * @return communicator
     */
    public static DescriptionChannelCommunicator newDescriptionChannelCommunicator(final InternalKnxClient knxClient) {
        final var communicator = new DescriptionChannelCommunicator(knxClient);
        getDescriptionChannelTasks(knxClient).forEach(communicator::subscribe);
        return communicator;
    }

    /**
     * Creates new {@link ControlChannelCommunicator} for control related packet communications
     *
     * @return communicator
     */
    public static ControlChannelCommunicator newControlChannelCommunicator(final InternalKnxClient knxClient) {
        final var communicator = new ControlChannelCommunicator(knxClient);
        getControlChannelTasks(knxClient).forEach(communicator::subscribe);
        return communicator;
    }

    /**
     * Creates new {@link DataChannelCommunicator} for data related packet communications
     *
     * @param client the internal knx client
     * @return communicator
     */
    public static DataChannelCommunicator newDataChannelCommunicator(final InternalKnxClient client) {
        final var communicator = new DataChannelCommunicator(client);
        getDataChannelTasks(client).forEach(communicator::subscribe);
        return communicator;
    }

    /**
     * Creates new {@link ControlAndDataChannelCommunicator} for control <strong>AND</strong> data
     * related packet communications
     *
     * @param client the internal KNX client
     * @return communicator
     */
    public static ControlAndDataChannelCommunicator newControlAndDataChannelCommunicator(final InternalKnxClient client) {
        final var communicator = new ControlAndDataChannelCommunicator(client);
        getDataChannelTasks(client).forEach(communicator::subscribe);
        getControlChannelTasks(client).forEach(communicator::subscribe);
        return communicator;
    }

    /**
     * Creates new {@link ConnectionStateCommunicator} for regular health-check
     * <p>
     * The connection state monitor is used in Tunneling mode only!
     *
     * @param client the internal knx client
     * @return communicator
     */
    public static ConnectionStateCommunicator newConnectionStateCommunicator(final InternalKnxClient client) {
        return new ConnectionStateCommunicator(client);
    }

    /**
     * Creates new {@link MulticastChannelCommunicator} for discovery related packet communications
     *
     * @param client the internal knx client
     * @return communicator
     */
    public static MulticastChannelCommunicator newDiscoveryChannelCommunicator(final InternalKnxClient client) {
        final var communicator = new MulticastChannelCommunicator(client);
        getDiscoveryChannelTasks(client).forEach(communicator::subscribe);
        return communicator;
    }

    /**
     * Creates new {@link MulticastChannelCommunicator} for routing related packet communications
     *
     * @param client the internal knx client
     * @return communicator
     */
    public static MulticastChannelCommunicator newRoutingChannelCommunicator(final InternalKnxClient client) {
        final var communicator = new MulticastChannelCommunicator(client);
        getRoutingChannelTasks(client).forEach(communicator::subscribe);
        return communicator;
    }

    /**
     * Returns a list of {@link Flow.Subscriber} that is suited for description channel communicators
     * <p>
     * Following subscribers are:
     * <ul>
     * <li>{@link DescriptionResponseTask} receiving the response after {@link DescriptionRequestBody} from client</li>
     * </ul>
     *
     * @param client the internal knx client
     * @return unmodifiable list of subscribers
     */
    private static List<Flow.Subscriber<Body>> getDescriptionChannelTasks(final InternalKnxClient client) {
        return Collections.singletonList(new DescriptionResponseTask(client));
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
     * @param client the internal knx client
     * @return list of subscribers
     */
    private static List<Flow.Subscriber<Body>> getControlChannelTasks(final InternalKnxClient client) {
        final var subscribers = new ArrayList<Flow.Subscriber<Body>>(4);
        subscribers.add(new ConnectResponseTask(client));
        subscribers.add(new ConnectionStateResponseTask(client));
        subscribers.add(new DisconnectRequestTask(client));
        subscribers.add(new DisconnectResponseTask(client));
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
     * @param client the internal knx client
     * @return list of subscribers
     */
    private static List<Flow.Subscriber<Body>> getDataChannelTasks(final InternalKnxClient client) {
        final var subscribers = new ArrayList<Flow.Subscriber<Body>>(2);
        subscribers.add(new TunnelingRequestTask(client));
        subscribers.add(new TunnelingAckTask(client));
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
     * @param client the internal knx client
     * @return list of subscribers
     */
    private static List<Flow.Subscriber<Body>> getDiscoveryChannelTasks(final InternalKnxClient client) {
        return Collections.singletonList(new SearchResponseTask(client));
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
     * @param client the internal knx client
     * @return list of subscribers
     */
    private static List<Flow.Subscriber<Body>> getRoutingChannelTasks(final InternalKnxClient client) {
        return Collections.singletonList(new RoutingIndicationTask(client));
    }

}
