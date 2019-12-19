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

package li.pitschmann.knx.core.utils;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.exceptions.KnxCommunicationException;
import li.pitschmann.knx.core.net.HPAI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.UnknownHostException;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.MulticastChannel;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Network utility class
 *
 * @author PITSCHR
 */
public final class Networker {
    private static final Logger log = LoggerFactory.getLogger(Networker.class);
    private static final InetAddress LOCALHOST = Networker.getByAddress(127, 0, 0, 1);
    private static Map<NetworkInterface, List<InetAddress>> networkInterfaceMap;

    private Networker() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Returns {@link InetAddress} instance for localhost
     *
     * @return
     */
    public static InetAddress getLocalHost() {
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
     * @param addressAsString address expected in {@code aaa.bbb.ccc.ddd} format (exceptional pattern: "localhost")
     * @return {@link InetAddress}
     * @throws IllegalArgumentException in case the given {@code addressAsString} is not valid
     */
    public static InetAddress getByAddress(final String addressAsString) {
        if (addressAsString.equalsIgnoreCase("localhost")) {
            return getLocalHost();
        }

        // more strict implementation rather than InetAddress#getByName(String)
        final var blocks = Stream.of(addressAsString.split(Pattern.quote("."))).mapToInt(Integer::parseInt).toArray();
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
     * @param channel either a {@link DatagramChannel} or a {@link SocketChannel}
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

    /**
     * Converts from {@link HPAI} containing address and port to {@link InetSocketAddress}
     *
     * @param hpai
     * @return A new {@link InetSocketAddress} instance
     */
    public static InetSocketAddress toInetSocketAddress(final HPAI hpai) {
        return new InetSocketAddress(hpai.getAddress(), hpai.getPort());
    }

    /**
     * Returns all applicable network interfaces including inet addresses for KNX communication
     *
     * @return map of network interfaces with list of {@link InetAddress}, in case of issue an empty map is returned
     */
    public static Map<NetworkInterface, List<InetAddress>> getNetworkInterfaces() { // NOSONAR
        // return network interface map from cache if possible
        if (networkInterfaceMap != null) {
            return networkInterfaceMap;
        }

        // not cached yet -> create!
        final var tmpMap = new LinkedHashMap<NetworkInterface, List<InetAddress>>();

        try {
            final var interfaces = NetworkInterface.getNetworkInterfaces();
            var loopAddressAlreadyFound = false;
            while (interfaces.hasMoreElements()) {
                final var ni = interfaces.nextElement();

                if (!ni.isUp()) {
                    log.trace("Network Interface is not up. Ignored: {}", ni);
                    continue;
                }

                final var inetAddressesToAdd = new LinkedList<InetAddress>();
                final var addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();

                    // without NAT we only keep IPv4-Addresses
                    if (address instanceof Inet4Address) {
                        log.trace("NetworkInterface: {} ({})", ni, address);
                        // keep only one loopback address
                        if (address.isLoopbackAddress()) {
                            // loopback address found
                            if (!loopAddressAlreadyFound) {
                                // this is the first loop back address -> add it
                                inetAddressesToAdd.add(address);
                                loopAddressAlreadyFound = true;
                            } else {
                                log.trace("Loopback address already found. Ignore: {}", address);
                            }
                        } else {
                            // no loopback address -> add it
                            inetAddressesToAdd.add(address);
                        }
                    } else {
                        log.trace("Ignore non-IP4 Address: {}", address);
                    }

                    if (!inetAddressesToAdd.isEmpty()) {
                        tmpMap.put(ni, Collections.unmodifiableList(inetAddressesToAdd));
                    }
                }
            }
        } catch (final SocketException se) {
            log.error("Error during getting network interfaces", se);
        }

        return networkInterfaceMap = Map.copyOf(tmpMap);
    }

    /**
     * Joins the multicast address for all network interfaces returned by {@link #getNetworkInterfaces()}
     *
     * @param channel channel to join
     * @param group   multicast address to be joined
     * @return list of {@link MembershipKey} where the multicast group was joined
     */
    public static List<MembershipKey> joinChannels(final MulticastChannel channel, final InetAddress group) {
        Preconditions.checkNonNull(group);
        // add all applicable network interfaces for discovery and join the multicast group
        // the membership keys will be return to allow to leave the joined multicast groups
        // by dropping of membership
        return getNetworkInterfaces().keySet().stream().map(ni -> {
            log.debug("Network Interface to join multicast address: {}", ni);
            try {
                return channel.join(group, ni);
            } catch (final IOException e) {
                throw new KnxCommunicationException("I/O exception during joining network interface: " + ni, e);
            }
        }).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Creates an UDP channel for communication
     *
     * @param localPort     given port to be used (A port number of {@code zero} will let the system pick up an ephemeral port)
     * @param socketTimeout socket timeout
     * @param socketAddress socket address to be connected, if {@code null} the socket won't be connected yet
     * @return a new instance of {@link DatagramChannel}
     */
    public static <T extends Object> DatagramChannel newDatagramChannel(final int localPort,
                                                                        final long socketTimeout,
                                                                        final @Nullable SocketAddress socketAddress,
                                                                        final @Nullable Map<? extends SocketOption<T>, T> socketOptionMap) {
        try {
            final var channel = DatagramChannel.open(StandardProtocolFamily.INET);
            channel.configureBlocking(false);
            final var socket = channel.socket();
            if (socketOptionMap != null) {
                for (final var option : socketOptionMap.entrySet()) {
                    channel.setOption(option.getKey(), option.getValue());
                }
            }
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
}
