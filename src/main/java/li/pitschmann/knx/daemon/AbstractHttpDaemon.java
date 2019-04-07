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
import li.pitschmann.knx.link.Configuration;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.utils.Executors;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.HttpConstants;
import ro.pippo.core.Pippo;

import javax.annotation.Nonnull;
import java.net.http.HttpClient;
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
    private boolean ready;
    private boolean cancel;

    protected AbstractHttpDaemon(final @Nonnull Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Starts the HTTP Daemon
     */
    protected void start() {
        Preconditions.checkState(this.executorService == null, "It seems the KNX Daemon is already started?");
        this.executorService = Executors.newSingleThreadExecutor(true);
        this.executorService.execute(this);
        this.executorService.shutdown();
    }

    @Override
    public void run() {
        final var pippo = new Pippo(new KnxHttpApplication());

        try (final var client = DefaultKnxClient.createStarted(configuration)) {
            ((KnxHttpApplication) pippo.getApplication()).setKnxClient(client);
            pippo.getApplication().getContentTypeEngine(HttpConstants.ContentType.APPLICATION_JSON);
            pippo.start();
            ready = true;
            logger.debug("Http Daemon Server started: {}", client);
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
     */
    public void cancel() {
        this.cancel = true;
    }

    /**
     * Returns if the KNX Daemon has been cancelled or interrupted.
     *
     * @return {@code true} if cancelled/interrupted, otherwise {@code false}
     */
    public boolean isCancelled() {
        return cancel || Thread.currentThread().isInterrupted();
    }

    /**
     * Returns if the KNX Daemon is ready
     *
     * @return {@code true} if daemon and server is ready, otherwise {@code false}
     */
    public boolean isReady() {
        return this.ready;
    }

    /**
     * Creates a new instance of {@link HttpClient} that is designed to communicate with the
     * KNX Daemon directly
     *
     * @return a new instance of {@link HttpClient}
     */
    public HttpClient createHttpClient() {
        return HttpClient.newHttpClient();
    }

    @Override
    public void close() {
        this.cancel();
        this.executorService.shutdownNow();
    }
}
