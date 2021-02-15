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
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.exceptions.KnxWrongChannelIdException;
import li.pitschmann.knx.core.test.KnxBody;
import li.pitschmann.knx.core.test.TestHelpers;
import li.pitschmann.knx.core.utils.Closeables;
import li.pitschmann.knx.core.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link AbstractKnxQueue}
 *
 * @author PITSCHR
 */
public class AbstractKnxQueueTest {

    @Test
    @DisplayName("OK: Test incoming KNX packet")
    public void testSuccessful() throws Exception {
        final var selectionKeyMock = mock(SelectionKey.class);

        final var selectedKeys = new HashSet<SelectionKey>();
        selectedKeys.add(selectionKeyMock);

        final var selectorMock = mock(Selector.class);
        when(selectorMock.selectedKeys()).thenReturn(selectedKeys);

        final var queueSpy = spy(new TestKnxQueue(mock(InternalKnxClient.class)));
        doReturn(selectorMock).when(queueSpy).openSelector();
        doReturn(true).when(queueSpy).valid(any(SelectionKey.class));

        //
        // Verification
        //
        final var executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(queueSpy);
            // verify if the action(SelectionKey) has been invoked
            assertThat(Sleeper.milliseconds(() -> {
                try {
                    verify(queueSpy).action(selectionKeyMock);
                    return true;
                } catch (final Throwable e) {
                    return false;
                }
            }, 3000)).isTrue();
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    @Test
    @DisplayName("ERROR: Test KNX packet with wrong channel id")
    public void testInboxWrongChannel() throws Exception {
        final var selectorMock = mock(Selector.class);
        when(selectorMock.select()).thenThrow(KnxWrongChannelIdException.class).thenReturn(0);

        final var clientMock = mock(InternalKnxClient.class);
        final var queueSpy = spy(new TestKnxQueue(clientMock));
        doReturn(selectorMock).when(queueSpy).openSelector();

        //
        // Verification
        //
        final var executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(queueSpy);
            // wait bit as the task is async
            Sleeper.milliseconds(() -> !mockingDetails(clientMock).getInvocations().isEmpty(), 1000);
            // verifies if the notify plugins about error has been called
            final var captor = ArgumentCaptor.forClass(Throwable.class);
            verify(clientMock).notifyError(captor.capture());
            assertThat(captor.getValue()).isInstanceOf(KnxWrongChannelIdException.class);
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Tests an queue when {@link InterruptedException} was thrown
     */
    @Test
    @DisplayName("ERROR: Test interrupted queue")
    public void testInterruption() throws Exception {
        final var selectionKeyMock = mock(SelectionKey.class);

        final var selectedKeys = new HashSet<SelectionKey>();
        selectedKeys.add(selectionKeyMock);

        final var selectorMock = mock(Selector.class);
        when(selectorMock.selectedKeys()).thenReturn(selectedKeys);

        final var clientMock = mock(InternalKnxClient.class);
        final var queueSpy = spy(new TestKnxQueue(clientMock));
        doReturn(selectorMock).when(queueSpy).openSelector();
        doReturn(true).when(queueSpy).valid(any(SelectionKey.class));
        doThrow(InterruptedException.class).when(queueSpy).action(any(SelectionKey.class));

        //
        // Verification
        //
        final var executor = Executors.newSingleThreadExecutor();
        try {
            final var taskFuture = executor.submit(queueSpy);
            assertThat(taskFuture.get()).isNull();
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Tests queue when {@link IOException} was thrown
     */
    @Test
    @DisplayName("ERROR: Test queue with IOException")
    public void testInboxIOException() throws Exception {
        final var selectorMock = mock(Selector.class);
        when(selectorMock.select()).thenThrow(IOException.class).thenReturn(0);

        final var clientMock = mock(InternalKnxClient.class);
        final var queueSpy = spy(new TestKnxQueue(clientMock));
        doReturn(selectorMock).when(queueSpy).openSelector();

        //
        // Verification
        //
        final var executor = Executors.newSingleThreadExecutor();
        try {
            final var taskFuture = executor.submit(queueSpy);
            assertThatThrownBy(taskFuture::get).hasCauseInstanceOf(KnxException.class);
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    /**
     * Tests queue when an unexpected exception was thrown
     */
    @Test
    @DisplayName("ERROR: Test queue with an unexpected exception")
    public void testInboxCorrupted() throws Exception {
        final var selectorMock = mock(Selector.class);
        when(selectorMock.select()).thenThrow(RuntimeException.class).thenReturn(0);

        final var clientMock = mock(InternalKnxClient.class);
        final var queueSpy = spy(new TestKnxQueue(clientMock)); // must inject clientMock for 'verify' invocation
        doReturn(selectorMock).when(queueSpy).openSelector();

        //
        // Verification
        //
        final var executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(queueSpy);
            // wait bit as the task is async
            Sleeper.milliseconds(() -> !mockingDetails(clientMock).getInvocations().isEmpty(), 1000);
            // verifies if the notify plugins about error has been called
            final var captor = ArgumentCaptor.forClass(Throwable.class);
            verify(clientMock).notifyError(captor.capture());
            assertThat(captor.getValue()).hasMessage("Error while processing KNX packets.");
        } finally {
            Closeables.shutdownQuietly(executor);
        }
    }

    @Test
    @DisplayName("Check #getInternalClient()")
    public void testInternalClient() {
        final var internalClientMock = TestHelpers.mockInternalKnxClient();

        final var queue = new TestKnxQueue(internalClientMock);
        assertThat(queue.getInternalClient()).isSameAs(internalClientMock);
    }

    @Test
    @DisplayName("Check #openSelector()")
    public void testOpenSelector() throws IOException {
        final var queue = new TestKnxQueue(mock(InternalKnxClient.class));

        assertThat(queue.openSelector()).isNotNull();
    }

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
    private class TestKnxQueue extends AbstractKnxQueue<ByteChannel> {
        public TestKnxQueue(final InternalKnxClient internalClient) {
            super(internalClient, mock(SelectableChannel.class));
        }

        @Override
        protected int interestOps() {
            return 4711;
        }

        @Override
        protected boolean valid(final SelectionKey key) {
            return true;
        }

        @Override
        protected void action(final SelectionKey key) throws InterruptedException, IOException {
            // NO-OP
        }
    }
}
