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

package li.pitschmann.knx.link.body;

import li.pitschmann.knx.link.body.hpai.HPAI;
import li.pitschmann.knx.link.body.hpai.HostProtocol;
import li.pitschmann.knx.link.body.tunnel.ConnectionRequestInformation;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link ConnectRequestBody}
 *
 * @author PITSCHR
 */
public class ConnectRequestBodyTest {
    // prepare
    private HPAI controlEndpoint;
    private HPAI dataEndpoint;
    private ConnectionRequestInformation cri;

    @BeforeEach
    public void before() throws UnknownHostException {
        this.controlEndpoint = HPAI.of(HostProtocol.IPV4_UDP, InetAddress.getByName("1.1.1.1"), 58702);
        this.dataEndpoint = HPAI.of(HostProtocol.IPV4_UDP, InetAddress.getByName("2.2.2.2"), 58703);
        this.cri = ConnectionRequestInformation.create();
    }

    /**
     * Tests the {@link ConnectRequestBody#create(HPAI, HPAI, ConnectionRequestInformation)} and
     * {@link ConnectRequestBody#valueOf(byte[])} methods.
     *
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
    public void validCases() {
        // create()
        final ConnectRequestBody body = ConnectRequestBody.create(this.controlEndpoint, this.dataEndpoint, this.cri);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.CONNECT_REQUEST);
        assertThat(body.getControlEndpoint()).isEqualTo(this.controlEndpoint);
        assertThat(body.getDataEndpoint()).isEqualTo(this.dataEndpoint);
        assertThat(body.getConnectionRequestInformation()).isEqualTo(this.cri);

        // compare raw data of create() with valueOf()
        final ConnectRequestBody bodyByBytes = ConnectRequestBody.valueOf(new byte[]{0x08, 0x01, 0x01, 0x01, 0x01, 0x01, (byte) 0xe5, (byte) 0x4e,
                0x08, 0x01, 0x02, 0x02, 0x02, 0x02, (byte) 0xe5, 0x4f, 0x04, 0x04, 0x02, 0x00});
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format(
                "ConnectRequestBody{controlEndpoint=%s, dataEndpoint=%s, connectionRequestInformation=%s, rawData=0x08 01 01 01 01 01 E5 4E 08 01 02 02 02 02 E5 4F 04 04 02 00}",
                this.controlEndpoint.toString(false), this.dataEndpoint.toString(false), this.cri.toString(false)));
    }

    /**
     * Tests {@link ConnectRequestBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> ConnectRequestBody.create(null, this.dataEndpoint, this.cri)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("controlEndpoint");
        assertThatThrownBy(() -> ConnectRequestBody.create(this.controlEndpoint, null, this.cri)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("dataEndpoint");
        assertThatThrownBy(() -> ConnectRequestBody.create(this.controlEndpoint, this.dataEndpoint, null))
                .isInstanceOf(KnxNullPointerException.class).hasMessageContaining("cri");

        // invalid raw data length
        assertThatThrownBy(() -> ConnectRequestBody.valueOf(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");
        assertThatThrownBy(() -> ConnectRequestBody.valueOf(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }

    /**
     * Test {@link ConnectRequestBody#toString()}
     */
    @Test
    public void testToString() {

    }
}
