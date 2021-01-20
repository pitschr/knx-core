/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2021 Pitschmann Christoph
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

import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT8Value.Percent}
 *
 * @author PITSCHR
 */
class DPT8ValuePercentTest {

    @Test
    @DisplayName("#(byte[]) with: 0x00 00")
    void testByte_0x00_00_Percent() {
        final var value = new DPT8Value.Percent(new byte[]{0x00, 0x00});
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(byte[]) with: 0x80 00")
    void testByte_0x80_00_Percent() {
        final var value = new DPT8Value.Percent(new byte[]{(byte) 0x80, (byte) 0x00});
        assertThat(value.getValue()).isEqualTo(-327.68);
        assertThat(value.toByteArray()).containsExactly(0x80, 0x00);

        assertThat(value.toText()).isEqualTo("-327.68");
    }

    @Test
    @DisplayName("#(byte[]) with: 0x7F FF")
    void testByte_0x7F_FF_Percent() {
        final var value = new DPT8Value.Percent(new byte[]{(byte) 0x7F, (byte)0xFF});
        assertThat(value.getValue()).isEqualTo(327.67);
        assertThat(value.toByteArray()).containsExactly(0x7F, 0xFF);

        assertThat(value.toText()).isEqualTo("327.67");
    }

    @Test
    @DisplayName("#(double) with: 0")
    void testInt_Zero_Percent() {
        final var value = new DPT8Value.Percent(0);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(double) with: -327.68")
    void testInt_Negative_Percent() {
        final var value = new DPT8Value.Percent(-327.68);
        assertThat(value.getValue()).isEqualTo(-327.68);
        assertThat(value.toByteArray()).containsExactly(0x80, 0x00);

        assertThat(value.toText()).isEqualTo("-327.68");
    }

    @Test
    @DisplayName("#(double) with: 327.67%")
    void testInt_Positive_Percent() {
        final var value = new DPT8Value.Percent(327.67);
        assertThat(value.getValue()).isEqualTo(327.67);
        assertThat(value.toByteArray()).containsExactly(0x7F, 0xFF);

        assertThat(value.toText()).isEqualTo("327.67");
    }

    @Test
    @DisplayName("#(double) with numbers out of range")
    void testPercentRange() {
        assertThatThrownBy(() -> new DPT8Value.Percent(-327.681))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-327.681' for argument 'value' is out of range '-327.68'..'327.67'.");
        assertThatThrownBy(() -> new DPT8Value.Percent(327.671))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '327.671' for argument 'value' is out of range '-327.68'..'327.67'.");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueBytes = new DPT8Value.Percent(new byte[]{0x56, 0x78});
        assertThat(valueBytes).hasToString(
                "DPT8Value$Percent{dpt=8.010, value=221.36, byteArray=0x56 78}"
        );

        final var valuePercent = new DPT8Value.Percent(134.23);
        assertThat(valuePercent).hasToString(
                "DPT8Value$Percent{dpt=8.010, value=134.23, byteArray=0x34 6F}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT8Value.Percent(47.11);
        final var valueBytes = new DPT8Value.Percent(new byte[]{0x12, 0x67});

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(valueBytes).isEqualTo(value);
        assertThat(valueBytes).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT8Value.Percent(11.47));
    }
}
