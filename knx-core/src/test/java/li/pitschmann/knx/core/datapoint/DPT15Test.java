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

import li.pitschmann.knx.core.datapoint.value.DPT15Value;
import li.pitschmann.knx.core.datapoint.value.DPT15Value.Flags;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT15}
 *
 * @author PITSCHR
 */
public class DPT15Test extends AbstractDataPointTypeTest<DPT15, DPT15Value> {
    private static final DPT15 DPT_ACCESS_DATA = DPT15.ACCESS_DATA;

    @Override
    @Test
    public void testIdAndDescription() {
        assertThat(DPT_ACCESS_DATA.getId()).isEqualTo("15.000");
        assertThat(DPT_ACCESS_DATA.getDescription()).isEqualTo("Access Data");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT_ACCESS_DATA;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[3])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[5])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue("0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue("0x00", "0x00", "0x00", "0x00", "0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // OK
        assertThat(dpt.toValue((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00)).isInstanceOf(DPT15Value.class);
        assertThat(dpt.toValue((byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF)).isInstanceOf(DPT15Value.class);
        assertThat(dpt.toValue("0x00", "0x00", "0x00", "0x00")).isInstanceOf(DPT15Value.class);
        assertThat(dpt.toValue("0xFF", "0xFF", "0xFF", "0xFF")).isInstanceOf(DPT15Value.class);
    }

    @Override
    @Test
    public void testOf() {
        this.assertDPT(new byte[]{0x00, 0x00, 0x00, 0x00}, new byte[3], new Flags(false, false, false, false, 0));

        this.assertDPT(new byte[]{0x11, 0x22, 0x33, 0x0F}, new byte[]{0x11, 0x22, 0x33}, new Flags(false, false, false, false, 0x0F));

        this.assertDPT(new byte[]{0x44, 0x55, 0x66, (byte) 0xff}, new byte[]{0x44, 0x55, 0x66}, new Flags(true, true, true, true, 0x0F));

        this.assertDPT(new byte[]{0x22, 0x44, 0x11, (byte) 0xA3}, new byte[]{0x22, 0x44, 0x11}, new Flags(true, false, true, false, 0x03));

        this.assertDPT(new byte[]{0x33, 0x77, 0x00, (byte) 0x5C}, new byte[]{0x33, 0x77, 0x00}, new Flags(false, true, false, true, 0x0C));
    }

    /**
     * Asserts the DPT for given arguments {@code bValueArray} and {@code dptValue}
     *
     * @param bValueArray  byte array with values
     * @param accessIdData access identification data
     * @param flags        flags for access identification data
     */
    private void assertDPT(final byte[] bValueArray, final byte[] accessIdData, final Flags flags) {
        final var dpt = DPT_ACCESS_DATA;
        final var dptValue = dpt.toValue(accessIdData, flags);

        // assert base DPT
        this.assertBaseDPT(dpt, bValueArray, dptValue);
        // assert specific DPT15
        assertThat(dpt.toValue(ByteFormatter.formatHexAsString(accessIdData, ""),
                // get 2nd hex string (remove leading '0x'
                ByteFormatter.formatHex(flags.getAsByte()).substring(2))).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(accessIdData, flags)).containsExactly(bValueArray);
    }
}
