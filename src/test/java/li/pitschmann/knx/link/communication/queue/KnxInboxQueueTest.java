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

package li.pitschmann.knx.link.communication.queue;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.test.KnxBody;
import li.pitschmann.utils.Bytes;
import li.pitschmann.utils.Closeables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Test for {@link KnxInboxQueue}
 *
 * @author PITSCHR
 */
public class KnxInboxQueueTest {

    /**
     * Tests a successful inbox packet from KNX Net/IP router
     */
    @Test
    @DisplayName("Test incoming KNX packet successfully")
    public void testSuccessful() throws IOException, InterruptedException {
        var clientMock = mock(InternalKnxClient.class);
        when(clientMock.verifyChannelId(any(Body.class))).thenReturn(true);

        var byteBufferMock = mock(ByteBuffer.class);
        when(byteBufferMock.array()).thenReturn(KnxBody.DESCRIPTION_RESPONSE.getBytes());

        var localChannel = localChannel();
        var remoteChannelFake = fakeRemoteChannel(localChannel, KnxBody.DESCRIPTION_RESPONSE);

        var selectionKeyMock = mock(SelectionKey.class);
        when(selectionKeyMock.isValid()).thenReturn(true);
        when(selectionKeyMock.isReadable()).thenReturn(true);
        when(selectionKeyMock.channel()).thenReturn(localChannel);

        var selectedKeys = new HashSet<SelectionKey>();
        selectedKeys.add(selectionKeyMock);

        var selectorMock = mock(Selector.class);
        when(selectorMock.selectedKeys()).thenReturn(selectedKeys);

        var inboxQueue = spy(new KnxInboxQueue("inboxQueue", clientMock, localChannel));
        when(inboxQueue.openSelector()).thenReturn(selectorMock);

        var executor = Executors.newSingleThreadExecutor();
        try (localChannel; remoteChannelFake) {
            executor.submit(inboxQueue);
            // simply wait until next packet is available in queue
            inboxQueue.next();
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Creates a local channel
     *
     * @return DatagramChannel of local machine
     * @throws IOException
     */
    private DatagramChannel localChannel() throws IOException {
        // channel to test
        var channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(0));

        return channel;
    }

    /**
     * Simulates a remote channel (from KNX Net/IP Router) that sends the given {@code hexStream} to {@code localChannel}
     *
     * @param localChannel
     * @param hexStream
     * @return DatagramChannel of remote device
     * @throws IOException
     */
    private DatagramChannel fakeRemoteChannel(final DatagramChannel localChannel, final String hexStream) throws IOException {
        // simulate opposite channel
        var otherChannel = DatagramChannel.open();
        otherChannel.configureBlocking(false);
        otherChannel.socket().bind(new InetSocketAddress(0));
        otherChannel.register(Selector.open(), localChannel.validOps());
        localChannel.connect(new InetSocketAddress("localhost", otherChannel.socket().getLocalPort()));
        otherChannel.send(ByteBuffer.wrap(Bytes.toByteArray(hexStream)), localChannel.getLocalAddress());
        return otherChannel;
    }
}
