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

package li.pitschmann.knx.core.datapoint.value;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.DPT7;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT7} (7.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Unsigned Value)                                              |
 * Encoding     | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     2 octets (U<sub>16</sub>)
 * Range:      U = [0 .. 65535]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT7Value extends AbstractDataPointValue<DPT7> {
    private final int value;

    public DPT7Value(final DPT7 dpt, final byte[] bytes) {
        this(dpt, new BigInteger(1, bytes).intValue());
    }

    public DPT7Value(final DPT7 dpt, final int value) {
        super(dpt);
        if (!getDPT().isRangeClosed(value)) {
            throw new KnxNumberOutOfRangeException("value", getDPT().getLowerValue(), getDPT().getUpperValue(), value);
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public byte[] toByteArray() {
        final var calcFunction = this.getDPT().getCalculationFunction();
        final int newValue;
        if (calcFunction == null) {
            newValue = value;
        } else {
            newValue = (int) Math.round(calcFunction.applyAsDouble(value));
        }
        return new byte[]{(byte) (newValue >>> 8), (byte) newValue};
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
        } else if (obj instanceof DPT7Value) {
            final var other = (DPT7Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) && Objects.equals(this.value, other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), value);
    }
}
