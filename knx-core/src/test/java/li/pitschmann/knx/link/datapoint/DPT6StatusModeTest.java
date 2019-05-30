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

import li.pitschmann.knx.link.datapoint.value.DPT6Value;
import li.pitschmann.knx.link.datapoint.value.DPT6Value.StatusMode.Mode;
import li.pitschmann.knx.link.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.link.exceptions.DataPointTypeIncompatibleSyntaxException;
import li.pitschmann.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT6.StatusMode}
 *
 * @author PITSCHR
 */
public class DPT6StatusModeTest extends AbstractDataPointTypeTest<DPT6.StatusMode, DPT6Value.StatusMode> {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT6.STATUS_MODE;

        assertThat(dpt.getId()).isEqualTo("6.020");
        assertThat(dpt.getDescription()).isEqualTo("Status Mode");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT6.STATUS_MODE;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[0])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"foo"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.toValue(new byte[]{0x00})).isInstanceOf(DPT6Value.StatusMode.class);
        assertThat(dpt.toValue(new byte[]{(byte) 0xFF})).isInstanceOf(DPT6Value.StatusMode.class);
        assertThat(dpt.toValue(new String[]{"0xFF"})).isInstanceOf(DPT6Value.StatusMode.class);
    }

    @Override
    @Test
    public void testOf() {
        // mode: 0, no flags set
        this.assertInternal((byte) 0x01, false, false, false, false, false, Mode.MODE_0);
        // mode: 1, no flags set
        this.assertInternal((byte) 0x02, false, false, false, false, false, Mode.MODE_1);
        // mode: 2, no flags set
        this.assertInternal((byte) 0x04, false, false, false, false, false, Mode.MODE_2);
    }

    /**
     * Asserts the {@link DPT6Value} for given arguments {@link DPT6.StatusMode}, {@code byteValue} and status mode
     * value relevant parameters
     *
     * @param byteValue
     * @param bool1
     * @param bool2
     * @param bool3
     * @param bool4
     * @param bool5
     * @param mode
     */
    private void assertInternal(final byte byteValue, final boolean bool1, final boolean bool2, final boolean bool3, final boolean bool4,
                                final boolean bool5, final Mode mode) {
        final var dptStatusMode = DPT6.STATUS_MODE;
        final var dptStatusModeValue = dptStatusMode.toValue(bool1, bool2, bool3, bool4, bool5, mode);

        // assert base DPT
        this.assertBaseDPT(dptStatusMode, new byte[]{byteValue}, dptStatusModeValue);
        // assert specific DPT6
        assertThat(dptStatusMode.toValue(new String[]{ByteFormatter.formatHex(byteValue)})).isEqualTo(dptStatusModeValue);
        assertThat(dptStatusMode.toByteArray(bool1, bool2, bool3, bool4, bool5, mode)).containsExactly(byteValue);
    }
}
