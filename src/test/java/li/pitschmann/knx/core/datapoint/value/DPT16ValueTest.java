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

import li.pitschmann.knx.core.datapoint.DPT16;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT16Value}
 *
 * @author PITSCHR
 */
class DPT16ValueTest {

    @Test
    @DisplayName("#(DPT16.ASCII, byte[]) with characters: abcXYZ")
    void testByteASCII() {
        final var value = new DPT16Value(DPT16.ASCII, new byte[]{0x61, 0x62, 0x63, 0x58, 0x59, 0x5A});
        assertThat(value.getCharacters()).isEqualTo("abcXYZ");
        assertThat(value.toByteArray()).containsExactly(
                // padded with 8 empty 0x00 as the byte array must be a 14-byte array
                0x61, 0x62, 0x63, // abc
                0x58, 0x59, 0x5A, // XYZ
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        );

        assertThat(value.toText()).isEqualTo("abcXYZ");
    }

    @Test
    @DisplayName("#(DPT16.ISO_8859_1, byte[]) with characters: äöüÄÖÜ123")
    void testByteISO() {
        final var value = new DPT16Value(DPT16.ISO_8859_1, new byte[]{
                (byte) 0xE4, (byte) 0xF6, (byte) 0xFC, // äöü
                (byte) 0xC4, (byte) 0xD6, (byte) 0xDC, // ÄÖÜ
                0x31, 0x32, 0x33 // 123
        });
        assertThat(value.getCharacters()).isEqualTo("äöüÄÖÜ123");
        assertThat(value.toByteArray()).containsExactly(
                // padded with 5 empty 0x00 as the byte array must be a 14-byte array
                0xE4, 0xF6, 0xFC, // äöü
                0xC4, 0xD6, 0xDC, // ÄÖÜ
                0x31, 0x32, 0x33, // 123
                0x00, 0x00, 0x00, 0x00, 0x00
        );

        assertThat(value.toText()).isEqualTo("äöüÄÖÜ123");
    }

    @Test
    @DisplayName("#(DPT16.ASCII, byte[]) with null bytes")
    void testNullBytes() {
        final var value = new DPT16Value(DPT16.ASCII, (byte[]) null);
        assertThat(value.getCharacters()).isEqualTo("");
        assertThat(value.toByteArray()).containsExactly(new byte[14]);

        assertThat(value.toText()).isEqualTo("");
    }

    @Test
    @DisplayName("#(DPT16.ASCII, byte[]) with empty byte array")
    void testEmptyBytes() {
        final var value = new DPT16Value(DPT16.ASCII, new byte[0]);
        assertThat(value.getCharacters()).isEqualTo("");
        assertThat(value.toByteArray()).containsExactly(new byte[14]);

        assertThat(value.toText()).isEqualTo("");
    }

    @Test
    @DisplayName("#(DPT16.ASCII, String) with null string")
    void testNullString() {
        final var value = new DPT16Value(DPT16.ASCII, (String) null);
        assertThat(value.getCharacters()).isEqualTo("");
        assertThat(value.toByteArray()).containsExactly(new byte[14]);

        assertThat(value.toText()).isEqualTo("");
    }

    @Test
    @DisplayName("#(DPT16.ASCII, String) with empty string")
    void testEmptyString() {
        final var value = new DPT16Value(DPT16.ASCII, "");
        assertThat(value.getCharacters()).isEqualTo("");
        assertThat(value.toByteArray()).containsExactly(new byte[14]);

        assertThat(value.toText()).isEqualTo("");
    }

    @Test
    @DisplayName("#(DPT16.ASCII, byte[]) with unsupported character 'ä' (0xE4)")
    void testBytesUnsupportedCharacter() {
        assertThatThrownBy(() -> new DPT16Value(DPT16.ASCII, new byte[]{(byte) 0xE4}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Issue during decoding charset 'US-ASCII' with: 0xE4");
    }

    @Test
    @DisplayName("#(DPT16.ASCII, String) with unsupported character 'ä' (0xE4)")
    void testStringUnsupportedCharacter() {
        assertThatThrownBy(() -> new DPT16Value(DPT16.ASCII, "ä"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given characters cannot be encoded by DPT '16.000': ä");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueAscii = new DPT16Value(DPT16.ASCII, "abcABC123");
        assertThat(valueAscii).hasToString(
                "DPT16Value{dpt=16.000, characters=abcABC123, " +
                        "byteArray=0x61 62 63 41 42 43 31 32 33 00 00 00 00 00}"
        );

        final var valueIso = new DPT16Value(DPT16.ISO_8859_1, "abcäöüÄÖÜ123");
        assertThat(valueIso).hasToString(
                "DPT16Value{dpt=16.001, characters=abcäöüÄÖÜ123, " +
                        "byteArray=0x61 62 63 E4 F6 FC C4 D6 DC 31 32 33 00 00}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT16Value.class).verify();
    }
}
