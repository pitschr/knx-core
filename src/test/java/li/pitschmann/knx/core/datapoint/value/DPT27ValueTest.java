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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT27Value}
 *
 * @author PITSCHR
 */
class DPT27ValueTest {

    @Test
    @DisplayName("#(byte[]) with: All Invalid, All Off")
    void testByte_allInvalid_allOff() {
        final var value = new DPT27Value(new byte[]{0x00, 0x00, 0x00, 0x00});
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0x00, 0x00);
        assertValidAndOn(value, false, false);

        assertThat(value.toText()).isEmpty(); // empty, because only valid values will be considered
    }

    @Test
    @DisplayName("#(byte[]) with: All Invalid, All On")
    void testByte_allInvalid_allOn() {
        final var value = new DPT27Value(new byte[]{0x00, 0x00, (byte) 0xFF, (byte) 0xFF});
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0xFF, 0xFF);
        assertValidAndOn(value, false, true);

        assertThat(value.toText()).isEmpty(); // empty, because only valid values will be considered
    }

    @Test
    @DisplayName("#(byte[]) with: All Valid, All Off")
    void testByte_allValid_allOff() {
        final var value = new DPT27Value(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00});
        assertThat(value.toByteArray()).containsExactly(0xFF, 0xFF, 0x00, 0x00);
        assertValidAndOn(value, true, false);

        assertThat(value.toText()).isEqualTo("" + //
                "0=off, 1=off, 2=off, 3=off, 4=off, 5=off, 6=off, 7=off, " + // 1st byte
                "8=off, 9=off, 10=off, 11=off, 12=off, 13=off, 14=off, 15=off" // 2nd byte
        );
    }

    @Test
    @DisplayName("#(byte[]) with: All Valid, All On")
    void testByte_allValid_allOn() {
        final var value = new DPT27Value(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertThat(value.toByteArray()).containsExactly(0xFF, 0xFF, 0xFF, 0xFF);
        assertValidAndOn(value, true, true);

        assertThat(value.toText()).isEqualTo("" + //
                "0=on, 1=on, 2=on, 3=on, 4=on, 5=on, 6=on, 7=on, " + // 1st byte
                "8=on, 9=on, 10=on, 11=on, 12=on, 13=on, 14=on, 15=on" // 2nd byte
        );
    }

    @Test
    @DisplayName("#(byte[]) with: All Valid, Mixed On/Off")
    void testByte_allValid_mixedOnOff() {
        final var value = new DPT27Value(new byte[]{(byte) 0xFF, (byte) 0xFF, 0b0110_0011, (byte) 0b1010_1011});
        assertThat(value.toByteArray()).containsExactly(0xFF, 0xFF, 0x63, 0xAB);
        assertValidAndOn(value,  0, true, true ); // .... .... .... ...1
        assertValidAndOn(value,  1, true, true ); // .... .... .... ..1.
        assertValidAndOn(value,  2, true, false); // .... .... .... .0..
        assertValidAndOn(value,  3, true, true ); // .... .... .... 1...
        assertValidAndOn(value,  4, true, false); // .... .... ...0 ....
        assertValidAndOn(value,  5, true, true ); // .... .... ..1. ....
        assertValidAndOn(value,  6, true, false); // .... .... .0.. ....
        assertValidAndOn(value,  7, true, true ); // .... .... 1... ....

        assertValidAndOn(value,  8, true, true ); // .... ...1 .... ....
        assertValidAndOn(value,  9, true, true ); // .... ..1. .... ....
        assertValidAndOn(value, 10, true, false); // .... .0.. .... ....
        assertValidAndOn(value, 11, true, false); // .... 0... .... ....
        assertValidAndOn(value, 12, true, false); // ...0 .... .... ....
        assertValidAndOn(value, 13, true, true ); // ..1. .... .... ....
        assertValidAndOn(value, 14, true, true ); // .1.. .... .... ....
        assertValidAndOn(value, 15, true, false); // 0... .... .... ....

        assertThat(value.toText()).isEqualTo("" + //
                "0=on, 1=on, 2=off, 3=on, 4=off, 5=on, 6=off, 7=on, " + // 1st byte
                "8=on, 9=on, 10=off, 11=off, 12=off, 13=on, 14=on, 15=off" // 2nd byte
        );
    }

    @Test
    @DisplayName("#(byte[]) with: Mixed Valid/Invalid, All On")
    void testByte_mixedValid_allOn() {
        final var value = new DPT27Value(new byte[]{0b0001_0110, (byte) 0b1101_1110, (byte) 0xFF, (byte) 0xFF});
        assertThat(value.toByteArray()).containsExactly(0x16, 0xDE, 0xFF, 0xFF);
        assertValidAndOn(value,  0, false,true); // .... .... .... ...0
        assertValidAndOn(value,  1, true ,true); // .... .... .... ..1.
        assertValidAndOn(value,  2, true ,true); // .... .... .... .1..
        assertValidAndOn(value,  3, true ,true); // .... .... .... 1...
        assertValidAndOn(value,  4, true ,true); // .... .... ...1 ....
        assertValidAndOn(value,  5, false,true); // .... .... ..0. ....
        assertValidAndOn(value,  6, true ,true); // .... .... .1.. ....
        assertValidAndOn(value,  7, true ,true); // .... .... 1... ....
        assertValidAndOn(value,  8, false,true); // .... ...0 .... ....
        assertValidAndOn(value,  9, true ,true); // .... ..1. .... ....
        assertValidAndOn(value, 10, true ,true); // .... .1.. .... ....
        assertValidAndOn(value, 11, false,true); // .... 0... .... ....
        assertValidAndOn(value, 12, true ,true); // ...1 .... .... ....
        assertValidAndOn(value, 13, false,true); // ..0. .... .... ....
        assertValidAndOn(value, 14, false,true); // .0.. .... .... ....
        assertValidAndOn(value, 15, false,true); // 0... .... .... ....

        assertThat(value.toText()).isEqualTo("" + //
                "1=on, 2=on, 3=on, 4=on, 6=on, 7=on, " + // 1st byte
                "9=on, 10=on, 12=on" // 2nd byte
        );
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var value = new DPT27Value(new byte[] {
                       0b0011_0001, 0b0111_0110,
                (byte) 0b1001_0000, 0b0010_0010
                //       ..01 ...0    .010 .01.
        });
        assertThat(value).hasToString(
                "DPT27Value{" +
                        "dpt=27.001, " +
                        "info={1=on, 2=off, 4=off, 5=on, 6=off, 8=off, 12=on, 13=off}" +
                        "}"
        );
    }

    @Test
    @DisplayName("#(byte[]) with invalid byte length")
    void testBytesOutOfRange() {
        // expected: 4 bytes, provided 3 bytes
        assertThatThrownBy(() -> new DPT27Value(new byte[3]))
                .isInstanceOf(ArrayIndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT27Value.class).withIgnoredFields("dpt").verify();
    }

    private void assertValidAndOn(final DPT27Value value, final boolean isValid, final boolean isOn) {
        for (int i = 0; i < 15; i++) {
            assertValidAndOn(value, i, isValid, isOn);
        }
    }

    private void assertValidAndOn(final DPT27Value value, final int index, final boolean isValid, final boolean isOn) {
        assertThat(value.isValid(index)).isEqualTo(isValid);
        assertThat(value.isOn(index)).isEqualTo(isOn);
    }

}
