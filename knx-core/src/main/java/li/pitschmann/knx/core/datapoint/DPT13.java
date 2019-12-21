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
import li.pitschmann.knx.core.datapoint.annotation.KnxDataPointType;
import li.pitschmann.knx.core.datapoint.value.DPT13Value;

import java.util.function.Function;

/**
 * Data Point Type 13 for '4-Octet Signed Value' (4 Octets)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Signed Value)                                                |
 * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | (Signed Value)                                                |
 *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
 *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     4 octets (V<sub>32</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT13 extends AbstractRangeDataPointType<DPT13Value, Integer> {
    /**
     * <strong>13.001</strong> Value 4-Octet Signed Count
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       pulses
     * Resolution: 1 pulse
     * </pre>
     */
    @KnxDataPointType(id = "13.001", description = "Value 4-Octet Signed Count")
    public static final DPT13 VALUE_4_OCTET_COUNT = new DPT13("13.001", "Value 4-Octet Signed Count", -2147483648, 2147483647, "pulses");
    /**
     * <strong>13.002</strong> Flow Rate (m<sup>3</sup>/h) with high resolution
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       m<sup>3</sup>/h
     * Resolution: 0.0001 m<sup>3</sup>/h
     * </pre>
     */
    @KnxDataPointType(id = "13.002", description = "Flow Rate")
    public static final DPT13 FLOW_RATE = new DPT13("13.002", "Flow Rate", -2147483648, 2147483647, "m³/h", v -> v / 10000d);
    /**
     * <strong>13.010</strong> Active Energy (Wh)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       Wh
     * Resolution: 1 Wh
     * </pre>
     */
    @KnxDataPointType(id = "13.010", description = "Active Energy (Wh)")
    public static final DPT13 ACTIVE_ENERGY = new DPT13("13.010", "Active Energy (Wh)", -2147483648, 2147483647, "Wh");
    /**
     * <strong>13.011</strong> Apparant Energy (VAh)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       VAh
     * Resolution: 1 VAh
     * </pre>
     */
    @KnxDataPointType(id = "13.011", description = "Apparant Energy")
    public static final DPT13 APPARANT_ENERGY = new DPT13("13.011", "Apparant Energy", -2147483648, 2147483647, "VAh");
    /**
     * <strong>13.012</strong> Reactive Energy (VARh)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       VARh
     * Resolution: 1 VARh
     * </pre>
     */
    @KnxDataPointType(id = "13.012", description = "Reactive Energy")
    public static final DPT13 REACTIVE_ENERGY = new DPT13("13.012", "Reactive Energy", -2147483648, 2147483647, "VARh");
    /**
     * <strong>13.013</strong> Active Energy (kWh)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       kWh
     * Resolution: 1 kWh
     * </pre>
     */
    @KnxDataPointType(id = "13.013", description = "Active Energy (kWh)")
    public static final DPT13 ACTIVE_ENERGY_KWH = new DPT13("13.013", "Active Energy (kWh)", -2147483648, 2147483647, "kWh");
    /**
     * <strong>13.014</strong> Apparant Energy (kVAh)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       kVAh
     * Resolution: 1 kVAh
     * </pre>
     */
    @KnxDataPointType(id = "13.014", description = "Apparant Energy")
    public static final DPT13 APPARANT_ENERGY_KVAH = new DPT13("13.014", "Apparant Energy", -2147483648, 2147483647, "kVAh");
    /**
     * <strong>13.015</strong> Reactive Energy (kVARh)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       kVARh
     * Resolution: 1 kVARh
     * </pre>
     */
    @KnxDataPointType(id = "13.015", description = "Reactive Energy")
    public static final DPT13 REACTIVE_ENERGY_KVARH = new DPT13("13.015", "Reactive Energy", -2147483648, 2147483647, "kVARh");

    /**
     * <strong>13.016</strong> Active Energy (MWh)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       MWh
     * Resolution: 1 MWh
     * </pre>
     */
    @KnxDataPointType(id = "13.016", description = "Active Energy (MWh)")
    public static final DPT13 ACTIVE_ENERGY_MWH = new DPT13("13.016", "Active Energy (MWh)", -2147483648, 2147483647, "MWh");

    /**
     * <strong>13.100</strong> Long Delta Time (s)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       s
     * Resolution: 1 s
     * </pre>
     */
    @KnxDataPointType(id = "13.100", description = "Long Delta Time")
    public static final DPT13 LONG_DELTA_TIME_SEC = new DPT13("13.100", "Long Delta Time", -2147483648, 2147483647, "s");

    /**
     * <strong>13.1200</strong> Delta Volume (l)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       l
     * Resolution: 1 l
     * </pre>
     */
    @KnxDataPointType(id = "13.1200", description = "Delta Volume")
    public static final DPT13 DELTA_VOLUME_L = new DPT13("13.1200", "Delta Volume", -2147483648, 2147483647, "l");

    /**
     * <strong>13.1201</strong> Delta Volume (m<sup>3</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Signed Value)                                                |
     * Encoding    | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Signed Value)                                                |
     *             | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (V<sub>32</sub>)
     * Range:      V = [-2147483648 .. 2147483647]
     * Unit:       m<sup>3</sup>
     * Resolution: 1 m<sup>3</sup>
     * </pre>
     */
    @KnxDataPointType(id = "13.1201", description = "Delta Volume")
    public static final DPT13 DELTA_VOLUME_M3 = new DPT13("13.1201", "Delta Volume", -2147483648, 2147483647, "m³");

    /**
     * Calculation function
     * <p>
     * Calculates from {@link Integer} to {@link Double} using a formula
     */
    private final Function<Integer, Double> calculationFunction;

    /**
     * Constructor for {@link DPT13}
     *
     * @param id         identifier for {@link DPT13}
     * @param desc       description for {@link DPT13}
     * @param lowerValue the lower value for {@link DPT13}
     * @param upperValue the upper value for {@link DPT13}
     * @param unit       the unit representation for {@link DPT13}
     */
    private DPT13(final String id,
                  final String desc,
                  final int lowerValue,
                  final int upperValue,
                  final @Nullable String unit) {
        this(id, desc, lowerValue, upperValue, unit, null);
    }

    /**
     * Constructor for {@link DPT13}
     *
     * @param id                  identifier for {@link DPT13}
     * @param desc                description for {@link DPT13}
     * @param lowerValue          the lower value for {@link DPT13}
     * @param upperValue          the upper value for {@link DPT13}
     * @param unit                the unit representation for {@link DPT13}
     * @param calculationFunction the calculation function for value representation
     */
    private DPT13(final String id,
                  final String desc,
                  final int lowerValue,
                  final int upperValue,
                  final @Nullable String unit,
                  final @Nullable Function<Integer, Double> calculationFunction) {
        super(id, desc, lowerValue, upperValue, unit);
        this.calculationFunction = calculationFunction;
    }

    @Nullable
    public Function<Integer, Double> getCalculationFunction() {
        return this.calculationFunction;
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 4;
    }

    @Override
    protected DPT13Value parse(final byte[] bytes) {
        return new DPT13Value(this, bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT13Value parse(final String[] args) {
        return new DPT13Value(this, Integer.parseInt(args[0]));
    }

    public DPT13Value toValue(final int value) {
        return new DPT13Value(this, value);
    }

    public byte[] toByteArray(final int value) {
        return DPT13Value.toByteArray(value);
    }
}
