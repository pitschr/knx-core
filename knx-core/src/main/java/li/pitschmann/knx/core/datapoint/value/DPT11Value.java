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

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.DPT11;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT11} (11.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | 0   0   0   (Day)             |
 * Encoding     |             U   U   U   U   U |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | 0   0   0   0   (Month)       |
 *              |                 U   U   U   U |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | 0   (Year)                    |
 *              |     U   U   U   U   U   U   U |
 *              +---+---+---+---+---+---+---+---+
 * Format:     3 octets (r<sub>3</sub> U<sub>5</sub> r<sub>4</sub> U<sub>4</sub> r<sub>1</sub> U<sub>7</sub>)
 * Encoding:   Day   = [1 .. 31]
 *             Month = [1 .. 12]
 *             Year  = [0 .. 99]
 * </pre>
 * <p>
 * This format covers the range 1990 to 2089. The following interpretation shall be carried out by devices receiving the
 * Data Point Type 11.001 and carrying out calculations on the basis of the entire 3rd octet:
 * <p>
 * - If Octet 3 contains value ≥ 90 : interpret as 20th century<br>
 * - If Octet 3 contains value < 90: interpret as 21st century<br>
 *
 * @author PITSCHR
 */
public final class DPT11Value extends AbstractDataPointValue<DPT11> {
    private static final Logger log = LoggerFactory.getLogger(DPT11Value.class);
    private final LocalDate date;

    public DPT11Value(final byte[] bytes) {
        this(toLocalDate(bytes));
    }

    public DPT11Value(final LocalDate date) {
        super(DPT11.DATE);
        this.date = validateLocalDate(date);
    }

    /**
     * Validates if the year of given {@link LocalDate} is within
     * {@code 1990} and {@code 2089}
     *
     * @param date the local date to be checked
     * @return the local date, if validation was successful
     * @throws NullPointerException     if local date is not provided
     * @throws IllegalArgumentException if the local date is not within the range
     */
    private static LocalDate validateLocalDate(final LocalDate date) {
        Preconditions.checkNonNull(date, "date is null");
        Preconditions.checkArgument(date.getYear() >= 1990 && date.getYear() <= 2089,
                "Year must be between '1990..2089'. Got: {}", date.getYear());
        return date;
    }

    /**
     * Converts byte array to {@link LocalDate}
     *
     * @param bytes byte array to be converted
     * @return {@link LocalDate}
     * @throws KnxNumberOutOfRangeException if the length of bytes is not expected
     */
    private static LocalDate toLocalDate(final byte[] bytes) {
        if (bytes.length != 3) {
            throw new KnxNumberOutOfRangeException("bytes", 3, 3, bytes.length, bytes);
        }

        // day
        final var day = Bytes.toUnsignedInt(bytes[0]);

        // month
        final var month = Bytes.toUnsignedInt(bytes[1]);

        // year (two digits only)
        // If year contains value ≥ 90 : interpret as 20th century
        // If year contains value < 90: interpret as 21st century
        final var year = Bytes.toUnsignedInt(bytes[2]);
        final var fullYear = (year < 90 ? 2000 : 1900) + year;

        final var date = LocalDate.of(fullYear, month, day);
        log.debug("Date of '{}': {}", ByteFormatter.formatHex(bytes), date);
        return date;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public byte[] toByteArray() {
        // byte 0: day
        final var dayAsByte = (byte) date.getDayOfMonth();

        // byte 1: month
        final var monthAsByte = (byte) date.getMonthValue();

        // byte 2: year (get last two digits only)
        final var yearAsByte = (byte) (date.getYear() % 100);

        final var bytes = new byte[]{dayAsByte, monthAsByte, yearAsByte};
        if (log.isDebugEnabled()) {
            log.debug("Bytes of '{}': {}", date, ByteFormatter.formatHexAsString(bytes));
        }
        return bytes;
    }

    @Override
    public String toText() {
        return getDate().format(DateTimeFormatter.ISO_DATE);
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("date", date)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT11Value) {
            final var other = (DPT11Value) obj;
            return Objects.equals(this.date, other.date);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), date);
    }
}
