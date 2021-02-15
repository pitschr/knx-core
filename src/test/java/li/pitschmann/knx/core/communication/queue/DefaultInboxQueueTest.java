/*
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

package li.pitschmann.knx.core.communication.queue;

import li.pitschmann.knx.core.communication.InternalKnxClient;
import li.pitschmann.knx.core.test.KnxBody;
import li.pitschmann.knx.core.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link DefaultInboxQueue}
 *
 * @author PITSCHR
 */
public class DefaultInboxQueueTest {

    /**
     * Test a successful incoming packet from KNX Net/IP device to client
     */
    @Test
    @DisplayName("Test successful incoming KNX packet")
    public void testViaMock() throws Exception {
        // Tunneling Request Body is used for test
        final var body = KnxBody.TUNNELING_REQUEST_BODY;

        final var clientMock = mock(InternalKnxClient.class);
        when(clientMock.verifyChannelId(body)).thenReturn(true);

        // fill the byte buffer when channel#read(..) is called
        final var channelMock = mock(DatagramChannel.class);
        when(channelMock.read(any(ByteBuffer.class))).thenAnswer(invocation -> {
            final var byteBuffer = invocation.<ByteBuffer>getArgument(0);
            byteBuffer.put(body.toByteArray(true));
            return 0;
        });

        // add body to outbox queue
        final var queue = new DefaultInboxQueue(clientMock, channelMock);

        // execute (this will pick up the body from outbox queue and write to channel)
        final var selectionKeyMock = mock(SelectionKey.class);
        when(selectionKeyMock.channel()).thenReturn(channelMock);
        queue.action(selectionKeyMock);

        // verify
        // - add body is in inbox queue
        // - incoming body notification
        assertThat(queue.next()).isEqualTo(body);
        verify(clientMock).notifyIncomingBody(body);
    }

    /**
     * Test a successful incoming packet from KNX Net/IP device to client
     */
    @Test
    @DisplayName("Test successful incoming KNX packet (through channel)")
    public void testViaChannel() throws Exception {
        // create & connect channel
        final var localChannel = fakeChannel();
        final var remoteChannel = fakeChannel();
        localChannel.connect(new InetSocketAddress("localhost", remoteChannel.socket().getLocalPort()));
        remoteChannel.connect(new InetSocketAddress("localhost", localChannel.socket().getLocalPort()));

        // Tunneling Request Body is used for test
        final var body = KnxBody.TUNNELING_REQUEST_BODY;

        final var clientMock = mock(InternalKnxClient.class);
        when(clientMock.verifyChannelId(body)).thenReturn(true);

        // add body to outbox queue
        final var queue = new DefaultInboxQueue(clientMock, localChannel);

        // write body data to remote channel
        remoteChannel.send(ByteBuffer.wrap(body.toByteArray(true)), localChannel.getLocalAddress());

        // execute (this will pick up the body from outbox queue and write to channel)
        final var selectionKeyMock = mock(SelectionKey.class);
        when(selectionKeyMock.channel()).thenReturn(localChannel);

        // wait bit - due non-blocking it could be happen that verification is faster before the bytes were sent
        Sleeper.milliseconds(100);

        queue.action(selectionKeyMock);

        // verify
        // - add body is in inbox queue
        // - incoming body notification
        assertThat(queue.next()).isEqualTo(body);
        verify(clientMock).notifyIncomingBody(body);
    }

    /**
     * Tests the {@link DefaultOutboxQueue#interestOps()}
     */
    @Test
    @DisplayName("Test for interest ops")
    public void testInterestOpsAndKeyValidity() {
        final var queue = new DefaultInboxQueue(mock(InternalKnxClient.class), mock(SelectableChannel.class));

        // verify if interest op is READ only
        assertThat(queue.interestOps()).isEqualTo(SelectionKey.OP_READ);
    }

    /**
     * Tests the {@link DefaultInboxQueue#valid(SelectionKey)}
     */
    @Test
    @DisplayName("Test for key validity")
    public void testKeyValidity() {
        final var queue = new DefaultInboxQueue(mock(InternalKnxClient.class), mock(SelectableChannel.class));

        // verify validity of the key (should be 'valid' + 'readable')
        final var selectionKeyMock = mock(SelectionKey.class);
        when(selectionKeyMock.channel()).thenReturn(mock(DatagramChannel.class));
        assertThat(queue.valid(selectionKeyMock)).isFalse();
        when(selectionKeyMock.isReadable()).thenReturn(true);
        assertThat(queue.valid(selectionKeyMock)).isFalse();
        when(selectionKeyMock.isValid()).thenReturn(true);
        assertThat(queue.valid(selectionKeyMock)).isTrue(); // it is true only when 'valid' + 'readable' is set
    }

    /**
     * Creates a fake {@link DatagramChannel}
     *
     * @return DatagramChannel faked channel
     * @throws IOException
     */
    private DatagramChannel fakeChannel() throws IOException {
        final var channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(0));
        return channel;
    }
}
