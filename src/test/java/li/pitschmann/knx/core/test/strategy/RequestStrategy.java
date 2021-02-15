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

package li.pitschmann.knx.core.test.strategy;

import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.test.MockServer;

/**
 * Request Strategy defining what should be done when creating
 * a request when KNX mock server got a request from KNX NET/IP client
 *
 * @param <T> type of a context instance
 */
public interface RequestStrategy<T> {
    /**
     * Returns an instance of {@link RequestBody} that was sent to KNX mock server
     *
     * @param mockServer the mock server
     * @param context    the context of request strategy
     * @return a request body
     */
    RequestBody createRequest(MockServer mockServer, T context);
}
