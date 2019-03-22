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

package li.pitschmann.knx.server.trigger;

import li.pitschmann.knx.server.MockServer;
import li.pitschmann.knx.server.strategy.impl.DefaultDisconnectStrategy;

/**
 * A trigger rule for sending a disconnect request
 */
public class DisconnectRequestTriggerRule implements TriggerRule {
    private final MockServer mockServer;

    public DisconnectRequestTriggerRule(final MockServer mockServer) {
        this.mockServer = mockServer;
    }

    @Override
    public boolean apply() {
        final var mockRequest = new DefaultDisconnectStrategy().createRequest(this.mockServer, null);
        this.mockServer.addToOutbox(mockRequest.getBody());
        return true;
    }
}
