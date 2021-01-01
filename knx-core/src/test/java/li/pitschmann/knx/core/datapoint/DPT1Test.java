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

import li.pitschmann.knx.core.datapoint.value.DPT1Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT1}
 *
 * @author PITSCHR
 */
class DPT1Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT1.SWITCH;
        assertThat(dpt.getId()).isEqualTo("1.001");
        assertThat(dpt.getDescription()).isEqualTo("Switch");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT1.SWITCH;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT1.SWITCH;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT1.SWITCH;
        assertThat(dpt.parse(new byte[]{0x00})).isInstanceOf(DPT1Value.class);
        assertThat(dpt.parse(new byte[]{0x01})).isInstanceOf(DPT1Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT1.SWITCH;
        assertThat(dpt.parse(new String[]{"false"})).isInstanceOf(DPT1Value.class);
        assertThat(dpt.parse(new String[]{"true"})).isInstanceOf(DPT1Value.class);
        assertThat(dpt.parse(new String[]{"0"})).isInstanceOf(DPT1Value.class);
        assertThat(dpt.parse(new String[]{"1"})).isInstanceOf(DPT1Value.class);
    }

    @Test
    @DisplayName("Test #getTextFor()")
    void testTextFor() {
        // Switch
        assertThat(DPT1.SWITCH.getTextFor(true)).isEqualTo("on");
        assertThat(DPT1.SWITCH.getTextFor(false)).isEqualTo("off");
        // Alaram
        assertThat(DPT1.ALARM.getTextFor(true)).isEqualTo("Alarm");
        assertThat(DPT1.ALARM.getTextFor(false)).isEqualTo("No Alarm");
    }

    @Test
    @DisplayName("Test #getTextForTrue()")
    void testTextForTrue() {
        assertThat(DPT1.SWITCH.getTextForTrue()).isEqualTo("on");
        assertThat(DPT1.ALARM.getTextForTrue()).isEqualTo("Alarm");
    }

    @Test
    @DisplayName("Test #getTextForFalse()")
    void testTextForFalse() {
        assertThat(DPT1.SWITCH.getTextForFalse()).isEqualTo("off");
        assertThat(DPT1.ALARM.getTextForFalse()).isEqualTo("No Alarm");
    }

    @Test
    @DisplayName("Test #of(boolean)")
    void testOf() {
        // false
        assertThat(DPT1.SWITCH.of(false)).isInstanceOf(DPT1Value.class);
        assertThat(DPT1.UP_DOWN.of(false)).isInstanceOf(DPT1Value.class);
        // true
        assertThat(DPT1.SWITCH.of(true)).isInstanceOf(DPT1Value.class);
        assertThat(DPT1.UP_DOWN.of(true)).isInstanceOf(DPT1Value.class);
    }

    @Test
    @DisplayName("Test #toByteArray(boolean)")
    void testToByteArray() {
        // false
        assertThat(DPT1.SWITCH.toByteArray(false)).containsExactly(0x00);
        assertThat(DPT1.UP_DOWN.toByteArray(false)).containsExactly(0x00);
        // true
        assertThat(DPT1.SWITCH.toByteArray(true)).containsExactly(0x01);
        assertThat(DPT1.UP_DOWN.toByteArray(true)).containsExactly(0x01);
    }
}
