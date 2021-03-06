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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link Status} enum class
 *
 * @author PITSCHR
 */
final class StatusTest {

    @Test
    @DisplayName("Test number of enum elements")
    void numberOfElements() {
        assertThat(Status.values()).hasSize(12);
    }

    @Test
    @DisplayName("Valid cases for #valueOf()")
    void validValueOf() {
        // 0x00 .. 0x10
        assertThat(Status.valueOf(0x00)).isEqualTo(Status.NO_ERROR);
        assertThat(Status.valueOf(0x01)).isEqualTo(Status.HOST_PROTOCOL_TYPE);
        assertThat(Status.valueOf(0x02)).isEqualTo(Status.VERSION_NOT_SUPPORTED);
        assertThat(Status.valueOf(0x04)).isEqualTo(Status.SEQUENCE_NUMBER);
        // 0x20 .. 0x30
        assertThat(Status.valueOf(0x21)).isEqualTo(Status.CONNECTION_ID);
        assertThat(Status.valueOf(0x22)).isEqualTo(Status.CONNECTION_TYPE);
        assertThat(Status.valueOf(0x23)).isEqualTo(Status.CONNECTION_OPTION);
        assertThat(Status.valueOf(0x24)).isEqualTo(Status.NO_MORE_CONNECTIONS);
        assertThat(Status.valueOf(0x26)).isEqualTo(Status.DATA_CONNECTION);
        assertThat(Status.valueOf(0x27)).isEqualTo(Status.KNX_CONNECTION);
        assertThat(Status.valueOf(0x29)).isEqualTo(Status.TUNNELING_LAYER);
        // else
        assertThat(Status.valueOf(0xFF)).isEqualTo(Status.UNKNOWN);
    }

    @Test
    @DisplayName("Invalid cases for #valueOf()")
    void invalidValueOf() {
        assertThat(Status.valueOf(0xEE)).isEqualTo(Status.UNKNOWN);
    }

    @Test
    @DisplayName("Test #getFriendlyName()")
    void testGetFriendlyName() {
        // 0x00 .. 0x10
        assertThat(Status.NO_ERROR.getFriendlyName()).isEqualTo("Operation successful");
        assertThat(Status.HOST_PROTOCOL_TYPE.getFriendlyName()).isEqualTo("Host Protocol type not supported");
        assertThat(Status.VERSION_NOT_SUPPORTED.getFriendlyName()).isEqualTo("Protocol version not supported");
        assertThat(Status.SEQUENCE_NUMBER.getFriendlyName()).isEqualTo("Sequence number out of order");
        // 0x20 .. 0x30
        assertThat(Status.CONNECTION_ID.getFriendlyName()).isEqualTo("No active connection with specified id");
        assertThat(Status.CONNECTION_TYPE.getFriendlyName()).isEqualTo("Connection type not supported");
        assertThat(Status.CONNECTION_OPTION.getFriendlyName()).isEqualTo("Connection option not supported");
        assertThat(Status.NO_MORE_CONNECTIONS.getFriendlyName()).isEqualTo("All connections already used");
        assertThat(Status.DATA_CONNECTION.getFriendlyName()).isEqualTo("Error in data connection for specified id");
        assertThat(Status.KNX_CONNECTION.getFriendlyName()).isEqualTo("Error in KNX connection for specified id");
        assertThat(Status.TUNNELING_LAYER.getFriendlyName()).isEqualTo("Requested KNX/IP Tunneling layer not supported");
        // else
        assertThat(Status.UNKNOWN.getFriendlyName()).isEqualTo("Unknown status");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        assertThat(Status.NO_ERROR).hasToString(
                "Status{name=NO_ERROR, friendlyName=Operation successful, code=0}"
        );

        assertThat(Status.NO_MORE_CONNECTIONS).hasToString(
                "Status{name=NO_MORE_CONNECTIONS, friendlyName=All connections already used, code=36}"
        );
    }
}
