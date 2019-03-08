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

package li.pitschmann.utils;

import li.pitschmann.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for {@link Networker} class
 *
 * @author PITSCHR
 */
public class NetworkerTest {

    /**
     * Test {@link Networker#getLocalhost()}
     */
    @Test
    public void testLocalhost() {
        final var addr = Networker.getLocalhost();
        assertThat(addr.getHostAddress()).isEqualTo("127.0.0.1");
        assertThat(addr.getAddress()).containsExactly(new byte[]{127, 0, 0, 1});

        // it should be always same instance
        assertThat(Networker.getLocalhost()).isSameAs(addr);
    }

    /**
     * Test {@link Networker#getAddressUnbound()}
     */
    @Test
    public void testByAddressUnbound() {
        final var addr = Networker.getAddressUnbound();
        assertThat(addr.getHostAddress()).isEqualTo("0.0.0.0");
        assertThat(addr.getAddress()).containsExactly(new byte[4]);
    }

    /**
     * Test {@link Networker#getByAddress(int, int, int, int)} (and
     * {@link Networker#getByAddress(byte, byte, byte, byte)} indirectly)
     */
    @Test
    public void testByAddress() {
        final var addr = Networker.getByAddress("1.2.3.4");
        assertThat(addr.getHostAddress()).isEqualTo("1.2.3.4");

        // OK
        assertThat(Networker.getByAddress(0, 0, 0, 0)).isInstanceOf(InetAddress.class);
        assertThat(Networker.getByAddress(255, 255, 255, 255)).isInstanceOf(InetAddress.class);
        assertThat(Networker.getByAddress("0.0.0.0")).isInstanceOf(InetAddress.class);
        assertThat(Networker.getByAddress("255.255.255.255")).isInstanceOf(InetAddress.class);

        // failures
        assertThatThrownBy(() -> Networker.getByAddress(-1, 0, 0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Networker.getByAddress(0, -1, 0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Networker.getByAddress(0, 0, -1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Networker.getByAddress(0, 0, 0, -1)).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Networker.getByAddress(256, 0, 0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Networker.getByAddress(0, 256, 0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Networker.getByAddress(0, 0, 256, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Networker.getByAddress(0, 0, 0, 256)).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Networker.getByAddress(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Networker.getByAddress("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Networker.getByAddress("-1.0.0.0")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Networker.getByAddress("256.0.0.0")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Networker.getByAddress("0.0.0")).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests {@link Networker#getRemoteAddressAsString(Channel)}
     */
    @Test
    public void testRemoteAddressAsString() throws IOException {
        // test UDP
        final var datagramChannelMock = Mockito.mock(DatagramChannel.class);
        final var socketAddressUdpMock = Mockito.mock(SocketAddress.class);
        Mockito.when(socketAddressUdpMock.toString()).thenReturn("1.2.3.4/udp");
        Mockito.when(datagramChannelMock.getRemoteAddress()).thenReturn(socketAddressUdpMock);
        assertThat(Networker.getRemoteAddressAsString(datagramChannelMock)).isEqualTo("1.2.3.4/udp");

        // test TCP
        final var socketAddressTdpMock = Mockito.mock(SocketAddress.class);
        final var socketChannelMock = Mockito.mock(SocketChannel.class);
        Mockito.when(socketAddressTdpMock.toString()).thenReturn("1.2.3.4/tcp");
        Mockito.when(socketChannelMock.getRemoteAddress()).thenReturn(socketAddressTdpMock);
        assertThat(Networker.getRemoteAddressAsString(socketChannelMock)).isEqualTo("1.2.3.4/tcp");

        // test "N/A"
        Mockito.reset(datagramChannelMock);
        Mockito.reset(socketChannelMock);
        assertThat(Networker.getRemoteAddressAsString(datagramChannelMock)).isEqualTo("N/A");
        assertThat(Networker.getRemoteAddressAsString(socketChannelMock)).isEqualTo("N/A");

        // test "Error"
        final var fileChannelMock = Mockito.mock(FileChannel.class);
        assertThat(Networker.getRemoteAddressAsString(fileChannelMock)).isEqualTo("Error[Unsupported channel type]");
    }

    /**
     * Tests {@link Networker#getLocalAddressAsString(Channel)}
     */
    @Test
    public void testLocalAddressAsString() throws IOException {
        // test UDP
        final var datagramChannelMock = Mockito.mock(DatagramChannel.class);
        final var socketAddressUdpMock = Mockito.mock(SocketAddress.class);
        Mockito.when(socketAddressUdpMock.toString()).thenReturn("5.6.7.8/udp");
        Mockito.when(datagramChannelMock.getLocalAddress()).thenReturn(socketAddressUdpMock);
        assertThat(Networker.getLocalAddressAsString(datagramChannelMock)).isEqualTo("5.6.7.8/udp");

        // test TCP
        final var socketAddressTdpMock = Mockito.mock(SocketAddress.class);
        final var socketChannelMock = Mockito.mock(SocketChannel.class);
        Mockito.when(socketAddressTdpMock.toString()).thenReturn("5.6.7.8/tcp");
        Mockito.when(socketChannelMock.getLocalAddress()).thenReturn(socketAddressTdpMock);
        assertThat(Networker.getLocalAddressAsString(socketChannelMock)).isEqualTo("5.6.7.8/tcp");

        // test "N/A"
        Mockito.reset(datagramChannelMock);
        Mockito.reset(socketChannelMock);
        assertThat(Networker.getLocalAddressAsString(datagramChannelMock)).isEqualTo("N/A");
        assertThat(Networker.getLocalAddressAsString(socketChannelMock)).isEqualTo("N/A");

        // test "Error"
        final var fileChannelMock = Mockito.mock(FileChannel.class);
        assertThat(Networker.getLocalAddressAsString(fileChannelMock)).isEqualTo("Error[Unsupported channel type]");
    }

    /**
     * Test constructor of {@link Networker}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Networker.class);
    }
}
