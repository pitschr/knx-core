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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.HostProtocol;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.Networker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link SearchRequestBody}
 *
 * @author PITSCHR
 */
public class SearchRequestBodyTest {

    /**
     * Tests the {@link SearchRequestBody#of(HPAI)} and {@link SearchRequestBody#of(byte[])} methods.
     *
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
    public void validCases() {
        // create
        final var discoveryEndpoint = HPAI.of(HostProtocol.IPV4_UDP, Networker.getByAddress(192, 168, 1, 24), 63723);
        final var body = SearchRequestBody.of(discoveryEndpoint);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.SEARCH_REQUEST);
        assertThat(body.getDiscoveryEndpoint()).isEqualTo(discoveryEndpoint);

        // create by bytes
        final var bodyByBytes = SearchRequestBody.of(new byte[]{0x08, 0x01, (byte) 0xc0, (byte) 0xa8, 0x01, 0x18, (byte) 0xf8, (byte) 0xeb});

        // compare raw data of 'create' and 'create by bytes'
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format("SearchRequestBody{discoveryEndpoint=%s, rawData=0x08 01 C0 A8 01 18 F8 EB}",
                discoveryEndpoint.toString(false)));
    }

    /**
     * Tests the {@link SearchRequestBody#useDefault()} method.
     *
     * <pre>
     * KNX/IP Search Request, Discovery @ 0.0.0.0:0
     *     KNX/IP Header: Search Request
     *         Header Length: 6 bytes
     *         Protocol Version: 1.0
     *         Service Identifier: Search Request (0x0201)
     *             Service Family: Core (0x02)
     *             Service Type: Search Request (0x0201)
     *         Total Length: 14 bytes
     *     HPAI Discovery Endpoint: 0.0.0.0:0 UDP
     *         Structure Length: 8 bytes
     *         Host Protocol: IPv4 UDP (0x01)
     *         IP Address: unbound (0.0.0.0)
     *         Port Number: 0
     * </pre>
     */
    @Test
    public void validCaseNoArg() {
        // create
        final var body = SearchRequestBody.useDefault();
        assertThat(body.getRawData()).containsExactly(0x08, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);

        // toString
        assertThat(body).hasToString(String.format("SearchRequestBody{discoveryEndpoint=%s, rawData=0x08 01 00 00 00 00 00 00}",
                HPAI.useDefault().toString(false)));
    }

    /**
     * Tests {@link SearchRequestBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> SearchRequestBody.of((HPAI) null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("discoveryEndpoint");

        // invalid raw data length
        assertThatThrownBy(() -> SearchRequestBody.of((byte[]) null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");
        assertThatThrownBy(() -> SearchRequestBody.of(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }
}
