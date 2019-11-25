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

import li.pitschmann.knx.core.datapoint.DPT7;
import li.pitschmann.knx.core.utils.ByteFormatter;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        this.assertValue(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, new byte[]{0x29, 0x23}, 10531, 10531, "10531");
        this.assertValue(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, new byte[]{(byte) 0xC9, (byte) 0xD4}, 51668, 51668, "51668");

        this.assertValue(DPT7.TIME_PERIOD_10MS, new byte[]{0x58, (byte) 0xF3}, 22771, 227.71d, "227.71");
        this.assertValue(DPT7.TIME_PERIOD_100MS, new byte[]{(byte) 0xEA, 0x32}, 59954, 5995.4d, "5995.4");
    }

    /**
     * Test {@link DPT7Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPT7Value(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, new byte[0])).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertValue(final DPT7 dpt, final byte[] bytes, final int rawUnsignedValue, final double unsignedValue, final String text) {
        final var dptValue = new DPT7Value(dpt, rawUnsignedValue);
        final var dptValueByByte = new DPT7Value(dpt, bytes);

        // instance methods
        assertThat(dptValue.getUnsignedValue()).isCloseTo(unsignedValue, Offset.offset(0.000001));
        assertThat(dptValue.getRawUnsignedValue()).isEqualTo(rawUnsignedValue);
        assertThat(dptValue.toByteArray()).containsExactly(bytes);
        assertThat(dptValue.toText()).isEqualTo(text);

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
        final var toString = String.format("DPT7Value{dpt=%s, unsignedValue=%s, rawUnsignedValue=%s, byteArray=%s}", dpt, unsignedValue,
                rawUnsignedValue, ByteFormatter.formatHexAsString(bytes));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
