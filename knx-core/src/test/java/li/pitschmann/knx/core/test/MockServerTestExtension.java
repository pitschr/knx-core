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

package li.pitschmann.knx.core.test;

import li.pitschmann.knx.core.utils.Closeables;
import li.pitschmann.knx.core.utils.Sleeper;
import li.pitschmann.knx.core.utils.Stopwatch;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extension to start/stop the {@link MockServer}. It will be invoked using {@link MockServerTest} annotation.
 *
 * @author PITSCHR
 */
public final class MockServerTestExtension
        implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Logger log = LoggerFactory.getLogger(MockServerTestExtension.class);
    private static final Map<ExtensionContext, MockServer> mockServers = new ConcurrentHashMap<>();
    private static final AtomicInteger junitTestNr = new AtomicInteger();

    /**
     * Initializes the {@link MockServer} and start
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        MDC.put("junitClass", context.getRequiredTestClass().getSimpleName());
        MDC.put("junitMethod", context.getRequiredTestMethod().getName() + "-" + junitTestNr.incrementAndGet());

        log.debug("Method 'beforeTestExecution' invoked for test method '{}'.", context.getRequiredTestMethod());

        // create and start
        if (!mockServers.containsKey(context)) {
            final var annotation = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), MockServerTest.class).get();
            final var stopwatch = Stopwatch.createStarted();
            final var mockServer = MockServer.createStarted(annotation);

            // wait until mock server is ready for receiving packets from client
            // if will take longer than 30 seconds -> abort
            if (!Sleeper.milliseconds(100, () -> mockServer.isReady(), 30000)) {
                throw new RuntimeException("Could not start KNX Mock Server (elapsed: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms).");
            }

            mockServers.put(context, mockServer);
            log.debug("KNX Mock Server started (elapsed: {}ms)", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } else {
            throw new IllegalStateException("KNX Mock Server already running.");
        }

        log.debug("Method 'beforeTestExecution' completed for test method '{}'.", context.getRequiredTestMethod());
    }

    /**
     * Shuts down the executor service running {@link MockServer} after test
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void afterTestExecution(final ExtensionContext context) {
        log.debug("Method 'afterTestExecution' invoked for test method '{}'.", context.getRequiredTestMethod());
        try {
            final var gracefully = Closeables.closeQuietly(mockServers.remove(context));
            log.debug("Shutdown of executor service was gracefully?: {}", gracefully);
        } finally {
            log.debug("Method 'afterTestExecution' completed for test method '{}'.", context.getRequiredTestMethod());
            MDC.clear();
        }
    }

    @Override
    public MockServer resolveParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return mockServers.get(context);
    }

    @Override
    public boolean supportsParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return paramContext.getParameter().getType().equals(MockServer.class);
    }
}
