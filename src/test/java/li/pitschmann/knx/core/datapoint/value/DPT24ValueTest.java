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
 * Test {@link DPT24Value}
 *
 * @author PITSCHR
 */
class DPT24ValueTest {

    @Test
    @DisplayName("#(byte[]) with characters: ABC 123 äöü")
    void testByte() {
        final var value = new DPT24Value(new byte[]{
                0x41, 0x42, 0x43,                           // ABC
                0x20,                                       // space
                0x31, 0x32, 0x33,                           // 123
                0x20,                                       // space
                (byte) 0xE4, (byte) 0xF6, (byte) 0xFC,      // äöü
                0x00                                        // termination
        });
        assertThat(value.getCharacters()).isEqualTo("ABC 123 äöü");
        assertThat(value.toByteArray()).containsExactly(
                0x41, 0x42, 0x43,                           // ABC
                0x20,                                       // space
                0x31, 0x32, 0x33,                           // 123
                0x20,                                       // space
                0xE4, 0xF6, 0xFC,                           // äöü
                0x00                                        // (termination)
        );

        assertThat(value.toText()).isEqualTo("ABC 123 äöü");
    }

    @Test
    @DisplayName("#(byte[]) with null bytes")
    void testNullBytes() {
        final var value = new DPT24Value((byte[]) null);
        assertThat(value.getCharacters()).isEmpty();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEmpty();
    }

    @Test
    @DisplayName("#(byte[]) with empty byte array")
    void testEmptyBytes() {
        final var value = new DPT24Value(new byte[0]);
        assertThat(value.getCharacters()).isEmpty();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEmpty();
    }

    @Test
    @DisplayName("#(String) with null string")
    void testNullString() {
        final var value = new DPT24Value((String) null);
        assertThat(value.getCharacters()).isEmpty();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEmpty();
    }

    @Test
    @DisplayName("#(String) with empty string")
    void testEmptyString() {
        final var value = new DPT24Value("");
        assertThat(value.getCharacters()).isEmpty();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEmpty();
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueAscii = new DPT24Value("abcABC123");
        assertThat(valueAscii).hasToString(
                "DPT24Value{dpt=24.001, characters=abcABC123, " +
                        "byteArray=0x61 62 63 41 42 43 31 32 33 00}"
        );

        final var valueUmlauts = new DPT24Value("äöü ÄÖÜ");
        assertThat(valueUmlauts).hasToString(
                "DPT24Value{dpt=24.001, characters=äöü ÄÖÜ, " +
                        "byteArray=0xE4 F6 FC 20 C4 D6 DC 00}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT24Value.class).withIgnoredFields("dpt").verify();
    }
}
