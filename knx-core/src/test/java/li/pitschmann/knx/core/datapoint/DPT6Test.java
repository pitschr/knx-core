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

import li.pitschmann.knx.core.datapoint.value.DPT6Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT6}
 *
 * @author PITSCHR
 */
public class DPT6Test extends AbstractDataPointTypeTest<DPT6, DPT6Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT6.PERCENT;

        assertThat(dpt.getId()).isEqualTo("6.001");
        assertThat(dpt.getDescription()).isEqualTo("Percent (%)");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT6.PERCENT;

        // failures
        assertThatThrownBy(() -> dpt.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("0x00", "0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("foo")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.of("-129")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.of("128")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.of((byte) 0x00)).isInstanceOf(DPT6Value.class);
        assertThat(dpt.of((byte) 0xFF)).isInstanceOf(DPT6Value.class);
        assertThat(dpt.of(-128)).isInstanceOf(DPT6Value.class);
        assertThat(dpt.of(127)).isInstanceOf(DPT6Value.class);
        assertThat(dpt.of("0x00")).isInstanceOf(DPT6Value.class);
        assertThat(dpt.of("0xFF")).isInstanceOf(DPT6Value.class);
        assertThat(dpt.of("-128")).isInstanceOf(DPT6Value.class);
        assertThat(dpt.of("127")).isInstanceOf(DPT6Value.class);
    }

    @Override
    @Test
    public void testOf() {
        final var dpt = DPT6.PERCENT;

        // value: 0%
        this.assertDPT(dpt, (byte) 0x00, 0);
        // value: -128%
        this.assertDPT(dpt, (byte) 0x80, -128);
        // value: 127%
        this.assertDPT(dpt, (byte) 0x7F, 127);
    }

    /**
     * Invalid Test {@link DPT6}
     */
    @Test
    public void testOfInvalid() {
        // wrong dpt
        assertThat(DPT6.PERCENT.of((byte) 0x00)).isNotEqualTo(DPT6.STATUS_MODE.of((byte) 0x00));
        // wrong value
        assertThat(DPT6.PERCENT.of((byte) 0x00)).isNotEqualTo(DPT6.PERCENT.of((byte) 0x01));
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code bValue} and {@code intValue}
     *
     * @param dpt      data point type
     * @param bValue   byte value
     * @param intValue integer value
     */
    private void assertDPT(final DPT6 dpt, final byte bValue, final int intValue) {
        final var dptValue = dpt.of(intValue);

        // assert base DPT
        this.assertBaseDPT(dpt, new byte[]{bValue}, dptValue);

        // assert specific DPT6
        assertThat(dpt.of(String.valueOf(intValue))).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(intValue)).containsExactly(bValue);
    }

}
