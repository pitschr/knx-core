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

package li.pitschmann.knx.core.address;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link IndividualAddress}
 *
 * @author PITSCHR
 */
final class IndividualAddressTest {

    @Test
    @DisplayName("Test valid Individual Addresses")
    void testValid() {
        assertIndividualAddress(0, 0, 0, new byte[]{0x00, 0x00});
        assertIndividualAddress(8, 7, 128, new byte[]{(byte) 0x87, (byte) 0x80});
        assertIndividualAddress(15, 15, 255, new byte[]{(byte) 0xFF, (byte) 0xFF});
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidByteLength() {
        // null
        assertThatThrownBy(() -> IndividualAddress.of((byte[]) null))
                .isInstanceOf(NullPointerException.class);

        // address should have 2 bytes
        assertThatThrownBy(() -> IndividualAddress.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("2 Bytes is expected but got: []");
        assertThatThrownBy(() -> IndividualAddress.of(new byte[3]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("2 Bytes is expected but got: [0, 0, 0]");
    }

    @Test
    @DisplayName("Invalid cases for #of(String)")
    void invalidOfString() {
        // invalid format
        assertThatThrownBy(() -> IndividualAddress.of("1.2.3.4"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Invalid Individual Address provided: 1.2.3.4");
    }

    @Test
    @DisplayName("Invalid cases for #of(int, int, int)")
    void invalidAddresses() {
        // area, line or device out or range
        assertThatThrownBy(() -> IndividualAddress.of(-1, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid area provided. Expected [0..15] but was: -1");
        assertThatThrownBy(() -> IndividualAddress.of(0x0F + 1, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid area provided. Expected [0..15] but was: 16");

        assertThatThrownBy(() -> IndividualAddress.of(0, -1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid line provided. Expected [0..15] but was: -1");
        assertThatThrownBy(() -> IndividualAddress.of(0, 0x0F + 1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid line provided. Expected [0..15] but was: 16");

        assertThatThrownBy(() -> IndividualAddress.of(0, 0, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid device provided. Expected [0..255] but was: -1");
        assertThatThrownBy(() -> IndividualAddress.of(0, 0, 0xFF + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid device provided. Expected [0..255] but was: 256");
    }

    @Test
    @DisplayName("Test #useDefault()")
    void testUseDefault() {
        final var addr = IndividualAddress.useDefault();
        assertThat(addr.getAddress()).isEqualTo("0.0.0");
        assertThat(addr.getRawData()).containsExactly(new byte[2]);
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(IndividualAddress.class).verify();
    }

    /**
     * Asserts the {@link IndividualAddress} if area, line and device parameters are correctly parsed
     * <ul>
     * <li>{@link IndividualAddress#of(int, int, int)}</li>
     * <li>{@link IndividualAddress#of(byte[])}</li>
     * </ul>
     * <p>
     * The param {@code bytes} is the stream to be compared as well against other parameters.
     *
     * @param area  area of individual address
     * @param line  line of individual address
     * @param bytes byte array of individual address
     */
    private void assertIndividualAddress(final int area, final int line, final int device, final byte[] bytes) {
        final var testByCreate = IndividualAddress.of(area, line, device);
        final var testByCreateRawData = IndividualAddress.of(testByCreate.toByteArray());
        final var testByValueOfRawData = IndividualAddress.of(bytes);
        final var testByString = IndividualAddress.of(area + "." + line + "." + device);

        assertThat(testByCreate.toByteArray()).containsExactly(testByCreateRawData.toByteArray());
        assertThat(testByCreate.toByteArray()).containsExactly(testByValueOfRawData.toByteArray());
        assertThat(testByCreate.toByteArray()).containsExactly(testByString.toByteArray());

        // check address type
        assertThat(testByCreate.getAddressType()).isEqualTo(AddressType.INDIVIDUAL);
        assertThat(testByCreateRawData.getAddressType()).isEqualTo(AddressType.INDIVIDUAL);
        assertThat(testByValueOfRawData.getAddressType()).isEqualTo(AddressType.INDIVIDUAL);
        assertThat(testByString.getAddressType()).isEqualTo(AddressType.INDIVIDUAL);

        // check address print
        final var addressAsString = String.format("%s.%s.%s", area, line, device);
        assertThat(testByCreate.getAddress()).isEqualTo(addressAsString);
        assertThat(testByCreateRawData.getAddress()).isEqualTo(addressAsString);
        assertThat(testByValueOfRawData.getAddress()).isEqualTo(addressAsString);
        assertThat(testByString.getAddress()).isEqualTo(addressAsString);

        // toString
        assertThat(testByCreate).hasToString(
                String.format("IndividualAddress{address=%s}", testByCreate.getAddress())
        );
    }
}
