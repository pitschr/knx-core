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
import li.pitschmann.knx.core.datapoint.value.DPT5Value;

import java.util.function.Function;

/**
 * Data Point Type 5 for 'Unsigned Value' (8 Bits)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Unsigned Value)              |
 * Encoding    | U   U   U   U   U   U   U   U |
 *             +---+---+---+---+---+---+---+---+
 * Format:     8 bit (U<sub>8</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT5 extends AbstractRangeDataPointType<DPT5Value, Integer> {
    /**
     * <strong>5.001</strong> Scaling (%)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)              |
     * Encoding    | U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (U<sub>8</sub>)
     * Range:      U = [0 .. 255]
     * Unit:       %
     * Resolution: ~ 0.4%
     *
     * The amount of intervals into which the range of 0-100% is subdivided, or the break indication.
     * </pre>
     */
    @DataPoint({"5.001", "dpst-5-1"})
    public static final DPT5 SCALING = new DPT5("Scaling", 0, 100, "%", v -> v * 100d / 255d);
    /**
     * <strong>5.003</strong> Angle (°)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)              |
     * Encoding    | U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (U<sub>8</sub>)
     * Range:      U = [0 .. 255]
     * Unit:       %
     * Resolution: ~ 1.4%
     *
     * The amount of intervals into which the range of 0-100% is subdivided, or the break indication.
     * </pre>
     */
    @DataPoint({"5.003", "dpst-5-3"})
    public static final DPT5 ANGLE = new DPT5("Angle", 0, 360, "°", v -> v * 360d / 255d);
    /**
     * <strong>5.004</strong> Percent 8-bit (%)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)              |
     * Encoding    | U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (U<sub>8</sub>)
     * Range:      U = [0 .. 255]
     * Unit:       %
     * Resolution: ~ 1%
     *
     * The amount of intervals into which the range of 0-100% is subdivided, or the break indication.
     * </pre>
     */
    @DataPoint({"5.004", "dpst-5-4"})
    public static final DPT5 PERCENT_U8 = new DPT5("Percent 8-bit", 0, 255, "%", null);
    /**
     * <strong>5.005</strong> Decimal Factor (%)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)              |
     * Encoding    | U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (U<sub>8</sub>)
     * Range:      U = [0 .. 255]
     * Unit:       ratio
     * Resolution: N/A
     * Use Type:   N/A
     *
     * The amount of intervals into which the range of 0-100% is subdivided, or the break indication.
     * </pre>
     */
    @DataPoint({"5.005", "dpst-5-5"})
    public static final DPT5 DECIMAL_FACTOR = new DPT5("Decimal Factor", 0, 255, "ratio", null);
    /**
     * <strong>5.006</strong> Tariff Information
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)              |
     * Encoding    | U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (U<sub>8</sub>)
     * Range:      U = [0 .. 254]
     *               0         = no tariff available
     *               1 .. 254 = current or desired value
     *               255       = (reserved)
     * Unit:       N/A
     * Resolution: N/A
     *
     * This DPT shall be used for reading and setting tariff information.
     * </pre>
     */
    @DataPoint({"5.006", "dpst-5-6"})
    public static final DPT5 TARIFF_INFORMATION = new DPT5("Tariff Information", 0, 254, null, null);
    /**
     * <strong>5.010</strong> Value 1-octet unsigned count
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Unsigned Value)              |
     * Encoding    | U   U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (U<sub>8</sub>)
     * Range:      U = [0 .. 255]
     * Unit:       pulses
     * Resolution: 1 pulse
     * </pre>
     */
    @DataPoint({"5.010", "dpt-5", "dpst-5-10"})
    public static final DPT5 VALUE_1_OCTET_UNSIGNED_COUNT = new DPT5("Value 1-Octet Unsigned Count", 0, 255, "pulses", null);
    /**
     * Calculation function
     * <p>
     * Calculates from {@link Integer} to {@link Float} using a formula
     */
    private final Function<Integer, Double> calculationFunction;

    /**
     * Constructor for {@link DPT5}
     *
     * @param desc                description for {@link DPT5}
     * @param lowerValue          the lower value for {@link DPT5}
     * @param upperValue          the upper value for {@link DPT5}
     * @param unit                the unit representation for {@link DPT5}
     * @param calculationFunction the calculation function for value representation
     */
    private DPT5(final String desc,
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
        return bytes.length == 1;
    }

    @Override
    protected DPT5Value parse(final byte[] bytes) {
        return new DPT5Value(this, bytes[0]);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT5Value parse(final String[] args) {
        return new DPT5Value(this, Integer.parseInt(args[0]));
    }

    public DPT5Value of(final byte value) {
        return of(new byte[]{value});
    }

    public DPT5Value of(final int value) {
        return new DPT5Value(this, value);
    }

    public byte[] toByteArray(final int value) {
        return DPT5Value.toByteArray(value);
    }
}
