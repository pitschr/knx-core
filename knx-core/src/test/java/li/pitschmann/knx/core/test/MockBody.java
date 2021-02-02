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

package li.pitschmann.knx.core.test;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.header.ServiceType;

/**
 * Mock Body
 * <p>
 * With KNX mock server we are simulating any scenarios (incl. bad / corrupt scenario).
 */
public class MockBody implements Body {
    private final byte[] bytes;

    protected MockBody(final byte[] bytes) {
        this.bytes = bytes.clone();
    }

    @Override
    public ServiceType getServiceType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] toByteArray() {
        return this.bytes.clone();
    }
}
