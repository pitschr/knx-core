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

package li.pitschmann.knx.core.utils;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Class to manage pause a Thread (sleep) or wait for something
 *
 * @author PITSCHR
 */
public final class Sleeper {
    private Sleeper() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Sleep method to pause the current thread and will suppress the {@link InterruptedException}. {@code duration} is
     * the time in milliseconds
     *
     * @param duration in milliseconds
     * @return {@code true} in case the sleep has <strong>not</strong> been interrupted, otherwise {@code false}
     */
    public static boolean milliseconds(long duration) {
        var isNotInterrupted = true;
        try {
            Thread.sleep(duration);
        } catch (final InterruptedException ie) {
            // ignore
            isNotInterrupted = false;
            Thread.currentThread().interrupt();
            System.out.println("Interrupt signal received:");
            ie.printStackTrace();
        }
        return isNotInterrupted;
    }

    /**
     * Sleep method to pause the current thread and will suppress the {@link InterruptedException}. {@code duration} is
     * the time in seconds.
     *
     * @param duration in seconds
     * @return {@code true} in case the sleep has <strong>not</strong> been interrupted, otherwise {@code false}
     */
    public static boolean seconds(long duration) {
        return sleep(duration, TimeUnit.SECONDS);
    }

    /**
     * Sleep method to pause the current thread and will suppress the {@link InterruptedException}. {@code duration} is
     * the time in defined {@code unit}.
     *
     * @param duration duration of timeout
     * @param unit     unit of timeout
     * @return {@code true} in case the sleep has <strong>not</strong> been interrupted, otherwise {@code false}
     */
    public static boolean sleep(long duration, final @Nonnull TimeUnit unit) {
        return milliseconds(unit.toMillis(duration));
    }

    /**
     * Sleeps until the {@link Supplier} is meet. But, not longer than timeout. Interval is hardcoded with 10 milliseconds.
     * <p>
     * Also see: {@link #milliseconds(long, Supplier, long)}.
     *
     * @param supplier supplier returning {@link Boolean} if the criteria is meet
     * @param timeout  timeout in milliseconds
     * @return {@code true} in case the criteria was meet and sleep has <strong>not</strong> been interrupted, otherwise {@code false}
     */
    public static boolean milliseconds(final @Nonnull Supplier<Boolean> supplier, long timeout) {
        return milliseconds(10, supplier, timeout);
    }

    /**
     * Sleeps until the {@link Supplier} is meet. But, not longer than timeout.
     *
     * @param interval interval check in milliseconds
     * @param supplier supplier returning {@link Boolean} if the criteria is meet
     * @param timeout  timeout in milliseconds
     * @return {@code true} in case the criteria was meet and sleep has <strong>not</strong> been interrupted, otherwise {@code false}
     */
    public static boolean milliseconds(long interval, @Nonnull final Supplier<Boolean> supplier, long timeout) {
        Preconditions.checkArgument(interval < timeout,
                "Interval ({}) cannot be bigger than timeout ({})", interval, timeout);

        final var end = System.currentTimeMillis() + timeout;
        do {
            // return true when criteria is meet
            if (supplier.get()) {
                return true;
            }
            // return false when sleep has been interrupted or timeout expired
            else if (!milliseconds(interval) || System.currentTimeMillis() > end) {
                return false;
            }
        } while (true);
    }
}
