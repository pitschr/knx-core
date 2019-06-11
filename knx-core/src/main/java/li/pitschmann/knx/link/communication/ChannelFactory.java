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

import li.pitschmann.knx.link.Configuration;
import li.pitschmann.knx.link.exceptions.KnxCommunicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;

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
     * Creates a channel for description communication (description)
     * to the given socket address provided by {@link Configuration}
     *
     * @param config
     * @return a new instance of {@link DatagramChannel} for description related communication
     * @throws KnxCommunicationException in case the channel could not be created
     */
    public static SelectableChannel newDescriptionChannel(final @Nonnull Configuration config) {
        final var localPort = config.getDescriptionChannelPort();
        final var socketAddress = config.getEndpoint();
        final var socketTimeout = config.getSocketTimeoutDescriptionChannel();
        log.debug("Create new description channel for local {}: {}: {} (socket timeout: {}ms)", localPort,
                socketAddress, socketTimeout);
        return newDatagramChannel(localPort, socketTimeout, socketAddress);
    }

    /**
     * Creates a channel for control communication (connect, description, connection state, disconnect)
     * to the given socket address provided by {@link Configuration}
     *
     * @param config
     * @return a new instance of {@link DatagramChannel} for control-related communication
     * @throws KnxCommunicationException in case the channel could not be created
     */
    public static SelectableChannel newControlChannel(final @Nonnull Configuration config) {
        final var localPort = config.getControlChannelPort();
        final var socketAddress = config.getEndpoint();
        final var socketTimeout = config.getSocketTimeoutControlChannel();
        log.debug("Create new control channel for local port {}: {}: {} (socket timeout: {}ms)", localPort,
                socketAddress, socketTimeout);
        return newDatagramChannel(localPort, socketTimeout, socketAddress);
    }

    /**
     * Creates channel for data communication and connects to the given socket
     * address provided by {@link Configuration}
     *
     * @param config
     * @return a new instance of {@link DatagramChannel} for data-related communication
     * @throws KnxCommunicationException in case the channel could not be created
     */
    public static SelectableChannel newDataChannel(final @Nonnull Configuration config) {
        final var localPort = config.getDataChannelPort();
        final var socketAddress = config.getEndpoint();
        final var socketTimeout = config.getSocketTimeoutDataChannel();
        log.debug("Create new data channel for local port {}: {} (socket timeout: {}ms)", localPort,
                socketAddress, socketTimeout);
        return newDatagramChannel(localPort, socketTimeout, socketAddress);
    }

    /**
     * Creates an UDP channel for communication
     * <p/>
     * See: {@link #newDatagramChannel(int, long, SocketAddress)} while local address and an
     * ephemeral port will be picked up by the system and {@link SocketAddress} is {@code null} which means
     * that the socket won't be connected yet
     *
     * @param socketTimeout socket timeout
     * @return a new instance of {@link DatagramChannel}
     */
    public static DatagramChannel newDatagramChannel(final long socketTimeout) {
        return newDatagramChannel(0, socketTimeout, null);
    }

    /**
     * Creates an UDP channel for communication
     *
     * @param localPort given port to be used (A port number of {@code zero} will let the system pick up an ephemeral port)
     * @param socketTimeout socket timeout
     * @param socketAddress socket address to be connected, if {@code null} the socket won't be connected yet
     * @return a new instance of {@link DatagramChannel}
     */
    public static DatagramChannel newDatagramChannel(final int localPort, final long socketTimeout, final @Nullable SocketAddress socketAddress) {
        try {
            final var channel = DatagramChannel.open();
            channel.configureBlocking(false);
            final var socket = channel.socket();
            socket.bind(new InetSocketAddress(localPort));
            socket.setSoTimeout((int) socketTimeout);
            if (socketAddress != null) {
                socket.connect(socketAddress);
            }
            return channel;
        } catch (final IOException e) {
            throw new KnxCommunicationException("Exception occurred during creating datagram channel", e);
        }
    }

//    /**
//     * Creates a TCP channel for communication
//     * @param socketAddress
//     * @param socketTimeout
//     * @return {@link SocketChannel}
//     */
//    private static SocketChannel newSocketChannel(final InetSocketAddress socketAddress, long socketTimeout) {
//        try {
//            final var channel = SocketChannel.open();
//            channel.configureBlocking(false);
//            final var socket = channel.socket();
//            socket.bind(new InetSocketAddress(0));
//            socket.setSoTimeout((int) socketTimeout);
//            socket.connect(socketAddress);
//            return channel;
//        } catch (final IOException e) {
//            throw new KnxCommunicationException("Exception occurred during creating socket channel", e);
//        }
//    }
}
