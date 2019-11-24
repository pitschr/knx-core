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

import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.utils.Strings;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Objects;

/**
 * Mutable KNX event data for {@link RequestBody}
 *
 * @param <T> instance of {@link RequestBody}
 * @author PITSCHR
 */
public class RequestEvent<T extends RequestBody> {
    private Instant requestTime;
    private T request;

    /**
     * Returns the time when last request was set.
     *
     * @return An {@link Instant}
     */
    @Nonnull
    public Instant getRequestTime() {
        return this.requestTime;
    }

    /**
     * Returns the request
     *
     * @return The request body which was set last time
     */
    @Nonnull
    public T getRequest() {
        return this.request;
    }

    /**
     * Sets the request and update the request time to now
     *
     * @param request the request body
     */
    public void setRequest(final @Nonnull T request) {
        this.requestTime = Instant.now();
        this.request = Objects.requireNonNull(request);
    }

    @Nonnull
    @Override
    public String toString() {
        return Strings.toStringHelper(this) //
                .add("requestTime", this.requestTime) //
                .add("request", this.request) //
                .toString();
    }
}
