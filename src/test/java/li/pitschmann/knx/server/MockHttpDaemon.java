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
import li.pitschmann.knx.daemon.AbstractHttpDaemon;
import li.pitschmann.knx.link.Configuration;
import li.pitschmann.utils.Sleeper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

/**
 * Creates a new instance of KNX Mock Daemon
 * <p/>
 * This will also start the KNX Mock Server ({@link MockServer}) in background which will be used
 * for communication with the KNX Net/IP device
 */
public final class MockHttpDaemon extends AbstractHttpDaemon {

    private MockHttpDaemon(final @Nonnull Configuration configuration) {
        super(configuration);
    }

    /**
     * Creates the KNX Mock Daemon and start it immediately
     * <p/>
     * The KNX Mock Server will be also started as a background service to serve
     * the requests coming from KNX Mock Daemon
     *
     * @param context
     * @return started KNX Mock Daemon
     */
    public static MockHttpDaemon createStarted(final @Nonnull ExtensionContext context) {
        final var annotation = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), MockDaemonTest.class).get();

        // start mock server
        final var stopwatch = Stopwatch.createStarted();
        final var mockServer = MockServer.createStarted(annotation.mockServer());
        // wait until mock server is ready for receiving packets from client (wait up to 5 seconds)
        if (!Sleeper.milliseconds(100, () -> mockServer.isReady(), 5000)) {
            // it took longer than 5 seconds -> abort
            throw new RuntimeException("Could not start KNX Mock Server (elapsed: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms).");
        }

        // define mock daemon
        final var mockDaemon = new MockHttpDaemon(mockServer.newConfigBuilder().build());

        // get next free port
        final int port;
        try {
            port = new ServerSocket(0).getLocalPort();
        } catch (IOException e) {
            // should never happen
            throw new AssertionError("Could not find a free port!");
        }

        // start mock daemon with a non-standard port
        mockDaemon.start(port);
        return mockDaemon;
    }
}
