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

import org.slf4j.*;

import javax.annotation.*;
import java.nio.channels.*;
import java.util.concurrent.*;

/**
 * Helper class to close some objects silently or with additional functionality.
 */
public final class Closeables {
    private static final Logger LOG = LoggerFactory.getLogger(Closeables.class);

    private Closeables() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Closes the given {@link DatagramChannel} quietly (no throwable)
     *
     * @param channel
     * @return {@code true} if the close was gracefully, otherwise {@code false}
     */
    public static boolean closeQuietly(final @Nullable Channel channel) {
        LOG.trace("Call 'closeQuietly(DatagramChannel)' method");

        boolean isOk = true;
        if (channel instanceof DatagramChannel) {
            try {
                ((DatagramChannel) channel).disconnect();
            } catch (final Exception ex) {
                LOG.warn("Exception caught during disconnect: {}", channel, ex);
                isOk = false;
            }
        }
        isOk &= closeQuietly((AutoCloseable) channel);
        return isOk;
    }

    /**
     * Closes the given {@link AutoCloseable} quietly (no throwable)
     *
     * @param closeable
     * @return {@code true} if the close was gracefully, otherwise {@code false}
     */
    public static boolean closeQuietly(final @Nullable AutoCloseable closeable) {
        LOG.trace("Call 'closeQuietly(Closeable)' method");
        boolean isOk = true;
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final Exception ex) {
                LOG.warn("Exception caught during closing: {}", closeable, ex);
                isOk = false;
            }
        }
        return isOk;
    }

    /**
     * Shuts down the {@link ExecutorService} quietly (no throwable) immediately.
     *
     * @param executorService
     * @return {@code true} if the shutdown was gracefully, otherwise {@code false}
     */
    public static boolean shutdownQuietly(final @Nullable ExecutorService executorService) {
        return shutdownQuietly(executorService, 0, null);
    }

    /**
     * Shuts down the {@link ExecutorService} quietly (no throwable) with
     * {@link ExecutorService#awaitTermination(long, TimeUnit)}
     *
     * @param executorService
     * @param timeout
     * @param timeUnit
     * @return {@code true} if the shutdown was gracefully, otherwise {@code false}
     */
    public static boolean shutdownQuietly(final @Nullable ExecutorService executorService, final long timeout, final @Nullable TimeUnit timeUnit) {
        LOG.trace("Call 'shutdownQuietly(ExecutorService, long, TimeUnit)' method: {}, {}, {}", executorService, timeout, timeUnit);
        boolean isOk = true;
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
