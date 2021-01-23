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

import li.pitschmann.knx.core.datapoint.value.DPT2Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT2}
 *
 * @author PITSCHR
 */
class DPT2Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT2.SWITCH_CONTROL;
        assertThat(dpt.getId()).isEqualTo("2.001");
        assertThat(dpt.getDescription()).isEqualTo("Switch Controlled");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT2.SWITCH_CONTROL;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT2.SWITCH_CONTROL;
        // String is supported for length == [1, 2] only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isTrue();
        assertThat(dpt.isCompatible(new String[3])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT2.SWITCH_CONTROL;
        assertThat(dpt.parse(new byte[]{0x00})).isInstanceOf(DPT2Value.class);
        assertThat(dpt.parse(new byte[]{0x01})).isInstanceOf(DPT2Value.class);
        assertThat(dpt.parse(new byte[]{0x02})).isInstanceOf(DPT2Value.class);
        assertThat(dpt.parse(new byte[]{0x03})).isInstanceOf(DPT2Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT2.SWITCH_CONTROL;

        // not-controlled, false
        final var falseValue = dpt.parse(new String[]{"false"});
        assertThat(falseValue.getValue()).isFalse();
        assertThat(falseValue.isControlled()).isFalse();
        // not-controlled, true
        final var trueValue = dpt.parse(new String[]{"true"});
        assertThat(trueValue.getValue()).isTrue();
        assertThat(trueValue.isControlled()).isFalse();
        // controlled, false
        final var falseControlled = dpt.parse(new String[]{"false", "controlled"});
        assertThat(falseControlled.getValue()).isFalse();
        assertThat(falseControlled.isControlled()).isTrue();
        // controlled, true
        final var trueControlled = dpt.parse(new String[]{"true", "controlled"});
        assertThat(trueControlled.getValue()).isTrue();
        assertThat(trueControlled.isControlled()).isTrue();
    }

    @Test
    @DisplayName("Test #getDPT1()")
    void testGetDPT1() {
        assertThat(DPT2.BOOL_CONTROL.getDPT1()).isSameAs(DPT1.BOOL);
        assertThat(DPT2.STEP_CONTROL.getDPT1()).isSameAs(DPT1.STEP);
        assertThat(DPT2.SWITCH_CONTROL.getDPT1()).isSameAs(DPT1.SWITCH);
    }

    @Test
    @DisplayName("Test #of(boolean, boolean)")
    void testOf() {
        // not-controlled, false
        final var falseValue = DPT2.SWITCH_CONTROL.of(false, false);
        assertThat(falseValue.getValue()).isFalse();
        assertThat(falseValue.isControlled()).isFalse();
        // not-controlled, true
        final var trueValue = DPT2.SWITCH_CONTROL.of(false, true);
        assertThat(trueValue.getValue()).isTrue();
        assertThat(trueValue.isControlled()).isFalse();
        // controlled, false
        final var falseControlled = DPT2.SWITCH_CONTROL.of(true, false);
        assertThat(falseControlled.getValue()).isFalse();
        assertThat(falseControlled.isControlled()).isTrue();
        // controlled, true
        final var trueControlled = DPT2.SWITCH_CONTROL.of(true, true);
        assertThat(trueControlled.getValue()).isTrue();
        assertThat(trueControlled.isControlled()).isTrue();
    }
}
