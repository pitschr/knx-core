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

import li.pitschmann.knx.core.datapoint.DPT13;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Function;

/**
 * Data Point Value for {@link DPT13} (13.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Signed Value)                                                |
 * Encoding     | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Signed Value)                                                |
 *              | V   V   V   V   V   V   V   V   V   V   V   V   V   V   V   V |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     4 octets (V<sub>32</sub>)
 * Range:      U = [-2147483648 .. 2147483647]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT13Value extends AbstractDataPointValue<DPT13> {
    private final int rawSignedValue;
    private final byte[] byteArray;

    public DPT13Value(final DPT13 dpt, final byte[] bytes) {
        super(dpt);
        Preconditions.checkArgument(bytes.length == 4);
        // signed value
        this.rawSignedValue = ByteBuffer.wrap(bytes).getInt();
        this.byteArray = bytes;
    }

    public DPT13Value(final DPT13 dpt, final int value) {
        super(dpt);
        Preconditions.checkArgument(dpt.isRangeClosed(value));
        this.rawSignedValue = value;
        this.byteArray = toByteArray(value);
    }

    /**
     * Converts signed int value to byte array
     *
     * @param value
     * @return byte array
     */
    public static byte[] toByteArray(final int value) {
        return new byte[]{ //
                (byte) (value >> 24), //
                (byte) (value >> 16), //
                (byte) (value >> 8), //
                (byte) value};
    }

    public double getSignedValue() {
        final Function<Integer, Double> calcFunction = this.getDPT().getCalculationFunction();
        if (calcFunction == null) {
            return this.rawSignedValue;
        } else {
            return calcFunction.apply(this.rawSignedValue);
        }
    }

    public int getRawSignedValue() {
        return this.rawSignedValue;
    }

    @Override
    public byte[] toByteArray() {
        return this.byteArray.clone();
    }

    @Override
    public String toText() {
        return getValueAsText(getSignedValue());
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("signedValue", this.getSignedValue())
                .add("rawSignedValue", this.rawSignedValue)
                .add("byteArray", ByteFormatter.formatHexAsString(this.byteArray))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT13Value) {
            final var other = (DPT13Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) && Objects.equals(this.rawSignedValue, other.rawSignedValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), this.rawSignedValue);
    }

}
