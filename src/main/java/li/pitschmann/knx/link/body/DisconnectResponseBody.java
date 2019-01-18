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
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.knx.link.header.*;
import li.pitschmann.utils.*;

import javax.annotation.*;

/**
 * Body for Disconnect Response
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Communication Channel ID      | Status                        |
 * | (1 octet)                     | (1 octet)                     |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 *
 * @author PITSCHR
 */
public final class DisconnectResponseBody extends AbstractMultiRawData implements ResponseBody, ChannelIdAware, ControlChannelRelated {
    /**
     * Structure Length for {@link DisconnectResponseBody}
     * <p>
     * 1 byte for channel id<br>
     * 1 byte for status<br>
     */
    private static final int STRUCTURE_LENGTH = 2;
    private final int channelId;
    private final Status status;

    private DisconnectResponseBody(final byte[] bytes) {
        super(bytes);

        this.channelId = Bytes.toUnsignedInt(bytes[0]);
        this.status = Status.valueOf(Bytes.toUnsignedInt(bytes[1]));
    }

    /**
     * Builds a new {@link DisconnectResponseBody} instance
     *
     * @param bytes complete byte array for {@link DisconnectResponseBody}
     * @return immutable {@link DisconnectResponseBody}
     */
    public static DisconnectResponseBody valueOf(final byte[] bytes) {
        return new DisconnectResponseBody(bytes);
    }

    /**
     * Creates a new {@link DisconnectResponseBody} instance
     *
     * @param channelId
     * @param status
     * @return immutable {@link DisconnectResponseBody}
     */
    public static DisconnectResponseBody create(final int channelId, final Status status) {
        // validate
        if (status == null) {
            throw new KnxNullPointerException("status");
        } else if (channelId < 0 || channelId > 0xFF) {
            throw new KnxNumberOutOfRangeException("channelId", 0, 0xFF, channelId);
        }

        // create bytes
        final byte[] bytes = new byte[2];
        bytes[0] = (byte) channelId;
        bytes[1] = status.getCodeAsByte();

        return valueOf(bytes);
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
    @Nonnull
    public ServiceType getServiceType() {
        return ServiceType.DISCONNECT_RESPONSE;
    }

    @Override
    public int getChannelId() {
        return this.channelId;
    }

    public Status getStatus() {
        return this.status;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("channelId", this.channelId + " (" + ByteFormatter.formatHex(this.channelId) + ")")
                .add("status", this.status);
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
