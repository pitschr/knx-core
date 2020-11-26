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

package li.pitschmann.knx.core.dib;

import li.pitschmann.knx.core.KnxByteEnum;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * Service Type Family used in DIB for Service Type support.
 *
 * @author PITSCHR
 */
public enum ServiceTypeFamily implements KnxByteEnum {
    /**
     * KNX/IP Core
     */
    CORE(0x02, "KNX/IP Core"),
    /**
     * KNX device Management
     */
    DEVICE_MANAGEMENT(0x03, "KNX device Management"),
    /**
     * KNX/IP Tunneling
     */
    TUNNELING(0x04, "KNX/IP Tunneling"),
    /**
     * KNX/IP Routing
     */
    ROUTING(0x05, "KNX/IP Routing"),
    /**
     * KNX/IP Remote Logging
     */
    REMOTE_LOGGING(0x06, "KNX/IP Remote Logging"),
    /**
     * KNX/IP Remote Configuration and Diagnosis
     */
    REMOTE_CONFIGURATION_AND_DIAGNOSIS(0x07, "KNX/IP Remote Configuration and Diagnosis"),
    /**
     * KNX/IP Object Server
     */
    OBJECT_SERVER(0x08, "KNX/IP Object Server");

    private final int code;
    private final String friendlyName;

    ServiceTypeFamily(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link ServiceTypeFamily} for the given {@code code}
     *
     * @param code value to find the associated {@link ServiceTypeFamily}
     * @return existing {@link ServiceTypeFamily}, or {@link KnxEnumNotFoundException} if no {@link ServiceTypeFamily}
     * for given {@code code} exists
     */
    public static ServiceTypeFamily valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(ServiceTypeFamily.class, code));
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
