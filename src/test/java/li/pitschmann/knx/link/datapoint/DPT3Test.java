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
import li.pitschmann.knx.link.datapoint.value.DPT3Value.*;
import li.pitschmann.knx.link.exceptions.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test Class for {@link DPT3}
 *
 * @author PITSCHR
 */
public class DPT3Test extends AbstractDataPointTypeTest<DPT3, DPT3Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final DPT3 dpt = DPT3.DPT_CONTROL_BLINDS;

        assertThat(dpt.getId()).isEqualTo("3.008");
        assertThat(dpt.getDescription()).isEqualTo("Control Blinds");
    }

    @Override
    @Test
    public void testCompatibility() {
        final DPT3 dpt = DPT3.DPT_CONTROL_BLINDS;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[]{0x10})).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[0])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"false", "true", "false"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        for (int i = 0; i < 0x10; i++) {
            assertThat(dpt.toValue(new byte[]{(byte) i})).isInstanceOf(DPT3Value.class);
        }
        for (int i = 0; i < 7; i++) {
            assertThat(dpt.toValue(new String[]{String.valueOf(i)})).isInstanceOf(DPT3Value.class);
            assertThat(dpt.toValue(new String[]{"controlled", String.valueOf(i)})).isInstanceOf(DPT3Value.class);
        }
    }

    @Override
    @Test
    public void testOf() {
        // value: controlled = false, step = 0 (STOP) => 0000
        this.assertDPT(DPT3.DPT_CONTROL_BLINDS, (byte) 0x00, false, 0, StepInterval.STOP, new String[]{"0"});
        // value: controlled = true, step = 2 (PERCENT_50) => 1010
        this.assertDPT(DPT3.DPT_CONTROL_BLINDS, (byte) 0x0A, true, 2, StepInterval.PERCENT_50, new String[]{"controlled", "2"});
        // value: controlled = false, stepInterval = 7 (PERCENT_1) => 0111
        this.assertDPT(DPT3.DPT_CONTROL_BLINDS, (byte) 0x07, false, 7, StepInterval.PERCENT_1, new String[]{"7"});
        // value: controlled = true, stepInterval = 7 (PERCENT_1) => 0111
        this.assertDPT(DPT3.DPT_CONTROL_BLINDS, (byte) 0x0F, true, 7, StepInterval.PERCENT_1, new String[]{"controlled", "7"});
    }

    @Test
    public void testText() {
        assertThat(DPT3.DPT_CONTROL_BLINDS.getDPT1()).isEqualTo(DPT1.UP_DOWN);
        assertThat(DPT3.DPT_CONTROL_DIMMING.getDPT1()).isEqualTo(DPT1.STEP);
    }

    /**
     * Invalid Test {@link DPT3}
     */
    @Test
    public void testOfInvalid() {
        // wrong dpt
        assertThat(DPT3.DPT_CONTROL_BLINDS.toValue(new byte[]{(byte) 0x00}))
                .isNotEqualTo(DPT3.DPT_CONTROL_DIMMING.toValue(new byte[]{(byte) 0x00}));
        // wrong value
        assertThat(DPT3.DPT_CONTROL_BLINDS.toValue(new byte[]{(byte) 0x00}))
                .isNotEqualTo(DPT3.DPT_CONTROL_BLINDS.toValue(new byte[]{(byte) 0x01}));
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code byteValue}, {@code controlled} and {@code stepCode}.
     * {@code stepInterval} represents the same value like {@code stepCode}. The {@code strValue} is the human-friendly
     * text for given arguments.
     *
     * @param dpt
     * @param byteValue
     * @param controlled
     * @param stepCode
     * @param stepInterval
     * @param strValue
     */
    private void assertDPT(final DPT3 dpt, final byte byteValue, final boolean controlled, final int stepCode, final StepInterval stepInterval,
                           final String[] strValue) {
        final DPT3Value dptValue = dpt.toValue(controlled, stepCode);

        // assert base DPT
        this.assertBaseDPT(dpt, new byte[]{byteValue}, dptValue);
        // assert specific DPT3
        assertThat(dpt.toValue(controlled, stepInterval)).isEqualTo(dptValue);
        assertThat(dpt.toValue(strValue)).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(controlled, stepCode)).containsExactly(byteValue);
        assertThat(dpt.toByteArray(controlled, stepInterval)).containsExactly(byteValue);
    }
}
