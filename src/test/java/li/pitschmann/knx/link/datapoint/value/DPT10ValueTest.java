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

import li.pitschmann.knx.link.datapoint.*;
import li.pitschmann.utils.*;
import org.junit.jupiter.api.*;

import java.time.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test {@link DPT10Value}
 *
 * @author PITSCHR
 */
public final class DPT10ValueTest {
    /**
     * Test {@link DPT10Value}
     */
    @Test
    public void test() {
        // no day, 00:00:00
        this.assertValue(new byte[]{0x00, 0x00, 0x00}, null, LocalTime.of(0, 0, 0));
        // monday, 6:15:20
        // monday => 001. ....
        // hour 6 => ...0 0110
        this.assertValue(new byte[]{(byte) 0x26, (byte) 0x0f, (byte) 0x14}, DayOfWeek.MONDAY, LocalTime.of(6, 15, 20));
        // wednesday, 12:30:45
        // wednesday => 011. ....
        // hour 12 ===> ...0 1100
        this.assertValue(new byte[]{(byte) 0x6c, (byte) 0x1e, (byte) 0x2d}, DayOfWeek.WEDNESDAY, LocalTime.of(12, 30, 45));
        // sunday, 23:59:59
        // sunday ==> 111. ....
        // hour 23 => ...1 0111
        this.assertValue(new byte[]{(byte) 0xf7, (byte) 0x3b, (byte) 0x3b}, DayOfWeek.SUNDAY, LocalTime.of(23, 59, 59));
    }

    /**
     * Test {@link DPT10Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPT10Value(new byte[0])).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertValue(final byte[] bytes, final DayOfWeek dayOfWeek, final LocalTime time) {
        DPT10Value dptValue = new DPT10Value(dayOfWeek, time);
        DPT10Value dptValueByByte = new DPT10Value(bytes);

        // instance methods
        assertThat(dptValue.getDayOfWeek()).isEqualTo(dayOfWeek);
        assertThat(dptValue.getTime()).isEqualTo(time);
        assertThat(dptValue.toByteArray()).containsExactly(bytes);

        // class methods
        assertThat(DPT10Value.toByteArray(dayOfWeek, time)).containsExactly(bytes);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        DayOfWeek anotherDayOfWeek = dayOfWeek == null ? DayOfWeek.MONDAY : dayOfWeek.plus(1);
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT10Value(anotherDayOfWeek, time));
        assertThat(dptValue).isNotEqualTo(new DPT10Value(dayOfWeek, time.plusMinutes(1)));
        if (dayOfWeek != null) {
            // additional check when day of week is not null
            assertThat(dptValue).isNotEqualTo(new DPT10Value(null, time));
            assertThat(dptValue).isNotEqualTo(new DPT10Value(null, time.plusMinutes(1)));
        }

        // toString
        String toString = String.format("DPT10Value{dpt=%s, dayOfWeek=%s, time=%s, byteArray=%s}", DPT10.TIME_OF_DAY, dayOfWeek, time,
                ByteFormatter.formatHexAsString(bytes));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
