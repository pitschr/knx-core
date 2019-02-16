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

import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.knx.link.exceptions.KnxWrongChannelIdException;
import li.pitschmann.test.MemoryAppender;
import li.pitschmann.test.MemoryLog;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link AbstractKnxQueue}
 *
 * @author PITSCHR
 */
public abstract class AbstractKnxQueueTest {

    /**
     * Tests an inbox packet from KNX Net/IP router with wrong channel id information
     */
    @Test
    @MemoryLog(AbstractKnxQueue.class)
    @DisplayName("Test KNX packet with wrong channel")
    public void testInboxWrongChannel(final MemoryAppender appender) throws Exception {
        var clientMock = mock(InternalKnxClient.class);
        var localChannel = localChannel();
        var inboxQueue = spy(new KnxInboxQueue("inboxQueue", clientMock, localChannel));
        var selectorMock = mock(Selector.class);
        when(inboxQueue.openSelector()).thenReturn(selectorMock);
        when(selectorMock.select()).thenThrow(KnxWrongChannelIdException.class).thenReturn(0);

        var executor = Executors.newSingleThreadExecutor();
        try (localChannel) {
            executor.submit(inboxQueue);
            // wait until task is done or wait for timeout
            // when timeout the return is false, otherwise true which passes the test
            assertThat(
                    Sleeper.milliseconds(
                            10,
                            () -> appender.anyMatch(s -> s.contains("KNX packet with wrong channel retrieved")),
                            1000)
            ).isTrue();
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Tests an inbox packet from KNX Net/IP router which has been interrupted
     */
    @Test
    @MemoryLog(AbstractKnxQueue.class)
    @DisplayName("Test queue with IOException")
    public void testInboxIOException(final MemoryAppender appender) throws Exception {
        var clientMock = mock(InternalKnxClient.class);
        var localChannel = localChannel();
        var inboxQueue = spy(new KnxInboxQueue("inboxQueue", clientMock, localChannel));
        var selectorMock = mock(Selector.class);
        when(inboxQueue.openSelector()).thenReturn(selectorMock);
        when(selectorMock.select()).thenThrow(IOException.class).thenReturn(0);

        var executor = Executors.newSingleThreadExecutor();
        try (localChannel) {
            var taskFuture = executor.submit(inboxQueue);
            // wait until task is done or wait for timeout
            // when timeout the return is false, otherwise true which passes the test
            assertThat(
                    Sleeper.milliseconds(
                            10,
                            () -> appender.anyMatch(s -> s.contains("IOException for channel")),
                            1000)
            ).isTrue();
            assertThatThrownBy(() -> taskFuture.get()).hasCauseInstanceOf(KnxException.class);
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Tests an inbox packet from KNX Net/IP router which has been interrupted
     */
    @Test
    @MemoryLog(AbstractKnxQueue.class)
    @DisplayName("Test queue with an unexpected exception")
    public void testInboxCorrupted(final MemoryAppender appender) throws Exception {
        var clientMock = mock(InternalKnxClient.class);
        var localChannel = localChannel();
        var inboxQueue = spy(new KnxInboxQueue("inboxQueue", clientMock, localChannel));
        var selectorMock = mock(Selector.class);
        when(inboxQueue.openSelector()).thenReturn(selectorMock);

        var exception = new RuntimeException("Test");
        when(selectorMock.select()).thenThrow(exception).thenReturn(0);

        var executor = Executors.newSingleThreadExecutor();
        try (localChannel) {
            var taskFuture = executor.submit(inboxQueue);
            // wait until task is done or wait for timeout
            // when timeout the return is false, otherwise true which passes the test
            assertThat(
                    Sleeper.milliseconds(
                            10,
                            () -> appender.anyMatch(s -> s.contains("Error while processing KNX packets")),
                            1000)
            ).isTrue();
            // verifies if the notify plugins about error has been called
            verify(clientMock).notifyPluginsError(exception);
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Creates a local channel
     *
     * @return DatagramChannel of local machine
     * @throws java.io.IOException
     */
    protected DatagramChannel localChannel() throws IOException {
        // channel to test
        var channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(0));

        return channel;
    }
}
