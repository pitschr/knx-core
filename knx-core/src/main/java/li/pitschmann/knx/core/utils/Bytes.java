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

package li.pitschmann.knx.core.utils;


import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Utility for bytes
 *
 * @author PITSCHR
 */
public final class Bytes {
    private static final Pattern PATTERN_HEX_STRING = Pattern.compile("^(0x)?([0-9a-fA-F]{2}\\s?)+$");

    private Bytes() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Converts given bytes to a signed short
     *
     * @param b
     * @param moreBytes
     * @return signed short, between {@link Short#MIN_VALUE} and {@link Short#MAX_VALUE}
     */
    public static short toSignedShort(final byte b, final byte... moreBytes) {
        if (moreBytes.length == 0) {
            return b;
        } else {
            return toSignedShort(concatByteToByteArray(b, moreBytes));
        }
    }

    /**
     * Converts two byte array to a signed short
     *
     * @param bytes
     * @return signed short, between {@link Short#MIN_VALUE} and {@link Short#MAX_VALUE}
     */
    public static short toSignedShort(final @Nullable byte[] bytes) {
        final var adjustedBytes = toByteArrayWithCapacity(bytes, 2);
        return (short) (adjustedBytes[0] << 8 | adjustedBytes[1] & 0xFF);
    }

    /**
     * Converts given bytes to an unsigned short
     *
     * @param b
     * @param moreBytes
     * @return unsigned short, between {@code 0} and {@link Short#MAX_VALUE}
     */
    public static short toUnsignedShort(final byte b, final byte... moreBytes) {
        if (moreBytes.length == 0) {
            return b;
        } else {
            return toUnsignedShort(concatByteToByteArray(b, moreBytes));
        }
    }

    /**
     * Converts two byte array to an unsigned short
     *
     * @param bytes
     * @return unsigned short, between {@code 0} and {@link Short#MAX_VALUE}
     */
    public static short toUnsignedShort(final @Nullable byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        final var adjustedBytes = toByteArrayWithCapacity(bytes, 2);
        // max size of short is Short#MAX_VALUE
        Preconditions.checkArgument(Byte.toUnsignedInt(adjustedBytes[0]) <= 0x7f,
                "Byte array cannot be converted to unsigned short because it exceeds Short#MAX_VALUE: {}", adjustedBytes);
        return (short) (adjustedBytes[0] << 8 | adjustedBytes[1] & 0xFF);
    }

    /**
     * Converts given bytes to a signed int
     *
     * @param b
     * @param moreBytes
     * @return signed int, between {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE}
     */
    public static int toSignedInt(final byte b, final byte... moreBytes) {
        if (moreBytes.length == 0) {
            return b;
        } else {
            return toSignedInt(concatByteToByteArray(b, moreBytes));
        }
    }

    /**
     * Converts four byte array to a signed int
     *
     * @param bytes
     * @return signed int, between {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE}
     */
    public static int toSignedInt(byte[] bytes) {
        final var adjustedBytes = toByteArrayWithCapacity(bytes, 4);

        return ((adjustedBytes[0] & 0xFF) << 24) //
                | ((adjustedBytes[1] & 0xFF) << 16) //
                | ((adjustedBytes[2] & 0xFF) << 8) //
                | (adjustedBytes[3] & 0xFF);
    }

    /**
     * Converts given bytes to an unsigned int
     *
     * @param b
     * @param moreBytes
     * @return unsigned int, between {@code 0} and {@link Integer#MAX_VALUE}
     */
    public static int toUnsignedInt(final byte b, final byte... moreBytes) {
        if (moreBytes.length == 0) {
            return Byte.toUnsignedInt(b);
        } else {
            return toUnsignedInt(concatByteToByteArray(b, moreBytes));
        }
    }

    /**
     * Converts four byte array to an unsigned int
     *
     * @param bytes
     * @return unsigned int, between {@code 0} and {@link Integer#MAX_VALUE}
     */
    public static int toUnsignedInt(final @Nullable byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        final var adjustedBytes = toByteArrayWithCapacity(bytes, 4);
        return toUnsignedInt(adjustedBytes[0], adjustedBytes[1], adjustedBytes[2], adjustedBytes[3]);
    }

    /**
     * Converts four bytes to an unsigned int
     *
     * @param b3
     * @param b2
     * @param b1
     * @param b0
     * @return unsigned int, between {@code 0} and {@link Integer#MAX_VALUE}
     */
    public static int toUnsignedInt(byte b3, byte b2, byte b1, byte b0) {
        Preconditions.checkArgument(Byte.toUnsignedInt(b3) <= 0x7f,
                "Byte array cannot be converted to unsigned int because it exceeds Integer#MAX_VALUE");
        return ((b3 & 0xFF) << 24) //
                | ((b2 & 0xFF) << 16) //
                | ((b1 & 0xFF) << 8) //
                | (b0 & 0xFF);
    }

    /**
     * Converts given bytes to an unsigned long
     *
     * @param b
     * @param moreBytes
     * @return signed long, between {@link Long#MIN_VALUE} and {@link Long#MAX_VALUE}
     */
    public static long toSignedLong(final byte b, final byte... moreBytes) {
        if (moreBytes.length == 0) {
            return b;
        } else {
            return toSignedLong(concatByteToByteArray(b, moreBytes));
        }
    }

    /**
     * Converts eight bytes to a signed long
     *
     * @param b7
     * @param b6
     * @param b5
     * @param b4
     * @param b3
     * @param b2
     * @param b1
     * @param b0
     * @return signed long, between {@link Long#MIN_VALUE} and {@link Long#MAX_VALUE}
     */
    public static long toSignedLong(byte b7, byte b6, byte b5, byte b4, byte b3, byte b2, byte b1, byte b0) { // NOSONAR
        return toSignedLong(new byte[]{b7, b6, b5, b4, b3, b2, b1, b0});
    }

    /**
     * Converts eight byte array to a signed long
     *
     * @param bytes
     * @return signed long, between {@link Long#MIN_VALUE} and {@link Long#MAX_VALUE}
     */
    public static long toSignedLong(byte[] bytes) {
        return ByteBuffer.wrap(toByteArrayWithCapacity(bytes, 8)).getLong();
    }

    /**
     * Converts given bytes to an unsigned long
     *
     * @param b
     * @param moreBytes
     * @return unsigned long, between {@code 0} and {@link Long#MAX_VALUE}
     */
    public static long toUnsignedLong(final byte b, final byte... moreBytes) {
        if (moreBytes.length == 0) {
            return Byte.toUnsignedLong(b);
        } else {
            return toUnsignedLong(concatByteToByteArray(b, moreBytes));
        }
    }

    /**
     * Converts eight bytes to a signed long
     *
     * @param b7
     * @param b6
     * @param b5
     * @param b4
     * @param b3
     * @param b2
     * @param b1
     * @param b0
     * @return unsigned long, between {@code 0} and {@link Long#MAX_VALUE}
     */
    public static long toUnsignedLong(byte b7, byte b6, byte b5, byte b4, byte b3, byte b2, byte b1, byte b0) { // NOSONAR
        return toUnsignedLong(new byte[]{b7, b6, b5, b4, b3, b2, b1, b0});
    }

    /**
     * Converts eight byte array to a signed long
     *
     * @param bytes
     * @return unsigned long, between {@code 0} and {@link Long#MAX_VALUE}
     */
    public static long toUnsignedLong(final @Nullable byte[] bytes) {
        final var adjustedBytes = toByteArrayWithCapacity(bytes, 8);
        // max size of long is Long#MAX_VALUE
        Preconditions.checkArgument(Byte.toUnsignedInt(adjustedBytes[0]) <= 0x7f,
                "Byte array cannot be converted to unsigned long because it exceeds Long#MAX_VALUE: {} (original: {})"
                , adjustedBytes, bytes);
        return ByteBuffer.wrap(adjustedBytes).getLong();
    }

    /**
     * Concatenates the {@code b} and {@code moreBytes} into one byte array
     *
     * @param b
     * @param moreBytes
     * @return byte array
     */
    private static byte[] concatByteToByteArray(final byte b, final byte[] moreBytes) {
        final var newByteArray = new byte[moreBytes.length + 1];
        newByteArray[0] = b;
        System.arraycopy(moreBytes, 0, newByteArray, 1, moreBytes.length);
        return newByteArray;
    }

    /**
     * Returns the byte array ({@code bytes}) with given {@code capacity}. In case the byte array is larger than
     * capacity, then we will try to shrink it. It would work only when there are leading zero bytes.
     *
     * @param bytes
     * @param capacity
     * @return byte array
     */
    public static byte[] toByteArrayWithCapacity(final @Nullable byte[] bytes, int capacity) {
        if (bytes == null) {
            return new byte[capacity];
        } else if (bytes.length == capacity) {
            return bytes;
        } else if (bytes.length > capacity) {
            for (var i = 0; i < bytes.length - capacity; i++) {
                if (bytes[i] != 0x00) {
                    throw new IllegalArgumentException("Cannot shrink bytes to " + capacity + " byte array: " + ByteFormatter.formatHexAsString(bytes));
                }
            }
            final var newByteArray = new byte[capacity];
            System.arraycopy(bytes, bytes.length - capacity, newByteArray, 0, capacity);
            return newByteArray;
        } else {
            final var newByteArray = new byte[capacity];
            System.arraycopy(bytes, 0, newByteArray, capacity - bytes.length, bytes.length);
            return newByteArray;
        }
    }

    /**
     * Fills the {@code templateArray} with {@code bytes} in given {@code direction}.
     *
     * @param templateArray the template array to be filled by {@code bytes}
     * @param bytes
     * @param direction     the direction how the template array should be filled
     * @return byte array
     */
    public static byte[] fillByteArray(final byte[] templateArray,
                                       final byte[] bytes,
                                       final FillDirection direction) {
        Preconditions.checkArgument(bytes.length <= templateArray.length,
                "Length of bytes cannot exceed the template array capacity.");

        if (bytes.length == templateArray.length) {
            return bytes;
        } else if (direction == FillDirection.LEFT_TO_RIGHT) {
            final var newByteArray = templateArray.clone();
            System.arraycopy(bytes, 0, newByteArray, 0, bytes.length);
            return newByteArray;
        } else if (direction == FillDirection.RIGHT_TO_LEFT) {
            final var newByteArray = templateArray.clone();
            System.arraycopy(bytes, 0, newByteArray, newByteArray.length - bytes.length, bytes.length);
            return newByteArray;
        } else {
            throw new UnsupportedOperationException("Unknown FillDirection");
        }
    }

    /**
     * Trims {@code 0x00} from {@code bytes} array on right side
     *
     * @param bytes
     * @return trimmed byte array
     */
    public static byte[] trimRight(final byte[] bytes) {
        return trimRight(bytes, (byte) 0x00);
    }

    /**
     * Trims the given {@code byteToRemoved} from {@code bytes} array.
     *
     * @param bytes
     * @param byteToRemoved byte to be removed
     * @return trimmed byte array
     */
    public static byte[] trimRight(final byte[] bytes, final byte byteToRemoved) {
        // count occurrence of bytes to be removed
        var count = 0;
        for (var i = bytes.length - 1; i >= 0; i--) {
            if (bytes[i] == byteToRemoved) {
                count++;
            } else {
                break;
            }
        }
        return count == 0 ? bytes.clone() : Arrays.copyOfRange(bytes, 0, bytes.length - count);
    }

    /**
     * Pads the given {@code b} to {@code bytes} array until {@code newCapacity} is reached
     *
     * @param bytes
     * @param b
     * @param newCapacity
     * @return padded byte array
     */
    public static byte[] padRight(final byte[] bytes, final byte b, final int newCapacity) {
        Preconditions.checkArgument(bytes.length <= newCapacity,
                "Capacity cannot be smaller than {} (actual: {})", bytes.length, newCapacity);

        if (bytes.length == newCapacity) {
            return bytes.clone();
        } else if (bytes.length == 0) {
            final var newBytes = new byte[newCapacity];
            Arrays.fill(newBytes, b);
            return newBytes;
        } else {
            // must be filled
            final var newBytes = new byte[newCapacity];
            if (b != (byte) 0x00) {
                Arrays.fill(newBytes, b);
            }
            System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
            return newBytes;
        }
    }

    /**
     * Creates a new bytes array with given {@code hexString}.
     * <p>
     * Examples:<br>
     * 0610020800080700<br>
     * 061004200017040705002900bce010824c040300800c1d<br>
     *
     * @param hexString
     * @return bytes array
     */
    public static byte[] toByteArray(final String hexString) {
        if (hexString.isEmpty()) {
            return new byte[0];
        }

        Preconditions.checkArgument(PATTERN_HEX_STRING.matcher(hexString).matches(),
                "Illegal hex string format: {}", hexString);

        final String newHexString;
        if (hexString.startsWith("0x")) {
            newHexString = hexString.substring(2).replace(" ", "");
        } else {
            newHexString = hexString;
        }
        final var hexAsBytes = new byte[newHexString.length() / 2];
        for (var i = 0; i < hexAsBytes.length; i++) {
            hexAsBytes[i] = (byte) Integer.parseInt(newHexString.substring(i * 2, i * 2 + 2), 16);
        }
        return hexAsBytes;
    }

    /**
     * Converts given 8-booleans to one byte
     *
     * @param b7
     * @param b6
     * @param b5
     * @param b4
     * @param b3
     * @param b2
     * @param b1
     * @param b0
     * @return one byte
     */
    public static byte toByte(final boolean b7, final boolean b6, final boolean b5, final boolean b4,    // NOSONAR
                              final boolean b3, final boolean b2, final boolean b1, final boolean b0) {
        var b = (byte) 0x00;
        if (b7) {
            b |= 0x80;
        }
        if (b6) {
            b |= 0x40;
        }
        if (b5) {
            b |= 0x20;
        }
        if (b4) {
            b |= 0x10;
        }
        if (b3) {
            b |= 0x08;
        }
        if (b2) {
            b |= 0x04;
        }
        if (b1) {
            b |= 0x02;
        }
        if (b0) {
            b |= 0x01;
        }
        return b;
    }

    /**
     * Converts given booleans to one byte
     *
     * @param b
     * @param moreBits up to 7 booleans only
     * @return one byte
     */
    public static byte toByte(final boolean b, final boolean... moreBits) {
        Preconditions.checkArgument(moreBits.length <= 7,
                "You can provide only up to 8 booleans for byte (actual: {})", (moreBits.length + 1));
        if (moreBits.length == 0) {
            return (byte) (b ? 0x01 : 0x00);
        } else {
            return (byte) toInt(b, moreBits);
        }
    }

    /**
     * Converts given boolean array to byte array
     *
     * @param bits the size of bits must be divisible by 8
     * @return byte array
     */
    public static byte[] toByteArray(final boolean... bits) {
        if (bits.length == 8) {
            return new byte[]{toByte(bits[0], bits[1], bits[2], bits[3], bits[4], bits[5], bits[6], bits[7])};
        } else if (bits.length == 16) {
            return new byte[]{
                    // byte 0
                    toByte(bits[0], bits[1], bits[2], bits[3], bits[4], bits[5], bits[6], bits[7]),
                    // byte 1
                    toByte(bits[8], bits[9], bits[10], bits[11], bits[12], bits[13], bits[14], bits[15])};
        } else {
            final var byteArrayLength = (int) Math.ceil(bits.length / 8d);
            final var remainder = bits.length % 8;

            var bytePos = 0;
            final var bytes = new byte[byteArrayLength];
            // if remainder available - first position is bytes[0]
            if (remainder > 0) {
                final var remainderBool = bits[0];
                final var moreRemainderBools = new boolean[remainder - 1];
                System.arraycopy(bits, 1, moreRemainderBools, 0, remainder - 1);
                bytes[bytePos++] = toByte(remainderBool, moreRemainderBools);
            }
            // if remainder available - first position is bytes[1]
            // if remainder not available - first position is bytes[0]
            if (byteArrayLength > 1 || remainder == 0) {
                for (var i = remainder; i < bits.length; i += 8) {
                    bytes[bytePos++] = toByte(bits[i], bits[i + 1], bits[i + 2], bits[i + 3], bits[i + 4], bits[i + 5], bits[i + 6], bits[i + 7]);
                }
            }
            return bytes;
        }
    }

    /**
     * Converts a boolean array into integer number
     * <p>
     * E.g. {@code true, true} will return an integer of {@code 3}<br>
     * E.g. {@code true, false, true} will return an integer of {@code 5}
     *
     * @param bits
     * @return
     */
    public static int toInt(final boolean bit, final boolean... bits) {
        var n = bit ? 1 : 0;
        if (bits.length > 0) {
            for (var i = 0; i < bits.length; ++i) {
                n = (n << 1) + (bits[i] ? 1 : 0);
            }
        }
        return n;
    }

    /**
     * Returns the values from each provided array combined into a single array. For example, {@code
     * concat(new byte[] {a, b}, new byte[] {}, new byte[] {c}} returns the array {@code {a, b, c}}.
     *
     * @param arrays zero or more {@code byte} arrays
     * @return a single array containing all the values from the source arrays, in order
     */
    public static byte[] concat(final byte[]... arrays) {
        var length = 0;
        for (final var array : arrays) {
            length += array.length;
        }

        final var result = new byte[length];
        int pos = 0;
        for (final var array : arrays) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }
        return result;
    }

    /**
     * The fill direction to be used in {@link Bytes#fillByteArray(byte[], byte[], FillDirection)} method.
     *
     * @author PITSCHR
     */
    public enum FillDirection {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }
}
