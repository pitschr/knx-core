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

package li.pitschmann.knx.core.body.dib;

import li.pitschmann.knx.core.KnxByteEnum;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;


import java.util.Arrays;

/**
 * Description Types
 *
 * @author PITSCHR
 */
public enum DescriptionType implements KnxByteEnum {
    /**
     * Device information e.g. KNX medium.
     */
    DEVICE_INFO(0x01, "Device information"),
    /**
     * Service families supported by the device.
     */
    SUPPORTED_SERVICE_FAMILIES(0x02, "Supported Service Families"),
    /**
     * IP Configuration
     */
    IP_CONFIG(0x03, "IP Configuration"),
    /**
     * IP Current Configuration
     */
    IP_CURRENT_CONFIG(0x04, "IP Current Configuration"),
    /**
     * KNX addresses
     */
    KNX_ADDRESSES(0x05, "KNX addresses"),
    /**
     * DIB structure for further data defined by device manufacturer.
     */
    MANUFACTURER_DATA(0xFE, "Manufacturer Data");

    private final int code;
    private final String friendlyName;

    DescriptionType(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link DescriptionType} for the given {@code code}
     *
     * @param code
     * @return existing {@link DescriptionType}, or {@link KnxEnumNotFoundException} if no {@link DescriptionType}
     * for given {@code code} exists
     */
    public static DescriptionType valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(DescriptionType.class, code));
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
