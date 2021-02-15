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

import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.body.Status;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.cemi.CEMI;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.strategy.TunnelingStrategy;
import li.pitschmann.knx.core.utils.Preconditions;

/**
 * Default implementation for {@link TunnelingStrategy}
 * <p>
 * {@inheritDoc}
 */
public class DefaultTunnelingStrategy implements TunnelingStrategy {
    /**
     * Returns the channel id. This method can be overridden.
     *
     * @param mockServer the mock server
     * @return channel id
     */
    protected int getChannelId(final MockServer mockServer) {
        return mockServer.getChannelId();
    }

    /**
     * Returns the sequence from KNX mock server that is used for request. This method can be overridden.
     *
     * @param mockServer the mock server
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
     * @param requestBody request body
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
        return Status.NO_ERROR;
    }

    @Override
    public RequestBody createRequest(final MockServer mockServer, final CEMI cemi) {
        final var channelId = getChannelId(mockServer);
        final var sequence = getRequestSequence(mockServer);

        return TunnelingRequestBody.of(channelId, sequence, cemi);
    }

    @Override
    public ResponseBody createResponse(final MockServer mockServer, final RequestBody request) {
        Preconditions.checkArgument(request instanceof TunnelingRequestBody);
        final var channelId = getChannelId(mockServer);
        final var sequence = getResponseSequence((TunnelingRequestBody) request);
        final var status = getStatus();

        return TunnelingAckBody.of(channelId, sequence, status);
    }
}
