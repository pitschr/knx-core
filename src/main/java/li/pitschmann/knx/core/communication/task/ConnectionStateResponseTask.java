/*
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

package li.pitschmann.knx.core.communication.task;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.ConnectionStateResponseBody;
import li.pitschmann.knx.core.communication.InternalKnxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * Listens to {@link ConnectionStateResponseTask} received by KNX Net/IP device about the health.
 *
 * @author PITSCHR
 */
public final class ConnectionStateResponseTask implements Subscriber<Body> {
    private static final Logger log = LoggerFactory.getLogger(ConnectionStateResponseTask.class);
    private final InternalKnxClient client;

    public ConnectionStateResponseTask(final InternalKnxClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public void onNext(final @Nullable Body body) {
        // we are interested in connection state response only
        if (body instanceof ConnectionStateResponseBody) {
            final var responseBody = (ConnectionStateResponseBody) body;
            log.debug("Connection State Response received: {}", responseBody);
            this.client.getEventPool().connectionStateEvent().setResponse(responseBody);
            log.trace("Connection State Response saved.");
        }
    }

    @Override
    public void onError(final @Nullable Throwable throwable) {
        log.error("Error during Connection State Response Task class", throwable);
    }

    @Override
    public void onComplete() {
        // NO-OP
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }
}
