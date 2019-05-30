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

import li.pitschmann.knx.link.body.hpai.HostProtocol;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link HostProtocol} enum class
 *
 * @author PITSCHR
 */
public final class HostProtocolTest extends AbstractKnxByteEnumTest<HostProtocol> {
    @Override
    protected int numberOfElements() {
        return 2;
    }

    @Test
    @Override
    void validValueOf() {
        assertThat(HostProtocol.valueOf(0x01)).isEqualTo(HostProtocol.IPV4_UDP);
        assertThat(HostProtocol.valueOf(0x02)).isEqualTo(HostProtocol.IPV4_TCP);
    }

    @Test
    @Override
    void friendlyName() {
        assertThat(HostProtocol.IPV4_UDP.getFriendlyName()).isEqualTo("IP v4 UDP communication");
        assertThat(HostProtocol.IPV4_TCP.getFriendlyName()).isEqualTo("IP v4 TCP communication");
    }

    @Test
    @Override
    public void testToString() {
        assertThat(HostProtocol.IPV4_UDP).hasToString("HostProtocol{name=IPV4_UDP, friendlyName=IP v4 UDP communication, code=1 (0x01)}");
    }
}
