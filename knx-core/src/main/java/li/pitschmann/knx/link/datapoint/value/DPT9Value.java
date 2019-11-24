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

import li.pitschmann.knx.link.datapoint.DPT9;
import li.pitschmann.knx.utils.ByteFormatter;
import li.pitschmann.knx.utils.Bytes;
import li.pitschmann.knx.utils.Preconditions;
import li.pitschmann.knx.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT9} (9.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Float Value)                                                 |
 * Encoding     | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     2 octets (F<sub>16</sub>)
 * Encoding:   Float Value = (0.01 * M)*2(E)
 *             E = [0 .. 15]
 *             M = [-2048 .. 2047], two's complement notation
 * Range:      [-670760 .. 670760]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT9Value extends AbstractDataPointValue<DPT9> {
    private static final Logger log = LoggerFactory.getLogger(DPT9Value.class);
    private final double floatingValue;
    private final byte[] byteArray;

    public DPT9Value(final @Nonnull DPT9 dpt, final @Nonnull byte[] bytes) {
        super(dpt);
        Preconditions.checkArgument(bytes.length == 2);
        this.floatingValue = toFloatingValue(bytes);
        this.byteArray = bytes;
    }

    public DPT9Value(final @Nonnull DPT9 dpt, final double value) {
        super(dpt);
        Preconditions.checkArgument(dpt.isRangeClosed(value));
        this.floatingValue = value;
        this.byteArray = toByteArray(value);
    }

    /**
     * Converts the two byte array to double according to formula {@code (0.01 * M)*2(E)}
     *
     * @param bytes
     * @return double value
     */
    @Nonnull
    public static double toFloatingValue(final @Nonnull byte[] bytes) {
        final var exponent = getExponent(bytes);
        final var mantissa = getMantissa(bytes);
        return (1 << exponent) * mantissa * 0.01d;
    }

    /**
     * Get exponent from high-byte
     *
     * <pre>
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names  | (Float Value)                                                 |
     * Encoding     | .   E   E   E   E   .   .   .                                 |
     *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *              E = [0 .. 15]
     * </pre>
     *
     * @param bytes
     * @return exponent, between {@code 0} and {@code 15}
     */
    private static int getExponent(final @Nonnull byte[] bytes) {
        // we are interested in high byte only!

        // @formatter:off
        // Result: .... EEEE
        return  // .EEE E...
                (bytes[0] & 0x78) >>> 3;
        // @formatter:on
    }

    /**
     * Calculate Mantissa based on high-byte and low-byte
     *
     * <pre>
     *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names  | (Float Value)                                                 |
     * Encoding     | M   .   .   .   .   M   M   M   M   M   M   M   M   M   M   M |
     *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *              M = [-2048 .. 2047], two's complement notation
     * </pre>
     *
     * @param bytes
     * @return mantissa
     */
    private static int getMantissa(final @Nonnull byte[] bytes) {
        // @formatter:off
        // Result: MMMM 0000 0000 0000 0000 0000 0000 0000
        var highByte = // M... .... --> M... .... 0000 0000 0000 0000 0000 0000
                (bytes[0] & 0x80 << 24)
                        // .... .MMM --> .MMM 0000 0000 0000 0000 0000 0000 0000
                        | ((bytes[0] & 0x07) << 28);
        //
        // normalize
        // Result: MMMM 0000 0000
        highByte = highByte >> 20;

        // Result: .... MMMM MMMM
        final var lowByte = (bytes[1] & 0xFF);
        // @formatter:on

        // result: MMMM MMMM MMMM
        final var mantissa = (highByte | lowByte);
        log.debug("Mantissa for value '{}' (high-byte={}, low-byte={}): {}", ByteFormatter.formatHexAsString(bytes), highByte, lowByte, mantissa);
        return mantissa;
    }

    /**
     * Converts double value to byte array that is compatible with {@link DPT9}
     *
     * @param value
     * @return {@link DPT9} compatible byte array for double value
     */
    @Nonnull
    public static byte[] toByteArray(final double value) {
        // multiply with 100 because value is a digit with two decimal places
        var calcValue = value * 100d;

        var exponent = 0;
        final var valueNegative = calcValue < 0d;
        if (valueNegative) {
            // negative value, calculate how many times it can be divided by 2
            while (calcValue < -2048.0d) {
                exponent++;
                calcValue /= 2;
            }
        } else {
            // positive value
            while (calcValue > 2047.0d) {
                exponent++;
                calcValue /= 2;
            }
        }
        log.debug("Exponents for '{}': {}", value, exponent);
        log.debug("Value after division for '{}': {}", value, calcValue);

        final var mantissa = Math.round(calcValue) & 0x7FF;
        log.debug("Mantissa for '{}': {}", value, mantissa);
        // M... ....
        var highByte = valueNegative ? (byte) 0x80 : 0x00;
        // .EEE ....
        highByte |= (exponent << 3);
        // .... MMMM
        highByte |= (mantissa >>> 8);
        log.debug("High Byte for '{}': {} (unsigned: {})", value, highByte, Bytes.toUnsignedInt(highByte));

        final var lowByte = (byte) (mantissa & 0xFF);
        log.debug("Low Byte for '{}': {} (unsigned: {})", value, lowByte, Bytes.toUnsignedInt(lowByte));

        return new byte[]{highByte, lowByte};
    }

    public double getFloatingValue() {
        return this.floatingValue;
    }

    @Nonnull
    @Override
    public byte[] toByteArray() {
        return this.byteArray.clone();
    }

    @Nonnull
    @Override
    public String toText() {
        return getValueAsText(getFloatingValue());
    }

    @Nonnull
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
        } else if (obj instanceof DPT9Value) {
            final var other = (DPT9Value) obj;
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
