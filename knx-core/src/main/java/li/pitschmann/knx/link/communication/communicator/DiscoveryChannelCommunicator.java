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

import li.pitschmann.knx.link.Constants;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.SearchResponseBody;
import li.pitschmann.knx.link.communication.ChannelFactory;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.communication.queue.DiscoveryInboxQueue;
import li.pitschmann.knx.link.communication.queue.DiscoveryOutboxQueue;
import li.pitschmann.utils.Networker;

import javax.annotation.Nonnull;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.List;

/**
 * Communicator for discovery channel related packets
 *
 * @author PITSCHR
 */
public final class DiscoveryChannelCommunicator extends AbstractChannelCommunicator<DatagramChannel> {
    private List<MembershipKey> membershipKeys;

    public DiscoveryChannelCommunicator(final @Nonnull InternalKnxClient client) {
        super(client);
    }

    @Override
    @Nonnull
    protected DatagramChannel newChannel(final @Nonnull InternalKnxClient internalClient) {
        // creates new channel
        final var channel = ChannelFactory.newDiscoveryChannel(internalClient);

        // join channels, the membership keys will be used for laving the joined
        // multicast groups -> see cleanUp() method.
        this.membershipKeys = Networker.joinChannels(channel, Constants.Default.KNX_MULTICAST_ADDRESS);

        return channel;
    }

    @Override
    protected void cleanUp() {
        membershipKeys.stream().forEach(MembershipKey::drop);
        log.debug("Membership of all multicast groups dropped.");
    }

    @Override
    @Nonnull
    protected DiscoveryInboxQueue createInboxQueue(final @Nonnull InternalKnxClient internalClient, final @Nonnull DatagramChannel channel) {
        return new DiscoveryInboxQueue(internalClient, channel);
    }

    @Override
    @Nonnull
    protected DiscoveryOutboxQueue createOutboxQueue(final @Nonnull InternalKnxClient internalClient, final @Nonnull DatagramChannel channel) {
        return new DiscoveryOutboxQueue(internalClient, channel);
    }

    @Override
    @Nonnull
    protected boolean isCompatible(final @Nonnull Body body) {
        return body instanceof SearchResponseBody;
    }
}
