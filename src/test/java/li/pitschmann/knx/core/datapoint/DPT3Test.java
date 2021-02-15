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

import li.pitschmann.knx.core.datapoint.value.DPT3Value;
import li.pitschmann.knx.core.datapoint.value.StepInterval;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT3}
 *
 * @author PITSCHR
 */
class DPT3Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT3.BLINDS_CONTROL;
        assertThat(dpt.getId()).isEqualTo("3.008");
        assertThat(dpt.getDescription()).isEqualTo("Blinds Controlled");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT3.BLINDS_CONTROL;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT3.BLINDS_CONTROL;
        // String is supported for length == 1 or 2 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isTrue();
        assertThat(dpt.isCompatible(new String[3])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT3.BLINDS_CONTROL;
        assertThat(dpt.parse(new byte[]{0x00})).isInstanceOf(DPT3Value.class);
        assertThat(dpt.parse(new byte[]{0x03})).isInstanceOf(DPT3Value.class);
        assertThat(dpt.parse(new byte[]{0x08})).isInstanceOf(DPT3Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT3.BLINDS_CONTROL;

        // not-controlled, stop
        final var stopValue = dpt.parse(new String[]{"STOP"});
        assertThat(stopValue.isControlled()).isFalse();
        assertThat(stopValue.getStepInterval()).isSameAs(StepInterval.STOP);
        // not-controlled, percent = 50%
        final var percent50Value = dpt.parse(new String[]{"50%"});
        assertThat(percent50Value.isControlled()).isFalse();
        assertThat(percent50Value.getStepInterval()).isSameAs(StepInterval.PERCENT_50);
        // controlled, percent = 11.5%  (nearest percent 12%) with dot as separator
        final var percentControlled = dpt.parse(new String[]{"controlled", "11.5%"});
        assertThat(percentControlled.isControlled()).isTrue();
        assertThat(percentControlled.getStepInterval()).isSameAs(StepInterval.PERCENT_12);
        // controlled, percent = 56,2%  (nearest percent 50%) with comma a separator
        final var percentControlled2 = dpt.parse(new String[]{"controlled", "56,2%"});
        assertThat(percentControlled2.isControlled()).isTrue();
        assertThat(percentControlled2.getStepInterval()).isSameAs(StepInterval.PERCENT_50);
        // controlled, interval = 40
        final var percent1Controlled = dpt.parse(new String[]{"40", "controlled"});
        assertThat(percent1Controlled.isControlled()).isTrue();
        assertThat(percent1Controlled.getStepInterval()).isSameAs(StepInterval.PERCENT_1);
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPT3.BLINDS_CONTROL;

        // no step interval provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"foobar"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Step Interval missing (format: '0', '0%', '0.0%', '0,0%' or 'stop'). Provided: [foobar]");
    }

    @Test
    @DisplayName("Test #getDPT1()")
    void testGetDPT1() {
        assertThat(DPT3.BLINDS_CONTROL.getDPT1()).isSameAs(DPT1.UP_DOWN);
        assertThat(DPT3.DIMMING_CONTROL.getDPT1()).isSameAs(DPT1.STEP);
    }

    @Test
    @DisplayName("Test #of(boolean, StepInterval)")
    void testOfStepInterval() {
        // not controlled, step = 0 (STOP)
        assertThat(DPT3.BLINDS_CONTROL.of(false, StepInterval.STOP)).isInstanceOf(DPT3Value.class);
        // controlled, step = 2 (PERCENT_50)
        assertThat(DPT3.BLINDS_CONTROL.of(true, StepInterval.PERCENT_50)).isInstanceOf(DPT3Value.class);
        // not controlled, step = 7 (PERCENT_1)
        assertThat(DPT3.BLINDS_CONTROL.of(false, StepInterval.PERCENT_1)).isInstanceOf(DPT3Value.class);
        // controlled, step =  7 (PERCENT_1)
        assertThat(DPT3.BLINDS_CONTROL.of(true, StepInterval.PERCENT_1)).isInstanceOf(DPT3Value.class);
    }
}
