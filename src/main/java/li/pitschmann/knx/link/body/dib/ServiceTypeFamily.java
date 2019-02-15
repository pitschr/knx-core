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

package li.pitschmann.knx.link.body.dib;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.KnxByteEnum;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.utils.ByteFormatter;

import java.util.Arrays;

/**
 * Service Type Family used in DIB for Service Type support.
 *
 * @author PITSCHR
 */
public enum ServiceTypeFamily implements KnxByteEnum {
    // @formatter:off
    /**
     * KNX/IP Core
     */
    CORE(0x02, "KNX/IP Core"),
    /**
     * KNX device Management
     */
    DEVICE_MANAGEMENT(0x03, "KNX device Management"),
    /**
     * KNX/IP Tunnelling
     */
    TUNNELLING(0x04, "KNX/IP Tunnelling"),
    /**
     * KNX/IP Routing
     */
    ROUTING(0x05, "KNX/IP Routing")
    // @formatter:on
    ;

    private final int code;
    private final String friendlyName;

    /**
     * Constructor for Service Type Family
     *
     * @param code
     * @param friendlyName
     */
    ServiceTypeFamily(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

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
        return MoreObjects.toStringHelper(this)
                .add("name", this.name())
                .add("friendlyName", this.friendlyName)
                .add("code", this.code + " (" + ByteFormatter.formatHex(this.code) + ")")
                .toString();
        // @formatter:on
    }
}
