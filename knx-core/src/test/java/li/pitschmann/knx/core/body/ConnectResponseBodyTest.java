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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.HostProtocol;
import li.pitschmann.knx.core.net.tunnel.ConnectionResponseData;
import li.pitschmann.knx.core.utils.Networker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link ConnectResponseBody}
 *
 * @author PITSCHR
 */
public class ConnectResponseBodyTest {
    // prepare
    private int channelId;
    private Status status;
    private HPAI dataEndpoint;
    private ConnectionResponseData crd;

    @BeforeEach
    public void before() {
        this.channelId = 7;
        this.status = Status.E_NO_ERROR;
        this.dataEndpoint = HPAI.of(HostProtocol.IPV4_UDP, Networker.getByAddress(3, 3, 3, 3), 3671);
        this.crd = ConnectionResponseData.of(IndividualAddress.of(15, 15, 242));
    }

    /**
     * Tests the {@link ConnectResponseBody#of(int, Status, HPAI, ConnectionResponseData)} and
     * {@link ConnectResponseBody#of(byte[])} methods.
     *
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: CONNECT_RESPONSE (0x0206)
     * 	        Total Length: 20 octets
     * 	    Body
     * 	        Communication Channel ID: 7
     * 	        Status: E_CONNECTION_TYPE - Connection type not supported (0x22)
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
    public void validCases() {
        // create
        final var body = ConnectResponseBody.of(this.channelId, this.status, this.dataEndpoint, this.crd);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.CONNECT_RESPONSE);
        assertThat(body.getChannelId()).isEqualTo(this.channelId);
        assertThat(body.getStatus()).isEqualTo(this.status);
        assertThat(body.getDataEndpoint()).isEqualTo(this.dataEndpoint);
        assertThat(body.getConnectionResponseData()).isEqualTo(this.crd);

        // create by bytes
        final var bodyByBytes = ConnectResponseBody
                .of(new byte[]{0x07, 0x00, 0x08, 0x01, 0x03, 0x03, 0x03, 0x03, 0x0e, 0x57, 0x04, 0x04, (byte) 0xff, (byte) 0xf2});

        // compare raw data of 'create' and 'create by bytes'
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format(
                "ConnectResponseBody{channelId=7 (0x07), status=%s, dataEndpoint=%s, connectionResponseData=%s, rawData=0x07 00 08 01 03 03 03 03 0E 57 04 04 FF F2}",
                this.status, this.dataEndpoint.toString(false), this.crd.toString(false)));
    }

    @Test
    public void validCaseErrorStatus() {
        // create
        final var body = ConnectResponseBody.of(10, Status.E_NO_MORE_CONNECTIONS, null, null);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.CONNECT_RESPONSE);
        assertThat(body.getChannelId()).isEqualTo(0x0A);
        assertThat(body.getStatus()).isEqualTo(Status.E_NO_MORE_CONNECTIONS);
        assertThat(body.getDataEndpoint()).isNull();
        assertThat(body.getConnectionResponseData()).isNull();

        // create by bytes
        final var bodyByBytes = ConnectResponseBody.of(new byte[]{0x0A, 0x24});

        // compare raw data of 'create' and 'create by bytes'
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format(
                "ConnectResponseBody{channelId=10 (0x0A), status=%s, dataEndpoint=null, connectionResponseData=null, rawData=0x0A 24}",
                Status.E_NO_MORE_CONNECTIONS));
    }

    /**
     * Tests {@link ConnectResponseBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> ConnectResponseBody.of(this.channelId, null, this.dataEndpoint, this.crd))
                .isInstanceOf(KnxNullPointerException.class).hasMessageContaining("status");
        assertThatThrownBy(() -> ConnectResponseBody.of(this.channelId, this.status, null, this.crd)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("dataEndpoint");
        assertThatThrownBy(() -> ConnectResponseBody.of(this.channelId, this.status, this.dataEndpoint, null))
                .isInstanceOf(KnxNullPointerException.class).hasMessageContaining("crd");

        // invalid channel id
        assertThatThrownBy(() -> ConnectResponseBody.of(-1, this.status, this.dataEndpoint, this.crd))
                .isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("channelId");
        assertThatThrownBy(() -> ConnectResponseBody.of(0xFF + 1, this.status, this.dataEndpoint, this.crd))
                .isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("channelId");

        // invalid raw data length
        assertThatThrownBy(() -> ConnectResponseBody.of(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");
        assertThatThrownBy(() -> ConnectResponseBody.of(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }
}
