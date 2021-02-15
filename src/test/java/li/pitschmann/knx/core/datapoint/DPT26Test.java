/*
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

import li.pitschmann.knx.core.datapoint.value.DPT26Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT26}
 *
 * @author PITSCHR
 */
class DPT26Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT26.SCENE_INFORMATION;
        assertThat(dpt.getId()).isEqualTo("26.001");
        assertThat(dpt.getDescription()).isEqualTo("Scene Information");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT26.SCENE_INFORMATION;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT26.SCENE_INFORMATION;
        // String is supported for length == [1, 2] only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isTrue();
        assertThat(dpt.isCompatible(new String[3])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT26.SCENE_INFORMATION;
        assertThat(dpt.parse(new byte[]{0b0000_0000})).isInstanceOf(DPT26Value.class);
        assertThat(dpt.parse(new byte[]{0b0111_1111})).isInstanceOf(DPT26Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT26.SCENE_INFORMATION;
        // scene active: false, scene number: 0
        final var scene0 = dpt.parse(new String[]{"0"});
        assertThat(scene0.isActive()).isFalse();
        assertThat(scene0.getSceneNumber()).isZero();

        // scene active: false, scene number: 17
        final var scene17Inactive = dpt.parse(new String[]{"inactive", "17"});
        assertThat(scene17Inactive.isActive()).isFalse();
        assertThat(scene17Inactive.getSceneNumber()).isEqualTo(17);

        // scene active: true, scene number: 63
        final var scene63Active = dpt.parse(new String[]{"63", "active"});
        assertThat(scene63Active.isActive()).isTrue();
        assertThat(scene63Active.getSceneNumber()).isEqualTo(63);

        // scene active: true, scene number: 63
        final var scene41True = dpt.parse(new String[]{"true", "41"});
        assertThat(scene41True.isActive()).isTrue();
        assertThat(scene41True.getSceneNumber()).isEqualTo(41);

    }

    @Test
    @DisplayName("Test #of(int)")
    void testOf() {
        final var dpt = DPT26.SCENE_INFORMATION;
        assertThat(dpt.of(false, 0)).isInstanceOf(DPT26Value.class);
        assertThat(dpt.of(true, 63)).isInstanceOf(DPT26Value.class);
    }
}
