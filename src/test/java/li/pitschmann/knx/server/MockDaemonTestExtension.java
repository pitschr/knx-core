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

package li.pitschmann.knx.server;

import com.google.common.base.Stopwatch;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Sleeper;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extension to start/stop the {@link MockHttpDaemon} (and {@link MockServer} indirectly).
 * <p/>
 * It will be invoked using {@link MockDaemonTest} annotation.
 *
 * @author PITSCHR
 */
public final class MockDaemonTestExtension
        implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Logger LOG = LoggerFactory.getLogger(MockDaemonTestExtension.class);
    private static final Map<ExtensionContext, MockHttpDaemon> mockDaemons = new ConcurrentHashMap<>();
    private static final AtomicInteger junitTestNr = new AtomicInteger();

    /**
     * Initializes the {@link MockHttpDaemon} and start
     *
     * @param context
     */
    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        MDC.put("junitClass", context.getRequiredTestClass().getSimpleName());
        MDC.put("junitMethod", context.getRequiredTestMethod().getName() + "-" + junitTestNr.incrementAndGet());

        LOG.debug("Method 'beforeTestExecution' invoked for test method '{}'.", context.getRequiredTestMethod());

        // create and start
        if (!mockDaemons.containsKey(context)) {
            final var stopwatch = Stopwatch.createStarted();
            final var mockDaemon = MockHttpDaemon.createStarted(context);

            // wait until mock daemon is ready for receiving packets from client (wait up to 10 seconds)
            if (!Sleeper.milliseconds(100, () -> mockDaemon.isReady(), 10000)) {
                // it took longer than 10 seconds -> abort
                throw new RuntimeException("Could not start KNX Mock Daemon (elapsed: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms).");
            }

            mockDaemons.put(context, mockDaemon);
            LOG.debug("KNX Mock Daemon started.");
        } else {
            throw new IllegalStateException("KNX Mock Daemon already running.");
        }

        LOG.debug("Method 'beforeTestExecution' completed for test method '{}'.", context.getRequiredTestMethod());
    }

    /**
     * Shuts down the executor service running {@link MockHttpDaemon} after test
     *
     * @param context
     */
    @Override
    public void afterTestExecution(final ExtensionContext context) throws Exception {
        LOG.debug("Method 'afterTestExecution' invoked for test method '{}'.", context.getRequiredTestMethod());
        try {
            final var gracefully = Closeables.closeQuietly(mockDaemons.remove(context));
            LOG.debug("Shutdown of executor service was gracefully?: {}", gracefully);
        } finally {
            LOG.debug("Method 'afterTestExecution' completed for test method '{}'.", context.getRequiredTestMethod());
            MDC.clear();
        }
    }

    @Override
    public MockHttpDaemon resolveParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return mockDaemons.get(context);
    }

    @Override
    public boolean supportsParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return paramContext.getParameter().getType().equals(MockHttpDaemon.class);
    }
}
