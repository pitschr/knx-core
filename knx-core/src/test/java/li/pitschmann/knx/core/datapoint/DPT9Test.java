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

import li.pitschmann.knx.core.datapoint.value.DPT9Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT9}
 *
 * @author PITSCHR
 */
public class DPT9Test extends AbstractDataPointTypeTest<DPT9, DPT9Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT9.TEMPERATURE;

        assertThat(dpt.getId()).isEqualTo("9.001");
        assertThat(dpt.getDescription()).isEqualTo("Temperature (°C)");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT9.TEMPERATURE_DIFFERENCE;

        // failures
        assertThatThrownBy(() -> dpt.of(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of(new byte[3])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("0x00", "0x00", "0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("foo")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.of("foo", "bar")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.of("-671088.65")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.of("670760.97")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.of((byte) 0x00, (byte) 0x00)).isInstanceOf(DPT9Value.class);
        assertThat(dpt.of((byte) 0xFF, (byte) 0xFF)).isInstanceOf(DPT9Value.class);
        assertThat(dpt.of(-671088.64)).isInstanceOf(DPT9Value.class);
        assertThat(dpt.of(670433.28)).isInstanceOf(DPT9Value.class);
        assertThat(dpt.of("0x00", "0x00")).isInstanceOf(DPT9Value.class);
        assertThat(dpt.of("0xFF", "0xFF")).isInstanceOf(DPT9Value.class);
        assertThat(dpt.of("-671088.64")).isInstanceOf(DPT9Value.class);
        assertThat(dpt.of("670433.28")).isInstanceOf(DPT9Value.class);
    }

    @Override
    @Test
    public void testOf() {
        /*
         * TEMPERATURE
         */
        final var dptTemperature = DPT9.TEMPERATURE;
        // value: 0°C
        this.assertInternal(dptTemperature, new byte[]{0x00, 0x00}, 0d);
        // value: -273 (-272.96 because the precision is not so accurate)
        // 0xa1 55 = -273.12
        // 0xa1 56 = -272.96 <== most accurate one
        // 0xa1 57 = -272.80
        this.assertInternal(dptTemperature, new byte[]{(byte) 0xa1, (byte) 0x56}, -272.96d);
        // value: 670433.28
        this.assertInternal(dptTemperature, new byte[]{(byte) 0x7f, (byte) 0xfe}, 670433.28d);

        /*
         * POWER
         */
        final var dptPower = DPT9.POWER;
        // value: 0 kW
        this.assertInternal(dptPower, new byte[]{0x00, 0x00}, 0d);
        // value: -671088.64 kW
        this.assertInternal(dptPower, new byte[]{(byte) 0xf8, (byte) 0x00}, -671088.64d);
        // value: 670433.28 kW
        this.assertInternal(dptPower, new byte[]{(byte) 0x7f, (byte) 0xfe}, 670433.28d);
    }

    /**
     * Asserts the DPT for given arguments {@code bValueArray} and {@code doubleValue}
     *
     * @param dpt         data point type
     * @param bValueArray byte array with values
     * @param doubleValue double value
     */
    private void assertInternal(final DPT9 dpt, final byte[] bValueArray, final double doubleValue) {
        final var dptValue = dpt.of(doubleValue);

        // assert base DPT
        this.assertBaseDPT(dpt, bValueArray, dptValue);

        // assert specific DPT9
        assertThat(dpt.of(String.valueOf(doubleValue))).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(doubleValue)).containsExactly(bValueArray);
    }
}
