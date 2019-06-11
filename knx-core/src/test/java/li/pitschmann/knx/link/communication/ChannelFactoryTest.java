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

package li.pitschmann.knx.link.communication;

import li.pitschmann.knx.link.Configuration;
import li.pitschmann.knx.link.exceptions.KnxCommunicationException;
import li.pitschmann.knx.test.TestHelpers;
import li.pitschmann.utils.Networker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link ChannelFactory}
 *
 * @author PITSCHR
 */
public class ChannelFactoryTest {
    /**
     * Test constructor of {@link ChannelFactory}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(ChannelFactory.class);
    }

    /**
     * Test creation of description channel using {@link ChannelFactory#newDescriptionChannel(Configuration)}
     *
     * @throws SocketException
     */
    @Test
    public void testNewDescriptionChannel() throws SocketException {
        final var configMock = mock(Configuration.class);
        when(configMock.getEndpoint()).thenReturn(new InetSocketAddress(Networker.getByAddress(1, 2, 3, 4), 4321));
        when(configMock.getSocketTimeoutDescriptionChannel()).thenReturn(1000L);

        final var channel = ChannelFactory.newDescriptionChannel(configMock);
        assertThat(channel).isNotNull();
        assertThat(channel).isInstanceOf(DatagramChannel.class);

        final var datagramChannel = (DatagramChannel) channel;
        assertThat(datagramChannel.socket().getInetAddress().getHostAddress()).isEqualTo("1.2.3.4");
        assertThat(datagramChannel.socket().getPort()).isEqualTo(4321);
        assertThat(datagramChannel.socket().getSoTimeout()).isEqualTo(1000);
    }

    /**
     * Test creation of control channel using {@link ChannelFactory#newControlChannel(Configuration)}
     *
     * @throws SocketException
     */
    @Test
    public void testNewControlChannel() throws SocketException {
        final var configMock = mock(Configuration.class);
        when(configMock.getEndpoint()).thenReturn(new InetSocketAddress(Networker.getByAddress(2, 3, 4, 5), 5432));
        when(configMock.getSocketTimeoutControlChannel()).thenReturn(2000L);

        final var channel = ChannelFactory.newControlChannel(configMock);
        assertThat(channel).isNotNull();
        assertThat(channel).isInstanceOf(DatagramChannel.class);

        final var datagramChannel = (DatagramChannel) channel;
        assertThat(datagramChannel.socket().getInetAddress().getHostAddress()).isEqualTo("2.3.4.5");
        assertThat(datagramChannel.socket().getPort()).isEqualTo(5432);
        assertThat(datagramChannel.socket().getSoTimeout()).isEqualTo(2000);
    }

    /**
     * Test creation of data channel using {@link ChannelFactory#newDataChannel(Configuration)}
     *
     * @throws SocketException
     */
    @Test
    public void testNewDataChannel() throws SocketException {
        final var configMock = mock(Configuration.class);
        when(configMock.getEndpoint()).thenReturn(new InetSocketAddress(Networker.getByAddress(3, 4, 5, 6), 6543));
        when(configMock.getSocketTimeoutDataChannel()).thenReturn(3000L);

        final var channel = ChannelFactory.newDataChannel(configMock);
        assertThat(channel).isNotNull();
        assertThat(channel).isInstanceOf(DatagramChannel.class);

        final var datagramChannel = (DatagramChannel) channel;
        assertThat(datagramChannel.socket().getInetAddress().getHostAddress()).isEqualTo("3.4.5.6");
        assertThat(datagramChannel.socket().getPort()).isEqualTo(6543);
        assertThat(datagramChannel.socket().getSoTimeout()).isEqualTo(3000);
    }

    /**
     * Test failure when creating a new data channel (because the socket address is 'unresolved')
     */
    @Test
    public void testFailure() {
        final var socketAddressMock = mock(InetSocketAddress.class);
        when(socketAddressMock.isUnresolved()).thenReturn(true);

        final var configMock = mock(Configuration.class);
        when(configMock.getEndpoint()).thenReturn(socketAddressMock);
        when(configMock.getSocketTimeoutDataChannel()).thenReturn(4000L);

        assertThatThrownBy(() -> ChannelFactory.newDataChannel(configMock)).isInstanceOf(KnxCommunicationException.class);
    }
}
