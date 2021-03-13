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

package li.pitschmann.knx.core.exceptions;

/**
 * Exception when an unsupported code for {@link li.pitschmann.knx.core.cemi.APCI}
 * was received
 *
 * @author PITSCHR
 */
public final class KnxUnsupportedAPCICodeException extends KnxException {
    public KnxUnsupportedAPCICodeException(final int code) {
        super(
                String.format("Unsupported APCI code and is not suitable for KNX Net/IP communication: %s (%s)",
                        code, asBinaryString(code)
                )
        );
    }

    /**
     * Converts the APCI code to a binary string format (xxxx | yyyyyy)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0--+
     * Field Names |                     (APCI)        | (data or APCI)             |
     * Encoding    |                     A   A   A   A | AD  AD  AD  AD  AD  AD  AD |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+----+
     * </pre>
     *
     * @param code the APCI code
     * @return string representation of APCI code
     */
    private static String asBinaryString(final int code) {
        final var binaryString = "0000000000" + Integer.toBinaryString(code);
        final var binaryStringLength = binaryString.length();
        final var apciMajor4 = binaryString.substring(binaryStringLength - 10, binaryStringLength - 6);
        final var apciMinor6 = binaryString.substring(binaryStringLength - 6, binaryStringLength);
        return apciMajor4 + " | " + apciMinor6;
    }
}
