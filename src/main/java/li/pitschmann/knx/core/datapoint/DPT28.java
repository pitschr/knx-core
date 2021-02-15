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

import li.pitschmann.knx.core.datapoint.value.DPT28Value;

/**
 * Data Point Type 28 for 'UTF-8 Text'
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
 * This Datapoint Type shall be used to transmit Unicode strings, whereas the UTF-8
 * encoding scheme shall be used for Unicode Transformation to data contents for transmission.
 * <p>
 * Using UTF-8 the data length for a string (multiple characters) is also not fixed, but variable.
 * The string shall be terminated by the NULL- character (00h).
 * <p>
 * Example:<br>
 * "ABC 123 äöü 子老何" is encoded as follows: "0x41 42 43 20 31 32 33 20 C3 A4 C3 B6 C3 BC 20 E5 AD 90 E8 80 81 E4 BD 95 00"<br>
 * "0x20" for space character<br>
 * "0x00" for termination / null-character
 * "0x41 42 43" for "ABC"<br>
 * "0x31 32 33" for "123"<br>
 * "0xC3 A4 C3 B6 C3 BC" for "äöü"
 * "0xE5 AD 90 E8 80 81 E4 BD 95" for "子老何"
 *
 * @author PITSCHR
 */
public final class DPT28 extends BaseDataPointType<DPT28Value> {
    /**
     * <strong>28.001</strong> UTF-8 Text
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
    @DataPoint({"28.001", "dpt-28", "dpst-28-1"})
    public static final DPT28 UTF_8 = new DPT28("UTF-8 Characters");

    /**
     * Constructor for {@link DPT28}
     *
     * @param desc description for {@link DPT28}
     */
    private DPT28(final String desc) {
        super(desc);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return true;
    }

    @Override
    protected DPT28Value parse(final byte[] bytes) {
        return new DPT28Value(bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT28Value parse(final String[] args) {
        return of(args[0]);
    }

    public DPT28Value of(final String text) {
        return new DPT28Value(text);
    }
}
