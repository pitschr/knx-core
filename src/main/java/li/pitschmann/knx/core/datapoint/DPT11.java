/*
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

import li.pitschmann.knx.core.datapoint.value.DPT11Value;
import li.pitschmann.knx.core.utils.Preconditions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Data Point Type 11 for 'Date' (3 Octets)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | 0   0   0   (Day)             |
 * Encoding    |             U   U   U   U   U |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | 0   0   0   0   (Month)       |
 *             |                 U   U   U   U |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | 0   (Year)                    |
 *             |     U   U   U   U   U   U   U |
 *             +---+---+---+---+---+---+---+---+
 * Format:     3 octets (r<sub>3</sub> U<sub>5</sub> r<sub>4</sub> U<sub>4</sub> r<sub>1</sub> U<sub>7</sub>)
 * Encoding:   Day   = [1 .. 31]
 *             Month = [1 .. 12]
 *             Year  = [0 .. 99]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT11 extends BaseDataPointType<DPT11Value> {
    /**
     * <strong>11.001</strong> Date
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   (Day)             |
     * Encoding    |             U   U   U   U   U |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | 0   0   0   0   (Month)       |
     *             |                 U   U   U   U |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | 0   (Year)                    |
     *             |     U   U   U   U   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     3 octets (r<sub>3</sub> U<sub>5</sub> r<sub>4</sub> U<sub>4</sub> r<sub>1</sub> U<sub>7</sub>)
     * Encoding:   Day   = [1 .. 31]
     *             Month = [1 .. 12]
     *             Year  = [0 .. 99]
     * </pre>
     * <p>
     * This format covers the range 1990 to 2089. The following interpretation shall be carried out by devices receiving
     * the Data Point Type 11.001 and carrying out calculations on the basis of the entire 3rd octet:
     * <p>
     * - If Octet 3 contains value ≥ 90 : interpret as 20th century<br>
     * - If Octet 3 contains value < 90: interpret as 21st century<br>
     */
    @DataPoint({"11.001", "dpt-11", "dpst-11-1"})
    public static final DPT11 DATE = new DPT11("Date");

    /**
     * Constructor for {@link DPT11}
     *
     * @param desc description for {@link DPT11}
     */
    private DPT11(final String desc) {
        super(desc);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 3;
    }

    @Override
    protected DPT11Value parse(final byte[] bytes) {
        return new DPT11Value(bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT11Value parse(final String[] args) {
        final var date = findByPattern(args, Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"), LocalDate::parse);

        Preconditions.checkArgument(date != null,
                "Date missing (supported format: 'yyyy-mm-dd'). Provided: {}", Arrays.toString(args));
        return of(LocalDate.parse(args[0]));
    }

    public DPT11Value of(final LocalDate date) {
        return new DPT11Value(date);
    }
}
