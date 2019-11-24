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

import li.pitschmann.knx.core.datapoint.value.DPT19Value;
import li.pitschmann.knx.core.datapoint.value.DPT19Value.Flags;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT19}
 *
 * @author PITSCHR
 */
public class DPT19Test extends AbstractDataPointTypeTest<DPT19, DPT19Value> {
    private static final DPT19 DPT_DATE_TIME = DPT19.DATE_TIME;

    @Override
    @Test
    public void testIdAndDescription() {
        assertThat(DPT_DATE_TIME.getId()).isEqualTo("19.001");
        assertThat(DPT_DATE_TIME.getDescription()).isEqualTo("Date & Time");
    }

    @Override
    @Test
    public void testCompatibility() {
        // failures
        assertThatThrownBy(() -> DPT_DATE_TIME.toValue(new byte[7])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT_DATE_TIME.toValue(new byte[9])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT_DATE_TIME.toValue(new String[0])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT_DATE_TIME.toValue("a", "b")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT_DATE_TIME.toValue("a", "b", "c", "d", "e"))
                .isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT_DATE_TIME.toValue("9999-99-99", "99:99:99"))
                .isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(DPT_DATE_TIME.toValue((byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00)).isInstanceOf(DPT19Value.class);
        assertThat(DPT_DATE_TIME.toValue((byte) 0xff, (byte) 0x0c, (byte) 0x1f, (byte) 0xf7, (byte) 0x3b, (byte) 0x3b, (byte) 0xff, (byte) 0x80))
                .isInstanceOf(DPT19Value.class);

        assertThat(DPT_DATE_TIME.toValue(null, LocalDate.now(), LocalTime.now(), null)).isInstanceOf(DPT19Value.class);
        assertThat(DPT_DATE_TIME.toValue(DayOfWeek.WEDNESDAY, LocalDate.now(), LocalTime.now(),
                new Flags(true, true, true, true, false, false, false, false, false))).isInstanceOf(DPT19Value.class);
        assertThat(DPT_DATE_TIME.toValue("2010-03-04", "14:56:30")).isInstanceOf(DPT19Value.class);
        assertThat(DPT_DATE_TIME.toValue("FrIDaY", "14:56:30", "2010-03-04")).isInstanceOf(DPT19Value.class);
        assertThat(DPT_DATE_TIME.toValue("FrIDaY", "14:56:30", "2010-03-04", "0x33 44")).isInstanceOf(DPT19Value.class);
    }

    @Override
    @Test
    public void testOf() {
        // no day, 1900-01-01 00:00:00
        this.assertDPT(new byte[]{0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00}, null, LocalDate.of(1900, 1, 1), LocalTime.of(0, 0, 0),
                new Flags(false, false, false, false, false, false, false, false, false));
        // monday, 1950-02-03 6:15:20
        // monday => 001. ....
        // hour 6 => ...0 0110
        this.assertDPT(new byte[]{0x32, 0x02, 0x03, 0x26, 0x0f, 0x14, (byte) 0xAA, (byte) 0x80}, DayOfWeek.MONDAY, LocalDate.of(1950, 2, 3),
                LocalTime.of(6, 15, 20), new Flags(true, false, true, false, true, false, true, false, true));
        // wednesday, 2000-04-05 12:30:45
        // wednesday => 011. ....
        // hour 12 ===> ...0 1100
        this.assertDPT(new byte[]{0x64, 0x04, 0x05, 0x6c, 0x1e, 0x2d, 0x55, 0x00}, DayOfWeek.WEDNESDAY, LocalDate.of(2000, 4, 5),
                LocalTime.of(12, 30, 45), new Flags(false, true, false, true, false, true, false, true, false));
        // sunday, 2155-12-31 23:59:59
        // sunday ==> 111. ....
        // hour 23 => ...1 0111
        this.assertDPT(new byte[]{(byte) 0xff, 0x0c, 0x1f, (byte) 0xf7, 0x3b, 0x3b, (byte) 0xff, (byte) 0x80}, DayOfWeek.SUNDAY,
                LocalDate.of(2155, 12, 31), LocalTime.of(23, 59, 59), new Flags(true, true, true, true, true, true, true, true, true));
    }

    @Test
    @DisplayName("Test #toValue(..) and #toByteArray(..) without flags, no-flags")
    public void testToValueAndByteArrayWithNoFlags() {
        // reference value
        final var dayOfWeek = DayOfWeek.WEDNESDAY;
        final var date = LocalDate.of(2019, 10, 21);
        final var time = LocalTime.of(13, 45, 47);
        final var flags = new Flags(false, false, false, false, false, false, false, false, false);
        final var dptValue = DPT_DATE_TIME.toValue(dayOfWeek, date, time, flags);

        // verify
        assertThat(DPT_DATE_TIME.toValue(dayOfWeek, date, time, Flags.NO_FLAGS)).isEqualTo(dptValue);
        assertThat(DPT_DATE_TIME.toByteArray(dayOfWeek, date, time, Flags.NO_FLAGS)).isEqualTo(dptValue.toByteArray());

        assertThat(DPT_DATE_TIME.toValue(dayOfWeek, date, time)).isEqualTo(dptValue);
        assertThat(DPT_DATE_TIME.toByteArray(dayOfWeek, date, time)).isEqualTo(dptValue.toByteArray());
    }

    /**
     * Asserts the DPT for given arguments {@code bValueArray}, {@code dayOfWeek} and {@code localTime}
     *
     * @param bValueArray
     * @param dayOfWeek
     * @param date
     * @param time
     */
    private void assertDPT(final byte[] bValueArray, final DayOfWeek dayOfWeek, final LocalDate date, final LocalTime time, final Flags flags) {
        final var dpt = DPT_DATE_TIME;
        final var dptValue = dpt.toValue(dayOfWeek, date, time, flags);

        // assert base DPT
        this.assertBaseDPT(dpt, bValueArray, dptValue);
        // assert specific DPT19
        assertThat(dpt.toValue(new String[]{String.valueOf(dayOfWeek), String.valueOf(date), String.valueOf(time),
                ByteFormatter.formatHexAsString(flags.getAsBytes())})).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(dayOfWeek, date, time, flags)).containsExactly(bValueArray);
    }
}
