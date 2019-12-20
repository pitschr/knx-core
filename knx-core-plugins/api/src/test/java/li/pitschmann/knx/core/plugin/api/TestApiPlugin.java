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

package li.pitschmann.knx.core.plugin.api;

import li.pitschmann.knx.core.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.HttpConstants;
import ro.pippo.core.Pippo;
import ro.pippo.core.PippoRuntimeException;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

/**
 * Creates a new instance of API plugin that is used for testing purposes.
 * It will start up the web server based on a randomized port address
 */
final class TestApiPlugin extends ApiPlugin {
    private static final Logger log = LoggerFactory.getLogger(TestApiPlugin.class);

    @Override
    protected void startPippo(Pippo pippo) {
        for (int i = 0; i < 5; i++) {
            int nextFreePort = getNextFreePort();
            try {
                pippo.start(nextFreePort);
                log.debug("Pippo server started successfully on port: {}", pippo.getServer().getPort());
                return;
            } catch (final PippoRuntimeException pre) {
                if (pre.getCause() instanceof RuntimeException) {
                    if (pre.getCause().getCause() instanceof BindException) {
                        log.warn("Could not start pippo because the port '{}' seems not be free yet (race-condition). " +
                                "Try with next attempt.", nextFreePort, pre);
                        pippo.stop();
                        continue;
                    }
                }
                throw pre;
            }
        }
    }

    /**
     * Returns the next free port if applicable
     *
     * @return next free port
     */
    private int getNextFreePort() {
        // get next free port
        try (final var serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            // should never happen
            throw new AssertionError("Could not find a free port!");
        }
    }

    /**
     * Creates a new {@link HttpRequest.Builder} for test requests to API
     * <p>
     * As we are using communicating via JSON only, the headers
     * {@link HttpConstants.Header#ACCEPT} and {@link HttpConstants.Header#CONTENT_TYPE}
     * are pre-defined with {@link HttpConstants.ContentType#APPLICATION_JSON}.
     *
     * @param path the path to be requested to API
     * @return Builder for HttpRequest
     */
    public final HttpRequest.Builder newRequestBuilder(final String path) {
        Preconditions.checkArgument(path.startsWith("/"), "Path must start with /");
        try {
            return HttpRequest.newBuilder(new URI("http://localhost:" + getPort() + path))
                    .header(HttpConstants.Header.ACCEPT, HttpConstants.ContentType.APPLICATION_JSON)
                    .header(HttpConstants.Header.CONTENT_TYPE, HttpConstants.ContentType.APPLICATION_JSON);
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException("Invalid path provided: " + path, e);
        }
    }
}
