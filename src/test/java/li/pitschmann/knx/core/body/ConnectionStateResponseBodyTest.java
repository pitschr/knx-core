/*
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
 * Tests the {@link ConnectionStateResponseBody}
 *
 * @author PITSCHR
 */
class ConnectionStateResponseBodyTest {

    /**
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: CONNECTION_STATE_RESPONSE (0x0208)
     * 	        Total Length: 8 octets
     * 	    Body
     * 	        Communication Channel ID: 17 (0x11)
     * 	        Status: E_SEQUENCE_NUMBER (0x04)
     * </pre>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(int, Status)")
    public void validCases() {
        // create by bytes
        final var bodyByBytes = ConnectionStateResponseBody.of(new byte[]{
                0x11, // Communication Channel ID
                0x04  // Status
        });

        // create
        final var channelId = 17;
        final var status = Status.SEQUENCE_NUMBER;

        final var body = ConnectionStateResponseBody.of(channelId, status);
        assertThat(body.getServiceType()).isSameAs(ServiceType.CONNECTION_STATE_RESPONSE);
        assertThat(body.getChannelId()).isEqualTo(channelId);
        assertThat(body.getStatus()).isSameAs(status);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("ConnectionStateResponseBody{channelId=17, status=%s}", status)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        // invalid cases
        assertThatThrownBy(() -> ConnectionStateResponseBody.of(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConnectionStateResponseBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '2' but was: 0");
    }

    @Test
    @DisplayName("Invalid cases for #of(int, Status)")
    void invalidCases_ofObjects() {
        // null
        assertThatThrownBy(() -> ConnectionStateResponseBody.of(0, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Status is required.");

        // invalid channel id
        assertThatThrownBy(() -> ConnectionStateResponseBody.of(-1, mock(Status.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: -1");

        assertThatThrownBy(() -> ConnectionStateResponseBody.of(0xFF + 1, mock(Status.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: 256");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ConnectionStateResponseBody.class).verify();
    }

}
