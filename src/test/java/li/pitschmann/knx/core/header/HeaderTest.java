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

package li.pitschmann.knx.core.header;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.Status;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link Header}
 *
 * @author PITSCHR
 */
final class HeaderTest {
    @Test
    @DisplayName("Valid cases with #of(byte[]) with body bytes")
    void testHeaders() {
        final var header = Header.of(new byte[]{
                Header.STRUCTURE_LENGTH,
                Header.PROTOCOL_VERSION_V1,
                0x02, 0x03,       // Service Type DESCRIPTION_REQUEST
                0x00, (byte) 0xFF // Total Length
        });

        assertThat(header.getLength()).isEqualTo(Header.STRUCTURE_LENGTH);
        assertThat(header.getProtocolVersion()).isEqualTo(Header.PROTOCOL_VERSION_V1);
        assertThat(header.getServiceType()).isSameAs(ServiceType.DESCRIPTION_REQUEST);
        assertThat(header.getTotalLength()).isEqualTo(255);
        assertThat(header).hasToString(
                "Header{length=6, protocolVersion=16, serviceType=DESCRIPTION_REQUEST, totalLength=255}"
        );
    }

    @Test
    @DisplayName("Valid cases with #of(byte[]) with body bytes")
    void testHeadersWithBodyBytes() {
        final var header = Header.of(new byte[]{
                Header.STRUCTURE_LENGTH,
                Header.PROTOCOL_VERSION_V1,
                0x02, 0x07, // Service Type CONNECTION_STATE_REQUEST
                0x00, 0x10, // Length (6 header bytes + 10 body bytes = 16 bytes
                // Connection State Request Body
                0x11,                                 // Communication Channel ID
                0x00,                                 // reserved
                0x08,                                 // HPAI Structure Length
                0x01,                                 // HPAI Host Protocol Code
                (byte) 0xC0, (byte) 0xA8, 0x01, 0x18, // HPAI IP Address
                (byte) 0xE1, (byte) 0xA9              // HPAI IP Port
        });

        assertThat(header.getLength()).isEqualTo(Header.STRUCTURE_LENGTH);
        assertThat(header.getProtocolVersion()).isEqualTo(Header.PROTOCOL_VERSION_V1);
        assertThat(header.getServiceType()).isSameAs(ServiceType.CONNECTION_STATE_REQUEST);
        assertThat(header.getTotalLength()).isEqualTo(16);
        assertThat(header).hasToString(
                "Header{length=6, protocolVersion=16, serviceType=CONNECTION_STATE_REQUEST, totalLength=16}"
        );
    }

    @Test
    @DisplayName("Valid cases with #of(Body) and #of(ServiceType, int)")
    void testHeadersWithBody() {
        final var body = TunnelingAckBody.of(0x33, 0x66, Status.TUNNELING_LAYER);

        // create header by body -> will construct the header based on Tunneling Ack Body and its byte array
        final var header = Header.of(body);

        assertThat(header.toByteArray()).containsExactly(
                Header.STRUCTURE_LENGTH,       // Header Length
                Header.PROTOCOL_VERSION_V1, // Protocol Version
                0x04, 0x21,                 // Service Type for TUNNELING_ACK
                0x00, 0x0A                  // Header Length (6 bytes) + Tunneling Ack Body Length (4 bytes) = 10 bytes
        );
    }

    @Test
    @DisplayName("Invalid cases with #of(byte[])")
    void testInvalid_ofBytes() {
        // test with illegal bytes
        assertThatThrownBy(() -> Header.of((byte[]) null)).isInstanceOf(NullPointerException.class);
        // test with empty bytes
        assertThatThrownBy(() -> Header.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible header structure length. Expected [6..]' but was: 0");
        // invalid header length (1st byte = 0xFF)
        assertThatThrownBy(() -> Header.of(new byte[]{(byte) 0xFF, 0x00, 0x02, 0x02, 0x00, 0x00}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible header length. Expected [6] but was: 255");
        // invalid protocol version (2nd byte = 0xFF)
        assertThatThrownBy(() -> Header.of(new byte[]{Header.STRUCTURE_LENGTH, (byte) 0xFF, 0x02, 0x02, 0x00, 0x00}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible protocol version. Expected [16] but was: 255");
    }

    @Test
    @DisplayName("Invalid cases with #of(Body)")
    void testInvalid_ofBody() {
        // test with no body
        assertThatThrownBy(() -> Header.of((Body) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Body is required.");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(Header.class).verify();
    }

}
