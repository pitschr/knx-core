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

import li.pitschmann.knx.link.body.hpai.HPAI;
import li.pitschmann.knx.link.body.hpai.HostProtocol;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.utils.Networker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link DescriptionRequestBody}
 *
 * @author PITSCHR
 */
public class DescriptionRequestBodyTest {
    /**
     * Tests the {@link DescriptionRequestBody#of(HPAI)} and {@link DescriptionRequestBody#of(byte[])} methods.
     *
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
     *         Host Protocol: IPv4 UDP (0x01)
     *         IP Address: 192.168.1.25
     *         Port Number: 33724
     * </pre>
     */
    @Test
    public void validCases() {
        // create
        final var controlEndpoint = HPAI.of(HostProtocol.IPV4_TCP, Networker.getByAddress(192, 168, 1, 25), 33724);
        final var body = DescriptionRequestBody.of(controlEndpoint);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.DESCRIPTION_REQUEST);
        assertThat(body.getControlEndpoint()).isEqualTo(controlEndpoint);

        // create by bytes
        final var bodyByBytes = DescriptionRequestBody.of(new byte[]{0x08, 0x02, (byte) 0xc0, (byte) 0xa8, 0x01, 0x19, (byte) 0x83, (byte) 0xbc});

        // compare raw data of 'create' and 'create by bytes'
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format("DescriptionRequestBody{controlEndpoint=%s, rawData=0x08 02 C0 A8 01 19 83 BC}",
                controlEndpoint.toString(false)));
    }

    /**
     * Tests the {@link SearchRequestBody#useDefault()} method.
     *
     * <pre>
     * KNX/IP Description Request, Control @ 0.0.0.0:0
     *     KNX/IP Header: Description Request
     *         Header Length: 6 bytes
     *         Protocol Version: 1.0
     *         Service Identifier: Description Request (0x0203)
     *             Service Family: Core (0x02)
     *             Service Type: Description Request (0x0203)
     *         Total Length: 14 bytes
     *     HPAI Control Endpoint: 0.0.0.0:0 UDP
     *         Structure Length: 8 bytes
     *         Host Protocol: IPv4 UDP (0x01)
     *         IP Address: 0.0.0.0
     *         Port Number: 0
     * </pre>
     */
    @Test
    public void validCaseNoArg() {
        // create
        final var body = DescriptionRequestBody.useDefault();
        assertThat(body.getRawData()).containsExactly(0x08, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);

        // toString
        assertThat(body).hasToString(String.format("DescriptionRequestBody{controlEndpoint=%s, rawData=0x08 01 00 00 00 00 00 00}",
                HPAI.useDefault().toString(false)));
    }

    /**
     * Tests {@link DescriptionRequestBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> DescriptionRequestBody.of((HPAI) null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("controlEndpoint");

        // invalid raw data length
        assertThatThrownBy(() -> DescriptionRequestBody.of((byte[]) null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");
        assertThatThrownBy(() -> DescriptionRequestBody.of(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }
}
