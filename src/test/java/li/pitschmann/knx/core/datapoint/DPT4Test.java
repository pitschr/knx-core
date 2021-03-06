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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPT4Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT4}
 *
 * @author PITSCHR
 */
class DPT4Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT4.ASCII;
        assertThat(dpt.getId()).isEqualTo("4.001");
        assertThat(dpt.getDescription()).isEqualTo("ASCII Character");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT4.ASCII;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT4.ASCII;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[2])).isFalse();

        // String is supported only when content of 1st String is a single character
        assertThat(dpt.isCompatible(new String[1])).isFalse();       // not supported, because empty
        assertThat(dpt.isCompatible(new String[]{"a"})).isTrue();
        assertThat(dpt.isCompatible(new String[]{"abc"})).isFalse(); // not supported, because more than 1 character
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT4.ASCII;
        assertThat(dpt.parse(new byte[]{0x61})).isInstanceOf(DPT4Value.class);
        assertThat(dpt.parse(new byte[]{0x62})).isInstanceOf(DPT4Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        // character: a (ASCII)
        final var a = DPT4.ASCII.parse(new String[]{"a"});
        assertThat(a.getCharacter()).isEqualTo('a');
        // character: z (ASCII)
        final var z = DPT4.ASCII.parse(new String[]{"z"});
        assertThat(z.getCharacter()).isEqualTo('z');

        // character: a (ISO_8859_1)
        final var ae = DPT4.ISO_8859_1.parse(new String[]{"ä"});
        assertThat(ae.getCharacter()).isEqualTo('ä');
        // character: a (ISO_8859_1)
        final var oe = DPT4.ISO_8859_1.parse(new String[]{"Ö"});
        assertThat(oe.getCharacter()).isEqualTo('Ö');
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPT4.ASCII;

        // no supported character for ASCII format provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"ä"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given character cannot be encoded by DPT '4.001': ä");
    }

    @Test
    @DisplayName("Test #getCharset() and #getCharsetDecoder()")
    void testCharset() {
        // ASCII
        assertThat(DPT4.ASCII.getCharset()).isSameAs(StandardCharsets.US_ASCII);
        assertThat(DPT4.ASCII.getCharsetDecoder().charset()).isSameAs(StandardCharsets.US_ASCII);
        // ISO-8859-1
        assertThat(DPT4.ISO_8859_1.getCharset()).isSameAs(StandardCharsets.ISO_8859_1);
        assertThat(DPT4.ISO_8859_1.getCharsetDecoder().charset()).isSameAs(StandardCharsets.ISO_8859_1);
    }

    @Test
    @DisplayName("Test #of(char)")
    void testOf() {
        // ASCII
        assertThat(DPT4.ASCII.of('a')).isInstanceOf(DPT4Value.class);
        assertThat(DPT4.ASCII.of('z')).isInstanceOf(DPT4Value.class);
        // ISO-8859-1
        assertThat(DPT4.ISO_8859_1.of('ä')).isInstanceOf(DPT4Value.class);
        assertThat(DPT4.ISO_8859_1.of('Ö')).isInstanceOf(DPT4Value.class);
    }
}
