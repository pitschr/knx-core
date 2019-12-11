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

import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.HostProtocol;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.Networker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link ConnectionStateRequestBody}
 *
 * @author PITSCHR
 */
public class ConnectionStateRequestBodyTest {
    // prepare
    private int channelId;
    private HPAI controlEndpoint;

    @BeforeEach
    public void before() {
        this.channelId = 8;
        this.controlEndpoint = HPAI.of(HostProtocol.IPV4_UDP, Networker.getByAddress(4, 4, 4, 4), 58702);
    }

    /**
     * Tests the {@link ConnectionStateRequestBody#of(int, HPAI)} and
     * {@link ConnectionStateRequestBody#of(byte[])} methods.
     *
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: CONNECTION_STATE_REQUEST (0x0207)
     * 	        Total Length: 16 octets
     * 	    Body
     * 	        Communication Channel ID: 8
     * 	        reserved: 00
     * 	        HPAI: Control endpoint
     * 	            Structure Length: 8 octets
     * 	            Host Protocol Code: IPV4_UDP (0x01)
     * 	            IP Address: 4.4.4.4 (4.4.4.4)
     * 	            IP Port: 58702
     * </pre>
     */
    @Test
    public void validCases() {
        // create
        final var body = ConnectionStateRequestBody.of(this.channelId, this.controlEndpoint);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.CONNECTION_STATE_REQUEST);
        assertThat(body.getChannelId()).isEqualTo(this.channelId);
        assertThat(body.getControlEndpoint()).isEqualTo(this.controlEndpoint);

        // create by bytes
        final var bodyByBytes = ConnectionStateRequestBody
                .of(new byte[]{0x08, 0x00, 0x08, 0x01, 0x04, 0x04, 0x04, 0x04, (byte) 0xe5, 0x4e});

        // compare raw data of 'create' and 'create by bytes'
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(
                String.format("ConnectionStateRequestBody{channelId=8 (0x08), controlEndpoint=%s, rawData=0x08 00 08 01 04 04 04 04 E5 4E}",
                        this.controlEndpoint.toString(false)));
    }

    /**
     * Tests {@link ConnectionStateRequestBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> ConnectionStateRequestBody.of(this.channelId, null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("controlEndpoint");

        // invalid channel id
        assertThatThrownBy(() -> ConnectionStateRequestBody.of(-1, this.controlEndpoint)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");
        assertThatThrownBy(() -> ConnectionStateRequestBody.of(0xFF + 1, this.controlEndpoint)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");

        // invalid raw data length
        assertThatThrownBy(() -> ConnectionStateRequestBody.of(null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("rawData");
        assertThatThrownBy(() -> ConnectionStateRequestBody.of(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }
}
