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
 * Tests the {@link ConnectionStateResponseBody}
 *
 * @author PITSCHR
 */
public class ConnectionStateResponseBodyTest {
    // prepare
    private int channelId;
    private Status status;

    @BeforeEach
    public void before() {
        this.channelId = 9;
        this.status = Status.E_SEQUENCE_NUMBER;
    }

    /**
     * Tests the {@link ConnectionStateResponseBody#of(int, Status)} and
     * {@link ConnectionStateResponseBody#of(byte[])} methods.
     *
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: CONNECTION_STATE_RESPONSE (0x0208)
     * 	        Total Length: 8 octets
     * 	    Body
     * 	        Communication Channel ID: 17
     * 	        Status: E_NO_ERROR - The connection state is normal (0x00)
     * </pre>
     */
    @Test
    public void validCases() {
        // create
        final var body = ConnectionStateResponseBody.of(this.channelId, this.status);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.CONNECTION_STATE_RESPONSE);
        assertThat(body.getChannelId()).isEqualTo(this.channelId);
        assertThat(body.getStatus()).isEqualTo(this.status);

        // create by bytes
        final var bodyByBytes = ConnectionStateResponseBody.of(new byte[]{0x09, 0x04});

        // compare raw data of 'create' and 'create by bytes'
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format("ConnectionStateResponseBody{channelId=9 (0x09), status=%s, rawData=0x09 04}", this.status));
    }

    /**
     * Tests {@link ConnectionStateResponseBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> ConnectionStateResponseBody.of(this.channelId, null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("status");

        // invalid channel id
        assertThatThrownBy(() -> ConnectionStateResponseBody.of(-1, this.status)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");
        assertThatThrownBy(() -> ConnectionStateResponseBody.of(0xFF + 1, this.status)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");

        // invalid raw data length
        assertThatThrownBy(() -> ConnectionStateResponseBody.of(null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("rawData");
        assertThatThrownBy(() -> ConnectionStateResponseBody.of(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }
}
