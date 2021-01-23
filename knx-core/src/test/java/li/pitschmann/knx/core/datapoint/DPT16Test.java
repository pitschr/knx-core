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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPT16Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT16}
 *
 * @author PITSCHR
 */
class DPT16Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT16.ASCII;
        assertThat(dpt.getId()).isEqualTo("16.000");
        assertThat(dpt.getDescription()).isEqualTo("ASCII Characters");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT16.ASCII;
        // byte is supported for length <= 14 only
        assertThat(dpt.isCompatible(new byte[0])).isTrue();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[14])).isTrue();
        assertThat(dpt.isCompatible(new byte[15])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT16.ASCII;
        // String is supported for length == 1
        // and if the length of text is below 14 characters
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isFalse();
        assertThat(dpt.isCompatible(new String[2])).isFalse();

        assertThat(dpt.isCompatible(new String[]{""})).isTrue();
        assertThat(dpt.isCompatible(new String[]{"12345678901234"})).isTrue();
        assertThat(dpt.isCompatible(new String[]{"123456789012345"})).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        // ASCII characters only
        assertThat(DPT16.ASCII.parse(new byte[]{'H', 'a', 'l', 'l', 'o'})).isInstanceOf(DPT16Value.class);
        assertThat(DPT16.ASCII.parse(new byte[]{'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!', '!', '!'})).isInstanceOf(DPT16Value.class);
        assertThat(DPT16.ASCII.parse(new byte[]{0x48 /*H*/, 0x61 /*a*/, 0x6C /*l*/, 0x6C /*l*/, 0x6F /*o*/})).isInstanceOf(DPT16Value.class);

        // ISO-8859-1 characters (umlauts)
        assertThat(DPT16.ISO_8859_1.parse(new byte[]{'H', (byte) 'ä', 'l', 'l', (byte) 'ö'})).isInstanceOf(DPT16Value.class);
        assertThat(DPT16.ISO_8859_1.parse(new byte[]{0x48 /*H*/, (byte) 0xE4 /*ä*/, 0x6C /*l*/, 0x6C /*l*/, (byte) 0xF6 /*ö*/})).isInstanceOf(DPT16Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[]) with ASCII")
    void testStringParseASCII() {
        final var text = DPT16.ASCII.parse(new String[]{"Hello World!"});
        assertThat(text.getCharacters()).isEqualTo("Hello World!");
    }

    @Test
    @DisplayName("Test #parse(String[]) with ISO-8859-1")
    void testStringParseISO() {
        // value by string
        final var text = DPT16.ISO_8859_1.parse(new String[]{"Grüass di"});
        assertThat(text.getCharacters()).isEqualTo("Grüass di");
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPT16.ASCII;

        // no supported character for ASCII format provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"äöü"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given characters cannot be encoded by DPT '16.000': äöü");
    }

    @Test
    @DisplayName("Test #of(String)")
    void testOf() {
        // ASCII
        assertThat(DPT16.ASCII.of("Lorem Ipsum")).isInstanceOf(DPT16Value.class);
        assertThat(DPT16.ASCII.of("Dolor Sit Amet")).isInstanceOf(DPT16Value.class);
        // ISO-8859-1
        assertThat(DPT16.ISO_8859_1.of("äöü")).isInstanceOf(DPT16Value.class);
        assertThat(DPT16.ISO_8859_1.of("ÜÖÄ")).isInstanceOf(DPT16Value.class);
    }

    @Test
    @DisplayName("Test #getCharset()")
    void testCharset() {
        // ASCII
        assertThat(DPT16.ASCII.getCharset()).isEqualTo(StandardCharsets.US_ASCII);
        // ISO-8859-1
        assertThat(DPT16.ISO_8859_1.getCharset()).isEqualTo(StandardCharsets.ISO_8859_1);
    }
}
