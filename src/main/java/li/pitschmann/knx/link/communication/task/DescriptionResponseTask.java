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
 * Listens to {@link DescriptionResponseBody} frame that is sent by KNX Net/IP router to client when
 * {@link DescriptionRequestBody} was sent.
 *
 * @author PITSCHR
 */
public final class DescriptionResponseTask implements Subscriber<Body> {
    private static final Logger LOG = LoggerFactory.getLogger(DescriptionResponseTask.class);
    private final InternalKnxClient client;

    public DescriptionResponseTask(final InternalKnxClient client) {
        this.client = client;
    }

    @Override
    public void onNext(final Body body) {
        // we are interested in description response only
        if (body instanceof DescriptionResponseBody) {
            final DescriptionResponseBody responseBody = (DescriptionResponseBody) body;
            LOG.debug("Description response received: {}", responseBody);
            this.client.getEventPool().descriptionEvent().setResponse(responseBody);
            LOG.trace("Description response saved.");
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        LOG.error("Error during Description Response Task class", throwable);
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
