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

package li.pitschmann.knx.core.communication.event;

import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Mutable KNX event data containing <strong>one request</strong>
 * and <strong>multiple response</strong>.
 *
 * @param <REQUEST>
 * @param <RESPONSE>
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
        return this.getResponse(0);
    }

    @Override
    public void setResponse(final @Nonnull RESPONSE response) {
        addResponse(response);
    }

    @Nullable
    public RESPONSE getResponse(final @Nonnull Predicate<RESPONSE> predicate) {
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
        return this.getResponseTime(0);
    }

    @Nullable
    public Instant getResponseTime(final @Nonnull Predicate<RESPONSE> predicate) {
        final var responseEvent = getResponseEvent(predicate);
        return responseEvent == null ? null : responseEvent.getResponseTime();
    }

    @Nullable
    public ResponseEvent<RESPONSE> getResponseEvent(final @Nonnull Predicate<RESPONSE> predicate) {
        for (final var responseEvent : new ArrayList<>(this.responseEvents)) {
            if (predicate.test(responseEvent.getResponse())) {
                return responseEvent;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public String toString() {
        return Strings.toStringHelper(this) //
                .add("requestEvent", this.requestEvent) //
                .add("responseEvents", this.responseEvents) //
                .toString();
    }
}