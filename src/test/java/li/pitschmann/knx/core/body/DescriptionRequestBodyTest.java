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
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.HostProtocol;
import li.pitschmann.knx.core.utils.Networker;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link DescriptionRequestBody}
 *
 * @author PITSCHR
 */
class DescriptionRequestBodyTest {

    /**
     * <pre>
     * KNX/IP Description Request, Control @ 192.168.1.25:33724
     *     KNX/IP Header: Description Request
     *         Header Length: 6 bytes
     *         Protocol Version: 1.0
     *         Service Identifier: Description Request (0x0203)
     *             Service Family: Core (0x02)
     *             Service Type: Description Request (0x0203)
     *         Total Length: 14 bytes
     *     HPAI Control Endpoint: 192.168.1.25:33724 UDP
     *         Structure Length: 8 bytes
     *         Host Protocol: IPv4 TCP (0x02)
     *         IP Address: 192.168.1.25
     *         Port Number: 33724
     * </pre>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(HPAI)")
    void validCases() {
        // create by bytes
        final var bodyByBytes = DescriptionRequestBody.of(new byte[]{
                0x08,                                 // Structure Length
                0x02,                                 // Host Protocol
                (byte) 0xC0, (byte) 0xA8, 0x01, 0x19, // IP Address
                (byte) 0x83, (byte) 0xBC              // IP Port Number
        });

        // create
        final var controlEndpoint = HPAI.of(HostProtocol.IPV4_TCP, Networker.getByAddress(192, 168, 1, 25), 33724);
        final var body = DescriptionRequestBody.of(controlEndpoint);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.DESCRIPTION_REQUEST);
        assertThat(body.getControlEndpoint()).isEqualTo(controlEndpoint);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("DescriptionRequestBody{controlEndpoint=%s}", controlEndpoint)
        );
    }

    @Test
    @DisplayName("Test #useDefault()")
    void testUseDefault() {
        // create by bytes
        final var bodyByBytes = DescriptionRequestBody.of(new byte[]{
                0x08,                   // Structure Length
                0x01,                   // Host Protocol
                0x00, 0x00, 0x00, 0x00, // IP Address
                0x00, 0x00              // IP Port Number
        });

        // create
        final var controlEndpoint = HPAI.useDefault();

        final var body = DescriptionRequestBody.useDefault();
        assertThat(body.getServiceType()).isSameAs(ServiceType.DESCRIPTION_REQUEST);
        assertThat(body.getControlEndpoint()).isSameAs(controlEndpoint);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("DescriptionRequestBody{controlEndpoint=%s}", controlEndpoint)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        assertThatThrownBy(() -> DescriptionRequestBody.of((byte[]) null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> DescriptionRequestBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '8' but was: 0");
    }

    @Test
    @DisplayName("Invalid cases for #of(HPAI)")
    void invalidCases_ofObjects() {
        // null
        assertThatThrownBy(() -> DescriptionRequestBody.of((HPAI) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Control Endpoint is required.");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DescriptionRequestBody.class).verify();
    }

}
