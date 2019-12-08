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

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.ExtensionPlugin;
import li.pitschmann.knx.core.plugin.IntegerConfigValue;
import li.pitschmann.knx.core.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.HttpConstants;
import ro.pippo.core.Pippo;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.Objects;

/**
 * Plugin for RESTful API (web server)
 */
public class ApiPlugin implements ExtensionPlugin {
    /**
     * Default port for Pippo Micro Web Server (re-using the default port value)
     */
    public static final IntegerConfigValue PORT = new IntegerConfigValue(
            "port",
            () -> 8338,
            Objects::nonNull
    );

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private KnxClient client;
    private Pippo pippo;
    private int port = -1;

    @Override
    public void onInitialization(final KnxClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public void onStart() {
        final var xmlProject = client.getConfig().getProject();
        final var app = new ApiApplication();
        app.setXmlProject(xmlProject);
        app.setKnxClient(client);
        app.getContentTypeEngine(HttpConstants.ContentType.APPLICATION_JSON);

        pippo = new Pippo(app);
        startPippo(pippo);
        // set port and state
        port = pippo.getServer().getPort();
        log.debug("API Plugin and Web Server started at port {}: {}", port, client);
    }

    @Override
    public void onShutdown() {
        port = -1;
        if (pippo != null) {
            pippo.stop();
            pippo = null;
        }
        log.debug("API Plugin and Web Server stopped.");
    }

    /**
     * Starts the Pippo server. This method can be overridden.
     *
     * @param pippo
     */
    protected void startPippo(final Pippo pippo) {
        pippo.start(client.getConfig(PORT));
    }

    /**
     * Returns the port for web server that serves the endpoints
     *
     * @return actual port
     */
    public final int getPort() {
        Preconditions.checkState(isReady(), "API Web Server is not ready yet!");
        return port;
    }

    /**
     * Returns if the web server is ready
     *
     * @return {@code true} if server is ready, otherwise {@code false}
     */
    public final boolean isReady() {
        return this.port != -1;
    }

    /**
     * Creates a new {@link HttpRequest.Builder} for requests to API
     * <p/>
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
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid path provided: " + path);
        }
    }
}
