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

import li.pitschmann.knx.core.datapoint.value.DPT7Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void testByteParse() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.parse(new byte[]{0x34, 0x67})).isInstanceOf(DPT7Value.class);
        assertThat(dpt.parse(new byte[]{0x56, (byte)0xEA})).isInstanceOf(DPT7Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    public void testStringParse() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.parse(new String[]{"0"})).isInstanceOf(DPT7Value.class);
        assertThat(dpt.parse(new String[]{"1234"})).isInstanceOf(DPT7Value.class);
        assertThat(dpt.parse(new String[]{"65535"})).isInstanceOf(DPT7Value.class);
    }

    @Test
    @DisplayName("Test #of(int)")
    void testOf() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.of(0)).isInstanceOf(DPT7Value.class);
        assertThat(dpt.of(65535)).isInstanceOf(DPT7Value.class);
    }

    @Test
    @DisplayName("Test #toByteArray(int)")
    void testToByteArray() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.toByteArray(0)).containsExactly(0x00, 0x00);
        assertThat(dpt.toByteArray(65535)).containsExactly(0xFF, 0xFF);
    }

    @Test
    @DisplayName("Test #getCalculationFunction()")
    void testCalculationFunction() {
        assertThat(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT.getCalculationFunction()).isNull();

        // Time Period in 10ms (0 = 0ms, 32145 = 321.45ms, 65535 = 655.35ms)
        final var dpt = DPT7.TIME_PERIOD_10MS;
        assertThat(dpt.getCalculationFunction()).isNotNull();
        assertThat(dpt.of(0).getRawUnsignedValue()).isZero();
        assertThat(dpt.of(0).getUnsignedValue()).isZero();
        assertThat(dpt.of(32145).getRawUnsignedValue()).isEqualTo(32145);
        assertThat(dpt.of(32145).getUnsignedValue()).isEqualTo(321.45);
        assertThat(dpt.of(65535).getRawUnsignedValue()).isEqualTo(65535);
        assertThat(dpt.of(65535).getUnsignedValue()).isEqualTo(655.35);
    }
}
