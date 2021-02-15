/*
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
import li.pitschmann.knx.core.datapoint.value.DPT6Value;
import li.pitschmann.knx.core.datapoint.value.DPT6Value.StatusMode.Mode;
import li.pitschmann.knx.core.utils.Bytes;

/**
 * Data Point Type 6 for 'Relative Signed Value' (8 Bits)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Relative Signed Value)       |
 * Encoding    | V   V   V   V   V   V   V   V |
 *             +---+---+---+---+---+---+---+---+
 * Format:     8 bit (V<sub>8</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT6 extends BaseRangeDataPointType<DPT6Value, Integer> {
    /**
     * <strong>6.001</strong> Percent (%)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Relative Signed Value)       |
     * Encoding    | V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (V<sub>8</sub>)
     * Range:      V = [-128% .. 127%]
     * Unit:       %
     * Resolution: 1%
     * </pre>
     */
    @DataPoint({"6.001", "dpst-6-1"})
    public static final DPT6 PERCENT = new DPT6("Percent", -128, 127, "%");

    /**
     * <strong>6.010</strong> Value 1 Octet Signed Count
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Relative Signed Value)       |
     * Encoding    | V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (V<sub>8</sub>)
     * Range:      V = [-128 .. 127]
     * Unit:       pulses
     * Resolution: 1 pulse
     * </pre>
     */
    @DataPoint({"6.010", "dpt-6", "dpst-6-10"})
    public static final DPT6 VALUE_1_OCTET_COUNT = new DPT6("Value 1 Octet Signed Count", -128, 127, "pulses");

    /**
     * <strong>6.020</strong> Status Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | a   b   c   d   e   f   f   f |
     * Encoding    | B   B   B   B   B   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (B<sub>5</sub> N<sub>3</sub>)
     * Range:      a, b, c, d, e = {0,1}
     *                  0 = set
     *                  1 = clear
     *             f             = {001b, 010b, 100b}
     *                  001b = mode 0 is active
     *                  010b = mode 1 is active
     *                  100b = mode 2 is active
     * Unit:       N/A
     * Resolution: N/A
     * </pre>
     */
    @DataPoint({"6.020", "dpst-6-20"})
    public static final DPT6.StatusMode STATUS_MODE = new DPT6.StatusMode("Status Mode");

    /**
     * Constructor for {@link DPT6}
     *
     * @param description description for {@link DPT6}
     * @param lowerValue  the lower value for {@link DPT6}
     * @param upperValue  the upper value for {@link DPT6}
     * @param unit        the unit representation for {@link DPT6}
     */
    private DPT6(final String description,
                 final int lowerValue,
                 final int upperValue,
                 final @Nullable String unit) {
        super(description, lowerValue, upperValue, unit);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 1;
    }

    @Override
    protected DPT6Value parse(final byte[] bytes) {
        return new DPT6Value(this, bytes[0]);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT6Value parse(final String[] args) {
        return of(Integer.parseInt(args[0]));
    }

    public DPT6Value of(final int value) {
        return new DPT6Value(this, value);
    }

    /**
     * Special class for {@link DPT6}: Status Mode
     *
     * @author PITSCHR
     */
    public static class StatusMode extends BaseDataPointType<DPT6Value.StatusMode> {
        /**
         * Constructor for {@link DPT6.StatusMode}
         *
         * @param description description of {@link StatusMode}
         */
        private StatusMode(final String description) {
            super(description);
        }

        @Override
        protected boolean isCompatible(final byte[] bytes) {
            return bytes.length == 1;
        }

        @Override
        protected DPT6Value.StatusMode parse(final byte[] bytes) {
            return new DPT6Value.StatusMode(bytes[0]);
        }

        @Override
        protected boolean isCompatible(final String[] args) {
            return args.length == 1;
        }

        @Override
        protected DPT6Value.StatusMode parse(final String[] args) {
            return new DPT6Value.StatusMode(Bytes.toByteArray(args[0])[0]);
        }

        public DPT6Value.StatusMode of(final boolean a, final boolean b, final boolean c, final boolean d, final boolean e, final Mode mode) {
            return new DPT6Value.StatusMode(a, b, c, d, e, mode);
        }
    }
}
