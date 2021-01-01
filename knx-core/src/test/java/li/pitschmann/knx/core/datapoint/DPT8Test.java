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

import li.pitschmann.knx.core.datapoint.value.DPT8Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(dpt.parse(new byte[]{(byte) 0x80, 0x00})).isInstanceOf(DPT8Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0xF6, (byte) 0xCA})).isInstanceOf(DPT8Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;
        assertThat(dpt.parse(new String[]{"-32768"})).isInstanceOf(DPT8Value.class);
        assertThat(dpt.parse(new String[]{"0"})).isInstanceOf(DPT8Value.class);
        assertThat(dpt.parse(new String[]{"32767"})).isInstanceOf(DPT8Value.class);
    }

    @Test
    @DisplayName("Test #of(int)")
    void testOf() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;
        assertThat(dpt.of(-32768)).isInstanceOf(DPT8Value.class);
        assertThat(dpt.of(0)).isInstanceOf(DPT8Value.class);
        assertThat(dpt.of(32767)).isInstanceOf(DPT8Value.class);
    }

    @Test
    @DisplayName("Test #toByteArray(int)")
    void testToByteArray() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;
        assertThat(dpt.toByteArray(-32768)).containsExactly(0x80, 0x00);
        assertThat(dpt.toByteArray(0)).containsExactly(0x00, 0x00);
        assertThat(dpt.toByteArray(32767)).containsExactly(0x7F, 0xFF);
    }

    @Test
    @DisplayName("Test #getCalculationFunction()")
    void testCalculationFunction() {
        assertThat(DPT8.VALUE_2_OCTET_COUNT.getCalculationFunction()).isNull();

        // Delta Time Period in 10ms (-32768 = -327.68ms, 0 = 0ms, 32767 = 327.67ms)
        final var dpt = DPT8.DELTA_TIME_10MS;
        assertThat(dpt.getCalculationFunction()).isNotNull();
        assertThat(dpt.of(-32768).getRawSignedValue()).isEqualTo(-32768);
        assertThat(dpt.of(-32768).getSignedValue()).isEqualTo(-327.68);
        assertThat(dpt.of(0).getRawSignedValue()).isZero();
        assertThat(dpt.of(0).getSignedValue()).isZero();
        assertThat(dpt.of(32767).getRawSignedValue()).isEqualTo(32767);
        assertThat(dpt.of(32767).getSignedValue()).isEqualTo(327.67);
    }
}
