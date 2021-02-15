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
import li.pitschmann.knx.core.body.DisconnectResponseBody;
import li.pitschmann.knx.core.communication.InternalKnxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * Listens to {@link DisconnectResponseBody} frame when KNX connection was subject to be closed by the local machine and
 * replied by the KNX Net/IP device.
 *
 * @author PITSCHR
 */
public final class DisconnectResponseTask implements Subscriber<Body> {
    private static final Logger log = LoggerFactory.getLogger(DisconnectResponseTask.class);
    private final InternalKnxClient client;

    public DisconnectResponseTask(final InternalKnxClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public void onNext(final @Nullable Body body) {
        // we are interested in disconnect response only
        if (body instanceof DisconnectResponseBody) {
            final var responseBody = (DisconnectResponseBody) body;
            log.debug("Disconnect Response received: {}", responseBody);
            this.client.getEventPool().disconnectEvent().setResponse(responseBody);
            log.trace("Disconnect Response saved.");
        }
    }

    @Override
    public void onError(final @Nullable Throwable throwable) {
        log.error("Error during Disconnect Response Task class", throwable);
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
