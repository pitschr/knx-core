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

package li.pitschmann.knx.core.net.tunnel;

import li.pitschmann.knx.core.net.ConnectionType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link ConnectionRequestInfo}
 *
 * @author PITSCHR
 */
final class ConnectionRequestInfoTest {

    @Test
    @DisplayName("Test #useDefault()")
    void testUseDefault() {
        final var criDefault = ConnectionRequestInfo.useDefault();
        assertThat(criDefault.getLength()).isEqualTo(ConnectionRequestInfo.STRUCTURE_LENGTH);
        assertThat(criDefault.getConnectionType()).isSameAs(ConnectionType.TUNNEL_CONNECTION);
        assertThat(criDefault.getLayerType()).isSameAs(LayerType.TUNNEL_LINKLAYER);

        assertThat(criDefault.toByteArray()).containsExactly(
                ConnectionRequestInfo.STRUCTURE_LENGTH,  // Structure Length
                0x04,                                           // Connection Type
                0x02,                                           // Layer Type
                0x00                                            // (reserved)
        );
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testOf_Bytes() {
        final var bytes = new byte[]{
                ConnectionRequestInfo.STRUCTURE_LENGTH,  // Structure Length
                0x07,                                           // Connection Type REMOTE_CONFIG_CONNECTION
                (byte) 0x80,                                    // Layer Type TUNNEL_BUSMONITOR
                0x00                                            // (reserved)
        };

        final var cri = ConnectionRequestInfo.of(bytes);
        assertThat(cri.getLength()).isEqualTo(ConnectionRequestInfo.STRUCTURE_LENGTH);
        assertThat(cri.getConnectionType()).isSameAs(ConnectionType.REMOTE_CONFIG_CONNECTION);
        assertThat(cri.getLayerType()).isSameAs(LayerType.TUNNEL_BUSMONITOR);
        assertThat(cri.toByteArray()).containsExactly(bytes);
    }

    @Test
    @DisplayName("Test #of(ConnectionType, LayerType)")
    void testOf_ConnectionType_LayerType() {
        final var cri = ConnectionRequestInfo.of(ConnectionType.OBJECT_SERVER_CONNECTION, LayerType.TUNNEL_LINKLAYER);
        assertThat(cri.getLength()).isEqualTo(ConnectionRequestInfo.STRUCTURE_LENGTH);
        assertThat(cri.getConnectionType()).isSameAs(ConnectionType.OBJECT_SERVER_CONNECTION);
        assertThat(cri.getLayerType()).isSameAs(LayerType.TUNNEL_LINKLAYER);

        assertThat(cri.toByteArray()).containsExactly(
                ConnectionRequestInfo.STRUCTURE_LENGTH,  // Structure Length
                0x08,                                           // Connection Type
                0x02,                                           // Layer Type
                0x00                                            // (reserved)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_of_Bytes() {
        // null
        assertThatThrownBy(() -> ConnectionRequestInfo.of(null))
                .isInstanceOf(NullPointerException.class);

        // invalid structure
        assertThatThrownBy(() -> ConnectionRequestInfo.of(new byte[5]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '4' but was: 5");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        final var criDefault = ConnectionRequestInfo.useDefault();
        assertThat(criDefault).hasToString(
                "ConnectionRequestInfo{length=4, connectionType=TUNNEL_CONNECTION, layerType=TUNNEL_LINKLAYER}"
        );

        final var cri = ConnectionRequestInfo.of(ConnectionType.DEVICE_MANAGEMENT_CONNECTION, LayerType.TUNNEL_RAW);
        assertThat(cri).hasToString(
                "ConnectionRequestInfo{length=4, connectionType=DEVICE_MANAGEMENT_CONNECTION, layerType=TUNNEL_RAW}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ConnectionRequestInfo.class).verify();
    }

}
