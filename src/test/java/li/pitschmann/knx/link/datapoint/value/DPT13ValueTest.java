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

import li.pitschmann.knx.link.datapoint.DPT13;
import li.pitschmann.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT13Value}
 *
 * @author PITSCHR
 */
public final class DPT13ValueTest {
    /**
     * Test {@link DPT13Value}
     */
    @Test
    public void test() {
        this.assertValue(DPT13.VALUE_4_OCTET_COUNT, new byte[]{0x68, 0x09, (byte) 0xC6, (byte) 0x9E}, 1745471134, 1745471134);
        this.assertValue(DPT13.VALUE_4_OCTET_COUNT, new byte[]{(byte) 0xFB, 0x02, 0x54, (byte) 0xB7}, -83733321, -83733321);

        this.assertValue(DPT13.FLOW_RATE, new byte[]{0x36, 0x61, 0x4A, 0x4E}, 912345678, DPT13.FLOW_RATE.getCalculationFunction().apply(912345678));
        this.assertValue(DPT13.FLOW_RATE, new byte[]{(byte) 0xFD, 0x17, (byte) 0xE6, 0x08}, -48765432,
                DPT13.FLOW_RATE.getCalculationFunction().apply(-48765432));
    }

    /**
     * Test {@link DPT13Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPT13Value(DPT13.ACTIVE_ENERGY, new byte[0])).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertValue(final DPT13 dpt, final byte[] bytes, final int rawSignedValue, final double signedValue) {
        final var dptValue = new DPT13Value(dpt, rawSignedValue);
        final var dptValueByByte = new DPT13Value(dpt, bytes);

        // instance methods
        assertThat(dptValue.getRawSignedValue()).isEqualTo(rawSignedValue);
        assertThat(dptValue.getSignedValue()).isEqualTo(signedValue);
        assertThat(dptValue.toByteArray()).containsExactly(bytes);

        // class methods
        assertThat(DPT13Value.toByteArray(rawSignedValue)).containsExactly(bytes);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT13Value(DPT13.ACTIVE_ENERGY, rawSignedValue));
        assertThat(dptValue).isNotEqualTo(new DPT13Value(dpt, rawSignedValue + 1));

        // toString
        final var toString = String.format("DPT13Value{dpt=%s, signedValue=%s, rawSignedValue=%s, byteArray=%s}", dpt, signedValue, rawSignedValue,
                ByteFormatter.formatHexAsString(bytes));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
