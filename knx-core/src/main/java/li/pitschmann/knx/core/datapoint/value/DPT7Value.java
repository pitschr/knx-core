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

import li.pitschmann.knx.core.datapoint.DPT7;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

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
    private final int rawUnsignedValue;
    private final byte[] byteArray;

    public DPT7Value(final DPT7 dpt, final byte[] bytes) {
        super(dpt);
        Preconditions.checkArgument(bytes.length == 2);
        // unsigned value
        this.rawUnsignedValue = Bytes.toUnsignedInt(bytes);
        this.byteArray = bytes;
    }

    public DPT7Value(final DPT7 dpt, final int value) {
        super(dpt);
        Preconditions.checkArgument(dpt.isRangeClosed(value));
        this.rawUnsignedValue = value;
        this.byteArray = toByteArray(value);
    }

    /**
     * Converts signed int value to byte array
     *
     * @param value
     * @return byte array
     */
    public static byte[] toByteArray(final int value) {
        return new byte[]{(byte) (value >>> 8), (byte) value};
    }

    public double getUnsignedValue() {
        final Function<Integer, Double> calcFunction = this.getDPT().getCalculationFunction();
        if (calcFunction == null) {
            return this.rawUnsignedValue;
        } else {
            return calcFunction.apply(this.rawUnsignedValue);
        }
    }

    public int getRawUnsignedValue() {
        return this.rawUnsignedValue;
    }

    @Override
    public byte[] toByteArray() {
        return this.byteArray.clone();
    }

    @Override
    public String toText() {
        return getValueAsText(getUnsignedValue());
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("unsignedValue", this.getUnsignedValue())
                .add("rawUnsignedValue", this.rawUnsignedValue)
                .add("byteArray", ByteFormatter.formatHexAsString(this.byteArray))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT7Value) {
            final var other = (DPT7Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) && Objects.equals(this.rawUnsignedValue, other.rawUnsignedValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), this.rawUnsignedValue);
    }
}
