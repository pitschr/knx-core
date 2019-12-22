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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.value.DPT12Value;

/**
 * Data Point Type 12 for 'Value 4-Octet Unsigned Count' (4 Octets)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Unsigned Value)                                              |
 * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | (Unsigned Value)                                              |
 *             | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     4 octets (U<sub>32</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT12 extends AbstractRangeDataPointType<DPT12Value, Long> {
    /**
     * <strong>12.001</strong> Value 4-Octet Unsigned Count
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)                                              |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Unsigned Value)                                              |
     *             | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (U<sub>32</sub>)
     * Range:      U = [0 .. 4294967295]
     * Unit:       pulses
     * Resolution: 1 pulse
     * </pre>
     */
    @KnxDataPointType({"12.001", "dpt-12", "dpst-12-1"})
    public static final DPT12 VALUE_4_OCTET_UNSIGNED_COUNT = new DPT12("12.001", "Value 4-Octet Unsigned Count", 0L, 4294967295L, "pulses");

    /**
     * <strong>12.1200</strong> Volume (l)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)                                              |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Unsigned Value)                                              |
     *             | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (U<sub>32</sub>)
     * Range:      U = [0 .. 4294967295]
     * Unit:       l
     * Resolution: 1 l
     * </pre>
     */
    @KnxDataPointType({"12.1200", "dpst-12-1200"})
    public static final DPT12 VOLUME_L = new DPT12("12.1200", "Volume", 0L, 4294967295L, "l");

    /**
     * <strong>12.1201</strong> Volume (m<sup>3</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)                                              |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Unsigned Value)                                              |
     *             | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (U<sub>32</sub>)
     * Range:      U = [0 .. 4294967295]
     * Unit:       m<sup>3</sup>
     * Resolution: 1 m<sup>3</sup>
     * </pre>
     */
    @KnxDataPointType({"12.1201", "dpst-12-1201"})
    public static final DPT12 VOLUME_M3 = new DPT12("12.1201", "Volume", 0L, 4294967295L, "m³");

    /**
     * Constructor for {@link DPT12}
     *
     * @param id         identifier for {@link DPT12}
     * @param desc       description for {@link DPT12}
     * @param lowerValue the lower value for {@link DPT12}
     * @param upperValue the upper value for {@link DPT12}
     * @param unit       the unit representation for {@link DPT12}
     */
    private DPT12(final String id,
                  final String desc,
                  final long lowerValue,
                  final long upperValue,
                  final @Nullable String unit) {
        super(id, desc, lowerValue, upperValue, unit);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 4;
    }

    @Override
    protected DPT12Value parse(final byte[] bytes) {
        return new DPT12Value(this, bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT12Value parse(final String[] args) {
        return new DPT12Value(this, Long.parseLong(args[0]));
    }

    public DPT12Value toValue(final long value) {
        return new DPT12Value(this, value);
    }

    public byte[] toByteArray(final long value) {
        return DPT12Value.toByteArray(value);
    }
}
