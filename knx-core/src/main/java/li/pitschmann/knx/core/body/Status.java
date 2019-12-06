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

import li.pitschmann.knx.core.KnxByteEnum;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;


import java.util.Arrays;

/**
 * Error codes for Status
 *
 * @author PITSCHR
 */
public enum Status implements KnxByteEnum {
    /**
     * Indicates that there was no error and operation was successful.
     */
    E_NO_ERROR(0x00, "Operation successful"),
    /**
     * The requested host protocol is not supported by the KNX device.
     */
    E_HOST_PROTOCOL_TYPE(0x01, "Host Protocol type not supported"),
    /**
     * The requested protocol version is not supported by the KNX device.
     */
    E_VERSION_NOT_SUPPORTED(0x02, "Protocol version not supported"),
    /**
     * The received sequence number is out of order.
     */
    E_SEQUENCE_NUMBER(0x04, "Sequence number out of order"),
    /**
     * The KNX Net/IP device device cannot find an active data connection
     * with the specified ID.
     */
    E_CONNECTION_ID(0x21, "No active connection with specified id"),
    /**
     * The KNX Net/IP device device does not support the requested connection type.
     */
    E_CONNECTION_TYPE(0x22, "Connection type not supported"),
    /**
     * The KNX Net/IP device device does not support one or more requested connection options.
     */
    E_CONNECTION_OPTION(0x23, "Connection option not supported"),
    /**
     * The KNX Net/IP device device cannot accept the new data connection because its
     * maximum amount of concurrent connections is already used.
     */
    E_NO_MORE_CONNECTIONS(0x24, "All connections already used"),
    /**
     * The KNX Net/IP device device detects an error concerning the data
     * connection with the specified ID.
     */
    E_DATA_CONNECTION(0x26, "Error in data connection for specified id"),
    /**
     * The KNX Net/IP device device detects an error concerning the KNX
     * connection with the specified ID.
     */
    E_KNX_CONNECTION(0x27, "Error in KNX connection for specified id"),
    /**
     * The KNX Net/IP device device does not support the requested KNX/IP
     * Tunneling layer.
     */
    E_TUNNELING_LAYER(0x29, "Requested KNX/IP Tunneling layer not supported"),
    /**
     * Unknown Status (not in KNX specification)
     */
    E_UNDEFINED_STATUS(0xFF, "Undefined status");

    private final int code;
    private final String friendlyName;

    Status(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link Status} for the given {@code code}
     *
     * @param code
     * @return existing {@link Status}, or {@link #E_UNDEFINED_STATUS} if no {@link Status}
     * for given {@code code} exists
     */
    public static Status valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElse(E_UNDEFINED_STATUS);
    }

    @Override
    public int getCode() {
        return this.code;
    }


    @Override
    public String getFriendlyName() {
        return this.friendlyName;
    }


    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("name", this.name())
                .add("friendlyName", this.friendlyName)
                .add("code", this.code + " (" + ByteFormatter.formatHex(this.code) + ")")
                .toString();
        // @formatter:on
    }
}
