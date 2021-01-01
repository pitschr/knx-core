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

import li.pitschmann.knx.core.datapoint.value.DPT13Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT13}
 *
 * @author PITSCHR
 */
class DPT13Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT13.VALUE_4_OCTET_COUNT;
        assertThat(dpt.getId()).isEqualTo("13.001");
        assertThat(dpt.getDescription()).isEqualTo("Value 4-Octet Signed Count (pulses)");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT13.VALUE_4_OCTET_COUNT;
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
        final var dpt = DPT13.VALUE_4_OCTET_COUNT;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT13.VALUE_4_OCTET_COUNT;
        assertThat(dpt.parse(new byte[]{(byte) 0x80, 0x00, 0x00, 0x00})).isInstanceOf(DPT13Value.class);
        assertThat(dpt.parse(new byte[]{0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF})).isInstanceOf(DPT13Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT13.VALUE_4_OCTET_COUNT;
        assertThat(dpt.parse(new String[]{"-2147483648"})).isInstanceOf(DPT13Value.class);
        assertThat(dpt.parse(new String[]{"0"})).isInstanceOf(DPT13Value.class);
        assertThat(dpt.parse(new String[]{"2147483647"})).isInstanceOf(DPT13Value.class);
    }

    @Test
    @DisplayName("Test #of(int)")
    void testOf() {
        final var dpt = DPT13.VALUE_4_OCTET_COUNT;
        assertThat(dpt.of(-2147483648)).isInstanceOf(DPT13Value.class);
        assertThat(dpt.of(0)).isInstanceOf(DPT13Value.class);
        assertThat(dpt.of(2147483647)).isInstanceOf(DPT13Value.class);
    }

    @Test
    @DisplayName("Test #toByteArray(int)")
    void testToByteArray() {
        final var dpt = DPT13.VALUE_4_OCTET_COUNT;
        assertThat(dpt.toByteArray(-2147483648)).containsExactly(0x80, 0x00, 0x00, 0x00);
        assertThat(dpt.toByteArray(0)).containsExactly(0x00, 0x00, 0x00, 0x00);
        assertThat(dpt.toByteArray(2147483647)).containsExactly(0x7F, 0xFF, 0xFF, 0xFF);
    }

    @Test
    @DisplayName("Test #getCalculationFunction()")
    void testCalculationFunction() {
        assertThat(DPT13.VALUE_4_OCTET_COUNT.getCalculationFunction()).isNull();

        // Flow Rate in 0.0001m^3 resolution (-2147483648 = -214748.3648m^3, 0 = 0ms, 2147483647 = 214748.3647m^3)
        final var dpt = DPT13.FLOW_RATE;
        assertThat(dpt.getCalculationFunction()).isNotNull();
        assertThat(dpt.of(-2147483648).getRawSignedValue()).isEqualTo(-2147483648);
        assertThat(dpt.of(-2147483648).getSignedValue()).isEqualTo(-214748.3648);
        assertThat(dpt.of(0).getRawSignedValue()).isZero();
        assertThat(dpt.of(0).getSignedValue()).isZero();
        assertThat(dpt.of(2147483647).getRawSignedValue()).isEqualTo(2147483647);
        assertThat(dpt.of(2147483647).getSignedValue()).isEqualTo(214748.3647);
    }
}
