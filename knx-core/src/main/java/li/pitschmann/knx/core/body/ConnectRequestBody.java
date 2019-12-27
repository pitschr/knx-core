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
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.tunnel.ConnectionRequestInformation;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * Body for Connect Request
 * <p>
 * The {@link ServiceType#CONNECT_REQUEST} frame shall be sent by the
 * KNXnet/IP Client to the control endpoint of the KNXnet/IP Server.
 * As for every request using control communication the KNXnet/IP body
 * shall begin with the return address information of the KNXnet/IP
 * Client’s control endpoint.
 * <p>
 * Next follows the {@link ConnectionRequestInformation}, a variable
 * data structure that shall include all additional information that
 * is specific to the requested connection type (and to the underlying
 * host protocol).
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | HPAI                                                          |
 * | Control endpoint                                              |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | HPAI                                                          |
 * | Data endpoint                                                 |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | CRI                                                           |
 * | Connection request Information                                |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * Source: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class ConnectRequestBody extends AbstractMultiRawData implements RequestBody, ControlChannelRelated {
    /**
     * Structure Length for {@link ConnectRequestBody}
     * <p>
     * 8 bytes for control HPAI<br>
     * 8 bytes for data HPAI<br>
     * 4 bytes for connection request information<br>
     */
    private static final int STRUCTURE_LENGTH = 20;
    private final HPAI controlEndpoint;
    private final HPAI dataEndpoint;
    private final ConnectionRequestInformation connectionRequestInformation;

    private ConnectRequestBody(final byte[] bytes) {
        super(bytes);

        this.controlEndpoint = HPAI.of(Arrays.copyOfRange(bytes, 0, 8));
        this.dataEndpoint = HPAI.of(Arrays.copyOfRange(bytes, 8, 16));
        this.connectionRequestInformation = ConnectionRequestInformation.of(Arrays.copyOfRange(bytes, 16, 20));
    }

    /**
     * Builds a new {@link ConnectRequestBody} instance
     *
     * @param bytes complete byte array for {@link ConnectRequestBody}
     * @return a  new immutable {@link ConnectRequestBody}
     */
    public static ConnectRequestBody of(final byte[] bytes) {
        return new ConnectRequestBody(bytes);
    }

    /**
     * Creates a new {@link ConnectRequestBody} instance
     *
     * @param controlEndpoint {@link HPAI} of control endpoint
     * @param dataEndpoint    {@link HPAI} of data endpoint
     * @param cri             connection request information
     * @return a  new immutable {@link ConnectRequestBody}
     */
    public static ConnectRequestBody of(final HPAI controlEndpoint, final HPAI dataEndpoint, final ConnectionRequestInformation cri) {
        // validate
        if (controlEndpoint == null) {
            throw new KnxNullPointerException("controlEndpoint");
        } else if (dataEndpoint == null) {
            throw new KnxNullPointerException("dataEndpoint");
        } else if (cri == null) {
            throw new KnxNullPointerException("cri");
        }

        final var controlEndpointAsBytes = controlEndpoint.getRawData();
        final var dataEndpointAsBytes = dataEndpoint.getRawData();
        final var criAsBytes = cri.getRawData();

        // create bytes
        final var bytes = new byte[controlEndpointAsBytes.length + dataEndpointAsBytes.length + criAsBytes.length];
        System.arraycopy(controlEndpointAsBytes, 0, bytes, 0, controlEndpointAsBytes.length);
        System.arraycopy(dataEndpointAsBytes, 0, bytes, 8, dataEndpointAsBytes.length);
        System.arraycopy(criAsBytes, 0, bytes, 16, criAsBytes.length);

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
        return ServiceType.CONNECT_REQUEST;
    }

    public HPAI getControlEndpoint() {
        return this.controlEndpoint;
    }

    public HPAI getDataEndpoint() {
        return this.dataEndpoint;
    }

    public ConnectionRequestInformation getConnectionRequestInformation() {
        return this.connectionRequestInformation;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("controlEndpoint", this.controlEndpoint.toString(false))
                .add("dataEndpoint", this.dataEndpoint.toString(false))
                .add("connectionRequestInformation", this.connectionRequestInformation.toString(false));
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
