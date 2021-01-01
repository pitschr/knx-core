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

import li.pitschmann.knx.core.datapoint.value.DPT18Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT18}
 *
 * @author PITSCHR
 */
class DPT18Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT18.SCENE_CONTROL;
        assertThat(dpt.getId()).isEqualTo("18.001");
        assertThat(dpt.getDescription()).isEqualTo("Scene Control");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT18.SCENE_CONTROL;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT18.SCENE_CONTROL;
        // String is supported for length == [1, 2] only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isTrue();
        assertThat(dpt.isCompatible(new String[3])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    public void testByteParse() {
        final var dpt = DPT18.SCENE_CONTROL;
        assertThat(dpt.parse(new byte[]{0x00})).isInstanceOf(DPT18Value.class);
        assertThat(dpt.parse(new byte[]{0x3F})).isInstanceOf(DPT18Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0x80})).isInstanceOf(DPT18Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0xBF})).isInstanceOf(DPT18Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    public void testStringParse() {
        final var dpt = DPT18.SCENE_CONTROL;
        assertThat(dpt.parse(new String[]{"0"})).isInstanceOf(DPT18Value.class);
        assertThat(dpt.parse(new String[]{"63"})).isInstanceOf(DPT18Value.class);
        assertThat(dpt.parse(new String[]{"controlled", "0"})).isInstanceOf(DPT18Value.class);
        assertThat(dpt.parse(new String[]{"controlled", "63"})).isInstanceOf(DPT18Value.class);
    }

    @Test
    @DisplayName("Test #of(boolean, int)")
    void testOf() {
        final var dpt = DPT18.SCENE_CONTROL;
        assertThat(dpt.of(false, 0)).isInstanceOf(DPT18Value.class);
        assertThat(dpt.of(false, 63)).isInstanceOf(DPT18Value.class);
        assertThat(dpt.of(true, 0)).isInstanceOf(DPT18Value.class);
        assertThat(dpt.of(true, 63)).isInstanceOf(DPT18Value.class);
    }

    @Test
    @DisplayName("Test #toByteArray(boolean, int)")
    void testToByteArray() {
        final var dpt = DPT18.SCENE_CONTROL;
        assertThat(dpt.toByteArray(false, 0)).containsExactly(0x00);
        assertThat(dpt.toByteArray(false, 63)).containsExactly(0x3F);
        assertThat(dpt.toByteArray(true, 0)).containsExactly(0x80);
        assertThat(dpt.toByteArray(true, 63)).containsExactly(0xBF);
    }
}
