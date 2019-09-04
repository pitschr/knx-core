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

import li.pitschmann.knx.link.datapoint.DPT6;
import li.pitschmann.knx.link.datapoint.value.DPT6Value.StatusMode;
import li.pitschmann.knx.link.datapoint.value.DPT6Value.StatusMode.Mode;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT6Value}
 *
 * @author PITSCHR
 */
public final class DPT6ValueTest {
    /**
     * Test {@link DPT6Value}
     */
    @Test
    public void test() {
        this.assertValue(DPT6.VALUE_1_OCTET_COUNT, (byte) 0x29, 41, "41");
        this.assertValue(DPT6.VALUE_1_OCTET_COUNT, (byte) 0xEA, -22, "-22");

        this.assertValue(DPT6.PERCENT, (byte) 0x45, 69, "69");
    }

    private void assertValue(final DPT6 dpt, final byte b, final int relativeSignedValue, final String text) {
        final var dptValue = new DPT6Value(dpt, relativeSignedValue);
        final var dptValueByByte = new DPT6Value(dpt, b);

        // instance methods
        assertThat(dptValue.getRelativeSignedValue()).isEqualTo(relativeSignedValue);
        assertThat(dptValue.toByteArray()).containsExactly(b);
        assertThat(dptValue.toText()).isEqualTo(text);

        // class methods
        assertThat(DPT6Value.toByteArray(relativeSignedValue)).containsExactly(b);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT6Value(dpt, relativeSignedValue + 1));
        final var anotherDpt = DPT6.PERCENT.equals(dptValue.getDPT()) ? DPT6.VALUE_1_OCTET_COUNT : DPT6.PERCENT;
        assertThat(dptValue).isNotEqualTo(new DPT6Value(anotherDpt, relativeSignedValue));

        // toString
        final var toString = String.format("DPT6Value{dpt=%s, relativeSignedValue=%s, byteArray=%s}", dpt, relativeSignedValue,
                ByteFormatter.formatHex(b));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }

    /**
     * Test {@link DPT6Value.StatusMode}
     */
    @Test
    public void testStatusMode() {
        this.assertStatusMode((byte) 0x01, false, false, false, false, false, Mode.MODE_0);
        this.assertStatusMode((byte) 0x02, false, false, false, false, false, Mode.MODE_1);
        this.assertStatusMode((byte) 0x04, false, false, false, false, false, Mode.MODE_2);

        this.assertStatusMode((byte) 0x81, true, false, false, false, false, Mode.MODE_0);
        this.assertStatusMode((byte) 0x41, false, true, false, false, false, Mode.MODE_0);
        this.assertStatusMode((byte) 0x21, false, false, true, false, false, Mode.MODE_0);
        this.assertStatusMode((byte) 0x11, false, false, false, true, false, Mode.MODE_0);
        this.assertStatusMode((byte) 0x09, false, false, false, false, true, Mode.MODE_0);

    }

    private void assertStatusMode(final byte b, final boolean bool1, final boolean bool2, final boolean bool3, final boolean bool4,
                                  final boolean bool5, final Mode mode) {
        DPT6Value.StatusMode dptValue = new DPT6Value.StatusMode(bool1, bool2, bool3, bool4, bool5, mode);
        DPT6Value.StatusMode dptValueByByte = new DPT6Value.StatusMode(b);

        // instance methods
        assertThat(dptValue.getMode()).isEqualTo(mode);
        assertThat(dptValue.isSet(0)).isEqualTo(bool1);
        assertThat(dptValue.isSet(1)).isEqualTo(bool2);
        assertThat(dptValue.isSet(2)).isEqualTo(bool3);
        assertThat(dptValue.isSet(3)).isEqualTo(bool4);
        assertThat(dptValue.isSet(4)).isEqualTo(bool5);
        assertThat(dptValue.toByteArray()).containsExactly(b);

        // class methods
        assertThat(DPT6Value.StatusMode.toByte(bool1, bool2, bool3, bool4, bool5, mode)).isEqualTo(b);
        assertThat(DPT6Value.StatusMode.toByteArray(bool1, bool2, bool3, bool4, bool5, mode)).containsExactly(b);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        final var anotherByte = (byte) ((b & 0x80) == 0 ? b | 0x80 : b & 0x7F);
        final var anotherMode = (mode == Mode.MODE_0) ? Mode.MODE_1 : Mode.MODE_0;
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT6Value.StatusMode(anotherByte));
        assertThat(dptValue).isNotEqualTo(new DPT6Value.StatusMode(!bool1, bool2, bool3, bool4, bool5, mode));
        assertThat(dptValue).isNotEqualTo(new DPT6Value.StatusMode(bool1, !bool2, bool3, bool4, bool5, mode));
        assertThat(dptValue).isNotEqualTo(new DPT6Value.StatusMode(bool1, bool2, !bool3, bool4, bool5, mode));
        assertThat(dptValue).isNotEqualTo(new DPT6Value.StatusMode(bool1, bool2, bool3, !bool4, bool5, mode));
        assertThat(dptValue).isNotEqualTo(new DPT6Value.StatusMode(bool1, bool2, bool3, bool4, !bool5, mode));
        assertThat(dptValue).isNotEqualTo(new DPT6Value.StatusMode(bool1, bool2, bool3, bool4, bool5, anotherMode));
    }

    /**
     * Tests the failures of {@link StatusMode}
     */
    @Test
    public void testStatusModeFailure() {
        assertThatThrownBy(() -> new DPT6Value.StatusMode((byte) 0x03).getMode()).isInstanceOf(KnxEnumNotFoundException.class);

        DPT6Value.StatusMode statusModeValue = new DPT6Value.StatusMode((byte) 0x01);
        assertThatThrownBy(() -> statusModeValue.isSet(-1)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> statusModeValue.isSet(5)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }
}
