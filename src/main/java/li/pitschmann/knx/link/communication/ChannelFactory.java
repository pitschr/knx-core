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
import li.pitschmann.knx.link.exceptions.*;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.nio.channels.*;

/**
 * Channel factory to create new channels.
 *
 * @author PITSCHR
 */
public final class ChannelFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ChannelFactory.class);

    private ChannelFactory() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Creates a channel for description communication (description)
     *
     * @param config
     * @return {@link DatagramChannel}
     * @throws KnxCommunicationException in case the channel could not be created
     */
    public static SelectableChannel newDescriptionChannel(final Configuration config) {
        LOG.debug("Create new description channel for: {} (socket timeout: {}ms)", config.getRouterEndpoint(),
                config.getSocketTimeoutControlChannel());
        return newDatagramChannel(config.getRouterEndpoint(), config.getSocketTimeoutControlChannel());
    }

    /**
     * Creates a channel for control communication (connect, description, connection state, disconnect)
     *
     * @param config
     * @return {@link DatagramChannel}
     * @throws KnxCommunicationException in case the channel could not be created
     */
    public static SelectableChannel newControlChannel(final Configuration config) {
        LOG.debug("Create new control channel for: {} (socket timeout: {}ms)", config.getRouterEndpoint(),
                config.getSocketTimeoutControlChannel());
        return newDatagramChannel(config.getRouterEndpoint(), config.getSocketTimeoutControlChannel());
    }

    /**
     * Creates channel for data communication
     *
     * @param config
     * @return {@link DatagramChannel}
     * @throws KnxCommunicationException in case the channel could not be created
     */
    public static SelectableChannel newDataChannel(final Configuration config) {
        LOG.debug("Create new data channel for: {} (socket timeout: {}ms)", config.getRouterEndpoint(), config.getSocketTimeoutDataChannel());
        return newDatagramChannel(config.getRouterEndpoint(), config.getSocketTimeoutDataChannel());
    }

    /**
     * Creates an UDP channel for communication
     *
     * @param socketAddress
     * @param socketTimeout
     * @return {@link DatagramChannel}
     */
    private static DatagramChannel newDatagramChannel(final InetSocketAddress socketAddress, long socketTimeout) {
        try {
            final DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            final DatagramSocket socket = channel.socket();
            socket.bind(new InetSocketAddress(0));
            socket.setSoTimeout((int) socketTimeout);
            socket.connect(socketAddress);
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
//            final SocketChannel channel = SocketChannel.open();
//            channel.configureBlocking(false);
//            final Socket socket = channel.socket();
//            socket.bind(new InetSocketAddress(0));
//            socket.setSoTimeout((int) socketTimeout);
//            socket.connect(socketAddress);
//            return channel;
//        } catch (final IOException e) {
//            throw new KnxCommunicationException("Exception occurred during creating socket channel", e);
//        }
//    }
}
