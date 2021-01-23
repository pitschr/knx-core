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

import li.pitschmann.knx.core.datapoint.value.DPT19Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(dpt.isCompatible(new String[1])).isFalse();
        assertThat(dpt.isCompatible(new String[2])).isTrue(); // YYYY-MM-DD HH:MM:SS
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
        final var dateAndTime = dpt.of("1900-01-01", "00:00");
        assertThat(dateAndTime.getDayOfWeek()).isNull();
        assertThat(dateAndTime.getDate()).isEqualTo(LocalDate.of(1900, 1, 1));
        assertThat(dateAndTime.getTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(dateAndTime.getFlags()).isSameAs(DPT19Value.Flags.NO_FLAGS);
        // monday, 1950-02-03 6:15:20
        final var weekOfDayAndDateAndTime = dpt.of("Monday", "1950-02-03", "06:15:20");
        assertThat(weekOfDayAndDateAndTime.getDayOfWeek()).isSameAs(DayOfWeek.MONDAY);
        assertThat(weekOfDayAndDateAndTime.getDate()).isEqualTo(LocalDate.of(1950, 2, 3));
        assertThat(weekOfDayAndDateAndTime.getTime()).isEqualTo(LocalTime.of(6, 15, 20));
        assertThat(weekOfDayAndDateAndTime.getFlags()).isSameAs(DPT19Value.Flags.NO_FLAGS);
        // wednesday, 2000-04-05 12:30:45
        final var weekOfDayAndDateAndTime2 = dpt.of("Wednesday", "2000-04-05", "12:30:45");
        assertThat(weekOfDayAndDateAndTime2.getDayOfWeek()).isSameAs(DayOfWeek.WEDNESDAY);
        assertThat(weekOfDayAndDateAndTime2.getDate()).isEqualTo(LocalDate.of(2000, 4, 5));
        assertThat(weekOfDayAndDateAndTime2.getTime()).isEqualTo(LocalTime.of(12, 30, 45));
        assertThat(weekOfDayAndDateAndTime2.getFlags()).isSameAs(DPT19Value.Flags.NO_FLAGS);
        // no day, 2155-12-31 23:59:59, Flags: 0x53 C0
        final var dateAndTimeAndFlag = dpt.of("2155-12-31", "23:59:59", "0x53C0");
        assertThat(dateAndTimeAndFlag.getDayOfWeek()).isNull();
        assertThat(dateAndTimeAndFlag.getDate()).isEqualTo(LocalDate.of(2155, 12, 31));
        assertThat(dateAndTimeAndFlag.getTime()).isEqualTo(LocalTime.of(23, 59, 59));
        assertThat(dateAndTimeAndFlag.getFlags()).isEqualTo(new DPT19Value.Flags(new byte[]{ 0b0101_0011, (byte) 0b1100_0000}));
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPT19.DATE_TIME;

        // no date provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"foobar"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date missing (supported format: 'yyyy-mm-dd'). Provided: [foobar]");
        // wrong date format provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"01.02.2020"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date missing (supported format: 'yyyy-mm-dd'). Provided: [01.02.2020]");

        // no time provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"2000-01-01"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Time missing (supported format: 'hh:mm', 'hh:mm:ss'). Provided: [2000-01-01]");
        // wrong time format (expected: 00:00:00)
        assertThatThrownBy(() -> dpt.parse(new String[]{"2000-01-01", "0:0:0"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Time missing (supported format: 'hh:mm', 'hh:mm:ss'). Provided: [2000-01-01, 0:0:0]");
        // wrong hour format (expected: 00 - 23)
        assertThatThrownBy(() -> dpt.parse(new String[]{"2000-01-01", "24:00:00"}))
                .isInstanceOf(DateTimeParseException.class);
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
}