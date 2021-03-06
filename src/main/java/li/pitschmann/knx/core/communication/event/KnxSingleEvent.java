/*
 * Copyright (C) 2021 Pitschmann Christoph
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

package li.pitschmann.knx.core.communication.event;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.utils.Strings;

import java.time.Instant;

/**
 * Mutable KNX event data containing <strong>one request</strong>
 * and <strong>one response</strong>.
 *
 * @param <REQUEST>  instance of {@link RequestBody}
 * @param <RESPONSE> instance of {@link ResponseBody}
 * @author PITSCHR
 */
public final class KnxSingleEvent<REQUEST extends RequestBody, RESPONSE extends ResponseBody> implements KnxEvent<REQUEST, RESPONSE> {
    private RequestEvent<REQUEST> requestEvent;
    private ResponseEvent<RESPONSE> responseEvent;

    @Nullable
    @Override
    public REQUEST getRequest() {
        return requestEvent == null ? null : requestEvent.getRequest();
    }

    @Override
    public void setRequest(final REQUEST request) {
        this.requestEvent = new RequestEvent<>(request);
    }

    @Nullable
    @Override
    public RESPONSE getResponse() {
        return responseEvent == null ? null : responseEvent.getResponse();
    }

    @Override
    public void setResponse(final RESPONSE response) {
        this.responseEvent = new ResponseEvent<>(response);
    }

    @Override
    public void clearResponse() {
        this.responseEvent = null;
    }

    @Override
    public boolean hasRequest() {
        return requestEvent != null;
    }

    @Override
    public boolean hasResponse() {
        return responseEvent != null;
    }

    @Nullable
    @Override
    public Instant getRequestTime() {
        return requestEvent == null ? null : requestEvent.getRequestTime();
    }

    @Nullable
    @Override
    public Instant getResponseTime() {
        return responseEvent == null ? null : responseEvent.getResponseTime();
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this) //
                .add("requestEvent", this.requestEvent) //
                .add("responseEvent", this.responseEvent) //
                .toString();
    }
}
