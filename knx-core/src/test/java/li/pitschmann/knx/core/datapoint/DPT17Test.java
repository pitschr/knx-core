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

import li.pitschmann.knx.core.datapoint.value.DPT17Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT17}
 *
 * @author PITSCHR
 */
public class DPT17Test implements DPTTest {
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
        assertThatThrownBy(() -> dpt.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("0x00", "0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("foo")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.of("-1")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.of("64")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.of((byte) 0x00)).isInstanceOf(DPT17Value.class);
        assertThat(dpt.of((byte) 0x3F)).isInstanceOf(DPT17Value.class);
        assertThat(dpt.of("0x00")).isInstanceOf(DPT17Value.class);
        assertThat(dpt.of("0x3F")).isInstanceOf(DPT17Value.class);
        assertThat(dpt.of(0)).isInstanceOf(DPT17Value.class);
        assertThat(dpt.of(63)).isInstanceOf(DPT17Value.class);
        assertThat(dpt.of("0")).isInstanceOf(DPT17Value.class);
        assertThat(dpt.of("63")).isInstanceOf(DPT17Value.class);
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
        assertThat(DPT17.SCENE_NUMBER.of((byte) 0x00)).isNotEqualTo(DPT17.SCENE_NUMBER.of((byte) 0x01));
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code bValue} and {@code intValue}
     *
     * @param dpt      data point type
     * @param bValue   byte value
     * @param intValue integer value
     */
    private void assertDPT(final DPT17 dpt, final byte bValue, final int intValue) {
        final var dptValue = dpt.of(intValue);

        // assert base DPT
        this.assertBaseDPT(dpt, new byte[]{bValue}, dptValue);
        // assert specific DPT17
        assertThat(dpt.of(String.valueOf(intValue))).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(intValue)).containsExactly(bValue);
    }
}
