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

import li.pitschmann.knx.core.communication.task.DescriptionResponseTask;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.slf4j.helpers.BasicMDCAdapter;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test cases for {@link Executors} class
 *
 * @author PITSCHR
 */
public class ExecutorsTest {
    private static MDCAdapter tmpAdapter;

    /**
     * This project is shipped with SLF4J only and without any bindings
     * (see: http://www.slf4j.org/codes.html#no_static_mdc_binder )
     * <p>
     * It defaults back to {@link org.slf4j.helpers.NOPMDCAdapter} which
     * doesn't store any data for MDC. We want to use the {@link BasicMDCAdapter}
     * per default in case {@link org.slf4j.helpers.NOPMDCAdapter} is used
     * because the tests are otherwise failing because of no-operation.
     * <p>
     * In concrete project a proper binding should be used (e.g. logback, ...)
     */
    @BeforeAll
    public static void setBasicMDCAdapter() throws ReflectiveOperationException {
        if (MDC.getMDCAdapter() instanceof NOPMDCAdapter) {
            final var mdcAdapterField = MDC.class.getDeclaredField("mdcAdapter");
            mdcAdapterField.setAccessible(true);
            tmpAdapter = (MDCAdapter) mdcAdapterField.get(null);
            mdcAdapterField.set(null, new BasicMDCAdapter());
        }
    }

    /**
     * Reset to previous MDCAdapter (it may be {@link org.slf4j.helpers.NOPMDCAdapter} )
     * which is OK!
     */
    @AfterAll
    public static void resetMDCAdapter() throws ReflectiveOperationException {
        if (tmpAdapter != null) {
            final var mdcAdapterField = MDC.class.getDeclaredField("mdcAdapter");
            mdcAdapterField.setAccessible(true);
            mdcAdapterField.set(null, tmpAdapter);
        }
    }

    /**
     * Test {@link Executors#newSingleThreadExecutor(boolean)}
     */
    @Test
    @DisplayName("Single Thread Executor with/without MDC")
    public void testSingleThreadExecutor() {
        assertThat(Executors.newSingleThreadExecutor(true).getClass().getSimpleName()).isEqualTo("MdcThreadPoolExecutor");
        assertThat(Executors.newSingleThreadExecutor(false).getClass().getSimpleName()).isEqualTo("FinalizableDelegatedExecutorService");
    }

    /**
     * Test {@link Executors#newFixedThreadPool(int, boolean)}
     */
    @Test
    @DisplayName("Fixed Thread Pool with/without MDC")
    public void testFixedThreadPool() {
        assertThat(Executors.newFixedThreadPool(1, true).getClass().getSimpleName()).isEqualTo("MdcThreadPoolExecutor");
        assertThat(Executors.newFixedThreadPool(1, false).getClass().getSimpleName()).isEqualTo("ThreadPoolExecutor");
    }

    /**
     * Test {@link Executors#wrapSubscriberWithMDC(Flow.Subscriber)}
     */
    @Test
    @DisplayName("Wrap Subscriber with MDC")
    public void testWrapSubscriberWithMDC() {
        final var subscriber = mock(DescriptionResponseTask.class);

        assertThat(Executors.wrapSubscriberWithMDC(subscriber).getClass().getSimpleName()).isEqualTo("MdcSubscriber");
    }

    /**
     * Test {@link Runnable}, {@link Callable}, {@link Flow.Subscriber} without MDC
     */
    @Test
    @DisplayName("Test Runnable, Callable with empty MDC")
    public void testEmptyMDC() throws ExecutionException, InterruptedException {
        final var executorService = Executors.newSingleThreadExecutor(true);

        executorService.execute(new TestRunnable());
        executorService.submit(new TestRunnable()).get();
        executorService.submit(new TestRunnable(), Integer.valueOf(3)).get();
        executorService.submit(new TestCallable()).get();
        executorService.shutdownNow();

        final var subscriber = Executors.wrapSubscriberWithMDC(new TestSubscriber());
        subscriber.onSubscribe(null);
        subscriber.onNext(null);
        subscriber.onComplete();
        subscriber.onError(null);
    }

    /**
     * Test {@link Runnable}, {@link Callable}, {@link Flow.Subscriber} with MDC
     */
    @Test
    @DisplayName("Test Runnable, Callable with non-empty MDC")
    public void testNonEmptyMDC() throws ExecutionException, InterruptedException {
        MDC.put("key", "foobar");

        final var executorService = Executors.newSingleThreadExecutor(true);
        executorService.execute(new TestRunnable());
        executorService.submit(new TestRunnable()).get();
        executorService.submit(new TestRunnable(), Integer.valueOf(3)).get();
        executorService.submit(new TestCallable()).get();
        executorService.shutdownNow();

        final var subscriber = Executors.wrapSubscriberWithMDC(new TestSubscriber());
        subscriber.onSubscribe(null);
        subscriber.onNext(null);
        subscriber.onComplete();
        subscriber.onError(null);

        assertThat(MDC.get("key")).isEqualTo("foobar"); // should not be changed!
    }

    /**
     * Test constructor of {@link Executors}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Executors.class);
    }

    private final class TestRunnable implements Runnable {
        @Override
        public void run() {
            // NO-OP
        }
    }

    private final class TestCallable implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            MDC.put("key", "I am a new value!");
            return 1 + 2;
        }
    }

    private final class TestSubscriber implements Flow.Subscriber<Object> {

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            // NO-OP
        }

        @Override
        public void onNext(Object item) {
            MDC.put("key", "I am a new value!");
            // NO-OP
        }

        @Override
        public void onError(Throwable throwable) {
            // NO-OP
        }

        @Override
        public void onComplete() {
            // NO-OP
        }
    }
}
