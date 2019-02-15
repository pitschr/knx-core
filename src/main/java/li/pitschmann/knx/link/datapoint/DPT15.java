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

package li.pitschmann.knx.link.datapoint;

import li.pitschmann.knx.link.datapoint.annotation.KnxDataPointType;
import li.pitschmann.knx.link.datapoint.value.DPT15Value;
import li.pitschmann.knx.link.datapoint.value.DPT15Value.Flags;

/**
 * Data Point Type 15 for 'Access Identification Data' (4 Octets)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Access Identification Data)                                  |
 *             | (D6)            (D5)            (D4)            (D3)          |
 * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | (Access Identification Data)  | E   P   D   C   (Index)       |
 *             | (D2)            (D1)          |                               |
 *             | U   U   U   U   U   U   U   U | b   b   b   b   N   N   N   N |
 *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     4 octets (U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> B<sub>4</sub> N<sub>4</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT15 extends AbstractDataPointType<DPT15Value> {
    /**
     * <strong>15.000</strong> Access Data
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Access Identification Data)                                  |
     *             | (D6)            (D5)            (D4)            (D3)          |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Access Identification Data)  | E   P   D   C   (Index)       |
     *             | (D2)            (D1)          |                               |
     *             | U   U   U   U   U   U   U   U | b   b   b   b   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> B<sub>4</sub> N<sub>4</sub>)
     * Range:      D6, D5, D4, D3, D2, D1 = [0 .. 9]
     *                Binary Encoded Value
     *                Digits of Access Identification code. Only a card or key number should be used.
     *                If 24 bits are not necessary, the most significant positions shall be set to zero.
     *             N = [0 .. 15]  Index
     *                Binary Encoded Value
     *             E = {0, 1}  Detection Error
     *                0 = No Error
     *                1 = Reading of Access Information Code were not successful
     *             P = {0, 1}  Permission
     *                0 = Not Accepted
     *                1 = Accepted
     *             D = {0, 1}  Read Direction (e.g. of Badge)
     *                0 = Left to Right
     *                1 = Right to Left
     *             C = {0, 1}  Encryption of Access Information
     *                0 = No Encryption
     *                1 = Encryption
     * </pre>
     */
    @KnxDataPointType(id = "15.000", description = "Access Data")
    public static final DPT15 ACCESS_DATA = new DPT15("15.000", "Access Data");

    /**
     * Constructor for {@link DPT15}
     *
     * @param id
     * @param desc
     */
    private DPT15(final String id, final String desc) {
        super(id, desc);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 4;
    }

    @Override
    protected DPT15Value parse(final byte[] bytes) {
        return new DPT15Value(bytes);
    }

    public DPT15Value toValue(final byte[] accessIdentificationData, final Flags flags) {
        return new DPT15Value(accessIdentificationData, flags);
    }

    public byte[] toByteArray(final byte[] accessIdentificationData, final Flags flags) {
        return DPT15Value.toByteArray(accessIdentificationData, flags);
    }
}
