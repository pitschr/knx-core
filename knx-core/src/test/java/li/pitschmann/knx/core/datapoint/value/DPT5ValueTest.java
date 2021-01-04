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

import li.pitschmann.knx.core.datapoint.DPT5;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT5Value}
 *
 * @author PITSCHR
 */
public final class DPT5ValueTest {

    @Test
    public void test() {
        this.assertValue(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, (byte) 0x29, 41, "41");
        this.assertValue(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, (byte) 0x85, 133, "133");

        this.assertValue(DPT5.ANGLE, (byte) 0x00, 0, "0");
        this.assertValue(DPT5.ANGLE, (byte) 0x1C, 40, "40");
        this.assertValue(DPT5.ANGLE, (byte) 0x7F, 180, "180");
        this.assertValue(DPT5.ANGLE, (byte) 0xFF, 360, "360");

        this.assertValue(DPT5.SCALING, (byte) 0x00, 0, "0");
        this.assertValue(DPT5.SCALING, (byte) 0x2D, 18, "18");
        this.assertValue(DPT5.SCALING, (byte) 0xFF, 100, "100");
    }

    @Test
    @DisplayName("Test #(DPT5, int) with numbers out of range")
    void testConstructorOutOfRange() {
        // range: 0..255
        assertThatThrownBy(() -> new DPT5Value(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, -1))
                .isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> new DPT5Value(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, 256))
                .isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    private void assertValue(final DPT5 dpt, final byte b, final int value, final String text) {
        final var dptValue = new DPT5Value(dpt, value);
        final var dptValue2 = new DPT5Value(dpt, value);

        // instance methods
        assertThat(dptValue.getValue()).isEqualTo(value);
        assertThat(dptValue.toByteArray()).containsExactly(b);
        assertThat(dptValue.toText()).isEqualTo(text);

        // payload can be optimized?
        assertThat(dptValue).isNotInstanceOf(PayloadOptimizable.class);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValue2).isEqualTo(dptValue);
        assertThat(dptValue2).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT5Value(DPT5.PERCENT_U8, 3));
        assertThat(dptValue).isNotEqualTo(new DPT5Value(dpt, value == 0 ? 1 : value - 1));

        // toString
        final var toString = String.format("DPT5Value{dpt=%s, value=%s, byteArray=%s}", dpt,
                value, ByteFormatter.formatHex(b));
        assertThat(dptValue).hasToString(toString);
    }
}
