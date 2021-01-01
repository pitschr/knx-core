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

import li.pitschmann.knx.core.datapoint.value.DPT10Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT10}
 *
 * @author PITSCHR
 */
class DPT10Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT10.TIME_OF_DAY;
        assertThat(dpt.getId()).isEqualTo("10.001");
        assertThat(dpt.getDescription()).isEqualTo("Time Of Day");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT10.TIME_OF_DAY;
        // byte is supported for length == 3 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isFalse();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
        assertThat(dpt.isCompatible(new byte[3])).isTrue();
        assertThat(dpt.isCompatible(new byte[4])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT10.TIME_OF_DAY;
        // String is supported for length == [1, 2] only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue(); // HH:MM:SS
        assertThat(dpt.isCompatible(new String[2])).isTrue(); // DDD, HH:MM:SS
        assertThat(dpt.isCompatible(new String[3])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT10.TIME_OF_DAY;
        assertThat(dpt.parse(new byte[]{0x00, 0x00, 0x00})).isInstanceOf(DPT10Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0xEF, 0x3B, 0x3B})).isInstanceOf(DPT10Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT10.TIME_OF_DAY;
        assertThat(dpt.parse(new String[]{"14:56:30"})).isInstanceOf(DPT10Value.class);
        assertThat(dpt.parse(new String[]{"FrIDaY", "14:56:30"})).isInstanceOf(DPT10Value.class);
    }

    @Test
    @DisplayName("Test #of(DayOfWeek, LocalTime)")
    void testOf() {
        final var dpt = DPT10.TIME_OF_DAY;
        assertThat(dpt.of(null, LocalTime.now())).isInstanceOf(DPT10Value.class);
        assertThat(dpt.of(DayOfWeek.MONDAY, LocalTime.now())).isInstanceOf(DPT10Value.class);
    }

    @Test
    @DisplayName("Test #toByteArray(DayOfWeek, LocalTime)")
    void testToByteArray() {
        final var dpt = DPT10.TIME_OF_DAY;
        // no day, 00:00:00
        assertThat(dpt.toByteArray(null, LocalTime.of(0, 0, 0)))
                .containsExactly(0x00, 0x00, 0x00);
        // monday, 6:15:20
        // monday => 001. ....
        // hour 6 => ...0 0110
        assertThat(dpt.toByteArray(DayOfWeek.MONDAY, LocalTime.of(6, 15, 20)))
                .containsExactly(0x26, 0x0F, 0x14);
        // wednesday, 12:30:45
        // wednesday => 011. ....
        // hour 12 ===> ...0 1100
        assertThat(dpt.toByteArray(DayOfWeek.WEDNESDAY, LocalTime.of(12, 30, 45)))
                .containsExactly(0x6C, 0x1E, 0x2D);
        // sunday, 23:59:59
        // sunday ==> 111. ....
        // hour 23 => ...1 0111
        assertThat(dpt.toByteArray(DayOfWeek.SUNDAY, LocalTime.of(23, 59, 59)))
                .containsExactly(0xF7, 0x3B, 0x3B);
    }
}
