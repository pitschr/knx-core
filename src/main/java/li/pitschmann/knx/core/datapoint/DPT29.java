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

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.value.DPT29Value;

/**
 * Data Point Type 29 for '4-Octet Signed Value' (8 Octets)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Signed Value)                                                |
 * Encoding     | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     8 octets (V<sub>64</sub>)
 * Range:      V = [-9 223 372 036 854 775 808 .. 9 223 372 036 854 775 807]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT29 extends BaseDataPointType<DPT29Value> {
    /**
     * <strong>29.001</strong> Value 8-Octet Signed Count
     *
     * <pre>
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names  | (Signed Value)                                                |
     * Encoding     | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     8 octets (V<sub>64</sub>)
     * Range:      V = [-9 223 372 036 854 775 808 .. 9 223 372 036 854 775 807]
     * Unit:       pulses
     * Resolution: 1 pulse
     * </pre>
     */
    @DataPoint({"29.001", "dpt-29", "dpst-29-1"})
    public static final DPT29 VALUE_8_OCTET_COUNT = new DPT29("Value 8-Octet Signed Count", "pulses");

    /**
     * <strong>29.010</strong> Active Energy (Wh)
     *
     * <pre>
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names  | (Signed Value)                                                |
     * Encoding     | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     8 octets (V<sub>64</sub>)
     * Range:      V = [-9 223 372 036 854 775 808 .. 9 223 372 036 854 775 807]
     * Unit:       Wh
     * Resolution: 1 Wh
     * </pre>
     */
    @DataPoint({"29.010", "dpst-29-10"})
    public static final DPT29 ACTIVE_ENERGY = new DPT29("Active Energy", "Wh");

    /**
     * <strong>29.011</strong> Apparant Energy (VAh)
     *
     * <pre>
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names  | (Signed Value)                                                |
     * Encoding     | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     8 octets (V<sub>64</sub>)
     * Range:      V = [-9 223 372 036 854 775 808 .. 9 223 372 036 854 775 807]
     * Unit:       VAh
     * Resolution: 1 VAh
     * </pre>
     */
    @DataPoint({"29.011", "dpst-29-11"})
    public static final DPT29 APPARANT_ENERGY = new DPT29("Apparant Energy", "VAh");

    /**
     * <strong>29.012</strong> Reactive Energy (VARh)
     *
     * <pre>
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names  | (Signed Value)                                                |
     * Encoding     | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
     *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     8 octets (V<sub>64</sub>)
     * Range:      V = [-9 223 372 036 854 775 808 .. 9 223 372 036 854 775 807]
     * Unit:       VARh
     * Resolution: 1 VARh
     * </pre>
     */
    @DataPoint({"29.012", "dpst-29-12"})
    public static final DPT29 REACTIVE_ENERGY = new DPT29("Reactive Energy", "VARh");

    /**
     * Constructor for {@link DPT29}
     *
     * @param desc description for {@link DPT29}
     * @param unit the unit representation for {@link DPT29}
     */
    private DPT29(final String desc,
                  final @Nullable String unit) {
        super(desc, unit);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 8;
    }

    @Override
    protected DPT29Value parse(final byte[] bytes) {
        return new DPT29Value(this, bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT29Value parse(final String[] args) {
        return of(Long.parseLong(args[0]));
    }

    public DPT29Value of(final long value) {
        return new DPT29Value(this, value);
    }
}
