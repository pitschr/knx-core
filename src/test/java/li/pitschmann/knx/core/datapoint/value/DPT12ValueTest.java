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

import li.pitschmann.knx.core.datapoint.DPT12;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT12Value}
 *
 * @author PITSCHR
 */
class DPT12ValueTest {

    @Test
    @DisplayName("#(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, byte[]) with: 0")
    void testByte_0() {
        final var value = new DPT12Value(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, new byte[]{0x00, 0x00, 0x00, 0x00});
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, byte[]) with: 2836120313")
    void testByte_2836120313() {
        final var value = new DPT12Value(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, new byte[]{(byte) 0xA9, 0x0B, (byte) 0xC2, (byte) 0xF9});
        assertThat(value.getValue()).isEqualTo(2836120313L);
        assertThat(value.toByteArray()).containsExactly(0xA9, 0x0B, 0xC2, 0xF9);

        assertThat(value.toText()).isEqualTo("2836120313");
    }

    @Test
    @DisplayName("#(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, byte[]) with: 4294967295")
    void testByte_4294967295() {
        final var value = new DPT12Value(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertThat(value.getValue()).isEqualTo(4294967295L);
        assertThat(value.toByteArray()).containsExactly(0xFF, 0xFF, 0xFF, 0xFF);

        assertThat(value.toText()).isEqualTo("4294967295");
    }

    @Test
    @DisplayName("#(DPT12.TIME_SECONDS, long) with: 0")
    void testTime_0() {
        final var value = new DPT12Value(DPT12.TIME_SECONDS, 0);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT12.TIME_SECONDS, long) with: 833322")
    void testTime_833322() {
        final var value = new DPT12Value(DPT12.TIME_SECONDS, 833322);
        assertThat(value.getValue()).isEqualTo(833322);
        assertThat(value.toByteArray()).containsExactly(0x00, 0x0C, 0xB7, 0x2A);

        assertThat(value.toText()).isEqualTo("833322");
    }

    @Test
    @DisplayName("#(DPT12.TIME_SECONDS, long) with: 4294967295")
    void testTime_4294967295() {
        final var value = new DPT12Value(DPT12.TIME_SECONDS, 4294967295L);
        assertThat(value.getValue()).isEqualTo(4294967295L);
        assertThat(value.toByteArray()).containsExactly(0xFF, 0xFF, 0xFF, 0xFF);

        assertThat(value.toText()).isEqualTo("4294967295");
    }

    @Test
    @DisplayName("#(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, long) with numbers out of range")
    void testScalingOutOfRange() {
        assertThatThrownBy(() -> new DPT12Value(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, -1))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'value' is out of range '0'..'4294967295'.");
        assertThatThrownBy(() -> new DPT12Value(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, 4294967296L))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '4294967296' for argument 'value' is out of range '0'..'4294967295'.");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueUnsigned = new DPT12Value(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT, 72549213);
        assertThat(valueUnsigned).hasToString(
                "DPT12Value{dpt=12.001, value=72549213, byteArray=0x04 53 03 5D}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT12Value.class).verify();
    }
}
