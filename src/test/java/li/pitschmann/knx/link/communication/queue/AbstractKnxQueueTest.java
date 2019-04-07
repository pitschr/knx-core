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
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.knx.link.exceptions.KnxWrongChannelIdException;
import li.pitschmann.test.KnxBody;
import li.pitschmann.test.MemoryAppender;
import li.pitschmann.test.MemoryLog;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link AbstractKnxQueue}
 *
 * @author PITSCHR
 */
public class AbstractKnxQueueTest {

    /**
     * Tests a successful KNX packet
     */
    @Test
    @DisplayName("Test incoming KNX packet successfully")
    public void testSuccessful() throws Exception {
        final var selectionKeyMock = mock(SelectionKey.class);

        final var selectedKeys = new HashSet<SelectionKey>();
        selectedKeys.add(selectionKeyMock);

        final var selectorMock = mock(Selector.class);
        when(selectorMock.selectedKeys()).thenReturn(selectedKeys);

        final var queue = mock(AbstractKnxQueue.class, CALLS_REAL_METHODS);
        doReturn(selectorMock).when(queue).openSelector();
        doReturn(true).when(queue).valid(any(SelectionKey.class));

        final var executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(queue);
            // verify if the action(SelectionKey) has been invoked
            assertThat(Sleeper.milliseconds(() -> {
                try {
                    verify(queue).action(selectionKeyMock);
                    return true;
                } catch (final Throwable e) {
                    return false;
                }
            }, 3000)).isTrue();
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Tests an inbox packet from KNX Net/IP device with wrong channel id information
     */
    @Test
    @MemoryLog(AbstractKnxQueue.class)
    @DisplayName("Test KNX packet with wrong channel")
    public void testInboxWrongChannel(final MemoryAppender appender) throws Exception {
        final var selectorMock = mock(Selector.class);
        when(selectorMock.select()).thenThrow(KnxWrongChannelIdException.class).thenReturn(0);

        final var queue = mock(AbstractKnxQueue.class, CALLS_REAL_METHODS);
        doReturn(selectorMock).when(queue).openSelector();

        final var executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(queue);
            // wait until task is done or wait for timeout
            // when timeout the return is false, otherwise true which passes the test
            assertThat(
                    Sleeper.milliseconds(
                            () -> appender.anyMatch(s -> s.contains("KNX packet with wrong channel retrieved")),
                            3000)
            ).isTrue();
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Tests an queue when {@link InterruptedException} was thrown
     */
    @Test
    @MemoryLog(AbstractKnxQueue.class)
    @DisplayName("Test interrupted queue")
    public void testInterruption(final MemoryAppender appender) throws Exception {
        final var selectionKeyMock = mock(SelectionKey.class);

        final var selectedKeys = new HashSet<SelectionKey>();
        selectedKeys.add(selectionKeyMock);

        final var selectorMock = mock(Selector.class);
        when(selectorMock.selectedKeys()).thenReturn(selectedKeys);

        final var queueMock = mock(AbstractKnxQueue.class, CALLS_REAL_METHODS);
        doReturn(selectorMock).when(queueMock).openSelector();
        doReturn(true).when(queueMock).valid(any(SelectionKey.class));
        doThrow(InterruptedException.class).when(queueMock).action(any(SelectionKey.class));

        final var executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(queueMock);
            // wait until task is done or wait for timeout
            // when timeout the return is false, otherwise true which passes the test
            assertThat(
                    Sleeper.milliseconds(
                            () -> appender.anyMatch(s -> s.contains("Channel is interrupted")),
                            1000)
            ).isTrue();
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Tests queue when {@link IOException} was thrown
     */
    @Test
    @MemoryLog(AbstractKnxQueue.class)
    @DisplayName("Test queue with IOException")
    public void testInboxIOException(final MemoryAppender appender) throws Exception {
        final var selectorMock = mock(Selector.class);
        when(selectorMock.select()).thenThrow(IOException.class).thenReturn(0);

        final var queueMock = mock(AbstractKnxQueue.class, CALLS_REAL_METHODS);
        doReturn(selectorMock).when(queueMock).openSelector();

        final var executor = Executors.newSingleThreadExecutor();
        try {
            final var taskFuture = executor.submit(queueMock);
            // wait until task is done or wait for timeout
            // when timeout the return is false, otherwise true which passes the test
            assertThat(
                    Sleeper.milliseconds(
                            () -> appender.anyMatch(s -> s.contains("IOException for channel")),
                            3000)
            ).isTrue();
            assertThatThrownBy(() -> taskFuture.get()).hasCauseInstanceOf(KnxException.class);
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Tests queue when an unexpected exception was thrown
     */
    @Test
    @MemoryLog(AbstractKnxQueue.class)
    @DisplayName("Test queue with an unexpected exception")
    public void testInboxCorrupted(final MemoryAppender appender) throws Exception {
        final var selectorMock = mock(Selector.class);
        when(selectorMock.select()).thenThrow(RuntimeException.class).thenReturn(0);

        final var clientMock = mock(InternalKnxClient.class);
        final var queueSpy = spy(new TestKnxQueue(clientMock)); // must inject clientMock for 'verify' invocation
        doReturn(selectorMock).when(queueSpy).openSelector();

        final var executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(queueSpy);
            // wait until task is done or wait for timeout
            // when timeout the return is false, otherwise true which passes the test
            assertThat(
                    Sleeper.milliseconds(
                            () -> appender.anyMatch(s -> s.contains("Error while processing KNX packets")),
                            3000)
            ).isTrue();
            Sleeper.seconds(1);
            // verifies if the notify plugins about error has been called
            verify(clientMock).notifyPluginsError(any(Throwable.class));
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Test {@link AbstractKnxQueue#getId()}
     */
    @Test
    @DisplayName("Check #getId()")
    public void testId() {
        final var queue = new TestKnxQueue(mock(InternalKnxClient.class));
        assertThat(queue.getId()).isEqualTo("testQueue");
    }

    /**
     * Test {@link AbstractKnxQueue#getInternalClient()}
     */
    @Test
    @DisplayName("Check #getInternalClient()")
    public void testInternalClient() {
        final var internalClient = mock(InternalKnxClient.class);

        final var queue = new TestKnxQueue(internalClient);
        assertThat(queue.getInternalClient()).isSameAs(internalClient);
    }

    /**
     * Test {@link AbstractKnxQueue#openSelector()}
     *
     * @throws IOException
     */
    @Test
    @DisplayName("Check #openSelector()")
    public void testOpenSelector() throws IOException {
        final var queue = new TestKnxQueue(mock(InternalKnxClient.class));

        assertThat(queue.openSelector()).isNotNull();
    }

    /**
     * Test {@link AbstractKnxQueue#add(Body)} and {@link AbstractKnxQueue#next()}
     *
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Check #add(Body) and #next()")
    public void testAddAndNext() throws InterruptedException {
        final var body = KnxBody.TUNNELING_ACK_BODY;

        final var queue = new TestKnxQueue(mock(InternalKnxClient.class));
        // add to queue
        queue.add(body);
        // verify if it is in queue
        assertThat(queue.next()).isSameAs(body);
    }

    /**
     * Test class for {@link AbstractKnxQueue}
     */
    private class TestKnxQueue extends AbstractKnxQueue {
        public TestKnxQueue(InternalKnxClient internalClient) {
            super("testQueue", internalClient, mock(SelectableChannel.class));
        }

        @Override
        protected int interestOps() {
            return 4711;
        }

        @Override
        protected boolean valid(SelectionKey key) {
            return true;
        }

        @Override
        protected void action(SelectionKey key) throws InterruptedException, IOException {
            // NO-OP
        }
    }
}
