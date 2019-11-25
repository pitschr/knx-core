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

import li.pitschmann.knx.core.datapoint.value.DPT14Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT14}
 *
 * @author PITSCHR
 */
public class DPT14Test extends AbstractDataPointTypeTest<DPT14, DPT14Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT14.TEMPERATURE;

        assertThat(dpt.getId()).isEqualTo("14.068");
        assertThat(dpt.getDescription()).isEqualTo("Temperature (°C)");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT14.TEMPERATURE;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[3])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[0])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("foo")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("-3.40282348e+38")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("3.40282348e+38")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.toValue((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00)).isInstanceOf(DPT14Value.class);
        assertThat(dpt.toValue((byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF)).isInstanceOf(DPT14Value.class);
        assertThat(dpt.toValue(-3.40282347e+38)).isInstanceOf(DPT14Value.class);
        assertThat(dpt.toValue(3.40282347e+38)).isInstanceOf(DPT14Value.class);
        assertThat(dpt.toValue("-3.40282347e+38")).isInstanceOf(DPT14Value.class);
        assertThat(dpt.toValue("3.40282347e+38")).isInstanceOf(DPT14Value.class);
    }

    @Override
    @Test
    public void testOf() {
        final var dpt = DPT14.TEMPERATURE;

        // value: 0°C
        this.assertDPT(dpt, new byte[]{0x00, 0x00, 0x00, 0x00}, 0f);
        // value: -3.40282347e+38f
        this.assertDPT(dpt, new byte[]{(byte) 0xff, (byte) 0x7f, (byte) 0xff, (byte) 0xff}, -3.40282347e+38f);
        // value: 3.40282347e+38f
        this.assertDPT(dpt, new byte[]{(byte) 0x7f, (byte) 0x7f, (byte) 0xff, (byte) 0xff}, 3.40282347e+38f);
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code bValueArray} and {@code doubleValue}
     *
     * @param dpt
     * @param bValueArray
     * @param doubleValue
     */
    private void assertDPT(final DPT14 dpt, final byte[] bValueArray, final double doubleValue) {
        final var dptValue = dpt.toValue(doubleValue);

        // assert base DPT
        this.assertBaseDPT(dpt, bValueArray, dptValue);
        // assert specific DPT14
        assertThat(dpt.toValue(String.valueOf(doubleValue))).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(doubleValue)).containsExactly(bValueArray);
    }

}
