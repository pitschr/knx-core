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

import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.communication.*;
import org.slf4j.*;

import java.util.concurrent.Flow.*;

/**
 * Observes the {@link TunnellingAckBody} which is received from KNX Net/IP Router on data channel.
 *
 * @author PITSCHR
 */
public final class TunnellingAckTask implements Subscriber<Body> {
    private static final Logger LOG = LoggerFactory.getLogger(TunnellingAckTask.class);
    private final InternalKnxClient client;

    public TunnellingAckTask(final InternalKnxClient client) {
        this.client = client;
    }

    @Override
    public void onNext(Body body) {
        // we are interested in tunnelling acknowledge only
        if (body instanceof TunnellingAckBody) {
            LOG.debug("Tunnelling Ack received: {}", body);
            final TunnellingAckBody ackBody = (TunnellingAckBody) body;

            final KnxEventData<TunnellingRequestBody, TunnellingAckBody> eventData = this.client.getEventPool().get(ackBody);
            if (eventData.hasResponse()) {
                LOG.warn("Event already acknowledged? Looks there was a communication problem. Ignoring acknowledge: {} for event: {}", ackBody,
                        eventData);
            } else {
                eventData.setResponse(ackBody);
                LOG.debug("Event acknowledged: {}", eventData);
            }
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        LOG.error("Error during Tunnelling Ack Task class", throwable);
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
