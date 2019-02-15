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

import li.pitschmann.knx.link.exceptions.KnxUnknownBodyException;
import li.pitschmann.knx.link.header.Header;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.test.TestHelpers;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link BodyFactory}
 *
 * @author PITSCHR
 */
public class BodyFactoryTest {
    /**
     * Test constructor of {@link BodyFactory}
     */
    @Test
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(BodyFactory.class);
    }

    /**
     * Tests {@link BodyFactory#valueOf(byte[])} with supported bodies
     */
    @Test
    public void valueOfWithBytes() {
        byte[] tunReqBytes = new byte[]{ //
                // Header
                Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, // KNX Header + Protocol
                0x04, 0x20, // Tunnelling Request
                0x00, 0x17, // Total Length Octets
                // Body
                0x04, 0x11, 0x5c, 0x00, 0x29, 0x00, (byte) 0xbc, (byte) 0xe0, 0x10, (byte) 0xa0, 0x4c, 0x07, 0x03, 0x00, (byte) 0x80, 0x0c, 0x3f};

        byte[] tunAckBytes = new byte[]{ //
                // Header
                Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, // KNX Header + Protocol
                0x04, 0x21, // Tunnelling Ack
                0x00, 0x0A, // Total Length Octets
                // Body
                0x04, 0x11, (byte) 0x81, 0x29};

        byte[] conStateReqBytes = new byte[]{ //
                // Header
                Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, // KNX Header + Protocol
                0x02, 0x07, // Connection State Request
                0x00, 0x10, // Total Length Octets
                // Body
                0x07, 0x00, 0x08, 0x01, 0x7F, 0x00, 0x00, 0x01, (byte) 0xF6, (byte) 0xB3
        };

        byte[] conStateResBytes = new byte[]{ //
                // Header
                Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, // KNX Header + Protocol
                0x02, 0x08, // Connection State Response
                0x00, 0x08, // Total Length Octets
                // Body
                0x07, 0x00};

        byte[] disconnectReqBytes = new byte[]{ //
                // Header
                Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, // KNX Header + Protocol
                0x02, 0x09, // Disconnect Request
                0x00, 0x10, // Total Length Octets
                // Body
                0x07, 0x00, 0x08, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        byte[] disconnectResBytes = new byte[]{ //
                // Header
                Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, // KNX Header + Protocol
                0x02, 0x0A, // Disconnect Response
                0x00, 0x08, // Total Length Octets
                // Body
                0x07, 0x00};

        byte[] descriptionReqBytes = new byte[]{ //
                // Header
                Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, // KNX Header + Protocol
                0x02, 0x03, // Description Request
                0x00, 0x0E, // Total Length Octets
                // Body
                0x08, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        byte[] descriptionResBytes = new byte[]{ //
                // Header
                Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, // KNX Header + Protocol
                0x02, 0x04, // Description Response
                0x00, 0x46, // Total Length Octets
                // Body
                0x36, 0x01, 0x02, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, (byte) 0x83, 0x49, 0x7F, 0x01, (byte) 0xEC, (byte) 0xE0, 0x00, 0x17, 0x0C,
                (byte) 0xCC, 0x1B, (byte) 0xE0, (byte) 0x80, 0x08, (byte) 0xDA, 0x4D, 0x44, 0x54, 0x20, 0x4B, 0x4E, 0x58, 0x20, 0x49, 0x50, 0x20,
                0x52, 0x6F, 0x75, 0x74, 0x65, 0x72, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0A, 0x02, 0x02,
                0x01, 0x03, 0x01, 0x04, 0x01, 0x05, 0x01};

        byte[] connectReqBytes = new byte[]{ //
                // Header
                Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, // KNX Header + Protocol
                0x02, 0x05, // Connect Request
                0x00, 0x1A, // Total Length Octets
                // Body
                0x08, 0x01, 0x7F, 0x00, 0x00, 0x01, (byte) 0xF6, (byte) 0xB3, 0x08, 0x01, 0x7F, 0x00, 0x00, 0x01, (byte) 0xF6, (byte) 0xB4, 0x04,
                0x04, 0x02, 0x00};

        byte[] connectResBytes = new byte[]{ //
                // Header
                Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, // KNX Header + Protocol
                0x02, 0x06, // Connect Response
                0x00, 0x14, // Total Length Octets
                // Body
                0x07, 0x00, 0x08, 0x01, (byte) 0xC0, (byte) 0xA8, 0x01, 0x10, 0x0E, 0x57, 0x04, 0x04, (byte) 0xFF, (byte) 0xF2};

        // bodies
        assertThat(BodyFactory.valueOf(tunReqBytes).getServiceType()).isEqualTo(ServiceType.TUNNELING_REQUEST);
        assertThat(BodyFactory.valueOf(tunAckBytes).getServiceType()).isEqualTo(ServiceType.TUNNELING_ACK);

        assertThat(BodyFactory.valueOf(conStateReqBytes).getServiceType()).isEqualTo(ServiceType.CONNECTIONSTATE_REQUEST);
        assertThat(BodyFactory.valueOf(conStateResBytes).getServiceType()).isEqualTo(ServiceType.CONNECTIONSTATE_RESPONSE);

        assertThat(BodyFactory.valueOf(disconnectReqBytes).getServiceType()).isEqualTo(ServiceType.DISCONNECT_REQUEST);
        assertThat(BodyFactory.valueOf(disconnectResBytes).getServiceType()).isEqualTo(ServiceType.DISCONNECT_RESPONSE);

        assertThat(BodyFactory.valueOf(descriptionReqBytes).getServiceType()).isEqualTo(ServiceType.DESCRIPTION_REQUEST);
        assertThat(BodyFactory.valueOf(descriptionResBytes).getServiceType()).isEqualTo(ServiceType.DESCRIPTION_RESPONSE);

        assertThat(BodyFactory.valueOf(connectReqBytes).getServiceType()).isEqualTo(ServiceType.CONNECT_REQUEST);
        assertThat(BodyFactory.valueOf(connectResBytes).getServiceType()).isEqualTo(ServiceType.CONNECT_RESPONSE);
    }

    /**
     * Tests {@link BodyFactory#valueOf(byte[])} with unsupported bodies
     */
    @Test
    public void valueOfWithUnsupportedBytes() {
        byte[] unsupportedBytes = new byte[]{ //
                // Header
                Header.KNXNET_HEADER_LENGTH, Header.KNXNET_PROTOCOL_VERSION, // KNX Header + Protocol
                0x03, 0x10, // Device Configuration Request (not supported)
                0x00, 0x06 // Total Length Octets
        };

        // unknown body
        assertThatThrownBy(() -> BodyFactory.valueOf(unsupportedBytes).getServiceType()).isInstanceOf(KnxUnknownBodyException.class);
    }
}
