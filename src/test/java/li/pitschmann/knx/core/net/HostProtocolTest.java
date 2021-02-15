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

package li.pitschmann.knx.core.net;

import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link HostProtocol} enum class
 *
 * @author PITSCHR
 */
final class HostProtocolTest {

    @Test
    @DisplayName("Test number of enum elements")
    void numberOfElements() {
        assertThat(HostProtocol.values()).hasSize(2);
    }

    @Test
    @DisplayName("Valid cases for #valueOf()")
    void validValueOf() {
        assertThat(HostProtocol.valueOf(0x01)).isEqualTo(HostProtocol.IPV4_UDP);
        assertThat(HostProtocol.valueOf(0x02)).isEqualTo(HostProtocol.IPV4_TCP);
    }

    @Test
    @DisplayName("Invalid cases for #valueOf()")
    void invalidValueOf() {
        assertThatThrownBy(() -> HostProtocol.valueOf(0x00)).isInstanceOf(KnxEnumNotFoundException.class);
        assertThatThrownBy(() -> HostProtocol.valueOf(0xFF)).isInstanceOf(KnxEnumNotFoundException.class);
    }

    @Test
    @DisplayName("Test #getFriendlyName()")
    void testGetFriendlyName() {
        assertThat(HostProtocol.IPV4_UDP.getFriendlyName()).isEqualTo("IP v4 UDP communication");
        assertThat(HostProtocol.IPV4_TCP.getFriendlyName()).isEqualTo("IP v4 TCP communication");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        assertThat(HostProtocol.IPV4_UDP).hasToString(
                "HostProtocol{name=IPV4_UDP, friendlyName=IP v4 UDP communication, code=1}"
        );
    }
}
