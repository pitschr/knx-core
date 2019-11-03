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

import li.pitschmann.knx.link.AbstractMultiRawData;
import li.pitschmann.knx.link.ChannelIdAware;
import li.pitschmann.knx.link.body.hpai.HPAI;
import li.pitschmann.knx.link.body.tunnel.ConnectionResponseData;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;
import li.pitschmann.utils.Strings;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Body for Connect Response
 * <p>
 * The {@link ServiceType#CONNECT_RESPONSE} frame shall be sent by the KNX Net/IP device as an answer to a received
 * {@link ServiceType#CONNECT_REQUEST} frame. It shall be addressed to the KNX client’s control endpoint using the
 * HPAI included in the received {@link ServiceType#CONNECT_REQUEST} frame.
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
 *
 * @author PITSCHR
 */
public final class ConnectResponseBody extends AbstractMultiRawData implements ResponseBody, ChannelIdAware, ControlChannelRelated {
    /**
     * Structure Length for {@link ConnectResponseBody}
     * <p>
     * 1 byte for channel id<br>
     * 1 byte for status<br>
     * 8 bytes for data endpoint<br>
     * 4 bytes for connection response data<br>
     */
    private static final int STRUCTURE_LENGTH = 14;
    private final int channelId;
    private final Status status;
    private final HPAI dataEndpoint;
    private final ConnectionResponseData connectionResponseData;

    private ConnectResponseBody(final @Nonnull byte[] bytes) {
        super(bytes);

        this.channelId = Bytes.toUnsignedInt(bytes[0]);
        this.status = Status.valueOf(Bytes.toUnsignedInt(bytes[1]));
        if (bytes.length == STRUCTURE_LENGTH) {
            this.dataEndpoint = HPAI.of(Arrays.copyOfRange(bytes, 2, 10));
            this.connectionResponseData = ConnectionResponseData.of(Arrays.copyOfRange(bytes, 10, 14));
        } else {
            this.dataEndpoint = null;
            this.connectionResponseData = null;
        }
    }

    /**
     * Builds a new {@link ConnectResponseBody} instance
     *
     * @param bytes complete byte array for {@link ConnectResponseBody}
     * @return a new immutable {@link ConnectResponseBody}
     */
    @Nonnull
    public static ConnectResponseBody of(final @Nonnull byte[] bytes) {
        return new ConnectResponseBody(bytes);
    }

    /**
     * Creates a new {@link ConnectResponseBody} instance
     *
     * @param channelId
     * @param status
     * @param dataEndpoint
     * @param crd
     * @return a new immutable {@link ConnectResponseBody}
     */
    @Nonnull
    public static ConnectResponseBody of(final int channelId,
                                         final @Nonnull Status status,
                                         final @Nonnull HPAI dataEndpoint,
                                         final @Nonnull ConnectionResponseData crd) {
        // validate
        if (channelId < 0 || channelId > 0xFF) {
            throw new KnxNumberOutOfRangeException("channelId", 0, 0xFF, channelId);
        } else if (status == null) {
            throw new KnxNullPointerException("status");
        }

        // behavior depends on status
        if (status == Status.E_NO_ERROR) {
            if (dataEndpoint == null) {
                throw new KnxNullPointerException("dataEndpoint");
            } else if (crd == null) {
                throw new KnxNullPointerException("crd");
            }

            // no error - provide everything
            final var dataEndpointAsBytes = dataEndpoint.getRawData();
            final var crdAsBytes = crd.getRawData();

            // create bytes
            final var bytes = new byte[2 + dataEndpointAsBytes.length + crdAsBytes.length];
            bytes[0] = (byte) channelId;
            bytes[1] = status.getCodeAsByte();
            System.arraycopy(dataEndpointAsBytes, 0, bytes, 2, dataEndpointAsBytes.length);
            System.arraycopy(crdAsBytes, 0, bytes, dataEndpointAsBytes.length + 2, crdAsBytes.length);

            return of(bytes);
        } else {
            // error (only channel id + status)
            return of(new byte[]{(byte) channelId, status.getCodeAsByte()});
        }
    }

    @Override
    protected void validate(final @Nonnull byte[] rawData) {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        } else if (rawData.length == 2) {
            // OK -> there was an error (dataEndpoint and connectionResponseData will be null)
        } else if (rawData.length != STRUCTURE_LENGTH) {
            // OK -> no error
            throw new KnxNumberOutOfRangeException("rawData", STRUCTURE_LENGTH, STRUCTURE_LENGTH, rawData.length, rawData);
        }
    }

    @Nonnull
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
        return this.channelId;
    }

    /**
     * Get the status of connect response
     *
     * @return
     */
    @Nonnull
    public Status getStatus() {
        return this.status;
    }

    /**
     * Returns the data endpoint
     *
     * @return {@link HPAI}, it may be null when there was an error (see: {@link #getStatus()})
     */
    @Nonnull
    public HPAI getDataEndpoint() {
        return this.dataEndpoint;
    }

    /**
     * Returns the connection response data
     *
     * @return {@link ConnectionResponseData}, it may be null when there was an error (see: {@link #getStatus()})
     */
    @Nonnull
    public ConnectionResponseData getConnectionResponseData() {
        return this.connectionResponseData;
    }

    @Nonnull
    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("channelId", this.channelId + " (" + ByteFormatter.formatHex(this.channelId) + ")")
                .add("status", this.status)
                .add("dataEndpoint", this.dataEndpoint == null ? "null" : this.dataEndpoint.toString(false))
                .add("connectionResponseData", this.connectionResponseData == null ? "null" : this.connectionResponseData.toString(false));
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
