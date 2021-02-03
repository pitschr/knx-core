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

import li.pitschmann.knx.core.datapoint.value.DPT5Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT5}
 *
 * @author PITSCHR
 */
class DPT5Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT5.VALUE_1_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.getId()).isEqualTo("5.010");
        assertThat(dpt.getDescription()).isEqualTo("Value 1-Octet Unsigned Count (pulses)");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT5.VALUE_1_OCTET_UNSIGNED_COUNT;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT5.VALUE_1_OCTET_UNSIGNED_COUNT;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT5.VALUE_1_OCTET_UNSIGNED_COUNT;

        // value: 0x00
        final var value0 = dpt.parse(new byte[]{0x00});
        assertThat(value0.getValue()).isZero();
        // value: 0xFF
        final var value255 = dpt.parse(new byte[]{(byte) 0xFF});
        assertThat(value255.getValue()).isEqualTo(255);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT5.VALUE_1_OCTET_UNSIGNED_COUNT;

        // value: 0
        final var value0 = dpt.parse(new String[]{"0"});
        assertThat(value0.getValue()).isZero();
        // value: 255
        final var value255 = dpt.parse(new String[]{"255"});
        assertThat(value255.getValue()).isEqualTo(255);
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPT5.VALUE_1_OCTET_UNSIGNED_COUNT;

        // no integer format provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"foobar"}))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    @DisplayName("Test #of(int)")
    void testOf() {
        final var dpt = DPT5.VALUE_1_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.of(0)).isInstanceOf(DPT5Value.class);
        assertThat(dpt.of(255)).isInstanceOf(DPT5Value.class);
    }

    @Test
    @DisplayName("Test #getCalculationFunction()")
    void testCalculationFunction() {
        assertThat(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT.getCalculationFunction()).isNull();

        // Scaling 0% = 0x00 (0), 25% = 0x3F (63), 50% = 0x7F (127), 100% = 0xFF (255)
        final var dptScaling = DPT5.SCALING;
        assertThat(dptScaling.getCalculationFunction()).isNotNull();
        assertThat(dptScaling.of(0).getValue()).isZero();
        assertThat(dptScaling.of(0).toByteArray()).containsExactly(0x00);
        assertThat(dptScaling.of(25).getValue()).isEqualTo(25);
        assertThat(dptScaling.of(25).toByteArray()).containsExactly(0x40); // 63.75 -> 64
        assertThat(dptScaling.of(50).getValue()).isEqualTo(50);
        assertThat(dptScaling.of(50).toByteArray()).containsExactly(0x80); // 127.5 -> 128
        assertThat(dptScaling.of(100).getValue()).isEqualTo(100);
        assertThat(dptScaling.of(100).toByteArray()).containsExactly(0xFF);

        // Scaling 0° = 0x00 (0), 60° = 0x2A (42), 180° = 0x7F (127), 360° = 0xFF (255)
        final var dptAngle = DPT5.ANGLE;
        assertThat(dptAngle.getCalculationFunction()).isNotNull();
        assertThat(dptAngle.of(0).getValue()).isZero();
        assertThat(dptAngle.of(0).toByteArray()).containsExactly(0x00);
        assertThat(dptAngle.of(60).getValue()).isEqualTo(60);
        assertThat(dptAngle.of(60).toByteArray()).containsExactly(0x2B); // 42.5 -> 43
        assertThat(dptAngle.of(180).getValue()).isEqualTo(180);
        assertThat(dptAngle.of(180).toByteArray()).containsExactly(0x80); // 127.5 -> 128
        assertThat(dptAngle.of(360).getValue()).isEqualTo(360);
        assertThat(dptAngle.of(360).toByteArray()).containsExactly(0xFF);
    }
}
