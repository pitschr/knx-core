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
import li.pitschmann.utils.Networker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link DescriptionRequestBody}
 *
 * @author PITSCHR
 */
public class DescriptionRequestBodyTest {
    private HPAI controlEndpoint;

    @BeforeEach
    public void before() {
        this.controlEndpoint = HPAI.of(HostProtocol.IPV4_UDP, Networker.getAddressUnbound(), 0);
    }

    /**
     * Tests the {@link DescriptionRequestBody#create(HPAI)} and {@link DescriptionRequestBody#valueOf(byte[])} methods.
     *
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: DESCRIPTION_REQUEST (0x0203)
     * 	        Total Length: 14 octets
     * 	    Body
     * 	        HPAI: Control endpoint
     * 	            Structure Length: 8 octets
     * 	            Host Protocol Code: IPV4_UDP (0x01)
     * 	            IP Address: 0.0.0.0 (0.0.0.0)
     * 	            IP Port: 0
     * </pre>
     */
    @Test
    public void validCases() {
        // create
        final var body = DescriptionRequestBody.create(this.controlEndpoint);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.DESCRIPTION_REQUEST);
        assertThat(body.getControlEndpoint()).isEqualTo(this.controlEndpoint);

        final var bodyDefault = DescriptionRequestBody.create();
        assertThat(body.getRawData()).containsExactly(bodyDefault.getRawData());

        // compare raw data with valueOf(byte[])
        final var bodyByBytes = DescriptionRequestBody.valueOf(new byte[]{0x08, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format("DescriptionRequestBody{controlEndpoint=%s, rawData=0x08 01 00 00 00 00 00 00}",
                this.controlEndpoint.toString(false)));
    }

    /**
     * Tests {@link DescriptionRequestBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> DescriptionRequestBody.create(null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("controlEndpoint");

        // invalid raw data length
        assertThatThrownBy(() -> DescriptionRequestBody.valueOf(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");
        assertThatThrownBy(() -> DescriptionRequestBody.valueOf(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }
}
