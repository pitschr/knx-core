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

import li.pitschmann.knx.core.datapoint.value.DPT12Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT12}
 *
 * @author PITSCHR
 */
class DPT12Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT12.VALUE_4_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.getId()).isEqualTo("12.001");
        assertThat(dpt.getDescription()).isEqualTo("Value 4-Octet Unsigned Count (pulses)");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT12.VALUE_4_OCTET_UNSIGNED_COUNT;
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
        final var dpt = DPT12.VALUE_4_OCTET_UNSIGNED_COUNT;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT12.VALUE_4_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.parse(new byte[]{0x00, 0x00, 0x00, 0x00})).isInstanceOf(DPT12Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF})).isInstanceOf(DPT12Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT12.VALUE_4_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.parse(new String[]{"0"})).isInstanceOf(DPT12Value.class);
        assertThat(dpt.parse(new String[]{"4294967295"})).isInstanceOf(DPT12Value.class);
    }

    @Test
    @DisplayName("Test #of(long)")
    void testOf() {
        final var dpt = DPT12.VALUE_4_OCTET_UNSIGNED_COUNT;
        assertThat(dpt.of(0)).isInstanceOf(DPT12Value.class);
        assertThat(dpt.of(4294967295L)).isInstanceOf(DPT12Value.class);
    }
}
