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

import li.pitschmann.knx.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link BitGenerator}
 */
public class BitGeneratorTest {
    /**
     * Only {@code false} signals (1-bit, 2-bit, ..., N-bit)
     */
    @Test
    public void falseOnly() {
        // 1-bit
        assertThat(BitGenerator.falseOnly(1)).containsExactly(false);

        // 2-bits
        assertThat(BitGenerator.falseOnly(2)).containsExactly(false, false);

        // 3-bits
        assertThat(BitGenerator.falseOnly(3)).containsExactly(false, false, false);
    }

    /**
     * Only {@code true} signals (1-bit, 2-bit, ..., N-bit)
     */
    @Test
    public void trueOnly() {
        // 1-bit
        assertThat(BitGenerator.trueOnly(1)).containsExactly(true);

        // 2-bits
        assertThat(BitGenerator.trueOnly(2)).containsExactly(true, true);

        // 3-bits
        assertThat(BitGenerator.trueOnly(3)).containsExactly(true, true, true);
    }

    /**
     * {@code true} and {@code false} combined signals (1-bit, 2-bit, ..., N-bit)
     * <p>
     * Excluded: only {@code false} and only {@code true} signals.
     */
    @Test
    public void falseAndTrueOnly() {
        // 1-bit
        assertThat(BitGenerator.falseAndTrueOnly(1)).isEmpty();

        // 2-bits
        assertThat(BitGenerator.falseAndTrueOnly(2)).containsExactly( //
                new boolean[]{false, true}, //
                new boolean[]{true, false});

        // 3-bits
        assertThat(BitGenerator.falseAndTrueOnly(3)).containsExactly( //
                new boolean[]{false, false, true}, //
                new boolean[]{false, true, false}, //
                new boolean[]{false, true, true}, //
                new boolean[]{true, false, false}, //
                new boolean[]{true, false, true}, //
                new boolean[]{true, true, false});
    }

    /**
     * All {@code true}/{@code false} signals (1-bit, 2-bit, ..., 5-bit)
     */
    @Test
    public void matrix() {
        // 1-bit
        assertThat(BitGenerator.matrix(1)).containsExactly( //
                new boolean[]{false}, //
                new boolean[]{true});

        // 2-bits
        assertThat(BitGenerator.matrix(2)).containsExactly( //
                new boolean[]{false, false}, //
                new boolean[]{false, true}, //
                new boolean[]{true, false}, //
                new boolean[]{true, true});

        // 3-bits
        assertThat(BitGenerator.matrix(3)).containsExactly( //
                new boolean[]{false, false, false}, //
                new boolean[]{false, false, true}, //
                new boolean[]{false, true, false}, //
                new boolean[]{false, true, true}, //
                new boolean[]{true, false, false}, //
                new boolean[]{true, false, true}, //
                new boolean[]{true, true, false}, //
                new boolean[]{true, true, true});
    }

    /**
     * All {@code true}/{@code false} signals (1-bit, 2-bit, ..., 5-bit) excluding the boolean array with only
     * <strong>false</strong> values.
     */
    @Test
    public void matrixSkipOnlyFalse() {
        // 1-bit
        assertThat(BitGenerator.matrix(1, true, false)).containsExactly( //
                new boolean[]{true});

        // 2-bits
        assertThat(BitGenerator.matrix(2, true, false)).containsExactly( //
                new boolean[]{false, true}, //
                new boolean[]{true, false}, //
                new boolean[]{true, true});

        // 3-bits
        assertThat(BitGenerator.matrix(3, true, false)).containsExactly( //
                new boolean[]{false, false, true}, //
                new boolean[]{false, true, false}, //
                new boolean[]{false, true, true}, //
                new boolean[]{true, false, false}, //
                new boolean[]{true, false, true}, //
                new boolean[]{true, true, false}, //
                new boolean[]{true, true, true});
    }

    /**
     * All {@code true}/{@code false} signals (1-bit, 2-bit, ..., 5-bit) excluding the boolean array with only
     * <strong>true</strong> values.
     */
    @Test
    public void matrixSkipOnlyTrue() {
        // 1-bit
        assertThat(BitGenerator.matrix(1, false, true)).containsExactly( //
                new boolean[]{false});

        // 2-bits
        assertThat(BitGenerator.matrix(2, false, true)).containsExactly( //
                new boolean[]{false, false}, //
                new boolean[]{false, true}, //
                new boolean[]{true, false});

        // 3-bits
        assertThat(BitGenerator.matrix(3, false, true)).containsExactly( //
                new boolean[]{false, false, false}, //
                new boolean[]{false, false, true}, //
                new boolean[]{false, true, false}, //
                new boolean[]{false, true, true}, //
                new boolean[]{true, false, false}, //
                new boolean[]{true, false, true}, //
                new boolean[]{true, true, false});
    }

    /**
     * Test constructor of {@link BitGenerator}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(BitGenerator.class);
    }
}
