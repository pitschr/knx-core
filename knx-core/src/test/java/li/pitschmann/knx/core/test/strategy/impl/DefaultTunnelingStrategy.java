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

import li.pitschmann.knx.core.body.Status;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.cemi.CEMI;
import li.pitschmann.knx.core.test.MockRequest;
import li.pitschmann.knx.core.test.MockResponse;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.strategy.TunnelingStrategy;

/**
 * Default implementation for {@link TunnelingStrategy}
 * <p>
 * {@inheritDoc}
 */
public class DefaultTunnelingStrategy implements TunnelingStrategy {
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
     * Returns the sequence from KNX mock server that is used for request. This method can be overridden.
     *
     * @param mockServer
     * @return sequence from KNX mock server
     */
    protected int getRequestSequence(final MockServer mockServer) {
        return mockServer.getAndIncrementTunnelingRequestSequence();
    }

    /**
     * Returns the sequence from {@link TunnelingRequestBody}.
     * <p>
     * Normally the sequence should be always same like request body because
     * we are acknowledging this. However for testing purpose it may useful
     * to return a wrong sequence. This method can be overridden.
     *
     * @param requestBody
     * @return sequence
     */
    protected int getResponseSequence(final TunnelingRequestBody requestBody) {
        return requestBody.getSequence();
    }

    /**
     * Return status. This method can be overridden.
     *
     * @return status
     */
    protected Status getStatus() {
        return Status.E_NO_ERROR;
    }

    @Override
    public MockRequest createRequest(final MockServer mockServer, final CEMI cemi) {
        final var channelId = getChannelId(mockServer);
        final var sequence = getRequestSequence(mockServer);

        return new MockRequest(TunnelingRequestBody.of(channelId, sequence, cemi));
    }

    @Override
    public MockResponse createResponse(final MockServer mockServer, final MockRequest request) {
        final var channelId = getChannelId(mockServer);
        final var sequence = getResponseSequence(request.getBody());
        final var status = getStatus();

        return new MockResponse(TunnelingAckBody.of(channelId, sequence, status));
    }
}
