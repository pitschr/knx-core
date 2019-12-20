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

package li.pitschmann.knx.core.communication.communicator;

import li.pitschmann.knx.core.communication.InternalKnxClient;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.exceptions.KnxCommunicationException;
import li.pitschmann.knx.core.utils.Networker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.util.Collections;

/**
 * Channel factory to create new channels.
 *
 * @author PITSCHR
 */
public final class ChannelFactory {
    private static final Logger log = LoggerFactory.getLogger(ChannelFactory.class);

    private ChannelFactory() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Creates a channel for multicast communication to the given
     * socket address provided by {@link InternalKnxClient}
     *
     * @param client the internal knx client
     * @return a new instance of {@link DatagramChannel} for discovery related communication
     * @throws KnxCommunicationException in case the channel could not be created
     */
    public static DatagramChannel newMulticastChannel(final InternalKnxClient client) {
        final var localPort = client.getConfig(CoreConfigs.Multicast.PORT);
        final var socketTimeout = client.getConfig(CoreConfigs.Multicast.SOCKET_TIMEOUT);
        final var timeToLive = client.getConfig(CoreConfigs.Multicast.TIME_TO_LIVE);
        log.debug("Create new multicast channel (local port: {}, socket timeout: {} ms, Time-To-Live (TTL): {})",
                localPort, socketTimeout, timeToLive);
        final var socketOptions = Collections.singletonMap(StandardSocketOptions.IP_MULTICAST_TTL, timeToLive);
        return Networker.newDatagramChannel(localPort, socketTimeout, null, socketOptions);
    }

    /**
     * Creates a channel for description communication (description) to the given
     * socket address provided by {@link InternalKnxClient}
     *
     * @param client the internal knx client
     * @return a new instance of {@link DatagramChannel} for description related communication
     * @throws KnxCommunicationException in case the channel could not be created
     */
    public static SelectableChannel newDescriptionChannel(final InternalKnxClient client) {
        final var localPort = client.getConfig(CoreConfigs.Description.PORT);
        final var socketAddress = client.getRemoteEndpoint();
        final var socketTimeout = client.getConfig(CoreConfigs.Description.SOCKET_TIMEOUT);
        log.debug("Create new description channel for local: {} (local port: {}, socket timeout: {} ms)",
                socketAddress, localPort, socketTimeout);
        return Networker.newDatagramChannel(localPort, socketTimeout, socketAddress, null);
    }

    /**
     * Creates a channel for control communication (connect, description, connection state, disconnect)
     * to the given socket address provided by {@link InternalKnxClient}
     *
     * @param client the internal knx client
     * @return a new instance of {@link DatagramChannel} for control-related communication
     * @throws KnxCommunicationException in case the channel could not be created
     */
    public static SelectableChannel newControlChannel(final InternalKnxClient client) {
        final var localPort = client.getConfig(CoreConfigs.Control.PORT);
        final var socketAddress = client.getRemoteEndpoint();
        final var socketTimeout = client.getConfig(CoreConfigs.Control.SOCKET_TIMEOUT);
        log.debug("Create new control channel for local: {} (local port: {}, socket timeout: {} ms)",
                socketAddress, localPort, socketTimeout);
        return Networker.newDatagramChannel(localPort, socketTimeout, socketAddress, null);
    }

    /**
     * Creates channel for data communication and connects to the given socket
     * address provided by {@link InternalKnxClient}
     *
     * @param client the internal knx client
     * @return a new instance of {@link DatagramChannel} for data-related communication
     * @throws KnxCommunicationException in case the channel could not be created
     */
    public static SelectableChannel newDataChannel(final InternalKnxClient client) {
        final var localPort = client.getConfig(CoreConfigs.Data.PORT);
        final var socketAddress = client.getRemoteEndpoint();
        final var socketTimeout = client.getConfig(CoreConfigs.Data.SOCKET_TIMEOUT);
        log.debug("Create new data channel for local: {} (local port: {}, socket timeout: {} ms)",
                socketAddress, localPort, socketTimeout);
        return Networker.newDatagramChannel(localPort, socketTimeout, socketAddress, null);
    }

}
