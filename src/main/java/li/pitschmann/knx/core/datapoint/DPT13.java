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
import li.pitschmann.knx.core.datapoint.value.DPT13Value;

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
public final class DPT13 extends BaseRangeDataPointType<DPT13Value, Integer> {
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
    @DataPoint({"13.001", "dpt-13", "dpst-13-1"})
    public static final DPT13 VALUE_4_OCTET_COUNT = new DPT13("Value 4-Octet Signed Count", -2147483648, 2147483647, "pulses");

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
    @DataPoint({"13.002", "dpst-13-2"})
    public static final DPT13 FLOW_RATE = new DPT13("Flow Rate", -2147483648, 2147483647, "m³/h");
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
    @DataPoint({"13.010", "dpst-13-10"})
    public static final DPT13 ACTIVE_ENERGY = new DPT13("Active Energy", -2147483648, 2147483647, "Wh");

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
    @DataPoint({"13.011", "dpst-13-11"})
    public static final DPT13 APPARANT_ENERGY = new DPT13("Apparant Energy", -2147483648, 2147483647, "VAh");

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
    @DataPoint({"13.012", "dpst-13-12"})
    public static final DPT13 REACTIVE_ENERGY = new DPT13("Reactive Energy", -2147483648, 2147483647, "VARh");

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
    @DataPoint({"13.013", "dpst-13-13"})
    public static final DPT13 ACTIVE_ENERGY_KWH = new DPT13("Active Energy", -2147483648, 2147483647, "kWh");

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
    @DataPoint({"13.014", "dpst-13-14"})
    public static final DPT13 APPARANT_ENERGY_KVAH = new DPT13("Apparant Energy", -2147483648, 2147483647, "kVAh");

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
    @DataPoint({"13.015", "dpst-13-15"})
    public static final DPT13 REACTIVE_ENERGY_KVARH = new DPT13("Reactive Energy", -2147483648, 2147483647, "kVARh");

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
    @DataPoint({"13.016", "dpst-13-16"})
    public static final DPT13 ACTIVE_ENERGY_MWH = new DPT13("Active Energy", -2147483648, 2147483647, "MWh");

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
    @DataPoint({"13.100", "dpst-13-100"})
    public static final DPT13 DELTA_TIME_SECONDS = new DPT13("Long Delta Time", -2147483648, 2147483647, "s");

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
    @DataPoint({"13.1200", "dpst-13-1200"})
    public static final DPT13 DELTA_VOLUME_L = new DPT13("Delta Volume", -2147483648, 2147483647, "l");

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
    @DataPoint({"13.1201", "dpst-13-1201"})
    public static final DPT13 DELTA_VOLUME_M3 = new DPT13("Delta Volume", -2147483648, 2147483647, "m³");

    /**
     * Constructor for {@link DPT13}
     *
     * @param desc                description for {@link DPT13}
     * @param lowerValue          the lower value for {@link DPT13}
     * @param upperValue          the upper value for {@link DPT13}
     * @param unit                the unit representation for {@link DPT13}
     */
    private DPT13(final String desc,
                  final int lowerValue,
                  final int upperValue,
                  final @Nullable String unit) {
        super(desc, lowerValue, upperValue, unit);
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
        return of(Integer.parseInt(args[0]));
    }

    public DPT13Value of(final int value) {
        return new DPT13Value(this, value);
    }
}
