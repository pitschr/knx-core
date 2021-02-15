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

import li.pitschmann.knx.core.datapoint.value.DPT25Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT25}
 *
 * @author PITSCHR
 */
class DPT25Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT25.BUSY_NAK_REPETITIONS;
        assertThat(dpt.getId()).isEqualTo("25.1000");
        assertThat(dpt.getDescription()).isEqualTo("Busy/Nak Repetitions");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT25.BUSY_NAK_REPETITIONS;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT25.BUSY_NAK_REPETITIONS;
        // String is not supported -> always false
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isFalse();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT25.BUSY_NAK_REPETITIONS;
        assertThat(dpt.parse(new byte[]{0x13})).isInstanceOf(DPT25Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0x33})).isInstanceOf(DPT25Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT25.BUSY_NAK_REPETITIONS;
        // parse for string not supported
        assertThatThrownBy(() -> dpt.parse(new String[0])).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Test #of(int, int)")
    void testOf() {
        final var dpt = DPT25.BUSY_NAK_REPETITIONS;
        assertThat(dpt.of(0, 0)).isInstanceOf(DPT25Value.class);
        assertThat(dpt.of(3, 3)).isInstanceOf(DPT25Value.class);
    }
}
