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
 * Test {@link DPT9Value}
 *
 * @author PITSCHR
 */
public final class DPT9ValueTest {
    /**
     * Test {@link DPT9Value}
     */
    @Test
    public void test() {
        this.assertValue(DPT9.TEMPERATURE, new byte[]{0x67, 0x44}, 76185.6);
        this.assertValue(DPT9.TIME_DIFFERENCE_SECONDS, new byte[]{0x0C, 0x5C}, 22.32);
        this.assertValue(DPT9.VOLTAGE, new byte[]{(byte) 0xE1, (byte) 0xA5}, -66641.92);
    }

    /**
     * Test {@link DPT9Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPT9Value(DPT9.AIR_FLOW, new byte[0])).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertValue(final DPT9 dpt, final byte[] bytes, final double floatingValue) {
        DPT9Value dptValue = new DPT9Value(dpt, floatingValue);
        DPT9Value dptValueByByte = new DPT9Value(dpt, bytes);

        // instance methods
        assertThat(dptValue.getFloatingValue()).isEqualTo(floatingValue);
        assertThat(dptValue.toByteArray()).containsExactly(bytes);

        // class methods
        assertThat(DPT9Value.toFloatingValue(bytes)).isEqualTo(floatingValue);
        assertThat(DPT9Value.toByteArray(floatingValue)).containsExactly(bytes);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT9Value(DPT9.AIR_FLOW, floatingValue));
        assertThat(dptValue).isNotEqualTo(new DPT9Value(dpt, floatingValue + 0.1));

        // toString
        String toString = String.format("DPT9Value{dpt=%s, floatingValue=%s, byteArray=%s}", dpt, floatingValue,
                ByteFormatter.formatHexAsString(bytes));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
