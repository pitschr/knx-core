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

import li.pitschmann.knx.link.KnxByteEnum;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.utils.ByteFormatter;
import li.pitschmann.knx.utils.Strings;

import javax.annotation.Nonnull;
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
    ROUTING(0x05, "KNX/IP Routing");

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

    /**
     * A matching {@link ServiceTypeFamily} for the given {@code code}
     *
     * @param code
     * @return existing {@link ServiceTypeFamily}, or {@link KnxEnumNotFoundException} if no {@link ServiceTypeFamily}
     * for given {@code code} exists
     */
    @Nonnull
    public static ServiceTypeFamily valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(ServiceTypeFamily.class, code));
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
        return Strings.toStringHelper(this)
                .add("name", this.name())
                .add("friendlyName", this.friendlyName)
                .add("code", this.code + " (" + ByteFormatter.formatHex(this.code) + ")")
                .toString();
        // @formatter:on
    }
}
