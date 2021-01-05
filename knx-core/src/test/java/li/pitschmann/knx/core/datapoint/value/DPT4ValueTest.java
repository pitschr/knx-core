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

import li.pitschmann.knx.core.datapoint.DPT4;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT4Value}
 *
 * @author PITSCHR
 */
class DPT4ValueTest {

    @Test
    @DisplayName("#(DPT4.ASCII, byte) with: character 'a'")
    void testByteA() {
        final var value = new DPT4Value(DPT4.ASCII, (byte) 0x61);
        assertThat(value.getCharacter()).isEqualTo('a');
        assertThat(value.toByteArray()).containsExactly(0x61);

        assertThat(value.toText()).isEqualTo("char 'a'");
    }

    @Test
    @DisplayName("#(DPT4.ASCII, byte) with: character 'Z'")
    void testByteZ() {
        final var value = new DPT4Value(DPT4.ASCII, (byte) 0x5A);
        assertThat(value.getCharacter()).isEqualTo('Z');
        assertThat(value.toByteArray()).containsExactly(0x5A);

        assertThat(value.toText()).isEqualTo("char 'Z'");
    }

    @Test
    @DisplayName("#(DPT4.ISO_8859_1, char) with: character 'ä'")
    void testCharacterAE() {
        final var value = new DPT4Value(DPT4.ISO_8859_1, 'ä');
        assertThat(value.getCharacter()).isEqualTo('ä');
        assertThat(value.toByteArray()).containsExactly(0xE4);

        assertThat(value.toText()).isEqualTo("char 'ä'");
    }

    @Test
    @DisplayName("#(DPT4.ISO_8859_1, char) with: character 'Ö'")
    void testCharacterOE() {
        final var value = new DPT4Value(DPT4.ISO_8859_1, 'Ö');
        assertThat(value.getCharacter()).isEqualTo('Ö');
        assertThat(value.toByteArray()).containsExactly(0xD6);

        assertThat(value.toText()).isEqualTo("char 'Ö'");
    }

    @Test
    @DisplayName("#(DPT4.ASCII, char) with unsupported character 'ä' (0xE4)")
    void testUnsupportedCharacter() {
        assertThatThrownBy(() -> new DPT4Value(DPT4.ASCII, (byte)0xE4))
                .isInstanceOf(KnxIllegalArgumentException.class)
                .hasMessage("Issue during decoding charset 'US-ASCII' with value: 0xE4");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueAscii = new DPT4Value(DPT4.ASCII, 'A');
        assertThat(valueAscii).hasToString(
                "DPT4Value{dpt=4.001, character=A, byteArray=0x41}"
        );

        final var valueIso = new DPT4Value(DPT4.ISO_8859_1, 'ß');
        assertThat(valueIso).hasToString(
                "DPT4Value{dpt=4.002, character=ß, byteArray=0xDF}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT4Value(DPT4.ASCII, 'a');
        final var value2 = new DPT4Value(DPT4.ASCII, 'a');

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(value2).isEqualTo(value);
        assertThat(value2).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT4Value(DPT4.ISO_8859_1, 'a'));
        assertThat(value).isNotEqualTo(new DPT4Value(DPT4.ASCII, 'b'));
    }
}
