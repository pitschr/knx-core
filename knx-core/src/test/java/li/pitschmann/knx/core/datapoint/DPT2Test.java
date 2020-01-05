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

import li.pitschmann.knx.core.datapoint.value.DPT2Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT2}
 *
 * @author PITSCHR
 */
public class DPT2Test extends AbstractDataPointTypeTest<DPT2, DPT2Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT2.SWITCH_CONTROL;

        assertThat(dpt.getId()).isEqualTo("2.001");
        assertThat(dpt.getDescription()).isEqualTo("Switch Controlled");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT2.SWITCH_CONTROL;

        // failures
        assertThatThrownBy(() -> dpt.of((byte) 0x04)).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("0x05")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("false", "true", "false")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.of((byte) 0x00)).isInstanceOf(DPT2Value.class);
        assertThat(dpt.of((byte) 0x01)).isInstanceOf(DPT2Value.class);
        assertThat(dpt.of("0x03")).isInstanceOf(DPT2Value.class);
        assertThat(dpt.of("false")).isInstanceOf(DPT2Value.class);
        assertThat(dpt.of("true")).isInstanceOf(DPT2Value.class);
        assertThat(dpt.of("0")).isInstanceOf(DPT2Value.class);
        assertThat(dpt.of("1")).isInstanceOf(DPT2Value.class);
    }

    @Override
    @Test
    public void testOf() {
        // value: false, false => 0000
        this.assertDPT(DPT2.SWITCH_CONTROL, (byte) 0x00, false, false, new String[]{"false"}, new String[]{"0"});
        // value: false, true => 0001
        this.assertDPT(DPT2.SWITCH_CONTROL, (byte) 0x01, false, true, new String[]{"true"}, new String[]{"1"});
        // value: true, false => 0010
        this.assertDPT(DPT2.SWITCH_CONTROL, (byte) 0x02, true, false, new String[]{"controlled", "false"}, new String[]{"controlled", "0"});
        // value: true, true => 0011
        this.assertDPT(DPT2.SWITCH_CONTROL, (byte) 0x03, true, true, new String[]{"controlled", "true"}, new String[]{"controlled", "1"});
    }

    @Test
    public void testText() {
        assertThat(DPT2.SWITCH_CONTROL.getDPT1()).isEqualTo(DPT1.SWITCH);
        assertThat(DPT2.OPEN_CLOSE_CONTROL.getDPT1()).isEqualTo(DPT1.OPEN_CLOSE);
    }

    /**
     * Invalid Test {@link DPT2}
     */
    @Test
    public void testOfInvalid() {
        // wrong dpt
        assertThat(DPT2.SWITCH_CONTROL.of((byte) 0x00)).isNotEqualTo(DPT2.ALARM_CONTROL.of((byte) 0x00));
        assertThat(DPT2.SWITCH_CONTROL.of((byte) 0x01)).isNotEqualTo(DPT2.ALARM_CONTROL.of((byte) 0x01));
        assertThat(DPT2.SWITCH_CONTROL.of((byte) 0x02)).isNotEqualTo(DPT2.ALARM_CONTROL.of((byte) 0x02));
        assertThat(DPT2.SWITCH_CONTROL.of((byte) 0x03)).isNotEqualTo(DPT2.ALARM_CONTROL.of((byte) 0x03));
        // wrong value
        assertThat(DPT2.SWITCH_CONTROL.of((byte) 0x00)).isNotEqualTo(DPT2.SWITCH_CONTROL.of((byte) 0x03));
        assertThat(DPT2.SWITCH_CONTROL.of((byte) 0x01)).isNotEqualTo(DPT2.SWITCH_CONTROL.of((byte) 0x02));
        assertThat(DPT2.SWITCH_CONTROL.of((byte) 0x02)).isNotEqualTo(DPT2.SWITCH_CONTROL.of((byte) 0x01));
        assertThat(DPT2.SWITCH_CONTROL.of((byte) 0x03)).isNotEqualTo(DPT2.SWITCH_CONTROL.of((byte) 0x00));
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code byteValue}, {@code controlled} and {@code boolValue}. The
     * {@code strValue} is the human-friendly text for given arguments, while {@code strIntValue} uses the binary
     * character for true/false evaluation (true=1, false=0).
     *
     * @param dpt         data point type
     * @param byteValue   byte value
     * @param controlled  controlled value
     * @param boolValue   boolean value
     * @param strValue    string value
     * @param strIntValue integer value as string representation
     */
    private void assertDPT(final DPT2 dpt, final byte byteValue, final boolean controlled, final boolean boolValue,
                           final String[] strValue, final String[] strIntValue) {
        final var dptValue = dpt.of(controlled, boolValue);

        // assert base DPT
        this.assertBaseDPT(dpt, new byte[]{byteValue}, dptValue);
        // assert specific DPT2
        assertThat(dpt.of(strValue)).isEqualTo(dptValue);
        assertThat(dpt.of(strIntValue)).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(controlled, boolValue)).containsExactly(byteValue);
    }
}
