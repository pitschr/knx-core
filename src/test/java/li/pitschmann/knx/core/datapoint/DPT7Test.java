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

import li.pitschmann.knx.core.datapoint.value.DPT7Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT7}
 *
 * @author PITSCHR
 */
class DPT7Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.getId()).isEqualTo("7.001");
        assertThat(dpt.getDescription()).isEqualTo("Value 2-Octet Unsigned Count (pulses)");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;
        // byte is supported for length == 2 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isFalse();
        assertThat(dpt.isCompatible(new byte[2])).isTrue();
        assertThat(dpt.isCompatible(new byte[3])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;

        // value: 0x00 0x00
        final var valueMin = dpt.parse(new byte[]{0x00, 0x00});
        assertThat(valueMin.getValue()).isZero();
        // value: 0xFF 0xFF
        final var valueMax = dpt.parse(new byte[]{(byte) 0xFF, (byte) 0xFF});
        assertThat(valueMax.getValue()).isEqualTo(65535);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;

        // value: 0
        final var valueMin = dpt.parse(new String[]{"0"});
        assertThat(valueMin.getValue()).isZero();
        // value: 65535
        final var valueMax = dpt.parse(new String[]{"65535"});
        assertThat(valueMax.getValue()).isEqualTo(65535);
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;

        // no integer format provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"foobar"}))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    @DisplayName("Test #of(int)")
    void testOf() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.of(0)).isInstanceOf(DPT7Value.class);
        assertThat(dpt.of(65535)).isInstanceOf(DPT7Value.class);
    }

    @Test
    @DisplayName("Test #getCalculationFunction()")
    void testCalculationFunction() {
        assertThat(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT.getCalculationFunction()).isNull();

        // Time Period in 10ms (0 = 0ms, 32145 = 321450ms, 65535 = 655350ms)
        final var dpt10ms = DPT7.TIME_PERIOD_10MS;
        assertThat(dpt10ms.getCalculationFunction()).isNotNull();
        assertThat(dpt10ms.of(0).getValue()).isZero();
        assertThat(dpt10ms.of(0).toByteArray()).containsExactly(0x00, 0x00);
        assertThat(dpt10ms.of(321450).getValue()).isEqualTo(321450);
        assertThat(dpt10ms.of(321450).toByteArray()).containsExactly(0x7D, 0x91);
        assertThat(dpt10ms.of(655350).getValue()).isEqualTo(655350);
        assertThat(dpt10ms.of(655350).toByteArray()).containsExactly(0xFF, 0xFF);

        // Time Period in 100ms (0 = 0ms, 23443 = 2344300ms, 65535 = 6553500ms)
        final var dpt100ms = DPT7.TIME_PERIOD_100MS;
        assertThat(dpt100ms.getCalculationFunction()).isNotNull();
        assertThat(dpt100ms.of(0).getValue()).isZero();
        assertThat(dpt100ms.of(0).toByteArray()).containsExactly(0x00, 0x00);
        assertThat(dpt100ms.of(2344300).getValue()).isEqualTo(2344300);
        assertThat(dpt100ms.of(2344300).toByteArray()).containsExactly(0x5B, 0x93);
        assertThat(dpt100ms.of(6553500).getValue()).isEqualTo(6553500);
        assertThat(dpt100ms.of(6553500).toByteArray()).containsExactly(0xFF, 0xFF);
    }
}
