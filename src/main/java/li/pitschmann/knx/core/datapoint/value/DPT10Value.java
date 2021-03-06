/*
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

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.DPT10;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT10} (10.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Day)       (Hour)            |
 * Encoding     | N   N   N   U   U   U   U   U |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | 0   0   (Minutes)             |
 *              |         U   U   U   U   U   U |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | 0   0   (Seconds)             |
 *              |         U   U   U   U   U   U |
 *              +---+---+---+---+---+---+---+---+
 * Format:     3 octets (N<sub>3</sub> U<sub>5</sub> r<sub>2</sub> U<sub>6</sub> r<sub>2</sub> U<sub>6</sub>)
 * Encoding:   DayOfWeek = [0 .. 7]
 *                1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday, 7 = Sunday, 0 = no day
 *             Hour    = [0 .. 23]
 *             Minutes = [0 .. 59]
 *             Seconds = [0 .. 59]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT10Value extends AbstractDataPointValue<DPT10> {
    private static final Logger log = LoggerFactory.getLogger(DPT10Value.class);
    private final DayOfWeek dayOfWeek;
    private final LocalTime time;

    public DPT10Value(final byte[] bytes) {
        super(DPT10.TIME_OF_DAY);
        // validate
        if (bytes.length != 3) {
            throw new KnxNumberOutOfRangeException("bytes", 3, 3, bytes.length, bytes);
        }

        this.dayOfWeek = toDayOfWeek(bytes);
        this.time = toLocalTime(bytes);
    }

    public DPT10Value(final @Nullable DayOfWeek dayOfWeek, final LocalTime time) {
        super(DPT10.TIME_OF_DAY);
        this.dayOfWeek = dayOfWeek;
        this.time = Objects.requireNonNull(time);
    }

    /**
     * Converts byte array to {@link DayOfWeek}
     *
     * @param bytes byte array to be converted
     * @return {@link DayOfWeek}, if no-day then return {@code null}
     */
    @Nullable
    private static DayOfWeek toDayOfWeek(final byte[] bytes) {
        // day of week
        final var dayNr = (bytes[0] & 0xE0) >>> 5;

        final var dayOfWeek = dayNr == 0 ? null : DayOfWeek.of(dayNr);
        if (log.isDebugEnabled()) {
            log.debug("DayOfWeek of '{}': {}", ByteFormatter.formatHex(bytes), dayOfWeek);
        }
        return dayOfWeek;
    }

    /**
     * Converts byte array to {@link LocalTime}
     *
     * @param bytes byte array to be converted
     * @return {@link LocalTime}
     */
    private static LocalTime toLocalTime(final byte[] bytes) {
        // hour
        final var hour = bytes[0] & 0x1F;

        // minute
        final var minute = Byte.toUnsignedInt(bytes[1]);

        // second
        final var second = Byte.toUnsignedInt(bytes[2]);

        final var time = LocalTime.of(hour, minute, second);
        if (log.isDebugEnabled()) {
            log.debug("Time of '{}': {}", ByteFormatter.formatHex(bytes), time);
        }
        return time;
    }

    @Nullable
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public byte[] toByteArray() {
        // byte 0: day-of-week + hour
        final var hourAsByte = (byte) time.getHour();
        final var dayOfWeekAsByte = dayOfWeek == null ? 0x00 : (byte) (dayOfWeek.getValue() << 5);

        // byte 1: minute
        final var minuteAsByte = (byte) time.getMinute();

        // byte 2: second
        final var secondAsByte = (byte) time.getSecond();

        final var bytes = new byte[]{(byte) (dayOfWeekAsByte | hourAsByte), minuteAsByte, secondAsByte};
        if (log.isDebugEnabled()) {
            log.debug("Bytes of [DayOfWeek={}, LocalTime={}]: {}", dayOfWeek, time, ByteFormatter.formatHexAsString(bytes));
        }
        return bytes;
    }

    @Override
    public String toText() {
        final var sb = new StringBuilder(30);
        final var dayOfWeek = getDayOfWeek();
        if (dayOfWeek != null) {
            sb.append(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())).append(", ");
        }
        sb.append(getTime().format(DateTimeFormatter.ISO_TIME));
        return sb.toString();
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("dayOfWeek", dayOfWeek)
                .add("time", time)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT10Value) {
            final var other = (DPT10Value) obj;
            return Objects.equals(this.dayOfWeek, other.dayOfWeek) //
                    && Objects.equals(this.time, other.time);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, time);
    }
}
