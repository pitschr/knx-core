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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;

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
        try {
            testNoInterruptInternal();
        } catch (final AssertionError error) {
            if (error.getMessage().contains("(a difference of exactly 10% being considered valid)")) {
                // give a 2nd try as it may happen that the Thread#sleep(..) is outside of 10% due JUnit parallelism
                Sleeper.seconds(1);
                testNoInterruptInternal();
            } else {
                // otherwise re-throw the error
                throw error;
            }
        }
    }

    private void testNoInterruptInternal() {
        boolean notInterrupted;

        // repeat 5 times to compare with an average value
        int repeats = 1;
        int durationTotal = 0;
        Stopwatch sw = Stopwatch.createUnstarted();
        for (int i = 0; i < repeats; i++) {
            try {
                sw.start();
                notInterrupted = Sleeper.milliseconds(100); // wait 100ms
                durationTotal += sw.elapsed(TimeUnit.MILLISECONDS);
                sw.reset();
                assertThat(notInterrupted).isTrue();
            } catch (Throwable t) {
                fail("A throwable was thrown for: " + t);
                throw new AssertionError();
            }
        }

        // Thread#sleep(..) is not very accurate, so it may be between 90 and 110ms
        assertThat(durationTotal / repeats).isCloseTo(100, Percentage.withPercentage(10));
    }

    /**
     * Test {@link Sleeper#milliseconds(long, Supplier, long)} where as the predicates is always TRUE
     */
    @Test
    @DisplayName("Sleep in milliseconds with predicates (always true)")
    public void testSleepWithPredicateAlwaysTrue() {
        // predicate meet immediately
        try {
            testSleepWithPredicateAlwaysTrueInternal();
        } catch (final AssertionError error) {
            if (error.getMessage().contains("(a difference of exactly 10% being considered valid)")) {
                // give a 2nd try as it may happen that the Thread#sleep(..) is outside of range due JUnit parallelism
                Sleeper.seconds(1);
                testSleepWithPredicateAlwaysTrueInternal();
            } else {
                // otherwise re-throw the error
                throw error;
            }
        }
    }

    private void testSleepWithPredicateAlwaysTrueInternal() {
        var sw = Stopwatch.createStarted();
        var notInterrupted = Sleeper.milliseconds(10, () -> true, 100);
        assertThat(sw.elapsed().toMillis()).isBetween(0L, 10L);
        assertThat(notInterrupted).isTrue();
    }

    /**
     * Test {@link Sleeper#milliseconds(long, Supplier, long)} where as the predicates is always FALSE
     * and therefore running into a timeout expiration.
     */
    @Test
    @DisplayName("Sleep in milliseconds with predicates (always false)")
    public void testSleepWithPredicateAlwaysFalse() {
        // predicate meet immediately
        try {
            testSleepWithPredicateAlwaysFalseInternal();
        } catch (final AssertionError error) {
            if (error.getMessage().contains("[100L, 110L]")) {
                // give a 2nd try as it may happen that the Thread#sleep(..) is outside of range due JUnit parallelism
                Sleeper.seconds(1);
                testSleepWithPredicateAlwaysFalseInternal();
            } else {
                // otherwise re-throw the error
                throw error;
            }
        }
    }

    private void testSleepWithPredicateAlwaysFalseInternal() {
        var sw = Stopwatch.createStarted();
        var notInterrupted = Sleeper.milliseconds(10, () -> false, 100);
        assertThat(sw.elapsed().toMillis()).isBetween(100L, 110L);
        assertThat(notInterrupted).isFalse();
    }

    /**
     * Test {@link Sleeper#milliseconds(long, Supplier, long)} where as the predicates is TRUE
     * after 50 milliseconds
     */
    @Test
    @DisplayName("Sleep in milliseconds with predicates (true after 50 ms)")
    public void testSleepWithPredicate() {
        // predicate meet immediately
        try {
            testSleepWithPredicateInternal();
        } catch (final AssertionError error) {
            if (error.getMessage().contains("[50L, 60L]")) {
                // give a 2nd try as it may happen that the Thread#sleep(..) is outside of range due JUnit parallelism
                Sleeper.seconds(1);
                testSleepWithPredicateInternal();
            } else {
                // otherwise re-throw the error
                throw error;
            }
        }
    }

    private void testSleepWithPredicateInternal() {
        long interruptSleep = System.currentTimeMillis() + 50;
        var sw = Stopwatch.createStarted();
        var notInterrupted = Sleeper.milliseconds(10, () -> System.currentTimeMillis() > interruptSleep, 100);
        assertThat(sw.elapsed().toMillis()).isBetween(50L, 60L);
        assertThat(notInterrupted).isTrue();
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
        Sleeper.seconds(1);
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
