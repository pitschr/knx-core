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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.HostProtocol;
import li.pitschmann.knx.core.net.tunnel.ConnectionResponseData;
import li.pitschmann.knx.core.utils.Networker;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Tests the {@link ConnectResponseBody}
 *
 * @author PITSCHR
 */
class ConnectResponseBodyTest {

    /**
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: CONNECT_RESPONSE (0x0206)
     * 	        Total Length: 20 octets
     * 	    Body
     * 	        Communication Channel ID: 7
     * 	        Status: E_NO_ERROR (0x00)
     * 	        HPAI: Data endpoint
     * 	            Structure Length: 8 octets
     * 	            Host Protocol Code: IPV4_UDP (0x01)
     * 	            IP Address: 3.3.3.3 (3.3.3.3)
     * 	            IP Port: 3671
     * 	        Connection Response Data Block
     * 	            Structure Length: 4 octets
     * 	            Connection Type: TUNNEL_CONNECTION (0x04)
     * 	            KNX Address 15.15.242
     * </pre>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(int, Status, HPAI, ConnectionResponseData)")
    public void validCases() {
        // create by bytes
        final var bodyByBytes = ConnectResponseBody
                .of(new byte[]{
                        0x07,                     // Channel ID
                        0x00,                     // Status
                        // Data Endpoint (HPAI)
                        0x08,                     // Structure Length
                        0x01,                     // Host Protocol Code
                        0x03, 0x03, 0x03, 0x03,   // IP Address
                        0x0e, 0x57,               // IP Port
                        // Connection Response Data
                        0x04,                     // Structure Length
                        0x04,                     // Connection Type
                        (byte) 0xff, (byte) 0xf2  // KNX Address
                });

        // create
        final var channelId = 7;
        final var status = Status.NO_ERROR;
        final var dataEndpoint = HPAI.of(HostProtocol.IPV4_UDP, Networker.getByAddress(3, 3, 3, 3), 3671);
        final var crd = ConnectionResponseData.of(IndividualAddress.of(15, 15, 242));

        final var body = ConnectResponseBody.of(channelId, status, dataEndpoint, crd);
        assertThat(body.getServiceType()).isSameAs(ServiceType.CONNECT_RESPONSE);
        assertThat(body.getChannelId()).isEqualTo(channelId);
        assertThat(body.getStatus()).isSameAs(status);
        assertThat(body.getDataEndpoint()).isSameAs(dataEndpoint);
        assertThat(body.getConnectionResponseData()).isSameAs(crd);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("ConnectResponseBody{channelId=7, status=%s, dataEndpoint=%s, connectionResponseData=%s}", status, dataEndpoint, crd)
        );
    }

    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(int, Status, HPAI, ConnectionResponseData) with error")
    void validCases_with_error() {
        // create by bytes
        final var bodyByBytes = ConnectResponseBody.of(new byte[]{
                0x0F, // Channel ID
                0x01  // Status (HOST_PROTOCOL_TYPE)
        });

        // create
        final var body = ConnectResponseBody.of(15, Status.HOST_PROTOCOL_TYPE, null, null);
        assertThat(body.getChannelId()).isEqualTo(15);
        assertThat(body.getStatus()).isSameAs(Status.HOST_PROTOCOL_TYPE);
        assertThat(body.getDataEndpoint()).isNull();
        assertThat(body.getConnectionResponseData()).isNull();

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("ConnectResponseBody{channelId=15, status=%s, dataEndpoint=null, connectionResponseData=null}", Status.HOST_PROTOCOL_TYPE)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        // invalid cases
        assertThatThrownBy(() -> ConnectResponseBody.of(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConnectResponseBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [2,14] but was: 0");
    }

    @Test
    @DisplayName("Invalid cases for #of(int, Status, HPAI, ConnectionResponseData)")
    void invalidCases_ofObjects() {
        // null
        assertThatThrownBy(() -> ConnectResponseBody.of(0, null, mock(HPAI.class), mock(ConnectionResponseData.class)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Status is required.");

        // null (depending on status)
        assertThatThrownBy(() -> ConnectResponseBody.of(0, Status.NO_ERROR, null, mock(ConnectionResponseData.class)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Data Endpoint is required.");
        assertThatThrownBy(() -> ConnectResponseBody.of(0, Status.NO_ERROR, mock(HPAI.class), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Connection response data is required.");

        // invalid range
        assertThatThrownBy(() -> ConnectResponseBody.of(-1, mock(Status.class), mock(HPAI.class), mock(ConnectionResponseData.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: -1");
        assertThatThrownBy(() -> ConnectResponseBody.of(0xFF + 1, mock(Status.class), mock(HPAI.class), mock(ConnectionResponseData.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: 256");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ConnectResponseBody.class).verify();
    }

}
