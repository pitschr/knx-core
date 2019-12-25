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

import li.pitschmann.knx.core.datapoint.value.DPT18Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT18}
 *
 * @author PITSCHR
 */
public class DPT18Test extends AbstractDataPointTypeTest<DPT18, DPT18Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT18.SCENE_CONTROL;

        assertThat(dpt.getId()).isEqualTo("18.001");
        assertThat(dpt.getDescription()).isEqualTo("Scene Control");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT18.SCENE_CONTROL;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue("0x00", "0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue("foo")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("-1")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("64")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.toValue((byte) 0x00)).isInstanceOf(DPT18Value.class);
        assertThat(dpt.toValue((byte) 0xFF)).isInstanceOf(DPT18Value.class);
        assertThat(dpt.toValue("0x00")).isInstanceOf(DPT18Value.class);
        assertThat(dpt.toValue("0xFF")).isInstanceOf(DPT18Value.class);
        assertThat(dpt.toValue(false, 0)).isInstanceOf(DPT18Value.class);
        assertThat(dpt.toValue(true, 63)).isInstanceOf(DPT18Value.class);
        assertThat(dpt.toValue("0")).isInstanceOf(DPT18Value.class);
        assertThat(dpt.toValue("controlled", "1")).isInstanceOf(DPT18Value.class);
        assertThat(dpt.toValue("62")).isInstanceOf(DPT18Value.class);
        assertThat(dpt.toValue("controlled", "63")).isInstanceOf(DPT18Value.class);
    }

    @Override
    @Test
    public void testOf() {
        final var dpt = DPT18.SCENE_CONTROL;

        // No Control, Scene Number: 0
        this.assertDPT(dpt, (byte) 0x00, false, 0);
        // Control, Scene Number: 21
        this.assertDPT(dpt, (byte) 0x95, true, 21);
        // No Control, Scene Number: 42
        this.assertDPT(dpt, (byte) 0x2A, false, 42);
        // Control, Scene Number: 63
        this.assertDPT(dpt, (byte) 0xBF, true, 63);
    }

    /**
     * Invalid Test {@link DPT18}
     */
    @Test
    public void testOfInvalid() {
        // wrong value
        assertThat(DPT18.SCENE_CONTROL.toValue((byte) 0x00)).isNotEqualTo(DPT18.SCENE_CONTROL.toValue((byte) 0x01));
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code bValue}, {@code controlled} and {@code intValue}
     *
     * @param dpt        data point type
     * @param bValue     byte value
     * @param controlled controlled
     * @param intValue   integer value
     */
    private void assertDPT(final DPT18 dpt, final byte bValue, final boolean controlled, final int intValue) {
        final var dptValue = dpt.toValue(controlled, intValue);

        // assert base DPT
        this.assertBaseDPT(dpt, new byte[]{bValue}, dptValue);
        // assert specific DPT18
        if (controlled) {
            assertThat(dpt.toValue("controlled", String.valueOf(intValue))).isEqualTo(dptValue);
        } else {
            assertThat(dpt.toValue(String.valueOf(intValue))).isEqualTo(dptValue);
        }
        assertThat(dpt.toByteArray(controlled, intValue)).containsExactly(bValue);
    }
}
