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

import li.pitschmann.knx.core.datapoint.DPT5;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT5Value}
 *
 * @author PITSCHR
 */
class DPT5ValueTest {

    @Test
    @DisplayName("#(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, byte) with: 0x00")
    void testByte_0x00() {
        final var value = new DPT5Value(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, (byte) 0x00);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT5.SCALING, byte) with: 0x00")
    void testByte_0x00_Scaling() {
        final var value = new DPT5Value(DPT5.SCALING, (byte) 0x00);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT5.ANGLE, byte) with: 0x00")
    void testByte_0x00_Angle() {
        final var value = new DPT5Value(DPT5.ANGLE, (byte) 0x00);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, byte) with: 0x85")
    void testByte_0x85() {
        final var value = new DPT5Value(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, (byte) 0x85);
        assertThat(value.getValue()).isEqualTo(133);
        assertThat(value.toByteArray()).containsExactly(0x85);

        assertThat(value.toText()).isEqualTo("133");
    }

    @Test
    @DisplayName("#(DPT5.SCALING, byte) with: 0x85")
    void testByte_0x85_Scaling() {
        final var value = new DPT5Value(DPT5.SCALING, (byte) 0x85);
        assertThat(value.getValue()).isEqualTo(52);
        assertThat(value.toByteArray()).containsExactly(0x85);

        assertThat(value.toText()).isEqualTo("52");
    }

    @Test
    @DisplayName("#(DPT5.ANGLE, byte) with: 0x85")
    void testByte_0x85_Angle() {
        final var value = new DPT5Value(DPT5.ANGLE, (byte) 0x85);
        assertThat(value.getValue()).isEqualTo(188);
        assertThat(value.toByteArray()).containsExactly(0x85);

        assertThat(value.toText()).isEqualTo("188");
    }

    @Test
    @DisplayName("#(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, byte) with: 0xFF")
    void testByte_0xFF() {
        final var value = new DPT5Value(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, (byte) 0xFF);
        assertThat(value.getValue()).isEqualTo(255);
        assertThat(value.toByteArray()).containsExactly(0xFF);

        assertThat(value.toText()).isEqualTo("255");
    }

    @Test
    @DisplayName("#(DPT5.SCALING, byte) with: 0xFF")
    void testByte_0xFF_Scaling() {
        final var value = new DPT5Value(DPT5.SCALING, (byte) 0xFF);
        assertThat(value.getValue()).isEqualTo(100);
        assertThat(value.toByteArray()).containsExactly(0xFF);

        assertThat(value.toText()).isEqualTo("100");
    }

    @Test
    @DisplayName("#(DPT5.ANGLE, byte) with: 0xFF")
    void testByte_0xFF_Angle() {
        final var value = new DPT5Value(DPT5.ANGLE, (byte) 0xFF);
        assertThat(value.getValue()).isEqualTo(360);
        assertThat(value.toByteArray()).containsExactly(0xFF);

        assertThat(value.toText()).isEqualTo("360");
    }

    @Test
    @DisplayName("#(DPT5.SCALING, int) with: 0")
    void testInt_0_Scaling() {
        final var value = new DPT5Value(DPT5.SCALING, 0);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT5.SCALING, int) with: 18")
    void testInt_18_Scaling() {
        final var value = new DPT5Value(DPT5.SCALING, 18);
        assertThat(value.getValue()).isEqualTo(18);
        assertThat(value.toByteArray()).containsExactly(0x2E);

        assertThat(value.toText()).isEqualTo("18");
    }

    @Test
    @DisplayName("#(DPT5.SCALING, int) with: 100")
    void testInt_100_Scaling() {
        final var value = new DPT5Value(DPT5.SCALING, 100);
        assertThat(value.getValue()).isEqualTo(100);
        assertThat(value.toByteArray()).containsExactly(0xFF);

        assertThat(value.toText()).isEqualTo("100");
    }

    @Test
    @DisplayName("#(DPT5.ANGLE, int) with: 40")
    void testInt_40_() {
        final var value = new DPT5Value(DPT5.ANGLE, 40);
        assertThat(value.getValue()).isEqualTo(40);
        assertThat(value.toByteArray()).containsExactly(0x1C);

        assertThat(value.toText()).isEqualTo("40");
    }

    @Test
    @DisplayName("#(DPT5.ANGLE, int) with: 360")
    void testInt_360_Angle() {
        final var value = new DPT5Value(DPT5.ANGLE, 360);
        assertThat(value.getValue()).isEqualTo(360);
        assertThat(value.toByteArray()).containsExactly(0xFF);

        assertThat(value.toText()).isEqualTo("360");
    }

    @Test
    @DisplayName("#(DPT5.SCALING, int) with numbers out of range")
    void testScalingOutOfRange() {
        assertThatThrownBy(() -> new DPT5Value(DPT5.SCALING, -1))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'value' is out of range '0'..'100'.");
        assertThatThrownBy(() -> new DPT5Value(DPT5.SCALING, 101))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '101' for argument 'value' is out of range '0'..'100'.");
    }

    @Test
    @DisplayName("#(DPT5.ANGLE, int) with numbers out of range")
    void testAngleOutOfRange() {
        assertThatThrownBy(() -> new DPT5Value(DPT5.ANGLE, -1))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'value' is out of range '0'..'360'.");
        assertThatThrownBy(() -> new DPT5Value(DPT5.ANGLE, 361))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '361' for argument 'value' is out of range '0'..'360'.");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueUnsigned = new DPT5Value(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, 47);
        assertThat(valueUnsigned).hasToString(
                "DPT5Value{dpt=5.010, value=47, byteArray=0x2F}"
        );

        final var valueScaling = new DPT5Value(DPT5.SCALING, 83);
        assertThat(valueScaling).hasToString(
                "DPT5Value{dpt=5.001, value=83, byteArray=0xD4}"
        );

        final var valueAngle = new DPT5Value(DPT5.ANGLE, 180);
        assertThat(valueAngle).hasToString(
                "DPT5Value{dpt=5.003, value=180, byteArray=0x80}"
        );

    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT5Value.class).verify();
    }
}
