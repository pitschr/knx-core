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

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.AbstractMultiRawData;
import li.pitschmann.knx.link.ChannelIdAware;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;

import javax.annotation.Nonnull;

/**
 * Body for Connection State Response
 * <p>
 * The {@link ServiceType#CONNECTION_STATE_RESPONSE} frame shall be sent by the KNX Net/IP device as an answer to a
 * received {@link ServiceType#CONNECTION_STATE_REQUEST} frame.
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
public final class ConnectionStateResponseBody extends AbstractMultiRawData implements ResponseBody, ChannelIdAware, ControlChannelRelated {
    /**
     * Structure Length for {@link ConnectionStateResponseBody}
     * <p>
     * 1 byte for channel id<br>
     * 1 byte for status<br>
     */
    private static final int STRUCTURE_LENGTH = 2;
    private final int channelId;
    private final Status status;

    private ConnectionStateResponseBody(final byte[] bytes) {
        super(bytes);

        this.channelId = Bytes.toUnsignedInt(bytes[0]);
        this.status = Status.valueOf(Bytes.toUnsignedInt(bytes[1]));
    }

    /**
     * Builds a new {@link ConnectionStateResponseBody} instance
     *
     * @param bytes complete byte array for {@link ConnectionStateResponseBody}
     * @return immutable {@link ConnectionStateResponseBody}
     */
    public static ConnectionStateResponseBody valueOf(final byte[] bytes) {
        return new ConnectionStateResponseBody(bytes);
    }

    /**
     * Creates a new {@link ConnectionStateResponseBody} instance
     *
     * @param channelId
     * @param status
     * @return immutable {@link ConnectionStateResponseBody}
     */
    public static ConnectionStateResponseBody create(final int channelId, final Status status) {
        // validate
        if (status == null) {
            throw new KnxNullPointerException("status");
        } else if (channelId < 0 || channelId > 0xFF) {
            throw new KnxNumberOutOfRangeException("channelId", 0, 0xFF, channelId);
        }

        // create bytes
        final var bytes = new byte[2];
        bytes[0] = (byte) channelId;
        bytes[1] = status.getCodeAsByte();

        return valueOf(bytes);
    }

    @Override
    protected void validate(final byte[] rawData) {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        } else if (rawData.length != STRUCTURE_LENGTH) {
            // 1 byte for channel id
            // 1 byte for status
            throw new KnxNumberOutOfRangeException("rawData", STRUCTURE_LENGTH, STRUCTURE_LENGTH, rawData.length, rawData);
        }
    }

    @Override
    @Nonnull
    public ServiceType getServiceType() {
        return ServiceType.CONNECTION_STATE_RESPONSE;
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