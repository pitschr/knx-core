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
import li.pitschmann.knx.core.datapoint.DPT29;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT29} (29.xxx)
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
 * Range:      U = [-9 223 372 036 854 775 808 .. 9 223 372 036 854 775 807]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT29Value extends AbstractDataPointValue<DPT29> {
    private final long value;

    public DPT29Value(final DPT29 dpt, final byte[] bytes) {
        this(dpt, new BigInteger(bytes).longValue());
    }

    public DPT29Value(final DPT29 dpt, final long value) {
        super(dpt);

        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[]{ //
                (byte) (value >>> 56), //
                (byte) (value >>> 48), //
                (byte) (value >>> 40), //
                (byte) (value >>> 32), //
                (byte) (value >>> 24), //
                (byte) (value >>> 16), //
                (byte) (value >>> 8), //
                (byte) value //
        };
    }

    @Override
    public String toText() {
        return getValueAsText(value);
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT().getId())
                .add("value", value)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT29Value) {
            final var other = (DPT29Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.value, other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), value);
    }
}
