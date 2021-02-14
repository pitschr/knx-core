/*
 * KNX Link - A library for KNX Net/IP communication
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Mutable KNX event data containing <strong>one request</strong>
 * and <strong>multiple response</strong>.
 *
 * @param <REQUEST>  instance that extends {@link RequestBody}
 * @param <RESPONSE> instance that extends {@link ResponseBody}
 * @author PITSCHR
 */
public final class KnxMultiEvent<REQUEST extends RequestBody, RESPONSE extends ResponseBody> implements KnxEvent<REQUEST, RESPONSE> {
    private RequestEvent<REQUEST> requestEvent;
    private List<ResponseEvent<RESPONSE>> responseEvents = new LinkedList<>();

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
    public RESPONSE getResponse(final int index) {
        return this.responseEvents.size() > index ? this.responseEvents.get(index).getResponse() : null;
    }

    @Nullable
    @Override
    public RESPONSE getResponse() {
        return this.getResponse(0);
    }

    @Override
    public void setResponse(final RESPONSE response) {
        addResponse(response);
    }

    @Override
    public void clearResponse() {
        this.responseEvents.clear();
    }

    @Nullable
    public RESPONSE getResponse(final Predicate<RESPONSE> predicate) {
        final var responseEvent = getResponseEvent(predicate);
        return responseEvent == null ? null : responseEvent.getResponse();
    }

    @Override
    public boolean hasRequest() {
        return requestEvent != null;
    }

    @Override
    public boolean hasResponse() {
        return !responseEvents.isEmpty();
    }

    public void addResponse(final RESPONSE response) {
        this.responseEvents.add(new ResponseEvent<>(response));
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
        return this.getResponseTime(0);
    }

    @Nullable
    public Instant getResponseTime(final Predicate<RESPONSE> predicate) {
        final var responseEvent = getResponseEvent(predicate);
        return responseEvent == null ? null : responseEvent.getResponseTime();
    }

    @Nullable
    public ResponseEvent<RESPONSE> getResponseEvent(final Predicate<RESPONSE> predicate) {
        for (final var responseEvent : new ArrayList<>(this.responseEvents)) {
            if (predicate.test(responseEvent.getResponse())) {
                return responseEvent;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this) //
                .add("requestEvent", this.requestEvent) //
                .add("responseEvents", this.responseEvents) //
                .toString();
    }
}
