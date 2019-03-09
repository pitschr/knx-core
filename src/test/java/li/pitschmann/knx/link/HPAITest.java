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

package li.pitschmann.knx.link;

import li.pitschmann.knx.link.body.hpai.HPAI;
import li.pitschmann.knx.link.body.hpai.HostProtocol;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.Networker;
import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.channels.DatagramChannel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test case for {@link HPAI}
 *
 * @author PITSCHR
 */
public final class HPAITest {
    private static final InetAddress LOCALHOST = Networker.getByAddress(127, 0, 0, 1);
    private static final InetAddress UNBOUND = Networker.getAddressUnbound();
    private static final DatagramChannel CHANNEL_MOCK;

    static {
        // mock DatagramChannel
        CHANNEL_MOCK = mock(DatagramChannel.class);
        final var socketMock = mock(DatagramSocket.class);
        final var inetAddressMock = mock(InetAddress.class);
        when(CHANNEL_MOCK.socket()).thenReturn(socketMock);
        when(socketMock.getLocalAddress()).thenReturn(inetAddressMock);
        when(socketMock.getLocalPort()).thenReturn(12345);
        when(inetAddressMock.getAddress()).thenReturn(new byte[]{127, 0, 0, 1});
    }

    /**
     * Tests the {@link HPAI#useDefault()}
     */
    @Test
    public void useDefault() {
        final var hpaiDefault = HPAI.useDefault();
        final var hpaiCreateBy = HPAI.of(HostProtocol.IPV4_UDP, UNBOUND, 0);

        // assert
        assertThat(hpaiDefault.getRawData()).containsExactly(hpaiCreateBy.getRawData());
    }

    /**
     * Tests the {@link HPAI#of(HostProtocol, java.nio.channels.DatagramChannel)}
     */
    @Test
    public void createByDatagramChannel() {
        final var hpaiCreateByChannel = HPAI.of(HostProtocol.IPV4_TCP, CHANNEL_MOCK);
        final var hpaiCreateBy = HPAI.of(HostProtocol.IPV4_TCP, LOCALHOST, 12345);

        // assert
        assertThat(hpaiCreateByChannel.getRawData()).containsExactly(hpaiCreateBy.getRawData());
    }

    /**
     * Tests the {@link HPAI} with no length
     */
    @Test
    public void emptyAdditionalInfo() {
        // create
        final var hpaiByCreate = HPAI.of(HostProtocol.IPV4_UDP, LOCALHOST, 80);
        final var hpaiByCreateRawData = HPAI.of(hpaiByCreate.getRawData());
        assertThat(hpaiByCreateRawData.getLength()).isEqualTo(HPAI.KNXNET_HPAI_LENGTH);
        assertThat(hpaiByCreateRawData.getProtocol()).isEqualTo(HostProtocol.IPV4_UDP);
        assertThat(hpaiByCreateRawData.getAddress().getAddress()).containsExactly(0x7f, 0x00, 0x00, 0x01);
        assertThat(hpaiByCreateRawData.getPort()).isEqualTo(80);

        // valueOf
        final var hpaiByValueOf = HPAI.of(new byte[]{0x08, 0x01, 0x7f, 0x00, 0x00, 0x01, 0x00, 0x50});
        assertThat(hpaiByValueOf.getLength()).isEqualTo(HPAI.KNXNET_HPAI_LENGTH);
        assertThat(hpaiByValueOf.getProtocol()).isEqualTo(HostProtocol.IPV4_UDP);
        assertThat(hpaiByValueOf.getAddress().getAddress()).containsExactly(0x7f, 0x00, 0x00, 0x01);
        assertThat(hpaiByValueOf.getPort()).isEqualTo(80);

        // compare raw data of 'create' and 'valueOf'
        assertThat(hpaiByCreate.getRawData()).isEqualTo(hpaiByCreateRawData.getRawData());
        assertThat(hpaiByCreate.getRawData()).isEqualTo(hpaiByValueOf.getRawData());
    }

    /**
     * Tests <strong>invalid</strong> control byte parameters
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> HPAI.of(null, CHANNEL_MOCK)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("protocol");
        assertThatThrownBy(() -> HPAI.of(HostProtocol.IPV4_UDP, null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("channel");
        assertThatThrownBy(() -> HPAI.of(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("hpaiRawData");
        assertThatThrownBy(() -> HPAI.of(new byte[3])).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("hpaiRawData");

        assertThatThrownBy(() -> HPAI.of(null, LOCALHOST, 80)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("protocol");
        assertThatThrownBy(() -> HPAI.of(HostProtocol.IPV4_UDP, null, 80)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("address");

        // length out of range
        assertThatThrownBy(() -> HPAI.of(HostProtocol.IPV4_UDP, LOCALHOST, -1)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("port");
        assertThatThrownBy(() -> HPAI.of(HostProtocol.IPV4_UDP, LOCALHOST, 0xFFFF + 1)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("port");
    }

    /**
     * Test {@link HPAI#toString()}
     */
    @Test
    public void testToString() {
        assertThat(HPAI.of(HostProtocol.IPV4_TCP, CHANNEL_MOCK)).hasToString(String.format(
                "HPAI{length=8 (0x08), protocol=%s, address=127.0.0.1 (0x7F 00 00 01), port=12345 (0x30 39), rawData=0x08 02 7F 00 00 01 30 39}",
                HostProtocol.IPV4_TCP));
    }

    /**
     * Test {@link HPAI#equals(Object)} and {@link HPAI#hashCode()}
     */
    @Test
    public void testEqualsAndHashcode() {
        final var hpaiA = HPAI.of(new byte[]{0x08, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x07});
        final var hpaiB = HPAI.of(HostProtocol.IPV4_TCP, Networker.getByAddress(3, 4, 5, 6), 7);

        // equals
        assertThat(hpaiA).isEqualTo(hpaiA);
        assertThat(hpaiB).isEqualTo(hpaiA);
        assertThat(hpaiA).hasSameHashCodeAs(hpaiA);
        assertThat(hpaiA).hasSameHashCodeAs(hpaiB);

        // not equals
        assertThat(hpaiA).isNotEqualTo(new Object());
        assertThat(hpaiA).isNotEqualTo(HPAI.of(new byte[]{0x07, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}));
        assertThat(hpaiA).isNotEqualTo(HPAI.of(HostProtocol.IPV4_UDP, Networker.getByAddress(3, 4, 5, 6), 7));
        assertThat(hpaiA).isNotEqualTo(HPAI.of(HostProtocol.IPV4_TCP, Networker.getByAddress(2, 4, 5, 6), 7));
        assertThat(hpaiA).isNotEqualTo(HPAI.of(HostProtocol.IPV4_TCP, Networker.getByAddress(3, 4, 5, 6), 8));
    }
}
