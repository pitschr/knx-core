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
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link DefaultOutboxQueue}
 *
 * @author PITSCHR
 */
public class DefaultOutboxQueueTest {

    /**
     * Test a successful outgoing packet from client
     */
    @Test
    @DisplayName("Test successful outgoing KNX packet")
    public void testViaMock() throws Exception {
        final var clientMock = mock(InternalKnxClient.class);
        final var channelMock = mock(DatagramChannel.class);
        when(channelMock.isOpen()).thenReturn(Boolean.TRUE);

        final var selectionKeyMock = mock(SelectionKey.class);
        when(selectionKeyMock.channel()).thenReturn(channelMock);

        // Tunneling Request Body is used for test
        final var body = KnxBody.TUNNELING_REQUEST_BODY;

        // add body to outbox queue
        final var queue = new DefaultOutboxQueue(clientMock, channelMock);
        queue.send(body);

        // execute (this will pick up the body from outbox queue and write to channel)
        queue.action(selectionKeyMock);

        // verify
        // - bytes are written
        // - outgoing body notification
        // capture what is written to channel
        final var byteBufferCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        verify(channelMock).write(byteBufferCaptor.capture());
        assertThat(byteBufferCaptor.getValue().array()).containsExactly(body.toByteArray(true));
        verify(clientMock).notifyOutgoingBody(body);
    }

    /**
     * Test a successful outgoing packet from client to KNX Net/IP device via Channel
     */
    @Test
    @DisplayName("Test successful outgoing KNX packet (through channel)")
    public void testViaChannel() throws Exception {
        final var localChannel = fakeChannel();
        final var remoteChannel = fakeChannel();

        // connect channel
        localChannel.connect(new InetSocketAddress("localhost", remoteChannel.socket().getLocalPort()));
        remoteChannel.connect(new InetSocketAddress("localhost", localChannel.socket().getLocalPort()));

        final var clientMock = mock(InternalKnxClient.class);
        final var selectionKeyMock = mock(SelectionKey.class);
        when(selectionKeyMock.channel()).thenReturn(localChannel);

        // Tunneling Request Body is used for test
        final var body = KnxBody.TUNNELING_REQUEST_BODY;

        // add body to outbox queue
        final var queue = new DefaultOutboxQueue(clientMock, localChannel);
        queue.send(body);

        // execute (this will pick up the body from outbox queue and write to remote channel)
        queue.action(selectionKeyMock);

        // wait bit - due non-blocking it could be happen that verification is faster before the bytes were sent
        Sleeper.milliseconds(100);

        // verify if remote channel got those bytes
        final var bb = ByteBuffer.allocate(body.toByteArray(true).length);
        remoteChannel.read(bb);
        assertThat(bb.array()).containsExactly(body.toByteArray(true));
    }

    /**
     * Tests the {@link DefaultOutboxQueue#interestOps()}
     */
    @Test
    @DisplayName("Test for interest ops")
    public void testInterestOpsAndKeyValidity() {
        final var queue = new DefaultOutboxQueue(mock(InternalKnxClient.class), mock(SelectableChannel.class));

        // verify if interest op is WRITE only
        assertThat(queue.interestOps()).isEqualTo(SelectionKey.OP_WRITE);
    }

    /**
     * Tests the {@link DefaultOutboxQueue#valid(SelectionKey)}
     */
    @Test
    @DisplayName("Test for key validity")
    public void testKeyValidity() {
        final var queue = new DefaultOutboxQueue(mock(InternalKnxClient.class), mock(SelectableChannel.class));

        // verify validity of the key (should be 'valid' + 'writable')
        final var selectionKeyMock = mock(SelectionKey.class);
        when(selectionKeyMock.channel()).thenReturn(mock(DatagramChannel.class));
        assertThat(queue.valid(selectionKeyMock)).isFalse();
        when(selectionKeyMock.isWritable()).thenReturn(true);
        assertThat(queue.valid(selectionKeyMock)).isFalse();
        when(selectionKeyMock.isValid()).thenReturn(true);
        assertThat(queue.valid(selectionKeyMock)).isTrue(); // it is true only when 'valid' + 'writable' is set
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
