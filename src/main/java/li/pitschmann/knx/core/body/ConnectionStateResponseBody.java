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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.ChannelIdAware;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Body for Connection State Response
 * <p>
 * The {@link ServiceType#CONNECTION_STATE_RESPONSE} frame shall be
 * sent by the KNX Net/IP Server as an answer to a received
 * {@link ServiceType#CONNECTION_STATE_REQUEST} frame. It shall be
 * addressed to the KNX Net/IP Client’s control endpoint using the
 * HPAI included in the received {@link ServiceType#CONNECTION_STATE_REQUEST}
 * frame.
 * <p>
 * The first octet of the KNX Net/IP body shall contain the communication
 * channel ID that the KNX Net/IP Client has passed to the KNX Net/IP Server
 * with the {@link ServiceType#CONNECTION_STATE_REQUEST} frame.
 * <p>
 * The second octet of the KNX Net/IP body shall contain the status
 * information of the connection state request.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Communication Channel ID      | Status                        |
 * | (1 octet)                     | (1 octet)                     |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * Source: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class ConnectionStateResponseBody implements ResponseBody, ChannelIdAware, ControlChannelRelated {
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
        this(
                // bytes[0] => channelId
                Byte.toUnsignedInt(bytes[0]),
                // bytes[1] => status
                Status.valueOf(Byte.toUnsignedInt(bytes[1]))
        );
    }

    private ConnectionStateResponseBody(final int channelId, final Status status) {
        Preconditions.checkArgument(channelId >= 0x00 && channelId <= 0xFF,
                "Incompatible channel id. Expected [0..255] but was: {}", channelId);
        Preconditions.checkNonNull(status, "Status is required.");

        this.channelId = channelId;
        this.status = status;
    }

    /**
     * Builds a new {@link ConnectionStateResponseBody} instance
     *
     * @param bytes complete byte array for {@link ConnectionStateResponseBody}
     * @return a new immutable {@link ConnectionStateResponseBody}
     */
    public static ConnectionStateResponseBody of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new ConnectionStateResponseBody(bytes);
    }

    /**
     * Creates a new {@link ConnectionStateResponseBody} instance
     *
     * @param channelId channel id (0..255)
     * @param status    status of connection state response
     * @return a  new immutable {@link ConnectionStateResponseBody}
     */
    public static ConnectionStateResponseBody of(final int channelId, final Status status) {
        return new ConnectionStateResponseBody(channelId, status);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.CONNECTION_STATE_RESPONSE;
    }

    @Override
    public int getChannelId() {
        return channelId;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public byte[] toByteArray() {
        // create bytes
        final var bytes = new byte[2];
        bytes[0] = (byte) channelId;
        bytes[1] = status.getCodeAsByte();
        return bytes;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("channelId", channelId)
                .add("status", status)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ConnectionStateResponseBody) {
            final var other = (ConnectionStateResponseBody) obj;
            return Objects.equals(this.channelId, other.channelId)
                    && Objects.equals(this.status, other.status);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, status);
    }
}
