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

import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT6Value.StatusMode}
 *
 * @author PITSCHR
 */
class DPT6ValueStatusModeTest {

    @Test
    @DisplayName("#(byte): bits set: 0000 0... and Mode.MODE_0")
    void testByteMode0() {
        final var value = new DPT6Value.StatusMode((byte) 0b0000_0001);
        assertThat(value.isSet(0)).isFalse(); // 0... ....
        assertThat(value.isSet(1)).isFalse(); // .0.. ....
        assertThat(value.isSet(2)).isFalse(); // ..0. ....
        assertThat(value.isSet(3)).isFalse(); // ...0 ....
        assertThat(value.isSet(4)).isFalse(); // .... 0...
        assertThat(value.getMode()).isEqualTo(DPT6Value.StatusMode.Mode.MODE_0); // .... .001
        assertThat(value.toByteArray()).containsExactly(0x01);

        assertThat(value.toText()).isEqualTo("0x01");
    }

    @Test
    @DisplayName("#(byte): bits set: 1001 1... and Mode.MODE_1")
    void testByteMode1() {
        final var value = new DPT6Value.StatusMode((byte) 0b1001_1010);
        assertThat(value.isSet(0)).isTrue();  // 1... ....
        assertThat(value.isSet(1)).isFalse(); // .0.. ....
        assertThat(value.isSet(2)).isFalse(); // ..0. ....
        assertThat(value.isSet(3)).isTrue();  // ...1 ....
        assertThat(value.isSet(4)).isTrue();  // .... 1...
        assertThat(value.getMode()).isEqualTo(DPT6Value.StatusMode.Mode.MODE_1); // .... .010
        assertThat(value.toByteArray()).containsExactly(0x9A);

        assertThat(value.toText()).isEqualTo("0x9A");
    }

    @Test
    @DisplayName("#(byte): bits set: 0110 0... and Mode.MODE_2")
    void testByteMode2() {
        final var value = new DPT6Value.StatusMode((byte) 0b0110_0100);
        assertThat(value.isSet(0)).isFalse(); // 0... ....
        assertThat(value.isSet(1)).isTrue();  // .1.. ....
        assertThat(value.isSet(2)).isTrue();  // ..1. ....
        assertThat(value.isSet(3)).isFalse(); // ...0 ....
        assertThat(value.isSet(4)).isFalse(); // .... 0...
        assertThat(value.getMode()).isEqualTo(DPT6Value.StatusMode.Mode.MODE_2); // .... .100
        assertThat(value.toByteArray()).containsExactly(0x64);

        assertThat(value.toText()).isEqualTo("0x64");
    }

    @Test
    @DisplayName("#(boolean, .. , Mode): bits set: 0000 0... and Mode.MODE_0")
    void testMode0() {
        final var value = new DPT6Value.StatusMode(false, false, false, false, false, DPT6Value.StatusMode.Mode.MODE_0);
        assertThat(value.isSet(0)).isFalse(); // 0... ....
        assertThat(value.isSet(1)).isFalse(); // .0.. ....
        assertThat(value.isSet(2)).isFalse(); // ..0. ....
        assertThat(value.isSet(3)).isFalse(); // ...0 ....
        assertThat(value.isSet(4)).isFalse(); // .... 0...
        assertThat(value.getMode()).isEqualTo(DPT6Value.StatusMode.Mode.MODE_0); // .... .001
        assertThat(value.toByteArray()).containsExactly(0x01);

        assertThat(value.toText()).isEqualTo("0x01");
    }

    @Test
    @DisplayName("StatusMode#(boolean, .. , Mode): bits set: 1001 1... and Mode.MODE_1")
    void testMode1() {
        final var value = new DPT6Value.StatusMode(true, false, false, true, true, DPT6Value.StatusMode.Mode.MODE_1);
        assertThat(value.isSet(0)).isTrue();  // 1... ....
        assertThat(value.isSet(1)).isFalse(); // .0.. ....
        assertThat(value.isSet(2)).isFalse(); // ..0. ....
        assertThat(value.isSet(3)).isTrue();  // ...1 ....
        assertThat(value.isSet(4)).isTrue();  // .... 1...
        assertThat(value.getMode()).isEqualTo(DPT6Value.StatusMode.Mode.MODE_1); // .... .010
        assertThat(value.toByteArray()).containsExactly(0x9A);

        assertThat(value.toText()).isEqualTo("0x9A");
    }

    @Test
    @DisplayName("#(boolean, .. , Mode): bits set: 0110 0... and Mode.MODE_2")
    void testMode2() {
        final var value = new DPT6Value.StatusMode(false, true, true, false, false, DPT6Value.StatusMode.Mode.MODE_2);
        assertThat(value.isSet(0)).isFalse(); // 0... ....
        assertThat(value.isSet(1)).isTrue();  // .1.. ....
        assertThat(value.isSet(2)).isTrue();  // ..1. ....
        assertThat(value.isSet(3)).isFalse(); // ...0 ....
        assertThat(value.isSet(4)).isFalse(); // .... 0...
        assertThat(value.getMode()).isEqualTo(DPT6Value.StatusMode.Mode.MODE_2); // .... .100
        assertThat(value.toByteArray()).containsExactly(0x64);

        assertThat(value.toText()).isEqualTo("0x64");
    }

    @Test
    @DisplayName("#isSet(..) call with bit number out of range")
    void testIsSetOutOfRange() {
        final var value = new DPT6Value.StatusMode((byte) 0x01);
        assertThatThrownBy(() -> value.isSet(-1))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'bit' is out of range '0'..'4'.");

        assertThatThrownBy(() -> value.isSet(5))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '5' for argument 'bit' is out of range '0'..'4'.");
    }

    @Test
    @DisplayName("#(byte): bits set: 0000 0... and invalid 0x00 for Mode")
    void testModeWithInvalidCode() {
        assertThatThrownBy(() -> new DPT6Value.StatusMode((byte) 0x00))
                .isInstanceOf(KnxEnumNotFoundException.class);
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueMode0 = new DPT6Value.StatusMode(false, true, true, false, true, DPT6Value.StatusMode.Mode.MODE_0);
        assertThat(valueMode0).hasToString(
                "DPT6Value$StatusMode{dpt=6.020, a=false, b=true, c=true, d=false, e=true, mode=MODE_0, byteArray=0x69}"
        );

        final var valueMode1 = new DPT6Value.StatusMode(false, false, true, true, false, DPT6Value.StatusMode.Mode.MODE_1);
        assertThat(valueMode1).hasToString(
                "DPT6Value$StatusMode{dpt=6.020, a=false, b=false, c=true, d=true, e=false, mode=MODE_1, byteArray=0x32}"
        );

        final var valueMode2 = new DPT6Value.StatusMode(true, true, true, false, true, DPT6Value.StatusMode.Mode.MODE_2);
        assertThat(valueMode2).hasToString(
                "DPT6Value$StatusMode{dpt=6.020, a=true, b=true, c=true, d=false, e=true, mode=MODE_2, byteArray=0xEC}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT6Value.StatusMode.class).withIgnoredFields("dpt").verify();
    }
}
