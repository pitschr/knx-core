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
import li.pitschmann.knx.core.datapoint.value.DPT7Value;

import java.util.function.Function;

/**
 * Data Point Type 7 for '2-Octets Unsigned Value' (2 Octets)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Unsigned Value)                                              |
 * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     2 octets (U<sub>16</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT7 extends BaseRangeDataPointType<DPT7Value, Integer> {
    /**
     * <strong>7.001</strong> Value 2-octet unsigned count
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)                                              |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     * Unit:       pulses
     * Resolution: 1 pulse
     * </pre>
     */
    @DataPoint({"7.001", "dpt-7", "dpst-7-1"})
    public static final DPT7 VALUE_2_OCTET_UNSIGNED_COUNT = new DPT7("Value 2-Octet Unsigned Count", 0, 65535, "pulses", null);
    /**
     * <strong>7.002</strong> Time Period (milliseconds)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Time Period)                                                 |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     *                  0ms .. 65535ms
     * Unit:       ms
     * Resolution: 1 ms
     * </pre>
     */
    @DataPoint({"7.002", "dpst-7-2"})
    public static final DPT7 TIME_PERIOD_MS = new DPT7("Time Period", 0, 65535, "ms", null);
    /**
     * <strong>7.003</strong> Time Period (milliseconds, resolution 10ms)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Time Period)                                                 |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     *                  0ms .. 655.35ms
     * Unit:       ms
     * Resolution: 10 ms
     * </pre>
     */
    @DataPoint({"7.003", "dpst-7-3"})
    public static final DPT7 TIME_PERIOD_10MS = new DPT7("Time Period 1/10", 0, 65535, "ms", v -> v / 100d);
    /**
     * <strong>7.004</strong> Time Period (milliseconds, resolution 100ms)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Time Period)                                                 |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     *                  0ms .. 6553.5ms
     * Unit:       ms
     * Resolution: 100 ms
     * </pre>
     */
    @DataPoint({"7.004", "dpst-7-4"})
    public static final DPT7 TIME_PERIOD_100MS = new DPT7("Time Period 1/100", 0, 65535, "ms", v -> v / 10d);
    /**
     * <strong>7.005</strong> Time Period (seconds)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Time Period)                                                 |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     * Unit:       s
     * Resolution: 1 s
     * </pre>
     */
    @DataPoint({"7.005", "dpst-7-5"})
    public static final DPT7 TIME_PERIOD_SECONDS = new DPT7("Time Period (sec)", 0, 65535, "s", null);
    /**
     * <strong>7.006</strong> Time Period (minutes)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Time Period)                                                 |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     * Unit:       min
     * Resolution: 1 min
     * </pre>
     */
    @DataPoint({"7.006", "dpst-7-6"})
    public static final DPT7 TIME_PERIOD_MINUTES = new DPT7("Time Period (min)", 0, 65535, "min", null);
    /**
     * <strong>7.007</strong> Time Period (hours)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Time Period)                                                 |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     * Unit:       h
     * Resolution: 1 h
     * </pre>
     */
    @DataPoint({"7.007", "dpst-7-7"})
    public static final DPT7 TIME_PERIOD_HOURS = new DPT7("Time Period (h)", 0, 65535, "h", null);
    /**
     * <strong>7.010</strong> Identifier Interface Object Property Data Type
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)                                              |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     * Unit:       N/A
     * Resolution: N/A
     *
     * Identifier Interface Object Property data type. No Unit.
     * </pre>
     */
    @DataPoint({"7.010", "dpst-7-10"})
    public static final DPT7 PROP_DATA_TYPE = new DPT7("Interface Object Property ID", 0, 65535, null, null);
    /**
     * <strong>7.011</strong> Length (mm)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)                                              |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     * Unit:       mm
     * Resolution: 1 mm
     * </pre>
     */
    @DataPoint({"7.011", "dpst-7-11"})
    public static final DPT7 LENGTH_MM = new DPT7("Length", 0, 65535, "mm", null);
    /**
     * <strong>7.012</strong> Electric current (mA)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)                                              |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     *                 0 = no power
     *                 1 .. 65535 = value binary encoded in milliamperes
     * Unit:       mA
     * Resolution: 1 mA
     * </pre>
     */
    @DataPoint({"7.012", "dpst-7-12"})
    public static final DPT7 ELECTRIC_CURRENT_MILLIAMPERES = new DPT7("Electrical Power", 0, 65535, "mA", null);
    /**
     * <strong>7.013</strong> Brightness (lux)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)                                              |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     * Unit:       lux
     * Resolution: 1 lux
     * </pre>
     */
    @DataPoint({"7.013", "dpst-7-13"})
    public static final DPT7 BRIGHTNESS = new DPT7("Brightness", 0, 65535, "lux", null);

    /**
     * <strong>7.600</strong> Absolute Color Temperature (K)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)                                              |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (U<sub>16</sub>)
     * Range:      U = [0 .. 65535]
     * Unit:       K
     * Resolution: 1 K
     * </pre>
     */
    @DataPoint({"7.600", "dpst-7-600"})
    public static final DPT7 ABSOLUTE_COLOR_TEMPERATURE = new DPT7("Absolute Color Temperature", 0, 65535, "K", null);
    /**
     * Calculation function
     * <p>
     * Calculates from {@link Integer} to {@link Float} using a formula
     */
    private final Function<Integer, Double> calculationFunction;

    /**
     * Constructor for {@link DPT7}
     *
     * @param desc                description for {@link DPT7}
     * @param lowerValue          the lower value for {@link DPT7}
     * @param upperValue          the upper value for {@link DPT7
     * @param unit                the unit representation for {@link DPT7}
     * @param calculationFunction the calculation function for value representation
     */
    private DPT7(final String desc,
                 final int lowerValue,
                 final int upperValue,
                 final @Nullable String unit,
                 final @Nullable Function<Integer, Double> calculationFunction) {
        super(desc, lowerValue, upperValue, unit);
        this.calculationFunction = calculationFunction;
    }

    @Nullable
    public Function<Integer, Double> getCalculationFunction() {
        return this.calculationFunction;
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 2;
    }

    @Override
    protected DPT7Value parse(final byte[] bytes) {
        return new DPT7Value(this, bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT7Value parse(final String[] args) {
        return new DPT7Value(this, Integer.parseInt(args[0]));
    }

    public DPT7Value of(final int value) {
        return new DPT7Value(this, value);
    }

    public byte[] toByteArray(final int value) {
        return DPT7Value.toByteArray(value);
    }
}
