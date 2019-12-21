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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPT13Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT13}
 *
 * @author PITSCHR
 */
public class DPT13Test extends AbstractDataPointTypeTest<DPT13, DPT13Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT13.VALUE_4_OCTET_COUNT;

        assertThat(dpt.getId()).isEqualTo("13.001");
        assertThat(dpt.getDescription()).isEqualTo("Value 4-Octet Signed Count (pulses)");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT13.VALUE_4_OCTET_COUNT;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[3])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[0])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("foo")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("-2147483649")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("2147483648")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.toValue((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00)).isInstanceOf(DPT13Value.class);
        assertThat(dpt.toValue((byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF)).isInstanceOf(DPT13Value.class);
        assertThat(dpt.toValue(-2147483648)).isInstanceOf(DPT13Value.class);
        assertThat(dpt.toValue(2147483647)).isInstanceOf(DPT13Value.class);
        assertThat(dpt.toValue("-2147483648")).isInstanceOf(DPT13Value.class);
        assertThat(dpt.toValue("2147483647")).isInstanceOf(DPT13Value.class);
    }

    @Override
    @Test
    public void testOf() {
        final var dpt = DPT13.VALUE_4_OCTET_COUNT;

        // value: 0
        this.assertDPT(dpt, new byte[]{0x00, 0x00, 0x00, 0x00}, 0);
        // value: 305419896
        this.assertDPT(dpt, new byte[]{(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78}, 305419896);
        // value: 2147483647
        this.assertDPT(dpt, new byte[]{(byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff}, Integer.MAX_VALUE);
        // value: 305419896
        this.assertDPT(dpt, new byte[]{(byte) 0xED, (byte) 0xCB, (byte) 0xA9, (byte) 0x87}, -305419897);
        // value: -2147483648
        this.assertDPT(dpt, new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00}, Integer.MIN_VALUE);
    }

    @Test
    public void testCalculation() {
        // with calculation functions
        assertThat(DPT13.FLOW_RATE.getCalculationFunction()).isInstanceOf(Function.class);

        // without calculation functions
        assertThat(DPT13.VALUE_4_OCTET_COUNT.getCalculationFunction()).isNull();

        // value: 214748.3647 m³/h
        final var valueMax = DPT13.FLOW_RATE.toValue(Integer.MAX_VALUE);
        assertThat(valueMax.getSignedValue()).isEqualTo(214748.3647);
        assertThat(valueMax.getRawSignedValue()).isEqualTo(Integer.MAX_VALUE);
        // value: -214748.3648 m³/h
        final var valueMin = DPT13.FLOW_RATE.toValue(Integer.MIN_VALUE);
        assertThat(valueMin.getSignedValue()).isEqualTo(-214748.3648);
        assertThat(valueMin.getRawSignedValue()).isEqualTo(Integer.MIN_VALUE);
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code bValueArray} and {@code intValue}
     *
     * @param dpt         data point type
     * @param bValueArray byte array with values
     * @param intValue    integer value
     */
    private void assertDPT(final DPT13 dpt, final byte[] bValueArray, final int intValue) {
        final var dptValue = dpt.toValue(intValue);

        // assert base DPT
        this.assertBaseDPT(dpt, bValueArray, dptValue);

        // assert specific DPT13
        assertThat(dpt.toValue(String.valueOf(intValue))).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(intValue)).containsExactly(bValueArray);
    }
}
