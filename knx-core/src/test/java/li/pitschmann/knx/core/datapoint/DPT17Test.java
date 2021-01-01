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

import li.pitschmann.knx.core.datapoint.value.DPT17Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT17}
 *
 * @author PITSCHR
 */
class DPT17Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT17.SCENE_NUMBER;
        assertThat(dpt.getId()).isEqualTo("17.001");
        assertThat(dpt.getDescription()).isEqualTo("Scene Number");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT17.SCENE_NUMBER;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT17.SCENE_NUMBER;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    public void testByteParse() {
        final var dpt = DPT17.SCENE_NUMBER;
        assertThat(dpt.parse(new byte[]{0x00})).isInstanceOf(DPT17Value.class);
        assertThat(dpt.parse(new byte[]{0x3F})).isInstanceOf(DPT17Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    public void testStringParse() {
        final var dpt = DPT17.SCENE_NUMBER;
        assertThat(dpt.parse(new String[]{"0"})).isInstanceOf(DPT17Value.class);
        assertThat(dpt.parse(new String[]{"63"})).isInstanceOf(DPT17Value.class);
    }

    @Test
    @DisplayName("Test #of(int)")
    void testOf() {
        final var dpt = DPT17.SCENE_NUMBER;
        assertThat(dpt.of(0)).isInstanceOf(DPT17Value.class);
        assertThat(dpt.of(63)).isInstanceOf(DPT17Value.class);
    }

    @Test
    @DisplayName("Test #toByteArray(int)")
    void testToByteArray() {
        final var dpt = DPT17.SCENE_NUMBER;
        assertThat(dpt.toByteArray(0)).containsExactly(0x00);
        assertThat(dpt.toByteArray(63)).containsExactly(0x3F);
    }
}
