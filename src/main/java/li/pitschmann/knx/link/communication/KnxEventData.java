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

package li.pitschmann.knx.link.communication;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;

import java.time.Instant;

/**
 * Mutable KNX event data containing about KNX/IP event communications.
 *
 * @param <REQUEST>  instance of {@link RequestBody}
 * @param <RESPONSE> instance of {@link ResponseBody}
 * @author PITSCHR
 */
public final class KnxEventData<REQUEST extends RequestBody, RESPONSE extends ResponseBody> {
    private Instant requestTime;
    private Instant responseTime;
    private REQUEST request;
    private RESPONSE response;

    /**
     * Returns the time when last {@code REQUEST} was set.
     *
     * @return An {@link Instant}
     */
    public Instant getRequestTime() {
        return this.requestTime;
    }

    /**
     * Returns the time when last {@code RESPONSE} was set.
     *
     * @return An {@link Instant}
     */
    public Instant getResponseTime() {
        return this.responseTime;
    }

    /**
     * Returns if {@code REQUEST} was set.
     *
     * @return {@code true} if it contains request otherwise {@code false}
     */
    public boolean hasRequest() {
        return this.request != null;
    }

    /**
     * Returns if {@code RESPONSE} was set.
     *
     * @return {@code true} if it contains response otherwise {@code false}
     */
    public boolean hasResponse() {
        return this.response != null;
    }

    /**
     * Returns the {@code REQUEST}
     *
     * @return The request body which was set last time
     */
    public REQUEST getRequest() {
        return this.request;
    }

    /**
     * Sets the {@code REQUEST} and update the request time to now
     *
     * @param request the request body
     */
    public void setRequest(final REQUEST request) {
        this.requestTime = Instant.now();
        this.request = request;
    }

    /**
     * Returns the {@code RESPONSE}
     *
     * @return The response body which was set last time
     */
    public RESPONSE getResponse() {
        return this.response;
    }

    /**
     * Sets the {@code RESPONSE} and update the request time to now
     *
     * @param response the repsonse body
     */
    public void setResponse(final RESPONSE response) {
        this.responseTime = Instant.now();
        this.response = response;
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(KnxEventData.class)
                .add("requestTime", this.requestTime)
                .add("request", this.request)
                .add("responseTime", this.responseTime)
                .add("response", this.response)
                .toString();
        // @formatter:on
    }
}
