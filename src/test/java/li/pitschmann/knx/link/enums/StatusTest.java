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

package li.pitschmann.knx.link.enums;

import li.pitschmann.knx.link.body.Status;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link Status} enum class
 *
 * @author PITSCHR
 */
public final class StatusTest extends AbstractKnxByteEnumTest<Status> {
    @Override
    protected int numberOfElements() {
        return 11;
    }

    @Test
    @Override
    void validValueOf() {
        // 0x00 .. 0x10
        assertThat(Status.valueOf(0x00)).isEqualTo(Status.E_NO_ERROR);
        assertThat(Status.valueOf(0x01)).isEqualTo(Status.E_HOST_PROTOCOL_TYPE);
        assertThat(Status.valueOf(0x02)).isEqualTo(Status.E_VERSION_NOT_SUPPORTED);
        assertThat(Status.valueOf(0x04)).isEqualTo(Status.E_SEQUENCE_NUMBER);
        // 0x20 .. 0x30
        assertThat(Status.valueOf(0x21)).isEqualTo(Status.E_CONNECTION_ID);
        assertThat(Status.valueOf(0x22)).isEqualTo(Status.E_CONNECTION_TYPE);
        assertThat(Status.valueOf(0x23)).isEqualTo(Status.E_CONNECTION_OPTION);
        assertThat(Status.valueOf(0x24)).isEqualTo(Status.E_NO_MORE_CONNECTIONS);
        assertThat(Status.valueOf(0x26)).isEqualTo(Status.E_DATA_CONNECTION);
        assertThat(Status.valueOf(0x27)).isEqualTo(Status.E_KNX_CONNECTION);
        assertThat(Status.valueOf(0x29)).isEqualTo(Status.E_TUNNELLING_LAYER);
    }

    @Test
    @Override
    void friendlyName() {
        // 0x00 .. 0x10
        assertThat(Status.E_NO_ERROR.getFriendlyName()).isEqualTo("Operation successful");
        assertThat(Status.E_HOST_PROTOCOL_TYPE.getFriendlyName()).isEqualTo("Host Protocol type not supported");
        assertThat(Status.E_VERSION_NOT_SUPPORTED.getFriendlyName()).isEqualTo("Protocol version not supported");
        assertThat(Status.E_SEQUENCE_NUMBER.getFriendlyName()).isEqualTo("Sequence number out of order");
        // 0x20 .. 0x30
        assertThat(Status.E_CONNECTION_ID.getFriendlyName()).isEqualTo("No active connection with specified id");
        assertThat(Status.E_CONNECTION_TYPE.getFriendlyName()).isEqualTo("Connection type not supported");
        assertThat(Status.E_CONNECTION_OPTION.getFriendlyName()).isEqualTo("Connection option not supported");
        assertThat(Status.E_NO_MORE_CONNECTIONS.getFriendlyName()).isEqualTo("All connections already used");
        assertThat(Status.E_DATA_CONNECTION.getFriendlyName()).isEqualTo("Error in data connection for specified id");
        assertThat(Status.E_KNX_CONNECTION.getFriendlyName()).isEqualTo("Error in KNX connection for specified id");
        assertThat(Status.E_TUNNELLING_LAYER.getFriendlyName()).isEqualTo("Requested KNX/IP Tunneling layer not supported");
    }

    @Test
    @Override
    void testToString() {
        assertThat(Status.E_NO_ERROR).hasToString("Status{name=E_NO_ERROR, friendlyName=Operation successful, code=0 (0x00)}");
        assertThat(Status.E_NO_MORE_CONNECTIONS)
                .hasToString("Status{name=E_NO_MORE_CONNECTIONS, friendlyName=All connections already used, code=36 (0x24)}");
    }
}
