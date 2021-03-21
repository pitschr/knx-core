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

package li.pitschmann.knx.core.net;

import li.pitschmann.knx.core.KnxByteEnum;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.tunnel.ConnectionRequestInfo;
import li.pitschmann.knx.core.net.tunnel.ConnectionResponseData;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * Connection Management Types how the connection is established
 * <p>
 * As for every {@link ServiceType#CONNECT_REQUEST} using control
 * communication the KNX Net/IP body shall begin with the return
 * address information of the KNX Net/IP Client’s control endpoint.
 * <p>
 * Inside the {@link ConnectionRequestInfo} and {@link ConnectionResponseData},
 * a variable data structure that shall include all additional information
 * that is specific to the requested connection type (and to the
 * underlying host protocol). The exact definition of this structure
 * can be found in the description of the specific connection type.
 * <p>
 * See: KNX Specification, Core, 7.8
 *
 * @author PITSCHR
 */
public enum ConnectionType implements KnxByteEnum {
    /**
     * Data connection used to configure a KNX device.
     */
    DEVICE_MANAGEMENT_CONNECTION(0x03, "Device Management Connection"),
    /**
     * Data connection used to forward KNX telegrams
     * between two KNX devices.
     */
    TUNNEL_CONNECTION(0x04, "Tunnel Connection"),
    /**
     * Data connection used for configuration and
     * data transfer with a remote logging server.
     */
    REMOTE_LOGGING_CONNECTION(0x06, "Remote Logging Connection"),
    /**
     * Data connection used for data transfer with
     * a remote configuration server.
     */
    REMOTE_CONFIG_CONNECTION(0x07, "Remote Configuration Connection"),
    /**
     * Data connection used for configuration and
     * data transfer with an Object Server in a KNX device.
     */
    OBJECT_SERVER_CONNECTION(0x08, "Object Server Connection");

    private final int code;
    private final String friendlyName;

    ConnectionType(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link ConnectionType} for the given {@code code}
     *
     * @param code value to find the associated {@link ConnectionType}
     * @return existing {@link ConnectionType}, or {@link KnxEnumNotFoundException} if no {@link ConnectionType}
     * for given {@code code} exists
     */
    public static ConnectionType valueOf(final int code) {
        return Arrays.stream(values())
                .filter(x -> x.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(ConnectionType.class, code));
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("name", name())
                .add("friendlyName", friendlyName)
                .add("code", code)
                .toString();
    }
}
