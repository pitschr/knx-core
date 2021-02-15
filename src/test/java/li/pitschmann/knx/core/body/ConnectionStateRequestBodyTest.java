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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.HostProtocol;
import li.pitschmann.knx.core.utils.Networker;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Tests the {@link ConnectionStateRequestBody}
 *
 * @author PITSCHR
 */
class ConnectionStateRequestBodyTest {

    /**
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: CONNECTION_STATE_REQUEST (0x0207)
     * 	        Total Length: 16 octets
     * 	    Body
     * 	        Communication Channel ID: 8 (0x08)
     * 	        reserved: 00 (0x00)
     * 	        HPAI: Control endpoint
     * 	            Structure Length: 8 octets
     * 	            Host Protocol Code: IPV4_UDP (0x01)
     * 	            IP Address: 4.4.4.4 (4.4.4.4)
     * 	            IP Port: 58702
     * </pre>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(int, HPAI)")
    void validCases() {
        // create by bytes
        final var bodyByBytes = ConnectionStateRequestBody
                .of(new byte[]{
                        0x08, // Communication Channel ID
                        0x00, // reserved
                        // HPAI -- START --
                        0x08,                   // Structure Length
                        0x01,                   // Host Protocol Code
                        0x04, 0x04, 0x04, 0x04, // IP Address
                        (byte) 0xe5, 0x4e       // Port
                        // HPAI --  END  --
                });

        // create
        final var channelId = 8;
        final var controlEndpoint = HPAI.of(HostProtocol.IPV4_UDP, Networker.getByAddress(4, 4, 4, 4), 58702);

        final var body = ConnectionStateRequestBody.of(channelId, controlEndpoint);
        assertThat(body.getServiceType()).isSameAs(ServiceType.CONNECTION_STATE_REQUEST);
        assertThat(body.getChannelId()).isEqualTo(channelId);
        assertThat(body.getControlEndpoint()).isSameAs(controlEndpoint);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("ConnectionStateRequestBody{channelId=8, controlEndpoint=%s}", controlEndpoint)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        // invalid cases
        assertThatThrownBy(() -> ConnectionStateRequestBody.of(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConnectionStateRequestBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '10' but was: 0");
    }

    @Test
    @DisplayName("Invalid cases for #of(int, HPAI)")
    void invalidCases_ofObjects() {
        // null
        assertThatThrownBy(() -> ConnectionStateRequestBody.of(0, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Control Endpoint is required.");

        // invalid channel id
        assertThatThrownBy(() -> ConnectionStateRequestBody.of(-1, mock(HPAI.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: -1");

        assertThatThrownBy(() -> ConnectionStateRequestBody.of(0xFF + 1, mock(HPAI.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: 256");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ConnectionStateRequestBody.class).verify();
    }

}
