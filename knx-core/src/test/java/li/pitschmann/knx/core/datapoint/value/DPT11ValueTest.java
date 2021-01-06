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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT11Value}
 *
 * @author PITSCHR
 */
class DPT11ValueTest {

    @Test
    @DisplayName("#(byte[]) with: 1990-01-01")
    void testBytes_1990_01_01() {
        final var value = new DPT11Value(new byte[]{0x01, 0x01, 0x5A});
        assertThat(value.getDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(value.toByteArray()).containsExactly(0x01, 0x01, 0x5A);

        assertThat(value.toText()).isEqualTo("1990-01-01");
    }

    @Test
    @DisplayName("#(byte[]) with: 1991-03-09")
    void testBytes_1991_03_09() {
        final var value = new DPT11Value(new byte[]{0x09, 0x03, 0x5B});
        assertThat(value.getDate()).isEqualTo(LocalDate.of(1991, 3, 9));
        assertThat(value.toByteArray()).containsExactly(0x09, 0x03, 0x5B);

        assertThat(value.toText()).isEqualTo("1991-03-09");
    }

    @Test
    @DisplayName("#(byte[]) with: 1999-06-18")
    void testBytes_1999_06_09() {
        final var value = new DPT11Value(new byte[]{0x12, 0x06, 0x63});
        assertThat(value.getDate()).isEqualTo(LocalDate.of(1999, 6, 18));
        assertThat(value.toByteArray()).containsExactly(0x12, 0x06, 0x63);

        assertThat(value.toText()).isEqualTo("1999-06-18");
    }

    @Test
    @DisplayName("#(byte[]) with: 2050-09-27")
    void testBytes_2050_09_27() {
        final var value = new DPT11Value(new byte[]{0x1B, 0x09, 0x32});
        assertThat(value.getDate()).isEqualTo(LocalDate.of(2050, 9, 27));
        assertThat(value.toByteArray()).containsExactly(0x1B, 0x09, 0x32);

        assertThat(value.toText()).isEqualTo("2050-09-27");
    }

    @Test
    @DisplayName("#(byte[]) with: 2089-12-30")
    void testBytes_2089_12_30() {
        final var value = new DPT11Value(new byte[]{0x1E, 0x0C, 0x59});
        assertThat(value.getDate()).isEqualTo(LocalDate.of(2089, 12, 30));
        assertThat(value.toByteArray()).containsExactly(0x1E, 0x0C, 0x59);

        assertThat(value.toText()).isEqualTo("2089-12-30");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var value = new DPT11Value(LocalDate.of(1997, 12, 5));
        assertThat(value).hasToString(
                "DPT11Value{dpt=11.001, date=1997-12-05, byteArray=0x05 0C 61}"
        );

        final var value2 = new DPT11Value(LocalDate.of(2045, 4, 23));
        assertThat(value2).hasToString(
                "DPT11Value{dpt=11.001, date=2045-04-23, byteArray=0x17 04 2D}"
        );
    }

    @Test
    @DisplayName("#(byte[]) with invalid byte length")
    void testBytesOutOfRange() {
        // expected: 3 bytes, provided 13 bytes
        assertThatThrownBy(() -> new DPT11Value(new byte[13]))
                .isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT11Value(LocalDate.of(2020, 1, 2));
        final var valueBytes = new DPT11Value(new byte[] {0b0000_0010, 0b0000_0001, 0b0001_0100});

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(valueBytes).isEqualTo(value);
        assertThat(valueBytes).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT11Value(LocalDate.of(1999, 1, 2)));
        assertThat(value).isNotEqualTo(new DPT11Value(LocalDate.of(2020, 9, 2)));
        assertThat(value).isNotEqualTo(new DPT11Value(LocalDate.of(2020, 1, 9)));
    }
}
