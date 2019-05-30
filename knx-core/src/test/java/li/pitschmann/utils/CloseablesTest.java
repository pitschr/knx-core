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

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test cases for {@link Closeables} class
 *
 * @author PITSCHR
 */
public class CloseablesTest {

    /**
     * Test {@link Closeables#closeQuietly(Channel)}
     */
    @Test
    public void testCloseQuietly() throws IOException {
        // close null
        assertThat(Closeables.closeQuietly(null)).isTrue();

        // close Closeable successfully
        final var closeableMock = mock(Closeable.class);
        assertThat(Closeables.closeQuietly(closeableMock)).isTrue();

        // close Closeable with throwing exception
        doThrow(new IOException()).when(closeableMock).close();
        assertThat(Closeables.closeQuietly(closeableMock)).isFalse();

        // close DatagramChannel successfully
        final var datagramChannelMock = mock(DatagramChannel.class);
        assertThat(Closeables.closeQuietly(datagramChannelMock)).isTrue();

        // close DatagramChannel with throwing exception
        doThrow(new IOException()).when(datagramChannelMock).disconnect();
        assertThat(Closeables.closeQuietly(datagramChannelMock)).isFalse();
    }

    /**
     * Test {@link Closeables#shutdownQuietly(ExecutorService)}
     */
    @Test
    public void testShutdownQuietly() {
        // shutdown null
        assertThat(Closeables.shutdownQuietly(null)).isTrue();

        // shutdown successfully
        final var executorServiceMock = mock(ExecutorService.class);
        when(executorServiceMock.isTerminated()).thenReturn(true);
        assertThat(Closeables.shutdownQuietly(executorServiceMock)).isTrue();

        // shutdown successfully with termination (empty list)
        when(executorServiceMock.isTerminated()).thenReturn(false);
        assertThat(Closeables.shutdownQuietly(executorServiceMock)).isTrue();

        // shutdown successfully with termination (non-empty list)
        final var runnableMock = mock(Runnable.class);
        when(executorServiceMock.shutdownNow()).thenReturn(List.of(runnableMock)); // not empty
        assertThat(Closeables.shutdownQuietly(executorServiceMock)).isFalse();
    }

    /**
     * Test {@link Closeables#shutdownQuietly(ExecutorService, long, TimeUnit)}
     */
    @Test
    public void testShutdownQuietlyWithTime() throws InterruptedException {
        // shutdown successfully
        final var executorServiceMock = mock(ExecutorService.class);
        when(executorServiceMock.isTerminated()).thenReturn(true);
        when(executorServiceMock.awaitTermination(1, TimeUnit.MILLISECONDS)).thenReturn(true);
        assertThat(Closeables.shutdownQuietly(executorServiceMock, 1, TimeUnit.MILLISECONDS)).isTrue();

        // shutdown with throwing exception
        // run mock in an isolated class because JUnit parallelism may fail when
        // Thread.currentThread().interrupt() is called
        final var es = Executors.newSingleThreadExecutor();
        es.submit(() -> {
            try {
                doThrow(new InterruptedException()).when(executorServiceMock).awaitTermination(2, TimeUnit.MILLISECONDS);
                assertThat(Closeables.shutdownQuietly(executorServiceMock, 2, TimeUnit.MILLISECONDS)).isFalse();
            } catch (final InterruptedException ie) {
                fail("InterruptedException caught here! Expected is that InterruptedException is thrown quietly.");
            }
        });
        es.shutdown();
        es.awaitTermination(3, TimeUnit.SECONDS);
        es.shutdownNow();

        // shutdown with invalid time (no timeout or/and missing time unit)
        assertThat(Closeables.shutdownQuietly(executorServiceMock, 0, TimeUnit.MILLISECONDS)).isTrue();
        assertThat(Closeables.shutdownQuietly(executorServiceMock, 2, null)).isTrue();
        assertThat(Closeables.shutdownQuietly(executorServiceMock, 0, null)).isTrue();
    }

    /**
     * Test constructor of {@link Closeables}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Closeables.class);
    }

}
