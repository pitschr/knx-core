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
import li.pitschmann.knx.core.net.tunnel.ConnectionRequestInfo;
import li.pitschmann.knx.core.utils.Networker;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Tests the {@link ConnectRequestBody}
 *
 * @author PITSCHR
 */
class ConnectRequestBodyTest {

    /**
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: CONNECT_REQUEST (0x0205)
     * 	        Total Length: 26 octets
     * 		Body
     * 	    	HPAI: Discovery endpoint
     * 	   	     Structure Length: 8 octets
     * 	    	    Host Protocol Code: IPV4_UDP (0x01)
     * 	        	IP Address: 1.1.1.1 (1.1.1.1)
     * 	        	IP Port: 58702
     * 	 	   HPAI: Data endpoint
     * 	    	    Structure Length: 8 octets
     * 	        	Host Protocol Code: IPV4_UDP (0x01)
     * 	      	 	IP Address: 2.2.2.2 (2.2.2.2)
     * 	        	IP Port: 58703
     * 	    	Connection Request Information
     * 				Structure Length: 4 octets
     * 	        	Connection Type: TUNNEL_CONNECTION (0x04)
     * 	        	KNX Layer: TUNNEL_LINKLAYER (0x02)
     * 	        	reserved: 00
     * </pre>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(HPAI, HPAI, ConnectionRequestInformation)")
    void validCases() {
        // create by bytes
        final var bodyByBytes = ConnectRequestBody.of(new byte[]{
                // Control Endpoint (HPAI)
                0x08,                     // Structure Length
                0x01,                     // Host Protocol Code
                0x01, 0x01, 0x01, 0x01,   // IP Address
                (byte) 0xE5, (byte) 0x4E, // Port
                // Data Endpoint (HPAI)
                0x08,                     // Structure Length
                0x01,                     // Host Protocol Code
                0x02, 0x02, 0x02, 0x02,   // IP Address
                (byte) 0xE5, 0x4F,        // Port
                // Connection Request Information (CRI)
                0x04,                     // Structure Length
                0x04,                     // Connection Type
                0x02,                     // KNX Layer
                0x00                      // (not-used, reserved)
        });

        // create
        final var controlEndpoint = HPAI.of(HostProtocol.IPV4_UDP, Networker.getByAddress(1, 1, 1, 1), 58702);
        final var dataEndpoint = HPAI.of(HostProtocol.IPV4_UDP, Networker.getByAddress(2, 2, 2, 2), 58703);
        final var cri = ConnectionRequestInfo.useDefault();

        final var body = ConnectRequestBody.of(controlEndpoint, dataEndpoint, cri);
        assertThat(body.getServiceType()).isSameAs(ServiceType.CONNECT_REQUEST);
        assertThat(body.getControlEndpoint()).isSameAs(controlEndpoint);
        assertThat(body.getDataEndpoint()).isSameAs(dataEndpoint);
        assertThat(body.getConnectionRequestInformation()).isSameAs(cri);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(String.format(
                "ConnectRequestBody{controlEndpoint=%s, dataEndpoint=%s, connectionRequestInformation=%s}",
                controlEndpoint, dataEndpoint, cri)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        // invalid cases
        assertThatThrownBy(() -> ConnectRequestBody.of(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConnectRequestBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '20' but was: 0");
    }

    @Test
    @DisplayName("Invalid cases for #of(HPAI, HPAI, ConnectionRequestInformation)")
    void invalidCases_ofObjects() {
        // null
        assertThatThrownBy(() -> ConnectRequestBody.of(null, mock(HPAI.class), mock(ConnectionRequestInfo.class)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Control Endpoint is required.");
        assertThatThrownBy(() -> ConnectRequestBody.of(mock(HPAI.class), null, mock(ConnectionRequestInfo.class)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Data Endpoint is required.");
        assertThatThrownBy(() -> ConnectRequestBody.of(mock(HPAI.class), mock(HPAI.class), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Connection request information is required.");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ConnectRequestBody.class).verify();
    }

}
