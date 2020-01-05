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

import li.pitschmann.knx.core.datapoint.value.DPT7Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT7}
 *
 * @author PITSCHR
 */
public class DPT7Test extends AbstractDataPointTypeTest<DPT7, DPT7Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;

        assertThat(dpt.getId()).isEqualTo("7.001");
        assertThat(dpt.getDescription()).isEqualTo("Value 2-Octet Unsigned Count (pulses)");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT;

        // failures
        assertThatThrownBy(() -> dpt.of(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of(new byte[3])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("0x00", "0x00", "0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("foo")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.of("-1")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.of("65536")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.of((byte) 0x00, (byte) 0x00)).isInstanceOf(DPT7Value.class);
        assertThat(dpt.of((byte) 0xFF, (byte) 0xFF)).isInstanceOf(DPT7Value.class);
        assertThat(dpt.of(0)).isInstanceOf(DPT7Value.class);
        assertThat(dpt.of(65535)).isInstanceOf(DPT7Value.class);
        assertThat(dpt.of("0x00", "0x00")).isInstanceOf(DPT7Value.class);
        assertThat(dpt.of("0xFF", "0xFF")).isInstanceOf(DPT7Value.class);
        assertThat(dpt.of("0")).isInstanceOf(DPT7Value.class);
        assertThat(dpt.of("65535")).isInstanceOf(DPT7Value.class);
    }

    @Test
    public void testCalculation() {
        // without calculation functions
        assertThat(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT.getCalculationFunction()).isNull();

        /*
         * TIME_PERIOD_10MS
         */
        // with calculation functions
        final DPT7 dpt10ms = DPT7.TIME_PERIOD_10MS;
        assertThat(dpt10ms.getCalculationFunction()).isInstanceOf(Function.class);
        // value: 0ms
        assertThat(dpt10ms.of(0).getUnsignedValue()).isEqualTo(0d);
        // value: 123.45ms
        assertThat(dpt10ms.of(12345).getUnsignedValue()).isEqualTo(123.45d);
        // value: 467.21ms
        assertThat(dpt10ms.of(46721).getUnsignedValue()).isEqualTo(467.21d);
        // value: 655.35ms
        assertThat(dpt10ms.of(65535).getUnsignedValue()).isEqualTo(655.35d);

        /*
         * TIME_PERIOD_100MS
         */
        final DPT7 dpt100ms = DPT7.TIME_PERIOD_100MS;
        assertThat(dpt100ms.getCalculationFunction()).isInstanceOf(Function.class);
        // value: 0ms
        assertThat(dpt100ms.of(0).getUnsignedValue()).isEqualTo(0d);
        // value: 1234.5ms
        assertThat(dpt100ms.of(12345).getUnsignedValue()).isEqualTo(1234.5d);
        // value: 4672.1ms
        assertThat(dpt100ms.of(46721).getUnsignedValue()).isEqualTo(4672.1d);
        // value: 6553.5ms
        assertThat(dpt100ms.of(65535).getUnsignedValue()).isEqualTo(6553.5d);
    }

    @Override
    @Test
    public void testOf() {
        final var dpt = DPT7.TIME_PERIOD_MS;

        // value: 0ms
        this.assertDPT(dpt, new byte[]{0x00, 0x00}, 0);
        // value: 12345ms
        this.assertDPT(dpt, new byte[]{0x30, 0x39}, 12345);
        // value: 46721ms
        this.assertDPT(dpt, new byte[]{(byte) 0xb6, (byte) 0x81}, 46721);
        // value: 65535ms
        this.assertDPT(dpt, new byte[]{(byte) 0xFF, (byte) 0xFF}, 65535);
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code bValueArray} and {@code intValue}
     *
     * @param dpt         data point type
     * @param bValueArray byte array with values
     * @param intValue    integer value
     */
    private void assertDPT(final DPT7 dpt, final byte[] bValueArray, final int intValue) {
        final var dptValue = dpt.of(intValue);

        // assert base DPT
        this.assertBaseDPT(dpt, bValueArray, dptValue);

        // assert specific DPT7
        assertThat(dpt.of(String.valueOf(intValue))).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(intValue)).containsExactly(bValueArray);
    }
}
