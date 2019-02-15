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

package li.pitschmann.knx.link.datapoint;

import li.pitschmann.knx.link.datapoint.annotation.KnxDataPointType;
import li.pitschmann.knx.link.datapoint.value.DPT11Value;

import java.time.LocalDate;

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
public final class DPT11 extends AbstractDataPointType<DPT11Value> {
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
    @KnxDataPointType(id = "11.001", description = "Date")
    public static final DPT11 DATE = new DPT11("11.001", "Date");

    /**
     * Constructor for {@link DPT11}
     *
     * @param id
     * @param desc
     */
    private DPT11(final String id, final String desc) {
        super(id, desc);
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
        return new DPT11Value(LocalDate.parse(args[0]));
    }

    public DPT11Value toValue(final LocalDate date) {
        return new DPT11Value(date);
    }

    public byte[] toByteArray(final LocalDate date) {
        return DPT11Value.toByteArray(date);
    }
}
