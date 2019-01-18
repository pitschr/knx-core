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

package li.pitschmann.knx.link.body;

import com.google.common.base.*;
import li.pitschmann.knx.link.*;
import li.pitschmann.knx.link.body.cemi.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.knx.link.header.*;
import li.pitschmann.utils.*;

import javax.annotation.*;
import java.util.*;

/**
 * Body for Tunnelling Request
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
 *
 * @author PITSCHR
 */
public final class TunnellingRequestBody extends AbstractMultiRawData implements RequestBody, ChannelIdAware, DataChannelRelated {
    /**
     * Structure Length for {@link TunnellingRequestBody} without {@link CEMI}
     * <p>
     * 1 byte for length<br>
     * 1 byte for channel id<br>
     * 1 byte for sequence<br>
     * 1 byte for status<br>
     */
    private static final int STRUCTURE_LENGTH = 4;
    /**
     * Minimum Structure Length for {@link TunnellingRequestBody} including {@link CEMI}
     * <p>
     * 4 bytes for {@link TunnellingRequestBody} ( {@link #STRUCTURE_LENGTH} )<br>
     * 11 bytes minimum for {@link CEMI}<br>
     */
    private static final int STRUCTURE_WITH_CEMI_MIN_LENGTH = STRUCTURE_LENGTH + 11;
    /**
     * Maximum Structure Length for {@link TunnellingRequestBody} including {@link CEMI}
     */
    private static final int STRUCTURE_WITH_CEMI_MAX_LENGTH = 0xFF;
    private final int length;
    private final int channelId;
    private final int sequence;
    private final CEMI cemi;

    private TunnellingRequestBody(final byte[] bytes) {
        super(bytes);

        this.length = Bytes.toUnsignedInt(bytes[0]);
        this.channelId = Bytes.toUnsignedInt(bytes[1]);
        this.sequence = Bytes.toUnsignedInt(bytes[2]);
        // [3] -> reserved
        this.cemi = CEMI.valueOf(Arrays.copyOfRange(bytes, 4, bytes.length));
    }

    /**
     * Builds a new {@link TunnellingRequestBody} instance
     *
     * @param bytes complete byte array for {@link TunnellingRequestBody}
     * @return immutable {@link TunnellingRequestBody}
     */
    public static TunnellingRequestBody valueOf(final byte[] bytes) {
        return new TunnellingRequestBody(bytes);
    }

    /**
     * Creates a new {@link TunnellingRequestBody} instance
     *
     * @param channelId
     * @param sequence
     * @param cemi
     * @return immutable {@link TunnellingRequestBody}
     */
    public static TunnellingRequestBody create(final int channelId, final int sequence, final CEMI cemi) {
        // validate
        if (cemi == null) {
            throw new KnxNullPointerException("cemi");
        } else if (channelId < 0 || channelId > 0xFF) {
            throw new KnxNumberOutOfRangeException("channelId", 0, 0xFF, channelId);
        } else if (sequence < 0 || sequence > 0xFF) {
            throw new KnxNumberOutOfRangeException("sequence", 0, 0xFF, sequence);
        }

        final byte[] cemiAsBytes = cemi.getRawData();

        // create bytes
        final byte[] bytes = new byte[STRUCTURE_LENGTH + cemiAsBytes.length];
        bytes[0] = STRUCTURE_LENGTH;
        bytes[1] = (byte) channelId;
        bytes[2] = (byte) sequence;
        bytes[3] = 0x00; // reserved
        System.arraycopy(cemiAsBytes, 0, bytes, STRUCTURE_LENGTH, cemiAsBytes.length);

        return valueOf(bytes);
    }

    @Override
    protected void validate(final byte[] rawData) {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        } else if (rawData.length < STRUCTURE_WITH_CEMI_MIN_LENGTH || rawData.length > STRUCTURE_WITH_CEMI_MAX_LENGTH) {
            throw new KnxNumberOutOfRangeException("rawData", STRUCTURE_WITH_CEMI_MIN_LENGTH, STRUCTURE_WITH_CEMI_MAX_LENGTH, rawData.length,
                    rawData);
        } else if (Bytes.toUnsignedInt(rawData[0]) != STRUCTURE_LENGTH) {
            throw new KnxNumberOutOfRangeException("rawData[0]", STRUCTURE_LENGTH, STRUCTURE_LENGTH, rawData[0], rawData);
        }
    }

    @Override
    @Nonnull
    public ServiceType getServiceType() {
        return ServiceType.TUNNELING_REQUEST;
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

    public CEMI getCEMI() {
        return this.cemi;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("length", this.length + " (" + ByteFormatter.formatHex(this.length) + ")")
                .add("channelId", this.channelId + " (" + ByteFormatter.formatHex(this.channelId) + ")")
                .add("sequence", this.sequence + " (" + ByteFormatter.formatHex(this.sequence) + ")")
                .add("cemi", this.cemi.toString(false));
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}