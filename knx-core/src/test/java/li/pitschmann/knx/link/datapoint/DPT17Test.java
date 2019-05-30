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

import li.pitschmann.knx.link.datapoint.value.DPT17Value;
import li.pitschmann.knx.link.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.link.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT17}
 *
 * @author PITSCHR
 */
public class DPT17Test extends AbstractDataPointTypeTest<DPT17, DPT17Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT17.SCENE_NUMBER;

        assertThat(dpt.getId()).isEqualTo("17.001");
        assertThat(dpt.getDescription()).isEqualTo("Scene Number");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT17.SCENE_NUMBER;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[0])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"foo"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"-1"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"64"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.toValue(new byte[]{0x00})).isInstanceOf(DPT17Value.class);
        assertThat(dpt.toValue(new byte[]{(byte) 0x3F})).isInstanceOf(DPT17Value.class);
        assertThat(dpt.toValue(0)).isInstanceOf(DPT17Value.class);
        assertThat(dpt.toValue(63)).isInstanceOf(DPT17Value.class);
        assertThat(dpt.toValue(new String[]{"0"})).isInstanceOf(DPT17Value.class);
        assertThat(dpt.toValue(new String[]{"63"})).isInstanceOf(DPT17Value.class);
    }

    @Override
    @Test
    public void testOf() {
        final var dpt = DPT17.SCENE_NUMBER;

        // Scene Number: 0
        this.assertDPT(dpt, (byte) 0x00, 0);
        // Scene Number: 21
        this.assertDPT(dpt, (byte) 0x15, 21);
        // Scene Number: 42
        this.assertDPT(dpt, (byte) 0x2A, 42);
        // Scene Number: 63
        this.assertDPT(dpt, (byte) 0x3F, 63);
    }

    /**
     * Invalid Test {@link DPT17}
     */
    @Test
    public void testOfInvalid() {
        // wrong value
        assertThat(DPT17.SCENE_NUMBER.toValue(new byte[]{(byte) 0x00})).isNotEqualTo(DPT17.SCENE_NUMBER.toValue(new byte[]{(byte) 0x01}));
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code bValue} and {@code intValue}
     *
     * @param dpt
     * @param bValue
     * @param intValue
     */
    private void assertDPT(final DPT17 dpt, final byte bValue, final int intValue) {
        final var dptValue = dpt.toValue(intValue);

        // assert base DPT
        this.assertBaseDPT(dpt, new byte[]{bValue}, dptValue);
        // assert specific DPT17
        assertThat(dpt.toValue(new String[]{String.valueOf(intValue)})).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(intValue)).containsExactly(bValue);
    }
}
