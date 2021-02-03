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

import li.pitschmann.knx.core.datapoint.DPT9;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT9Value}
 *
 * @author PITSCHR
 */
class DPT9ValueTest {

    @Test
    @DisplayName("#(DPT9.TEMPERATURE, byte[]) with: 0")
    void testByteZero() {
        final var value = new DPT9Value(DPT9.TEMPERATURE, new byte[]{0x00, 0x00});
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT9.AIR_FLOW, byte[]) with: -66641.92")
    void testByteNegative() {
        final var value = new DPT9Value(DPT9.AIR_FLOW, new byte[]{(byte) 0xE1, (byte) 0xA5});
        assertThat(value.getValue()).isEqualTo(-66641.92);
        assertThat(value.toByteArray()).containsExactly(0xE1, 0xA5);

        assertThat(value.toText()).isEqualTo("-66641.92");
    }

    @Test
    @DisplayName("#(DPT9.TEMPERATURE, byte[]) with: 76185.6")
    void testBytePositive() {
        final var value = new DPT9Value(DPT9.TEMPERATURE, new byte[]{0x67, 0x44});
        assertThat(value.getValue()).isEqualTo(76185.6);
        assertThat(value.toByteArray()).containsExactly(0x67, 0x44);

        assertThat(value.toText()).isEqualTo("76185.6");
    }

    @Test
    @DisplayName("#(DPT9.TEMPERATURE, double) with numbers out of range")
    void testPercentRange() {
        assertThatThrownBy(() -> new DPT9Value(DPT9.TEMPERATURE, -327.681))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-327.681' for argument 'value' is out of range '-273.0'..'670760.96'.");
    }

    @Test
    @DisplayName("#(byte[]) with invalid byte length")
    void testBytesOutOfRange() {
        // expected: 2 bytes, provided 8 bytes
        assertThatThrownBy(() -> new DPT9Value(DPT9.TEMPERATURE, new byte[8]))
                .isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueTemperature = new DPT9Value(DPT9.TEMPERATURE, -249.34);
        assertThat(valueTemperature).hasToString(
                "DPT9Value{dpt=9.001, value=-249.34, byteArray=0xA1 EA}"
        );

        final var valueAirFlow = new DPT9Value(DPT9.AIR_FLOW, 538331.1245);
        assertThat(valueAirFlow).hasToString(
                "DPT9Value{dpt=9.009, value=538331.1245, byteArray=0x7E 6B}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT9Value.class).verify();
    }
}
