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

import li.pitschmann.knx.core.datapoint.value.DPT14Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT14}
 *
 * @author PITSCHR
 */
class DPT14Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT14.TEMPERATURE;
        assertThat(dpt.getId()).isEqualTo("14.068");
        assertThat(dpt.getDescription()).isEqualTo("Temperature (°C)");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT14.TEMPERATURE;
        // byte is supported for length == 4 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isFalse();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
        assertThat(dpt.isCompatible(new byte[3])).isFalse();
        assertThat(dpt.isCompatible(new byte[4])).isTrue();
        assertThat(dpt.isCompatible(new byte[5])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT14.TEMPERATURE;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT14.TEMPERATURE;
        assertThat(dpt.parse(new byte[]{0x7F, 0x7F, (byte) 0xFF, (byte) 0xFF})).isInstanceOf(DPT14Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0xFF, 0x7F, (byte) 0xFF, (byte) 0xFF})).isInstanceOf(DPT14Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT14.TEMPERATURE;

        // value: -3.40282347E38
        final var valueNegative = dpt.parse(new String[]{"-3.40282347E38"});
        assertThat(valueNegative.getValue()).isEqualTo(-3.40282347e+38);
        // value: 0
        final var valueZero = dpt.parse(new String[]{"0"});
        assertThat(valueZero.getValue()).isZero();
        // value: 3.40282347E38
        final var valuePositive = dpt.parse(new String[]{"3.40282347E38"});
        assertThat(valuePositive.getValue()).isEqualTo(3.40282347e+38);
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPT14.TEMPERATURE;

        // no double format provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"foobar"}))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    @DisplayName("Test #of(double)")
    void testOf() {
        final var dpt = DPT14.TEMPERATURE;
        assertThat(dpt.of(-3.40282347e+38f)).isInstanceOf(DPT14Value.class);
        assertThat(dpt.of(0)).isInstanceOf(DPT14Value.class);
        assertThat(dpt.of(3.40282347e+38f)).isInstanceOf(DPT14Value.class);
    }
}