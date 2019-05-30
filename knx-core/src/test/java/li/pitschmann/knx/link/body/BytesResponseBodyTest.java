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

import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.ServiceType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link BytesResponseBody}
 *
 * @author PITSCHR
 */
public class BytesResponseBodyTest {
    /**
     * Tests the {@link BytesResponseBody#create(ServiceType, byte[])}
     */
    @Test
    public void validCases() {
        final var body = BytesResponseBody.create(ServiceType.DEVICE_CONFIGURATION_ACK, new byte[]{0x11, 0x22, 0x33});

        assertThat(body.getServiceType()).isEqualTo(ServiceType.DEVICE_CONFIGURATION_ACK);
        assertThat(body.getRawData()).containsExactly(0x11, 0x22, 0x33);

        // test toString
        assertThat(body).hasToString(String.format("BytesResponseBody{serviceType=%s, rawData=0x11 22 33}", ServiceType.DEVICE_CONFIGURATION_ACK));
        assertThat(body.toString(false)).isEqualTo(String.format("BytesResponseBody{serviceType=%s}", ServiceType.DEVICE_CONFIGURATION_ACK));
    }

    /**
     * Tests {@link BytesResponseBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> BytesResponseBody.create(null, new byte[0])).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("serviceType");
        assertThatThrownBy(() -> BytesResponseBody.create(ServiceType.CONNECT_REQUEST, null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("bytes");

        // invalid raw data length
        assertThatThrownBy(() -> BytesResponseBody.create(ServiceType.CONNECT_REQUEST, new byte[0xFF + 1])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }
}
