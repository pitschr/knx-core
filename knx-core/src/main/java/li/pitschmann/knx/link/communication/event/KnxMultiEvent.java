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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public final class KnxMultiEvent<REQUEST extends RequestBody, RESPONSE extends ResponseBody> implements KnxEvent<REQUEST, RESPONSE> {
    private RequestEvent<REQUEST> requestEvent;
    private List<ResponseEvent<RESPONSE>> responseEvents = new LinkedList<>();

    @Nullable
    public REQUEST getRequest() {
        return requestEvent == null ? null : requestEvent.getRequest();
    }

    public void setRequest(final @Nonnull REQUEST request) {
        final var newRequestEvent = new RequestEvent<REQUEST>();
        newRequestEvent.setRequest(request);
        this.requestEvent = newRequestEvent;
    }

    @Nullable
    public RESPONSE getResponse(final int index) {
        return this.responseEvents.size() > index ? this.responseEvents.get(index).getResponse() : null;
    }

    @Nullable
    @Override
    public RESPONSE getResponse() {
        return this.responseEvents.isEmpty() ? null : this.responseEvents.get(0).getResponse();
    }

    @Override
    public void setResponse(final @Nonnull RESPONSE response) {
        addResponse(response);
    }

    public boolean hasRequest() {
        return requestEvent != null;
    }

    public boolean hasResponse() {
        return !responseEvents.isEmpty();
    }

    public void addResponse(final @Nonnull RESPONSE response) {
        final var newResponseEvent = new ResponseEvent<RESPONSE>();
        newResponseEvent.setResponse(response);
        this.responseEvents.add(newResponseEvent);
    }

    @Nullable
    @Override
    public Instant getRequestTime() {
        return requestEvent == null ? null : requestEvent.getRequestTime();
    }

    @Nullable
    public Instant getResponseTime(final int index) {
        return this.responseEvents.size() > index ? this.responseEvents.get(index).getResponseTime() : null;
    }

    @Nullable
    @Override
    public Instant getResponseTime() {
        return this.responseEvents.isEmpty() ? null : this.responseEvents.get(0).getResponseTime();
    }

    @Nonnull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("requestEvent", this.requestEvent) //
                .add("responseEvents", this.responseEvents) //
                .toString();
    }
}
