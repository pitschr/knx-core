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

import li.pitschmann.knx.core.datapoint.DPT8;
import li.pitschmann.knx.core.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT8Value}
 *
 * @author PITSCHR
 */
public final class DPT8ValueTest {
    /**
     * Test {@link DPT8Value}
     */
    @Test
    public void test() {
        this.assertValue(DPT8.VALUE_2_OCTET_COUNT, new byte[]{0x6B, (byte) 0xA2}, 27554, 27554, "27554");
        this.assertValue(DPT8.VALUE_2_OCTET_COUNT, new byte[]{(byte) 0xE1, (byte) 0xA5}, -7771, -7771, "-7771");

        this.assertValue(DPT8.DELTA_TIME_10MS, new byte[]{0x1C, (byte) 0x9A}, 7322, 73.22, "73.22");
        this.assertValue(DPT8.DELTA_TIME_100MS, new byte[]{0x1C, (byte) 0x9A}, 7322, 732.2, "732.2");
    }

    /**
     * Test {@link DPT8Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPT8Value(DPT8.VALUE_2_OCTET_COUNT, new byte[0])).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertValue(final DPT8 dpt, final byte[] bytes, final int rawSignedValue, final double signedValue, final String text) {
        final var dptValue = new DPT8Value(dpt, rawSignedValue);
        final var dptValueByByte = new DPT8Value(dpt, bytes);

        // instance methods
        assertThat(dptValue.getRawSignedValue()).isEqualTo(rawSignedValue);
        assertThat(dptValue.getSignedValue()).isEqualTo(signedValue);
        assertThat(dptValue.toByteArray()).containsExactly(bytes);
        assertThat(dptValue.toText()).isEqualTo(text);

        // class methods
        assertThat(DPT8Value.toByteArray(rawSignedValue)).containsExactly(bytes);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT8Value(DPT8.PERCENT, rawSignedValue));
        assertThat(dptValue).isNotEqualTo(new DPT8Value(dpt, rawSignedValue + 1));

        // toString
        final var toString = String.format("DPT8Value{dpt=%s, signedValue=%s, rawSignedValue=%s, byteArray=%s}", dpt, signedValue, rawSignedValue,
                ByteFormatter.formatHexAsString(bytes));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
