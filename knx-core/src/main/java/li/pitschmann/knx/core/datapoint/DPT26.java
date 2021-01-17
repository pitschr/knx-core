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

import li.pitschmann.knx.core.datapoint.value.DPT26Value;

import java.util.regex.Pattern;

/**
 * Data Point Type 26 for 'Scene Information' (1 Octet)
 *
 * <pre>
 *             +-7-+-6-------+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | 0   (Active) (Scene Number)         |
 * Encoding    | r   B        U   U   U   U   U   U  |
 *             +---+---------+---+---+---+---+---+---+
 * Format:     1 octet (r<sub>1</sub> B<sub>1</sub> U<sub>6</sub>)
 * Active:     B = {0 = inactive, 1 = active}
 * Scene Nr:   U = [0 .. 63]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT26 extends BaseRangeDataPointType<DPT26Value, Integer> {
    /**
     * <strong>26.001</strong> Scene Number
     *
     * <pre>
     *             +-7-+-6-------+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   (Active) (Scene Number)         |
     * Encoding    | r   B        U   U   U   U   U   U  |
     *             +---+---------+---+---+---+---+---+---+
     * Format:     1 octet (r<sub>1</sub> B<sub>1</sub> U<sub>6</sub>)
     * Active:     B = {0 = inactive, 1 = active}
     * Scene Nr:   U = [0 .. 63]
     * </pre>
     */
    @DataPoint({"26.001", "dpt-26", "dpst-26-1"})
    public static final DPT26 SCENE_INFORMATION = new DPT26("Scene Information");

    /**
     * Constructor for {@link DPT26}
     *
     * @param desc description for {@link DPT26}
     */
    private DPT26(final String desc) {
        super(desc, 0, 63, null);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 1;
    }

    @Override
    protected DPT26Value parse(final byte[] bytes) {
        return new DPT26Value(bytes[0]);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        // valid cases:
        // 47           --> scene active: false
        // active 47    --> scene active: true
        // inactive 47  --> scene active: false
        // true 47      --> scene active: true
        // false 47     --> scene active: false
        return args.length == 1 || args.length == 2;
    }

    @Override
    protected DPT26Value parse(final String[] args) {
        final var sceneActive = this.findByString(args, "active", "true");
        final var sceneNumber = this.findByPattern(args, Pattern.compile("^[\\d]+$"), Integer::valueOf);
        return of(sceneActive, sceneNumber);
    }

    public DPT26Value of(final boolean active, final int sceneNumber) {
        return new DPT26Value(active, sceneNumber);
    }
}
