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

package li.pitschmann.knx.core.dib;

import li.pitschmann.knx.core.KnxByteEnum;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * Medium Types for KNX communications
 *
 * @author PITSCHR
 */
public enum MediumType implements KnxByteEnum {
    /**
     * KNX Medium for Twisted Pair 1
     */
    TP(0x02, "Twisted Pair 1 (9600bit/s)"),
    /**
     * KNX Medium for Power Line 110 (1200 bit/s)
     */
    PL110(0x04, "Power Line 110 (110 kHz)"),
    /**
     * KNX Medium for Radio Frequency (868 MHz)
     */
    RF(0x10, "Radio Frequency (868 MHz)"),
    /**
     * KNX addresses for KNX IP
     */
    KNX_IP(0x20, "KNX IP");

    private final int code;
    private final String friendlyName;

    MediumType(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link MediumType} for the given {@code code}
     *
     * @param code value to find the associated {@link MediumType}
     * @return existing {@link MediumType}, or {@link KnxEnumNotFoundException} if no {@link MediumType}
     * for given {@code code} exists
     */
    public static MediumType valueOf(final int code) {
        return Arrays.stream(values())
                .filter(x -> x.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(MediumType.class, code));
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
