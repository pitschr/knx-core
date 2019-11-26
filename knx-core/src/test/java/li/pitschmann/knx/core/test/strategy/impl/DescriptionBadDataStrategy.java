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

package li.pitschmann.knx.core.test.strategy.impl;

import li.pitschmann.knx.core.test.MockRequest;
import li.pitschmann.knx.core.test.MockResponse;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.strategy.DescriptionStrategy;
import li.pitschmann.knx.core.utils.Bytes;

/**
 * Bad Data provided for {@link DescriptionStrategy}
 */
public final class DescriptionBadDataStrategy implements DescriptionStrategy {

    @Override
    public MockResponse createResponse(MockServer mockServer, MockRequest request) {
        return new MockResponse(Bytes.toByteArray("06100204004634010200100000000083497f01ece000170ccc1be08008da4d4454204b4e5820495020526f7574657200000000000000000000000000"));
    }

}