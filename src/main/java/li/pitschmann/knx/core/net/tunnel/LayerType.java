/*
 * KNX Link - A library for KNX Net/IP communication
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

package li.pitschmann.knx.core.net.tunnel;

import li.pitschmann.knx.core.KnxByteEnum;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * KNX Layer types
 * <p>
 * See: KNX Specification, Tunneling, 2.2.2 - 2.2.4, 4.4.3
 *
 * @author PITSCHR
 */
public enum LayerType implements KnxByteEnum {
    /**
     * Establish a Data Link Layer tunnel to the KNX network.
     */
    TUNNEL_LINKLAYER(0x02, "Tunneling Link Layer"),
    /**
     * Establish a cEMI Raw Tunnel to the KNX network.
     */
    TUNNEL_RAW(0x04, "Tunneling Raw Layer"),
    /**
     * Establish a Busmonitor tunnel to the KNX network.
     */
    TUNNEL_BUSMONITOR(0x80, "Tunneling Busmonitor Layer");

    private final int code;
    private final String friendlyName;

    LayerType(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link LayerType} for the given {@code code}
     *
     * @param code value to find the associated {@link LayerType}
     * @return existing {@link LayerType}, or {@link KnxEnumNotFoundException} if no {@link LayerType}
     * for given {@code code} exists
     */
    public static LayerType valueOf(final int code) {
        return Arrays.stream(values())
                .filter(x -> x.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(LayerType.class, code));
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
