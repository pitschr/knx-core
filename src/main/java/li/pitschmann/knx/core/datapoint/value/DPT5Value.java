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
import li.pitschmann.knx.core.datapoint.DPT5;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

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
    private final int value;

    public DPT5Value(final DPT5 dpt, final byte b) {
        this(
                dpt,
                toUnsignedInt(dpt, b)
        );
    }

    public DPT5Value(final DPT5 dpt, final int value) {
        super(dpt);
        if (!getDPT().isRangeClosed(value)) {
            throw new KnxNumberOutOfRangeException("value", getDPT().getLowerValue(), getDPT().getUpperValue(), value);
        }

        this.value = value;
    }

    /**
     * <p>
     * Converts from {@code byte} to an unsigned {@code int} according
     * to the {@link DPT5} specification.
     * </p>
     * <p>
     * <u>Examples:</u><br>
     * For {@link DPT5#VALUE_1_OCTET_UNSIGNED_COUNT} it is: 0x00 = 0, 0xFF = 255<br>
     * For {@link DPT5#SCALING} it is: 0x00 = 0, 0xFF = 100<br>
     * For {@link DPT5#ANGLE} it is: 0x00 = 0, 0xFF = 360<br>
     * </p>
     * @param dpt the data point type that
     * @param b the byte to be converted to unsigned int
     * @return the unsigned integer
     */
    private static int toUnsignedInt(final DPT5 dpt, final byte b) {
        final var calcFunction = dpt.getCalculationFunction();
        final int unsignedInt = Byte.toUnsignedInt(b);
        if (calcFunction == null) {
            return unsignedInt;
        } else {
            return (int) Math.round((100d / calcFunction.applyAsDouble(100)) * unsignedInt);
        }
    }

    /**
     * Returns the value
     *
     * @return int
     */
    public int getValue() {
        return value;
    }

    @Override
    public byte[] toByteArray() {
        final var calcFunction = getDPT().getCalculationFunction();
        final byte b;
        if (calcFunction == null) {
            b = (byte) value;
        } else {
            b = (byte) Math.round(calcFunction.applyAsDouble(value));
        }
        return new byte[]{b};
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
        } else if (obj instanceof DPT5Value) {
            final var other = (DPT5Value) obj;
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
