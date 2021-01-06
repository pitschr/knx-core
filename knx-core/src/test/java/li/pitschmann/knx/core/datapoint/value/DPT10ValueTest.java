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

import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT10Value}
 *
 * @author PITSCHR
 */
class DPT10ValueTest {

    @Test
    @DisplayName("#(byte[]) with: No Day, 00:00:00")
    void testBytes_NoDay_00_00_00() {
        final var value = new DPT10Value(new byte[]{0x00, 0x00, 0x00});
        assertThat(value.getDayOfWeek()).isNull();
        assertThat(value.getTime()).isEqualTo(LocalTime.of(0, 0, 0));
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("00:00:00");
    }

    @Test
    @DisplayName("#(byte[]) with: Monday, 06:15:20")
    void testBytes_Monday_06_15_20() {
        final var value = new DPT10Value(new byte[]{0x26, 0x0F, 0x14});
        assertThat(value.getDayOfWeek()).isSameAs(DayOfWeek.MONDAY);
        assertThat(value.getTime()).isEqualTo(LocalTime.of(6, 15, 20));
        assertThat(value.toByteArray()).containsExactly(0x26, 0x0F, 0x14);

        assertThat(value.toText()).isEqualTo("Monday, 06:15:20");
    }

    @Test
    @DisplayName("#(byte[]) with: Wednesday, 12:30:45")
    void testBytes_Wednesday_06_15_20() {
        final var value = new DPT10Value(new byte[]{0x6C, 0x1E, 0x2D});
        assertThat(value.getDayOfWeek()).isSameAs(DayOfWeek.WEDNESDAY);
        assertThat(value.getTime()).isEqualTo(LocalTime.of(12, 30, 45));
        assertThat(value.toByteArray()).containsExactly(0x6C, 0x1E, 0x2D);

        assertThat(value.toText()).isEqualTo("Wednesday, 12:30:45");
    }

    @Test
    @DisplayName("#(byte[]) with: Sunday, 23:59:59")
    void testBytes_Sunday_23_59_59() {
        final var value = new DPT10Value(new byte[]{(byte) 0xF7, 0x3B, 0x3B});
        assertThat(value.getDayOfWeek()).isSameAs(DayOfWeek.SUNDAY);
        assertThat(value.getTime()).isEqualTo(LocalTime.of(23, 59, 59));
        assertThat(value.toByteArray()).containsExactly(0xF7, 0x3B, 0x3B);

        assertThat(value.toText()).isEqualTo("Sunday, 23:59:59");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueNoWeekDay = new DPT10Value(null, LocalTime.of(14, 47, 31));
        assertThat(valueNoWeekDay).hasToString(
                "DPT10Value{dpt=10.001, dayOfWeek=null, time=14:47:31, byteArray=0x0E 2F 1F}"
        );

        final var valueWithWeekDay = new DPT10Value(DayOfWeek.THURSDAY, LocalTime.of(3, 12, 57));
        assertThat(valueWithWeekDay).hasToString(
                "DPT10Value{dpt=10.001, dayOfWeek=THURSDAY, time=03:12:57, byteArray=0x83 0C 39}"
        );
    }

    @Test
    @DisplayName("#(byte[]) with invalid byte length")
    void testBytesOutOfRange() {
        // expected: 3 bytes, provided 13 bytes
        assertThatThrownBy(() -> new DPT10Value(new byte[13]))
                .isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT10Value(DayOfWeek.MONDAY, LocalTime.of(1, 2, 3));
        final var value2 = new DPT10Value(DayOfWeek.MONDAY, LocalTime.of(1, 2, 3));

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(value2).isEqualTo(value);
        assertThat(value2).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT10Value(DayOfWeek.MONDAY, LocalTime.of(9, 2, 3)));
        assertThat(value).isNotEqualTo(new DPT10Value(DayOfWeek.MONDAY, LocalTime.of(1, 9, 3)));
        assertThat(value).isNotEqualTo(new DPT10Value(DayOfWeek.MONDAY, LocalTime.of(1, 2, 9)));
        assertThat(value).isNotEqualTo(new DPT10Value(DayOfWeek.TUESDAY, LocalTime.of(1, 2, 3)));
        assertThat(value).isNotEqualTo(new DPT10Value(null, LocalTime.of(1, 2, 3)));
    }
}
