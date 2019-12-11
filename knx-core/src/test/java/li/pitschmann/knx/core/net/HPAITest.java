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

package li.pitschmann.knx.core.net;

import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.Networker;
import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

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
    private static final InetAddress TCP_ADDRESS = Networker.getByAddress(126, 0, 0, 1);
    private static final InetAddress UNBOUND = Networker.getAddressUnbound();
    private static final DatagramChannel udpChannelMock;
    private static final SocketChannel tcpChannelMock;

    static {
        // mock UDP (DatagramChannel)
        udpChannelMock = mock(DatagramChannel.class);
        final var socketMock = mock(DatagramSocket.class);
        when(udpChannelMock.socket()).thenReturn(socketMock);
        when(socketMock.getLocalAddress()).thenReturn(LOCALHOST);
        when(socketMock.getLocalPort()).thenReturn(12345);

        tcpChannelMock = mock(SocketChannel.class);
        final var tcpSocketMock = mock(Socket.class);
        when(tcpChannelMock.socket()).thenReturn(tcpSocketMock);
        when(tcpSocketMock.getLocalAddress()).thenReturn(TCP_ADDRESS);
        when(tcpSocketMock.getLocalPort()).thenReturn(45678);
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
     * Tests the {@link HPAI#of(Channel)}
     */
    @Test
    public void createByUdpChannel() {
        final var hpaiCreateByChannel = HPAI.of(udpChannelMock);
        final var hpaiCreateBy = HPAI.of(HostProtocol.IPV4_UDP, LOCALHOST, 12345);

        // assert
        assertThat(hpaiCreateByChannel.getRawData()).containsExactly(hpaiCreateBy.getRawData());
    }

    /**
     * Tests the {@link HPAI#of(Channel)}
     */
    @Test
    public void createByTcpChannel() {
        final var hpaiCreateByChannel = HPAI.of(tcpChannelMock);
        final var hpaiCreateBy = HPAI.of(HostProtocol.IPV4_TCP, TCP_ADDRESS, 45678);

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

        // create by bytes
        final var hpaiByValueOf = HPAI.of(new byte[]{0x08, 0x01, 0x7f, 0x00, 0x00, 0x01, 0x00, 0x50});
        assertThat(hpaiByValueOf.getLength()).isEqualTo(HPAI.KNXNET_HPAI_LENGTH);
        assertThat(hpaiByValueOf.getProtocol()).isEqualTo(HostProtocol.IPV4_UDP);
        assertThat(hpaiByValueOf.getAddress().getAddress()).containsExactly(0x7f, 0x00, 0x00, 0x01);
        assertThat(hpaiByValueOf.getPort()).isEqualTo(80);

        // compare raw data of 'create' and 'create by bytes'
        assertThat(hpaiByCreate.getRawData()).isEqualTo(hpaiByCreateRawData.getRawData());
        assertThat(hpaiByCreate.getRawData()).isEqualTo(hpaiByValueOf.getRawData());
    }

    /**
     * Tests <strong>invalid</strong> control byte parameters
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> HPAI.of((Channel) null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("channel");
        assertThatThrownBy(() -> HPAI.of((byte[]) null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("hpaiRawData");
        assertThatThrownBy(() -> HPAI.of(new byte[3])).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("hpaiRawData");

        assertThatThrownBy(() -> HPAI.of(null, LOCALHOST, 80)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("protocol");
        assertThatThrownBy(() -> HPAI.of(HostProtocol.IPV4_UDP, null, 80)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("address");

        // length out of range
        assertThatThrownBy(() -> HPAI.of(HostProtocol.IPV4_UDP, LOCALHOST, -1)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("port");
        assertThatThrownBy(() -> HPAI.of(HostProtocol.IPV4_UDP, LOCALHOST, 0xFFFF + 1)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("port");

        // test unsupported channel
        assertThatThrownBy(() -> HPAI.of(mock(SelectableChannel.class))) //
                .isInstanceOf(KnxIllegalArgumentException.class) //
                .hasMessageStartingWith("Channel type is not supported:");
    }

    /**
     * Test {@link HPAI#toString()}
     */
    @Test
    public void testToString() {
        assertThat(HPAI.of(udpChannelMock)).hasToString(String.format(
                "HPAI{length=8 (0x08), protocol=%s, address=127.0.0.1 (0x7F 00 00 01), port=12345 (0x30 39), rawData=0x08 01 7F 00 00 01 30 39}",
                HostProtocol.IPV4_UDP));

        assertThat(HPAI.of(tcpChannelMock)).hasToString(String.format(
                "HPAI{length=8 (0x08), protocol=%s, address=126.0.0.1 (0x7E 00 00 01), port=45678 (0xB2 6E), rawData=0x08 02 7E 00 00 01 B2 6E}",
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
