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

import li.pitschmann.knx.link.KnxByteEnum;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Strings;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Host Protocols for IP network
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
     * @param code
     * @return existing {@link HostProtocol}, or {@link KnxEnumNotFoundException} if no {@link HostProtocol}
     * for given {@code code} exists
     */
    @Nonnull
    public static HostProtocol valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(HostProtocol.class, code));
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
                .add("name", name())
                .add("friendlyName", this.friendlyName)
                .add("code", this.code + " (" + ByteFormatter.formatHex(this.code) + ")")
                .toString();
        // @formatter:on
    }
}
