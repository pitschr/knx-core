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
import li.pitschmann.knx.test.MockResponse;
import li.pitschmann.knx.test.MockServer;

/**
 * A special {@link ResponseStrategy} to ignore the requests from KNX mock client
 * <p/>
 * This is useful if you want to simulate/test a no reaction on e.g. description request frame.
 * In this case the KNX client should resend the description request frame again which will be
 * responded by a different strategy class.
 */
public final class IgnoreStrategy implements ResponseStrategy {
    public static final IgnoreStrategy DEFAULT = new IgnoreStrategy();

    @Override
    public MockResponse createResponse(final MockServer mockServer, final MockRequest request) {
        // do nothing ...
        return null;
    }
}
