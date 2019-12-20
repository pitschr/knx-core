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

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.annotation.KnxDataPointType;
import li.pitschmann.knx.core.datapoint.value.DPT19Value;
import li.pitschmann.knx.core.datapoint.value.DPT19Value.Flags;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

/**
 * Data Point Type 19 for 'Date &amp; Time' (8 Octets)
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
public final class DPT19 extends AbstractDataPointType<DPT19Value> {
    /**
     * <strong>19.001</strong> Date & Time
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
     * Encoding:
     *             Year  = [0 .. 255]
     *                0 = year 1900
     *                255 = year 2155
     *             Month = [1 .. 12]
     *             DayOfMonth = [1 .. 31]
     *             DayOfWeek = [0 .. 7]
     *                1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday, 7 = Sunday, 0 = any day
     *             Hour    = [0 .. 24]
     *             Minutes = [0 .. 59]
     *             Seconds = [0 .. 59]
     *             (F)   Fault = {0, 1}
     *                        0 = Normal (no fault)
     *                        1 = Fault
     *             (WD)   WorkingDay = {0, 1}
     *                        0 = No Working Day
     *                        1 = Working Day
     *             (NWD)  NoWorkingDay = {0, 1}
     *                        0 = WorkingDay field valid
     *                        1 = WorkingDay field not valid
     *             (NY)   NoYear = {0, 1}
     *                        0 = Year field valid
     *                        1 = Year field not valid
     *             (ND)   NoDate = {0, 1}
     *                        0 = Month and DayOfMonth fields valid
     *                        1 = Month and DayOfMonth fields not valid
     *             (NDoW) NoDayOfWeek = {0, 1}
     *                        0 = DayOfWeek field valid
     *                        1 = DayOfWeek field not valid
     *             (NT)   NoTime = {0, 1}
     *                        0 = Hour, Minutes and Seconds valid
     *                        1 = Hour, Minutes and Seconds not valid
     *             (SST)  Standard Summer Time = {0, 1}
     *                        0 = UTC+x (standard time)
     *                        1 = UTC+x +1h (summer daylight saving time)
     *             (CLQ)  QualityOfClock = {0, 1}
     *                        0 = Clock without external synchronization signal
     *                        1 = Clock with external synchronization signal (DCF 77, VideoText, ...)
     * </pre>
     * <p>
     * The encoding of the hour is within the range [0 .. 24] instead of [0 .. 23]. When the hour is set to "24", the
     * values of octet 3 (Minutes) and 2 (Seconds) have to be set to zero.
     * <p>
     * "Fault" is set if one ore more supported fields of the Date & Time information are corrupted. "Fault" is set e.g.
     * power-down if battery backup was not sufficient, after 1st start up of device (clock unconfigured) or radio-clock
     * (DCF 77) had no reception for a very long time. "Fault" is usually cleared automatically by the device if the
     * local clock is set or clock data is refreshed
     * <p>
     * The receiver (e.g. a room unit, MMI) will interpret Date&Time with "Fault" as corrupted and will either ignore
     * the message or show --:--:-- or blinking 00:00:00 (as known from Video recorders after power-up).
     */
    @KnxDataPointType(id = "19.001", description = "Date & Time")
    public static final DPT19 DATE_TIME = new DPT19("19.001", "Date & Time");

    /**
     * Constructor for {@link DPT19}
     *
     * @param id
     * @param desc
     */
    private DPT19(final String id, final String desc) {
        super(id, desc);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 8;
    }

    @Override
    protected DPT19Value parse(final byte[] bytes) {
        return new DPT19Value(bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length >= 1 && args.length <= 4;
    }

    @Override
    protected DPT19Value parse(final String[] args) {
        final var dayOfWeek = this.findByEnumConstant(args, DayOfWeek.class);
        final var date = Preconditions.checkNonNull(this.findByPattern(args, Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"), LocalDate::parse),
                "Date must be present in format: 0000-00-00");
        final var time = Preconditions.checkNonNull(this.findByPattern(args, Pattern.compile("^[0-9]{2}:[0-9]{2}(:[0-9]{2})?$"), LocalTime::parse),
                "Time must be present in format: 00:00:00 or 00:00");
        final var flags = this.findByPattern(args, Pattern.compile("^(0x)?([0-9a-fA-F]{2}\\s?){2}$"), v -> new Flags(Bytes.toByteArray(v)), null);

        return new DPT19Value(dayOfWeek, date, time, flags);
    }

    public DPT19Value toValue(final @Nullable DayOfWeek dayOfWeek, final LocalDate date, final LocalTime time) {
        return toValue(dayOfWeek, date, time, Flags.NO_FLAGS);
    }

    public DPT19Value toValue(final @Nullable DayOfWeek dayOfWeek, final LocalDate date, final LocalTime time, final Flags flags) {
        return new DPT19Value(dayOfWeek, date, time, flags);
    }

    public byte[] toByteArray(final @Nullable DayOfWeek dayOfWeek, final LocalDate date, final LocalTime time) {
        return toByteArray(dayOfWeek, date, time, Flags.NO_FLAGS);
    }

    public byte[] toByteArray(final @Nullable DayOfWeek dayOfWeek, final LocalDate date, final LocalTime time, final Flags flags) {
        return DPT19Value.toByteArray(dayOfWeek, date, time, flags);
    }
}
