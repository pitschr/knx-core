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

package li.pitschmann.knx.core.net;

import li.pitschmann.knx.core.AbstractMultiRawData;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Networker;
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
public final class HPAI extends AbstractMultiRawData {
    /**
     * Fixed length for HPAI
     */
    public static final int KNXNET_HPAI_LENGTH = 0x08;
    private static final HPAI DEFAULT = of(HostProtocol.IPV4_UDP, Networker.getAddressUnbound(), 0);

    private final int length;
    private final HostProtocol protocol;
    private final InetAddress address;
    private final int port;

    private HPAI(final byte[] hpaiRawData) {
        super(hpaiRawData);

        this.length = Byte.toUnsignedInt(hpaiRawData[0]);
        this.protocol = HostProtocol.valueOf(Byte.toUnsignedInt(hpaiRawData[1]));
        this.address = Networker.getByAddress(hpaiRawData[2], hpaiRawData[3], hpaiRawData[4], hpaiRawData[5]);
        this.port = Bytes.toUnsignedInt(hpaiRawData[6], hpaiRawData[7]);
    }

    /**
     * Returns an instance of {@link HPAI}
     *
     * @param bytes complete byte array for {@link HPAI}
     * @return a new immutable {@link HPAI}
     */
    public static HPAI of(final byte[] bytes) {
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
        // validate
        if (channel == null) {
            throw new KnxNullPointerException("channel");
        }

        // is channel supported?
        if (channel instanceof DatagramChannel) {
            // UDP
            final var socket = ((DatagramChannel) channel).socket();
            return of(HostProtocol.IPV4_UDP, socket.getLocalAddress(), socket.getLocalPort());
        } else if (channel instanceof SocketChannel) {
            final var socket = ((SocketChannel) channel).socket();
            return of(HostProtocol.IPV4_TCP, socket.getLocalAddress(), socket.getLocalPort());
        } else {
            throw new KnxIllegalArgumentException(String.format("Channel type is not supported: %s", channel.getClass()));
        }
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
        // validate
        if (protocol == null) {
            throw new KnxNullPointerException("protocol");
        } else if (address == null) {
            throw new KnxNullPointerException("address");
        } else if (port < 0 || port > 0xFFFF) {
            throw new KnxNumberOutOfRangeException("port", 0, 0xFFFF, port);
        }

        final var ipAddressAsBytes = address.getAddress();
        final var ipPortAsBytes = new byte[]{(byte) (port >>> 8), (byte) port};

        // create bytes
        final var bytes = new byte[]{KNXNET_HPAI_LENGTH, protocol.getCodeAsByte(), ipAddressAsBytes[0], ipAddressAsBytes[1], ipAddressAsBytes[2],
                ipAddressAsBytes[3], ipPortAsBytes[0], ipPortAsBytes[1]};

        return of(bytes);
    }

    @Override
    protected void validate(final byte[] hpaiRawData) {
        if (hpaiRawData == null) {
            throw new KnxNullPointerException("hpaiRawData");
        } else if (hpaiRawData.length != KNXNET_HPAI_LENGTH) {
            throw new KnxNumberOutOfRangeException("hpaiRawData", KNXNET_HPAI_LENGTH, KNXNET_HPAI_LENGTH, hpaiRawData.length, hpaiRawData);
        }
    }

    public int getLength() {
        return this.length;
    }

    public HostProtocol getProtocol() {
        return this.protocol;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("length", this.length + " (" + ByteFormatter.formatHex(this.length) + ")")
                .add("protocol", this.protocol)
                .add("address", this.address.getHostAddress() + " (" + ByteFormatter.formatHexAsString(this.address.getAddress()) + ")")
                .add("port", this.port + " (" + ByteFormatter.formatHex(this.port) + ")");
        // @formatter:off
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof HPAI) {
            final var other = (HPAI) obj;
            return this.length == other.length && Objects.equals(this.protocol, other.protocol) && Objects.equals(this.address, other.address) && this.port == other.port;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.length, this.protocol, this.address, this.port);
    }
}
