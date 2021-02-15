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

import li.pitschmann.knx.core.datapoint.DPT6;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT6Value}
 *
 * @author PITSCHR
 */
class DPT6ValueTest {

    @Test
    @DisplayName("#(DPT6.VALUE_1_OCTET_COUNT, byte) with: 0")
    void testByteZero() {
        final var value = new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, (byte) 0x00);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT6.VALUE_1_OCTET_COUNT, byte) with: -128")
    void testByteNegative() {
        final var value = new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, (byte) 0x80);
        assertThat(value.getValue()).isEqualTo(-128);
        assertThat(value.toByteArray()).containsExactly(0x80);

        assertThat(value.toText()).isEqualTo("-128");
    }

    @Test
    @DisplayName("#(DPT6.VALUE_1_OCTET_COUNT, byte) with: 127")
    void testBytePositive() {
        final var value = new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, (byte) 0x7F);
        assertThat(value.getValue()).isEqualTo(127);
        assertThat(value.toByteArray()).containsExactly(0x7F);

        assertThat(value.toText()).isEqualTo("127");
    }

    @Test
    @DisplayName("#(DPT6.VALUE_1_OCTET_COUNT, int) with: 0")
    void testZero() {
        final var value = new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, 0);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT6.VALUE_1_OCTET_COUNT, int) with: -128")
    void testNegative() {
        final var value = new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, -128);
        assertThat(value.getValue()).isEqualTo(-128);
        assertThat(value.toByteArray()).containsExactly(0x80);

        assertThat(value.toText()).isEqualTo("-128");
    }

    @Test
    @DisplayName("#(DPT6.VALUE_1_OCTET_COUNT, int) with: 127")
    void testPositive() {
        final var value = new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, 127);
        assertThat(value.getValue()).isEqualTo(127);
        assertThat(value.toByteArray()).containsExactly(0x7F);

        assertThat(value.toText()).isEqualTo("127");
    }

    @Test
    @DisplayName("#(DPT6.VALUE_1_OCTET_COUNT, int) with numbers out of range")
    void testScalingOutOfRange() {
        assertThatThrownBy(() -> new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, -129))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-129' for argument 'value' is out of range '-128'..'127'.");
        assertThatThrownBy(() -> new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, 128))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '128' for argument 'value' is out of range '-128'..'127'.");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueUnsigned = new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, -87);
        assertThat(valueUnsigned).hasToString(
                "DPT6Value{dpt=6.010, value=-87, byteArray=0xA9}"
        );

        final var valueScaling = new DPT6Value(DPT6.PERCENT, 123);
        assertThat(valueScaling).hasToString(
                "DPT6Value{dpt=6.001, value=123, byteArray=0x7B}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT6Value.class).verify();
    }
}
