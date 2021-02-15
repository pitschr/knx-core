/*
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

package li.pitschmann.knx.core.net.tunnel;

import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.net.ConnectionType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link ConnectionResponseData}
 *
 * @author PITSCHR
 */
final class ConnectionResponseDataTest {

    @Test
    @DisplayName("Test #of(byte[])")
    void testOf_Bytes() {
        final var bytes = new byte[]{
                ConnectionResponseData.STRUCTURE_LENGTH,  // Structure Length
                0x03,                                     // Connection Type DEVICE_MANAGEMENT_CONNECTION
                (byte) 0x74, 0x5C                         // Address
        };

        final var cri = ConnectionResponseData.of(bytes);
        assertThat(cri.getLength()).isEqualTo(ConnectionResponseData.STRUCTURE_LENGTH);
        assertThat(cri.getConnectionType()).isSameAs(ConnectionType.DEVICE_MANAGEMENT_CONNECTION);
        assertThat(cri.getAddress()).isEqualTo(IndividualAddress.of(7, 4, 92));
        assertThat(cri.toByteArray()).containsExactly(bytes);
    }

    @Test
    @DisplayName("Test #of(ConnectionType, IndividualAddress)")
    void testOf_ConnectionType_IndividualAddress() {
        final var address = IndividualAddress.of(13, 14, 15);
        final var cri = ConnectionResponseData.of(ConnectionType.OBJECT_SERVER_CONNECTION, address);
        assertThat(cri.getLength()).isEqualTo(ConnectionResponseData.STRUCTURE_LENGTH);
        assertThat(cri.getConnectionType()).isSameAs(ConnectionType.OBJECT_SERVER_CONNECTION);
        assertThat(cri.getAddress()).isEqualTo(address);

        assertThat(cri.toByteArray()).containsExactly(
                ConnectionResponseData.STRUCTURE_LENGTH,  // Structure Length
                0x08,                                     // Connection Type OBJECT_SERVER_CONNECTION
                (byte) 0xDE, 0x0F                         // Address
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_of_Bytes() {
        // null
        assertThatThrownBy(() -> ConnectionResponseData.of(null))
                .isInstanceOf(NullPointerException.class);

        // invalid structure
        assertThatThrownBy(() -> ConnectionResponseData.of(new byte[5]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '4' but was: 5");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        final var address = IndividualAddress.of(12, 3, 41);
        final var crd = ConnectionResponseData.of(ConnectionType.TUNNEL_CONNECTION, address);
        assertThat(crd).hasToString(
                String.format("ConnectionResponseData{length=4, connectionType=TUNNEL_CONNECTION, address=%s}",
                        address)
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ConnectionResponseData.class).verify();
    }

}
