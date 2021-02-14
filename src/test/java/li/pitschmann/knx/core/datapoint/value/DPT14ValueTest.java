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

import li.pitschmann.knx.core.datapoint.DPT14;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT14Value}
 *
 * @author PITSCHR
 */
class DPT14ValueTest {
    @Test
    @DisplayName("#(DPT14.ACCELERATION, byte[]) with: 0")
    void testByteZero() {
        final var value = new DPT14Value(DPT14.ACCELERATION, new byte[]{0x00, 0x00, 0x00, 0x00});
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT14.ACCELERATION, byte[]) with: -65434.925781")
    void testByteNegative() {
        final var value = new DPT14Value(DPT14.ACCELERATION, new byte[]{(byte) 0xC7, 0x7F, (byte) 0x9A, (byte) 0xED});
        assertThat(value.getValue()).isCloseTo(-65434.925781, Offset.offset(0.00001));
        assertThat(value.toByteArray()).containsExactly(0xC7, 0x7F, 0x9A, 0xED);

        assertThat(value.toText()).isEqualTo("-65434.925781");
    }

    @Test
    @DisplayName("#(DPT14.ACCELERATION, byte[]) with: 792006492160")
    void testBytePositive() {
        final var value = new DPT14Value(DPT14.ACCELERATION, new byte[]{0x53, 0x38, 0x67, 0x44});
        assertThat(value.getValue()).isEqualTo(7.9200649216E11);
        assertThat(value.toByteArray()).containsExactly(0x53, 0x38, 0x67, 0x44);

        assertThat(value.toText()).isEqualTo("792006492160");
    }

    @Test
    @DisplayName("#(DPT14.ACCELERATION, double) with numbers out of range")
    void testValueOutOfRange() {
        assertThatThrownBy(() -> new DPT14Value(DPT14.ACCELERATION, -3.40282348E38))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-3.40282348E38' for argument 'value' is out of range '-3.40282347E38'..'3.40282347E38'.");
        assertThatThrownBy(() -> new DPT14Value(DPT14.ACCELERATION, 3.40282348E38))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '3.40282348E38' for argument 'value' is out of range '-3.40282347E38'..'3.40282347E38'.");
    }

    @Test
    @DisplayName("#(byte[]) with invalid byte length")
    void testBytesOutOfRange() {
        // expected: 4 bytes, provided 8 bytes
        assertThatThrownBy(() -> new DPT14Value(DPT14.ACCELERATION, new byte[8]))
                .isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueAcceleration = new DPT14Value(DPT14.ACCELERATION, -33444.32);
        assertThat(valueAcceleration).hasToString(
                "DPT14Value{dpt=14.000, value=-33444.32, byteArray=0xC7 02 A4 52}"
        );

        final var valueEnergy = new DPT14Value(DPT14.ENERGY, 7.33266933E11);
        assertThat(valueEnergy).hasToString(
                "DPT14Value{dpt=14.031, value=733266933000, byteArray=0x53 2A BA 1D}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT14Value.class).verify();
    }
}
