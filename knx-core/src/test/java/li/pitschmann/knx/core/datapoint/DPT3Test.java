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

import li.pitschmann.knx.core.datapoint.value.DPT3Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT3}
 *
 * @author PITSCHR
 */
class DPT3Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT3.CONTROL_BLINDS;
        assertThat(dpt.getId()).isEqualTo("3.008");
        assertThat(dpt.getDescription()).isEqualTo("Control Blinds");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT3.CONTROL_BLINDS;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT3.CONTROL_BLINDS;
        // String is supported for length == 1 or 2 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isTrue();
        assertThat(dpt.isCompatible(new String[3])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT3.CONTROL_BLINDS;
        assertThat(dpt.parse(new byte[]{0x00})).isInstanceOf(DPT3Value.class);
        assertThat(dpt.parse(new byte[]{0x03})).isInstanceOf(DPT3Value.class);
        assertThat(dpt.parse(new byte[]{0x08})).isInstanceOf(DPT3Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT3.CONTROL_BLINDS;
        assertThat(dpt.parse(new String[]{"0"})).isInstanceOf(DPT3Value.class);
        assertThat(dpt.parse(new String[]{"2"})).isInstanceOf(DPT3Value.class);
        assertThat(dpt.parse(new String[]{"controlled", "2"})).isInstanceOf(DPT3Value.class);
        assertThat(dpt.parse(new String[]{"controlled", "7"})).isInstanceOf(DPT3Value.class);
    }

    @Test
    @DisplayName("Test #getDPT1()")
    void testGetDPT1() {
        assertThat(DPT3.CONTROL_BLINDS.getDPT1()).isSameAs(DPT1.UP_DOWN);
        assertThat(DPT3.CONTROL_DIMMING.getDPT1()).isSameAs(DPT1.STEP);
    }

    @Test
    @DisplayName("Test #of(boolean, StepInterval)")
    void testOfStepInterval() {
        // not controlled, step = 0 (STOP)
        assertThat(DPT3.CONTROL_BLINDS.of(false, DPT3Value.StepInterval.STOP)).isInstanceOf(DPT3Value.class);
        // controlled, step = 2 (PERCENT_50)
        assertThat(DPT3.CONTROL_BLINDS.of(true, DPT3Value.StepInterval.PERCENT_50)).isInstanceOf(DPT3Value.class);
        // not controlled, step = 7 (PERCENT_1)
        assertThat(DPT3.CONTROL_BLINDS.of(false, DPT3Value.StepInterval.PERCENT_1)).isInstanceOf(DPT3Value.class);
        // controlled, step =  7 (PERCENT_1)
        assertThat(DPT3.CONTROL_BLINDS.of(true, DPT3Value.StepInterval.PERCENT_1)).isInstanceOf(DPT3Value.class);
    }
}
