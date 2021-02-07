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

import li.pitschmann.knx.core.datapoint.value.DPT28Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT28}
 *
 * @author PITSCHR
 */
class DPT28Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT28.UTF_8;
        assertThat(dpt.getId()).isEqualTo("28.001");
        assertThat(dpt.getDescription()).isEqualTo("UTF-8 Characters");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT28.UTF_8;
        // byte is always supported (even if null/empty)
        assertThat(dpt.isCompatible((byte[]) null)).isTrue();
        for (int i = 0; i < 10; i++) {
            assertThat(dpt.isCompatible(new byte[i])).isTrue();
        }
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT28.UTF_8;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT28.UTF_8;

        assertThat(dpt.parse(new byte[]{0x48 /*H*/, (byte) 0xE4 /*ä*/, 0x6C /*l*/, 0x6C /*l*/, (byte) 0xF6 /*ö*/})).isInstanceOf(DPT28Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0xEA, (byte) 0xB5, (byte) 0xAD /* 국 */})).isInstanceOf(DPT28Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0xE6, (byte) 0x97, (byte) 0x85 /* 旅 */})).isInstanceOf(DPT28Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[]) with null/empty")
    void testStringParse_null() {
        final var dpt = DPT28.UTF_8;

        assertThat(dpt.parse(new String[] { null }).getCharacters()).isEmpty();
        assertThat(dpt.parse(new String[] { "" } ).getCharacters()).isEmpty();
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var texts = new String[]{
                "abc123",      // ASCII
                "äöüÄÖÜ",      // ISO-8859-1
                "דת דפים",     // Hebrew
                "الثقيلة بال", // Arabic
                "側経意責",     // Chinese
                "Λορεμ ιπσθμ", // Greek
                "Лорем ипсум"  // Russian
        };
        for (var text : texts) {
            final var value = DPT28.UTF_8.parse(new String[]{text});
            assertThat(value.getCharacters()).isEqualTo(text);
        }
    }

    @Test
    @DisplayName("Test #of(String)")
    void testOf() {
        assertThat(DPT28.UTF_8.of("abc 123 äöü")).isInstanceOf(DPT28Value.class);
        assertThat(DPT28.UTF_8.of("側経 ум لث")).isInstanceOf(DPT28Value.class);
    }
}
