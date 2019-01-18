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

package li.pitschmann.knx.link.datapoint;

import li.pitschmann.knx.link.datapoint.value.*;
import li.pitschmann.knx.link.exceptions.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test Class for {@link DPT12}
 *
 * @author PITSCHR
 */
public class DPT12Test extends AbstractDataPointTypeTest<DPT12, DPT12Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final DPT12 dpt = DPT12.VALUE_4_OCTET_UNSIGNED_COUNT;

        assertThat(dpt.getId()).isEqualTo("12.001");
        assertThat(dpt.getDescription()).isEqualTo("Value 4-Octet Unsigned Count (pulses)");
    }

    @Override
    @Test
    public void testCompatibility() {
        final DPT12 dpt = DPT12.VALUE_4_OCTET_UNSIGNED_COUNT;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[3])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[5])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[0])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"foo"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"-1"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"4294967296"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.toValue(new byte[]{0x00, 0x00, 0x00, 0x00})).isInstanceOf(DPT12Value.class);
        assertThat(dpt.toValue(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF})).isInstanceOf(DPT12Value.class);
        assertThat(dpt.toValue(0)).isInstanceOf(DPT12Value.class);
        assertThat(dpt.toValue(4294967295L)).isInstanceOf(DPT12Value.class);
        assertThat(dpt.toValue(new String[]{"0"})).isInstanceOf(DPT12Value.class);
        assertThat(dpt.toValue(new String[]{"4294967295"})).isInstanceOf(DPT12Value.class);
    }

    @Override
    @Test
    public void testOf() {
        final DPT12 dpt = DPT12.VALUE_4_OCTET_UNSIGNED_COUNT;

        // value: 0
        this.assertDPT(dpt, new byte[]{0x00, 0x00, 0x00, 0x00}, 0L);
        // value: 305419896
        this.assertDPT(dpt, new byte[]{0x12, 0x34, 0x56, 0x78}, 305419896L);
        // value: 4294967295
        this.assertDPT(dpt, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, 4294967295L);
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code bValueArray} and {@code longValue}
     *
     * @param dpt
     * @param bValueArray
     * @param longValue
     */
    private void assertDPT(final DPT12 dpt, final byte[] bValueArray, final long longValue) {
        final DPT12Value dptValue = dpt.toValue(longValue);

        // assert base DPT
        this.assertBaseDPT(dpt, bValueArray, dptValue);

        // assert specific DPT12
        assertThat(dpt.toValue(new String[]{String.valueOf(longValue)})).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(longValue)).containsExactly(bValueArray);
    }
}
