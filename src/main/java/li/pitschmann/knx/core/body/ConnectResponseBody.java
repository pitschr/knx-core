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
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.tunnel.ConnectionResponseData;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Body for Connect Response
 * <p>
 * The {@link ServiceType#CONNECT_RESPONSE} frame shall be sent by
 * the KNXnet/IP Server as an answer to a received {@link ServiceType#CONNECT_REQUEST}
 * frame. It shall be addressed to the KNXnet/IP Client’s control
 * endpoint using the HPAI included in the received {@link ServiceType#CONNECT_REQUEST}
 * frame.
 * <p>
 * The size of the KNXnet/IP body varies according to the success
 * or failure of the KNXnet/IP Client’s {@link ServiceType#CONNECT_REQUEST}.
 * <p>
 * If the connection request is successfully fulfilled with all the
 * requested options, the body of the {@link ServiceType#CONNECT_REQUEST}
 * frame shall contain a communication channel ID that shall uniquely
 * identify this connection with the KNXnet/IP Server. The communication
 * channel ID shall be the first octet of the body.
 * <p>
 * The second octet of the body shall contain the status information
 * of the connection request. This status information can contain
 * error information regarding the request itself or regarding the
 * connection type specific information.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Communication Channel ID    | Status                          |
 * |                             |                                 |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | HPAI                                                          |
 * | Data endpoint                                                 |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | CRD                                                           |
 * | Connection Response Data Block                                |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * Source: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class ConnectResponseBody implements ResponseBody, ChannelIdAware, ControlChannelRelated {
    /**
     * Structure Length for {@link ConnectResponseBody} in case there was <strong>an error</strong>
     * <p>
     * 1 byte for channel id<br>
     * 1 byte for status<br>
     */
    private static final int STRUCTURE_LENGTH_WITH_ERROR = 2;

    /**
     * Structure Length for {@link ConnectResponseBody} in case there was <strong>no error</strong>
     * <p>
     * 1 byte for channel id<br>
     * 1 byte for status<br>
     * 8 bytes for data endpoint<br>
     * 4 bytes for connection response data<br>
     */
    private static final int STRUCTURE_LENGTH_NO_ERROR = 14;
    private final int channelId;
    private final Status status;
    private final HPAI dataEndpoint;
    private final ConnectionResponseData connectionResponseData;

    private ConnectResponseBody(final byte[] bytes) {
        // byte[0] => channel id
        this.channelId = Byte.toUnsignedInt(bytes[0]);
        // byte[1] => status
        this.status = Status.valueOf(Byte.toUnsignedInt(bytes[1]));

        if (bytes.length == STRUCTURE_LENGTH_NO_ERROR) {
            // byte[2..9] => data endpoint
            this.dataEndpoint = HPAI.of(Arrays.copyOfRange(bytes, 2, 10));
            // byte[10..13] => connection response data
            this.connectionResponseData = ConnectionResponseData.of(Arrays.copyOfRange(bytes, 10, 14));
        } else {
            this.dataEndpoint = null;
            this.connectionResponseData = null;
        }
    }

    private ConnectResponseBody(final int channelId,
                                final Status status,
                                final @Nullable HPAI dataEndpoint,
                                final @Nullable ConnectionResponseData connectionResponseData) {
        Preconditions.checkArgument(channelId >= 0x00 && channelId <= 0xFF,
                "Incompatible channel id. Expected [0..255] but was: {}", channelId);
        Preconditions.checkNonNull(status, "Status is required.");
        if (Status.NO_ERROR.equals(status)) {
            Preconditions.checkNonNull(dataEndpoint, "Data Endpoint is required.");
            Preconditions.checkNonNull(connectionResponseData, "Connection response data is required.");
        }

        this.channelId = channelId;
        this.status = status;
        this.dataEndpoint = dataEndpoint;
        this.connectionResponseData = connectionResponseData;
    }

    /**
     * Builds a new {@link ConnectResponseBody} instance
     *
     * @param bytes complete byte array for {@link ConnectResponseBody}
     * @return a new immutable {@link ConnectResponseBody}
     */
    public static ConnectResponseBody of(final byte[] bytes) {
        Preconditions.checkArgument(
                // OK -> there was no error (dataEndpoint and connectionResponseData will be present)
                bytes.length == STRUCTURE_LENGTH_NO_ERROR
                        // OK -> there was an error (dataEndpoint and connectionResponseData will be null)
                        || bytes.length == STRUCTURE_LENGTH_WITH_ERROR,
                "Incompatible structure length. Expected [{},{}] but was: {}", STRUCTURE_LENGTH_WITH_ERROR, STRUCTURE_LENGTH_NO_ERROR, bytes.length);

        return new ConnectResponseBody(bytes);
    }

    /**
     * Creates a new {@link ConnectResponseBody} instance
     *
     * @param channelId              channel id (0..255)
     * @param status                 status of connect response
     * @param dataEndpoint           (required only when {@link Status#NO_ERROR})
     * @param connectionResponseData (required only when {@link Status#NO_ERROR})
     * @return a new immutable {@link ConnectResponseBody}
     */
    public static ConnectResponseBody of(final int channelId,
                                         final Status status,
                                         final @Nullable HPAI dataEndpoint,
                                         final @Nullable ConnectionResponseData connectionResponseData) {
        return new ConnectResponseBody(channelId, status, dataEndpoint, connectionResponseData);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.CONNECT_RESPONSE;
    }

    /**
     * Returns the channel id
     *
     * @return channel id, in case of error the channel id will be {@code 0} (zero).
     */
    @Override
    public int getChannelId() {
        return channelId;
    }

    /**
     * Get the status of connect response
     *
     * @return status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns the data endpoint
     *
     * @return {@link HPAI}, it may be null when there was an error (see: {@link #getStatus()})
     */
    @Nullable
    public HPAI getDataEndpoint() {
        return dataEndpoint;
    }

    /**
     * Returns the connection response data
     *
     * @return {@link ConnectionResponseData}, it may be null when there was an error (see: {@link #getStatus()})
     */
    @Nullable
    public ConnectionResponseData getConnectionResponseData() {
        return connectionResponseData;
    }

    @Override
    public byte[] toByteArray() {
        // behavior depends on status
        if (status == Status.NO_ERROR) {
            // no error - provide everything
            final var dataEndpointAsBytes = dataEndpoint.toByteArray();
            final var crdAsBytes = connectionResponseData.toByteArray();

            // create bytes
            final var bytes = new byte[2 + dataEndpointAsBytes.length + crdAsBytes.length];
            bytes[0] = (byte) channelId;
            bytes[1] = status.getCodeAsByte();
            System.arraycopy(dataEndpointAsBytes, 0, bytes, 2, dataEndpointAsBytes.length);
            System.arraycopy(crdAsBytes, 0, bytes, dataEndpointAsBytes.length + 2, crdAsBytes.length);

            return bytes;
        } else {
            // error (only channel id + status)
            return new byte[]{
                    (byte) channelId,
                    status.getCodeAsByte()
            };
        }
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("channelId", channelId)
                .add("status", status)
                .add("dataEndpoint", dataEndpoint)
                .add("connectionResponseData", connectionResponseData)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ConnectResponseBody) {
            final var other = (ConnectResponseBody) obj;
            return Objects.equals(this.channelId, other.channelId)
                    && Objects.equals(this.status, other.status)
                    && Objects.equals(this.dataEndpoint, other.dataEndpoint)
                    && Objects.equals(this.connectionResponseData, other.connectionResponseData);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, status, dataEndpoint, connectionResponseData);
    }
}
