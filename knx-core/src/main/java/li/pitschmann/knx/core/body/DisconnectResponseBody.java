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
 * Body for Disconnect Response
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
public final class DisconnectResponseBody implements ResponseBody, ChannelIdAware, ControlChannelRelated {
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
        this(
                // byte[0] => channel id
                Byte.toUnsignedInt(bytes[0]),
                // byte[1] => status
                Status.valueOf(Byte.toUnsignedInt(bytes[1]))
        );
    }

    private DisconnectResponseBody(final int channelId, final Status status) {
        Preconditions.checkArgument(channelId >= 0x00 && channelId <= 0xFF,
                "Incompatible channel id. Expected [0..255] but was: {}", channelId);
        Preconditions.checkNonNull(status, "Status is required.");

        this.channelId = channelId;
        this.status = status;
    }

    /**
     * Builds a new {@link DisconnectResponseBody} instance
     *
     * @param bytes complete byte array for {@link DisconnectResponseBody}
     * @return a new immutable {@link DisconnectResponseBody}
     */
    public static DisconnectResponseBody of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new DisconnectResponseBody(bytes);
    }

    /**
     * Creates a new {@link DisconnectResponseBody} instance
     *
     * @param channelId channel id (0..255)
     * @param status    status of disconnect response
     * @return a new immutable {@link DisconnectResponseBody}
     */
    public static DisconnectResponseBody of(final int channelId, final Status status) {
        return new DisconnectResponseBody(channelId, status);
    }

    @Override
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
    public byte[] getRawData() {
        return toByteArray();
    }

    public byte[] toByteArray() {
        final var bytes = new byte[2];
        bytes[0] = (byte) channelId;
        bytes[1] = status.getCodeAsByte();
        return bytes;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("channelId", this.channelId)
                .add("status", this.status)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof DisconnectResponseBody) {
            final var other = (DisconnectResponseBody) obj;
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
