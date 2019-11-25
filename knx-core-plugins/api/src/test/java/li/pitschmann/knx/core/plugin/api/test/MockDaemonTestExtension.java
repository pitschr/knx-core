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

package li.pitschmann.knx.core.plugin.api.test;

import li.pitschmann.knx.core.communication.BaseKnxClient;
import li.pitschmann.knx.core.communication.DefaultKnxClient;
import li.pitschmann.knx.core.communication.InternalKnxClient;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.utils.Closeables;
import li.pitschmann.knx.core.utils.Executors;
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

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extension to start/stop the {@link MockHttpDaemonPlugin} (and {@link MockServer} indirectly).
 * <p/>
 * It will be invoked using {@link MockDaemonTest} annotation.
 *
 * @author PITSCHR
 */
public final class MockDaemonTestExtension
        implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Logger log = LoggerFactory.getLogger(MockDaemonTestExtension.class);
    private static final Map<ExtensionContext, MockServer> mockServers = new ConcurrentHashMap<>();
    private static final Map<ExtensionContext, MockHttpDaemonPlugin> mockDaemons = new ConcurrentHashMap<>();
    private static final Map<ExtensionContext, KnxClient> knxClients = new ConcurrentHashMap<>();
    private static final AtomicInteger junitTestNr = new AtomicInteger();

    /**
     * Initializes the {@link MockHttpDaemonPlugin} and start
     *
     * @param context
     */
    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        MDC.put("junitClass", context.getRequiredTestClass().getSimpleName());
        MDC.put("junitMethod", context.getRequiredTestMethod().getName() + "-" + junitTestNr.incrementAndGet());

        log.debug("Method 'beforeTestExecution' invoked for test method '{}'.", context.getRequiredTestMethod());

        // create and start
        if (!mockServers.containsKey(context)) {
            final var stopwatch = Stopwatch.createStarted();
            final var annotation = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), MockDaemonTest.class).get();
            // -------------------------------------
            // 1) start mock server
            // -------------------------------------
            final var mockServer = MockServer.createStarted(annotation.value());

            // wait until mock server is ready for receiving packets from client
            // if will take longer than 30 seconds -> abort
            if (!Sleeper.milliseconds(100, mockServer::isReady, 30000)) {
                throw new RuntimeException("Could not start KNX Mock Server (elapsed: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms).");
            }
            mockServers.put(context, mockServer);
            log.debug("KNX Mock Server started (elapsed: {} ms)", stopwatch.elapsed(TimeUnit.MILLISECONDS));

            // -------------------------------------
            // 2) start KNX client
            //    (with mock http daemon as plugin)
            // -------------------------------------
            stopwatch.reset();
            final ExecutorService executorService = Executors.newSingleThreadExecutor(true);
            executorService.execute(
                    () -> {
                        final var config = mockServer.newConfigBuilder().plugin(MockHttpDaemonPlugin.class).build();
                        try (final var client = DefaultKnxClient.createStarted(config)) {
                            knxClients.put(context, client);
                            mockDaemons.put(context, getMockHttpDaemonPlugin(client));
                            while (client.isRunning() && Sleeper.seconds(1)) {
                                // do nothing ...
                                log.debug("ping ...");
                            }
                        } finally {
                            log.debug("KNX Client stopped.");
                        }
                    }
            );
            executorService.shutdown();

            // wait mock http daemon is ready (it will be started by KNX client as plugin)
            if (!Sleeper.milliseconds(100, () -> isMockDaemonReady(context), 30000)) {
                throw new RuntimeException("Could not start KNX Mock Server (elapsed: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms).");
            }

            log.debug("KNX Client started (elapsed: {}ms)", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } else {
            throw new IllegalStateException("KNX Mock Server already running.");
        }

        log.debug("Method 'beforeTestExecution' completed for test method '{}'.", context.getRequiredTestMethod());
    }

    /**
     * Returns the {@link MockHttpDaemonPlugin} which is inside the KNX client
     * and not accessible outside
     *
     * @param baseKnxClient
     * @return an existing instance of {@link MockHttpDaemonPlugin}
     */
    @Nonnull
    private MockHttpDaemonPlugin getMockHttpDaemonPlugin(final @Nonnull BaseKnxClient baseKnxClient) {
        try {
            final var internalClientField = BaseKnxClient.class.getDeclaredField("internalClient");
            internalClientField.setAccessible(true);
            final var internalClient = (InternalKnxClient)internalClientField.get(baseKnxClient);
            return Objects.requireNonNull(internalClient.getPluginManager().getPlugin(MockHttpDaemonPlugin.class));
        } catch (final ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }

    /**
     * Returns {@code true} when mock API is ready, otherwise {@code false}
     *
     * @param context
     * @return {@code true} if ready, otherwise {@code false}
     */
    private boolean isMockDaemonReady(final ExtensionContext context) {
        final var mockDaemon = mockDaemons.get(context);
        return mockDaemon != null && mockDaemon.isReady();
    }

    /**
     * Shuts down the executor service running {@link MockServer} and {@link KnxClient} after test
     *
     * @param context
     */
    @Override
    public void afterTestExecution(final ExtensionContext context) {
        log.debug("Method 'afterTestExecution' invoked for test method '{}'.", context.getRequiredTestMethod());
        try {
            final var knxClientGracefully = Closeables.closeQuietly(knxClients.remove(context));
            log.debug("Shutdown of KNX client service was gracefully?: {}", knxClientGracefully);
            final var mockServerGracefully = Closeables.closeQuietly(mockServers.remove(context));
            log.debug("Shutdown of KNX Mock Server was gracefully?: {}", mockServerGracefully);
        } finally {
            log.debug("Method 'afterTestExecution' completed for test method '{}'.", context.getRequiredTestMethod());
            MDC.clear();
        }
    }

    @Override
    public MockHttpDaemonPlugin resolveParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return mockDaemons.get(context);
    }

    @Override
    public boolean supportsParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return paramContext.getParameter().getType().equals(MockHttpDaemonPlugin.class);
    }
}
