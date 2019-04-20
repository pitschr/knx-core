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

package li.pitschmann.knx.daemon;

import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import li.pitschmann.knx.link.Configuration;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.exceptions.KnxIllegalArgumentException;
import li.pitschmann.utils.Executors;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.HttpConstants;
import ro.pippo.core.Pippo;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.concurrent.ExecutorService;

/**
 * Abstract HTTP Daemon
 * <p/>
 * It will internally start a KNX client to communicate with the KNX Net/IP device.
 */
public abstract class AbstractHttpDaemon implements Runnable, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHttpDaemon.class);
    private final Configuration configuration;
    private ExecutorService executorService;
    private int port = -1;
    private boolean ready;
    private boolean cancel;

    protected AbstractHttpDaemon(final @Nonnull Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Starts the HTTP Daemon
     */
    protected final void start() {
        Preconditions.checkState(this.executorService == null, "It seems the Http Daemon Server is already started?");
        this.executorService = Executors.newSingleThreadExecutor(true);
        this.executorService.execute(this);
        this.executorService.shutdown();
    }

    @Override
    public final void run() {
        final var pippo = new Pippo(new HttpDaemonApplication());

        try (final var client = DefaultKnxClient.createStarted(configuration)) {
            ((HttpDaemonApplication) pippo.getApplication()).setKnxClient(client);
            pippo.getApplication().getContentTypeEngine(HttpConstants.ContentType.APPLICATION_JSON);
            port = getPort();
            pippo.start(port);
            ready = true;
            logger.debug("Http Daemon Server started at port {}: {}", port, client);
            while (!isCancelled() && Sleeper.seconds(1)) {
                // sleep 1 second
                logger.debug("ping...");
            }
        } catch (Throwable t) {
            logger.error("Something went wrong", t);
        } finally {
            logger.debug("Http Daemon Server stopped.");
            pippo.stop();
        }
    }

    /**
     * Cancels the KNX Daemon
     * <p/>
     * When cancelling it it will stop the KNX Daemon including KNX Client as background process
     */
    public final void cancel() {
        cancel = true;
    }

    /**
     * Returns if the KNX Daemon has been cancelled or interrupted.
     *
     * @return {@code true} if cancelled/interrupted, otherwise {@code false}
     */
    public final boolean isCancelled() {
        return cancel || Thread.currentThread().isInterrupted();
    }

    /**
     * Returns if the KNX Daemon is ready
     *
     * @return {@code true} if daemon and server is ready, otherwise {@code false}
     */
    public final boolean isReady() {
        return ready;
    }

    /**
     * Returns the port for KNX Daemon
     *
     * @return actual port
     */
    public final int getPort() {
        // port uninitialized?
        if (port == -1) {
            port = getDefaultPort(); // initialize
        }
        return port;
    }

    /**
     * Returns the default port for KNX Daemon and will be only called
     * during initialization. This may be overridden.
     *
     * @return default port
     */
    protected int getDefaultPort() {
        return configuration.getDaemonPort();
    }

    /**
     * Creates a new {@link HttpRequest.Builder} for requests to KNX Daemon.
     * <p/>
     * As we are using communicating via JSON only, the headers {@link HttpHeaders#ACCEPT},
     * {@link HttpHeaders#CONTENT_TYPE} are pre-defined with {@link MediaType#JSON_UTF_8}
     *
     * @param path the path to be requested to KNX Daemon
     * @return Builder for HttpRequest
     */
    public final HttpRequest.Builder newRequestBuilder(final String path) {
        Preconditions.checkArgument(path.startsWith("/"), "Path must start with /");
        try {
            return HttpRequest.newBuilder(new URI("http://localhost:" + this.port + path))
                    .header(HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.toString())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
        } catch (URISyntaxException e) {
            throw new KnxIllegalArgumentException("Invalid path provided: " + path);
        }
    }

    @Override
    public final void close() {
        this.cancel();
        this.executorService.shutdownNow();
    }
}
