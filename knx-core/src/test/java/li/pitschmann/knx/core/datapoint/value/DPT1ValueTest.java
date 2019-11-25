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

package li.pitschmann.knx.core.datapoint.value;

import li.pitschmann.knx.core.datapoint.DPT1;
import li.pitschmann.knx.core.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DPT1Value}
 *
 * @author PITSCHR
 */
public final class DPT1ValueTest {
    /**
     * Test {@link DPT1Value}
     */
    @Test
    public void test() {
        this.assertValue(DPT1.BOOL, (byte) 0x00, false, "false");
        this.assertValue(DPT1.BOOL, (byte) 0x01, true, "true");

        this.assertValue(DPT1.SWITCH, (byte) 0x00, false, "off");
        this.assertValue(DPT1.SWITCH, (byte) 0x01, true, "on");
    }

    private void assertValue(final DPT1 dpt, final byte b, final boolean booleanValue, final String booleanText) {
        final var dptValue = new DPT1Value(dpt, booleanValue);
        final var dptValueByByte = new DPT1Value(dpt, b);

        // instance methods
        assertThat(dptValue.getBooleanValue()).isEqualTo(booleanValue);
        assertThat(dptValue.getBooleanText()).isEqualTo(booleanText);
        assertThat(dptValue.toByteArray()).containsExactly(b);
        assertThat(dptValue.toText()).isEqualTo(booleanText);

        // class methods
        assertThat(DPT1Value.toByteArray(booleanValue)).containsExactly(b);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT1Value(DPT1.ACK, booleanValue));
        assertThat(dptValue).isNotEqualTo(new DPT1Value(dpt, !booleanValue));

        // toString
        final var toString = String.format("DPT1Value{dpt=%s, booleanValue=%s, booleanText=%s, byteArray=%s}", dpt, booleanValue, booleanText,
                ByteFormatter.formatHex(b));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
