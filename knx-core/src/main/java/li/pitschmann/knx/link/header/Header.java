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

package li.pitschmann.knx.link.header;

import li.pitschmann.knx.link.AbstractMultiRawData;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;
import li.pitschmann.utils.Strings;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * This class represents the header of KNX/IP and is immutable.
 * <p>
 * All KNX/IP frames shall have a common header, consisting of header length information, the protocol version, the
 * KNX/IP service type identifier, and the total length of the KNX/IP frame.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Header Size                   | KNX/IP Version             |
 * | (1 octet = 06h)               | (1 octet = 10h)               |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Service Type Identifier                                       |
 * | (2 octets)                                                    |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Header Size (06h) + sizeof(message body)                      |
 * | (2 octets)                                                    |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 *
 * @author PITSCHR
 */
public final class Header extends AbstractMultiRawData {
    /**
     * Constant size of KNX/IP header as defined in protocol version 1.0
     */
    public static final int KNXNET_HEADER_LENGTH = 0x06;
    /**
     * Identifier for KNX/IP protocol version 1.0
     */
    public static final int KNXNET_PROTOCOL_VERSION = 0x10;

    private final int length;
    private final int protocolVersion;
    private final ServiceType serviceType;
    private final int totalLength;

    private Header(final @Nonnull byte[] headerRawData) {
        super(headerRawData);

        this.length = Byte.toUnsignedInt(headerRawData[0]);
        this.protocolVersion = Byte.toUnsignedInt(headerRawData[1]);
        this.serviceType = ServiceType.valueOf(Bytes.toUnsignedInt(headerRawData[2], headerRawData[3]));
        this.totalLength = Bytes.toUnsignedInt(headerRawData[4], headerRawData[5]);
    }

    /**
     * Builds a new {@link Header} instance
     *
     * @param bytes the given parameter can be either bytes for header only, but also the complete byte array stream. In
     *              case of complete byte array stream the header will be cut at given {@link #KNXNET_HEADER_LENGTH}
     * @return a new immutable {@link Header}
     */
    @Nonnull
    public static Header of(final @Nonnull byte[] bytes) {
        if (bytes == null) {
            throw new KnxNullPointerException("bytes");
        } else if (bytes.length < KNXNET_HEADER_LENGTH) {
            throw new KnxNumberOutOfRangeException("bytes", KNXNET_HEADER_LENGTH, Integer.MAX_VALUE, bytes.length, bytes);
        }

        if (bytes.length == KNXNET_HEADER_LENGTH) {
            return new Header(bytes);
        } else {
            return new Header(Arrays.copyOf(bytes, KNXNET_HEADER_LENGTH));
        }
    }

    /**
     * Creates a new {@link Header} instance
     *
     * @param serviceType
     * @param totalLength
     * @return a new immutable {@link Header}
     */
    @Nonnull
    public static Header of(final @Nonnull ServiceType serviceType, final int totalLength) {
        if (serviceType == null) {
            throw new KnxNullPointerException("serviceType");
        } else if (totalLength < KNXNET_HEADER_LENGTH || totalLength > 0xFFFF) {
            throw new KnxNumberOutOfRangeException("totalLength", KNXNET_HEADER_LENGTH, 0xFFFF, totalLength);
        }

        final var serviceTypeIdentifierAsBytes = serviceType.getCodeAsBytes();
        final var lengthAsBytes = new byte[]{(byte) (totalLength >>> 8), (byte) totalLength};

        // create bytes
        final var bytes = new byte[]{Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, serviceTypeIdentifierAsBytes[0],
                serviceTypeIdentifierAsBytes[1], lengthAsBytes[0], lengthAsBytes[1]};

        return of(bytes);
    }

    /**
     * Creates a new {@link Header} instance based on {@link Body} structure.
     *
     * @param body
     * @return a new immutable {@link Header}
     */
    @Nonnull
    public static Header of(final @Nonnull Body body) {
        if (body == null) {
            throw new KnxNullPointerException("body");
        }

        return of(body.getServiceType(), Header.KNXNET_HEADER_LENGTH + body.getRawData().length);
    }

    /**
     * Header length (actually header length is always {@link #KNXNET_HEADER_LENGTH})
     *
     * @return header length
     */
    public int getLength() {
        return this.length;
    }

    /**
     * Protocol Version (actually only {@link #KNXNET_PROTOCOL_VERSION} is supported)
     *
     * @return the protocol version
     */
    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    /**
     * Total length of whole data structure
     *
     * @return the total length, including header length
     */
    public int getTotalLength() {
        return this.totalLength;
    }

    /**
     * Service type
     *
     * @return {@link ServiceType}
     */
    @Nonnull
    public ServiceType getServiceType() {
        return this.serviceType;
    }

    /**
     * Validates the given {@code rawData} if it qualifies to for KNX NET/IP header. In case the validation fails then a
     * {@link KnxException} will be thrown.
     *
     * @param headerRawData
     */
    @Override
    protected void validate(final @Nonnull byte[] headerRawData) {
        if (headerRawData[0] != KNXNET_HEADER_LENGTH) {
            throw new KnxNumberOutOfRangeException("headerRawData[0]", KNXNET_HEADER_LENGTH, KNXNET_HEADER_LENGTH, headerRawData[0], headerRawData);
        } else if (headerRawData[1] != KNXNET_PROTOCOL_VERSION) {
            throw new KnxNumberOutOfRangeException("headerRawData[1]", KNXNET_PROTOCOL_VERSION, KNXNET_PROTOCOL_VERSION, headerRawData[1],
                    headerRawData);
        }
    }

    @Nonnull
    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("length", this.length + " (" + ByteFormatter.formatHex(this.length) + ")")
                .add("protocolVersion", this.protocolVersion + " (" + ByteFormatter.formatHex(this.protocolVersion) + ")")
                .add("serviceType", this.serviceType)
                .add("totalLength", this.totalLength + " (" + ByteFormatter.formatHex(this.totalLength) + ")");
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
