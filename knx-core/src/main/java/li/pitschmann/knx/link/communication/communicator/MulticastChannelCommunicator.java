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
import li.pitschmann.knx.link.body.MulticastChannelRelated;
import li.pitschmann.knx.link.communication.ChannelFactory;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.communication.queue.MulticastInboxQueue;
import li.pitschmann.knx.link.communication.queue.MulticastOutboxQueue;
import li.pitschmann.knx.link.config.CoreConfigs;
import li.pitschmann.knx.utils.Networker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.channels.MembershipKey;
import java.nio.channels.SelectableChannel;
import java.util.List;

/**
 * Channel communicator for multicast related packets (Discovery, Routing)
 *
 * @author PITSCHR
 */
public final class MulticastChannelCommunicator extends AbstractChannelCommunicator {
    private List<MembershipKey> membershipKeys;

    MulticastChannelCommunicator(final @Nonnull InternalKnxClient client) {
        super(client);
    }

    @Nonnull
    @Override
    protected final SelectableChannel newChannel(final @Nonnull InternalKnxClient client) {
        // creates new channel
        final var channel = ChannelFactory.newMulticastChannel(client);

        // join channels, the membership keys will be used for laving the joined
        // multicast groups -> see cleanUp() method.
        this.membershipKeys = Networker.joinChannels(channel, client.getConfig(CoreConfigs.Multicast.ADDRESS));

        return channel;
    }

    @Override
    protected final void cleanUp() {
        membershipKeys.forEach(MembershipKey::drop);
        log.debug("Membership of all multicast groups dropped.");
    }

    @Nonnull
    @Override
    protected final MulticastInboxQueue createInboxQueue(final @Nonnull InternalKnxClient client,
                                                         final @Nonnull SelectableChannel channel) {
        return new MulticastInboxQueue(client, channel);
    }

    @Nonnull
    @Override
    protected final MulticastOutboxQueue createOutboxQueue(final @Nonnull InternalKnxClient client,
                                                           final @Nonnull SelectableChannel channel) {
        return new MulticastOutboxQueue(client, channel);
    }

    @Override
    public boolean isCompatible(final @Nullable Body body) {
        return body instanceof MulticastChannelRelated;
    }
}
