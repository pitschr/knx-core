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

package li.pitschmann.knx.core.utils;

import li.pitschmann.knx.core.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Helper class to close some objects silently or with additional functionality.
 */
public final class Closeables {
    private static final Logger log = LoggerFactory.getLogger(Closeables.class);

    private Closeables() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Closes the given {@link DatagramChannel} quietly (no throwable)
     *
     * @param channel the channel that should be closed quietly
     * @return {@code true} if the close was gracefully, otherwise {@code false}
     */
    public static boolean closeQuietly(final @Nullable Channel channel) {
        log.trace("Call 'closeQuietly(Channel)' method");

        var isOk = true;
        if (channel instanceof DatagramChannel) {
            try {
                ((DatagramChannel) channel).disconnect();
            } catch (final Throwable t) {
                log.warn("Throwable caught during disconnect: {}", channel, t);
                isOk = false;
            }
        }
        isOk &= closeQuietly((AutoCloseable) channel);
        return isOk;
    }

    /**
     * Closes the given {@link AutoCloseable} quietly (no throwable)
     *
     * @param closeable the {@link AutoCloseable} that should be closed quietly
     * @return {@code true} if the close was gracefully, otherwise {@code false}
     */
    public static boolean closeQuietly(final @Nullable AutoCloseable closeable) {
        log.trace("Call 'closeQuietly(Closeable)' method");
        var isOk = true;
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final Throwable t) {
                log.warn("Throwable caught during closing: {}", closeable, t);
                isOk = false;
            }
        }
        return isOk;
    }

    /**
     * Shuts down the {@link ExecutorService} quietly (no throwable) immediately.
     *
     * @param executorService the executor service that should be closed quietly
     * @return {@code true} if the shutdown was gracefully, otherwise {@code false}
     */
    public static boolean shutdownQuietly(final @Nullable ExecutorService executorService) {
        return shutdownQuietly(executorService, 0, null);
    }

    /**
     * Shuts down the {@link ExecutorService} quietly (no throwable) with
     * {@link ExecutorService#awaitTermination(long, TimeUnit)}
     *
     * @param executorService the executor service that should be closed quietly
     * @param timeout         the maximum timeout number until when the executor service should be closed
     * @param timeUnit        the timeout unit until when the executor should be closed
     * @return {@code true} if the shutdown was gracefully, otherwise {@code false}
     */
    public static boolean shutdownQuietly(final @Nullable ExecutorService executorService,
                                          final long timeout,
                                          final @Nullable TimeUnit timeUnit) {
        log.trace("Call 'shutdownQuietly(ExecutorService, long, TimeUnit)' method: {}, {}, {}", executorService, timeout, timeUnit);
        var isOk = true;
        if (executorService != null) {
            executorService.shutdown();
            if (timeout > 0 && timeUnit != null) {
                try {
                    executorService.awaitTermination(timeout, timeUnit);
                } catch (final InterruptedException ie) {
                    isOk = false;
                    Thread.currentThread().interrupt();
                }
            }
            if (!executorService.isTerminated()) {
                isOk &= executorService.shutdownNow().isEmpty();
            }
        }
        return isOk;
    }
}
