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
import li.pitschmann.knx.link.datapoint.value.DPT8Value;

import java.util.function.Function;

/**
 * Data Point Type 8 for '2-Octet Signed Value' (2 Octets)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Signed Value)                                                |
 * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     2 octets (V<sub>16</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT8 extends AbstractRangeUnitDataPointType<DPT8Value, Integer> {
    /**
     * <strong>8.001</strong> Value 2-octet signed count
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (V<sub>16</sub>)
     * Range:      U = [-32768 .. 32767]
     * Unit:       pulses
     * Resolution: 1 pulse
     * </pre>
     */
    @KnxDataPointType(id = "8.001", description = "Value 2-Octet Signed Count")
    public static final DPT8 VALUE_2_OCTET_COUNT = new DPT8("8.001", "Value 2-Octet Signed Count", -32768, 32767, "pulses");

    /**
     * <strong>8.002</strong> Delta Time milliseconds
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Delta Time)                                                  |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (V<sub>16</sub>)
     * Range:      U = [-32768 .. 32767]
     *                  -32768ms .. 32767ms
     * Unit:       ms
     * Resolution: ms
     * </pre>
     */
    @KnxDataPointType(id = "8.002", description = "Delta Time (1ms)")
    public static final DPT8 DELTA_TIME_MS = new DPT8("8.002", "Delta Time (1ms)", -32768, 32767, "ms");

    /**
     * <strong>8.003</strong> Delta Time (milliseconds, resolution 10ms)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Delta Time)                                                  |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (V<sub>16</sub>)
     * Range:      U = [-32768 .. 32767]
     *                  -327.68ms .. 327.67ms
     * Unit:       ms
     * Resolution: 10 ms
     * </pre>
     */
    @KnxDataPointType(id = "8.003", description = "Delta Time (10ms)")
    public static final DPT8 DELTA_TIME_10MS = new DPT8("8.003", "Delta Time (10ms)", -32768, 32767, "ms", v -> v / 100d);

    /**
     * <strong>8.004</strong> Delta Time (milliseconds, resolution 100ms)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Delta Time)                                                  |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (V<sub>16</sub>)
     * Range:      U = [-32768 .. 32767]
     *                  -3276.8ms .. -3276.7ms
     * Unit:       ms
     * Resolution: 100 ms
     * </pre>
     */
    @KnxDataPointType(id = "8.004", description = "Delta Time (100ms)")
    public static final DPT8 DELTA_TIME_100MS = new DPT8("8.004", "Delta Time (100ms)", -32768, 32767, "ms", v -> v / 10d);

    /**
     * <strong>8.005</strong> Delta Time (seconds)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Delta Time)                                                  |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (V<sub>16</sub>)
     * Range:      U = [-32768 .. 32767]
     * Unit:       s
     * Resolution: 1 s
     * </pre>
     */
    @KnxDataPointType(id = "8.005", description = "Delta Time (s)")
    public static final DPT8 DELTA_TIME_SECONDS = new DPT8("8.005", "Delta Time (s)", -32768, 32767, "s");

    /**
     * <strong>8.006</strong> Delta Time (minutes)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Delta Time)                                                  |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (V<sub>16</sub>)
     * Range:      U = [-32768 .. 32767]
     * Unit:       min
     * Resolution: 1 min
     * </pre>
     */
    @KnxDataPointType(id = "8.006", description = "Delta Time (min)")
    public static final DPT8 DELTA_TIME_MINUTES = new DPT8("8.006", "Delta Time (min)", -32768, 32767, "min");

    /**
     * <strong>8.007</strong> Delta Time (hours)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Delta Time)                                                  |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (V<sub>16</sub>)
     * Range:      U = [-32768 .. 32767]
     * Unit:       h
     * Resolution: 1 h
     * </pre>
     */
    @KnxDataPointType(id = "8.007", description = "Delta Time (h)")
    public static final DPT8 DELTA_TIME_HOURS = new DPT8("8.007", "Delta Time (h)", -32768, 32767, "h");

    /**
     * <strong>8.010</strong> Percent (V<sup>16</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (V<sub>16</sub>)
     * Range:      U = [-327,68% .. 327,67%]
     * Unit:       %
     * Resolution: 0.01 %
     * </pre>
     */
    @KnxDataPointType(id = "8.010", description = "Percent")
    public static final DPT8 PERCENT = new DPT8("8.010", "Percent", -32768, 32767, "%", v -> v / 100d);

    /**
     * <strong>8.011</strong> Rotation Angle (°)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Rotation Angle)                                              |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (V<sub>16</sub>)
     * Range:      U = [-32768 .. 32767]
     * Unit:       °
     * Resolution: 1 °
     * </pre>
     * <p>
     * Used for absolute control, slats position in degrees for Shutters and Blinds.
     */
    @KnxDataPointType(id = "8.011", description = "Rotation Angle")
    public static final DPT8 ROTATION_ANGLE = new DPT8("8.011", "Rotation Angle", -32768, 32767, "°");

    /**
     * <strong>8.012</strong> Length / Altitude Above Sea Level (m)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Rotation Angle)                                              |
     * Encoding    | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (V<sub>16</sub>)
     * Range:      U = [-32768 .. 32767]
     * Unit:       m
     * Resolution: 1 m
     * </pre>
     * <p>
     * This DPT shall be used to encode information about the altitude above sea level. Since altitude may be negative,
     * signed value type is needed.
     */
    @KnxDataPointType(id = "8.012", description = "Length / Altitude Above Sea Level")
    public static final DPT8 LENGTH_IN_METER = new DPT8("8.012", "Length / Altitude Above Sea Level", -32768, 32767, "m");

    /**
     * Calculation function
     * <p>
     * Calculates from {@link Integer} to {@link Double} using a formula
     */
    private final Function<Integer, Double> calculationFunction;

    /**
     * Constructor for {@link DPT8}
     *
     * @param id
     * @param desc
     * @param lowerValue
     * @param upperValue
     * @param unit
     */
    private DPT8(final String id, final String desc, final int lowerValue, final int upperValue, final String unit) {
        this(id, desc, lowerValue, upperValue, unit, null);
    }

    /**
     * Constructor for {@link DPT8}
     *
     * @param id
     * @param desc
     * @param lowerValue
     * @param upperValue
     * @param unit
     * @param calculationFunction
     */
    private DPT8(final String id, final String desc, final int lowerValue, final int upperValue, final String unit,
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
    protected DPT8Value parse(final byte[] bytes) {
        return new DPT8Value(this, bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT8Value parse(final String[] args) {
        return new DPT8Value(this, Integer.parseInt(args[0]));
    }

    public DPT8Value toValue(final int value) {
        return new DPT8Value(this, value);
    }

    public byte[] toByteArray(final int value) {
        return DPT8Value.toByteArray(value);
    }
}
