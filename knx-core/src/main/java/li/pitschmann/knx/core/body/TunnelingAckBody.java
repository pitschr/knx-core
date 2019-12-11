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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.AbstractMultiRawData;
import li.pitschmann.knx.core.ChannelIdAware;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Strings;

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
 *
 * @author PITSCHR
 */
public final class TunnelingAckBody extends AbstractMultiRawData implements ResponseBody, ChannelIdAware, DataChannelRelated {
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
        super(bytes);

        this.length = Bytes.toUnsignedInt(bytes[0]);
        this.channelId = Bytes.toUnsignedInt(bytes[1]);
        this.sequence = Bytes.toUnsignedInt(bytes[2]);
        this.status = Status.valueOf(Bytes.toUnsignedInt(bytes[3]));
    }

    /**
     * Builds a new {@link TunnelingAckBody} instance
     *
     * @param bytes complete byte array for {@link TunnelingAckBody}
     * @return a new immutable {@link TunnelingAckBody}
     */
    public static TunnelingAckBody of(final byte[] bytes) {
        return new TunnelingAckBody(bytes);
    }

    /**
     * Creates a new {@link TunnelingAckBody} instance
     *
     * @param channelId
     * @param sequence
     * @param status
     * @return a new immutable {@link TunnelingAckBody}
     */
    public static TunnelingAckBody of(final int channelId, final int sequence, final Status status) {
        // validate
        if (status == null) {
            throw new KnxNullPointerException("status");
        } else if (channelId < 0 || channelId > 0xFF) {
            throw new KnxNumberOutOfRangeException("channelId", 0, 0xFF, channelId);
        } else if (sequence < 0 || sequence > 0xFF) {
            throw new KnxNumberOutOfRangeException("sequence", 0, 0xFF, sequence);
        }

        // create bytes
        final var bytes = new byte[STRUCTURE_LENGTH];
        bytes[0] = STRUCTURE_LENGTH;
        bytes[1] = (byte) channelId;
        bytes[2] = (byte) sequence;
        bytes[3] = status.getCodeAsByte();

        return of(bytes);
    }

    @Override
    protected void validate(final byte[] rawData) {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        } else if (rawData.length != STRUCTURE_LENGTH) {
            throw new KnxNumberOutOfRangeException("rawData", STRUCTURE_LENGTH, STRUCTURE_LENGTH, rawData.length, rawData);
        }
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.TUNNELING_ACK;
    }

    public int getLength() {
        return this.length;
    }

    @Override
    public int getChannelId() {
        return this.channelId;
    }

    public int getSequence() {
        return this.sequence;
    }

    public Status getStatus() {
        return this.status;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("length", this.length + " (" + ByteFormatter.formatHex(this.length) + ")")
                .add("channelId", this.channelId + " (" + ByteFormatter.formatHex(this.channelId) + ")")
                .add("sequence", this.sequence + " (" + ByteFormatter.formatHex(this.sequence) + ")")
                .add("status", this.status);
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
