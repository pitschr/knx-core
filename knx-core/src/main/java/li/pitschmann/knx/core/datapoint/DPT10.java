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
import li.pitschmann.knx.core.datapoint.value.DPT10Value;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.regex.Pattern;

/**
 * Data Point Type 10 for 'Time' (3 Octets)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Day)       (Hour)            |
 * Encoding    | N   N   N   U   U   U   U   U |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | 0   0   (Minutes)             |
 *             |         U   U   U   U   U   U |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | 0   0   (Seconds)             |
 *             |         U   U   U   U   U   U |
 *             +---+---+---+---+---+---+---+---+
 * Format:     3 octets (N<sub>3</sub> U<sub>5</sub> r<sub>2</sub> U<sub>6</sub> r<sub>2</sub> U<sub>6</sub>)
 * Encoding:   Day = [0 .. 7]
 *                1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday, 7 = Sunday, 0 = no day
 *             Hour    = [0 .. 23]
 *             Minutes = [0 .. 59]
 *             Seconds = [0 .. 59]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT10 extends BaseDataPointType<DPT10Value> {
    /**
     * <strong>10.001</strong> Time Of Day
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Day)       (Hour)            |
     * Encoding    | N   N   N   U   U   U   U   U |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | 0   0   (Minutes)             |
     *             |         U   U   U   U   U   U |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | 0   0   (Seconds)             |
     *             |         U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     3 octets (N<sub>3</sub> U<sub>5</sub> r<sub>2</sub> U<sub>6</sub> r<sub>2</sub> U<sub>6</sub>)
     * Encoding:   Day = [0 .. 7]
     *                1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday, 7 = Sunday, 0 = no day
     *             Hour    = [0 .. 23]
     *             Minutes = [0 .. 59]
     *             Seconds = [0 .. 59]
     * </pre>
     */
    @DataPoint({"10.001", "dpt-10", "dpst-10-1"})
    public static final DPT10 TIME_OF_DAY = new DPT10("Time Of Day");

    /**
     * Constructor for {@link DPT10}
     *
     * @param desc description for {@link DPT10}
     */
    private DPT10(final String desc) {
        super(desc);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 3;
    }

    @Override
    protected DPT10Value parse(final byte[] bytes) {
        return new DPT10Value(bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1 || args.length == 2;
    }

    @Override
    protected DPT10Value parse(final String[] args) {
        final var dayOfWeek = this.findByEnumConstant(args, DayOfWeek.class);
        final var time = this.findByPattern(args, Pattern.compile("^[0-9]{2}:[0-9]{2}(:[0-9]{2})?$"), LocalTime::parse);

        return new DPT10Value(dayOfWeek, time);
    }

    public DPT10Value of(final @Nullable DayOfWeek dayOfWeek, final LocalTime time) {
        return new DPT10Value(dayOfWeek, time);
    }

    public byte[] toByteArray(final @Nullable DayOfWeek dayOfWeek, final LocalTime time) {
        return DPT10Value.toByteArray(dayOfWeek, time);
    }
}
