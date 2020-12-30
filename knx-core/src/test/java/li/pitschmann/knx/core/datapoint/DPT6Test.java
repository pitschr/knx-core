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

import li.pitschmann.knx.core.datapoint.value.DPT6Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT6}
 *
 * @author PITSCHR
 */
class DPT6Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT6.VALUE_1_OCTET_COUNT;
        assertThat(dpt.getId()).isEqualTo("6.010");
        assertThat(dpt.getDescription()).isEqualTo("Value 1 Octet Signed Count (pulses)");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT6.VALUE_1_OCTET_COUNT;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT6.VALUE_1_OCTET_COUNT;
        // String is supported for length == 1 only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    public void testByteParse() {
        final var dpt = DPT6.VALUE_1_OCTET_COUNT;
        assertThat(dpt.parse(new byte[]{0x00})).isInstanceOf(DPT6Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0xFF})).isInstanceOf(DPT6Value.class);
        assertThatThrownBy(() -> dpt.parse(new byte[0]));
    }

    @Test
    @DisplayName("Test #parse(String[])")
    public void testStringParse() {
        final var dpt = DPT6.VALUE_1_OCTET_COUNT;
        assertThat(dpt.parse(new String[]{"45"})).isInstanceOf(DPT6Value.class);
        assertThat(dpt.parse(new String[]{"-128"})).isInstanceOf(DPT6Value.class);
        assertThat(dpt.parse(new String[]{"127"})).isInstanceOf(DPT6Value.class);
    }

    @Test
    @DisplayName("Test #of(int)")
    void testOf() {
        assertThat(DPT6.VALUE_1_OCTET_COUNT.of(23)).isInstanceOf(DPT6Value.class);
        assertThat(DPT6.VALUE_1_OCTET_COUNT.of(-128)).isInstanceOf(DPT6Value.class);
        assertThat(DPT6.VALUE_1_OCTET_COUNT.of(127)).isInstanceOf(DPT6Value.class);
    }

    @Test
    @DisplayName("Test #toByteArray(int)")
    void testToByteArray() {
        assertThat(DPT6.VALUE_1_OCTET_COUNT.toByteArray(23)).containsExactly(0x17);
        assertThat(DPT6.VALUE_1_OCTET_COUNT.toByteArray(-128)).containsExactly(0x80);
        assertThat(DPT6.VALUE_1_OCTET_COUNT.toByteArray(127)).containsExactly(0x7F);
    }
}
