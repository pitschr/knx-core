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

import li.pitschmann.knx.link.body.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Flow;

/**
 * Communicator for KNX mock server (package-protected)
 */
public class MockServerProjectLogic implements Flow.Subscriber<Body> {
    private final static Logger logger = LoggerFactory.getLogger(MockServerProjectLogic.class);
    private final MockServer mockServer;
    private final Path projectPath;

    public MockServerProjectLogic(final MockServer mockServer, final Path projectPath) {
        this.mockServer = Objects.requireNonNull(mockServer);
        this.projectPath = Objects.requireNonNull(projectPath);
    }

    protected MockServer getMockServer() {
        return mockServer;
    }

    protected Path getProjectPath() {
        return projectPath;
    }

    @Override
    public void onNext(final Body body) {
        logger.debug("Body received, but no logic defined: {}", body);
    }

    @Override
    public void onError(Throwable throwable) {
        logger.error("Error during KNX Mock Server Communicator class", throwable);
    }

    @Override
    public void onComplete() {
        // NO-OP
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }
}
