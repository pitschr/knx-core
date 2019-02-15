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

import li.pitschmann.knx.link.datapoint.DPT12;
import li.pitschmann.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT12Value}
 *
 * @author PITSCHR
 */
public final class DPT12ValueTest {
    /**
     * Test {@link DPT12Value}
     */
    @Test
    public void test() {
        this.assertValue(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, new byte[]{0x29, 0x31, 0x47, 0x58}, 691095384L);
        this.assertValue(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, new byte[]{(byte) 0xF4, (byte) 0xAB, (byte) 0xC9, (byte) 0xD4}, 4104899028L);
    }

    /**
     * Test {@link DPT12Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPT12Value(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, new byte[0])).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertValue(final DPT12 dpt, final byte[] bytes, final long unsignedValue) {
        DPT12Value dptValue = new DPT12Value(dpt, unsignedValue);
        DPT12Value dptValueByByte = new DPT12Value(dpt, bytes);

        // instance methods
        assertThat(dptValue.getUnsignedValue()).isEqualTo(unsignedValue);
        assertThat(dptValue.toByteArray()).containsExactly(bytes);

        // class methods
        assertThat(DPT12Value.toByteArray(unsignedValue)).containsExactly(bytes);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT12Value(DPT12.VOLUME_L, unsignedValue));
        assertThat(dptValue).isNotEqualTo(new DPT12Value(dpt, unsignedValue + 1));

        // toString
        String toString = String.format("DPT12Value{dpt=%s, unsignedValue=%s, byteArray=%s}", dpt, unsignedValue,
                ByteFormatter.formatHexAsString(bytes));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
