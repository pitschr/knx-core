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

import li.pitschmann.knx.core.datapoint.DPT14;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT14} (14.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | S   (Exponent)                  (Fraction)                    |
 * Encoding     | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Fraction)                                                    |
 *              | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     4 octets (F<sub>32</sub>)
 * Range:      S = {0, 1}
 *             Exponent = [0 .. 255]
 *             Fraction = [0 .. 8388607]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT14Value extends AbstractDataPointValue<DPT14> {
    private final double floatingValue;
    private final byte[] byteArray;

    public DPT14Value(final DPT14 dpt, final byte[] bytes) {
        super(dpt);
        Preconditions.checkArgument(bytes.length == 4);
        this.floatingValue = toFloatingValue(bytes);
        this.byteArray = bytes;
    }

    public DPT14Value(final DPT14 dpt, final double value) {
        super(dpt);
        Preconditions.checkArgument(dpt.isRangeClosed(value));
        this.floatingValue = value;
        this.byteArray = toByteArray(value);
    }

    /**
     * Converts the four byte array to float
     *
     * @param bytes
     * @return float value
     */
    public static double toFloatingValue(final byte[] bytes) {
        return Float.intBitsToFloat(ByteBuffer.wrap(bytes).getInt());
    }

    /**
     * Converts double value to byte array
     *
     * @param value
     * @return 4-byte array
     */

    public static byte[] toByteArray(final double value) {
        final var rawBits = Float.floatToIntBits(Double.valueOf(value).floatValue());
        return new byte[]{ //
                (byte) ((rawBits >>> 24) & 0xFF), //
                (byte) ((rawBits >>> 16) & 0xFF), //
                (byte) ((rawBits >>> 8) & 0xFF), //
                (byte) (rawBits & 0xFF)};
    }

    public double getFloatingValue() {
        return this.floatingValue;
    }


    @Override
    public byte[] toByteArray() {
        return this.byteArray.clone();
    }


    @Override
    public String toText() {
        return getValueAsText(getFloatingValue());
    }


    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("floatingValue", this.floatingValue)
                .add("byteArray", ByteFormatter.formatHexAsString(this.byteArray))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT14Value) {
            final var other = (DPT14Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.floatingValue, other.floatingValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), this.floatingValue);
    }
}
