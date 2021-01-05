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

import li.pitschmann.knx.core.datapoint.DPT6;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
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
        final var value = new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, 111);
        final var value2 = new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, 111);

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(value2).isEqualTo(value);
        assertThat(value2).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT6Value(DPT6.PERCENT, 111));
        assertThat(value).isNotEqualTo(new DPT6Value(DPT6.VALUE_1_OCTET_COUNT, 123));
    }

    @Test
    @DisplayName("StatusMode#(byte): bits set: 0000 0... and Mode.MODE_0")
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
    @DisplayName("StatusMode#(byte): bits set: 1001 1... and Mode.MODE_1")
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
    @DisplayName("StatusMode#(byte): bits set: 0110 0... and Mode.MODE_2")
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
    @DisplayName("StatusMode#(boolean, .. , Mode): bits set: 0000 0... and Mode.MODE_0")
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
    @DisplayName("StatusMode#(boolean, .. , Mode): bits set: 0110 0... and Mode.MODE_2")
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
    @DisplayName("StatusMode#isSet(..) call with bit number out of range")
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
    @DisplayName("StatusMode#toString()")
    void testStatusModeToString() {
        final var valueMode0 = new DPT6Value.StatusMode(false, true, true, false, true, DPT6Value.StatusMode.Mode.MODE_0);
        assertThat(valueMode0).hasToString(
                "StatusMode{dpt=6.020, a=false, b=true, c=true, d=false, e=true, mode=MODE_0, byteArray=0x69}"
        );

        final var valueMode1 = new DPT6Value.StatusMode(false, false, true, true, false, DPT6Value.StatusMode.Mode.MODE_1);
        assertThat(valueMode1).hasToString(
                "StatusMode{dpt=6.020, a=false, b=false, c=true, d=true, e=false, mode=MODE_1, byteArray=0x32}"
        );

        final var valueMode2 = new DPT6Value.StatusMode(true, true, true, false, true, DPT6Value.StatusMode.Mode.MODE_2);
        assertThat(valueMode2).hasToString(
                "StatusMode{dpt=6.020, a=true, b=true, c=true, d=false, e=true, mode=MODE_2, byteArray=0xEC}"
        );
    }

    @Test
    @DisplayName("StatusMode#equals() and StatusMode#hashCode()")
    void testStatusModeEqualsAndHashCode() {
        final var value = new DPT6Value.StatusMode(false, false, false, false, false, DPT6Value.StatusMode.Mode.MODE_0);
        final var value2 = new DPT6Value.StatusMode(false, false, false, false, false, DPT6Value.StatusMode.Mode.MODE_0);

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(value2).isEqualTo(value);
        assertThat(value2).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(
                new DPT6Value.StatusMode(true, false, false, false, false, DPT6Value.StatusMode.Mode.MODE_0)
        );
        assertThat(value).isNotEqualTo(
                new DPT6Value.StatusMode(false, true, false, false, false, DPT6Value.StatusMode.Mode.MODE_0)
        );
        assertThat(value).isNotEqualTo(
                new DPT6Value.StatusMode(false, false, true, false, false, DPT6Value.StatusMode.Mode.MODE_0)
        );
        assertThat(value).isNotEqualTo(
                new DPT6Value.StatusMode(false, false, false, true, false, DPT6Value.StatusMode.Mode.MODE_0)
        );
        assertThat(value).isNotEqualTo(
                new DPT6Value.StatusMode(false, false, false, false, true, DPT6Value.StatusMode.Mode.MODE_0)
        );
        assertThat(value).isNotEqualTo(
                new DPT6Value.StatusMode(false, false, false, false, false, DPT6Value.StatusMode.Mode.MODE_1)
        );
    }

    @Test
    @DisplayName("StatusMode#(byte): bits set: 0000 0... and invalid 0x00 for Mode")
    void testModeWithInvalidCode() {
        assertThatThrownBy(() -> new DPT6Value.StatusMode((byte) 0x00))
                .isInstanceOf(KnxEnumNotFoundException.class);
    }
}
