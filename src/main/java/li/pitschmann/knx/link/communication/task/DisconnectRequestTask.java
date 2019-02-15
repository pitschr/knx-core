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
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.Status;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * Disconnect task when KNX connection is subject to be closed by the remote machine. This class will reply the
 * {@link DisconnectResponseBody} frame.
 *
 * @author PITSCHR
 */
public final class DisconnectRequestTask implements Subscriber<Body> {
    private static final Logger LOG = LoggerFactory.getLogger(DisconnectRequestTask.class);
    private final InternalKnxClient client;

    public DisconnectRequestTask(InternalKnxClient client) {
        this.client = client;
    }

    @Override
    public void onNext(final Body body) {
        // we are interested in disconnect request only
        if (body instanceof DisconnectRequestBody) {
            LOG.trace("Disconnect Request received");
            final DisconnectRequestBody requestBody = (DisconnectRequestBody) body;

            // create body
            final DisconnectResponseBody responseBody = DisconnectResponseBody.create(this.client.getChannelId(), Status.E_NO_ERROR);
            this.client.getEventPool().disconnectEvent().setRequest(requestBody);
            LOG.trace("Disconnect Request saved.");
            this.client.getEventPool().disconnectEvent().setResponse(responseBody);
            LOG.trace("Disconnect Response saved.");
            try {
                this.client.send(responseBody);
                Sleeper.milliseconds(this.client.getConfig().getTimeoutDisconnectResponse());
            } finally {
                // initiate close by remote
                this.client.close();
            }
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        LOG.error("Error during Disconnect Request Task class", throwable);
    }

    @Override
    public void onComplete() {
        // NO-OP
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }
}
