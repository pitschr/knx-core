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
 * Test {@link DPT14Value}
 *
 * @author PITSCHR
 */
public final class DPT14ValueTest {
    /**
     * Test {@link DPT14Value}
     */
    @Test
    public void test() {
        this.assertValue(DPT14.ANGLE_DEGREE, new byte[]{0x53, 0x38, 0x67, 0x44}, 7.9200649216E11);
        this.assertValue(DPT14.ANGLE_DEGREE, new byte[]{(byte) 0xC7, (byte) 0x7F, (byte) 0x9A, (byte) 0xED}, -65434.92578125);
    }

    /**
     * Test {@link DPT14Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPT14Value(DPT14.ANGLE_DEGREE, new byte[0])).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertValue(final DPT14 dpt, final byte[] bytes, final double floatingValue) {
        DPT14Value dptValue = new DPT14Value(dpt, floatingValue);
        DPT14Value dptValueByByte = new DPT14Value(dpt, bytes);

        // instance methods
        assertThat(dptValue.getFloatingValue()).isEqualTo(floatingValue);
        assertThat(dptValue.toByteArray()).containsExactly(bytes);

        // class methods
        assertThat(DPT14Value.toFloatingValue(bytes)).isEqualTo(floatingValue);
        assertThat(DPT14Value.toByteArray(floatingValue)).containsExactly(bytes);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT14Value(DPT14.ACTIVITY, floatingValue));
        assertThat(dptValue).isNotEqualTo(new DPT14Value(dpt, floatingValue + 0.1));

        // toString
        String toString = String.format("DPT14Value{dpt=%s, floatingValue=%s, byteArray=%s}", dpt, floatingValue,
                ByteFormatter.formatHexAsString(bytes));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
