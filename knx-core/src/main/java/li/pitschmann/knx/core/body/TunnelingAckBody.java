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

import li.pitschmann.knx.core.ChannelIdAware;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Body for Tunneling Acknowledge / Response
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length              | Communication Channel ID      |
 * | (1 octet = 04h)               | (1 octet)                     |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | Sequence Counter              | Status                        |
 * | (1 octet)                     | (1 octet)                     |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * Source: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class TunnelingAckBody implements ResponseBody, ChannelIdAware, DataChannelRelated {
    /**
     * Structure Length for {@link TunnelingAckBody}
     * <p>
     * 1 byte for length<br>
     * 1 byte for channel id<br>
     * 1 byte for sequence<br>
     * 1 byte for status<br>
     */
    private static final int STRUCTURE_LENGTH = 4;
    private final int length;
    private final int channelId;
    private final int sequence;
    private final Status status;

    private TunnelingAckBody(final byte[] bytes) {
        this(
                // byte[0] => length
                Byte.toUnsignedInt(bytes[0]),
                // byte[1] => channel id
                Byte.toUnsignedInt(bytes[1]),
                // byte[2] => sequence
                Byte.toUnsignedInt(bytes[2]),
                // byte[3] => status
                Status.valueOf(Byte.toUnsignedInt(bytes[3]))
        );
    }

    private TunnelingAckBody(final int length, final int channelId, final int sequence, final Status status) {
        Preconditions.checkArgument(length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, length);
        Preconditions.checkArgument(channelId >= 0x00 && channelId <= 0xFF,
                "Incompatible channel id. Expected [0..255] but was: {}", channelId);
        Preconditions.checkArgument(sequence >= 0x00 && sequence <= 0xFF,
                "Incompatible sequence. Expected [0..255] but was: {}", sequence);
        Preconditions.checkNonNull(status, "Status is required.");

        this.length = length;
        this.channelId = channelId;
        this.sequence = sequence;
        this.status = status;
    }

    /**
     * Builds a new {@link TunnelingAckBody} instance
     *
     * @param bytes complete byte array for {@link TunnelingAckBody}
     * @return a new immutable {@link TunnelingAckBody}
     */
    public static TunnelingAckBody of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new TunnelingAckBody(bytes);
    }

    /**
     * Creates a new {@link TunnelingAckBody} instance
     *
     * @param channelId channel id (0..255)
     * @param sequence  tunneling packet sequence number
     * @param status    status of tunneling acknowledge
     * @return a new immutable {@link TunnelingAckBody}
     */
    public static TunnelingAckBody of(final int channelId, final int sequence, final Status status) {
        return new TunnelingAckBody(STRUCTURE_LENGTH, channelId, sequence, status);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.TUNNELING_ACK;
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

    public Status getStatus() {
        return status;
    }

    @Override
    public byte[] getRawData() {
        return toByteArray();
    }

    public byte[] toByteArray() {
        final var bytes = new byte[STRUCTURE_LENGTH];
        bytes[0] = STRUCTURE_LENGTH;
        bytes[1] = (byte) channelId;
        bytes[2] = (byte) sequence;
        bytes[3] = status.getCodeAsByte();
        return bytes;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("length", length)
                .add("channelId", channelId)
                .add("sequence", sequence)
                .add("status", status)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof TunnelingAckBody) {
            final var other = (TunnelingAckBody) obj;
            return Objects.equals(this.length, other.length)
                    && Objects.equals(this.channelId, other.channelId)
                    && Objects.equals(this.sequence, other.sequence)
                    && Objects.equals(this.status, other.status);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, channelId, sequence, status);
    }
}
