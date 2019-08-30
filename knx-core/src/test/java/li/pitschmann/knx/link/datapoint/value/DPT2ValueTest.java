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

package li.pitschmann.knx.link.datapoint.value;

import li.pitschmann.knx.link.datapoint.DPT2;
import li.pitschmann.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DPT2Value}
 *
 * @author PITSCHR
 */
public final class DPT2ValueTest {
    /**
     * Test {@link DPT2Value}
     */
    @Test
    public void test() {
        this.assertValue(DPT2.BOOL_CONTROL, (byte) 0x00, false, false, "false", "false");
        this.assertValue(DPT2.BOOL_CONTROL, (byte) 0x01, false, true, "true", "true");
        this.assertValue(DPT2.BOOL_CONTROL, (byte) 0x02, true, false, "false", "controlled 'false'");
        this.assertValue(DPT2.BOOL_CONTROL, (byte) 0x03, true, true, "true", "controlled 'true'");

        this.assertValue(DPT2.SWITCH_CONTROL, (byte) 0x00, false, false, "off", "off");
        this.assertValue(DPT2.SWITCH_CONTROL, (byte) 0x01, false, true, "on", "on");
        this.assertValue(DPT2.SWITCH_CONTROL, (byte) 0x02, true, false, "off", "controlled 'off'");
        this.assertValue(DPT2.SWITCH_CONTROL, (byte) 0x03, true, true, "on", "controlled 'on'");
    }

    private void assertValue(final DPT2 dpt, final byte b, final boolean controlled, final boolean booleanValue, final String booleanText, final String text) {
        final var dptValue = new DPT2Value(dpt, controlled, booleanValue);
        final var dptValueByByte = new DPT2Value(dpt, b);

        // instance methods
        assertThat(dptValue.isControlled()).isEqualTo(controlled);
        assertThat(dptValue.getBooleanValue()).isEqualTo(booleanValue);
        assertThat(dptValue.getBooleanText()).isEqualTo(booleanText);
        assertThat(dptValue.toByteArray()).containsExactly(b);
        assertThat(dptValue.toText()).isEqualTo(text);

        // class methods
        assertThat(DPT2Value.toByteArray(controlled, booleanValue)).containsExactly(b);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT2Value(DPT2.ALARM_CONTROL, controlled, booleanValue));
        assertThat(dptValue).isNotEqualTo(new DPT2Value(dpt, controlled, !booleanValue));
        assertThat(dptValue).isNotEqualTo(new DPT2Value(dpt, !controlled, booleanValue));

        // toString
        final var toString = String.format("DPT2Value{dpt=%s, controlled=%s, booleanValue=%s, booleanText=%s, byteArray=%s}", dpt, controlled,
                booleanValue, booleanText, ByteFormatter.formatHex(b));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
