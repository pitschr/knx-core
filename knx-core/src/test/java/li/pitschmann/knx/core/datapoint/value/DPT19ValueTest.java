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

package li.pitschmann.knx.core.datapoint.value;

import li.pitschmann.knx.core.datapoint.value.DPT19Value.Flags;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT19Value}
 *
 * @author PITSCHR
 */
final class DPT19ValueTest {

    @Test
    @DisplayName("#(byte[]) with: No DayOfWeek, 1900-01-01 00:00:00, No flags")
    void testBytes_1900_01_01() {
        final var value = new DPT19Value(new byte[]{0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertThat(value.getDayOfWeek()).isNull();
        assertThat(value.getDate()).isEqualTo(LocalDate.of(1900, 1, 1));
        assertThat(value.getTime()).isEqualTo(LocalTime.of(0, 0, 0));
        assertThat(value.getFlags()).isEqualTo(Flags.NO_FLAGS);
        assertThat(value.getFlags()).isEqualTo(new Flags(false, false, false, false, false, false, false, false, false, false));
        assertThat(value.toByteArray()).containsExactly(0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("1900-01-01 00:00:00, Flags: 0x00 00");
    }

    @Test
    @DisplayName("#(byte[]) with: Monday, 1950-02-03 06:15:20, Flags: 0xAA 80")
    void testBytes_1950_02_03() {
        final var value = new DPT19Value(new byte[]{0x32, 0x02, 0x03, 0x26, 0x0f, 0x14, (byte) 0xAA, (byte) 0x80});
        assertThat(value.getDayOfWeek()).isSameAs(DayOfWeek.MONDAY);
        assertThat(value.getDate()).isEqualTo(LocalDate.of(1950, 2, 3));
        assertThat(value.getTime()).isEqualTo(LocalTime.of(6, 15, 20));
        assertThat(value.getFlags()).isEqualTo(new Flags(true, false, true, false, true, false, true, false, true, false));
        assertThat(value.toByteArray()).containsExactly(0x32, 0x02, 0x03, 0x26, 0x0f, 0x14, 0xAA, 0x80);

        assertThat(value.toText()).isEqualTo("Monday, 1950-02-03 06:15:20, Flags: 0xAA 80");
    }

    @Test
    @DisplayName("#(byte[]) with: Wednesday, 2000-04-05 12:30:45, Flags: 0x55 00")
    void testBytes_2000_04_05() {
        final var value = new DPT19Value(new byte[]{0x64, 0x04, 0x05, 0x6c, 0x1e, 0x2d, 0x55, 0x00});
        assertThat(value.getDayOfWeek()).isSameAs(DayOfWeek.WEDNESDAY);
        assertThat(value.getDate()).isEqualTo(LocalDate.of(2000, 4, 5));
        assertThat(value.getTime()).isEqualTo(LocalTime.of(12, 30, 45));
        assertThat(value.getFlags()).isEqualTo(new Flags(false, true, false, true, false, true, false, true, false, false));
        assertThat(value.toByteArray()).containsExactly(0x64, 0x04, 0x05, 0x6c, 0x1e, 0x2d, 0x55, 0x00);

        assertThat(value.toText()).isEqualTo("Wednesday, 2000-04-05 12:30:45, Flags: 0x55 00");
    }

    @Test
    @DisplayName("#(byte[]) with: Sunday, 2155-12-31 23:59:59, Flags: 0xFF 80")
    void testByte_2155_12_31() {
        final var value = new DPT19Value(new byte[]{(byte) 0xFF, 0x0C, 0x1F, (byte) 0xF7, 0x3B, 0x3B, (byte) 0xFF, (byte) 0x80});
        assertThat(value.getDayOfWeek()).isSameAs(DayOfWeek.SUNDAY);
        assertThat(value.getDate()).isEqualTo(LocalDate.of(2155, 12, 31));
        assertThat(value.getTime()).isEqualTo(LocalTime.of(23, 59, 59));
        assertThat(value.getFlags()).isEqualTo(new Flags(true, true, true, true, true, true, true, true, true, false));
        assertThat(value.toByteArray()).containsExactly(0xFF, 0x0C, 0x1F, 0xF7, 0x3B, 0x3B, 0xFF, 0x80);

        assertThat(value.toText()).isEqualTo("Sunday, 2155-12-31 23:59:59, Flags: 0xFF 80");
    }

    @Test
    @DisplayName("#(DayOfWeek, LocalDate, LocalTime, Flags) with: No DayOfWeek, 1900-01-01 00:00:00, No flags (as null)")
    void test_1900_01_01_FlagNull() {
        // with flags = null
        final var value = new DPT19Value(null, LocalDate.of(1900, 1, 1), LocalTime.of(0, 0, 0), null);
        assertThat(value.getDate()).isEqualTo(LocalDate.of(1900, 1, 1));
        assertThat(value.getTime()).isEqualTo(LocalTime.of(0, 0, 0));
        assertThat(value.getFlags()).isEqualTo(Flags.NO_FLAGS);
        assertThat(value.getFlags()).isEqualTo(new Flags(false, false, false, false, false, false, false, false, false, false));
        assertThat(value.toByteArray()).containsExactly(0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("1900-01-01 00:00:00, Flags: 0x00 00");
    }

    @Test
    @DisplayName("#(DayOfWeek, LocalDate, LocalTime, Flags) with: No DayOfWeek, 1900-01-01 00:00:00, No flags (Flags.NO_FLAG)")
    void test_1900_01_01_NoFlag() {
        // with Flags.NO_FLAGS
        final var value = new DPT19Value(null, LocalDate.of(1900, 1, 1), LocalTime.of(0, 0, 0), Flags.NO_FLAGS);
        assertThat(value.getDate()).isEqualTo(LocalDate.of(1900, 1, 1));
        assertThat(value.getTime()).isEqualTo(LocalTime.of(0, 0, 0));
        assertThat(value.getFlags()).isEqualTo(new Flags(new byte[]{0x00, 0x00}));
        assertThat(value.toByteArray()).containsExactly(0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("1900-01-01 00:00:00, Flags: 0x00 00");
    }

    @Test
    @DisplayName("#(DayOfWeek, LocalDate, LocalTime, Flags) with: Monday, 1950-02-03 06:15:20, Flags: 0xAA 80")
    void test_1950_02_03() {
        final var value = new DPT19Value(
                DayOfWeek.MONDAY,
                LocalDate.of(1950, 2, 3),
                LocalTime.of(6, 15, 20),
                new Flags(true, false, true, false, true, false, true, false, true, false)
        );
        assertThat(value.getDayOfWeek()).isSameAs(DayOfWeek.MONDAY);
        assertThat(value.getDate()).isEqualTo(LocalDate.of(1950, 2, 3));
        assertThat(value.getTime()).isEqualTo(LocalTime.of(6, 15, 20));
        assertThat(value.getFlags()).isEqualTo(new Flags(new byte[]{(byte) 0xAA, (byte) 0x80}));
        assertThat(value.toByteArray()).containsExactly(0x32, 0x02, 0x03, 0x26, 0x0f, 0x14, 0xAA, 0x80);

        assertThat(value.toText()).isEqualTo("Monday, 1950-02-03 06:15:20, Flags: 0xAA 80");
    }

    @Test
    @DisplayName("#(DayOfWeek, LocalDate, LocalTime, Flags) with: Wednesday, 2000-04-05 12:30:45, Flags: 0x55 00")
    void test_2000_04_05() {
        final var value = new DPT19Value(
                DayOfWeek.WEDNESDAY,
                LocalDate.of(2000, 4, 5),
                LocalTime.of(12, 30, 45),
                new Flags(false, true, false, true, false, true, false, true, false, false)
        );
        assertThat(value.getDayOfWeek()).isSameAs(DayOfWeek.WEDNESDAY);
        assertThat(value.getDate()).isEqualTo(LocalDate.of(2000, 4, 5));
        assertThat(value.getTime()).isEqualTo(LocalTime.of(12, 30, 45));
        assertThat(value.getFlags()).isEqualTo(new Flags(new byte[]{0x55, 0x00}));
        assertThat(value.toByteArray()).containsExactly(0x64, 0x04, 0x05, 0x6c, 0x1e, 0x2d, 0x55, 0x00);

        assertThat(value.toText()).isEqualTo("Wednesday, 2000-04-05 12:30:45, Flags: 0x55 00");
    }

    @Test
    @DisplayName("#(DayOfWeek, LocalDate, LocalTime, Flags) with: Sunday, 2155-12-31 23:59:59, Flags: 0xFF 80")
    void test_2155_12_31() {
        final var value = new DPT19Value(
                DayOfWeek.SUNDAY,
                LocalDate.of(2155, 12, 31),
                LocalTime.of(23, 59, 59),
                new Flags(true, true, true, true, true, true, true, true, true, false)
        );
        assertThat(value.getDayOfWeek()).isSameAs(DayOfWeek.SUNDAY);
        assertThat(value.getDate()).isEqualTo(LocalDate.of(2155, 12, 31));
        assertThat(value.getTime()).isEqualTo(LocalTime.of(23, 59, 59));
        assertThat(value.getFlags()).isEqualTo(new Flags(new byte[]{(byte) 0xFF, (byte) 0x80}));
        assertThat(value.toByteArray()).containsExactly(0xFF, 0x0C, 0x1F, 0xF7, 0x3B, 0x3B, 0xFF, 0x80);

        assertThat(value.toText()).isEqualTo("Sunday, 2155-12-31 23:59:59, Flags: 0xFF 80");
    }

    @Test
    @DisplayName("#(byte[]) with invalid byte length")
    void testBytesOutOfRange() {
        // expected: 8 bytes, provided 13 bytes
        assertThatThrownBy(() -> new DPT19Value(new byte[13]))
                .isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var value = new DPT19Value(
                DayOfWeek.TUESDAY,
                LocalDate.of(2021, 1, 8),
                LocalTime.of(21, 4, 32),
                new Flags(true, false, true, true, true, false, false, false, true, false)

        );
        assertThat(value).hasToString(
                String.format(
                    "DPT19Value{dpt=19.001, dayOfWeek=TUESDAY, date=2021-01-08, time=21:04:32, flags=%s, byteArray=0x79 01 08 55 04 20 B8 80}",
                        new Flags(new byte[]{ (byte) 0xB8, (byte) 0x80 })
                )
        );

        final var valueBytes = new DPT19Value(new byte[]{0x7B, 0x0B, 0x18, (byte)0xB1, 0x04, 0x21, 0x08, 0x00});
        assertThat(valueBytes).hasToString(
                String.format(
                        "DPT19Value{dpt=19.001, dayOfWeek=FRIDAY, date=2023-11-24, time=17:04:33, flags=%s, byteArray=0x7B 0B 18 B1 04 21 08 00}",
                        new Flags(false, false, false, false, true, false, false, false, false, false)
                )
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT19Value(
                DayOfWeek.SUNDAY, //
                LocalDate.of(2155, 12, 30), //
                LocalTime.of(23, 59, 59), //
                Flags.NO_FLAGS //
        );
        final var valueNullFlags = new DPT19Value(
                DayOfWeek.SUNDAY, //
                LocalDate.of(2155, 12, 30), //
                LocalTime.of(23, 59, 59), //
                null //
        );
        final var valueByte = new DPT19Value(new byte[]{(byte) 0xff, 0x0c, 0x1e, (byte) 0xf7, 0x3b, 0x3b, 0x00, 0x00});

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(valueNullFlags).isEqualTo(value);
        assertThat(valueNullFlags).hasSameHashCodeAs(value);
        assertThat(valueByte).isEqualTo(value);
        assertThat(valueByte).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT19Value(null, LocalDate.of(2155, 12, 30), LocalTime.of(23, 59, 59), Flags.NO_FLAGS));
        assertThat(value).isNotEqualTo(new DPT19Value(DayOfWeek.MONDAY, LocalDate.of(2155, 12, 30), LocalTime.of(23, 59, 59), Flags.NO_FLAGS));
        assertThat(value).isNotEqualTo(new DPT19Value(DayOfWeek.SUNDAY, LocalDate.of(2100, 12, 30), LocalTime.of(23, 59, 59), Flags.NO_FLAGS));
        assertThat(value).isNotEqualTo(new DPT19Value(DayOfWeek.SUNDAY, LocalDate.of(2155, 1, 30), LocalTime.of(23, 59, 59), Flags.NO_FLAGS));
        assertThat(value).isNotEqualTo(new DPT19Value(DayOfWeek.SUNDAY, LocalDate.of(2155, 12, 1), LocalTime.of(23, 59, 59), Flags.NO_FLAGS));
        assertThat(value).isNotEqualTo(new DPT19Value(DayOfWeek.SUNDAY, LocalDate.of(2155, 12, 30), LocalTime.of(1, 59, 59), Flags.NO_FLAGS));
        assertThat(value).isNotEqualTo(new DPT19Value(DayOfWeek.SUNDAY, LocalDate.of(2155, 12, 30), LocalTime.of(23, 1, 59), Flags.NO_FLAGS));
        assertThat(value).isNotEqualTo(new DPT19Value(DayOfWeek.SUNDAY, LocalDate.of(2155, 12, 30), LocalTime.of(23, 59, 1), Flags.NO_FLAGS));
        assertThat(value).isNotEqualTo(new DPT19Value(DayOfWeek.SUNDAY, LocalDate.of(2155, 12, 30), LocalTime.of(23, 59, 59), new Flags(new byte[] { 0x01, 0x00})));
    }

    @Test
    @DisplayName("Flags#(boolean, ..) with: 1101 1001 1000 0000")
    void testFlags_1101_1001_1000_0000() {
        final var flags = new DPT19Value.Flags(
                true, true, false, true, //
                true, false, false, true, //
                true, false //
        );
        // instance methods
        assertThat(flags.isFault()).isTrue();
        assertThat(flags.isWorkingDay()).isTrue();
        assertThat(flags.isWorkingDayValid()).isFalse();
        assertThat(flags.isYearValid()).isTrue();
        assertThat(flags.isDateValid()).isTrue();
        assertThat(flags.isDayOfWeekValid()).isFalse();
        assertThat(flags.isTimeValid()).isFalse();
        assertThat(flags.isSummerTime()).isTrue();
        assertThat(flags.isClockWithExternalSyncSignal()).isTrue();
        assertThat(flags.getAsBytes()).containsExactly(0b1101_1001, 0b1000_0000);
    }

    @Test
    @DisplayName("Flags#(boolean, ..) with: 0110 1101 0100 0000")
    void testFlags_0110_1101_0000_0000() {
        final var flags = new DPT19Value.Flags(
                false, true, true, false, //
                true, true, false, true, //
                false, true //
        );
        // instance methods
        assertThat(flags.isFault()).isFalse();
        assertThat(flags.isWorkingDay()).isTrue();
        assertThat(flags.isWorkingDayValid()).isTrue();
        assertThat(flags.isYearValid()).isFalse();
        assertThat(flags.isDateValid()).isTrue();
        assertThat(flags.isDayOfWeekValid()).isTrue();
        assertThat(flags.isTimeValid()).isFalse();
        assertThat(flags.isSummerTime()).isTrue();
        assertThat(flags.isClockWithExternalSyncSignal()).isFalse();
        assertThat(flags.getAsBytes()).containsExactly(0b0110_1101, 0b0100_0000);
    }

    @Test
    @DisplayName("Flags#toString()")
    void testFlagsToString() {
        final var flags = new DPT19Value.Flags(true, false, true, false, true, true, false, false, true, true);
        assertThat(flags).hasToString(
                "Flags{fault=true, workingDay=false, workingDayValid=true, yearValid=false, dateValid=true, " +
                        "dayOfWeekValid=true, timeValid=false, summerTime=false, clockWithExternalSyncSignal=true, " +
                        "synchronizationSourceReliability=true}"
        );

        final var flagsBytes = new DPT19Value.Flags(new byte[]{0b0110_1101, 0b0000_0000});
        assertThat(flagsBytes).hasToString(
                "Flags{fault=false, workingDay=true, workingDayValid=true, yearValid=false, dateValid=true, " +
                        "dayOfWeekValid=true, timeValid=false, summerTime=true, clockWithExternalSyncSignal=false, " +
                        "synchronizationSourceReliability=false}"
        );
    }

    @Test
    @DisplayName("Flags#equals() and Flags#hashCode()")
    void testFlagsEqualsAndHashCode() {
        final var flags = new Flags(true, true, false, false, false, true, true, false, true, true);
        final var flagBytes = new Flags(new byte[]{(byte) 0b1100_0110, (byte) 0b1100_0000});

        // equals & same hash code
        assertThat(flags).isEqualTo(flags);
        assertThat(flagBytes).isEqualTo(flags);
        assertThat(flagBytes).hasSameHashCodeAs(flags);

        // not equals
        assertThat(flags).isNotEqualTo(null);
        assertThat(flags).isNotEqualTo(new Object());
        assertThat(flags).isNotEqualTo(new Flags(!true, true, false, false, false, true, true, false, true, true));
        assertThat(flags).isNotEqualTo(new Flags(true, !true, false, false, false, true, true, false, true, true));
        assertThat(flags).isNotEqualTo(new Flags(true, true, !false, false, false, true, true, false, true, true));
        assertThat(flags).isNotEqualTo(new Flags(true, true, false, !false, false, true, true, false, true, true));
        assertThat(flags).isNotEqualTo(new Flags(true, true, false, false, !false, true, true, false, true, true));
        assertThat(flags).isNotEqualTo(new Flags(true, true, false, false, false, !true, true, false, true, true));
        assertThat(flags).isNotEqualTo(new Flags(true, true, false, false, false, true, !true, false, true, true));
        assertThat(flags).isNotEqualTo(new Flags(true, true, false, false, false, true, true, !false, true, true));
        assertThat(flags).isNotEqualTo(new Flags(true, true, false, false, false, true, true, false, !true, true));
        assertThat(flags).isNotEqualTo(new Flags(true, true, false, false, false, true, true, false, true, !true));
    }
}
