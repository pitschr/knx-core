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

import li.pitschmann.knx.core.datapoint.DPT8;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT8Value}
 *
 * @author PITSCHR
 */
class DPT8ValueTest {

    @Test
    @DisplayName("#(DPT8.VALUE_2_OCTET_COUNT, byte[]) with: 0")
    void testByteZero() {
        final var value = new DPT8Value(DPT8.VALUE_2_OCTET_COUNT, new byte[]{0x00, 0x00});
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT8.VALUE_2_OCTET_COUNT, byte[]) with: -32768")
    void testByteNegative() {
        final var value = new DPT8Value(DPT8.VALUE_2_OCTET_COUNT, new byte[]{(byte) 0x80, (byte) 0x00});
        assertThat(value.getValue()).isEqualTo(-32768);
        assertThat(value.toByteArray()).containsExactly(0x80, 0x00);

        assertThat(value.toText()).isEqualTo("-32768");
    }

    @Test
    @DisplayName("#(DPT8.VALUE_2_OCTET_COUNT, byte[]) with: 32767")
    void testBytePositive() {
        final var value = new DPT8Value(DPT8.VALUE_2_OCTET_COUNT, new byte[]{(byte) 0x7F, (byte)0xFF});
        assertThat(value.getValue()).isEqualTo(32767);
        assertThat(value.toByteArray()).containsExactly(0x7F, 0xFF);

        assertThat(value.toText()).isEqualTo("32767");
    }

    @Test
    @DisplayName("#(DPT8.DELTA_TIME_100MS, int) with: 0")
    void testDeltaTimeZero() {
        final var value = new DPT8Value(DPT8.DELTA_TIME_100MS, 0);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT8.DELTA_TIME_100MS, int) with: -3276800")
    void testDeltaTimeNegative() {
        final var value = new DPT8Value(DPT8.DELTA_TIME_100MS, -3276800);
        assertThat(value.getValue()).isEqualTo(-3276800);
        assertThat(value.toByteArray()).containsExactly(0x80, 0x00);

        assertThat(value.toText()).isEqualTo("-3276800");
    }

    @Test
    @DisplayName("#(DPT8.DELTA_TIME_100MS, int) with: 3276700")
    void testDeltaTimePositive() {
        final var value = new DPT8Value(DPT8.DELTA_TIME_100MS, 3276700);
        assertThat(value.getValue()).isEqualTo(3276700);
        assertThat(value.toByteArray()).containsExactly(0x7F, 0xFF);

        assertThat(value.toText()).isEqualTo("3276700");
    }

    @Test
    @DisplayName("#(DPT8.PERCENT, double) with: 0")
    void testPercentZero() {
        final var value = new DPT8Value(DPT8.PERCENT, 0);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT8.PERCENT, double) with: -327.68")
    void testPercentNegative() {
        final var value = new DPT8Value(DPT8.PERCENT, -327.68);
        assertThat(value.getValue()).isEqualTo(-327.68);
        assertThat(value.toByteArray()).containsExactly(0x80, 0x00);

        assertThat(value.toText()).isEqualTo("-327.68");
    }

    @Test
    @DisplayName("#(DPT8.PERCENT, double) with: 327.67%")
    void testPercentPositive() {
        final var value = new DPT8Value(DPT8.PERCENT, 327.67);
        assertThat(value.getValue()).isEqualTo(327.67);
        assertThat(value.toByteArray()).containsExactly(0x7F, 0xFF);

        assertThat(value.toText()).isEqualTo("327.67");
    }

    @Test
    @DisplayName("#(DPT8.VALUE_2_OCTET_COUNT, int) with numbers out of range")
    void testValueOutOfRange() {
        assertThatThrownBy(() -> new DPT8Value(DPT8.VALUE_2_OCTET_COUNT, -32769))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-32769.0' for argument 'value' is out of range '-32768.0'..'32767.0'.");
        assertThatThrownBy(() -> new DPT8Value(DPT8.VALUE_2_OCTET_COUNT, 32768))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '32768.0' for argument 'value' is out of range '-32768.0'..'32767.0'.");
    }

    @Test
    @DisplayName("#(DPT8.PERCENT, double) with numbers out of range")
    void testPercentRange() {
        assertThatThrownBy(() -> new DPT8Value(DPT8.PERCENT, -327.681))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-327.681' for argument 'value' is out of range '-327.68'..'327.67'.");
        assertThatThrownBy(() -> new DPT8Value(DPT8.PERCENT, 327.671))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '327.671' for argument 'value' is out of range '-327.68'..'327.67'.");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueSigned = new DPT8Value(DPT8.VALUE_2_OCTET_COUNT, -8382);
        assertThat(valueSigned).hasToString(
                "DPT8Value{dpt=8.001, value=-8382.0, byteArray=0xDF 42}"
        );

        final var valuePercent = new DPT8Value(DPT8.PERCENT, 134.23);
        assertThat(valuePercent).hasToString(
                "DPT8Value{dpt=8.010, value=134.23, byteArray=0x34 6F}"
        );

        final var valueDeltaTime = new DPT8Value(DPT8.DELTA_TIME_100MS, 2733200);
        assertThat(valueDeltaTime).hasToString(
                "DPT8Value{dpt=8.004, value=2733200.0, byteArray=0x6A C4}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT8Value(DPT8.VALUE_2_OCTET_COUNT, 4711);
        final var value2 = new DPT8Value(DPT8.VALUE_2_OCTET_COUNT, 4711);

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(value2).isEqualTo(value);
        assertThat(value2).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT8Value(DPT8.PERCENT, 47.11));
        assertThat(value).isNotEqualTo(new DPT8Value(DPT8.VALUE_2_OCTET_COUNT, 1147));
    }
}
