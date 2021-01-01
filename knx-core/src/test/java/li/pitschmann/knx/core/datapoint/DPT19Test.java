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

import li.pitschmann.knx.core.datapoint.value.DPT19Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT19}
 *
 * @author PITSCHR
 */
class DPT19Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT19.DATE_TIME;
        assertThat(dpt.getId()).isEqualTo("19.001");
        assertThat(dpt.getDescription()).isEqualTo("Date & Time");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT19.DATE_TIME;
        // byte is supported for length == 8 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isFalse();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
        assertThat(dpt.isCompatible(new byte[3])).isFalse();
        assertThat(dpt.isCompatible(new byte[4])).isFalse();
        assertThat(dpt.isCompatible(new byte[5])).isFalse();
        assertThat(dpt.isCompatible(new byte[6])).isFalse();
        assertThat(dpt.isCompatible(new byte[7])).isFalse();
        assertThat(dpt.isCompatible(new byte[8])).isTrue();
        assertThat(dpt.isCompatible(new byte[9])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT19.DATE_TIME;
        // String is supported for length == [1, 4] only
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue(); // HH:MM:SS
        assertThat(dpt.isCompatible(new String[2])).isTrue(); // DDD, HH:MM:SS
        assertThat(dpt.isCompatible(new String[3])).isTrue(); // DDD, YYYY-MM-DD HH:MM:SS
        assertThat(dpt.isCompatible(new String[4])).isTrue(); // DDD, YYYY-MM-DD HH:MM:SS Flags
        assertThat(dpt.isCompatible(new String[5])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT19.DATE_TIME;
        // no day, 1900-01-01 00:00:00
        assertThat(dpt.parse(new byte[]{0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00})).isInstanceOf(DPT19Value.class);
        // monday, 1950-02-03 6:15:20
        assertThat(dpt.parse(new byte[]{0x32, 0x02, 0x03, 0x26, 0x0F, 0x14, (byte) 0xAA, (byte) 0x80})).isInstanceOf(DPT19Value.class);
        // wednesday, 2000-04-05 12:30:45
        assertThat(dpt.parse(new byte[]{0x64, 0x04, 0x05, 0x6C, 0x1E, 0x2D, 0x55, 0x00})).isInstanceOf(DPT19Value.class);
        // sunday, 2155-12-31 23:59:59
        assertThat(dpt.parse(new byte[]{(byte) 0xFF, 0x0C, 0x1F, (byte) 0xF7, 0x3B, 0x3B, (byte) 0xFF, (byte) 0x80})).isInstanceOf(DPT19Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT19.DATE_TIME;
        // no day, 1900-01-01 00:00:00
        assertThat(dpt.of("1900-01-01", "00:00:00")).isInstanceOf(DPT19Value.class);
        // monday, 1950-02-03 6:15:20
        assertThat(dpt.of("Monday", "1950-02-03", "06:15:20")).isInstanceOf(DPT19Value.class);
        // wednesday, 2000-04-05 12:30:45
        assertThat(dpt.of("Wednesday", "2000-04-05", "12:30:45")).isInstanceOf(DPT19Value.class);
        // sunday, 2155-12-31 23:59:59
        assertThat(dpt.of("Sunday", "2155-12-31", "23:59:59")).isInstanceOf(DPT19Value.class);
    }

    @Test
    @DisplayName("Test #of(DayOfWeek, LocalDate, LocalTime)")
    void testOf() {
        final var dpt = DPT19.DATE_TIME;
        // no day, 1900-01-01 00:00:00
        assertThat(dpt.of(null, LocalDate.of(1900, 1, 1), LocalTime.of(0, 0, 0))).isInstanceOf(DPT19Value.class);
        // monday, 1950-02-03 6:15:20
        assertThat(dpt.of(DayOfWeek.MONDAY, LocalDate.of(1950, 2, 3), LocalTime.of(6, 15, 20))).isInstanceOf(DPT19Value.class);
        // wednesday, 2000-04-05 12:30:45
        assertThat(dpt.of(DayOfWeek.WEDNESDAY, LocalDate.of(2000, 4, 5), LocalTime.of(12, 30, 45))).isInstanceOf(DPT19Value.class);
        // sunday, 2155-12-31 23:59:59
        assertThat(dpt.of(DayOfWeek.SUNDAY, LocalDate.of(2155, 12, 31), LocalTime.of(23, 59, 59))).isInstanceOf(DPT19Value.class);
    }

    @Test
    @DisplayName("Test #toByteArray(DayOfWeek, LocalDate, LocalTime)")
    void testToByteArray() {
        final var dpt = DPT19.DATE_TIME;
        // no day, 1900-01-01 00:00:00
        assertThat(dpt.toByteArray(null, LocalDate.of(1900, 1, 1), LocalTime.of(0, 0, 0)))
                .containsExactly(0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00);
        // monday, 1950-02-03 6:15:20
        // monday => 001. ....
        // hour 6 => ...0 0110
        assertThat(dpt.toByteArray(DayOfWeek.MONDAY, LocalDate.of(1950, 2, 3), LocalTime.of(6, 15, 20)))
                .containsExactly(0x32, 0x02, 0x03, 0x26, 0x0F, 0x14, 0x00, 0x00);
        // wednesday, 2000-04-05 12:30:45
        // wednesday => 011. ....
        // hour 12 ===> ...0 1100
        assertThat(dpt.toByteArray(DayOfWeek.WEDNESDAY, LocalDate.of(2000, 4, 5), LocalTime.of(12, 30, 45)))
                .containsExactly(0x64, 0x04, 0x05, 0x6C, 0x1E, 0x2D, 0x00, 0x00);
        // sunday, 2155-12-31 23:59:59
        // sunday ==> 111. ....
        // hour 23 => ...1 0111
        assertThat(dpt.toByteArray(DayOfWeek.SUNDAY, LocalDate.of(2155, 12, 31), LocalTime.of(23, 59, 59)))
                .containsExactly(0xFF, 0x0C, 0x1F, 0xF7, 0x3B, 0x3B, 0x00, 0x00);
    }
}