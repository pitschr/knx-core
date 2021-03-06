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
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * Host Protocols for IP network
 * <p>
 * The KNX Net/IP core specification defines the integration
 * of KNX protocol implementations on top of the Internet
 * Protocol (IP). It describes the general and host protocol
 * independent as well as the host protocol specific parts
 * of the KNX Net/IP communication.
 * <p>
 * See: KNX Specification, Core, 3.1
 *
 * @author PITSCHR
 */
public enum HostProtocol implements KnxByteEnum {
    /**
     * Identifies an Internet Protocol version 4 address and
     * port number for UDP communication.
     */
    IPV4_UDP(0x01, "IP v4 UDP communication"),
    /**
     * Identifies an Internet Protocol version 4 address and
     * port number for TCP communication.
     */
    IPV4_TCP(0x02, "IP v4 TCP communication");

    private final int code;
    private final String friendlyName;

    HostProtocol(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link HostProtocol} for the given {@code code}
     *
     * @param code value to find the associated {@link HostProtocol}
     * @return existing {@link HostProtocol}, or {@link KnxEnumNotFoundException} if no {@link HostProtocol}
     * for given {@code code} exists
     */
    public static HostProtocol valueOf(final int code) {
        return Arrays.stream(values())
                .filter(x -> x.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(HostProtocol.class, code));
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
