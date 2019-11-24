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

package li.pitschmann.knx.link.datapoint.value;

import li.pitschmann.knx.link.datapoint.DPT19;
import li.pitschmann.knx.link.datapoint.value.DPT19Value.Flags;
import li.pitschmann.knx.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT19Value}
 *
 * @author PITSCHR
 */
public final class DPT19ValueTest {
    /**
     * Test {@link DPT19Value}
     */
    @Test
    public void test() {
        // no day, 1900-01-01 00:00:00
        this.assertValue(
                new byte[]{0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00}, //
                null, //
                LocalDate.of(1900, 1, 1), //
                LocalTime.of(0, 0, 0), //
                new Flags(false, false, false, false, false, false, false, false, false),
                "1900-01-01 00:00:00, flags: 0x00 00"
        );
        // monday, 1950-02-03 06:15:20
        // monday => 001. ....
        // hour 6 => ...0 0110
        this.assertValue(
                new byte[]{0x32, 0x02, 0x03, 0x26, 0x0f, 0x14, (byte) 0xAA, (byte) 0x80}, //
                DayOfWeek.MONDAY, //
                LocalDate.of(1950, 2, 3), //
                LocalTime.of(6, 15, 20), //
                new Flags(true, false, true, false, true, false, true, false, true),
                "Monday, 1950-02-03 06:15:20, flags: 0xAA 80"
        );
        // wednesday, 2000-04-05 12:30:45
        // wednesday => 011. ....
        // hour 12 ===> ...0 1100
        this.assertValue(
                new byte[]{0x64, 0x04, 0x05, 0x6c, 0x1e, 0x2d, 0x55, 0x00}, //
                DayOfWeek.WEDNESDAY, //
                LocalDate.of(2000, 4, 5), //
                LocalTime.of(12, 30, 45), //
                new Flags(false, true, false, true, false, true, false, true, false),
                "Wednesday, 2000-04-05 12:30:45, flags: 0x55 00"
        );
        // sunday, 2155-12-30 23:59:59
        // sunday ==> 111. ....
        // hour 23 => ...1 0111
        this.assertValue(
                new byte[]{(byte) 0xff, 0x0c, 0x1e, (byte) 0xf7, 0x3b, 0x3b, (byte) 0xff, (byte) 0x80}, //
                DayOfWeek.SUNDAY, //
                LocalDate.of(2155, 12, 30), //
                LocalTime.of(23, 59, 59), //
                new Flags(true, true, true, true, true, true, true, true, true),
                "Sunday, 2155-12-30 23:59:59, flags: 0xFF 80");

        // no flags, sunday, 2155-12-30 23:59:59
        this.assertValue(
                new byte[]{(byte) 0xff, 0x0c, 0x1e, (byte) 0xf7, 0x3b, 0x3b, 0x00, 0x00}, //
                DayOfWeek.SUNDAY, //
                LocalDate.of(2155, 12, 30), //
                LocalTime.of(23, 59, 59), //
                null,
                "Sunday, 2155-12-30 23:59:59, flags: 0x00 00"
        );
    }

