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

import li.pitschmann.knx.core.exceptions.KnxUnknownBodyException;
import li.pitschmann.knx.core.header.Header;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link BodyFactory}
 *
 * @author PITSCHR
 */
class BodyFactoryTest {

    @Test
    @DisplayName("Constructor not instantiable")
    void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(BodyFactory.class);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: TUNNELING_REQUEST")
    void testOf_TunnelingRequest() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x04, 0x20, // Tunneling Request
                0x00, 0x17, // Total Length Octets
                // Body
                0x04, 0x11, 0x5c, 0x00, 0x29, 0x00, (byte) 0xbc, (byte) 0xe0, 0x10, (byte) 0xa0, 0x4c, 0x07, 0x03, 0x00, (byte) 0x80, 0x0c, 0x3f};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.TUNNELING_REQUEST);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: TUNNELING_ACK")
    void testOf_TunnelingAck() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x04, 0x21, // Tunneling Ack
                0x00, 0x0A, // Total Length Octets
                // Body
                0x04, 0x11, (byte) 0x81, 0x29};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.TUNNELING_ACK);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: ROUTING_INDICATION")
    void testOf_RoutingIndication() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x05, 0x30, // Routing Indication
                0x00, 0x13, // Total Length Octets
                // Body
                0x29, 0x00, (byte) 0xbc, (byte) 0xe0, 0x10, (byte) 0xa0, 0x4c, 0x07, 0x03, 0x00, (byte) 0x80, 0x0c, 0x3f};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.ROUTING_INDICATION);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: CONNECTION_STATE_REQUEST")
    void testOf_ConnectionStateRequest() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x02, 0x07, // Connection State Request
                0x00, 0x10, // Total Length Octets
                // Body
                0x07, 0x00, 0x08, 0x01, 0x7F, 0x00, 0x00, 0x01, (byte) 0xF6, (byte) 0xB3
        };

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.CONNECTION_STATE_REQUEST);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: CONNECTION_STATE_RESPONSE")
    void testOf_ConnectionStateResponse() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x02, 0x08, // Connection State Response
                0x00, 0x08, // Total Length Octets
                // Body
                0x07, 0x00};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.CONNECTION_STATE_RESPONSE);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: DISCONNECT_REQUEST")
    void testOf_DisconnectRequest() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x02, 0x09, // Disconnect Request
                0x00, 0x10, // Total Length Octets
                // Body
                0x07, 0x00, 0x08, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.DISCONNECT_REQUEST);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: DISCONNECT_RESPONSE")
    void testOf_DisconnectResponse() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x02, 0x0A, // Disconnect Response
                0x00, 0x08, // Total Length Octets
                // Body
                0x07, 0x00};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.DISCONNECT_RESPONSE);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: DESCRIPTION_REQUEST")
    void testOf_DescriptionRequest() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x02, 0x03, // Description Request
                0x00, 0x0E, // Total Length Octets
                // Body
                0x08, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.DESCRIPTION_REQUEST);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: DESCRIPTION_RESPONSE")
    void testOf_DescriptionResponse() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x02, 0x04, // Description Response
                0x00, 0x46, // Total Length Octets
                // Body
                0x36, 0x01, 0x02, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, (byte) 0x83, 0x49, 0x7F, 0x01, (byte) 0xEC, (byte) 0xE0, 0x00, 0x17, 0x0C,
                (byte) 0xCC, 0x1B, (byte) 0xE0, (byte) 0x80, 0x08, (byte) 0xDA, 0x4D, 0x44, 0x54, 0x20, 0x4B, 0x4E, 0x58, 0x20, 0x49, 0x50, 0x20,
                0x52, 0x6F, 0x75, 0x74, 0x65, 0x72, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0A, 0x02, 0x02,
                0x01, 0x03, 0x01, 0x04, 0x01, 0x05, 0x01};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.DESCRIPTION_RESPONSE);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: CONNECT_REQUEST")
    void testOf_ConnectRequest() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x02, 0x05, // Connect Request
                0x00, 0x1A, // Total Length Octets
                // Body
                0x08, 0x01, 0x7F, 0x00, 0x00, 0x01, (byte) 0xF6, (byte) 0xB3, 0x08, 0x01, 0x7F, 0x00, 0x00, 0x01, (byte) 0xF6, (byte) 0xB4, 0x04,
                0x04, 0x02, 0x00};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.CONNECT_REQUEST);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: CONNECT_RESPONSE")
    void testOf_ConnectResponse() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x02, 0x06, // Connect Response
                0x00, 0x14, // Total Length Octets
                // Body
                0x07, 0x00, 0x08, 0x01, (byte) 0xC0, (byte) 0xA8, 0x01, 0x10, 0x0E, 0x57, 0x04, 0x04, (byte) 0xFF, (byte) 0xF2};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.CONNECT_RESPONSE);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: SEARCH_REQUEST")
    void testOf_SearchRequest() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x02, 0x01, // Search Request
                0x00, 0x0E, // Total Length Octets
                // Body
                0x08, 0x01, (byte) 0xc0, (byte) 0xa8, 0x01, 0x18, (byte) 0xf8, (byte) 0xeb};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.SEARCH_REQUEST);
    }

    @Test
    @DisplayName("Test #of(byte[]) with body: SEARCH_RESPONSE")
    void testOf_SearchResponse() {
        final var bytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x02, 0x02, // Search Response
                0x00, 0x4a, // Total Length Octets
                // Body
                0x08, 0x01, (byte) 0xc0, (byte) 0xa8, 0x01, 0x1d, 0x0e, 0x57, 0x36, 0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xe0, 0x00, 0x17, 0x0c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x65, 0x69, 0x62, 0x64, 0x20, 0x6f, 0x6e, 0x20, 0x57, 0x69, 0x72, 0x65, 0x47, 0x61, 0x74, 0x65,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x02,
                0x02, 0x01, 0x04, 0x01};

        assertThat(BodyFactory.of(bytes).getServiceType()).isEqualTo(ServiceType.SEARCH_RESPONSE);
    }

    @Test
    @DisplayName("Test #of(byte[]) with unsupported body type")
    void testWithUnsupportedBytes() {
        final var unsupportedBytes = new byte[]{ //
                // Header
                Header.STRUCTURE_LENGTH, Header.PROTOCOL_VERSION_V1, // KNX Header + Protocol
                0x03, 0x10, // Device Configuration Request (not supported)
                0x00, 0x06 // Total Length Octets
        };

        // unknown body
        assertThatThrownBy(() -> BodyFactory.of(unsupportedBytes)).isInstanceOf(KnxUnknownBodyException.class);
    }
}
