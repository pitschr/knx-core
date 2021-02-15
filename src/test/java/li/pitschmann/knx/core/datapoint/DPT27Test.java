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

import li.pitschmann.knx.core.datapoint.value.DPT27Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT27}
 *
 * @author PITSCHR
 */
class DPT27Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT27.COMBINED_INFO_ON_OFF;
        assertThat(dpt.getId()).isEqualTo("27.001");
        assertThat(dpt.getDescription()).isEqualTo("Combined Info On/Off");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT27.COMBINED_INFO_ON_OFF;
        // byte is supported for length == 4 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isFalse();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
        assertThat(dpt.isCompatible(new byte[3])).isFalse();
        assertThat(dpt.isCompatible(new byte[4])).isTrue();
        assertThat(dpt.isCompatible(new byte[5])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT27.COMBINED_INFO_ON_OFF;
        // String is not supported -> always false
        for (int i = 0; i < 10; i++) {
            assertThat(dpt.isCompatible(new String[i])).isFalse();
        }
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT27.COMBINED_INFO_ON_OFF;
        assertThat(dpt.parse(new byte[]{0b0000_0000, 0b0000_0000, 0b0000_0000, 0b0000_0000})).isInstanceOf(DPT27Value.class);
        assertThat(dpt.parse(new byte[]{0b0111_1111, 0b0111_1111, 0b0111_1111, 0b0111_1111})).isInstanceOf(DPT27Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT27.COMBINED_INFO_ON_OFF;
        // parse for string not supported
        assertThatThrownBy(() -> dpt.parse(new String[0])).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Test #of(byte, byte, byte, byte)")
    void testOf() {
        final var dpt = DPT27.COMBINED_INFO_ON_OFF;
        assertThat(dpt.of((byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44)).isInstanceOf(DPT27Value.class);
    }
}
