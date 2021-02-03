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

import li.pitschmann.knx.core.datapoint.value.DPT18Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void testByteParse() {
        final var dpt = DPT18.SCENE_CONTROL;
        assertThat(dpt.parse(new byte[]{0x00})).isInstanceOf(DPT18Value.class);
        assertThat(dpt.parse(new byte[]{0x3F})).isInstanceOf(DPT18Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0x80})).isInstanceOf(DPT18Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0xBF})).isInstanceOf(DPT18Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT18.SCENE_CONTROL;

        // not controlled, scene number: 0
        final var scene0NotControlled = dpt.parse(new String[]{"0"});
        assertThat(scene0NotControlled.isControlled()).isFalse();
        assertThat(scene0NotControlled.getSceneNumber()).isZero();
        // not controlled, scene number: 63
        final var scene63NotControlled = dpt.parse(new String[]{"63"});
        assertThat(scene63NotControlled.isControlled()).isFalse();
        assertThat(scene63NotControlled.getSceneNumber()).isEqualTo(63);
        // controlled, scene number: 0
        final var scene0Controlled = dpt.parse(new String[]{"0", "controlled"});
        assertThat(scene0Controlled.isControlled()).isTrue();
        assertThat(scene0Controlled.getSceneNumber()).isZero();
        // controlled, scene number: 63
        final var scene63Controlled = dpt.parse(new String[]{"controlled", "63"});
        assertThat(scene63Controlled.isControlled()).isTrue();
        assertThat(scene63Controlled.getSceneNumber()).isEqualTo(63);
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPT18.SCENE_CONTROL;

        // no scene number format provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"foobar"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Scene Number missing (digit between 0 and 63). Provided: [foobar]");
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
}