    /**
     * Test {@link DPT19Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPT19Value(new byte[0])).isInstanceOf(IllegalArgumentException.class);

        // invalid year (only 1900..2155 should be accepted)
        assertThatThrownBy(() -> new DPT19Value(null, LocalDate.of(1899, 12, 31), LocalTime.now(), null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new DPT19Value(null, LocalDate.of(2156, 1, 1), LocalTime.now(), null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DPT19Value.toByteArray(null, LocalDate.of(1899, 12, 31), LocalTime.now(), null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DPT19Value.toByteArray(null, LocalDate.of(2156, 1, 1), LocalTime.now(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void assertValue(final byte[] bytes, final DayOfWeek dayOfWeek, final LocalDate date, final LocalTime time, final Flags flags, final String text) {
        final var flagsNotNull = Objects.requireNonNullElse(flags, Flags.NO_FLAGS);

        final var dptValue = new DPT19Value(dayOfWeek, date, time, flags);
        final var dptValueFlagsNotNull = new DPT19Value(dayOfWeek, date, time, flagsNotNull);
        final var dptValueByByte = new DPT19Value(bytes);

        // instance methods
        assertThat(dptValue.getDayOfWeek()).isEqualTo(dayOfWeek);
        assertThat(dptValue.getDate()).isEqualTo(date);
        assertThat(dptValue.getTime()).isEqualTo(time);
        assertThat(dptValue.getFlags()).isEqualTo(flagsNotNull);
        assertThat(dptValue.toByteArray()).containsExactly(bytes);
        assertThat(dptValue.toText()).isEqualTo(text);

        // class methods
        assertThat(DPT19Value.toByteArray(dayOfWeek, date, time, flagsNotNull)).containsExactly(bytes);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueFlagsNotNull).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        final var anotherDayOfWeek = dayOfWeek == null ? DayOfWeek.MONDAY : dayOfWeek.plus(1);
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT19Value(anotherDayOfWeek, date, time, flagsNotNull));
        assertThat(dptValue).isNotEqualTo(new DPT19Value(dayOfWeek, date.plusDays(1), time, flagsNotNull));
        assertThat(dptValue).isNotEqualTo(new DPT19Value(dayOfWeek, date, time.plusMinutes(1), flagsNotNull));
        if (dayOfWeek != null) {
            // additional check when day of week is not null
            assertThat(dptValue).isNotEqualTo(new DPT19Value(null, date, time, flagsNotNull));
            assertThat(dptValue).isNotEqualTo(new DPT19Value(null, date.plusDays(1), time, flagsNotNull));
            assertThat(dptValue).isNotEqualTo(new DPT19Value(null, date, time.plusMinutes(1), flagsNotNull));
        }
        final var flagBytes = flagsNotNull.getAsBytes();
        final var anotherFlagByte1 = (byte) ((flagBytes[0] & 0x80) == 0 ? flagBytes[0] | 0x80 : flagBytes[0] & 0x7F);
        final var anotherFlagByte2 = (byte) ((flagBytes[1] & 0x80) == 0 ? flagBytes[1] | 0x80 : flagBytes[1] & 0x7F);
        assertThat(dptValue).isNotEqualTo(new DPT19Value(dayOfWeek, date, time, new Flags(new byte[]{anotherFlagByte1, flagBytes[1]})));
        assertThat(dptValue).isNotEqualTo(new DPT19Value(dayOfWeek, date, time, new Flags(new byte[]{flagBytes[0], anotherFlagByte2})));

        // toString
        final var toString = String.format("DPT19Value{dpt=%s, dayOfWeek=%s, date=%s, time=%s, flags=%s, byteArray=%s}", DPT19.DATE_TIME, dayOfWeek,
                date, time, flagsNotNull, ByteFormatter.formatHexAsString(bytes));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }

    /**
     * Test {@link Flags}
     */
    @Test
    public void testFlags() {
        this.assertFlags(new byte[]{0x00, 0x00}, false, false, false, false, false, false, false, false, false);
    }

