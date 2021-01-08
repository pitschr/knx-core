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

import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT18Value}
 *
 * @author PITSCHR
 */
class DPT18ValueTest {

    @Test
    @DisplayName("#(boolean, byte) with: 0")
    void testByte0() {
        final var value = new DPT18Value((byte) 0x00);
        assertThat(value.isControlled()).isFalse();
        assertThat(value.getSceneNumber()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("scene '0'");
    }

    @Test
    @DisplayName("#(boolean, byte) with: 0, controlled")
    void testByte0Controlled() {
        final var value = new DPT18Value((byte) 0x80);
        assertThat(value.isControlled()).isTrue();
        assertThat(value.getSceneNumber()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x80);

        assertThat(value.toText()).isEqualTo("controlled 'scene 0'");
    }

    @Test
    @DisplayName("#(boolean, byte) with: 42")
    void testByte42() {
        final var value = new DPT18Value((byte) 0x2A);
        assertThat(value.isControlled()).isFalse();
        assertThat(value.getSceneNumber()).isEqualTo(42);
        assertThat(value.toByteArray()).containsExactly(0x2A);

        assertThat(value.toText()).isEqualTo("scene '42'");
    }

    @Test
    @DisplayName("#(boolean, byte) with: 42, controlled")
    void testByte42Controlled() {
        final var value = new DPT18Value((byte) 0xAA);
        assertThat(value.isControlled()).isTrue();
        assertThat(value.getSceneNumber()).isEqualTo(42);
        assertThat(value.toByteArray()).containsExactly(0xAA);

        assertThat(value.toText()).isEqualTo("controlled 'scene 42'");
    }

    @Test
    @DisplayName("#(boolean, byte) with: 63")
    void testByte63() {
        final var value = new DPT18Value((byte) 0x3F);
        assertThat(value.isControlled()).isFalse();
        assertThat(value.getSceneNumber()).isEqualTo(63);
        assertThat(value.toByteArray()).containsExactly(0x3F);

        assertThat(value.toText()).isEqualTo("scene '63'");
    }

    @Test
    @DisplayName("#(boolean, byte) with: 63, controlled")
    void testByte63Controlled() {
        final var value = new DPT18Value((byte) 0xBF);
        assertThat(value.isControlled()).isTrue();
        assertThat(value.getSceneNumber()).isEqualTo(63);
        assertThat(value.toByteArray()).containsExactly(0xBF);

        assertThat(value.toText()).isEqualTo("controlled 'scene 63'");
    }

    @Test
    @DisplayName("#(boolean, int) with: 0")
    void testInt0() {
        final var value = new DPT18Value(false, 0);
        assertThat(value.isControlled()).isFalse();
        assertThat(value.getSceneNumber()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("scene '0'");
    }

    @Test
    @DisplayName("#(boolean, int) with: 0, controlled")
    void testInt0Controlled() {
        final var value = new DPT18Value(true, 0);
        assertThat(value.isControlled()).isTrue();
        assertThat(value.getSceneNumber()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x80);

        assertThat(value.toText()).isEqualTo("controlled 'scene 0'");
    }

    @Test
    @DisplayName("#(boolean, int) with: 53")
    void testInt53() {
        final var value = new DPT18Value(false, 53);
        assertThat(value.isControlled()).isFalse();
        assertThat(value.getSceneNumber()).isEqualTo(53);
        assertThat(value.toByteArray()).containsExactly(0x35);

        assertThat(value.toText()).isEqualTo("scene '53'");
    }

    @Test
    @DisplayName("#(boolean, int) with: 53, controlled")
    void testInt53Controlled() {
        final var value = new DPT18Value(true, 53);
        assertThat(value.isControlled()).isTrue();
        assertThat(value.getSceneNumber()).isEqualTo(53);
        assertThat(value.toByteArray()).containsExactly(0xB5);

        assertThat(value.toText()).isEqualTo("controlled 'scene 53'");
    }

    @Test
    @DisplayName("#(boolean, int) with: 63")
    void testInt63() {
        final var value = new DPT18Value(false, 63);
        assertThat(value.isControlled()).isFalse();
        assertThat(value.getSceneNumber()).isEqualTo(63);
        assertThat(value.toByteArray()).containsExactly(0x3F);

        assertThat(value.toText()).isEqualTo("scene '63'");
    }

    @Test
    @DisplayName("#(boolean, int) with: 63, controlled")
    void testInt63Controlled() {
        final var value = new DPT18Value(true, 63);
        assertThat(value.isControlled()).isTrue();
        assertThat(value.getSceneNumber()).isEqualTo(63);
        assertThat(value.toByteArray()).containsExactly(0xBF);

        assertThat(value.toText()).isEqualTo("controlled 'scene 63'");
    }

    @Test
    @DisplayName("#(boolean, byte) with scene number out of range")
    void testByteOutOfRange() {
        // negative scene number not possible with byte due unsigned int conversion
        assertThatThrownBy(() -> new DPT18Value((byte)0x40))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '64' for argument 'sceneNumber' is out of range '0'..'63'.");
    }

    @Test
    @DisplayName("#(boolean, int) with scene number out of range")
    void testIntOutOfRange() {
        assertThatThrownBy(() -> new DPT18Value(true,-1))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'sceneNumber' is out of range '0'..'63'.");
        assertThatThrownBy(() -> new DPT18Value(true,64))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '64' for argument 'sceneNumber' is out of range '0'..'63'.");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var value = new DPT18Value(false,31);
        assertThat(value).hasToString(
                "DPT18Value{dpt=18.001, controlled=false, sceneNumber=31, byteArray=0x1F}"
        );

        final var valueControlled = new DPT18Value(true, 60);
        assertThat(valueControlled).hasToString(
                "DPT18Value{dpt=18.001, controlled=true, sceneNumber=60, byteArray=0xBC}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT18Value(true, 17);
        final var valueByte = new DPT18Value((byte)0b1001_0001);

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(valueByte).isEqualTo(value);
        assertThat(valueByte).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT18Value(false, 17));
        assertThat(value).isNotEqualTo(new DPT18Value(true, 13));
    }
}
