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
import li.pitschmann.knx.link.body.ResponseBody;

import java.time.Instant;

/**
 * Mutable KNX event data containing <strong>one request</strong> and <strong>one response</strong>.
 *
 * @param <REQUEST>  instance of {@link RequestBody}
 * @param <RESPONSE> instance of {@link ResponseBody}
 * @author PITSCHR
 */
public interface KnxEvent<REQUEST extends RequestBody, RESPONSE extends ResponseBody> {
    REQUEST getRequest();

    void setRequest(REQUEST request);

    Instant getRequestTime();

    RESPONSE getResponse();

    void setResponse(RESPONSE response);

    Instant getResponseTime();

    boolean hasRequest();

    boolean hasResponse();
}
