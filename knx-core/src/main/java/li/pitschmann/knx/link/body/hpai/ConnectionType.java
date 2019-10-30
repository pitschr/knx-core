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

package li.pitschmann.knx.link.body.hpai;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.KnxByteEnum;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.utils.ByteFormatter;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Connection types how the connection is established
 *
 * @author PITSCHR
 */
public enum ConnectionType implements KnxByteEnum {
    /**
     * Data connection used to configure a KNX device.
     */
    DEVICE_MANAGEMENT_CONNECTION(0x03, "Device Management Connection"),
    /**
     * Data connection used to forward KNX telegrams between two KNX devices.
     */
    TUNNEL_CONNECTION(0x04, "Tunnel Connection"),
    /**
     * Data connection used for configuration and data transfer with a remote logging server.
     */
    REMOTE_LOGGING_CONNECTION(0x06, "Remote Logging Connection"),
    /**
     * Data connection used for data transfer with a remote configuration server.
     */
    REMOTE_CONFIG_CONNECTION(0x07, "Remote Configuration Connection"),
    /**
     * Data connection used for configuration and data transfer with an Object Server
     * in a KNX device.
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
     * @param code
     * @return existing {@link ConnectionType}, or {@link KnxEnumNotFoundException} if no {@link ConnectionType}
     * for given {@code code} exists
     */
    @Nonnull
    public static ConnectionType valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(ConnectionType.class, code));
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Nonnull
    @Override
    public String getFriendlyName() {
        return this.friendlyName;
    }

    @Nonnull
    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(this)
                .add("name", this.name())
                .add("friendlyName", this.friendlyName)
                .add("code", this.code + " (" + ByteFormatter.formatHex(this.code) + ")")
                .toString();
        // @formatter:on
    }
}
