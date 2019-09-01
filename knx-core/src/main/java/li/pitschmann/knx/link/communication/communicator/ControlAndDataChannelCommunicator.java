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

package li.pitschmann.knx.link.communication.communicator;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ControlChannelRelated;
import li.pitschmann.knx.link.body.DataChannelRelated;
import li.pitschmann.knx.link.communication.ChannelFactory;
import li.pitschmann.knx.link.communication.InternalKnxClient;

import javax.annotation.Nonnull;
import java.nio.channels.SelectableChannel;

/**
 * Communicator for control <strong>AND</strong> data channel related packets.
 * <p/>
 * When using NAT then control and data packets are going over the same channel.
 *
 * @author PITSCHR
 */
public final class ControlAndDataChannelCommunicator extends AbstractChannelCommunicator<SelectableChannel> {
    public ControlAndDataChannelCommunicator(final @Nonnull InternalKnxClient client) {
        super(client);
    }

    @Override
    @Nonnull
    protected SelectableChannel newChannel(final @Nonnull InternalKnxClient internalClient) {
        // we will use the control channel for data as well
        return ChannelFactory.newControlChannel(internalClient);
    }

    @Override
    public boolean isCompatible(final @Nonnull Body body) {
        return body instanceof ControlChannelRelated
                || body instanceof DataChannelRelated;
    }
}
