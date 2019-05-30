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

import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link DisconnectResponseBody}
 *
 * @author PITSCHR
 */
public class DisconnectResponseBodyTest {
    // prepare
    private int channelId;
    private Status status;

    @BeforeEach
    public void before() {
        this.channelId = 11;
        this.status = Status.E_NO_MORE_CONNECTIONS;
    }

    /**
     * Tests the {@link DisconnectResponseBody#create(int, Status)} and {@link DisconnectResponseBody#valueOf(byte[])}
     * methods.
     *
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: DISCONNECT_RESPONSE (0x020a)
     * 	        Total Length: 8 octets
     * 	    Body
     * 	        Communication Channel ID: 11
     * 	        Status: E_NO_MORE_CONNECTIONS - All connections already used (0x24)
     * </pre>
     */
    @Test
    public void validCases() {
        // create
        final var body = DisconnectResponseBody.create(this.channelId, this.status);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.DISCONNECT_RESPONSE);
        assertThat(body.getChannelId()).isEqualTo(this.channelId);
        assertThat(body.getStatus()).isEqualTo(this.status);

        // compare raw data with valueOf(byte[])
        final var bodyByBytes = DisconnectResponseBody.valueOf(new byte[]{0x0B, 0x24});
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(
                String.format("DisconnectResponseBody{channelId=11 (0x0B), status=%s, rawData=0x0B 24}", Status.E_NO_MORE_CONNECTIONS));
    }

    /**
     * Tests {@link DisconnectResponseBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> DisconnectResponseBody.create(this.channelId, null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("status");

        // invalid channel id
        assertThatThrownBy(() -> DisconnectResponseBody.create(-1, this.status)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");
        assertThatThrownBy(() -> DisconnectResponseBody.create(0xFF + 1, this.status)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");

        // invalid raw data length
        assertThatThrownBy(() -> DisconnectResponseBody.valueOf(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");
        assertThatThrownBy(() -> DisconnectResponseBody.valueOf(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }
}
