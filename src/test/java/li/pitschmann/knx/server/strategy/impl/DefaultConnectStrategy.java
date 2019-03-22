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

package li.pitschmann.knx.server.strategy.impl;

import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.Status;
import li.pitschmann.knx.link.body.hpai.HPAI;
import li.pitschmann.knx.link.body.tunnel.ConnectionResponseData;
import li.pitschmann.knx.server.MockRequest;
import li.pitschmann.knx.server.MockResponse;
import li.pitschmann.knx.server.MockServer;
import li.pitschmann.knx.server.strategy.ConnectStrategy;

/**
 * Default implementation for {@link ConnectStrategy}
 * <p>
 * {@inheritDoc}
 */
public class DefaultConnectStrategy implements ConnectStrategy {
    /**
     * Returns the channel id. This method can be overridden.
     *
     * @param mockServer
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
        return Status.E_NO_ERROR;
    }

    /**
     * Returns the HPAI for mock server. This method can be overridden.
     *
     * @param mockServer
     * @return HPAI of mock server
     */
    protected HPAI getHPAI(final MockServer mockServer) {
        return mockServer.getHPAI();
    }

    /**
     * Returns a new instance of {@link ConnectionResponseData}. This method can be overridden.
     *
     * @param mockServer
     * @return connection response data
     */
    protected ConnectionResponseData getConnectionResponseData(final MockServer mockServer) {
        return ConnectionResponseData.create(mockServer.getIndividualAddress());
    }

    @Override
    public MockResponse createResponse(final MockServer mockServer, final MockRequest request) {
        final var responseBody = ConnectResponseBody.create(
                getChannelId(mockServer), //
                getStatus(), //
                getHPAI(mockServer), //
                getConnectionResponseData(mockServer));

        return new MockResponse(responseBody);
    }
}
