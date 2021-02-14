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

import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.utils.Strings;

import java.time.Instant;

/**
 * Mutable KNX event data for {@link ResponseBody}
 *
 * @param <T> instance of {@link ResponseBody}
 * @author PITSCHR
 */
public class ResponseEvent<T extends ResponseBody> {
    private Instant responseTime;
    private T response;

    /**
     * Creates a new response event (package-protected)
     * to be called by {@link KnxSingleEvent} or {@link KnxMultiEvent}
     *
     * @param response the response body
     */
    ResponseEvent(final T response) {
        this.responseTime = Instant.now();
        this.response = response;
    }

    /**
     * Returns the time when last response was set.
     *
     * @return An {@link Instant}
     */
    public Instant getResponseTime() {
        return this.responseTime;
    }

    /**
     * Returns the response
     *
     * @return The response body which was set last time
     */
    public T getResponse() {
        return this.response;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this) //
                .add("responseTime", this.responseTime) //
                .add("response", this.response) //
                .toString();
    }
}
