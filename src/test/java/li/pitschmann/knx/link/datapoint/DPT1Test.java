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

import li.pitschmann.knx.link.datapoint.value.DPT1Value;
import li.pitschmann.knx.link.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.link.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT1}
 *
 * @author PITSCHR
 */
public class DPT1Test extends AbstractDataPointTypeTest<DPT1, DPT1Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final DPT1 dpt = DPT1.SWITCH;

        assertThat(dpt.getId()).isEqualTo("1.001");
        assertThat(dpt.getDescription()).isEqualTo("Switch");
    }

    @Override
    @Test
    public void testCompatibility() {
        final DPT1 dpt = DPT1.SWITCH;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[]{0x02})).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT1.SWITCH.toValue(new String[0])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT1.SWITCH.toValue(new String[]{"false", "true"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.toValue(new byte[]{0x00})).isInstanceOf(DPT1Value.class);
        assertThat(dpt.toValue(new byte[]{0x01})).isInstanceOf(DPT1Value.class);
        assertThat(dpt.toValue(new String[]{"false"})).isInstanceOf(DPT1Value.class);
        assertThat(dpt.toValue(new String[]{"true"})).isInstanceOf(DPT1Value.class);
        assertThat(dpt.toValue(new String[]{"0"})).isInstanceOf(DPT1Value.class);
        assertThat(dpt.toValue(new String[]{"1"})).isInstanceOf(DPT1Value.class);
    }

    /**
     * Test {@link DPT1}
     */
    @Override
    @Test
    public void testOf() {
        // value: false
        this.assertDPT(DPT1.SWITCH, (byte) 0x00, false, new String[]{"false"}, new String[]{"0"});
        // value: true
        this.assertDPT(DPT1.SWITCH, (byte) 0x01, true, new String[]{"true"}, new String[]{"1"});
    }

    /**
     * Tests the {@link DPT1#getTextForFalse()}, {@link DPT1#getTextForTrue()} and {@link DPT1#getTextFor(boolean)}
     */
    @Test
    public void testText() {
        final DPT1 dpt = DPT1.SWITCH;

        // direct methods
        assertThat(dpt.getTextForFalse()).isEqualTo("off");
        assertThat(dpt.getTextForTrue()).isEqualTo("on");

        // method controlled by argument
        assertThat(dpt.getTextFor(false)).isEqualTo("off");
        assertThat(dpt.getTextFor(true)).isEqualTo("on");
    }

    /**
     * Invalid Test {@link DPT1}
     */
    @Test
    public void testOfInvalid() {
        // wrong dpt
        assertThat(DPT1.SWITCH.toValue(new byte[]{(byte) 0x00})).isNotEqualTo(DPT1.ACK.toValue(new byte[]{(byte) 0x00}));
        assertThat(DPT1.SWITCH.toValue(new byte[]{(byte) 0x01})).isNotEqualTo(DPT1.ACK.toValue(new byte[]{(byte) 0x01}));
        // wrong value
        assertThat(DPT1.SWITCH.toValue(new byte[]{(byte) 0x00})).isNotEqualTo(DPT1.SWITCH.toValue(new byte[]{(byte) 0x01}));
        assertThat(DPT1.SWITCH.toValue(new byte[]{(byte) 0x01})).isNotEqualTo(DPT1.SWITCH.toValue(new byte[]{(byte) 0x00}));
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code byteValue} and {@code boolValue}. The {@code strValue} is
     * the human-friendly text for given arguments, while {@code strIntValue} uses the binary character for true/false
     * evaluation (true=1, false=0).
     *
     * @param dpt
     * @param byteValue
     * @param boolValue
     * @param strValue
     * @param strIntValue
     */
    private void assertDPT(final DPT1 dpt, final byte byteValue, final boolean boolValue, final String[] strValue, final String[] strIntValue) {
        final DPT1Value dptValue = dpt.toValue(boolValue);

        // assert base DPT
        this.assertBaseDPT(dpt, new byte[]{byteValue}, dptValue);
        // assert specific DPT1
        assertThat(dpt.toValue(strValue)).isEqualTo(dptValue);
        assertThat(dpt.toValue(strIntValue)).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(boolValue)).containsExactly(byteValue);
    }
}
