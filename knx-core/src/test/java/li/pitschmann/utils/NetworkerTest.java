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

import li.pitschmann.knx.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Test cases for {@link Networker} class
 *
 * @author PITSCHR
 */
public class NetworkerTest {

    /**
     * Test {@link Networker#getLocalHost()}
     */
    @Test
    public void testLocalhost() {
        final InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (final Throwable t) {
            fail("Could not obtain the local host address", t);
            throw new AssertionError();
        }

        final var hostAddress = inetAddress.getHostAddress();
        final var hostAddressAsByteArray = Stream.of(hostAddress.split("\\.")).mapToInt(Integer::valueOf).toArray();

        final var addr = Networker.getLocalHost();
        assertThat(addr.getHostAddress()).isEqualTo(hostAddress);
        assertThat(addr.getAddress()).containsExactly(hostAddressAsByteArray);

        // it should be always same instance
        assertThat(Networker.getLocalHost()).isSameAs(addr);
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
        final var datagramChannelMock = mock(DatagramChannel.class);
        final var socketAddressUdpMock = mock(SocketAddress.class);
        when(socketAddressUdpMock.toString()).thenReturn("1.2.3.4/udp");
        when(datagramChannelMock.getRemoteAddress()).thenReturn(socketAddressUdpMock);
        assertThat(Networker.getRemoteAddressAsString(datagramChannelMock)).isEqualTo("1.2.3.4/udp");

        // test TCP
        final var socketAddressTdpMock = mock(SocketAddress.class);
        final var socketChannelMock = mock(SocketChannel.class);
        when(socketAddressTdpMock.toString()).thenReturn("1.2.3.4/tcp");
        when(socketChannelMock.getRemoteAddress()).thenReturn(socketAddressTdpMock);
        assertThat(Networker.getRemoteAddressAsString(socketChannelMock)).isEqualTo("1.2.3.4/tcp");

        // test "N/A"
        reset(datagramChannelMock);
        reset(socketChannelMock);
        assertThat(Networker.getRemoteAddressAsString(datagramChannelMock)).isEqualTo("N/A");
        assertThat(Networker.getRemoteAddressAsString(socketChannelMock)).isEqualTo("N/A");

        // test "Error"
        final var fileChannelMock = mock(FileChannel.class);
        assertThat(Networker.getRemoteAddressAsString(fileChannelMock)).isEqualTo("Error[Unsupported channel type]");
    }

    /**
     * Tests {@link Networker#getLocalAddressAsString(Channel)}
     */
    @Test
    public void testLocalAddressAsString() throws IOException {
        // test UDP
        final var datagramChannelMock = mock(DatagramChannel.class);
        final var socketAddressUdpMock = mock(SocketAddress.class);
        when(socketAddressUdpMock.toString()).thenReturn("5.6.7.8/udp");
        when(datagramChannelMock.getLocalAddress()).thenReturn(socketAddressUdpMock);
        assertThat(Networker.getLocalAddressAsString(datagramChannelMock)).isEqualTo("5.6.7.8/udp");

        // test TCP
        final var socketAddressTdpMock = mock(SocketAddress.class);
        final var socketChannelMock = mock(SocketChannel.class);
        when(socketAddressTdpMock.toString()).thenReturn("5.6.7.8/tcp");
        when(socketChannelMock.getLocalAddress()).thenReturn(socketAddressTdpMock);
        assertThat(Networker.getLocalAddressAsString(socketChannelMock)).isEqualTo("5.6.7.8/tcp");

        // test "N/A"
        reset(datagramChannelMock);
        reset(socketChannelMock);
        assertThat(Networker.getLocalAddressAsString(datagramChannelMock)).isEqualTo("N/A");
        assertThat(Networker.getLocalAddressAsString(socketChannelMock)).isEqualTo("N/A");

        // test "Error"
        final var fileChannelMock = mock(FileChannel.class);
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
