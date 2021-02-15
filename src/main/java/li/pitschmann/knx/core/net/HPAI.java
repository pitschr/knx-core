/*
 * Copyright (C) 2021 Pitschmann Christoph
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

package li.pitschmann.knx.core.net;

import li.pitschmann.knx.core.MultiRawDataAware;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Networker;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.net.InetAddress;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;

/**
 * Host Protocol Address Information
 * <p>
 * The Host Protocol Address Information structure (HPAI) shall be the
 * address information required to uniquely identify a communication
 * channel on the host protocol. Its size shall vary between different
 * host protocols. For the specific definition of the HPAI consult the
 * host protocol dependent addendums of the KNXnet/IP specification.
 * <p>
 * Both, IP address and port number, shall be stored binary in
 * network octet order.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length              | Host Protocol Code            |
 * | (1 octet = 08h)               | (1 octet)                     |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * |                                                               |
 * | IP Address                                                    |
 * | (4 octets)                                                    |
 * |                                                               |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | IP Port Number                                                |
 * | (2 Octets)                                                    |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * See: KNX Specification, Core, 7.5.1
 *
 * @author PITSCHR
 */
public final class HPAI implements MultiRawDataAware {
    /**
     * Fixed length for HPAI
     */
    public static final int STRUCTURE_LENGTH = 0x08;
    /**
     * Default HPAI (UDP, Address Unbound, Port = 0)
     */
    private static final HPAI DEFAULT = of(HostProtocol.IPV4_UDP, Networker.getAddressUnbound(), 0);

    private final int length;
    private final HostProtocol protocol;
    private final InetAddress address;
    private final int port;

    private HPAI(final byte[] hpaiRawData) {
        this(
                // bytes[0] => length
                Byte.toUnsignedInt(hpaiRawData[0]),
                // bytes[1] => Host Protocol
                HostProtocol.valueOf(Byte.toUnsignedInt(hpaiRawData[1])),
                // bytes[2..5] => Address
                Networker.getByAddress(hpaiRawData[2], hpaiRawData[3], hpaiRawData[4], hpaiRawData[5]),
                // bytes[6+7] => Port
                Bytes.toUnsignedInt(hpaiRawData[6], hpaiRawData[7])
        );
    }

    private HPAI(final int length,
                 final HostProtocol protocol,
                 final InetAddress address,
                 final int port) {
        Preconditions.checkArgument(length == STRUCTURE_LENGTH);
        Preconditions.checkNonNull(protocol, "Host Protocol is required.");
        Preconditions.checkNonNull(address, "Address is required.");
        Preconditions.checkArgument(port >= 0 && port <= 65535,
                "Port is out of range. Expected [0..65535] but was: {}", port);

        this.length = length;
        this.protocol = protocol;
        this.address = address;
        this.port = port;
    }

    /**
     * Returns an instance of {@link HPAI}
     *
     * @param bytes complete byte array for {@link HPAI}
     * @return a new immutable {@link HPAI}
     */
    public static HPAI of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new HPAI(bytes);
    }

    /**
     * Returns the default {@link HPAI} instance. It is equivalent to {@link HostProtocol#IPV4_UDP} and {@link InetAddress}
     * with {@code 0.0.0.0} and port {@code 0}.
     *
     * @return re-usable immutable default {@link HPAI}
     */
    public static HPAI useDefault() {
        return DEFAULT;
    }

    /**
     * Returns an instance of {@link HPAI} based on {@link Channel} instance
     *
     * @param channel communication channel
     * @return a new immutable {@link HPAI}
     */
    public static HPAI of(final Channel channel) {
        Preconditions.checkNonNull(channel, "Channel is required.");

        // is channel supported?
        if (channel instanceof DatagramChannel) {
            // UDP
            final var socket = ((DatagramChannel) channel).socket();
            return of(HostProtocol.IPV4_UDP, socket.getLocalAddress(), socket.getLocalPort());
        } else if (channel instanceof SocketChannel) {
            // TCP
            final var socket = ((SocketChannel) channel).socket();
            return of(HostProtocol.IPV4_TCP, socket.getLocalAddress(), socket.getLocalPort());
        }
        throw new IllegalArgumentException("HPAI is not supported for channel class: " + channel.getClass().getName());
    }

    /**
     * Returns an instance of {@link HPAI}
     *
     * @param protocol host protocol (UDP or TCP)
     * @param address  address of endpoint
     * @param port     port of endpoint
     * @return a new immutable {@link HPAI}
     */
    public static HPAI of(final HostProtocol protocol, final InetAddress address, final int port) {
        return new HPAI(STRUCTURE_LENGTH, protocol, address, port);
    }

    public int getLength() {
        return length;
    }

    public HostProtocol getProtocol() {
        return protocol;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public byte[] toByteArray() {
        final var ipAddressAsBytes = address.getAddress();

        // create bytes
        return new byte[]{
                // Structure Length
                STRUCTURE_LENGTH,
                // Host Protocol
                protocol.getCodeAsByte(),
                // Address
                ipAddressAsBytes[0], ipAddressAsBytes[1], ipAddressAsBytes[2], ipAddressAsBytes[3],
                // Port
                (byte) (port >>> 8), (byte) port
        };
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("length", length)
                .add("protocol", protocol.name())
                .add("address", address.getHostAddress())
                .add("port", port)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof HPAI) {
            final var other = (HPAI) obj;
            return this.length == other.length //
                    && Objects.equals(this.protocol, other.protocol) //
                    && Objects.equals(this.address, other.address) //
                    && this.port == other.port; //
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, protocol, address, port);
    }
}
