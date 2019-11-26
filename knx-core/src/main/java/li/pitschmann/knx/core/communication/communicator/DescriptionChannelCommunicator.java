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

package li.pitschmann.knx.core.communication.communicator;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.DescriptionResponseBody;
import li.pitschmann.knx.core.communication.ChannelFactory;
import li.pitschmann.knx.core.communication.InternalKnxClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.channels.SelectableChannel;

/**
 * Communicator for description channel related packets
 *
 * @author PITSCHR
 */
public final class DescriptionChannelCommunicator extends AbstractChannelCommunicator {
    DescriptionChannelCommunicator(final @Nonnull InternalKnxClient client) {
        super(client);
    }

    @Nonnull
    @Override
    protected SelectableChannel newChannel(final @Nonnull InternalKnxClient client) {
        return ChannelFactory.newDescriptionChannel(client);
    }

    @Override
    public boolean isCompatible(final @Nullable Body body) {
        return body instanceof DescriptionResponseBody;
    }
}