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
 * Tests the {@link DisconnectRequestBody}
 *
 * @author PITSCHR
 */
public class DisconnectRequestBodyTest {
    // prepare
    private int channelId;
    private HPAI controlEndpoint;

    @BeforeEach
    public void before() throws UnknownHostException {
        this.channelId = 10;
        this.controlEndpoint = HPAI.of(HostProtocol.IPV4_UDP, InetAddress.getByName("5.5.5.5"), 58702);
    }

    /**
     * Tests the {@link DisconnectRequestBody#create(int, HPAI)} and {@link DisconnectRequestBody#valueOf(byte[])}
     * methods.
     *
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: DISCONNECT_REQUEST (0x0209)
     * 	        Total Length: 16 octets
     * 	    Body
     * 	        Communication Channel ID: 10
     * 	        reserved: 00
     * 	        HPAI: Control endpoint
     * 	            Structure Length: 8 octets
     * 	            Host Protocol Code: IPV4_UDP (0x01)
     * 	            IP Address: 5.5.5.5 (5.5.5.5)
     * 	            IP Port: 58702
     * </pre>
     */
    @Test
    public void validCases() {
        // create()
        final var body = DisconnectRequestBody.create(this.channelId, this.controlEndpoint);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.DISCONNECT_REQUEST);
        assertThat(body.getChannelId()).isEqualTo(this.channelId);
        assertThat(body.getControlEndpoint()).isEqualTo(this.controlEndpoint);

        // compare raw data of create() with valueOf()
        final var bodyByBytes = DisconnectRequestBody
                .valueOf(new byte[]{0x0A, 0x00, 0x08, 0x01, 0x05, 0x05, 0x05, 0x05, (byte) 0xe5, 0x4e});
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(
                String.format("DisconnectRequestBody{channelId=10 (0x0A), controlEndpoint=%s, rawData=0x0A 00 08 01 05 05 05 05 E5 4E}",
                        this.controlEndpoint.toString(false)));
    }

    /**
     * Tests {@link DisconnectRequestBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> DisconnectRequestBody.create(this.channelId, null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("controlEndpoint");

        // invalid channel id
        assertThatThrownBy(() -> DisconnectRequestBody.create(-1, this.controlEndpoint)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");
        assertThatThrownBy(() -> DisconnectRequestBody.create(0xFF + 1, this.controlEndpoint)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");

        // invalid raw data length
        assertThatThrownBy(() -> DisconnectRequestBody.valueOf(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");
        assertThatThrownBy(() -> DisconnectRequestBody.valueOf(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }
}
