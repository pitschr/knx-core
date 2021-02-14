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

package li.pitschmann.knx.core.net;

import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link ConnectionType} enum class
 *
 * @author PITSCHR
 */
final class ConnectionTypeTest {

    @Test
    @DisplayName("Test number of enum elements")
    void numberOfElements() {
        assertThat(ConnectionType.values()).hasSize(5);
    }

    @Test
    @DisplayName("Valid cases for #valueOf()")
    void validValueOf() {
        assertThat(ConnectionType.valueOf(0x03)).isEqualTo(ConnectionType.DEVICE_MANAGEMENT_CONNECTION);
        assertThat(ConnectionType.valueOf(0x04)).isEqualTo(ConnectionType.TUNNEL_CONNECTION);
        assertThat(ConnectionType.valueOf(0x06)).isEqualTo(ConnectionType.REMOTE_LOGGING_CONNECTION);
        assertThat(ConnectionType.valueOf(0x07)).isEqualTo(ConnectionType.REMOTE_CONFIG_CONNECTION);
        assertThat(ConnectionType.valueOf(0x08)).isEqualTo(ConnectionType.OBJECT_SERVER_CONNECTION);
    }

    @Test
    @DisplayName("Invalid cases for #valueOf()")
    void invalidValueOf() {
        assertThatThrownBy(() -> ConnectionType.valueOf(0x00)).isInstanceOf(KnxEnumNotFoundException.class);
        assertThatThrownBy(() -> ConnectionType.valueOf(0xFF)).isInstanceOf(KnxEnumNotFoundException.class);
    }

    @Test
    @DisplayName("Test #getFriendlyName()")
    void testGetFriendlyName() {
        assertThat(ConnectionType.DEVICE_MANAGEMENT_CONNECTION.getFriendlyName()).isEqualTo("Device Management Connection");
        assertThat(ConnectionType.TUNNEL_CONNECTION.getFriendlyName()).isEqualTo("Tunnel Connection");
        assertThat(ConnectionType.REMOTE_LOGGING_CONNECTION.getFriendlyName()).isEqualTo("Remote Logging Connection");
        assertThat(ConnectionType.REMOTE_CONFIG_CONNECTION.getFriendlyName()).isEqualTo("Remote Configuration Connection");
        assertThat(ConnectionType.OBJECT_SERVER_CONNECTION.getFriendlyName()).isEqualTo("Object Server Connection");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        assertThat(ConnectionType.TUNNEL_CONNECTION).hasToString(
                "ConnectionType{name=TUNNEL_CONNECTION, friendlyName=Tunnel Connection, code=4}"
        );
    }
}
