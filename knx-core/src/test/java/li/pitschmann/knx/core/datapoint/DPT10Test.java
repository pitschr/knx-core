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

import li.pitschmann.knx.core.datapoint.value.DPT10Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT10}
 *
 * @author PITSCHR
 */
public class DPT10Test extends AbstractDataPointTypeTest<DPT10, DPT10Value> {
    private static final DPT10 DPT_TIME = DPT10.TIME_OF_DAY;

    @Override
    @Test
    public void testIdAndDescription() {
        assertThat(DPT_TIME.getId()).isEqualTo("10.001");
        assertThat(DPT_TIME.getDescription()).isEqualTo("Time Of Day");
    }

    @Override
    @Test
    public void testCompatibility() {
        // failures
        assertThatThrownBy(() -> DPT_TIME.toValue(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT_TIME.toValue(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT_TIME.toValue(new byte[4])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT_TIME.toValue("foo")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT_TIME.toValue("foo", "bar")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT_TIME.toValue("foo", "bar", "far")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT_TIME.toValue("99:99:99")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT_TIME.toValue("FRIDAY", "99:99:99"))
                .isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(DPT_TIME.toValue((byte) 0x00, (byte) 0x00, (byte) 0x00)).isInstanceOf(DPT10Value.class);
        assertThat(DPT_TIME.toValue((byte) 0xef, (byte) 0x3b, (byte) 0x3b)).isInstanceOf(DPT10Value.class);
        assertThat(DPT_TIME.toValue(null, LocalTime.now())).isInstanceOf(DPT10Value.class);
        assertThat(DPT_TIME.toValue(DayOfWeek.MONDAY, LocalTime.now())).isInstanceOf(DPT10Value.class);
        assertThat(DPT_TIME.toValue("14:56:30")).isInstanceOf(DPT10Value.class);
        assertThat(DPT_TIME.toValue("FrIDaY", "14:56:30")).isInstanceOf(DPT10Value.class);
    }

    @Override
    @Test
    public void testOf() {
        // no day, 00:00:00
        this.assertDPT(new byte[]{0x00, 0x00, 0x00}, null, LocalTime.of(0, 0, 0));
        // monday, 6:15:20
        // monday => 001. ....
        // hour 6 => ...0 0110
        this.assertDPT(new byte[]{(byte) 0x26, (byte) 0x0f, (byte) 0x14}, DayOfWeek.MONDAY, LocalTime.of(6, 15, 20));
        // wednesday, 12:30:45
        // wednesday => 011. ....
        // hour 12 ===> ...0 1100
        this.assertDPT(new byte[]{(byte) 0x6c, (byte) 0x1e, (byte) 0x2d}, DayOfWeek.WEDNESDAY, LocalTime.of(12, 30, 45));
        // sunday, 23:59:59
        // sunday ==> 111. ....
        // hour 23 => ...1 0111
        this.assertDPT(new byte[]{(byte) 0xf7, (byte) 0x3b, (byte) 0x3b}, DayOfWeek.SUNDAY, LocalTime.of(23, 59, 59));
    }

    /**
     * Asserts the DPT for given arguments {@code bValueArray}, {@code dayOfWeek} and {@code localTime}
     *
     * @param bValueArray byte array with values
     * @param dayOfWeek   day of week
     * @param localTime   local time
     */
    private void assertDPT(final byte[] bValueArray, final DayOfWeek dayOfWeek, final LocalTime localTime) {
        final var dpt = DPT_TIME;
        final var dptValue = dpt.toValue(dayOfWeek, localTime);

        // assert base DPT
        this.assertBaseDPT(dpt, bValueArray, dptValue);

        // assert specific DPT10
        if (dayOfWeek == null) {
            assertThat(dpt.toValue(localTime.toString())).isEqualTo(dptValue);
        } else {
            assertThat(dpt.toValue(dayOfWeek.toString(), localTime.toString())).isEqualTo(dptValue);
        }
        assertThat(dpt.toByteArray(dayOfWeek, localTime)).containsExactly(bValueArray);
    }
}
