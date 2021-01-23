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

import li.pitschmann.knx.core.datapoint.value.DPT8Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT8}
 *
 * @author PITSCHR
 */
class DPT8Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;
        assertThat(dpt.getId()).isEqualTo("8.001");
        assertThat(dpt.getDescription()).isEqualTo("Value 2-Octet Signed Count (pulses)");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;
        // byte is supported for length == 2 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isFalse();
        assertThat(dpt.isCompatible(new byte[2])).isTrue();
        assertThat(dpt.isCompatible(new byte[3])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;

        // value: 0x80 0x00
        final var valueNegative = dpt.parse(new byte[]{(byte) 0x80, 0x00});
        assertThat(valueNegative.getValue()).isEqualTo(-32768);
        // value: 0x00 0x00
        final var valueZero = dpt.parse(new byte[]{0x00, 0x00});
        assertThat(valueZero.getValue()).isZero();
        // value: 0x7F 0xFF
        final var valuePositive = dpt.parse(new byte[]{0x7F, (byte) 0xFF});
        assertThat(valuePositive.getValue()).isEqualTo(32767);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;

        // value: -32768
        final var valueNegative = dpt.parse(new String[]{"-32768"});
        assertThat(valueNegative.getValue()).isEqualTo(-32768);
        // value: 0
        final var valueZero = dpt.parse(new String[]{"0"});
        assertThat(valueZero.getValue()).isZero();
        // value: 32767
        final var valuePositive = dpt.parse(new String[]{"32767"});
        assertThat(valuePositive.getValue()).isEqualTo(32767);
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;

        // no integer format provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"foobar"}))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    @DisplayName("Test #of(int)")
    void testIntOf() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;
        assertThat(dpt.of(-32768)).isInstanceOf(DPT8Value.class);
        assertThat(dpt.of(0)).isInstanceOf(DPT8Value.class);
        assertThat(dpt.of(32767)).isInstanceOf(DPT8Value.class);
    }

    @Test
    @DisplayName("Test #getCalculationFunction()")
    void testCalculationFunction() {
        assertThat(DPT8.VALUE_2_OCTET_COUNT.getCalculationFunction()).isNull();

        // Delta Time Period in 10ms (-32768 = -327680ms, 0 = 0ms, 32767 = 327670ms)
        final var dptDeltaTime = DPT8.DELTA_TIME_10MS;
        assertThat(dptDeltaTime.getCalculationFunction()).isNotNull();
        assertThat(dptDeltaTime.of(-327680).getValue()).isEqualTo(-327680);
        assertThat(dptDeltaTime.of(-327680).toByteArray()).containsExactly(0x80, 0x00);
        assertThat(dptDeltaTime.of(0).getValue()).isZero();
        assertThat(dptDeltaTime.of(0).toByteArray()).containsExactly(0x00, 0x00);
        assertThat(dptDeltaTime.of(327670).getValue()).isEqualTo(327670);
        assertThat(dptDeltaTime.of(327670).toByteArray()).containsExactly(0x7F, 0xFF);
    }
}
