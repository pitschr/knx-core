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
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Body for Connection State Request
 * <p>
 * The {@link ServiceType#CONNECTION_STATE_REQUEST} frame shall be
 * sent by the  KNXnet/IP Client to the control endpoint of the KNX Net/IP
 * Server. The first octet of the KNXnet/IP body shall contain the
 * communication channel ID that the KNXnet/IP Server uses to uniquely
 * identify the data connection for this connection state request.
 * The second octet shall be reserved for future use.
 * <p>
 * The HPAI with the return address information of the KNXnet/IP Client's
 * control endpoint shall be added after the communication channel ID.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Communication Channel ID      | reserved                      |
 * | (1 octet)                     | (1 octet)                     |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | HPAI                                                          |
 * | Control endpoint                                              |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * Source: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class ConnectionStateRequestBody implements RequestBody, ChannelIdAware, ControlChannelRelated {
    /**
     * Structure Length for {@link ConnectionStateRequestBody}
     * <p>
     * 1 byte for channel id<br>
     * 1 byte not-used / reserved<br>
     * 8 bytes for HPAI<br>
     */
    private static final int STRUCTURE_LENGTH = 10;
    private final int channelId;
    private final HPAI controlEndpoint;

    private ConnectionStateRequestBody(final byte[] bytes) {
        this(
                // bytes[0] => channelId
                Byte.toUnsignedInt(bytes[0]),
                // bytes[1] (not-used, reserved)
                // bytes[2..10] => HPAI
                HPAI.of(Arrays.copyOfRange(bytes, 2, 10))
        );
    }

    private ConnectionStateRequestBody(final int channelId, final HPAI controlEndpoint) {
        Preconditions.checkArgument(channelId >= 0x00 && channelId <= 0xFF,
                "Incompatible channel id. Expected [0..255] but was: {}", channelId);
        Preconditions.checkNonNull(controlEndpoint, "Control Endpoint is required.");

        this.channelId = channelId;
        this.controlEndpoint = controlEndpoint;
    }

    /**
     * Builds a new {@link ConnectionStateRequestBody} instance
     *
     * @param bytes complete byte array for {@link ConnectionStateRequestBody}
     * @return a new immutable {@link ConnectionStateRequestBody}
     */
    public static ConnectionStateRequestBody of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new ConnectionStateRequestBody(bytes);
    }

    /**
     * Creates a new {@link ConnectionStateRequestBody} instance
     *
     * @param channelId       channel id between 0..255
     * @param controlEndpoint {@link HPAI} of control endpoint
     * @return a new immutable {@link ConnectionStateRequestBody}
     */
    public static ConnectionStateRequestBody of(final int channelId, final HPAI controlEndpoint) {
        return new ConnectionStateRequestBody(channelId, controlEndpoint);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.CONNECTION_STATE_REQUEST;
    }

    @Override
    public int getChannelId() {
        return channelId;
    }

    public HPAI getControlEndpoint() {
        return controlEndpoint;
    }

    @Override
    public byte[] getRawData() {
        return toByteArray();
    }

    public byte[] toByteArray() {
        final var hpaiAsBytes = controlEndpoint.getRawData();

        // create bytes
        final var bytes = new byte[2 + hpaiAsBytes.length];
        bytes[0] = (byte) channelId;
        bytes[1] = 0x00; // reserved
        System.arraycopy(hpaiAsBytes, 0, bytes, 2, hpaiAsBytes.length);

        return bytes;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("channelId", channelId)
                .add("controlEndpoint", controlEndpoint)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ConnectionStateRequestBody) {
            final var other = (ConnectionStateRequestBody) obj;
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
