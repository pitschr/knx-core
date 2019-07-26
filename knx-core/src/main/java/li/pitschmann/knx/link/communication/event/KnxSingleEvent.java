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

package li.pitschmann.knx.link.communication.event;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;

import java.time.Instant;

/**
 * Mutable KNX event data containing <strong>one request</strong> and <strong>one response</strong>.
 *
 * @param <REQUEST>  instance of {@link RequestBody}
 * @param <RESPONSE> instance of {@link ResponseBody}
 * @author PITSCHR
 */
public final class KnxSingleEvent<REQUEST extends RequestBody, RESPONSE extends ResponseBody> implements KnxEvent<REQUEST, RESPONSE> {
    private RequestEvent<REQUEST> requestEvent;
    private ResponseEvent<RESPONSE> responseEvent;

    public REQUEST getRequest() {
        return requestEvent == null ? null : requestEvent.getRequest();
    }

    public void setRequest(final REQUEST request) {
        final var newRequestEvent = new RequestEvent<REQUEST>();
        newRequestEvent.setRequest(request);
        this.requestEvent = newRequestEvent;
    }

    public RESPONSE getResponse() {
        return responseEvent == null ? null : responseEvent.getResponse();
    }

    public void setResponse(final RESPONSE response) {
        final var newEvent = new ResponseEvent<RESPONSE>();
        newEvent.setResponse(response);
        this.responseEvent = newEvent;
    }

    public boolean hasRequest() {
        return requestEvent != null;
    }

    public boolean hasResponse() {
        return responseEvent != null;
    }

    @Override
    public Instant getRequestTime() {
        return requestEvent == null ? null : requestEvent.getRequestTime();
    }

    @Override
    public Instant getResponseTime() {
        return responseEvent == null ? null : responseEvent.getResponseTime();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("requestEvent", this.requestEvent) //
                .add("responseEvent", this.responseEvent) //
                .toString();
    }
}