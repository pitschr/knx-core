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

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Executors;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extension to start/stop the {@link MockServer}. It will be invoked using {@link MockServerTest} annotation.
 *
 * @author PITSCHR
 */
public class MockServerTestExtension
        implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Logger LOG = LoggerFactory.getLogger(MockServerTestExtension.class);
    private static final Map<ExtensionContext, MockServerTestContainer> testContainers = new ConcurrentHashMap<>();
    private static final AtomicInteger junitTestNr = new AtomicInteger();

    /**
     * Initializes the {@link MockServer} and start
     *
     * @param context
     */
    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        MDC.put("junitClass", context.getRequiredTestClass().getSimpleName());
        MDC.put("junitMethod", context.getRequiredTestMethod().getName() + "-" + junitTestNr.incrementAndGet());

        LOG.debug("Method 'beforeTestExecution' invoked for test method '{}'.", context.getRequiredTestMethod());

        // create and start KNX Mock Server
        if (!testContainers.containsKey(context)) {
            testContainers.put(context, new MockServerTestContainer(context));
            LOG.debug("KNX Mock Server started.");
        } else {
            throw new IllegalStateException("KNX Mock Server already running.");
        }

        LOG.debug("Method 'beforeTestExecution' completed for test method '{}'.", context.getRequiredTestMethod());
    }

    /**
     * Shuts down the executor service running {@link MockServer} after test
     *
     * @param context
     */
    @Override
    public void afterTestExecution(final ExtensionContext context) throws Exception {
        LOG.debug("Method 'afterTestExecution' invoked for test method '{}'.", context.getRequiredTestMethod());
        try {
            final var testContainer = testContainers.remove(context);
            if (testContainer != null) {
                final var executorService = testContainer.getExecutorService();
                final var gracefully = Closeables.shutdownQuietly(executorService, 10, TimeUnit.SECONDS);
                LOG.debug("Shutdown of executor service was gracefully?: {}", gracefully);
            } else {
                LOG.warn("Test container could not be found for context: {}", context);
            }
        } finally {
            LOG.debug("Method 'afterTestExecution' completed for test method '{}'.", context.getRequiredTestMethod());
            MDC.clear();
        }
    }

    @Override
    public MockServer resolveParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return testContainers.get(context).getMockServer();
    }

    @Override
    public boolean supportsParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return paramContext.getParameter().getType().equals(MockServer.class);
    }

    /**
     * Mock server test container holding mock server
     *
     * @author PITSCHR
     */
    private class MockServerTestContainer {
        private final MockServer mockServer;
        private final ExecutorService executorService;

        /**
         * Initializes {@link MockServer} and starts as single thread executor immediately
         *
         * @param context
         */
        public MockServerTestContainer(final ExtensionContext context) {
            final var annotation = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), MockServerTest.class).get();
            final var stopwatch = Stopwatch.createStarted();
            this.mockServer = new MockServer(annotation);

            this.executorService = Executors.newSingleThreadExecutor(true);
            this.executorService.execute(this.mockServer);
            this.executorService.shutdown();

            // wait until mock server is ready for receiving packets from client
            final var wait = this.mockServer.waitReady();
            if (!wait) {
                // it took too long!
                throw new RuntimeException("Could not start KNX mock server (elapsed: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms).");
            }
        }

        /**
         * Returns the mock server
         *
         * @return an instance of mock server, otherwise
         */
        public MockServer getMockServer() {
            Preconditions.checkState(this.mockServer.isReady(), "KNX mock server is not ready.");
            return this.mockServer;
        }

        /**
         * Returns the executor service that is running the mock server
         *
         * @return executor service
         */
        public ExecutorService getExecutorService() {
            return this.executorService;
        }
    }
}
