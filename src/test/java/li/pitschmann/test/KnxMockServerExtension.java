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

package li.pitschmann.test;

import li.pitschmann.utils.Sleeper;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Extension to start/stop the {@link KnxMockServer}. It will be invoked using {@link KnxTest} annotation.
 *
 * @author PITSCHR
 */
public class KnxMockServerExtension
        implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Logger LOG = LoggerFactory.getLogger(KnxMockServerExtension.class);
    private static final Map<ExtensionContext, ExecutorContainerEntry> executorContainer = new ConcurrentHashMap<>();

    /**
     * Initializes the {@link KnxMockServer} and start
     *
     * @param context
     */
    @Override
    public void beforeTestExecution(final ExtensionContext context) throws Exception {
        LOG.debug("Method 'beforeTestExecution' invoked for test method '{}'.", context.getRequiredTestMethod());

        // create and start KNX Mock Server
        if (!executorContainer.containsKey(context)) {
            executorContainer.put(context, new ExecutorContainerEntry(context));
            LOG.debug("KNX Mock Server started.");
        } else {
            throw new IllegalStateException("KNX Mock Server already running.");
        }

        LOG.debug("Method 'beforeTestExecution' completed for test method '{}'.", context.getRequiredTestMethod());
    }

    /**
     * Shuts down the {@link KnxMockServer} after test
     *
     * @param context
     */
    @Override
    public void afterTestExecution(final ExtensionContext context) throws Exception {
        LOG.debug("Method 'afterTestExecution' invoked for test method '{}'.", context.getRequiredTestMethod());
        if (executorContainer.containsKey(context)) {
            final var es = executorContainer.get(context).getExecutorService();
            try {
                es.awaitTermination(10, TimeUnit.SECONDS);
            } finally {
                es.shutdownNow();
                executorContainer.remove(context);
            }
        } else {
            LOG.warn("Executor Container could not be found.");
        }
        LOG.debug("Method 'afterTestExecution' completed for test method '{}'.", context.getRequiredTestMethod());
    }

    @Override
    public Object resolveParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return executorContainer.get(context).getMockServer();
    }

    @Override
    public boolean supportsParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return paramContext.getParameter().getType().equals(KnxMockServer.class);
    }

    /**
     * Entry for executor container
     *
     * @author PITSCHR
     */
    private class ExecutorContainerEntry {
        private static final int MAX_START_DELAY_IN_MILLISECONDS = 5000; // 5 seconds
        private final KnxMockServer mockServer;
        private final ExecutorService executorService;

        /**
         * Initializes {@link KnxMockServer} and starts as single thread executor immediately
         *
         * @param context
         */
        public ExecutorContainerEntry(final ExtensionContext context) {
            final var annotation = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), KnxTest.class).get();
            this.mockServer = new KnxMockServer(annotation.value());

            this.executorService = Executors.newSingleThreadExecutor();
            try {
                this.executorService.submit(this.mockServer);
                this.executorService.shutdown();
            } catch (Throwable t) {
                t.printStackTrace();
            }

            // wait until server is ready for receiving packets from client
            final var startTime = System.currentTimeMillis();
            do {
                Sleeper.milliseconds(500);
                if ((System.currentTimeMillis() - startTime) > MAX_START_DELAY_IN_MILLISECONDS) {
                    throw new RuntimeException("Could not start KNX Mock server within " + MAX_START_DELAY_IN_MILLISECONDS + "ms (" + (System.currentTimeMillis() - startTime) + ").");
                }
            } while (!this.mockServer.isReady());
        }

        public KnxMockServer getMockServer() {
            return this.mockServer;
        }

        public ExecutorService getExecutorService() {
            return this.executorService;
        }
    }
}
