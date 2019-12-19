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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.KnxBytesEnum;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * Application Layer Protocol Control Information
 *
 * @author PITSCHR
 */
public enum APCI implements KnxBytesEnum {
    /**
     * A_GroupValue_Read-PDU
     * <pre>
     * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * |                         0   0 | 0   0   0   0   0   0   0   0 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    GROUP_VALUE_READ(0x00, "Group Value Read"),
    /**
     * A_GroupValue_Response-PDU
     * <pre>
     * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * |                         0   0 | 0   1   n   n   n   n   n   n |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    GROUP_VALUE_RESPONSE(0x40, 0x7F, "Group Value Response"),
    /**
     * A_GroupValue_Write-PDU
     * <pre>
     * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * |                         0   0 | 1   0   n   n   n   n   n   n |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    GROUP_VALUE_WRITE(0x80, 0xBF, "Group Value Write"),
    /**
     * A_IndividualAddress_Write-PDU
     * <pre>
     * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * |                         0   0 | 1   1   0   0   0   0   0   0 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    INDIVIDUAL_ADDRESS_WRITE(0xC0, "Individual Address Write"),
    /**
     * A_IndividualAddress_Read-PDU
     * <pre>
     * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * |                         0   1 | 0   0   0   0   0   0   0   0 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    INDIVIDUAL_ADDRESS_READ(0x0100, "Individual Address Read"),
    /**
     * A_IndividualAddress_Response-PDU
     * <pre>
     * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * |                         0   1 | 0   1   0   0   0   0   0   0 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    INDIVIDUAL_ADDRESS_RESPONSE(0x0140, "Individual Address Response");

    private final int codeRangeStart;
    private final int codeRangeEnd;
    private final String friendlyName;

    APCI(final int code, final String friendlyName) {
        this(code, code, friendlyName);
    }

    APCI(final int codeStartInclusive, final int codeEndInclusive, final String friendlyName) {
        this.codeRangeStart = codeStartInclusive;
        this.codeRangeEnd = codeEndInclusive;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link APCI} for the given {@code code}
     *
     * @param code value to find the associated {@link APCI}
     * @return existing {@link APCI}, or {@link KnxEnumNotFoundException} if no {@link APCI}
     * for given {@code code} exists
     */
    public static APCI valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> code >= x.codeRangeStart && code <= x.codeRangeEnd).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(APCI.class, code));
    }

    @Override
    public int getCode() {
        return this.codeRangeStart;
    }

    @Override
    public byte[] getCodeAsBytes() {
        final var code = this.getCode();
        return new byte[]{(byte) (code >>> 8), (byte) (code & 0xFF)};
    }

    /**
     * Returns if the code of APCI is in a range
     *
     * @return {@code true} if APCI code is a range, otherwise {@code false}
     */
    public boolean isCodeRange() {
        return this.codeRangeStart != this.codeRangeEnd;
    }

    @Override
    public String getFriendlyName() {
        return this.friendlyName;
    }

    @Override
    public String toString() {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("name", this.name())
                .add("friendlyName", this.friendlyName);
        if (this.isCodeRange()) {
            h.add("code", this.codeRangeStart + ".." + this.codeRangeEnd + " (" + ByteFormatter.formatHex(this.codeRangeStart) + ".." + ByteFormatter.formatHex(this.codeRangeEnd) + ")");
        } else {
            h.add("code", this.codeRangeStart + " (" + ByteFormatter.formatHex(this.codeRangeStart) + ")");
        }
        return h.toString();
        // @formatter:on
    }
}
