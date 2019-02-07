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

import com.google.common.base.*;
import li.pitschmann.test.*;
import org.assertj.core.data.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

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
        // verify the result (expected 'true')
        assertThat(Sleeper.milliseconds(100)).isTrue();

        // measure
        // Thread#sleep(..) is not very accurate, so it may be between 90 and 110ms
        var elapsedTime = elapsedAverage((startTime) -> Sleeper.milliseconds(100), 3);
        assertThat(elapsedTime).isCloseTo(100d, Percentage.withPercentage(10));
    }

    /**
     * Test {@link Sleeper#milliseconds(long, Supplier, long)} where as the predicates is always TRUE
     */
    @Test
    @DisplayName("Sleep in milliseconds with predicates (always true)")
    public void testSleepWithPredicateAlwaysTrue() {
        // verify the result (expected 'true')
        assertThat(Sleeper.milliseconds(10, () -> true, 45)).isTrue();

        // predicate meet immediately
        var elapsedTime = elapsedAverage((startTime) -> Sleeper.milliseconds(10, () -> true, 45), 3);
        assertThat(elapsedTime).isBetween(0d, 10d);
    }

    /**
     * Test {@link Sleeper#milliseconds(long, Supplier, long)} where as the predicates is always FALSE
     * and therefore running into a timeout expiration.
     */
    @Test
    @DisplayName("Sleep in milliseconds with predicates (always false)")
    public void testSleepWithPredicateAlwaysFalse() {
        // verify the result (expected 'false' because of timeout)
        assertThat(Sleeper.milliseconds(10, () -> false, 45)).isFalse();

        // predicate meet immediately
        var elapsedTime = elapsedAverage((startTime) -> Sleeper.milliseconds(10, () -> false, 45), 3);
        assertThat(elapsedTime).isCloseTo(45, Percentage.withPercentage(20));
    }

    /**
     * Test {@link Sleeper#milliseconds(long, Supplier, long)} where as the predicates is TRUE
     * after 50 milliseconds
     */
    @Test
    @DisplayName("Sleep in milliseconds with predicates (true after 50 ms)")
    public void testSleepWithPredicate() {
        // verify the result (true = not interrupted)
        var startTime = System.currentTimeMillis();
        assertThat(Sleeper.milliseconds(10, () -> System.currentTimeMillis() > startTime + 50, 100)).isTrue();

        // predicate meet immediately
        var elapsedTime = elapsedAverage((st) -> Sleeper.milliseconds(10, () -> System.currentTimeMillis() > st + 45, 100), 3);
        assertThat(elapsedTime).isCloseTo(50, Percentage.withPercentage(20));
    }

    /**
     * Test {@link Sleeper#milliseconds(long, Supplier, long)} where as timeout is shorter than interval
     */
    @Test
    @DisplayName("Sleep in milliseconds where timeout is shorter than interval check")
    public void testSleepWithPredicateWrongArgument() {
        assertThatThrownBy(() -> Sleeper.milliseconds(10, () -> false, 5)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test {@link Sleeper#milliseconds(long, Supplier, long)} where as timeout is shorter than interval
     */
    @Test
    @DisplayName("Sleep in milliseconds and thread interrupt")
    public void testSleepWithPredicateTheradInterrupt() {
        var thread = new Thread(new Runnable() {
            @Override
            public void run() {
                assertThat(Sleeper.milliseconds(10, () -> false, 100)).isFalse();
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
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Sleeper.class);
    }

    /**
     * Returns the average of elapsed time in milliseconds
     *
     * @param supplier what should be executed/measured
     * @param times    how many iterations
     * @return the elapsed average time in milliseconds
     */
    private double elapsedAverage(final LongConsumer supplier, final int times) {
        var sw = Stopwatch.createStarted();
        for (int i = 0; i < times; i++) {
            supplier.accept(System.currentTimeMillis());
        }
        return sw.elapsed().toMillis() / (double) times;
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
