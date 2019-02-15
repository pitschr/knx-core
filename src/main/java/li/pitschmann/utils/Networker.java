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

package li.pitschmann.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SocketChannel;
import java.util.stream.StreamSupport;

/**
 * Network utility class
 *
 * @author PITSCHR
 */
public final class Networker {
    private static final InetAddress LOCALHOST = Networker.getByAddress(127, 0, 0, 1);

    private Networker() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Returns {@link InetAddress} instance for localhost (127.0.0.1)
     *
     * @return
     */
    public static InetAddress getLocalhost() {
        return LOCALHOST;
    }

    /**
     * Returns a default 0.0.0.0 {@link InetAddress} instance
     *
     * @return unbound {@link InetAddress}
     */
    public static InetAddress getAddressUnbound() {
        return getByAddress((byte) 0, (byte) 0, (byte) 0, (byte) 0);
    }

    /**
     * Returns the {@link InetAddress} for given string. Each block must be an integer between 0-255.
     * <p>
     * It is a wrapper of {@link InetAddress#getByAddress(byte[])} without throwing {@link UnknownHostException}.
     *
     * @param addressAsString address expected in {@code aaa.bbb.ccc.ddd} format
     * @return {@link InetAddress}
     * @throws IllegalArgumentException in case the given {@code addressAsString} is not valid
     */
    public static InetAddress getByAddress(final String addressAsString) {
        // more strict implementation rather than InetAddress#getByName(String)
        int[] blocks = StreamSupport.stream(Splitter.on('.').split(addressAsString).spliterator(), false).mapToInt(Integer::parseInt).toArray();
        Preconditions.checkArgument(blocks.length == 4);
        return getByAddress(blocks[0], blocks[1], blocks[2], blocks[3]);
    }

    /**
     * Returns the {@link InetAddress} for given four integers. Each integer must be between 0-255.
     * <p>
     * It is a wrapper of {@link InetAddress#getByAddress(byte[])} without throwing {@link UnknownHostException}.
     *
     * @param i1
     * @param i2
     * @param i3
     * @param i4
     * @return {@link InetAddress}
     */
    public static InetAddress getByAddress(final int i1, final int i2, final int i3, final int i4) {
        Preconditions.checkArgument(i1 >= 0 && i1 <= 255);
        Preconditions.checkArgument(i2 >= 0 && i2 <= 255);
        Preconditions.checkArgument(i3 >= 0 && i3 <= 255);
        Preconditions.checkArgument(i4 >= 0 && i4 <= 255);
        return getByAddress((byte) i1, (byte) i2, (byte) i3, (byte) i4);
    }

    /**
     * Returns the {@link InetAddress} for given four bytes.
     * <p>
     * It is a wrapper of {@link InetAddress#getByAddress(byte[])} without throwing {@link UnknownHostException}.
     *
     * @param b1
     * @param b2
     * @param b3
     * @param b4
     * @return {@link InetAddress}
     */
    public static InetAddress getByAddress(final byte b1, final byte b2, final byte b3, final byte b4) {
        try {
            return InetAddress.getByAddress(new byte[]{b1, b2, b3, b4});
        } catch (final UnknownHostException e) {
            // cannot happen!
            throw new AssertionError();
        }
    }

    /**
     * Returns the remote address for given {@code Channel} as a string representative.
     *
     * @param channel
     * @return string representative for remote address
     */
    public static String getRemoteAddressAsString(final Channel channel) {
        try {
            final SocketAddress sa;
            if (channel instanceof DatagramChannel) {
                sa = ((DatagramChannel) channel).getRemoteAddress();
            } else if (channel instanceof SocketChannel) {
                sa = ((SocketChannel) channel).getRemoteAddress();
            } else {
                throw new IllegalArgumentException("Unsupported channel type");
            }

            return sa == null ? "N/A" : sa.toString();
        } catch (final Exception ex) {
            return "Error[" + ex.getMessage() + "]";
        }
    }

    /**
     * Returns the local address for given {@code Channel} as a string representative.
     *
     * @param channel
     * @return string representative for local address
     */
    public static String getLocalAddressAsString(final Channel channel) {
        try {
            final SocketAddress sa;
            if (channel instanceof NetworkChannel) {
                sa = ((NetworkChannel) channel).getLocalAddress();
            } else {
                throw new IllegalArgumentException("Unsupported channel type");
            }

            return sa == null ? "N/A" : sa.toString();
        } catch (final Exception ex) {
            return "Error[" + ex.getMessage() + "]";
        }
    }
}
