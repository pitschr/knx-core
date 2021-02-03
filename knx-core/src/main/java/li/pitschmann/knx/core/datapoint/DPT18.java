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

import li.pitschmann.knx.core.datapoint.value.DPT18Value;
import li.pitschmann.knx.core.utils.Preconditions;

import java.util.Arrays;
import java.util.regex.Pattern;

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
public final class DPT18 extends BaseRangeDataPointType<DPT18Value, Integer> {
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
    @DataPoint({"18.001", "dpt-18", "dpst-18-1"})
    public static final DPT18 SCENE_CONTROL = new DPT18("Scene Control");

    /**
     * Constructor for {@link DPT18}
     *
     * @param desc description for {@link DPT18}
     */
    private DPT18(final String desc) {
        super(desc, 0, 63, null);
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
        final var controlled = containsString(args, "controlled");
        final var intValue = findByPattern(args, Pattern.compile("^[\\d]+$"), Integer::valueOf);

        Preconditions.checkArgument(intValue != null,
                "Scene Number missing (digit between {} and {}). Provided: {}",
                getLowerValue(),
                getUpperValue(),
                Arrays.toString(args)
        );
        return of(controlled, intValue);
    }

    public DPT18Value of(final boolean controlled, final int sceneNumber) {
        return new DPT18Value(controlled, sceneNumber);
    }
}
