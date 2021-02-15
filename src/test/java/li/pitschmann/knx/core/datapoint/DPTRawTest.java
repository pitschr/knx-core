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

import li.pitschmann.knx.core.datapoint.value.DPTRawValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT29}
 *
 * @author PITSCHR
 */
class DPTRawTest {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPTRaw.VALUE;
        assertThat(dpt.getId()).isEqualTo("raw");
        assertThat(dpt.getDescription()).isEqualTo("Raw Value");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPTRaw.VALUE;
        // byte is always supported
        for (int i = 0; i < 10; i++) {
            assertThat(dpt.isCompatible(new byte[i])).isTrue();
        }
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPTRaw.VALUE;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPTRaw.VALUE;
        assertThat(dpt.parse(new byte[]{0x11, 0x22, 0x33, 0x44})).isInstanceOf(DPTRawValue.class);
        assertThat(dpt.parse(new byte[]{0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xAA})).isInstanceOf(DPTRawValue.class);
    }

    @Test
    @DisplayName("Test #parse(String[]): 0x12 13 14 15 16")
    void testStringParse_withSpaces() {
        final var dpt = DPTRaw.VALUE;

        // value: 0x12 13 14 15 16
        final var valueWithSpaces = dpt.parse(new String[]{"0x12 13 14 15 16"});
        assertThat(valueWithSpaces.toByteArray()).containsExactly(0x12, 0x13, 0x14, 0x15, 0x16);
    }

    @Test
    @DisplayName("Test #parse(String[]): 0x212223242526")
    void testStringParse_withoutSpaces() {
        final var dpt = DPTRaw.VALUE;

        // value: 0x212223242526
        final var valueWithSpaces = dpt.parse(new String[]{"0x212223242526"});
        assertThat(valueWithSpaces.toByteArray()).containsExactly(0x21, 0x22, 0x23, 0x24, 0x25, 0x26);
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPTRaw.VALUE;

        // no long format provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"foobar"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Illegal hex string format: foobar");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testOf() {
        final var dpt = DPTRaw.VALUE;

        assertThat(dpt.of(new byte[0])).isInstanceOf(DPTRawValue.class);
        assertThat(dpt.of(new byte[]{0x11})).isInstanceOf(DPTRawValue.class);
    }
}
