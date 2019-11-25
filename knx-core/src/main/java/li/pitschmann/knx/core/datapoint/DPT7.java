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

import li.pitschmann.knx.core.datapoint.annotation.KnxDataPointType;
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
public final class DPT7 extends AbstractRangeDataPointType<DPT7Value, Integer> {
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
    @KnxDataPointType(id = "7.001", description = "Value 2-Octet Unsigned Count")
    public static final DPT7 VALUE_2_OCTET_UNSIGNED_COUNT = new DPT7("7.001", "Value 2-Octet Unsigned Count", 0, 65535, "pulses");
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
    @KnxDataPointType(id = "7.002", description = "Time Period")
    public static final DPT7 TIME_PERIOD_MS = new DPT7("7.002", "Time Period", 0, 65535, "ms");
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
    @KnxDataPointType(id = "7.003", description = "Time Period 1/10")
    public static final DPT7 TIME_PERIOD_10MS = new DPT7("7.003", "Time Period 1/10", 0, 65535, "ms", v -> v / 100d);
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
    @KnxDataPointType(id = "7.004", description = "Time Period 1/100")
    public static final DPT7 TIME_PERIOD_100MS = new DPT7("7.004", "Time Period 1/100", 0, 65535, "ms", v -> v / 10d);
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
    @KnxDataPointType(id = "7.005", description = "Time Period (sec)")
    public static final DPT7 TIME_PERIOD_SECONDS = new DPT7("7.005", "Time Period (sec)", 0, 65535, "s");
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
    @KnxDataPointType(id = "7.006", description = "Time Period (min)")
    public static final DPT7 TIME_PERIOD_MINUTES = new DPT7("7.006", "Time Period (min)", 0, 65535, "min");
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
    @KnxDataPointType(id = "7.007", description = "Time Period (h)")
    public static final DPT7 TIME_PERIOD_HOURS = new DPT7("7.007", "Time Period (h)", 0, 65535, "h");
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
    @KnxDataPointType(id = "7.010", description = "Interface Object Property ID")
    public static final DPT7 PROP_DATA_TYPE = new DPT7("7.010", "Interface Object Property ID", 0, 65535, null);
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
    @KnxDataPointType(id = "7.011", description = "Length")
    public static final DPT7 LENGTH_MM = new DPT7("7.011", "Length", 0, 65535, "mm");
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
    @KnxDataPointType(id = "7.012", description = "Electrical Power")
    public static final DPT7 ELECTRIC_CURRENT_MILLIAMPERES = new DPT7("7.012", "Electrical Power", 0, 65535, "mA");
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
    @KnxDataPointType(id = "7.013", description = "Brightness")
    public static final DPT7 BRIGHTNESS = new DPT7("7.013", "Brightness", 0, 65535, "lux");

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
    @KnxDataPointType(id = "7.600", description = "Absolute Color Temperature")
    public static final DPT7 ABSOLUTE_COLOR_TEMPERATURE = new DPT7("7.600", "Absolute Color Temperature", 0, 65535, "K");
    /**
     * Calculation function
     * <p>
     * Calculates from {@link Integer} to {@link Float} using a formula
     */
    private final Function<Integer, Double> calculationFunction;

    /**
     * Constructor for {@link DPT7}
     *
     * @param id
     * @param desc
     * @param lowerValue
     * @param upperValue
     * @param unit
     */
    private DPT7(final String id, final String desc, final int lowerValue, final int upperValue, final String unit) {
        this(id, desc, lowerValue, upperValue, unit, null);
    }

    /**
     * Constructor for {@link DPT7}
     *
     * @param id
     * @param desc
     * @param lowerValue
     * @param upperValue
     * @param unit
     * @param calculationFunction
     */
    private DPT7(final String id, final String desc, final int lowerValue, final int upperValue, final String unit,
                 final Function<Integer, Double> calculationFunction) {
        super(id, desc, lowerValue, upperValue, unit);
        this.calculationFunction = calculationFunction;
    }

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

    public DPT7Value toValue(final int value) {
        return new DPT7Value(this, value);
    }

    public byte[] toByteArray(final int value) {
        return DPT7Value.toByteArray(value);
    }
}
