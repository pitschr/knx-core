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

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.server.MockServer;

/**
 * A trigger rule for sending request
 */
public class RequestTriggerRule implements TriggerRule {
    private final MockServer mockServer;
    private final Body body;

    public RequestTriggerRule(final MockServer mockServer, final Body body) {
        this.mockServer = mockServer;
        this.body = body;
    }

    @Override
    public boolean apply() {
        this.mockServer.addToOutbox(body);
        return true;
    }
}
