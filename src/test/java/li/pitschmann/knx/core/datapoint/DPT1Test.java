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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        // 0x01 = 1 => true
        final var trueByte = dpt.parse(new byte[]{0x01});
        assertThat(trueByte.getValue()).isTrue();

        // all others are "false" per default

        // 0x00 = 0 => false
        final var falseByte = dpt.parse(new byte[]{0x00});
        assertThat(falseByte.getValue()).isFalse();
        // 0x02 = 0 => false
        final var invalidByte = dpt.parse(new byte[]{0x02});
        assertThat(invalidByte.getValue()).isFalse();
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT1.SWITCH;

        // TruE => true
        final var trueString = dpt.parse(new String[]{"TruE"});
        assertThat(trueString.getValue()).isTrue();
        // 1 => true
        final var trueInt = dpt.parse(new String[]{"1"});
        assertThat(trueInt.getValue()).isTrue();
        // On => true
        final var trueValueText = dpt.parse(new String[]{"ON"});
        assertThat(trueValueText.getValue()).isTrue();

        // all others are as "false" per default

        // FaLsE => false
        final var falseString = dpt.parse(new String[]{"FaLsE"});
        assertThat(falseString.getValue()).isFalse();
        // 0 => false
        final var falseInt = dpt.parse(new String[]{"0"});
        assertThat(falseInt.getValue()).isFalse();
        // Off => false
        final var falseValueText = dpt.parse(new String[]{"OFF"});
        assertThat(falseValueText.getValue()).isFalse();
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        // no value provided
        // false = Off, true = On
        assertThatThrownBy(() -> DPT1.SWITCH.parse(new String[]{"foobar"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Please provide a value (supported: 'false', 'true', '0', '1', 'Off' and 'On'). Provided: [foobar]");

        // no value provided (2nd case)
        // false = No Alarm, true = Alarm
        assertThatThrownBy(() -> DPT1.ALARM.parse(new String[]{"baz"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Please provide a value (supported: 'false', 'true', '0', '1', 'No Alarm' and 'Alarm'). Provided: [baz]");
    }

    @Test
    @DisplayName("Test #getTextFor()")
    void testTextFor() {
        // Switch
        assertThat(DPT1.SWITCH.getTextFor(true)).isEqualTo("On");
        assertThat(DPT1.SWITCH.getTextFor(false)).isEqualTo("Off");
        // Alarm
        assertThat(DPT1.ALARM.getTextFor(true)).isEqualTo("Alarm");
        assertThat(DPT1.ALARM.getTextFor(false)).isEqualTo("No Alarm");
    }

    @Test
    @DisplayName("Test #getTextForTrue()")
    void testTextForTrue() {
        assertThat(DPT1.SWITCH.getTextForTrue()).isEqualTo("On");
        assertThat(DPT1.ALARM.getTextForTrue()).isEqualTo("Alarm");
    }

    @Test
    @DisplayName("Test #getTextForFalse()")
    void testTextForFalse() {
        assertThat(DPT1.SWITCH.getTextForFalse()).isEqualTo("Off");
        assertThat(DPT1.ALARM.getTextForFalse()).isEqualTo("No Alarm");
    }

    @Test
    @DisplayName("Test #of(boolean)")
    void testOf() {
        // not-controlled, false
        final var falseValue = DPT1.SWITCH.of(false);
        assertThat(falseValue.getValue()).isFalse();
        // not-controlled, true
        final var trueValue = DPT1.SWITCH.of(true);
        assertThat(trueValue.getValue()).isTrue();
    }
}
