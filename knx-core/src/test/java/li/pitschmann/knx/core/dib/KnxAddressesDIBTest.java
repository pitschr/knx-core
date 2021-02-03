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

package li.pitschmann.knx.core.dib;

import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.utils.Bytes;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link KnxAddressesDIB}
 *
 * @author PITSCHR
 */
final class KnxAddressesDIBTest {

    @Test
    @DisplayName("Test #of(byte[])")
    void testOf_Bytes() {
        final var bytes = new byte[]{ //
                0x0A,                       // Structure Length
                0x05,                       // Description Type Code
                0x00, 0x00,                 // KNX Individual Address (0.0.0)
                0x12, 0x03,                 // Additional Individual Address 1 (1.2.3)
                0x78, 0x7F,                 // Additional Individual Address 2 (7.8.127)
                (byte) 0xFF, (byte) 0xFF,   // Additional Individual Address 3 (15.15.255)
        };
        final var dibByBytes = KnxAddressesDIB.of(bytes);

        // compare
        assertThat(dibByBytes.getAddress()).isEqualTo(IndividualAddress.of(0, 0, 0));
        assertThat(dibByBytes.toByteArray()).containsExactly(bytes);

        final var additionalAddresses = dibByBytes.getAdditionalAddresses();
        assertThat(additionalAddresses).hasSize(3);
        assertThat(additionalAddresses.get(0).getAddress()).isEqualTo("1.2.3");
        assertThat(additionalAddresses.get(1).getAddress()).isEqualTo("7.8.127");
        assertThat(additionalAddresses.get(2).getAddress()).isEqualTo("15.15.255");

        assertThat(dibByBytes).hasToString(
                "KnxAddressesDIB{address=0.0.0, additionalAddresses=[1.2.3, 7.8.127, 15.15.255]}"
        );
    }

    @Test
    @DisplayName("Test #of(byte[]) without additional addresses")
    void testOf_Bytes_Without_AdditionalAddresses() {
        final var bytes = new byte[]{ //
                0x04,        // Structure Length
                0x05,        // Description Type Code
                0x38, 0x5C   // KNX Individual Address (3.8.92)
        };
        final var dibByBytes = KnxAddressesDIB.of(bytes);

        // compare
        assertThat(dibByBytes.getAddress()).isEqualTo(IndividualAddress.of(3, 8, 92));
        assertThat(dibByBytes.getAdditionalAddresses()).isEmpty();
        assertThat(dibByBytes.toByteArray()).containsExactly(bytes);

        assertThat(dibByBytes).hasToString(
                "KnxAddressesDIB{address=3.8.92, additionalAddresses=[]}"
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_of_Bytes() {
        // null
        assertThatThrownBy(() -> KnxAddressesDIB.of(null))
                .isInstanceOf(NullPointerException.class);

        // specific for KNX addresses DIB
        assertThatThrownBy(() -> KnxAddressesDIB.of(new byte[]{0x05, 0x00, 0x00, 0x00, 0x00}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Length must be divisible by 2, but was: 5");

        // incorrect size of bytes
        assertThatThrownBy(() -> KnxAddressesDIB.of(new byte[]{0x03, 0x02, 0x01}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [4..254] but was: 3");
        assertThatThrownBy(() -> KnxAddressesDIB.of(Bytes.padRight(new byte[]{(byte) 0xFF}, (byte) 0x00, 255)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [4..254] but was: 255");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(KnxAddressesDIB.class).verify();
    }

}
