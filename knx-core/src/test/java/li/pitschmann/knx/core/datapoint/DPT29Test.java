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

import li.pitschmann.knx.core.datapoint.value.DPT29Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT29}
 *
 * @author PITSCHR
 */
class DPT29Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT29.VALUE_8_OCTET_COUNT;
        assertThat(dpt.getId()).isEqualTo("29.001");
        assertThat(dpt.getDescription()).isEqualTo("Value 8-Octet Signed Count (pulses)");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT29.VALUE_8_OCTET_COUNT;
        // byte is supported for length == 8 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isFalse();
        assertThat(dpt.isCompatible(new byte[7])).isFalse();
        assertThat(dpt.isCompatible(new byte[8])).isTrue();
        assertThat(dpt.isCompatible(new byte[9])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT29.VALUE_8_OCTET_COUNT;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT29.VALUE_8_OCTET_COUNT;
        assertThat(dpt.parse(new byte[]{(byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00})).isInstanceOf(DPT29Value.class);
        assertThat(dpt.parse(new byte[]{0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF})).isInstanceOf(DPT29Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT29.VALUE_8_OCTET_COUNT;

        // value: -9223372036854775808
        final var valueNegative = dpt.parse(new String[]{"-9223372036854775808"});
        assertThat(valueNegative.getValue()).isEqualTo(Long.MIN_VALUE);
        // value: 0
        final var valueZero = dpt.parse(new String[]{"0"});
        assertThat(valueZero.getValue()).isZero();
        // value: 9223372036854775807
        final var valuePositive = dpt.parse(new String[]{"9223372036854775807"});
        assertThat(valuePositive.getValue()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPT29.VALUE_8_OCTET_COUNT;

        // no long format provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"foobar"}))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    @DisplayName("Test #of(int)")
    void testOf() {
        final var dpt = DPT29.VALUE_8_OCTET_COUNT;
        assertThat(dpt.of(Long.MIN_VALUE)).isInstanceOf(DPT29Value.class);
        assertThat(dpt.of(0)).isInstanceOf(DPT29Value.class);
        assertThat(dpt.of(Long.MAX_VALUE)).isInstanceOf(DPT29Value.class);
    }
}
