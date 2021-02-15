/*
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
 * Tests the {@link SearchRequestBody}
 *
 * @author PITSCHR
 */
class SearchRequestBodyTest {

    /**
     * <pre>
     * KNX/IP Search Request, Discovery @ 192.168.1.24:63723
     *     KNX/IP Header: Search Request
     *     Header Length: 6 bytes
     *         Protocol Version: 1.0
     *         Service Identifier: Search Request (0x0201)
     *             Service Family: Core (0x02)
     *             Service Type: Search Request (0x0201)
     *         Total Length: 14 bytes
     *     HPAI Discovery Endpoint: 192.168.1.24:63723 UDP
     *         Structure Length: 8 bytes
     *         Host Protocol: IPv4 UDP (0x01)
     *         IP Address: 192.168.1.24
     *         Port Number: 63723
     * </pre>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(HPAI)")
    void validCases() {
        // create by bytes
        final var bodyByBytes = SearchRequestBody.of(new byte[]{
                0x08,                                   // Structure Length
                0x01,                                   // Host Protocol
                (byte) 0xC0, (byte) 0xA8, 0x01, 0x18,   // IP Address
                (byte) 0xF8, (byte) 0xEB                // IP Port Number
        });

        // create
        final var discoveryEndpoint = HPAI.of(HostProtocol.IPV4_UDP, Networker.getByAddress(192, 168, 1, 24), 63723);
        final var body = SearchRequestBody.of(discoveryEndpoint);
        assertThat(body.getServiceType()).isSameAs(ServiceType.SEARCH_REQUEST);
        assertThat(body.getDiscoveryEndpoint()).isSameAs(discoveryEndpoint);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("SearchRequestBody{discoveryEndpoint=%s}", discoveryEndpoint)
        );
    }

    @Test
    @DisplayName("Test #useDefault()")
    void testUseDefault() {
        // create by bytes
        final var bodyByBytes = SearchRequestBody.of(new byte[]{
                0x08,                   // Structure Length
                0x01,                   // Host Protocol
                0x00, 0x00, 0x00, 0x00, // IP Address
                0x00, 0x00              // IP Port Number
        });

        // create
        final var discoveryEndpoint = HPAI.useDefault();

        final var body = SearchRequestBody.useDefault();
        assertThat(body.getServiceType()).isSameAs(ServiceType.SEARCH_REQUEST);
        assertThat(body.getDiscoveryEndpoint()).isSameAs(discoveryEndpoint);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("SearchRequestBody{discoveryEndpoint=%s}", discoveryEndpoint)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        assertThatThrownBy(() -> SearchRequestBody.of((byte[]) null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> SearchRequestBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '8' but was: 0");
    }

    @Test
    @DisplayName("Invalid cases for #of(HPAI)")
    void invalidCases_ofObjects() {
        // null
        assertThatThrownBy(() -> SearchRequestBody.of((HPAI) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Discovery Endpoint is required.");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(SearchRequestBody.class).verify();
    }

}
