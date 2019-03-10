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
 * Tests the {@link TunnelingAckBody}
 *
 * @author PITSCHR
 */
public class TunnelingAckBodyTest {
    // prepare
    private int channelId;
    private int sequence;
    private Status status;

    @BeforeEach
    public void before() {
        this.channelId = 17;
        this.sequence = 129;
        this.status = Status.E_TUNNELING_LAYER;
    }

    /**
     * Tests the {@link TunnelingAckBody#create(int, int, Status)} and {@link TunnelingAckBody#valueOf(byte[])}
     * methods.
     *
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: TUNNELING_ACK (0x0421)
     * 	        Total Length: 10 octets
     * 	    Body
     * 	        Structure Length: 4 octets
     * 	        Communication Channel ID: 17
     * 	        Sequence Counter: 129
     * 	        Status: E_TUNNELING_LAYER - Requested KNX/IP Tunneling layer not supported (0x29)
     * </pre>
     */
    @Test
    public void validCases() {
        // create()
        final var body = TunnelingAckBody.create(this.channelId, this.sequence, this.status);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.TUNNELING_ACK);
        assertThat(body.getLength()).isEqualTo(4);
        assertThat(body.getChannelId()).isEqualTo(this.channelId);
        assertThat(body.getSequence()).isEqualTo(this.sequence);
        assertThat(body.getStatus()).isEqualTo(this.status);

        // compare raw data of create() with valueOf()
        final var bodyByBytes = TunnelingAckBody.valueOf(new byte[]{0x04, 0x11, (byte) 0x81, 0x29});
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format(
                "TunnelingAckBody{length=4 (0x04), channelId=17 (0x11), sequence=129 (0x81), status=%s, rawData=0x04 11 81 29}",
                Status.E_TUNNELING_LAYER));
    }

    /**
     * Tests {@link TunnelingAckBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> TunnelingAckBody.create(this.channelId, this.sequence, null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("status");

        // invalid size
        assertThatThrownBy(() -> TunnelingAckBody.create(-1, this.sequence, this.status)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");
        assertThatThrownBy(() -> TunnelingAckBody.create(0xFF + 1, this.sequence, this.status)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");
        assertThatThrownBy(() -> TunnelingAckBody.create(this.channelId, -1, this.status)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("sequence");
        assertThatThrownBy(() -> TunnelingAckBody.create(this.channelId, 0xFF + 1, this.status)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("sequence");

        // invalid raw data length
        assertThatThrownBy(() -> TunnelingAckBody.valueOf(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");
        assertThatThrownBy(() -> TunnelingAckBody.valueOf(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }
}
