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

package li.pitschmann.knx.core.test.strategy;

import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.test.MockServer;

/**
 * Response Strategy defining what should be respond to KNX Net/IP client
 * when KNX mock server got a request
 */
public interface ResponseStrategy {
    /**
     * Returns an instance of {@link ResponseBody} to be sent to KNX Net/IP client
     *
     * @param mockServer the mock server
     * @param request    the request body
     * @return a response body
     */
    ResponseBody createResponse(MockServer mockServer, RequestBody request);
}
