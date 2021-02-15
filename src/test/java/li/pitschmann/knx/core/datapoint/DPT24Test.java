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

import li.pitschmann.knx.core.datapoint.value.DPT24Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT24}
 *
 * @author PITSCHR
 */
class DPT24Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT24.ISO_8859_1;
        assertThat(dpt.getId()).isEqualTo("24.001");
        assertThat(dpt.getDescription()).isEqualTo("ISO-8859-1 Characters");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT24.ISO_8859_1;
        // byte is always supported (even if null/empty)
        assertThat(dpt.isCompatible((byte[]) null)).isTrue();
        for (int i = 0; i < 10; i++) {
            assertThat(dpt.isCompatible(new byte[i])).isTrue();
        }
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT24.ISO_8859_1;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT24.ISO_8859_1;

        assertThat(dpt.parse(new byte[]{0x48 /*H*/, (byte) 0x65 /*e*/, 0x6C /*l*/, 0x6C /*l*/, (byte) 0x6F /*o*/})).isInstanceOf(DPT24Value.class);
        assertThat(dpt.parse(new byte[]{0x48 /*H*/, (byte) 0xE4 /*ä*/, 0x6C /*l*/, 0x6C /*l*/, (byte) 0xF6 /*ö*/})).isInstanceOf(DPT24Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[]) with null/empty")
    void testStringParse_null() {
        final var dpt = DPT24.ISO_8859_1;

        assertThat(dpt.parse(new String[] { null }).getCharacters()).isEmpty();
        assertThat(dpt.parse(new String[] { "" } ).getCharacters()).isEmpty();
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT24.ISO_8859_1;

        assertThat(dpt.parse(new String[]{ "abc 123" }).getCharacters()).isEqualTo("abc 123");
        assertThat(dpt.parse(new String[]{ "äöü ÄÖÜ" }).getCharacters()).isEqualTo("äöü ÄÖÜ");
    }

    @Test
    @DisplayName("Test #of(String)")
    void testOf() {
        assertThat(DPT24.ISO_8859_1.of("abc 123 äöü")).isInstanceOf(DPT24Value.class);
        assertThat(DPT24.ISO_8859_1.of("ABC 456 ÄÖÜ")).isInstanceOf(DPT24Value.class);
    }
}
