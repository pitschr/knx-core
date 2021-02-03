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
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT17Value}
 *
 * @author PITSCHR
 */
class DPT17ValueTest {

    @Test
    @DisplayName("#(byte) with: 0")
    void testByte0() {
        final var value = new DPT17Value((byte) 0x00);
        assertThat(value.getSceneNumber()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("scene '0'");
    }

    @Test
    @DisplayName("#(byte) with: 42")
    void testByte42() {
        final var value = new DPT17Value((byte) 0x2A);
        assertThat(value.getSceneNumber()).isEqualTo(42);
        assertThat(value.toByteArray()).containsExactly(0x2A);

        assertThat(value.toText()).isEqualTo("scene '42'");
    }

    @Test
    @DisplayName("#(byte) with: 63")
    void testByte63() {
        final var value = new DPT17Value((byte) 0x3F);
        assertThat(value.getSceneNumber()).isEqualTo(63);
        assertThat(value.toByteArray()).containsExactly(0x3F);

        assertThat(value.toText()).isEqualTo("scene '63'");
    }

    @Test
    @DisplayName("#(int) with: 0")
    void testInt0() {
        final var value = new DPT17Value(0);
        assertThat(value.getSceneNumber()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("scene '0'");
    }

    @Test
    @DisplayName("#(int) with: 53")
    void testInt53() {
        final var value = new DPT17Value(53);
        assertThat(value.getSceneNumber()).isEqualTo(53);
        assertThat(value.toByteArray()).containsExactly(0x35);

        assertThat(value.toText()).isEqualTo("scene '53'");
    }

    @Test
    @DisplayName("#(int) with: 63")
    void testInt63() {
        final var value = new DPT17Value(63);
        assertThat(value.getSceneNumber()).isEqualTo(63);
        assertThat(value.toByteArray()).containsExactly(0x3F);

        assertThat(value.toText()).isEqualTo("scene '63'");
    }

    @Test
    @DisplayName("#(byte) with scene number out of range")
    void testByteOutOfRange() {
        // negative scene number not possible with byte due unsigned int conversion
        assertThatThrownBy(() -> new DPT17Value((byte) 0x40))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '64' for argument 'sceneNumber' is out of range '0'..'63'.");
    }

    @Test
    @DisplayName("#(int) with scene number out of range")
    void testIntOutOfRange() {
        assertThatThrownBy(() -> new DPT17Value(-1))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'sceneNumber' is out of range '0'..'63'.");
        assertThatThrownBy(() -> new DPT17Value(64))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '64' for argument 'sceneNumber' is out of range '0'..'63'.");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var value31 = new DPT17Value(31);
        assertThat(value31).hasToString(
                "DPT17Value{dpt=17.001, sceneNumber=31, byteArray=0x1F}"
        );

        final var value60 = new DPT17Value(60);
        assertThat(value60).hasToString(
                "DPT17Value{dpt=17.001, sceneNumber=60, byteArray=0x3C}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT17Value.class).withIgnoredFields("dpt").verify();
    }
}
