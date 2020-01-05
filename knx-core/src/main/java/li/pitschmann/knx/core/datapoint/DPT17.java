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

import li.pitschmann.knx.core.datapoint.value.DPT17Value;

/**
 * Data Point Type 17 for 'Scene Number' (1 Octet)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | 0   0   (Scene Number)        |
 * Encoding    | r   r   U   U   U   U   U   U |
 *             +---+---+---+---+---+---+---+---+
 * Format:     1 octet (r<sub>2</sub> U<sub>6</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT17 extends AbstractRangeDataPointType<DPT17Value, Integer> {
    /**
     * <strong>17.001</strong> Scene Number
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   (Scene Number)        |
     * Encoding    | r   r   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 octet (r<sub>2</sub> U<sub>6</sub>)
     * Range:      U = [0 .. 63]
     * </pre>
     */
    @DataPoint({"17.001", "dpt-17", "dpst-17-1"})
    public static final DPT17 SCENE_NUMBER = new DPT17("Scene Number");

    /**
     * Constructor for {@link DPT17}
     *
     * @param desc description for {@link DPT17}
     */
    private DPT17(final String desc) {
        super(desc, 0, 63, null);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 1;
    }

    @Override
    protected DPT17Value parse(final byte[] bytes) {
        return new DPT17Value(bytes[0]);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT17Value parse(final String[] args) {
        return new DPT17Value(Integer.parseInt(args[0]));
    }

    public DPT17Value of(final int sceneNumber) {
        return new DPT17Value(sceneNumber);
    }

    public byte[] toByteArray(final int sceneNumber) {
        return DPT17Value.toByteArray(sceneNumber);
    }
}
