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

import li.pitschmann.knx.core.datapoint.value.DPT8Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT8}
 *
 * @author PITSCHR
 */
public class DPT8Test extends AbstractDataPointTypeTest<DPT8, DPT8Value> {

    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;

        assertThat(dpt.getId()).isEqualTo("8.001");
        assertThat(dpt.getDescription()).isEqualTo("Value 2-Octet Signed Count (pulses)");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[3])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue("foo")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("foo", "bar")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("-32769")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("32768")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.toValue((byte) 0x00, (byte) 0x00)).isInstanceOf(DPT8Value.class);
        assertThat(dpt.toValue((byte) 0xFF, (byte) 0xFF)).isInstanceOf(DPT8Value.class);
        assertThat(dpt.toValue(-32768)).isInstanceOf(DPT8Value.class);
        assertThat(dpt.toValue(32767)).isInstanceOf(DPT8Value.class);
        assertThat(dpt.toValue("-32768")).isInstanceOf(DPT8Value.class);
        assertThat(dpt.toValue("32767")).isInstanceOf(DPT8Value.class);
    }

    /**
     * Tests the {@link DPT8#getCalculationFunction()}
     */
    @Test
    public void testCalculation() {
        // without calculation functions
        assertThat(DPT8.VALUE_2_OCTET_COUNT.getCalculationFunction()).isNull();

        // with calculation functions
        /*
         * PERCENT
         */
        final var dpt = DPT8.PERCENT;
        assertThat(dpt.getCalculationFunction()).isInstanceOf(Function.class);
        // value: 0%
        assertThat(dpt.toValue(0).getSignedValue()).isEqualTo(0d);
        // value: 327,67%
        assertThat(dpt.toValue(32767).getSignedValue()).isEqualTo(327.67d);
        // value: -327,68%
        assertThat(dpt.toValue(-32768).getSignedValue()).isEqualTo(-327.68d);

        /*
         * DELTA_TIME_10MS / DELTA_TIME_100MS
         */
        // value: 327,67ms
        assertThat(DPT8.DELTA_TIME_10MS.toValue(32767).getSignedValue()).isEqualTo(327.67d);
        // value: 3276,7ms
        assertThat(DPT8.DELTA_TIME_100MS.toValue(32767).getSignedValue()).isEqualTo(3276.7d);
    }

    @Override
    @Test
    public void testOf() {
        final var dpt = DPT8.VALUE_2_OCTET_COUNT;

        // value: 0
        this.assertDPT(dpt, new byte[]{0x00, 0x00}, 0);
        // value: 32767
        this.assertDPT(dpt, new byte[]{(byte) 0x7f, (byte) 0xff}, 32767);
        // value: -32768
        this.assertDPT(dpt, new byte[]{(byte) 0x80, (byte) 0x00}, -32768);
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code bValueArray} and {@code intValue}
     *
     * @param dpt         data point type
     * @param bValueArray byte array with values
     * @param intValue    integer value
     */
    private void assertDPT(final DPT8 dpt, final byte[] bValueArray, final int intValue) {
        final var dptValue = dpt.toValue(intValue);

        // assert base DPT
        this.assertBaseDPT(dpt, bValueArray, dptValue);

        // assert specific DPT8
        assertThat(dpt.toValue(String.valueOf(intValue))).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(intValue)).containsExactly(bValueArray);
        assertThat(dptValue.getRawSignedValue()).isEqualTo(intValue);
    }
}
