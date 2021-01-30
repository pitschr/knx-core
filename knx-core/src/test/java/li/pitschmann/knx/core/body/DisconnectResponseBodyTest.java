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
import static org.mockito.Mockito.mock;

/**
 * Tests the {@link DisconnectResponseBody}
 *
 * @author PITSCHR
 */
class DisconnectResponseBodyTest {

    /**
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: DISCONNECT_RESPONSE (0x020a)
     * 	        Total Length: 8 octets
     * 	    Body
     * 	        Communication Channel ID: 11 (0x0B)
     * 	        Status: E_NO_MORE_CONNECTIONS - All connections already used (0x24)
     * </pre>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(int, Status)")
    void validCases() {
        // create by bytes
        final var bodyByBytes = DisconnectResponseBody.of(new byte[]{
                0x0B,  // Communication Channel ID
                0x24   // Status
        });

        // create
        final var channelId = 11;
        final var status = Status.NO_MORE_CONNECTIONS;

        final var body = DisconnectResponseBody.of(channelId, status);
        assertThat(body.getServiceType()).isSameAs(ServiceType.DISCONNECT_RESPONSE);
        assertThat(body.getChannelId()).isEqualTo(channelId);
        assertThat(body.getStatus()).isSameAs(status);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("DisconnectResponseBody{channelId=11, status=%s}", Status.NO_MORE_CONNECTIONS)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        assertThatThrownBy(() -> DisconnectResponseBody.of(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> DisconnectResponseBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '2' but was: 0");
    }

    @Test
    @DisplayName("Invalid cases for #of(int, Status)")
    void invalidCases_ofObjects() {
        // null
        assertThatThrownBy(() -> DisconnectResponseBody.of(0, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Status is required.");

        // invalid range
        assertThatThrownBy(() -> DisconnectResponseBody.of(-1, mock(Status.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: -1");
        assertThatThrownBy(() -> DisconnectResponseBody.of(0xFF + 1, mock(Status.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: 256");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DisconnectResponseBody.class).verify();
    }

}
