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

import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.utils.Networker;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
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
final class HPAITest {

    @Test
    @DisplayName("Test #useDefault()")
    void testUseDefault() {
        final var hpai = HPAI.useDefault();
        assertThat(hpai.getLength()).isEqualTo(HPAI.STRUCTURE_LENGTH);
        assertThat(hpai.getProtocol()).isSameAs(HostProtocol.IPV4_UDP);
        assertThat(hpai.getAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(hpai.getPort()).isZero();

        assertThat(hpai.toByteArray()).containsExactly(new byte[]{
                HPAI.STRUCTURE_LENGTH,  // Structure Length
                0x01,                   // Host Protocol
                0x00, 0x00, 0x00, 0x00, // Address
                0x00, 0x00              // Port
        });
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testOf_Bytes() {
        final var bytes = new byte[]{
                HPAI.STRUCTURE_LENGTH,  // Structure Length
                0x02,                   // Host Protocol
                0x7E, 0x04, 0x05, 0x06, // Address (126.4.5.6)
                (byte) 0xB2, 0x6E       // Port (45678)
        };

        final var hpai = HPAI.of(bytes);
        assertThat(hpai.getLength()).isEqualTo(HPAI.STRUCTURE_LENGTH);
        assertThat(hpai.getProtocol()).isSameAs(HostProtocol.IPV4_TCP);
        assertThat(hpai.getAddress()).isEqualTo(Networker.getByAddress(126, 4, 5, 6));
        assertThat(hpai.getPort()).isEqualTo(45678);
        assertThat(hpai.toByteArray()).containsExactly(bytes);
    }

    @Test
    @DisplayName("Test #of(HostProtocol, InetAddress, int)")
    void testOf_HostProtocol_Address_Port() {
        final var address = Networker.getByAddress(10, 37, 83, 30);
        final var hpai = HPAI.of(HostProtocol.IPV4_UDP, address, 32373);

        assertThat(hpai.getLength()).isEqualTo(HPAI.STRUCTURE_LENGTH);
        assertThat(hpai.getProtocol()).isSameAs(HostProtocol.IPV4_UDP);
        assertThat(hpai.getAddress()).isEqualTo(address);
        assertThat(hpai.getPort()).isEqualTo(32373);
        assertThat(hpai.toByteArray()).containsExactly(new byte[]{
                HPAI.STRUCTURE_LENGTH,  // Structure Length
                0x01,                   // Host Protocol
                0x0A, 0x25, 0x53, 0x1E, // Address (10.37.83.30)
                0x7E, 0x75              // Port (32373)
        });
    }

    @Test
    @DisplayName("Test #of(Channel) with UDP")
    void testOf_Channel_UDP() {
        final var address = Networker.getByAddress(81, 124, 233, 7);
        final var udpChannelMock = mock(DatagramChannel.class);
        final var socketMock = mock(DatagramSocket.class);
        when(udpChannelMock.socket()).thenReturn(socketMock);
        when(socketMock.getLocalAddress()).thenReturn(address);
        when(socketMock.getLocalPort()).thenReturn(12345);

        final var hpai = HPAI.of(udpChannelMock);
        assertThat(hpai.toByteArray()).containsExactly(new byte[]{
                HPAI.STRUCTURE_LENGTH,         // Structure Length
                0x01,                          // Host Protocol
                0x51, 0x7C, (byte) 0xE9, 0x07, // Address (81.124.233.7)
                0x30, 0x39                     // Port (12345)
        });
    }

    @Test
    @DisplayName("Test #of(Channel) with TCP")
    void testOf_Channel_TCP() {
        final var address = Networker.getByAddress(243, 28, 92, 41);
        final var tcpChannelMock = mock(SocketChannel.class);
        final var tcpSocketMock = mock(Socket.class);
        when(tcpChannelMock.socket()).thenReturn(tcpSocketMock);
        when(tcpSocketMock.getLocalAddress()).thenReturn(address);
        when(tcpSocketMock.getLocalPort()).thenReturn(7333);

        final var hpai = HPAI.of(tcpChannelMock);
        assertThat(hpai.toByteArray()).containsExactly(new byte[]{
                HPAI.STRUCTURE_LENGTH,         // Structure Length
                0x02,                          // Host Protocol
                (byte) 0xF3, 0x1C, 0x5C, 0x29, // Address (243.28.92.41)
                0x1C, (byte) 0xA5              // Port (7333)
        });
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_of_Bytes() {
        // null
        assertThatThrownBy(() -> HPAI.of((byte[]) null))
                .isInstanceOf(NullPointerException.class);

        // invalid structure length
        assertThatThrownBy(() -> HPAI.of(new byte[3]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '8' but was: 3");
    }

    @Test
    @DisplayName("Invalid cases for #of(HostProtocol, InetAddress, port)")
    void invalidCases_of_HostProtocol_InetAddress_Port() {
        // null
        assertThatThrownBy(() -> HPAI.of(null, mock(InetAddress.class), 80))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Host Protocol is required.");
        assertThatThrownBy(() -> HPAI.of(mock(HostProtocol.class), null, 80))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Address is required.");

        // length out of range
        assertThatThrownBy(() -> HPAI.of(mock(HostProtocol.class), mock(InetAddress.class), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Port is out of range. Expected [0..65535] but was: -1");
        assertThatThrownBy(() -> HPAI.of(mock(HostProtocol.class), mock(InetAddress.class), 0xFFFF + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Port is out of range. Expected [0..65535] but was: 65536");
    }

    @Test
    @DisplayName("Invalid cases for #of(Channel)")
    void invalidCases_of_Channel() {
        // null
        assertThatThrownBy(() -> HPAI.of((Channel) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Channel is required.");

        // unsupported channel
        assertThatThrownBy(() -> HPAI.of(mock(SelectableChannel.class))) //
                .isInstanceOf(IllegalArgumentException.class) //
                .hasMessageStartingWith("HPAI is not supported for channel class: ");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        final var udpAddress = Networker.getByAddress(125, 1, 2, 3);
        final var udpHpai = HPAI.of(HostProtocol.IPV4_UDP, udpAddress, 12345);
        assertThat(udpHpai).hasToString(
                "HPAI{length=8, protocol=IPV4_UDP, address=125.1.2.3, port=12345}"
        );

        final var tcpAddress = Networker.getByAddress(126, 4, 5, 6);
        final var tcpHpai = HPAI.of(HostProtocol.IPV4_TCP, tcpAddress, 56789);
        assertThat(tcpHpai).hasToString(
                "HPAI{length=8, protocol=IPV4_TCP, address=126.4.5.6, port=56789}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ConnectionStateRequestBody.class).verify();
    }

}
