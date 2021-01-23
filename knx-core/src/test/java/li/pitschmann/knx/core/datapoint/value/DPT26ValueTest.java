/*
 * KNX Link - A library for KNX Net/IP communication
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

import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT26Value}
 *
 * @author PITSCHR
 */
class DPT26ValueTest {

    @Test
    @DisplayName("#(byte) with: inactive, 0")
    void testByte0() {
        final var value = new DPT26Value((byte) 0x00);
        assertThat(value.isActive()).isFalse();
        assertThat(value.getSceneNumber()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("inactive scene '0'");
    }

    @Test
    @DisplayName("#(byte) with: active, 0")
    void testByte0Active() {
        final var value = new DPT26Value((byte) 0x40);
        assertThat(value.isActive()).isTrue();
        assertThat(value.getSceneNumber()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x40);

        assertThat(value.toText()).isEqualTo("active scene '0'");
    }

    @Test
    @DisplayName("#(byte) with: inactive, 42")
    void testByte42() {
        final var value = new DPT26Value((byte) 0x2A);
        assertThat(value.isActive()).isFalse();
        assertThat(value.getSceneNumber()).isEqualTo(42);
        assertThat(value.toByteArray()).containsExactly(0x2A);

        assertThat(value.toText()).isEqualTo("inactive scene '42'");
    }

    @Test
    @DisplayName("#(byte) with: active, 42")
    void testByte42Active() {
        final var value = new DPT26Value((byte) 0x6A);
        assertThat(value.isActive()).isTrue();
        assertThat(value.getSceneNumber()).isEqualTo(42);
        assertThat(value.toByteArray()).containsExactly(0x6A);

        assertThat(value.toText()).isEqualTo("active scene '42'");
    }

    @Test
    @DisplayName("#(byte) with: inactive, 63")
    void testByte63() {
        final var value = new DPT26Value((byte) 0x3F);
        assertThat(value.isActive()).isFalse();
        assertThat(value.getSceneNumber()).isEqualTo(63);
        assertThat(value.toByteArray()).containsExactly(0x3F);

        assertThat(value.toText()).isEqualTo("inactive scene '63'");
    }

    @Test
    @DisplayName("#(byte) with: active, 63")
    void testByte63Active() {
        final var value = new DPT26Value((byte) 0x7F);
        assertThat(value.isActive()).isTrue();
        assertThat(value.getSceneNumber()).isEqualTo(63);
        assertThat(value.toByteArray()).containsExactly(0x7F);

        assertThat(value.toText()).isEqualTo("active scene '63'");
    }

    @Test
    @DisplayName("#(boolean, int) with: false, 0")
    void testInt0() {
        final var value = new DPT26Value(false, 0);
        assertThat(value.isActive()).isFalse();
        assertThat(value.getSceneNumber()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("inactive scene '0'");
    }

    @Test
    @DisplayName("#(boolean, int) with: true, 0")
    void testInt0Active() {
        final var value = new DPT26Value(true, 0);
        assertThat(value.isActive()).isTrue();
        assertThat(value.getSceneNumber()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x40);

        assertThat(value.toText()).isEqualTo("active scene '0'");
    }

    @Test
    @DisplayName("#(boolean, int) with: false, 53")
    void testInt53() {
        final var value = new DPT26Value(false, 53);
        assertThat(value.isActive()).isFalse();
        assertThat(value.getSceneNumber()).isEqualTo(53);
        assertThat(value.toByteArray()).containsExactly(0x35);

        assertThat(value.toText()).isEqualTo("inactive scene '53'");
    }

    @Test
    @DisplayName("#(boolean, int) with: true, 53")
    void testInt53Active() {
        final var value = new DPT26Value(true, 53);
        assertThat(value.isActive()).isTrue();
        assertThat(value.getSceneNumber()).isEqualTo(53);
        assertThat(value.toByteArray()).containsExactly(0x75);

        assertThat(value.toText()).isEqualTo("active scene '53'");
    }

    @Test
    @DisplayName("#(boolean, int) with: false, 63")
    void testInt63() {
        final var value = new DPT26Value(false, 63);
        assertThat(value.isActive()).isFalse();
        assertThat(value.getSceneNumber()).isEqualTo(63);
        assertThat(value.toByteArray()).containsExactly(0x3F);

        assertThat(value.toText()).isEqualTo("inactive scene '63'");
    }

    @Test
    @DisplayName("#(boolean, int) with: true, 63")
    void testInt63Active() {
        final var value = new DPT26Value(true, 63);
        assertThat(value.isActive()).isTrue();
        assertThat(value.getSceneNumber()).isEqualTo(63);
        assertThat(value.toByteArray()).containsExactly(0x7F);

        assertThat(value.toText()).isEqualTo("active scene '63'");
    }

    @Test
    @DisplayName("#(boolean, int) with scene number out of range")
    void testIntOutOfRange() {
        assertThatThrownBy(() -> new DPT26Value(true, -1))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'sceneNumber' is out of range '0'..'63'.");
        assertThatThrownBy(() -> new DPT26Value(true, 64))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '64' for argument 'sceneNumber' is out of range '0'..'63'.");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var value = new DPT26Value(false, 31);
        assertThat(value).hasToString(
                "DPT26Value{dpt=26.001, active=false, sceneNumber=31, byteArray=0x1F}"
        );

        final var valueActive = new DPT26Value(true, 60);
        assertThat(valueActive).hasToString(
                "DPT26Value{dpt=26.001, active=true, sceneNumber=60, byteArray=0x7C}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT26Value(true, 17);
        final var valueByte = new DPT26Value((byte) 0b0101_0001);

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(valueByte).isEqualTo(value);
        assertThat(valueByte).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT26Value(false, 17));
        assertThat(value).isNotEqualTo(new DPT26Value(true, 13));
    }
}
