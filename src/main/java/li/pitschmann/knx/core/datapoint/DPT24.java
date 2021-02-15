/*
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

import li.pitschmann.knx.core.datapoint.value.DPT24Value;

/**
 * Data Point Type 24 for 'ISO-8859-1 Text'
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Character 1)                   (Character 2)                 |
 * Encoding     | A   A   A   A   A   A   A   A   A   A   A   A   A   A   A   A |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Character 3)                   (Character 4)                 |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              |                    ... variable length ...                    |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Character N)                   (Terminated by NULL (0x00)    |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:      N octets + 1 NULL-character termination (A<sub>N</sub>B<sub>1</sub>)
 * </pre>
 * <p>
 * This Datapoint Type shall be used to transmit strings of textual characters.
 * The length is not fixed, but variable; the string shall be terminated by a
 * single character NULL (00h).
 * <p>
 * Example:<br>
 * "KNX is OK" is encoded as follows: "0x4B 4E 58 20 69 73 20 4F 4B 00"<br>
 * "0x20" for space character<br>
 * "0x00" for termination / null-character
 * "0x4B 4E 58" for "KNX"<br>
 * "0x69 73" for "is"<br>
 * "0x4F 4B" for "OK"
 *
 * @author PITSCHR
 */
public final class DPT24 extends BaseDataPointType<DPT24Value> {
    /**
     * <strong>24.001</strong> ISO-8859-1 Text
     *
     * <pre>
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names  | (Character 1)                   (Character 2)                 |
     * Encoding     | A   A   A   A   A   A   A   A   A   A   A   A   A   A   A   A |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | (Character 3)                   (Character 4)                 |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              |                    ... variable length ...                    |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | (Character N)                   (Terminated by NULL (0x00)    |
     *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:      N octets + 1 NULL-character termination (A<sub>N</sub>B<sub>1</sub>)
     * </pre>
     */
    @DataPoint({"24.001", "dpt-24", "dpst-24-1"})
    public static final DPT24 ISO_8859_1 = new DPT24("ISO-8859-1 Characters");

    /**
     * Constructor for {@link DPT24}
     *
     * @param desc description for {@link DPT24}
     */
    private DPT24(final String desc) {
        super(desc);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return true;
    }

    @Override
    protected DPT24Value parse(final byte[] bytes) {
        return new DPT24Value(bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT24Value parse(final String[] args) {
        return of(args[0]);
    }

    public DPT24Value of(final String text) {
        return new DPT24Value(text);
    }
}
