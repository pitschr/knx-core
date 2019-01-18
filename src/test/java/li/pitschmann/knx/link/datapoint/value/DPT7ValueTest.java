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

import static org.assertj.core.api.Assertions.*;

/**
 * Test {@link DPT7Value}
 *
 * @author PITSCHR
 */
public final class DPT7ValueTest {
    /**
     * Test {@link DPT7Value}
     */
    @Test
    public void test() {
        this.assertValue(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, new byte[]{0x29, 0x23}, 10531, 10531);
        this.assertValue(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, new byte[]{(byte) 0xC9, (byte) 0xD4}, 51668, 51668);

        this.assertValue(DPT7.TIME_PERIOD_10MS, new byte[]{0x58, (byte) 0xF3}, 22771, DPT7.TIME_PERIOD_10MS.getCalculationFunction().apply(22771));
        this.assertValue(DPT7.TIME_PERIOD_100MS, new byte[]{(byte) 0xEA, 0x32}, 59954,
                DPT7.TIME_PERIOD_100MS.getCalculationFunction().apply(59954));
    }

    /**
     * Test {@link DPT7Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPT7Value(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, new byte[0])).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertValue(final DPT7 dpt, final byte[] bytes, final int rawUnsignedValue, final double unsignedValue) {
        DPT7Value dptValue = new DPT7Value(dpt, rawUnsignedValue);
        DPT7Value dptValueByByte = new DPT7Value(dpt, bytes);

        // instance methods
        assertThat(dptValue.getUnsignedValue()).isEqualTo(unsignedValue);
        assertThat(dptValue.getRawUnsignedValue()).isEqualTo(rawUnsignedValue);
        assertThat(dptValue.toByteArray()).containsExactly(bytes);

        // class methods
        assertThat(DPT7Value.toByteArray(rawUnsignedValue)).containsExactly(bytes);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT7Value(DPT7.LENGTH_MM, rawUnsignedValue));
        assertThat(dptValue).isNotEqualTo(new DPT7Value(dpt, rawUnsignedValue + 1));

        // toString
        String toString = String.format("DPT7Value{dpt=%s, unsignedValue=%s, rawUnsignedValue=%s, byteArray=%s}", dpt, unsignedValue,
                rawUnsignedValue, ByteFormatter.formatHexAsString(bytes));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
