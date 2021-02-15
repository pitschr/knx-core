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

package li.pitschmann.knx.core.test.strategy.impl;

import li.pitschmann.knx.core.body.ConnectionStateResponseBody;
import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.body.Status;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.strategy.ConnectionStateStrategy;

/**
 * Default implementation for {@link ConnectionStateStrategy}
 * <p>
 * {@inheritDoc}
 */
public class DefaultConnectionStateStrategy implements ConnectionStateStrategy {
    /**
     * Returns the channel id that is used by the mock server
     *
     * @param mockServer the mock server
     * @return channel id
     */
    protected int getChannelId(final MockServer mockServer) {
        return mockServer.getChannelId();
    }

    /**
     * Return status. This method can be overridden.
     *
     * @return status
     */
    protected Status getStatus() {
        return Status.NO_ERROR;
    }

    @Override
    public ResponseBody createResponse(final MockServer mockServer, final RequestBody unused) {
        final var channelId = getChannelId(mockServer);
        final var status = getStatus();

        return ConnectionStateResponseBody.of(channelId, status);
    }
}
