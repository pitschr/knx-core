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
import li.pitschmann.knx.link.body.hpai.*;
import li.pitschmann.knx.link.body.tunnel.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.knx.link.header.*;

import javax.annotation.*;
import java.util.*;

/**
 * Body for Connect Request
 * <p>
 * The {@link ServiceType#CONNECT_REQUEST} frame shall be sent by the KNX client to the control endpoint of the
 * KNX Net/IP router.
 * <p>
 * Next follows the CRI, a variable data structure that shall include all additional information that is specific to the
 * requested connection type (and to the underlying host protocol).
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
        this.connectionRequestInformation = ConnectionRequestInformation.valueOf(Arrays.copyOfRange(bytes, 16, 20));
    }

    /**
     * Builds a new {@link ConnectRequestBody} instance
     *
     * @param bytes complete byte array for {@link ConnectRequestBody}
     * @return immutable {@link ConnectRequestBody}
     */
    public static ConnectRequestBody valueOf(final byte[] bytes) {
        return new ConnectRequestBody(bytes);
    }

    /**
     * Creates a new {@link ConnectRequestBody} instance
     *
     * @param controlEndpoint
     * @param dataEndpoint
     * @param cri
     * @return immutable {@link ConnectRequestBody}
     */
    public static ConnectRequestBody create(final HPAI controlEndpoint, final HPAI dataEndpoint, final ConnectionRequestInformation cri) {
        // validate
        if (controlEndpoint == null) {
            throw new KnxNullPointerException("controlEndpoint");
        } else if (dataEndpoint == null) {
            throw new KnxNullPointerException("dataEndpoint");
        } else if (cri == null) {
            throw new KnxNullPointerException("cri");
        }

        final byte[] controlEndpointAsBytes = controlEndpoint.getRawData();
        final byte[] dataEndpointAsBytes = dataEndpoint.getRawData();
        final byte[] criAsBytes = cri.getRawData();

        // create bytes
        final byte[] bytes = new byte[controlEndpointAsBytes.length + dataEndpointAsBytes.length + criAsBytes.length];
        System.arraycopy(controlEndpointAsBytes, 0, bytes, 0, controlEndpointAsBytes.length);
        System.arraycopy(dataEndpointAsBytes, 0, bytes, 8, dataEndpointAsBytes.length);
        System.arraycopy(criAsBytes, 0, bytes, 16, criAsBytes.length);

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
        final var h = MoreObjects.toStringHelper(this)
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
