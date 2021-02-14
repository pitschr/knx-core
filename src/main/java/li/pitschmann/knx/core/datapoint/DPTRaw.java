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

import li.pitschmann.knx.core.datapoint.value.DPTRawValue;
import li.pitschmann.knx.core.utils.Bytes;

/**
 * Data Point Type for raw values
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Byte Array)                                                  |
 * Encoding     | B   B   B   B   B   B   B   B   B   B   B   B   B   B   B   B |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              |                           . . . . .                           |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | B   B   B   B   B   B   B   B   B   B   B   B   B   B   B   B |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:      Byte Array
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPTRaw extends BaseDataPointType<DPTRawValue> {
    /**
     * <strong>raw</strong> Raw Value
     *
     * <pre>
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names  | (Byte Array)                                                  |
     * Encoding     | B   B   B   B   B   B   B   B   B   B   B   B   B   B   B   B |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              |                           . . . . .                           |
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *              | B   B   B   B   B   B   B   B   B   B   B   B   B   B   B   B |
     *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:      Byte Array
     * </pre>
     */
    @DataPoint({"raw"})
    public static final DPTRaw VALUE = new DPTRaw();

    /**
     * Constructor for {@link DPTRaw}
     */
    private DPTRaw() {
        super("Raw Value", null);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes != null;
    }

    @Override
    protected DPTRawValue parse(final byte[] bytes) {
        return new DPTRawValue(bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPTRawValue parse(final String[] args) {
        return of(Bytes.toByteArray(args[0]));
    }
}
