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

import java.time.Instant;

/**
 * Interface for mutable KNX event data container
 *
 * @param <REQUEST>  instance of {@link RequestBody}
 * @param <RESPONSE> instance of {@link ResponseBody}
 * @author PITSCHR
 */
public interface KnxEvent<REQUEST extends RequestBody, RESPONSE extends ResponseBody> {
    /**
     * Returns the KNX request event
     *
     * @return request
     */
    REQUEST getRequest();

    /**
     * Sets the KNX request event
     *
     * @param request the request body
     */
    void setRequest(REQUEST request);

    /**
     * Returns the {@link Instant} time of KNX request event
     *
     * @return instant time for request
     */
    Instant getRequestTime();

    /**
     * Returns the KNX response event
     *
     * @return response
     */
    RESPONSE getResponse();

    /**
     * Sets the KNX response event
     *
     * @param response the response body
     */
    void setResponse(RESPONSE response);

    /**
     * Returns the {@link Instant} time of KNX response event
     *
     * @return instant time for response
     */
    Instant getResponseTime();

    /**
     * Returns if KNX request event is present
     *
     * @return {@code true} if present, otherwise {@code false}
     */
    boolean hasRequest();

    /**
     * Returns if KNX response event is present
     *
     * @return {@code true} if present, otherwise {@code false}
     */
    boolean hasResponse();
}
