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

import li.pitschmann.knx.core.ChannelIdAware;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Body for Disconnect Request
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Communication Channel ID      | reserved                      |
 * | (1 octet)                     | (1 octet)                     |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | HPAI                                                          |
 * | Control endpoint (8 octets)                                   |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * Source: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class DisconnectRequestBody implements RequestBody, ChannelIdAware, ControlChannelRelated {
    /**
     * Structure Length for {@link DisconnectRequestBody}
     * <p>
     * 2 bytes for this body<br>
     * 8 bytes for HPAI<br>
     */
    private static final int STRUCTURE_LENGTH = 10;
    private final int channelId;
    private final HPAI controlEndpoint;

    private DisconnectRequestBody(final byte[] bytes) {
        this(
                // bytes[0] => channel id
                Byte.toUnsignedInt(bytes[0]),
                // bytes[1] (not-used, reserved)
                // bytes[2..] => control endpoint
                HPAI.of(Arrays.copyOfRange(bytes, 2, bytes.length))
        );
    }

    private DisconnectRequestBody(final int channelId, final HPAI controlEndpoint) {
        Preconditions.checkArgument(channelId >= 0x00 && channelId <= 0xFF,
                "Incompatible channel id. Expected [0..255] but was: {}", channelId);
        Preconditions.checkNonNull(controlEndpoint, "Control Endpoint is required.");

        this.channelId = channelId;
        this.controlEndpoint = controlEndpoint;
    }

    /**
     * Builds a new {@link DisconnectRequestBody} instance
     *
     * @param bytes complete byte array for {@link DisconnectRequestBody}
     * @return a new immutable {@link DisconnectRequestBody}
     */
    public static DisconnectRequestBody of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new DisconnectRequestBody(bytes);
    }

    /**
     * Creates a new {@link DisconnectRequestBody} instance
     *
     * @param channelId       channel id (0..255)
     * @param controlEndpoint {@link HPAI} of control endpoint
     * @return a new immutable {@link DisconnectRequestBody}
     */
    public static DisconnectRequestBody of(final int channelId, final HPAI controlEndpoint) {
        return new DisconnectRequestBody(channelId, controlEndpoint);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.DISCONNECT_REQUEST;
    }

    @Override
    public int getChannelId() {
        return this.channelId;
    }

    public HPAI getControlEndpoint() {
        return this.controlEndpoint;
    }

    @Override
    public byte[] getRawData() {
        return toByteArray();
    }

    public byte[] toByteArray() {
        final var hpaiAsBytes = controlEndpoint.getRawData();

        final var bytes = new byte[2 + hpaiAsBytes.length];
        bytes[0] = (byte) channelId;
        bytes[1] = 0x00; // reserved
        System.arraycopy(hpaiAsBytes, 0, bytes, 2, hpaiAsBytes.length);

        return bytes;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("channelId", this.channelId)
                .add("controlEndpoint", this.controlEndpoint)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof DisconnectRequestBody) {
            final var other = (DisconnectRequestBody) obj;
            return Objects.equals(this.channelId, other.channelId)
                    && Objects.equals(this.controlEndpoint, other.controlEndpoint);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, controlEndpoint);
    }
}
