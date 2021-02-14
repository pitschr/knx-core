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

import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT15Value}
 *
 * @author PITSCHR
 */
class DPT15ValueTest {
    @Test
    @DisplayName("#(byte[]) with: 1-byte array for access data and 1-byte for flag")
    void test1ByteAccessData_1ByteFlag() {
        final var value = new DPT15Value(new byte[]{0x01, 0x02});
        assertThat(value.getAccessIdentificationData()).containsExactly(0x00, 0x00, 0x01);
        assertThat(value.getFlags()).isEqualTo(new DPT15Value.Flags((byte) 0x02));
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0x01, 0x02);

        assertThat(value.toText()).isEqualTo("Data: 0x00 00 01, Flags: 0x02");
    }

    @Test
    @DisplayName("#(byte[]) with: 2-byte array for access data and 1-byte for flag")
    void test2BytesAccessData_1ByteFlag() {
        final var value = new DPT15Value(new byte[]{0x01, 0x02, 0x03});
        assertThat(value.getAccessIdentificationData()).containsExactly(0x00, 0x01, 0x02);
        assertThat(value.getFlags()).isEqualTo(new DPT15Value.Flags((byte) 0x03));
        assertThat(value.toByteArray()).containsExactly(0x00, 0x01, 0x02, 0x03);

        assertThat(value.toText()).isEqualTo("Data: 0x00 01 02, Flags: 0x03");
    }

    @Test
    @DisplayName("#(byte[]) with: 3-byte array for access data and 1-byte for flag")
    void test3BytesAccessData_1ByteFlag() {
        final var value = new DPT15Value(new byte[]{0x01, 0x02, 0x03, 0x04});
        assertThat(value.getAccessIdentificationData()).containsExactly(0x01, 0x02, 0x03);
        assertThat(value.getFlags()).isEqualTo(new DPT15Value.Flags((byte) 0x04));
        assertThat(value.toByteArray()).containsExactly(0x01, 0x02, 0x03, 0x04);

        assertThat(value.toText()).isEqualTo("Data: 0x01 02 03, Flags: 0x04");
    }

    @Test
    @DisplayName("#(byte[], Flags) with: 1-byte array for access data")
    void test1ByteAccessData_FlagObject() {
        final var value = new DPT15Value(new byte[]{0x01}, new DPT15Value.Flags((byte) 0x02));
        assertThat(value.getAccessIdentificationData()).containsExactly(0x00, 0x00, 0x01);
        assertThat(value.getFlags()).isEqualTo(new DPT15Value.Flags((byte) 0x02));
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00, 0x01, 0x02);

        assertThat(value.toText()).isEqualTo("Data: 0x00 00 01, Flags: 0x02");
    }

    @Test
    @DisplayName("#(byte[], Flags) with: 2-byte array for access data")
    void test2BytesAccessData_FlagObject() {
        final var value = new DPT15Value(new byte[]{0x01, 0x02}, new DPT15Value.Flags((byte) 0x03));
        assertThat(value.getFlags()).isEqualTo(new DPT15Value.Flags((byte) 0x03));
        assertThat(value.getAccessIdentificationData()).containsExactly(0x00, 0x01, 0x02);
        assertThat(value.toByteArray()).containsExactly(0x00, 0x01, 0x02, 0x03);

        assertThat(value.toText()).isEqualTo("Data: 0x00 01 02, Flags: 0x03");
    }

    @Test
    @DisplayName("#(byte[]) with invalid byte length")
    void testBytesOutOfRange() {
        // expected: 2-4 bytes, provided 5 bytes
        assertThatThrownBy(() -> new DPT15Value(new byte[5]))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '5' for argument 'bytes' is out of range '2'..'4'. Raw Data: 0x00 00 00 00 00");
    }

    @Test
    @DisplayName("#(byte[], Flags) with: 3-byte array for access data")
    void test3BytesAccessData_FlagObject() {
        final var value = new DPT15Value(new byte[]{0x01, 0x02, 0x03}, new DPT15Value.Flags((byte) 0x04));
        assertThat(value.getAccessIdentificationData()).containsExactly(0x01, 0x02, 0x03);
        assertThat(value.toByteArray()).containsExactly(0x01, 0x02, 0x03, 0x04);

        assertThat(value.toText()).isEqualTo("Data: 0x01 02 03, Flags: 0x04");
    }

    @Test
    @DisplayName("#(byte[], Flags) with invalid length for Access Identification Data")
    void testAccessIdentificationDataOutOfRange() {
        // expected: 1, 2 or 3-byte array
        assertThatThrownBy(() -> new DPT15Value(new byte[0], new DPT15Value.Flags((byte) 0x00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Access Identification Data must be 3 bytes or less: []");
        assertThatThrownBy(() -> new DPT15Value(new byte[4], new DPT15Value.Flags((byte) 0x00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Access Identification Data must be 3 bytes or less: [0, 0, 0, 0]");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var flags = new DPT15Value.Flags((byte) 0x99);
        final var value = new DPT15Value(new byte[]{0x11}, flags);
        assertThat(value).hasToString(
                String.format(
                        "DPT15Value{dpt=15.000, accessIdentificationData=0x00 00 11, flags=%s, byteArray=0x00 00 11 99}", flags
                )
        );

        final var value2 = new DPT15Value(new byte[]{0x11, 0x22}, flags);
        assertThat(value2).hasToString(
                String.format(
                        "DPT15Value{dpt=15.000, accessIdentificationData=0x00 11 22, flags=%s, byteArray=0x00 11 22 99}", flags
                )
        );

        final var value3 = new DPT15Value(new byte[]{0x11, 0x22, 0x33}, flags);
        assertThat(value3).hasToString(
                String.format(
                        "DPT15Value{dpt=15.000, accessIdentificationData=0x11 22 33, flags=%s, byteArray=0x11 22 33 99}", flags
                )
        );

        final var valueBytes = new DPT15Value(new byte[]{0x11, 0x22, 0x33, (byte) 0x99});
        assertThat(valueBytes).hasToString(
                String.format(
                        "DPT15Value{dpt=15.000, accessIdentificationData=0x11 22 33, flags=%s, byteArray=0x11 22 33 99}", flags
                )
        );
    }

    @Test
    @DisplayName("Flags#(byte) with: flags=1001 ...., index = 4")
    void testFlagsCase1() {
        final var flags = new DPT15Value.Flags(true, false, false, true, 4);
        assertThat(flags.isError()).isTrue();
        assertThat(flags.isPermissionAccepted()).isFalse();
        assertThat(flags.isReadDirectionRightToLeft()).isFalse();
        assertThat(flags.isEncryptionEnabled()).isTrue();
        assertThat(flags.getIndex()).isEqualTo(4);

        assertThat(flags.getAsByte()).isEqualTo((byte) 0b1001_0100);
    }

    @Test
    @DisplayName("Flags#(byte) with: flags=0110 ...., index = 11")
    void testFlagsCase2() {
        final var flags = new DPT15Value.Flags(false, true, true, false, 11);
        assertThat(flags.isError()).isFalse();
        assertThat(flags.isPermissionAccepted()).isTrue();
        assertThat(flags.isReadDirectionRightToLeft()).isTrue();
        assertThat(flags.isEncryptionEnabled()).isFalse();
        assertThat(flags.getIndex()).isEqualTo(11);

        assertThat(flags.getAsByte()).isEqualTo((byte) 0b0110_1011);
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT15Value.class).withIgnoredFields("dpt").verify();
        EqualsVerifier.forClass(DPT15Value.Flags.class).verify();
    }
}
