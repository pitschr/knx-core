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

import li.pitschmann.knx.core.header.ServiceType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link TunnelingAckBody}
 *
 * @author PITSCHR
 */
class TunnelingAckBodyTest {
    /**
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: TUNNELING_ACK (0x0421)
     * 	        Total Length: 10 octets
     * 	    Body
     * 	        Structure Length: 4 octets (0x04)
     * 	        Communication Channel ID: 17 (0x11)
     * 	        Sequence Counter: 129 (0x81)
     * 	        Status: E_TUNNELING_LAYER - Requested KNX/IP Tunneling layer not supported (0x29)
     * </pre>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(int, int, Status)")
    void validCases() {
        // create by bytes
        final var bodyByBytes = TunnelingAckBody.of(new byte[]{
                0x04,        // Structure Length
                0x11,        // Communication Channel ID
                (byte) 0x81, // Sequence Counter
                0x29         // Status
        });

        // create
        final var channelId = 17;
        final var sequence = 129;
        final var status = Status.TUNNELING_LAYER;

        final var body = TunnelingAckBody.of(channelId, sequence, status);
        assertThat(body.getServiceType()).isSameAs(ServiceType.TUNNELING_ACK);
        assertThat(body.getLength()).isEqualTo(4);
        assertThat(body.getChannelId()).isEqualTo(channelId);
        assertThat(body.getSequence()).isEqualTo(sequence);
        assertThat(body.getStatus()).isSameAs(status);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("TunnelingAckBody{length=4, channelId=17, sequence=129, status=%s}", Status.TUNNELING_LAYER)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        // invalid cases
        assertThatThrownBy(() -> TunnelingAckBody.of(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> TunnelingAckBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '4' but was: 0");
    }

    @Test
    @DisplayName("Invalid cases for #of(int, int, Status)")
    void invalidCases_ofObjects() {
        // null
        assertThatThrownBy(() -> TunnelingAckBody.of(0, 0, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Status is required.");

        // invalid range
        assertThatThrownBy(() -> TunnelingAckBody.of(-1, 0, Status.NO_ERROR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: -1");
        assertThatThrownBy(() -> TunnelingAckBody.of(0xFF + 1, 0, Status.NO_ERROR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: 256");

        assertThatThrownBy(() -> TunnelingAckBody.of(0, -1, Status.NO_ERROR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible sequence. Expected [0..255] but was: -1");
        assertThatThrownBy(() -> TunnelingAckBody.of(0, 0xFF + 1, Status.NO_ERROR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible sequence. Expected [0..255] but was: 256");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(TunnelingAckBody.class).verify();
    }

}
