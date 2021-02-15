/*
 * Copyright (C) 2021 Pitschmann Christoph
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

import li.pitschmann.knx.core.datapoint.DPT29;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DPT29Value}
 *
 * @author PITSCHR
 */
class DPT29ValueTest {

    @Test
    @DisplayName("#(DPT29.VALUE_8_OCTET_COUNT, byte[]) with: 0")
    void testByteZero() {
        final var value = new DPT29Value(DPT29.VALUE_8_OCTET_COUNT, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT29.VALUE_8_OCTET_COUNT, byte[]) with: -9223372036854775808")
    void testByteNegative() {
        final var value = new DPT29Value(DPT29.VALUE_8_OCTET_COUNT, new byte[]{(byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertThat(value.getValue()).isEqualTo(Long.MIN_VALUE);
        assertThat(value.toByteArray()).containsExactly(0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("-9223372036854775808");
    }

    @Test
    @DisplayName("#(DPT29.VALUE_8_OCTET_COUNT, byte[]) with: 9223372036854775807")
    void testBytePositive() {
        final var value = new DPT29Value(DPT29.VALUE_8_OCTET_COUNT, new byte[]{(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertThat(value.getValue()).isEqualTo(Long.MAX_VALUE);
        assertThat(value.toByteArray()).containsExactly(0x7F, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF);

        assertThat(value.toText()).isEqualTo("9223372036854775807");
    }


    @Test
    @DisplayName("#(DPT29.ACTIVE_ENERGY, long) with: 0")
    void testLongZero() {
        final var value = new DPT29Value(DPT29.ACTIVE_ENERGY, 0);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT29.ACTIVE_ENERGY, long) with: -9223372036854775808")
    void testLongNegative() {
        final var value = new DPT29Value(DPT29.ACTIVE_ENERGY, Long.MIN_VALUE);
        assertThat(value.getValue()).isEqualTo(Long.MIN_VALUE);
        assertThat(value.toByteArray()).containsExactly(0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("-9223372036854775808");
    }

    @Test
    @DisplayName("#(DPT29.ACTIVE_ENERGY, long) with: 9223372036854775807")
    void testLongPositive() {
        final var value = new DPT29Value(DPT29.ACTIVE_ENERGY, Long.MAX_VALUE);
        assertThat(value.getValue()).isEqualTo(Long.MAX_VALUE);
        assertThat(value.toByteArray()).containsExactly(0x7F, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF);

        assertThat(value.toText()).isEqualTo("9223372036854775807");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueNegative = new DPT29Value(DPT29.VALUE_8_OCTET_COUNT, -952343294763232L);
        assertThat(valueNegative).hasToString(
                "DPT29Value{dpt=29.001, value=-952343294763232, byteArray=0xFF FC 9D D9 4C 36 1F 20}"
        );

        final var valuePositive = new DPT29Value(DPT29.VALUE_8_OCTET_COUNT, 7443934732935601L);
        assertThat(valuePositive).hasToString(
                "DPT29Value{dpt=29.001, value=7443934732935601, byteArray=0x00 1A 72 38 1D 7C E1 B1}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT29Value.class).verify();
    }
}
