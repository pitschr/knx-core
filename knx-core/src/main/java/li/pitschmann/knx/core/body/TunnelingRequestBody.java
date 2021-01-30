/*
 * KNX Link - A library for KNX Net/IP communication
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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.CEMIAware;
import li.pitschmann.knx.core.ChannelIdAware;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.cemi.CEMI;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Body for Tunneling Request
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length              | Communication Channel ID      |
 * | (1 octet)                     | (1 octet)                     |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | Sequence Counter              | reserved                      |
 * | (1 octet)                     | (1 octet)                     |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * |                                                               |
 * |                           cEMI frame                          |
 * |                                                               |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * Source: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class TunnelingRequestBody implements RequestBody, ChannelIdAware, CEMIAware, DataChannelRelated {
    /**
     * Structure Length for {@link TunnelingRequestBody} without {@link CEMI}
     * <p>
     * 1 byte for length<br>
     * 1 byte for channel id<br>
     * 1 byte for sequence<br>
     * 1 byte is not-used / reserved<br>
     */
    private static final int STRUCTURE_LENGTH = 4;

    /**
     * Minimum Structure Length for {@link TunnelingRequestBody} including {@link CEMI}
     * <p>
     * 4 bytes for {@link TunnelingRequestBody} ( {@link #STRUCTURE_LENGTH} )<br>
     * 11 bytes minimum for {@link CEMI}<br>
     */
    private static final int STRUCTURE_WITH_CEMI_MIN_LENGTH = STRUCTURE_LENGTH + 11;

    /**
     * Maximum Structure Length for {@link TunnelingRequestBody} including {@link CEMI}
     */
    private static final int STRUCTURE_WITH_CEMI_MAX_LENGTH = 0xFF;
    private final int length;
    private final int channelId;
    private final int sequence;
    private final CEMI cemi;

    private TunnelingRequestBody(final byte[] bytes) {
        this(
                // byte[0] => length
                Byte.toUnsignedInt(bytes[0]),
                // byte[1] => channel id
                Byte.toUnsignedInt(bytes[1]),
                // byte[2] => sequence
                Byte.toUnsignedInt(bytes[2]),
                // byte[3] (not used, reserved)
                // byte[4..] => cemi
                CEMI.of(Arrays.copyOfRange(bytes, 4, bytes.length))
        );
    }

    private TunnelingRequestBody(final int length, final int channelId, final int sequence, final CEMI cemi) {
        Preconditions.checkArgument(length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, length);
        Preconditions.checkArgument(channelId >= 0x00 && channelId <= 0xFF,
                "Incompatible channel id. Expected [0..255] but was: {}", channelId);
        Preconditions.checkArgument(sequence >= 0x00 && sequence <= 0xFF,
                "Incompatible sequence. Expected [0..255] but was: {}", sequence);
        Preconditions.checkNonNull(cemi, "CEMI is required.");

        this.length = length;
        this.channelId = channelId;
        this.sequence = sequence;
        this.cemi = cemi;
    }

    /**
     * Builds a new {@link TunnelingRequestBody} instance
     *
     * @param bytes complete byte array for {@link TunnelingRequestBody}
     * @return a new immutable {@link TunnelingRequestBody}
     */
    public static TunnelingRequestBody of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length >= STRUCTURE_WITH_CEMI_MIN_LENGTH && bytes.length <= STRUCTURE_WITH_CEMI_MAX_LENGTH,
                "Incompatible structure length. Expected [{}..{}] but was: {}", STRUCTURE_WITH_CEMI_MIN_LENGTH, STRUCTURE_WITH_CEMI_MAX_LENGTH, bytes.length);
        return new TunnelingRequestBody(bytes);
    }

    /**
     * Creates a new {@link TunnelingRequestBody} instance
     *
     * @param channelId channel id (0..255)
     * @param sequence  tunneling packet sequence number
     * @param cemi      the {@link CEMI} to be sent
     * @return a new immutable {@link TunnelingRequestBody}
     */
    public static TunnelingRequestBody of(final int channelId, final int sequence, final CEMI cemi) {
        return new TunnelingRequestBody(STRUCTURE_LENGTH, channelId, sequence, cemi);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.TUNNELING_REQUEST;
    }

    public int getLength() {
        return length;
    }

    @Override
    public int getChannelId() {
        return channelId;
    }

    public int getSequence() {
        return sequence;
    }

    @Override
    public CEMI getCEMI() {
        return cemi;
    }

    @Override
    public byte[] getRawData() {
        return toByteArray();
    }

    public byte[] toByteArray() {
        final var cemiAsBytes = cemi.getRawData();

        // create bytes
        final var bytes = new byte[STRUCTURE_LENGTH + cemiAsBytes.length];
        bytes[0] = STRUCTURE_LENGTH;
        bytes[1] = (byte) channelId;
        bytes[2] = (byte) sequence;
        bytes[3] = 0x00; // reserved
        System.arraycopy(cemiAsBytes, 0, bytes, STRUCTURE_LENGTH, cemiAsBytes.length);

        return bytes;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("length", length)
                .add("channelId", channelId)
                .add("sequence", sequence)
                .add("cemi", cemi)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof TunnelingRequestBody) {
            final var other = (TunnelingRequestBody) obj;
            return Objects.equals(this.length, other.length)
                    && Objects.equals(this.channelId, other.channelId)
                    && Objects.equals(this.sequence, other.sequence)
                    && Objects.equals(this.cemi, other.cemi);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, channelId, sequence, cemi);
    }
}
