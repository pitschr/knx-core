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

/**
 * Test matrix for bits
 */
public final class BitGenerator {

    private BitGenerator() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Returns for given {@code bits} only {@code true} signals
     *
     * @param bits
     * @return array of {@code true}
     */
    public static boolean[] trueOnly(final int bits) {
        final var trueOnly = new boolean[bits];
        for (var i = 0; i < bits; i++) {
            trueOnly[i] = true;
        }
        return trueOnly;
    }

    /**
     * Returns for given {@code bits} only {@code false} signals
     *
     * @param bits
     * @return array of {@code false}
     */
    public static boolean[] falseOnly(final int bits) {
        return new boolean[bits]; // simply return, values are false per default
    }

    /**
     * Returns for given {@code bits} to a matrix with {@code false} and {@code true} signals.
     * <p>
     * Excluded are: only {@code false} and only {@code true} signals
     *
     * @param bits
     * @return
     */
    public static boolean[][] falseAndTrueOnly(final int bits) {
        return matrix(bits, true, true);
    }

    /**
     * Returns for given {@code bits} a full matrix with all combinations of {@code true} and {@code false} signals
     *
     * @param bits
     * @return
     */
    public static boolean[][] matrix(final int bits) {
        return matrix(bits, false, false);
    }

    /**
     * Returns for given {@code bytes} a full matrix with all necessary combinations.
     *
     * @param bitsLength
     * @param skipOnlyFalse if {@code true} it will skip boolean array with {@code false} values only
     * @param skipOnlyTrue  if {@code true} it will skip boolean array with {@code true} values only
     * @return
     */
    public static boolean[][] matrix(final int bitsLength, final boolean skipOnlyFalse, final boolean skipOnlyTrue) {
        final var bitsCombo = 1 << bitsLength;

        // create full matrix
        final var matrix = new boolean[bitsCombo][bitsLength];
        for (var i = 0; i < bitsCombo; i++) {
            final var tmp = new boolean[bitsLength];
            for (var j = 0; j < bitsLength; j++) {
                tmp[bitsLength - 1 - j] = (i & (1 << j)) != 0;
            }
            matrix[i] = tmp;
        }

        // remove only false / only true if requested
        if (skipOnlyFalse || skipOnlyTrue) {
            final var startPos = skipOnlyFalse ? 1 : 0;
            final var endPos = bitsCombo - startPos - (skipOnlyTrue ? 1 : 0);
            final var newMatrix = new boolean[endPos][bitsLength];
            System.arraycopy(matrix, startPos, newMatrix, 0, endPos);
            return newMatrix;
        } else {
            return matrix;
        }
    }
}
