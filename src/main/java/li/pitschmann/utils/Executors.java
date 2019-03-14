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

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class extends the Java API Executors class with Mapped Diagnostic Context (MDC)
 * information which is useful when executing some threads in parallel. Using MDC we are
 * able to get a clean logging because MDC data will be inherited by child threads.
 */
public final class Executors {

    /**
     * Creates an Executor that uses a single worker thread operating
     * off an unbounded queue.
     * <p>
     * If {@code true} it will wrap the executor service with MDC data. If {@code false} it will call
     * {@link java.util.concurrent.Executors#newSingleThreadExecutor()}
     *
     * @param withMdc {@code true} if MDC should be considered
     * @return An instance of {@link ExecutorService}
     */
    public static ExecutorService newSingleThreadExecutor(final boolean withMdc) {
        return withMdc ? new MdcThreadPoolExecutor(1) : java.util.concurrent.Executors.newSingleThreadExecutor();
    }

    /**
     * Creates a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue.
     * <p>
     * If {@code true} it will wrap the executor service with MDC data. If {@code false} it will call
     * {@link java.util.concurrent.Executors#newSingleThreadExecutor()}
     *
     * @param withMdc {@code true} if MDC should be considered
     * @return An instance of {@link ExecutorService}
     */
    public static ExecutorService newFixedThreadPool(final int nThreads, final boolean withMdc) {
        return withMdc ? new MdcThreadPoolExecutor(nThreads) : java.util.concurrent.Executors.newFixedThreadPool(nThreads);
    }

    /**
     * Wraps the {@link Flow.Subscriber} with MDC
     *
     * @param subscriber
     * @param <T>
     * @return wrapped {@link Flow.Subscriber}
     */
    public static <T> Flow.Subscriber<T> wrapSubscriberWithMDC(final Flow.Subscriber<T> subscriber) {
        return new MdcSubscriber<>(subscriber);
    }

    /**
     * Internal {@link ThreadPoolExecutor} it will wrap several concurrent implementations
     * with MDC supported wrappers.
     */
    private static final class MdcThreadPoolExecutor extends ThreadPoolExecutor {
        public MdcThreadPoolExecutor(final int nThreads) {
            super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        }

        @Override
        public void execute(final Runnable runnable) {
            super.execute(new MdcRunnable(runnable));
        }

        @Override
        public Future<?> submit(final Runnable task) {
            return super.submit(new MdcRunnable(task));
        }

        @Override
        public <T> Future<T> submit(final Callable<T> task) {
            return super.submit(new MdcCallable<>(task));
        }

        @Override
        public <T> Future<T> submit(final Runnable task, final T result) {
            return super.submit(new MdcRunnable(task), result);
        }
    }

    /**
     * Wrapper for {@link Runnable} to use Mapped Diagnostic Context (MDC) for simultaneously executions.
     *
     * @author PITSCHR
     */
    private static final class MdcRunnable implements Runnable {
        private final Map<String, String> context = MDC.getCopyOfContextMap();
        private final Runnable runnable;

        public MdcRunnable(final Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public final void run() {
            final var originalContext = MDC.getCopyOfContextMap();
            if (this.context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(this.context);
            }
            try {
                runnable.run();
            } finally {
                if (originalContext == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(originalContext);
                }
            }
        }
    }

    /**
     * Wrapper for {@link Callable} to use Mapped Diagnostic Context (MDC) for simultaneously executions.
     *
     * @param <V>
     * @author PITSCHR
     */
    private static final class MdcCallable<V> implements Callable<V> {
        private final Map<String, String> context = MDC.getCopyOfContextMap();
        private final Callable<V> callable;

        public MdcCallable(final Callable<V> callable) {
            this.callable = callable;
        }

        @Override
        public final V call() throws Exception {
            final var originalContext = MDC.getCopyOfContextMap();
            if (this.context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(this.context);
            }
            try {
                return callable.call();
            } finally {
                if (originalContext == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(originalContext);
                }
            }
        }
    }

    /**
     * Wrapper for {@link Flow.Subscriber} to use Mapped Diagnostic Context (MDC) for simultaneously executions.
     *
     * @param <T>
     * @author PITSCHR
     */
    private static final class MdcSubscriber<T> implements Flow.Subscriber<T> {
        private final Map<String, String> context = MDC.getCopyOfContextMap();
        private final Flow.Subscriber<T> subscriber;

        public MdcSubscriber(final Flow.Subscriber<T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscriber.onSubscribe(subscription);
        }

        @Override
        public void onNext(T item) {
            final var originalContext = MDC.getCopyOfContextMap();
            if (this.context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(this.context);
            }
            try {
                subscriber.onNext(item);
            } finally {
                if (originalContext == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(originalContext);
                }
            }
        }

        @Override
        public void onError(Throwable throwable) {
            subscriber.onError(throwable);
        }

        @Override
        public void onComplete() {
            subscriber.onComplete();
        }
    }
}
