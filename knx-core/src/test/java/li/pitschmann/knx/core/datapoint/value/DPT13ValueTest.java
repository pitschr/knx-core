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

import li.pitschmann.knx.core.datapoint.DPT13;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT13Value}
 *
 * @author PITSCHR
 */
class DPT13ValueTest {

    @Test
    @DisplayName("#(DPT13.VALUE_4_OCTET_COUNT, byte[]) with: 0")
    void testByteZero() {
        final var value = new DPT13Value(DPT13.VALUE_4_OCTET_COUNT, new byte[]{0x00, 0x00, 0x00, 0x00});
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT13.VALUE_4_OCTET_COUNT, byte[]) with: -2147483648")
    void testByteNegative() {
        final var value = new DPT13Value(DPT13.VALUE_4_OCTET_COUNT, new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00});
        assertThat(value.getValue()).isEqualTo(-2147483648);
        assertThat(value.toByteArray()).containsExactly(0x80, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("-2147483648");
    }

    @Test
    @DisplayName("#(DPT13.VALUE_4_OCTET_COUNT, byte[]) with: 2147483647")
    void testBytePositive() {
        final var value = new DPT13Value(DPT13.VALUE_4_OCTET_COUNT, new byte[]{(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertThat(value.getValue()).isEqualTo(2147483647);
        assertThat(value.toByteArray()).containsExactly(0x7F, 0xFF, 0xFF, 0xFF);

        assertThat(value.toText()).isEqualTo("2147483647");
    }

    @Test
    @DisplayName("#(DPT13.FLOW_RATE, int) with: 0")
    void testIntegerZero() {
        final var value = new DPT13Value(DPT13.FLOW_RATE, 0);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT13.FLOW_RATE, int) with: -2147483648")
    void testIntegerNegative() {
        final var value = new DPT13Value(DPT13.FLOW_RATE, -2147483648);
        assertThat(value.getValue()).isEqualTo(-2147483648);
        assertThat(value.toByteArray()).containsExactly(0x80, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("-2147483648");
    }

    @Test
    @DisplayName("#(DPT13.FLOW_RATE, int) with: 2147483647")
    void testIntegerPositive() {
        final var value = new DPT13Value(DPT13.FLOW_RATE, 2147483647);
        assertThat(value.getValue()).isEqualTo(2147483647);
        assertThat(value.toByteArray()).containsExactly(0x7F, 0xFF, 0xFF, 0xFF);

        assertThat(value.toText()).isEqualTo("2147483647");
    }

    @Disabled // cannot be tested yet as there is no DPT13 type available with a smaller number range
    @Test
    @DisplayName("#(DPT13.VALUE_4_OCTET_COUNT, int) with numbers out of range")
    void testValueOutOfRange() {
        assertThatThrownBy(() -> new DPT13Value(DPT13.VALUE_4_OCTET_COUNT, -214748364))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-214748364' for argument 'value' is out of range '-2147483648'..'2147483647'.");
        assertThatThrownBy(() -> new DPT13Value(DPT13.VALUE_4_OCTET_COUNT, 2147483647))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '2147483647' for argument 'value' is out of range '-2147483648'..'2147483647'.");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueSigned = new DPT13Value(DPT13.VALUE_4_OCTET_COUNT, -9529462);
        assertThat(valueSigned).hasToString(
                "DPT13Value{dpt=13.001, value=-9529462, byteArray=0xFF 6E 97 8A}"
        );

        final var valuePercent = new DPT13Value(DPT13.FLOW_RATE, 32934237);
        assertThat(valuePercent).hasToString(
                "DPT13Value{dpt=13.002, value=32934237, byteArray=0x01 F6 89 5D}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT13Value.class).verify();
    }
}
