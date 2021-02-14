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

import li.pitschmann.knx.core.datapoint.value.DPT25Value;

/**
 * Data Point Type 25 for 'Double Nibble Set' (8 Bits)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Busy)          (Nak)         |
 * Encoding    | U   U   U   U   U   U   U   U |
 *             +---+---+---+---+---+---+---+---+
 * Format:     8 bit (U<sub>4</sub>U<sub>4</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT25 extends BaseDataPointType<DPT25Value> {
    /**
     * <strong>25.1000</strong> Busy/Nak Repetitions
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Busy)          (Nak)         |
     * Encoding    | U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (U<sub>4</sub>U<sub>4</sub>)
     * Range:      Busy = [0 .. 15]
     *             Nak  = [0 .. 15]
     * </pre>
     */
    @DataPoint({"25.1000", "dpt-25", "dpst-25-1"})
    public static final DPT25 BUSY_NAK_REPETITIONS = new DPT25("Busy/Nak Repetitions");

    /**
     * Constructor for {@link DPT25}
     *
     * @param description         description for {@link DPT25}
     */
    private DPT25(final String description) {
        super(description);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 1;
    }

    @Override
    protected DPT25Value parse(final byte[] bytes) {
        return new DPT25Value(bytes[0]);
    }

    public DPT25Value of(final int busy, final int nak) {
        return new DPT25Value(busy, nak);
    }
}
