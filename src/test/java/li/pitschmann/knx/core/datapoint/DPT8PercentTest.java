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

import li.pitschmann.knx.core.datapoint.value.DPT8Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT8.Percent}
 *
 * @author PITSCHR
 */
class DPT8PercentTest {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT8.PERCENT;
        assertThat(dpt.getId()).isEqualTo("8.010");
        assertThat(dpt.getDescription()).isEqualTo("Percent (%)");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT8.PERCENT;
        // byte is supported for length == 2 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isFalse();
        assertThat(dpt.isCompatible(new byte[2])).isTrue();
        assertThat(dpt.isCompatible(new byte[3])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT8.PERCENT;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT8.PERCENT;

        final var valueNegative = dpt.parse(new byte[]{(byte) 0x80, 0x00});
        assertThat(valueNegative).isInstanceOf(DPT8Value.Percent.class);
        assertThat(valueNegative.getValue()).isEqualTo(-327.68);

        final var valueZero = dpt.parse(new byte[]{0x00, 0x00});
        assertThat(valueZero).isInstanceOf(DPT8Value.Percent.class);
        assertThat(valueZero.getValue()).isZero();

        final var valuePositive = dpt.parse(new byte[]{(byte) 0x7F, (byte) 0xFF});
        assertThat(valuePositive).isInstanceOf(DPT8Value.Percent.class);
        assertThat(valuePositive.getValue()).isEqualTo(327.67);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT8.PERCENT;

        final var valueNegative = dpt.parse(new String[]{"-327.68"});
        assertThat(valueNegative).isInstanceOf(DPT8Value.Percent.class);
        assertThat(valueNegative.getValue()).isEqualTo(-327.68);

        final var valueZero = dpt.parse(new String[]{"0.0"});
        assertThat(valueZero).isInstanceOf(DPT8Value.Percent.class);
        assertThat(valueZero.getValue()).isZero();

        final var valuePositive = dpt.parse(new String[]{"327.67"});
        assertThat(valuePositive).isInstanceOf(DPT8Value.Percent.class);
        assertThat(valuePositive.getValue()).isEqualTo(327.67);
    }

    @Test
    @DisplayName("Test #of(double)")
    void testDoubleOf() {
        final var dpt = DPT8.PERCENT;

        assertThat(dpt.of(-327.68)).isInstanceOf(DPT8Value.Percent.class);
        assertThat(dpt.of(0)).isInstanceOf(DPT8Value.Percent.class);
        assertThat(dpt.of(327.67)).isInstanceOf(DPT8Value.Percent.class);
    }

    @Test
    @DisplayName("Test #getCalculationFunction()")
    void testCalculationFunction() {
        // Percent (-32768 = -327.68%, 0 = 0%, 32767 = 327.67%)
        final var dptPercent = DPT8.PERCENT;
        assertThat(dptPercent.of(-327.68).getValue()).isEqualTo(-327.68);
        assertThat(dptPercent.of(-327.68).toByteArray()).containsExactly(0x80, 0x00);
        assertThat(dptPercent.of(0).getValue()).isZero();
        assertThat(dptPercent.of(0).toByteArray()).containsExactly(0x00, 0x00);
        assertThat(dptPercent.of(327.67).getValue()).isEqualTo(327.67);
        assertThat(dptPercent.of(327.67).toByteArray()).containsExactly(0x7F, 0xFF);
    }
}
