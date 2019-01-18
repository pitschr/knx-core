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

import li.pitschmann.knx.link.datapoint.annotation.*;
import li.pitschmann.knx.link.datapoint.value.*;

import java.util.regex.*;

/**
 * Data Point Type 18 for 'Scene Control' (1 Octet)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | c   0   (Scene Number)        |
 * Encoding    | B   r   U   U   U   U   U   U |
 *             +---+---+---+---+---+---+---+---+
 * Format:     1 octet (B<sub>1</sub> r<sub>1</sub> U<sub>6</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT18 extends AbstractRangeUnitDataPointType<DPT18Value, Integer> {
    /**
     * <strong>18.001</strong> Scene Control
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | c   0   (Scene Number)        |
     * Encoding    | B   r   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 octet (B<sub>1</sub> r<sub>1</sub> U<sub>6</sub>)
     * Range:      c = {0, 1}
     *                  0 = activate the scene corresponding to the field Scene Number
     *                  1 = learn the scene corresponding to the field Scene Number
     *             U = [0 .. 63]
     * </pre>
     */
    @KnxDataPointType(id = "18.001", description = "Scene Control")
    public static final DPT18 SCENE_CONTROL = new DPT18("18.001", "Scene Control");

    /**
     * Constructor for {@link DPT18}
     *
     * @param id
     * @param desc
     */
    private DPT18(final String id, final String desc) {
        super(id, desc, 0, 63, null);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 1;
    }

    @Override
    protected DPT18Value parse(final byte[] bytes) {
        return new DPT18Value(bytes[0]);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1 || args.length == 2;
    }

    @Override
    protected DPT18Value parse(final String[] args) {
        boolean controlled = this.findByString(args, "controlled");
        int intValue = this.findByPattern(args, Pattern.compile("^[\\d]+$"), Integer::valueOf);

        return new DPT18Value(controlled, intValue);
    }

    public DPT18Value toValue(final boolean controlled, final int sceneNumber) {
        return new DPT18Value(controlled, sceneNumber);
    }

    public byte[] toByteArray(final boolean controlled, final int sceneNumber) {
        return DPT18Value.toByteArray(controlled, sceneNumber);
    }
}
