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

import li.pitschmann.knx.core.datapoint.DPT11;
import li.pitschmann.knx.core.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT11Value}
 *
 * @author PITSCHR
 */
public final class DPT11ValueTest {
    /**
     * Test {@link DPT11Value}
     */
    @Test
    public void test() {
        // day = 1, month = 1, year = 90 (=1990)
        this.assertValue(new byte[]{0x01, 0x01, 0x5a}, LocalDate.of(1990, 1, 1), "1990-01-01");
        // day = 9, month = 3, year = 91 (=1991)
        this.assertValue(new byte[]{0x09, 0x03, 0x5b}, LocalDate.of(1991, 3, 9), "1991-03-09");
        // day = 18, month = 6, year = 99 (=1999)
        this.assertValue(new byte[]{0x12, 0x06, 0x63}, LocalDate.of(1999, 6, 18), "1999-06-18");
        // day = 27, month = 9, year = 50 (=2050)
        this.assertValue(new byte[]{0x1b, 0x09, 0x32}, LocalDate.of(2050, 9, 27), "2050-09-27");
        // day = 30, month = 12, year = 89 (=2089)
        this.assertValue(new byte[]{0x1e, 0x0c, 0x59}, LocalDate.of(2089, 12, 30), "2089-12-30");
    }

    /**
     * Test {@link DPT11Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPT11Value(new byte[0])).isInstanceOf(IllegalArgumentException.class);

        // invalid year (only 1990..2089 should be accepted)
        assertThatThrownBy(() -> new DPT11Value(LocalDate.of(1989, 12, 31))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new DPT11Value(LocalDate.of(2090, 1, 1))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DPT11Value.toByteArray(LocalDate.of(1989, 12, 31))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DPT11Value.toByteArray(LocalDate.of(2090, 1, 1))).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertValue(final byte[] bytes, final LocalDate date, final String text) {
        final var dptValue = new DPT11Value(date);
        final var dptValueByByte = new DPT11Value(bytes);

        // instance methods
        assertThat(dptValue.getDate()).isEqualTo(date);
        assertThat(dptValue.toByteArray()).containsExactly(bytes);
        assertThat(dptValue.toText()).isEqualTo(text);

        // class methods
        assertThat(DPT11Value.toByteArray(date)).containsExactly(bytes);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT11Value(date.plusDays(1)));

        // toString
        final var toString = String.format("DPT11Value{dpt=%s, date=%s, byteArray=%s}", DPT11.DATE, date, ByteFormatter.formatHexAsString(bytes));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}