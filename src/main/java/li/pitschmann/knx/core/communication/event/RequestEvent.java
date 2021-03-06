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

import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.utils.Strings;

import java.time.Instant;

/**
 * Mutable KNX event data for {@link RequestBody}
 *
 * @param <T> instance of {@link RequestBody}
 * @author PITSCHR
 */
public class RequestEvent<T extends RequestBody> {
    private final Instant requestTime;
    private final T request;

    /**
     * Creates a new request event (package-protected)
     * to be called by {@link KnxSingleEvent} or {@link KnxMultiEvent}
     *
     * @param request the request body
     */
    RequestEvent(final T request) {
        this.requestTime = Instant.now();
        this.request = request;
    }

    /**
     * Returns the time when last request was set.
     *
     * @return An {@link Instant}
     */
    public Instant getRequestTime() {
        return this.requestTime;
    }

    /**
     * Returns the request
     *
     * @return The request body which was set last time
     */
    public T getRequest() {
        return this.request;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this) //
                .add("requestTime", this.requestTime) //
                .add("request", this.request) //
                .toString();
    }
}
