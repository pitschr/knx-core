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

import java.util.concurrent.*;

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
     * @param duration
     * @return {@code true} in case the sleep has <strong>not</strong> been interrupted, otherwise {@code false}
     */
    public static boolean milliseconds(long duration) {
        boolean isNotInterrupted = true;
        try {
            Thread.sleep(duration);
        } catch (final InterruptedException ie) {
            // ignore
            isNotInterrupted = false;
            Thread.currentThread().interrupt();
        }
        return isNotInterrupted;
    }

    /**
     * Sleep method to pause the current thread and will suppress the {@link InterruptedException}. {@code duration} is
     * the time in seconds.
     *
     * @param duration
     * @return {@code true} in case the sleep has been interrupted, otherwise {@code false}
     */
    public static boolean seconds(long duration) {
        return sleep(duration, TimeUnit.SECONDS);
    }

    /**
     * Sleep method to pause the current thread and will suppress the {@link InterruptedException}. {@code duration} is
     * the time in defined {@code unit}.
     *
     * @param duration
     * @param unit
     * @return {@code true} in case the sleep has been interrupted, otherwise {@code false}
     */
    public static boolean sleep(long duration, final TimeUnit unit) {
        return milliseconds(unit.toMillis(duration));
    }
}
