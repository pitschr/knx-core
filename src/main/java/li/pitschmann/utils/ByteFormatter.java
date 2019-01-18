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

package li.pitschmann.utils;

import com.google.common.base.*;

import java.util.stream.*;

/**
 * Formatter for byte and byte arrays to print out e.g. byte array in hex-decimal format.
 *
 * @author PITSCHR
 */
public final class ByteFormatter {
    private ByteFormatter() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Formats a single byte to a hex-decimal format
     *
     * @param b
     * @return hex-decimal formatted byte
     */
    public static String formatHex(final byte b) {
        return String.format("0x%02X", b);
    }

    /**
     * Formats an integer a hex-decimal format
     *
     * @param number positive number
     * @return hex-decimal formatted integer
     */
    public static String formatHex(final int number) {
        Preconditions.checkArgument(number >= 0, "Argument 'number' must be positive.");

        final char[] chars = Integer.toHexString(number).toUpperCase().toCharArray();
        final StringBuilder sb = new StringBuilder();
        int j = chars.length % 2;
        sb.append(j != 0 ? "0x0" : "0x").append(chars[0]);
        for (int i = 1; i < chars.length; i++) {
            if (++j % 2 == 0) {
                sb.append(' ');
            }
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    /**
     * Returns a hex-decimal formatted byte array as String array. Every array item will start with "0x" prefix. In case
     * the {@code bytes} is null or empty, a {@code null} will be returned.
     *
     * @param bytes
     * @return string array whereas each bytes are formatted in hex-decimal, if given argument was null or empty then
     * {@code null} is returned
     */
    public static String[] formatHex(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return new String[0];
        }

        final String[] rawDataAsHex = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            rawDataAsHex[i] = formatHex(bytes[i]);
        }
        return rawDataAsHex;
    }

    /**
     * Returns a hex-decimal formatted byte array as a String. The hex format will have only one "0x" prefix and are
     * separated by an empty space. In case the {@code bytes} is null or empty, a {@code null} will be returned.
     *
     * @param bytes
     * @return complete string array whereas whole bytes are formatted as a single string, if given argument was null or
     * empty then {@code null} is returned
     */
    public static String formatHexAsString(final byte[] bytes) {
        return formatHexAsString(bytes, " ");
    }

    /**
     * Returns a hex-decimal formatted byte array as a String. The hex format will have only one "0x" prefix and are
     * separated by given {@code delimiter} char sequence. In case the {@code bytes} is null or empty, a {@code null}
     * will be returned.
     *
     * @param bytes
     * @param delimiter between hex-decimal values
     * @return complete string array whereas whole bytes are formatted as a single string, if given {@code bytes}
     * argument was null or empty then {@code null} is returned
     */
    public static String formatHexAsString(final byte[] bytes, final CharSequence delimiter) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return "0x" + IntStream.range(0, bytes.length).mapToObj(i -> String.format("%02X", bytes[i])).collect(Collectors.joining(delimiter));
    }

}
