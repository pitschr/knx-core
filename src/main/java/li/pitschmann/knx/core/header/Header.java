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

package li.pitschmann.knx.core.header;

import li.pitschmann.knx.core.MultiRawDataAware;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * This class represents the header of KNX/IP and is immutable.
 * <p>
 * Every KNXnet/IP frame, without any exception, shall consist of at
 * least the common KNXnet/IP header that shall contain information
 * about the protocol version, the header and total packet length
 * and the KNXnet/IP service type identifier.
 * <p>
 * The KNXnet/IP header may be followed by a KNXnet/IP body, depending
 * on the KNXnet/IP service. Although the length of the header is always
 * fixed, it is possible that the size of the header changes with a
 * new version of the protocol.
 * <p>
 * The protocol version information states the revision of the KNXnet/IP
 * protocol that the following KNXnet/IP frame is subject to. The only
 * valid protocol version at this time is 1.0.
 * <p>
 * The KNXnet/IP service type identifier defines the kind of action to be
 * performed and the type of the data payload contained in the KNXnet/IP
 * body if applicable.
 * <p>
 * The total length shall express the total KNXnet/IP frame length in
 * octets. The length shall include the complete KNXnet/IP frame, starting
 * with the header length of the KNXnet/IP header and including the whole
 * KNXnet/IP body.
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
 * See: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class Header implements MultiRawDataAware {
    /**
     * Constant size of KNX/IP header as defined in protocol version 1.0
     */
    public static final int STRUCTURE_LENGTH = 0x06;
    /**
     * Identifier for KNX/IP protocol version 1.0
     */
    public static final int PROTOCOL_VERSION_V1 = 0x10;
    /**
     * Minimum Total Length (Header Length + Body Length)
     */
    private static final int TOTAL_LENGTH_MIN = STRUCTURE_LENGTH;
    /**
     * Maximum Total Length (Header Length + Body Length)
     */
    private static final int TOTAL_LENGTH_MAX = 0xFFFF;
    private final int length;
    private final int protocolVersion;
    private final ServiceType serviceType;
    private final int totalLength;

    private Header(final byte[] bytes) {
        this(
                // bytes[0] => Header Length
                Byte.toUnsignedInt(bytes[0]),
                // bytes[1] => Protocol Version
                Byte.toUnsignedInt(bytes[1]),
                // bytes[2,3] => Service Type
                ServiceType.valueOf(Bytes.toUnsignedInt(bytes[2], bytes[3])),
                // bytes[4,5] => Total Length (Header Length + Body Length)
                Bytes.toUnsignedInt(bytes[4], bytes[5])
        );
    }

    private Header(final int length,
                   final int protocolVersion,
                   final ServiceType serviceType,
                   final int totalLength) {
        Preconditions.checkArgument(length == STRUCTURE_LENGTH,
                "Incompatible header length. Expected [{}] but was: {}", STRUCTURE_LENGTH, length);
        Preconditions.checkArgument(protocolVersion == PROTOCOL_VERSION_V1,
                "Incompatible protocol version. Expected [{}] but was: {}", PROTOCOL_VERSION_V1, protocolVersion);
        Preconditions.checkNonNull(serviceType, "Service Type is required.");
        Preconditions.checkArgument(totalLength >= TOTAL_LENGTH_MIN && totalLength <= TOTAL_LENGTH_MAX,
                "Incompatible total length. Expected [{}..{}] but was: {}", TOTAL_LENGTH_MIN, TOTAL_LENGTH_MAX, totalLength);

        this.length = length;
        this.protocolVersion = protocolVersion;
        this.serviceType = serviceType;
        this.totalLength = totalLength;
    }

    /**
     * Creates a new {@link Header} instance
     *
     * @param bytes the given parameter can be either bytes for header only, but also the complete byte array stream. In
     *              case of complete byte array stream the header will be cut at given {@link #STRUCTURE_LENGTH}
     * @return a new immutable {@link Header}
     */
    public static Header of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length >= STRUCTURE_LENGTH,
                "Incompatible header structure length. Expected [{}..]' but was: {}", STRUCTURE_LENGTH, bytes.length);

        if (bytes.length == STRUCTURE_LENGTH) {
            return new Header(bytes);
        } else {
            return new Header(Arrays.copyOf(bytes, STRUCTURE_LENGTH));
        }
    }

    /**
     * Creates a new {@link Header} instance based on {@link Body} structure.
     *
     * @param body body of header
     * @return a new immutable {@link Header} calculated based on {@link Body}
     */
    public static Header of(final Body body) {
        Preconditions.checkNonNull(body, "Body is required.");
        return new Header(STRUCTURE_LENGTH, PROTOCOL_VERSION_V1, body.getServiceType(), Header.STRUCTURE_LENGTH + body.toByteArray().length);
    }

    /**
     * Header length (actually header length is always {@link #STRUCTURE_LENGTH})
     *
     * @return header length
     */
    public int getLength() {
        return length;
    }

    /**
     * Protocol Version (actually only {@link #PROTOCOL_VERSION_V1} is supported)
     *
     * @return the protocol version
     */
    public int getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Total length of whole data structure
     *
     * @return the total length, including header length
     */
    public int getTotalLength() {
        return totalLength;
    }

    /**
     * Service type
     *
     * @return {@link ServiceType}
     */
    public ServiceType getServiceType() {
        return serviceType;
    }

    @Override
    public byte[] toByteArray() {
        final var serviceTypeAsBytes = serviceType.getCodeAsBytes();
        final var totalLengthAsBytes = new byte[]{(byte) (totalLength >>> 8), (byte) totalLength};

        return new byte[]{
                Header.STRUCTURE_LENGTH,
                Header.PROTOCOL_VERSION_V1,
                serviceTypeAsBytes[0], serviceTypeAsBytes[1],
                totalLengthAsBytes[0], totalLengthAsBytes[1]
        };
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("length", length)
                .add("protocolVersion", protocolVersion)
                .add("serviceType", serviceType.name())
                .add("totalLength", totalLength)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Header) {
            final var other = (Header) obj;
            return this.length == other.length
                    && this.protocolVersion == other.protocolVersion
                    && Objects.equals(this.serviceType, other.serviceType)
                    && this.totalLength == other.totalLength;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, protocolVersion, serviceType, totalLength);
    }
}
