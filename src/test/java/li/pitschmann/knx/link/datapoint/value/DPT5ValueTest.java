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
 * Test {@link DPT5Value}
 *
 * @author PITSCHR
 */
public final class DPT5ValueTest {
    /**
     * Test {@link DPT5Value}
     */
    @Test
    public void test() {
        this.assertValue(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, (byte) 0x29, 41, 41);
        this.assertValue(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, (byte) 0x85, 133, 133);

        this.assertValue(DPT5.ANGLE, (byte) 0x29, 41, DPT5.ANGLE.getCalcuationFunction().apply(41));
        this.assertValue(DPT5.SCALING, (byte) 0x2D, 45, DPT5.SCALING.getCalcuationFunction().apply(45));
    }

    private void assertValue(final DPT5 dpt, final byte b, final int rawUnsignedValue, final double unsignedValue) {
        DPT5Value dptValue = new DPT5Value(dpt, rawUnsignedValue);
        DPT5Value dptValueByByte = new DPT5Value(dpt, b);

        // instance methods
        assertThat(dptValue.getUnsignedValue()).isEqualTo(unsignedValue);
        assertThat(dptValue.getRawUnsignedValue()).isEqualTo(rawUnsignedValue);
        assertThat(dptValue.toByteArray()).containsExactly(b);

        // class methods
        assertThat(DPT5Value.toByteArray(rawUnsignedValue)).containsExactly(b);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT5Value(DPT5.PERCENT_U8, rawUnsignedValue));
        assertThat(dptValue).isNotEqualTo(new DPT5Value(dpt, rawUnsignedValue + 1));

        // toString
        String toString = String.format("DPT5Value{dpt=%s, unsignedValue=%s, rawUnsignedValue=%s, byteArray=%s}", dpt, unsignedValue,
                rawUnsignedValue, ByteFormatter.formatHex(b));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
