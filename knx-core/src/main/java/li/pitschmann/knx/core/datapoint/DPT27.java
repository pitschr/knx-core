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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPT27Value;

/**
 * Data Point Type 27 for 'Combined Info On Off' (4 Octets)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | m15                  (Mask Bit Info)                       m0 |
 * Encoding    | B   B   B   B   B   B   B   B   B   B   B   B   B   B   B   B |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | s15                   (Info On Off)                        s0 |
 *             | B   B   B   B   B   B   B   B   B   B   B   B   B   B   B   B |
 *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     4 octets (B<sub>32</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT27 extends BaseDataPointType<DPT27Value> {
    /**
     * <strong>27.001</strong> Combined Info On/Off
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | m15                  (Mask Bit Info)                       m0 |
     * Encoding    | B   B   B   B   B   B   B   B   B   B   B   B   B   B   B   B |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | s15                   (Info On Off)                        s0 |
     *             | B   B   B   B   B   B   B   B   B   B   B   B   B   B   B   B |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (B<sub>32</sub>)
     *             (0 = Output is Off, 1 = Output is On)
     *             s0  = Info On Off Output 1
     *             s1  = Info On Off Output 2
     *             ..
     *             s15 = Info On Off Output 16
     *
     *             (0 = Output is not valid, 1 = Output is valid)
     *             m0  = Mask Bit Info Output 1
     *             m1  = Mask Bit Info Output 2
     *             ..
     *             m15 = Mask Bit Info Output 16
     * </pre>
     *
     *
     */
    @DataPoint({"27.001", "dpt-27", "dpst-27-1"})
    public static final DPT27 COMBINED_INFO_ON_OFF = new DPT27("Combined Info On/Off");

    /**
     * Constructor for {@link DPT27}
     *
     * @param desc description for {@link DPT27}
     */
    private DPT27(final String desc) {
        super(desc, null);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 4;
    }

    @Override
    protected DPT27Value parse(final byte[] bytes) {
        return new DPT27Value(bytes);
    }

    public DPT27Value of(final byte maskByte1, final byte maskByte0, final byte onByte1, final byte onByte0) {
        return new DPT27Value(maskByte1, maskByte0, onByte1, onByte0);
    }
}
