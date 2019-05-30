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

package li.pitschmann.knx.test.strategy;

import li.pitschmann.knx.test.MockRequest;
import li.pitschmann.knx.test.MockServer;

/**
 * Request Strategy defining what should be done when creating
 * a request and after the request when KNX mock server got a
 * response from KNX NET/IP client
 *
 * @param <T> type of a context instance
 */
public interface RequestStrategy<T> {
    /**
     * Creates a new instance of mock request to be sent to KNX Net/IP client
     *
     * @param mockServer
     * @param context
     * @return a mocked request
     */
    MockRequest createRequest(MockServer mockServer, T context);
}
