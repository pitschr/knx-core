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

import li.pitschmann.knx.daemon.AbstractHttpDaemon;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;

/**
 * Creates a new instance of KNX Mock Daemon
 * <p/>
 * This will also start the KNX Mock Server ({@link MockServer}) in background which will be used
 * for communication with the KNX Net/IP device
 */
public class MockDaemon extends AbstractHttpDaemon {
    private final MockServer mockServer;

    private MockDaemon(final @Nonnull MockServer mockServer) {
        super(mockServer.newConfigBuilder().build());
        this.mockServer = mockServer;
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
    public static MockDaemon createStarted(final @Nonnull ExtensionContext context) {
        final var annotation = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), MockDaemonTest.class).get();
        final var mockServer = MockServer.createStarted(annotation.mockServer());
        final var mockDaemon = new MockDaemon(mockServer);
        mockDaemon.start();
        return mockDaemon;
    }
}
