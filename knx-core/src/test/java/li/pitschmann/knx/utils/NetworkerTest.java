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

package li.pitschmann.knx.utils;

import li.pitschmann.knx.link.body.hpai.HPAI;
import li.pitschmann.knx.link.body.hpai.HostProtocol;
import li.pitschmann.knx.link.config.CoreConfigs;
import li.pitschmann.knx.link.exceptions.KnxCommunicationException;
import li.pitschmann.knx.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.MulticastChannel;
import java.nio.channels.SocketChannel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
        final var localHost = Networker.getLocalHost();
        assertThat(localHost.getHostAddress()).isEqualTo("127.0.0.1");
        assertThat(localHost.getAddress()).containsExactly(new byte[]{127, 0, 0, 1});

        // it should be always same instance
        assertThat(Networker.getLocalHost()).isSameAs(localHost);
        assertThat(Networker.getByAddress("localhost")).isSameAs(localHost);
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
     * Tests {@link Networker#toInetSocketAddress(HPAI)}
     */
    @Test
    @DisplayName("Test conversion from HPAI to InetSocketAddress")
    public void testToInetSocketAddress() {
        final var address = Networker.getByAddress(1, 2, 3, 4);
        final var port = 5678;
        final var hpai = HPAI.of(HostProtocol.IPV4_UDP, address, port);
        final var socketAddress = Networker.toInetSocketAddress(hpai);

        assertThat(socketAddress.getAddress()).isEqualTo(address);
        assertThat(socketAddress.getPort()).isEqualTo(port);
    }

    /**
     * Tests {@link Networker#getNetworkInterfaces()} returning a map of {@link NetworkInterface} as key
     * and list of {@link Inet4Address} as values.
     */
    @Test
    @DisplayName("Test gathering the network interfaces including IPv4 addresses")
    public void testNetworkInterfaces() throws SocketException {
        final var netInterfaces = Networker.getNetworkInterfaces();

        // at least one network interface available
        assertThat(netInterfaces).isNotEmpty();
        // should return exactly same instance (cached!)
        assertThat(Networker.getNetworkInterfaces()).isSameAs(netInterfaces);

        // all network interfaces should be up + list of addresses should be IPv4 only
        for (final var netInterface : netInterfaces.entrySet()) {
            assertThat(netInterface.getKey().isUp()).isTrue();
            for (final var address : netInterface.getValue()) {
                assertThat(address).isInstanceOf(Inet4Address.class);
            }
        }

        // should contain only one loopback address
        final var numberOfLoopbackAddress = netInterfaces.values().stream().flatMap(v -> v.stream()).filter(InetAddress::isLoopbackAddress).count();
        assertThat(numberOfLoopbackAddress).isEqualTo(1);
    }

    /**
     * Test {@link Networker#joinChannels(MulticastChannel, InetAddress)}
     */
    @Test
    @DisplayName("Test join channels (multi-cast)")
    public void testJoinChannels() throws IOException {
        final var channel = mock(MulticastChannel.class);
        final var membershipKey = mock(MembershipKey.class);

        // test success
        when(channel.join(any(InetAddress.class), any(NetworkInterface.class))).thenReturn(membershipKey);
        final var membershipKeys = Networker.joinChannels(channel, CoreConfigs.MULTICAST_ADDRESS);
        assertThat(membershipKeys).hasSize(Networker.getNetworkInterfaces().size()); // should have same size
        assertThat(membershipKeys.get(0)).isSameAs(membershipKey);
        assertThatThrownBy(() -> membershipKeys.add(membershipKey)).isInstanceOf(UnsupportedOperationException.class); // should be immutable

        // test failure
        when(channel.join(any(InetAddress.class), any(NetworkInterface.class))).thenThrow(new IOException("Test I/O Exception"));
        assertThatThrownBy(() -> Networker.joinChannels(channel, CoreConfigs.MULTICAST_ADDRESS)).isInstanceOf(KnxCommunicationException.class);
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
