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

import li.pitschmann.knx.core.datapoint.value.DPT6Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT6.StatusMode}
 *
 * @author PITSCHR
 */
class DPT6StatusModeTest {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT6.STATUS_MODE;
        assertThat(dpt.getId()).isEqualTo("6.020");
        assertThat(dpt.getDescription()).isEqualTo("Status Mode");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT6.STATUS_MODE;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT6.STATUS_MODE;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT6.STATUS_MODE;
        assertThat(dpt.parse(new byte[]{0x01})).isInstanceOf(DPT6Value.StatusMode.class);
        assertThat(dpt.parse(new byte[]{0x02})).isInstanceOf(DPT6Value.StatusMode.class);
        assertThatThrownBy(() -> dpt.parse(new byte[0]));
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT6.STATUS_MODE;
        assertThat(dpt.parse(new String[]{"0x11"})).isInstanceOf(DPT6Value.StatusMode.class);
        assertThat(dpt.parse(new String[]{"0xFF"})).isInstanceOf(DPT6Value.StatusMode.class);
    }

    @Test
    @DisplayName("Test #of(boolean.., Mode)")
    void testOf() {
        final var dpt = DPT6.STATUS_MODE;
        // mode: 0, no flags set
        assertThat(dpt.of(false, false, false, false, false, DPT6Value.StatusMode.Mode.MODE_0))
                .isInstanceOf(DPT6Value.StatusMode.class);
        // mode: 1, no flags set
        assertThat(dpt.of(false, false, false, false, false, DPT6Value.StatusMode.Mode.MODE_1))
                .isInstanceOf(DPT6Value.StatusMode.class);
        // mode: 2, no flags set
        assertThat(dpt.of(false, false, false, false, false, DPT6Value.StatusMode.Mode.MODE_2))
                .isInstanceOf(DPT6Value.StatusMode.class);
        // mode: 1, flags set (abcd ennn = 0110 1010)
        assertThat(dpt.of(false, true, true, false, true, DPT6Value.StatusMode.Mode.MODE_1))
                .isInstanceOf(DPT6Value.StatusMode.class);
    }

    @Test
    @DisplayName("Test #toByteArray(boolean.., Mode)")
    void testToByteArray() {
        final var dpt = DPT6.STATUS_MODE;
        // mode: 0, no flags set
        assertThat(dpt.toByteArray(false, false, false, false, false, DPT6Value.StatusMode.Mode.MODE_0))
                .containsExactly(0b0000_0001); // 0x01
        // mode: 1, no flags set
        assertThat(dpt.toByteArray(false, false, false, false, false, DPT6Value.StatusMode.Mode.MODE_1))
                .containsExactly(0b0000_0010); // 0x02
        // mode: 2, no flags set
        assertThat(dpt.toByteArray(false, false, false, false, false, DPT6Value.StatusMode.Mode.MODE_2))
                .containsExactly(0b0000_0100); // 0x04
        // mode: 1, flags set (abcd ennn = 0110 1010)
        assertThat(dpt.toByteArray(false, true, true, false, true, DPT6Value.StatusMode.Mode.MODE_1))
                .containsExactly(0b0110_1010); // 0x6A
    }
}
