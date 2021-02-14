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
import li.pitschmann.knx.core.datapoint.DPT14;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

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
    private final double value;

    public DPT14Value(final DPT14 dpt, final byte[] bytes) {
        this(dpt, toFloatingValue(bytes));
    }

    public DPT14Value(final DPT14 dpt, final double value) {
        super(dpt);
        if (!getDPT().isRangeClosed(value)) {
            throw new KnxNumberOutOfRangeException("value", getDPT().getLowerValue(), getDPT().getUpperValue(), value);
        }
        this.value = value;
    }

    /**
     * Converts the four byte array to float
     *
     * @param bytes byte array to be converted
     * @return double value
     * @throws KnxNumberOutOfRangeException if the length of bytes is not expected
     */
    private static double toFloatingValue(final byte[] bytes) {
        if (bytes.length != 4) {
            throw new KnxNumberOutOfRangeException("bytes", 4, 4, bytes.length, bytes);
        }

        return Float.intBitsToFloat(ByteBuffer.wrap(bytes).getInt());
    }

    public double getValue() {
        return value;
    }

    @Override
    public byte[] toByteArray() {
        final var rawBits = Float.floatToIntBits((float) value);
        return new byte[]{ //
                (byte) ((rawBits >>> 24) & 0xFF), //
                (byte) ((rawBits >>> 16) & 0xFF), //
                (byte) ((rawBits >>> 8) & 0xFF), //
                (byte) (rawBits & 0xFF) //
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
                .add("value", getValueAsText(value))
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
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
                    && Objects.equals(this.value, other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), value);
    }
}
