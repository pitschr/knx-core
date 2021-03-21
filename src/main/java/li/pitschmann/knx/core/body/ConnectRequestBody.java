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

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.tunnel.ConnectionRequestInfo;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Body for Connect Request
 * <p>
 * The {@link ServiceType#CONNECT_REQUEST} frame shall be sent by the
 * KNX Net/IP Client to the control endpoint of the KNX Net/IP Server.
 * As for every request using control communication the KNX Net/IP body
 * shall begin with the return address information of the KNX Net/IP
 * Client’s control endpoint.
 * <p>
 * Next follows the {@link ConnectionRequestInfo}, a variable
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
public final class ConnectRequestBody implements RequestBody, ControlChannelRelated {
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
    private final ConnectionRequestInfo connectionRequestInformation;

    private ConnectRequestBody(final byte[] bytes) {
        this(
                // byte[0..7] => control endpoint
                HPAI.of(Arrays.copyOfRange(bytes, 0, 8)),
                // byte[8..15]
                HPAI.of(Arrays.copyOfRange(bytes, 8, 16)),
                // byte[16..19]
                ConnectionRequestInfo.of(Arrays.copyOfRange(bytes, 16, 20))
        );
    }

    private ConnectRequestBody(final HPAI controlEndpoint,
                               final HPAI dataEndpoint,
                               final ConnectionRequestInfo connectionRequestInformation) {
        Preconditions.checkNonNull(controlEndpoint, "Control Endpoint is required.");
        Preconditions.checkNonNull(dataEndpoint, "Data Endpoint is required.");
        Preconditions.checkNonNull(connectionRequestInformation, "Connection request information is required.");

        this.controlEndpoint = controlEndpoint;
        this.dataEndpoint = dataEndpoint;
        this.connectionRequestInformation = connectionRequestInformation;
    }

    /**
     * Builds a new {@link ConnectRequestBody} instance
     *
     * @param bytes complete byte array for {@link ConnectRequestBody}
     * @return a new immutable {@link ConnectRequestBody}
     */
    public static ConnectRequestBody of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new ConnectRequestBody(bytes);
    }

    /**
     * Creates a new {@link ConnectRequestBody} instance
     *
     * @param controlEndpoint              {@link HPAI} of control endpoint
     * @param dataEndpoint                 {@link HPAI} of data endpoint
     * @param connectionRequestInformation connection request information
     * @return a  new immutable {@link ConnectRequestBody}
     */
    public static ConnectRequestBody of(final HPAI controlEndpoint,
                                        final HPAI dataEndpoint,
                                        final ConnectionRequestInfo connectionRequestInformation) {
        return new ConnectRequestBody(controlEndpoint, dataEndpoint, connectionRequestInformation);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.CONNECT_REQUEST;
    }

    public HPAI getControlEndpoint() {
        return controlEndpoint;
    }

    public HPAI getDataEndpoint() {
        return dataEndpoint;
    }

    public ConnectionRequestInfo getConnectionRequestInformation() {
        return connectionRequestInformation;
    }

    @Override
    public byte[] toByteArray() {
        final var controlEndpointAsBytes = controlEndpoint.toByteArray();
        final var dataEndpointAsBytes = dataEndpoint.toByteArray();
        final var criAsBytes = connectionRequestInformation.toByteArray();

        // create bytes
        final var bytes = new byte[controlEndpointAsBytes.length + dataEndpointAsBytes.length + criAsBytes.length];
        System.arraycopy(controlEndpointAsBytes, 0, bytes, 0, controlEndpointAsBytes.length);
        System.arraycopy(dataEndpointAsBytes, 0, bytes, 8, dataEndpointAsBytes.length);
        System.arraycopy(criAsBytes, 0, bytes, 16, criAsBytes.length);

        return bytes;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("controlEndpoint", controlEndpoint)
                .add("dataEndpoint", dataEndpoint)
                .add("connectionRequestInformation", connectionRequestInformation)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ConnectRequestBody) {
            final var other = (ConnectRequestBody) obj;
            return Objects.equals(this.controlEndpoint, other.controlEndpoint)
                    && Objects.equals(this.dataEndpoint, other.dataEndpoint)
                    && Objects.equals(this.connectionRequestInformation, other.connectionRequestInformation);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlEndpoint, dataEndpoint, connectionRequestInformation);
    }
}
