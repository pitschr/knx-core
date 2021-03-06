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
import li.pitschmann.knx.core.datapoint.DPT8;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.math.BigInteger;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

/**
 * Data Point Value for {@link DPT8} (8.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Signed Value)                                                |
 * Encoding     | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     2 octets (V<sub>16</sub>)
 * Range:      U = [-32768 .. 32767]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT8Value extends AbstractDataPointValue<DPT8> {
    private final int value;

    public DPT8Value(final DPT8 dpt, final byte[] bytes) {
        this(
                dpt, //
                toInt(dpt, bytes) //
        );
    }

    public DPT8Value(final DPT8 dpt, final int value) {
        super(dpt);
        if (!getDPT().isRangeClosed(value)) {
            throw new KnxNumberOutOfRangeException("value", getDPT().getLowerValue(), getDPT().getUpperValue(), value);
        }
        this.value = value;
    }

    /**
     * <p>
     * Converts from {@code bytes[]} to a {@code int} according
     * to the {@link DPT8} specification.
     * </p>
     * <p>
     * <u>Examples:</u><br>
     * For {@link DPT8#VALUE_2_OCTET_COUNT} it is: 0x00 00 = -32768, 0xFF FF = 32767<br>
     * For {@link DPT8#DELTA_TIME_100MS} it is: 0x00 00 = -3276800, 0xFF FF = 3276700<br>
     * </p>
     * @param dpt the data point type that
     * @param bytes the bytes array to be converted to signed int
     * @return the signed integer
     */
    private static int toInt(final DPT8 dpt, final byte[] bytes) {
        final var calcFunction = dpt.getCalculationFunction();
        final int signedInt = new BigInteger(bytes).intValue();
        if (calcFunction == null) {
            return signedInt;
        } else {
            return (100 / calcFunction.applyAsInt(100)) * signedInt;
        }
    }

    /**
     * Returns the value as int
     *
     * @return int
     */
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
            newValue = calcFunction.applyAsInt(value);
        }

        return new byte[]{
                (byte) (newValue >>> 8), //
                (byte) newValue //
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
                .add("value", getValueAsText(value))
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT8Value) {
            final var other = (DPT8Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.value, other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), value);
    }

    /**
     * Special Class for {@link DPT8Value} as it is not an Integer
     */
    public static final class Percent extends AbstractDataPointValue<DPT8.Percent> {
        private static final DoubleUnaryOperator calcFunction = v -> v * 100d;
        private final double value;

        public Percent(final byte[] bytes) {
            this(
                    toDouble(bytes) //
            );
        }

        public Percent(final double value) {
            super(DPT8.PERCENT);
            if (!getDPT().isRangeClosed(value)) {
                throw new KnxNumberOutOfRangeException("value", getDPT().getLowerValue(), getDPT().getUpperValue(), value);
            }
            this.value = value;
        }

        /**
         * Converts from {@code bytes[]} to a {@code double} according.
         */
        private static double toDouble(final byte[] bytes) {
            final int signedInt = new BigInteger(bytes).intValue();
            return (100d / calcFunction.applyAsDouble(100)) * signedInt;
        }

        /**
         * Returns the value as double
         * @return double
         */
        public double getValue() { return value; }

        @Override
        public byte[] toByteArray() {
            final int newValue = (int) Math.round(calcFunction.applyAsDouble(value));

            return new byte[]{
                    (byte) (newValue >>> 8), //
                    (byte) newValue //
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
                    .add("value", getValueAsText(value))
                    .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                    .toString();
            // @formatter:on
        }

        @Override
        public boolean equals(final @Nullable Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof DPT8Value.Percent) {
                final var other = (DPT8Value.Percent) obj;
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
}
