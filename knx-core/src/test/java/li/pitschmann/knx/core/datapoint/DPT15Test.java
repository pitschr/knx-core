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

import li.pitschmann.knx.core.datapoint.value.DPT15Value;
import li.pitschmann.knx.core.datapoint.value.DPT15Value.Flags;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT15}
 *
 * @author PITSCHR
 */
class DPT15Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT15.ACCESS_DATA;
        assertThat(dpt.getId()).isEqualTo("15.000");
        assertThat(dpt.getDescription()).isEqualTo("Access Data");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT15.ACCESS_DATA;
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
        final var dpt = DPT15.ACCESS_DATA;
        // String is not supported -> always false
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isFalse();
        assertThat(dpt.isCompatible(new String[2])).isFalse();
        assertThat(dpt.isCompatible(new String[3])).isFalse();
        assertThat(dpt.isCompatible(new String[4])).isFalse();
        assertThat(dpt.isCompatible(new String[5])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    public void testByteParse() {
        final var dpt = DPT15.ACCESS_DATA;
        // parse with only 4-bytes supported
        assertThat(dpt.parse(new byte[4])).isInstanceOf(DPT15Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    public void testStringParse() {
        final var dpt = DPT15.ACCESS_DATA;
        // parse for string not supported
        assertThatThrownBy(() -> dpt.parse(new String[0])).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Test #of(byte[], Flags)")
    void testOf() {
        final var dpt = DPT15.ACCESS_DATA;
        // access id data, flags set (1010), index=3
        assertThat(
                dpt.of(
                        new byte[]{0x22, 0x44, 0x11},
                        new Flags(true, false, true, false, 0x03)
                )
        ).isInstanceOf(DPT15Value.class);
        // access id data, flags set (0101), index=12
        assertThat(
                dpt.of(
                        new byte[]{0x33, 0x77, 0x00},
                        new Flags(false, true, false, true, 0x0C)
                )
        ).isInstanceOf(DPT15Value.class);
    }

    @Test
    @DisplayName("Test #toByteArray(byte[], Flags)")
    void testToByteArray() {
        final var dpt = DPT15.ACCESS_DATA;
        // access id data, flags set (1010), index=3
        assertThat(
                dpt.toByteArray(
                        new byte[]{0x22, 0x44, 0x11},
                        new Flags(true, false, true, false, 0x03)
                )
        ).containsExactly(0x22, 0x44, 0x11, (byte) 0xA3);
        // access id data, flags set (0101), index=12
        assertThat(
                dpt.toByteArray(
                        new byte[]{0x33, 0x77, 0x00},
                        new Flags(false, true, false, true, 0x0C)
                )
        ).containsExactly(0x33, 0x77, 0x00, 0x5C);
    }
}
