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

import com.google.common.base.Stopwatch;
import li.pitschmann.test.TestHelpers;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for {@link Sleeper} class
 *
 * @author PITSCHR
 */
public class SleeperTest {

    /**
     * Test {@link Sleeper#milliseconds(long)}
     */
    @Test
    @DisplayName("Sleep in milliseconds")
    public void testNoInterrupt() {
        assertUpToThreeTimes(() -> {
            var sw = Stopwatch.createStarted();
            var result = Sleeper.milliseconds(100);
            var elapsedTime = sw.elapsed().toMillis();
            // verify the result
            //   true = not interrupted
            //   and between 90-110 ms
            assertThat(result).isTrue();
            assertThat(elapsedTime).isCloseTo(100, Percentage.withPercentage(10));
        });
    }

    /**
     * Test {@link Sleeper#milliseconds(Supplier, long)} where as the predicates is always TRUE
     */
    @Test
    @DisplayName("Sleep in milliseconds with predicates (always true)")
    public void testSleepWithPredicateAlwaysTrue() {
        assertUpToThreeTimes(() -> {
            var sw = Stopwatch.createStarted();
            var result = Sleeper.milliseconds(() -> true, 100);
            var elapsedTime = sw.elapsed().toMillis();
            // verify the result
            //   true = not interrupted
            //   and between 0-10ms because it is checked immediately
            assertThat(result).isTrue();
            assertThat(elapsedTime).isBetween(0L, 10L);
        });
    }

    /**
     * Test {@link Sleeper#milliseconds(Supplier, long)} where as the predicates is always FALSE
     * and therefore running into a timeout expiration.
     */
    @Test
    @DisplayName("Sleep in milliseconds with predicates (always false)")
    public void testSleepWithPredicateAlwaysFalse() {
        assertUpToThreeTimes(() -> {
            var sw = Stopwatch.createStarted();
            var result = Sleeper.milliseconds(() -> false, 100);
            var elapsedTime = sw.elapsed().toMillis();
            // verify the result
            //   false = interrupted
            //   and between 90-110 ms
            assertThat(result).isFalse();
            assertThat(elapsedTime).isCloseTo(100, Percentage.withPercentage(10));
        });
    }

    /**
     * Test {@link Sleeper#milliseconds(Supplier, long)} where as the predicates is TRUE
     * after 100 milliseconds
     */
    @Test
    @DisplayName("Sleep in milliseconds with predicates (true after 100 ms)")
    public void testSleepWithPredicate() {
        assertUpToThreeTimes(() -> {
            var start = System.currentTimeMillis();
            var sw = Stopwatch.createStarted();
            var result = Sleeper.milliseconds(() -> System.currentTimeMillis() > start + 100, 200);
            var elapsedTime = sw.elapsed().toMillis();
            // verify the result
            //   true = not interrupted
            //   and between 90-110 ms
            assertThat(result).isTrue();
            assertThat(elapsedTime).isCloseTo(100, Percentage.withPercentage(10));
        });
    }

    /**
     * Test {@link Sleeper#milliseconds(Supplier, long)} where as timeout is shorter than interval
     */
    @Test
    @DisplayName("Sleep in milliseconds where timeout is shorter than interval check")
    public void testSleepWithPredicateWrongArgument() {
        assertThatThrownBy(() -> Sleeper.milliseconds(() -> false, 5)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test {@link Sleeper#milliseconds(Supplier, long)} where as timeout is shorter than interval
     */
    @Test
    @DisplayName("Sleep in milliseconds and thread interrupt")
    public void testSleepWithPredicateTheradInterrupt() {
        var thread = new Thread(new Runnable() {
            @Override
            public void run() {
                assertThat(Sleeper.milliseconds(() -> false, 100)).isFalse();
            }
        });
        // start thread and interrupt immediately
        thread.start();
        thread.interrupt();
    }


    /**
     * Test the interruption of {@link Sleeper#seconds(long)}, {@link Sleeper#milliseconds(long)} and
     * {@link Sleeper#sleep(long, java.util.concurrent.TimeUnit)}
     * <p>
     * Starts the {@link SleepTestRunnable} and interrupts it immediately.
     */
    @Test
    public void testInterrupt() {
        final SleepTestRunnable sleepRunnable = new SleepTestRunnable();
        final Future<?> future = Executors.newSingleThreadExecutor().submit(sleepRunnable);
        Sleeper.milliseconds(100);
        future.cancel(true);

        assertThat(sleepRunnable.isSleeperNotInterrupted()).isFalse();
        assertThat(future.isCancelled()).isTrue();
    }

    /**
     * Test constructor of {@link Sleeper}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Sleeper.class);
    }

    /**
     * Because the Thread.sleep(..) is not accurate it may happen that the
     * assertion will fail because the elapsed time is outside of expected
     * time range
     * <p>
     * We will accept up to three assertion failures. It may happen that
     * first run failed, but the second run succeeded. If so, then the test
     * is marked as accepted
     * <p>
     * It should be very rarely happen that all three run failed. In this
     * case it should be re-checked if the issue was caused by a code change
     *
     * @param runnable body to test
     */
    private void assertUpToThreeTimes(final Runnable runnable) {
        AssertionError t;
        int retries = 0;
        do {
            t = null;
            try {
                runnable.run();
            } catch (final AssertionError error) {
                t = error;
                retries++;
            }
        } while (t != null && retries < 3);
        // we still have assertion error and already tried three times
        // in this case we will re-throw the assertion error to mark
        // the test as failed!
        if (t != null) {
            throw t;
        }
    }

    /**
     * Test Thread for testing the Sleep class
     *
     * @author PITSCHR
     */
    private static class SleepTestRunnable implements Runnable {
        private boolean sleeperNotInterrupted;

        @Override
        public void run() {
            while (true) {
                // we don't want to wait 10 seconds!!!
                this.sleeperNotInterrupted = Sleeper.seconds(10);
                if (!this.sleeperNotInterrupted) {
                    break;
                }
            }
        }

        public boolean isSleeperNotInterrupted() {
            return this.sleeperNotInterrupted;
        }
    }
}
