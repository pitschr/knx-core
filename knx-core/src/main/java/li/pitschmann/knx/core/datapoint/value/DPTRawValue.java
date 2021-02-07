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

package li.pitschmann.knx.core.datapoint.value;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.DPTRaw;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * Data Point Value for {@link DPTRaw}
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
public final class DPTRawValue extends AbstractDataPointValue<DPTRaw> {
    private final byte[] bytes;

    public DPTRawValue(final byte[] bytes) {
        super(DPTRaw.VALUE);

        this.bytes = bytes.clone();
    }

    @Override
    public byte[] toByteArray() {
        return this.bytes.clone();
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPTRawValue) {
            final var other = (DPTRawValue) obj;
            return Arrays.equals(this.bytes, other.bytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
