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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DPT28Value}
 *
 * @author PITSCHR
 */
class DPT28ValueTest {

    @Test
    @DisplayName("#(byte[]) with characters: ABC 123 äöü 子老何")
    void testByte() {
        final var value = new DPT28Value(new byte[]{
                // ABC
                0x41, 0x42, 0x43,
                // space
                0x20,
                // 123
                0x31, 0x32, 0x33,
                // space
                0x20,
                // äöü
                (byte) 0xC3, (byte) 0xA4, (byte) 0xC3, (byte) 0xB6, (byte) 0xC3, (byte) 0xBC,
                // space
                0x20,
                // 子老何
                (byte) 0xE5, (byte) 0xAD, (byte) 0x90, (byte) 0xE8, (byte) 0x80, (byte) 0x81, (byte) 0xE4, (byte) 0xBD, (byte) 0x95,
                // termination
                0x00
        });
        assertThat(value.getCharacters()).isEqualTo("ABC 123 äöü 子老何");
        assertThat(value.toByteArray()).containsExactly(
                0x41, 0x42, 0x43,                                       // ABC
                0x20,                                                   // space
                0x31, 0x32, 0x33,                                       // 123
                0x20,                                                   // space
                0xC3, 0xA4, 0xC3, 0xB6, 0xC3, 0xBC,                     // äöü
                0x20,                                                   // space
                0xE5, 0xAD, 0x90, 0xE8, 0x80, 0x81, 0xE4, 0xBD, 0x95,   // 子老何
                0x00                                                    // (termination)
        );

        assertThat(value.toText()).isEqualTo("ABC 123 äöü 子老何");
    }

    @Test
    @DisplayName("#(byte[]) with null bytes")
    void testNullBytes() {
        final var value = new DPT28Value((byte[]) null);
        assertThat(value.getCharacters()).isEmpty();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEmpty();
    }

    @Test
    @DisplayName("#(byte[]) with empty byte array")
    void testEmptyBytes() {
        final var value = new DPT28Value(new byte[0]);
        assertThat(value.getCharacters()).isEmpty();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEmpty();
    }

    @Test
    @DisplayName("#(String) with null string")
    void testNullString() {
        final var value = new DPT28Value((String) null);
        assertThat(value.getCharacters()).isEmpty();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEmpty();
    }

    @Test
    @DisplayName("#(String) with empty string")
    void testEmptyString() {
        final var value = new DPT28Value("");
        assertThat(value.getCharacters()).isEmpty();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEmpty();
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueAscii = new DPT28Value("abcABC123");
        assertThat(valueAscii).hasToString(
                "DPT28Value{dpt=28.001, characters=abcABC123, " +
                        "byteArray=0x61 62 63 41 42 43 31 32 33 00}"
        );

        final var valueUmlauts = new DPT28Value("äöü ÄÖÜ");
        assertThat(valueUmlauts).hasToString(
                "DPT28Value{dpt=28.001, characters=äöü ÄÖÜ, " +
                        "byteArray=0xC3 A4 C3 B6 C3 BC 20 C3 84 C3 96 C3 9C 00}"
        );

        final var valueChinese = new DPT28Value("品 間 識");
        assertThat(valueChinese).hasToString(
                "DPT28Value{dpt=28.001, characters=品 間 識, " +
                        "byteArray=0xE5 93 81 20 E9 96 93 20 E8 AD 98 00}"
        );

        final var valueRussian = new DPT28Value("Лорем ипсум");
        assertThat(valueRussian).hasToString(
                "DPT28Value{dpt=28.001, characters=Лорем ипсум, " +
                        "byteArray=0xD0 9B D0 BE D1 80 D0 B5 D0 BC 20 D0 B8 D0 BF D1 81 D1 83 D0 BC 00}"
        );

    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT28Value.class).withIgnoredFields("dpt").verify();
    }
}
