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

package li.pitschmann.knx.link.body.cemi;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.KnxByteEnum;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.utils.ByteFormatter;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Transport Layer Protocol Control Information
 *
 * @author PITSCHR
 */
public enum TPCI implements KnxByteEnum {
    /**
     * Unnumbered Package
     */
    UNNUMBERED_PACKAGE(0x00, "UDT unnumbered package"),
    /**
     * Numbered Package
     */
    NUMBERED_PACKAGE(0x40, "NDT numbered package"),
    /**
     * Unnumbered Control Data
     */
    UNNUMBERED_CONTROL_DATA(0x80, "UCD unnumbered control data"),
    /**
     * Numbered Control Data
     */
    NUMBERED_CONTROL_DATA(0xC0, "NCD numbered control data");

    private final int code;
    private final String friendlyName;

    TPCI(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link TPCI} for the given {@code code}
     *
     * @param code
     * @return existing {@link TPCI}, or {@link KnxEnumNotFoundException} if no {@link TPCI}
     * for given {@code code} exists
     */
    @Nonnull
    public static TPCI valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst().orElseThrow(() -> new KnxEnumNotFoundException(TPCI.class, code));
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
                .add("name", name())
                .add("friendlyName", this.friendlyName)
                .add("code", this.code + " (" + ByteFormatter.formatHex(this.code) + ")")
                .toString();
        // @formatter:on
    }
}
