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

package li.pitschmann.knx.core.address;

import li.pitschmann.knx.core.KnxByteEnum;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * Type of KNX Address
 *
 * @author PITSCHR
 */
public enum AddressType implements KnxByteEnum {
    /**
     * Individual Address used by physical devices
     * <p>
     * For further information, see {@link IndividualAddress}
     */
    INDIVIDUAL(0x00, "Individual Address"),
    /**
     * Group Address used as virtual addresses that
     * is listened by physical/non-physical devices
     * <p>
     * For further information, see {@link GroupAddress}
     */
    GROUP(0x01, "Group Address");

    private final int code;
    private final String friendlyName;

    AddressType(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link AddressType} for the given {@code code}
     *
     * @param code value to find the associated {@link AddressType}
     * @return existing {@link AddressType}, or {@link KnxEnumNotFoundException} if no {@link AddressType}
     * for given {@code code} exists
     */
    public static AddressType valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(AddressType.class, code));
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
        return Strings.toStringHelper(this)
                .add("name", name())
                .add("friendlyName", friendlyName)
                .add("code", ByteFormatter.formatHex(code))
                .toString();
    }
}
