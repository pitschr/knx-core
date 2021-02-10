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
import li.pitschmann.knx.core.datapoint.DataPointType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Abstract implementation of {@link DataPointValue}
 *
 * @author PITSCHR
 */
abstract class AbstractDataPointValue<T extends DataPointType> implements DataPointValue {
    private final T dpt;

    protected AbstractDataPointValue(final T dpt) {
        this.dpt = Objects.requireNonNull(dpt);
    }

    /**
     * Returns {@code double} value into a string representation
     *
     * @param value the double value to be converted to string
     * @return string representation of {@code double} value
     */
    protected static String getValueAsText(final double value) {
        return BigDecimal.valueOf(value).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    /**
     * Returns {@code int} value into a string representation
     * @param value the int value to be converted to string
     * @return string representation of {@code int} value
     */
    protected static String getValueAsText(final int value) {
        return Integer.toString(value);
    }

    /**
     * Returns {@code long} value into a string representation
     * @param value the long value to be converted to string
     * @return string representation of {@code long} value
     */

    protected static String getValueAsText(final long value) {
        return Long.toString(value);
    }

    /**
     * Returns {@link Object} value into a string representation. This
     * method is null-safe. If value is null, the {@code "null"}
     * will be printed.
     *
     * @param value the {@link Object} value to be converted to string
     * @return string representation of {@link Object}
     */
    protected static String getValueAsText(final @Nullable Object value) {
        return String.valueOf(value);
    }

    /**
     * Returns if the bit {@code position} at {@code byte} is set.
     * As a single byte has only 8 bits, this the {@code position}
     * must be between 0 and 7.
     *
     * <pre>
     * Position:  7   6   5   4   3   2   1   0
     *          +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *          | B   B   B   B   B   B   B   B |
     *          +---+---+---+---+---+---+---+---+
     * </pre>
     *
     * @param b the byte
     * @param position the bit position of byte
     * @return {@code true} if bit is set, otherwise {@code false}
     */
    protected static boolean isBitSet(final byte b, final int position) {
        return ((b & 0xFF) & (0x01 << position)) != 0x00;
    }

    /**
     * Returns if the bit {@code position} at {@code bytes} is set.
     * This method supports byte array which allows to look up for
     * bit position from 0 until {@code numberOfBytes * 8 - 1}.
     * <p>
     * For example, 2 bytes, the bit position must be between 0 and 15.
     *
     * <pre>
     * Position: 15  14  13  12  11  10   9   8   7   6   5   4   3   2   1   0
     *          +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *          | B   B   B   B   B   B   B   B   B   B   B   B   B   B   B   B |
     *          +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * </pre>
     *
     * @param bytes the byte array
     * @param position the bit position of byte array
     * @return {@code true} if bit is set, otherwise {@code false}
     */
    protected static boolean isBitSet(final byte[] bytes, final int position) {
        var index = bytes.length - 1 - (position / 8);
        var bitPosition = position % 8;
        return isBitSet(bytes[index], bitPosition);
    }

    /**
     * Returns the Data Point Type for the current value
     *
     * @return data point type
     */
    protected final T getDPT() {
        return this.dpt;
    }
}
