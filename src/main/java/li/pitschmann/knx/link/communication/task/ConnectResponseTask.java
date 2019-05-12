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

package li.pitschmann.knx.link.communication.task;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.exceptions.KnxBodyNotReceivedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * Listens to {@link ConnectResponseBody} frame that is sent by KNX Net/IP device to client when
 * {@link ConnectRequestBody} was sent.
 *
 * @author PITSCHR
 */
public final class ConnectResponseTask implements Subscriber<Body> {
    private static final Logger log = LoggerFactory.getLogger(ConnectResponseTask.class);
    private final InternalKnxClient client;
    private Subscription subscription;

    public ConnectResponseTask(final InternalKnxClient client) {
        this.client = client;
    }

    @Override
    public void onNext(final Body body) {
        // we are interested in connect response only
        if (body instanceof ConnectResponseBody) {
            final var responseBody = (ConnectResponseBody) body;
            log.debug("Connect Response received: {}", responseBody);
            this.client.getEventPool().connectEvent().setResponse(responseBody);
            log.trace("Connect Response saved.");
            // now cancel the subscription as we only expect this frame once time at beginning only!
            this.subscription.cancel();
            log.trace("Subscription for task '{}' cancelled.", this.getClass());
        } else {
            // at beginning we MUST receive the ConnectResponseBody otherwise something went wrong!
            throw new KnxBodyNotReceivedException(ConnectResponseBody.class);
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        log.error("Error during Connect Response Task class", throwable);
    }

    @Override
    public void onComplete() {
        // NO-OP
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1); // receive it only once time at beginning!
    }

}
