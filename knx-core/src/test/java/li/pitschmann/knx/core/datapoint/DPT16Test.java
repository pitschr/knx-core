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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPT16Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    public void testByteParse() {
        // ASCII characters only
        assertThat(DPT16.ASCII.parse(new byte[]{'H', 'a', 'l', 'l', 'o'})).isInstanceOf(DPT16Value.class);
        assertThat(DPT16.ASCII.parse(new byte[]{'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!', '!', '!'})).isInstanceOf(DPT16Value.class);
        assertThat(DPT16.ASCII.parse(new byte[]{0x48 /*H*/, 0x61 /*a*/, 0x6C /*l*/, 0x6C /*l*/, 0x6F /*o*/})).isInstanceOf(DPT16Value.class);

        // ISO-8859-1 characters (umlauts)
        assertThat(DPT16.ISO_8859_1.parse(new byte[]{'H', (byte) 'ä', 'l', 'l', (byte) 'ö'})).isInstanceOf(DPT16Value.class);
        assertThat(DPT16.ISO_8859_1.parse(new byte[]{0x48 /*H*/, (byte) 0xE4 /*ä*/, 0x6C /*l*/, 0x6C /*l*/, (byte) 0xF6 /*ö*/})).isInstanceOf(DPT16Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    public void testStringParse() {
        // ASCII characters only
        assertThat(DPT16.ASCII.parse(new String[]{"Hallo"})).isInstanceOf(DPT16Value.class);
        assertThat(DPT16.ASCII.parse(new String[]{"Hello World!!!"})).isInstanceOf(DPT16Value.class);
        assertThat(DPT16.ASCII.parse(new String[]{"0x48" /*H*/, "0x61" /*a*/, "0x6C" /*l*/, "0x6C" /*l*/, "0x6F" /*o*/})).isInstanceOf(DPT16Value.class);

        // ISO-8859-1 characters (umlauts)
        assertThat(DPT16.ISO_8859_1.parse(new String[]{"Hällö"})).isInstanceOf(DPT16Value.class);
        assertThat(DPT16.ISO_8859_1.parse(new String[]{"0x48" /*H*/, "0xE4" /*ä*/, "0x6C" /*l*/, "0x6C" /*l*/, "0xF6" /*ö*/})).isInstanceOf(DPT16Value.class);
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
    @DisplayName("Test #toByteArray(String)")
    void testToByteArray() {
        // ASCII
        assertThat(DPT16.ASCII.toByteArray("Lorem Ipsum"))
                .containsExactly(0x4c, 0x6f, 0x72, 0x65, 0x6d, 0x20, 0x49, 0x70, 0x73, 0x75, 0x6d, 0x00, 0x00, 0x00);
        assertThat(DPT16.ASCII.toByteArray("Dolor Sit Amet"))
                .containsExactly(0x44, 0x6F, 0x6C, 0x6F, 0x72, 0x20, 0x53, 0x69, 0x74, 0x20, 0x41, 0x6D, 0x65, 0x74);
        // ISO-8859-1
        assertThat(DPT16.ISO_8859_1.toByteArray("äöü"))
                .containsExactly(0xE4, 0xF6, 0xFC, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);
        assertThat(DPT16.ISO_8859_1.toByteArray("ÜÖÄ"))
                .containsExactly(0xDC, 0xD6, 0xC4, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);
    }
}
