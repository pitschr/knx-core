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

package li.pitschmann.knx.link.datapoint.value;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import li.pitschmann.knx.link.datapoint.DPT5;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

/**
 * Data Point Value for {@link DPT5} (5.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Unsigned Value)              |
 * Encoding     | U   U   U   U   U   U   U   U |
 *              +---+---+---+---+---+---+---+---+
 * Format:     8 bit (U<sub>8</sub>)
 * Range:      U = [0 .. 255]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT5Value extends AbstractDataPointValue<DPT5> {
    private int rawUnsignedValue;

    public DPT5Value(final @Nonnull DPT5 dpt, final byte b) {
        super(dpt);
        // unsigned value
        this.rawUnsignedValue = Bytes.toUnsignedInt(b);
    }

    public DPT5Value(final @Nonnull DPT5 dpt, final int value) {
        super(dpt);
        Preconditions.checkArgument(dpt.isRangeClosed(value));
        this.rawUnsignedValue = value;
    }

    /**
     * Converts unsigned int value to byte array
     *
     * @param value
     * @return byte array
     */
    @Nonnull
    public static byte[] toByteArray(final int value) {
        return new byte[]{(byte) value};
    }

    public double getUnsignedValue() {
        final Function<Integer, Double> calcFunction = this.getDPT().getCalcuationFunction();
        if (calcFunction == null) {
            return this.rawUnsignedValue;
        } else {
            return calcFunction.apply(this.rawUnsignedValue);
        }
    }

    public int getRawUnsignedValue() {
        return this.rawUnsignedValue;
    }

    @Nonnull
    @Override
    public byte[] toByteArray() {
        return toByteArray(this.rawUnsignedValue);
    }

    @Nonnull
    @Override
    public String toText() {
        return getValueAsText(getUnsignedValue(), getDPT().getUnit());
    }

    @Nonnull
    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(DPT5Value.class)
                .add("dpt", this.getDPT())
                .add("unsignedValue", this.getUnsignedValue())
                .add("rawUnsignedValue", this.rawUnsignedValue)
                .add("byteArray", ByteFormatter.formatHexAsString(this.toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT5Value) {
            final var other = (DPT5Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.rawUnsignedValue, other.rawUnsignedValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), this.rawUnsignedValue);
    }

}