    private void assertFlags(final byte[] bytes, final boolean fault, final boolean workingDay, final boolean workingDayValid,
                             final boolean yearValid, final boolean dateValid, final boolean dayOfWeekValid, final boolean timeValid, final boolean summerTime,
                             final boolean clockWithExternalSyncSignal) {
        final var flags = new Flags(fault, workingDay, workingDayValid, yearValid, dateValid, dayOfWeekValid, timeValid, summerTime,
                clockWithExternalSyncSignal);
        final var flagsByBytes = new Flags(bytes);

        // instance methods
        assertThat(flags.isFault()).isEqualTo(fault);
        assertThat(flags.isWorkingDay()).isEqualTo(workingDay);
        assertThat(flags.isWorkingDayValid()).isEqualTo(workingDayValid);
        assertThat(flags.isYearValid()).isEqualTo(yearValid);
        assertThat(flags.isDateValid()).isEqualTo(dateValid);
        assertThat(flags.isDayOfWeekValid()).isEqualTo(dayOfWeekValid);
        assertThat(flags.isTimeValid()).isEqualTo(timeValid);
        assertThat(flags.isSummerTime()).isEqualTo(summerTime);
        assertThat(flags.isClockWithExternalSyncSignal()).isEqualTo(clockWithExternalSyncSignal);
        assertThat(flags.getAsBytes()).containsExactly(bytes);

        // equals
        assertThat(flags).isEqualTo(flags);
        assertThat(flagsByBytes).isEqualTo(flags);
        assertThat(flagsByBytes).hasSameHashCodeAs(flags);

        // not equals
        assertThat(flags).isNotEqualTo(null);
        assertThat(flags).isNotEqualTo(new Object());
        // @formatter:off
        assertThat(flags).isNotEqualTo(new Flags(!fault, workingDay, workingDayValid, yearValid, dateValid, dayOfWeekValid, timeValid, summerTime, clockWithExternalSyncSignal));
        assertThat(flags).isNotEqualTo(new Flags(fault, !workingDay, workingDayValid, yearValid, dateValid, dayOfWeekValid, timeValid, summerTime, clockWithExternalSyncSignal));
        assertThat(flags).isNotEqualTo(new Flags(fault, workingDay, !workingDayValid, yearValid, dateValid, dayOfWeekValid, timeValid, summerTime, clockWithExternalSyncSignal));
        assertThat(flags).isNotEqualTo(new Flags(fault, workingDay, workingDayValid, !yearValid, dateValid, dayOfWeekValid, timeValid, summerTime, clockWithExternalSyncSignal));
        assertThat(flags).isNotEqualTo(new Flags(fault, workingDay, workingDayValid, yearValid, !dateValid, dayOfWeekValid, timeValid, summerTime, clockWithExternalSyncSignal));
        assertThat(flags).isNotEqualTo(new Flags(fault, workingDay, workingDayValid, yearValid, dateValid, !dayOfWeekValid, timeValid, summerTime, clockWithExternalSyncSignal));
        assertThat(flags).isNotEqualTo(new Flags(fault, workingDay, workingDayValid, yearValid, dateValid, dayOfWeekValid, !timeValid, summerTime, clockWithExternalSyncSignal));
        assertThat(flags).isNotEqualTo(new Flags(fault, workingDay, workingDayValid, yearValid, dateValid, dayOfWeekValid, timeValid, !summerTime, clockWithExternalSyncSignal));
        assertThat(flags).isNotEqualTo(new Flags(fault, workingDay, workingDayValid, yearValid, dateValid, dayOfWeekValid, timeValid, summerTime, !clockWithExternalSyncSignal));
        // @formatter:on

        // toString
        final var toString = String.format(
                "Flags{fault=%s, workingDay=%s, workingDayValid=%s, yearValid=%s, dateValid=%s, dayOfWeekValid=%s, timeValid=%s, summerTime=%s, clockWithExternalSyncSignal=%s}",
                fault, workingDay, workingDayValid, yearValid, dateValid, dayOfWeekValid, timeValid, summerTime, clockWithExternalSyncSignal);
        assertThat(flags).hasToString(toString);
        assertThat(flagsByBytes).hasToString(toString);
    }

    /**
     * Test failures for {@link DPT19Value}
     */
    @Test
    public void testFlagsFailures() {
        assertThatThrownBy(() -> new Flags(new byte[1])).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The length of bytes must be 2 (actual: 1)");
        assertThatThrownBy(() -> new Flags(new byte[3])).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The length of bytes must be 2 (actual: 3)");
    }
}
