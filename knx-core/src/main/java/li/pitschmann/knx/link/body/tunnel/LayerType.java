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

package li.pitschmann.knx.link.body.tunnel;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.KnxByteEnum;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.utils.ByteFormatter;

import java.util.Arrays;

/**
 * KNX Tunneling Layer types
 *
 * @author PITSCHR
 */
public enum LayerType implements KnxByteEnum {
    // @formatter:off
    /**
     * Establish a Data Link Layer tunnel to the KNX network.
     */
    TUNNEL_LINKLAYER(0x02, "Tunneling Link Layer"),
    /**
     * Establish a raw tunnel to the KNX network.
     */
    TUNNEL_RAW(0x04, "Tunneling Raw Layer"),
    /**
     * Establish a Busmonitor tunnel to the KNX network.
     */
    TUNNEL_BUSMONITOR(0x80, "Tunneling Busmonitor Layer")
    // @formatter:on
    ;

    private final int code;
    private final String friendlyName;

    LayerType(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    public static LayerType valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(LayerType.class, code));
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
                .add("name", name())
                .add("friendlyName", this.friendlyName)
                .add("code", this.code + " (" + ByteFormatter.formatHex(this.code) + ")")
                .toString();
        // @formatter:on
    }
}
