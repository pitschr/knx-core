/*
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

package li.pitschmann.knx.core.datapoint.value;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.DPT12;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT12} (12.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Unsigned Value)                                              |
 * Encoding     | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Unsigned Value)                                              |
 *              | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     4 octets (U<sub>32</sub>)
 * Range:      U = [0 .. 4294967295]
 * Unit:       pulses
 * Resolution: 1 pulse
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT12Value extends AbstractDataPointValue<DPT12> {
    private final long value;

    public DPT12Value(final DPT12 dpt, final byte[] bytes) {
        this(dpt, new BigInteger(1, bytes).longValue());
    }

    public DPT12Value(final DPT12 dpt, final long value) {
        super(dpt);
        if (!getDPT().isRangeClosed(value)) {
            throw new KnxNumberOutOfRangeException("value", getDPT().getLowerValue(), getDPT().getUpperValue(), value);
        }
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[]{ //
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
                .add("dpt", getDPT().getId())
                .add("value", value)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT12Value) {
            final var other = (DPT12Value) obj;
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
