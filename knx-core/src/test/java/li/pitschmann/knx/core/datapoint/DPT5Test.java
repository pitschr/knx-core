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

import li.pitschmann.knx.core.datapoint.value.DPT5Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT5}
 *
 * @author PITSCHR
 */
public class DPT5Test extends AbstractDataPointTypeTest<DPT5, DPT5Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT5.VALUE_1_OCTET_UNSIGNED_COUNT;

        assertThat(dpt.getId()).isEqualTo("5.010");
        assertThat(dpt.getDescription()).isEqualTo("Value 1-Octet Unsigned Count (pulses)");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT5.VALUE_1_OCTET_UNSIGNED_COUNT;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[0])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("foo")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("-1")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("256")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.toValue((byte) 0x00)).isInstanceOf(DPT5Value.class);
        assertThat(dpt.toValue((byte) 0xFF)).isInstanceOf(DPT5Value.class);
        assertThat(dpt.toValue(0)).isInstanceOf(DPT5Value.class);
        assertThat(dpt.toValue(255)).isInstanceOf(DPT5Value.class);
        assertThat(dpt.toValue("0")).isInstanceOf(DPT5Value.class);
        assertThat(dpt.toValue("255")).isInstanceOf(DPT5Value.class);
    }

    @Test
    public void testCalculation() {
        // without calculation functions
        assertThat(DPT5.PERCENT_U8.getCalcuationFunction()).isNull();

        /*
         * SCALING
         */
        // with calculation functions
        final var dptScaling = DPT5.SCALING;
        assertThat(dptScaling.getCalcuationFunction()).isInstanceOf(Function.class);
        // value: 0%
        assertThat(dptScaling.toValue(0).getUnsignedValue()).isEqualTo(0d);
        // value: ~25%
        assertThat(dptScaling.toValue((byte) 0x40).getUnsignedValue()).isCloseTo(25.0f, Percentage.withPercentage(0.4));
        // value: ~50%
        assertThat(dptScaling.toValue((byte) 0x7F).getUnsignedValue()).isCloseTo(50.0f, Percentage.withPercentage(0.4));
        // value: ~75%
        assertThat(dptScaling.toValue((byte) 0xC0).getUnsignedValue()).isCloseTo(75.0f, Percentage.withPercentage(0.4));
        // value: 100%
        assertThat(dptScaling.toValue((byte) 0xFF).getUnsignedValue()).isEqualTo(100d);

        /*
         * ANGLE
         */
        final var dptAngle = DPT5.ANGLE;
        // value: 0%
        assertThat(dptAngle.toValue(0).getUnsignedValue()).isEqualTo(0d);
        // value: ~90°
        assertThat(dptAngle.toValue(64).getUnsignedValue()).isCloseTo(90.0f, Percentage.withPercentage(1.4));
        // value: ~180°
        assertThat(dptAngle.toValue(127).getUnsignedValue()).isCloseTo(180.0f, Percentage.withPercentage(1.4));
        // value: ~270°
        assertThat(dptAngle.toValue(192).getUnsignedValue()).isCloseTo(270.0f, Percentage.withPercentage(1.4));
        // value: 360°
        assertThat(dptAngle.toValue(255).getUnsignedValue()).isEqualTo(360d);
    }

    @Override
    @Test
    public void testOf() {
        final var dpt = DPT5.VALUE_1_OCTET_UNSIGNED_COUNT;
        this.assertDPT(dpt, (byte) 0x00, 0);
        this.assertDPT(dpt, (byte) 0x40, 64);
        this.assertDPT(dpt, (byte) 0x7f, 127);
        this.assertDPT(dpt, (byte) 0xC0, 192);
        this.assertDPT(dpt, (byte) 0xFF, 255);
    }

    /**
     * Invalid Test {@link DPT5}
     */
    @Test
    public void testOfInvalid() {
        // wrong dpt
        assertThat(DPT5.ANGLE.toValue((byte) 0x00)).isNotEqualTo(DPT5.SCALING.toValue((byte) 0x00));
        // wrong value
        assertThat(DPT5.ANGLE.toValue((byte) 0x00)).isNotEqualTo(DPT5.ANGLE.toValue((byte) 0x01));
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code byteValue} and {@code intValue}
     *
     * @param dpt
     * @param byteValue
     * @param intValue
     */
    private void assertDPT(final DPT5 dpt, final byte byteValue, final int intValue) {
        final var dptValue = dpt.toValue(intValue);

        // assert base DPT
        this.assertBaseDPT(dpt, new byte[]{byteValue}, dptValue);
        // assert specific DPT5
        assertThat(dpt.toValue(String.valueOf(intValue))).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(intValue)).containsExactly(byteValue);
    }
}
