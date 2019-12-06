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

import li.pitschmann.knx.core.datapoint.DPT19;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT19} (19.xxx)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Year)                        | 0   0   0   0   (Month)       |
 * Encoding    | U   U   U   U   U   U   U   U | r   r   r   r   U   U   U   U |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | 0   0   0   (Day Of Month)    | (DayOfWeek) (Hour)            |
 *             | r   r   r   U   U   U   U   U | U   U   U   U   U   U   U   U |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | 0   0   (Minutes)             | 0   0   (Seconds)             |
 *             | r   r   U   U   U   U   U   U | r   r   U   U   U   U   U   U |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | F  WD  NWD NY  ND  NDoW NT SST| CLQ 0   0   0   0   0   0   0 |
 *             | B   B   B   B   B   B   B   B |  B  r   r   r   r   r   r   r |
 *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     8 octets (U<sub>8</sub> [r<sub>4</sub>U<sub>4</sub>] [r<sub>3</sub>U<sub>5</sub>] [r<sub>3</sub>U<sub>5</sub>] [r<sub>2</sub>U<sub>6</sub>] [r<sub>2</sub>U<sub>6</sub>] B<sub>16</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT19Value extends AbstractDataPointValue<DPT19> {
    private static final Logger log = LoggerFactory.getLogger(DPT19Value.class);
    private final DayOfWeek dayOfWeek;
    private final LocalDate date;
    private final LocalTime time;
    private final Flags flags;
    private final byte[] byteArray;

    public DPT19Value(final byte[] bytes) {
        super(DPT19.DATE_TIME);
        Preconditions.checkArgument(bytes.length == 8);

        this.dayOfWeek = toDayOfWeek(bytes);
        this.date = toLocalDate(bytes);
        this.time = toLocalTime(bytes);
        this.flags = new Flags(new byte[]{bytes[6], bytes[7]});
        this.byteArray = bytes;
    }

    public DPT19Value(final @Nullable DayOfWeek dayOfWeek,
                      final LocalDate date,
                      final LocalTime time,
                      final @Nullable Flags flags) {
        super(DPT19.DATE_TIME);
        Preconditions.checkNonNull(date);
        Preconditions.checkNonNull(time);
        Preconditions.checkArgument(date.getYear() >= 1900 && date.getYear() <= 2155,
                "Year must be between '1900..2155'. Got: {}", date.getYear());

        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.time = time;
        this.flags = Objects.requireNonNullElse(flags, Flags.NO_FLAGS);
        this.byteArray = toByteArray(dayOfWeek, date, time, flags);
    }

    /**
     * Converts byte array to {@link DayOfWeek}
     *
     * @param bytes
     * @return {@link DayOfWeek}, if no-day then return {@code null}
     */
    @Nullable
    private static DayOfWeek toDayOfWeek(final byte[] bytes) {
        // byte 3: day of week
        final var dayNr = (bytes[3] & 0xE0) >>> 5;

        final var dayOfWeek = dayNr == 0 ? null : DayOfWeek.of(dayNr);
        log.debug("DayOfWeek of '{}': {}", ByteFormatter.formatHex(bytes), dayOfWeek);
        return dayOfWeek;
    }

    /**
     * Converts byte array to {@link LocalDate}
     *
     * @param bytes
     * @return {@link LocalDate}
     */
    private static LocalDate toLocalDate(final byte[] bytes) {
        // byte 0: year (starting from 1900: 0=1900, 255=2155)
        final var year = Bytes.toUnsignedInt(bytes[0]) + 1900;

        // byte 1: month
        final var month = Bytes.toUnsignedInt(bytes[1]);

        // byte 2: day of month
        final var dayOfMonth = Bytes.toUnsignedInt(bytes[2]);

        final var date = LocalDate.of(year, month, dayOfMonth);
        log.debug("Date of '{}': {}", ByteFormatter.formatHex(bytes), date);
        return date;
    }

    /**
     * Converts byte array to {@link LocalTime}
     *
     * @param bytes
     * @return {@link LocalTime}
     */
    private static LocalTime toLocalTime(final byte[] bytes) {
        // byte 3: hour (day of week is done separately)
        final var hour = bytes[3] & 0x1F;

        // byte 4: minute
        final var minute = Bytes.toUnsignedInt(bytes[4]);

        // byte 5: second
        final var second = Bytes.toUnsignedInt(bytes[5]);

        final var time = LocalTime.of(hour, minute, second);
        log.debug("DateTime of '{}': {}", ByteFormatter.formatHex(bytes), time);
        return time;
    }

    /**
     * Converts {@link LocalDateTime} value to byte array
     *
     * @param dayOfWeek
     * @param date
     * @param time
     * @param flags
     * @return byte array
     */
    public static byte[] toByteArray(final @Nullable DayOfWeek dayOfWeek,
                                     final LocalDate date,
                                     final LocalTime time,
                                     final @Nullable Flags flags) {
        Preconditions.checkArgument(date.getYear() >= 1900 && date.getYear() <= 2155, "Year must be between '1900..2155'. Got: " + date.getYear());

        // byte 0: year (starting from 1900: 0=1900, 255=2155)
        final var yearAsByte = (byte) (date.getYear() - 1900);

        // byte 1: month
        final var monthAsByte = (byte) date.getMonthValue();

        // byte 2: day of month
        final var dayOfMonthAsByte = (byte) date.getDayOfMonth();

        // byte 3: dayOfWeek + hour
        final var dayOfWeekAsByte = dayOfWeek == null ? 0x00 : (byte) (dayOfWeek.getValue() << 5);
        final var hourAsByte = (byte) time.getHour();

        // byte 4: minute
        final var minuteAsByte = (byte) time.getMinute();

        // byte 5: second
        final var secondAsByte = (byte) time.getSecond();

        // byte 6 + 7: flags
        final var byte6FlagsAsByte = flags == null ? 0x00 : flags.getByte6();
        final var byte7FlagsAsByte = flags == null ? 0x00 : flags.getByte7();

        final var bytes = new byte[]{yearAsByte, monthAsByte, dayOfMonthAsByte, (byte) (dayOfWeekAsByte | hourAsByte), minuteAsByte, secondAsByte,
                byte6FlagsAsByte, byte7FlagsAsByte};
        if (log.isDebugEnabled()) {
            log.debug("Bytes of [DayOfWeek={}, Date={}, Time={}, Flags={}]: {}", dayOfWeek, date, time, flags, ByteFormatter.formatHexAsString(bytes));
        }
        return bytes;
    }

    @Nullable
    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }


    public LocalDate getDate() {
        return this.date;
    }


    public LocalTime getTime() {
        return this.time;
    }


    public Flags getFlags() {
        return this.flags;
    }


    @Override
    public byte[] toByteArray() {
        return this.byteArray.clone();
    }


    @Override
    public String toText() {
        final var sb = new StringBuilder(50);
        final var dayOfWeek = getDayOfWeek();
        if (dayOfWeek != null) {
            sb.append(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())).append(", ");
        }
        sb.append(getDate().format(DateTimeFormatter.ISO_DATE))
                .append(' ')
                .append(getTime().format(DateTimeFormatter.ISO_TIME))
                .append(", flags: ")
                .append(getFlags().toText());
        return sb.toString();
    }


    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("dayOfWeek", this.dayOfWeek)
                .add("date", this.date)
                .add("time", this.time)
                .add("flags", this.flags)
                .add("byteArray", ByteFormatter.formatHexAsString(this.byteArray))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT19Value) {
            final var other = (DPT19Value) obj;
            return Objects.equals(this.dayOfWeek, other.dayOfWeek) //
                    && Objects.equals(this.date, other.date) //
                    && Objects.equals(this.time, other.time) //
                    && Objects.equals(this.flags, other.flags);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dayOfWeek, this.date, this.time, this.flags);
    }

    /**
     * Flags for {@link DPT19Value}
     *
     * @author PITSCHR
     */
    public static final class Flags {
        public static final Flags NO_FLAGS = new Flags(new byte[2]);
        private boolean fault;
        private boolean workingDay;
        private boolean workingDayValid;
        private boolean yearValid;
        private boolean dateValid;
        private boolean dayOfWeekValid;
        private boolean timeValid;
        private boolean summerTime;
        private boolean clockWithExternalSyncSignal;

        /**
         * Create {@link Flags} given two bytes
         *
         * @param bytes
         * @return new instance of {@link Flags}
         */
        public Flags(final byte[] bytes) {
            Preconditions.checkArgument(bytes != null && bytes.length == 2, "The length of bytes must be 2 (actual: " + (bytes == null ? 0 : bytes.length) + ")");

            // byte 6
            this.fault = (bytes[0] & 0x80) != 0x00;
            this.workingDay = (bytes[0] & 0x40) != 0x00;
            this.workingDayValid = (bytes[0] & 0x20) != 0x00;
            this.yearValid = (bytes[0] & 0x10) != 0x00;
            this.dateValid = (bytes[0] & 0x08) != 0x00;
            this.dayOfWeekValid = (bytes[0] & 0x04) != 0x00;
            this.timeValid = (bytes[0] & 0x02) != 0x00;
            this.summerTime = (bytes[0] & 0x01) != 0x00;

            // byte 7
            this.clockWithExternalSyncSignal = (bytes[1] & 0x80) != 0x00;
        }

        /**
         * Create {@link Flags} with given parameters
         *
         * @param fault
         * @param workingDay
         * @param workingDayValid
         * @param yearValid
         * @param dateValid
         * @param dayOfWeekValid
         * @param timeValid
         * @param summerTime
         * @param clockWithExternalSyncSignal
         * @return new instance of {@link Flags}
         */
        public Flags(final boolean fault,
                     final boolean workingDay,
                     final boolean workingDayValid,
                     final boolean yearValid,
                     final boolean dateValid,
                     final boolean dayOfWeekValid,
                     final boolean timeValid,
                     final boolean summerTime,
                     final boolean clockWithExternalSyncSignal) {
            this.fault = fault;
            this.workingDay = workingDay;
            this.workingDayValid = workingDayValid;
            this.yearValid = yearValid;
            this.dateValid = dateValid;
            this.dayOfWeekValid = dayOfWeekValid;
            this.timeValid = timeValid;
            this.summerTime = summerTime;
            this.clockWithExternalSyncSignal = clockWithExternalSyncSignal;
        }

        public boolean isFault() {
            return this.fault;
        }

        public boolean isWorkingDay() {
            return this.workingDay;
        }

        public boolean isWorkingDayValid() {
            return this.workingDayValid;
        }

        public boolean isYearValid() {
            return this.yearValid;
        }

        public boolean isDateValid() {
            return this.dateValid;
        }

        public boolean isDayOfWeekValid() {
            return this.dayOfWeekValid;
        }

        public boolean isTimeValid() {
            return this.timeValid;
        }

        public boolean isSummerTime() {
            return this.summerTime;
        }

        public boolean isClockWithExternalSyncSignal() {
            return this.clockWithExternalSyncSignal;
        }

        /**
         * Returns the byte-6 flag settings as byte for {@link DPT19Value}
         *
         * @return byte
         */
        private byte getByte6() {
            byte byte6 = 0x00;
            if (this.fault) {
                byte6 |= 0x80;
            }
            if (this.workingDay) {
                byte6 |= 0x40;
            }
            if (this.workingDayValid) {
                byte6 |= 0x20;
            }
            if (this.yearValid) {
                byte6 |= 0x10;
            }
            if (this.dateValid) {
                byte6 |= 0x08;
            }
            if (this.dayOfWeekValid) {
                byte6 |= 0x04;
            }
            if (this.timeValid) {
                byte6 |= 0x02;
            }
            if (this.summerTime) {
                byte6 |= 0x01;
            }
            return byte6;
        }

        /**
         * Returns the byte-7 flag settings as byte for {@link DPT19Value}
         *
         * @return byte
         */
        private byte getByte7() {
            return this.clockWithExternalSyncSignal ? (byte) 0x80 : 0x00;
        }

        /**
         * Returns the two-byte array flag setting for {@link DPT19Value}
         *
         * @return two-byte array
         */
    public byte[] getAsBytes() {
            return new byte[]{this.getByte6(), this.getByte7()};
        }

        /**
         * Returns the Flag as string representation
         *
         * @return flag as string
         */
        public String toText() {
            return ByteFormatter.formatHexAsString(getAsBytes());
        }


        @Override
        public String toString() {
            // @formatter:off
            return Strings.toStringHelper(this)
                    // byte 6
                    .add("fault", this.fault)
                    .add("workingDay", this.workingDay)
                    .add("workingDayValid", this.workingDayValid)
                    .add("yearValid", this.yearValid)
                    .add("dateValid", this.dateValid)
                    .add("dayOfWeekValid", this.dayOfWeekValid)
                    .add("timeValid", this.timeValid)
                    .add("summerTime", this.summerTime)
                    // byte 7
                    .add("clockWithExternalSyncSignal", this.clockWithExternalSyncSignal)
                    .toString();
            // @formatter:on
        }

        @Override
        public boolean equals(final @Nullable Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof Flags) {
                final var other = (Flags) obj;
                return Objects.equals(this.fault, other.fault) //
                        && Objects.equals(this.workingDay, other.workingDay) //
                        && Objects.equals(this.workingDayValid, other.workingDayValid) //
                        && Objects.equals(this.yearValid, other.yearValid) //
                        && Objects.equals(this.dateValid, other.dateValid) //
                        && Objects.equals(this.dayOfWeekValid, other.dayOfWeekValid) //
                        && Objects.equals(this.timeValid, other.timeValid) //
                        && Objects.equals(this.summerTime, other.summerTime) //
                        && Objects.equals(this.clockWithExternalSyncSignal, other.clockWithExternalSyncSignal);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.fault, //
                    this.workingDay, //
                    this.workingDayValid, //
                    this.yearValid, //
                    this.dateValid, //
                    this.dayOfWeekValid, //
                    this.timeValid, //
                    this.summerTime, //
                    this.clockWithExternalSyncSignal);
        }
    }
}
