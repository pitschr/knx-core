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

package li.pitschmann.knx.link.communication.queue;

import li.pitschmann.knx.link.Constants;
import li.pitschmann.knx.link.communication.InternalKnxClient;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.util.Objects;

/**
 * Discovery Outbox Queue for KNX multicast packets to be sent to KNX Net/IP device
 * This class is special because the multicast communication is based on broadcast.
 *
 * @author PITSCHR
 */
public final class MulticastOutboxQueue extends AbstractOutboxQueue<DatagramChannel> {
    private final InetSocketAddress multicastSocketAddress;

    /**
     * Constructor for KNX Discovery Inbox Queue
     *
     * @param internalClient internal KNX client for internal actions like informing plug-ins
     * @param channel        channel of communication
     */
    public MulticastOutboxQueue(final @Nonnull InternalKnxClient internalClient, final @Nonnull SelectableChannel channel) {
        super(internalClient, channel);

        final var config = internalClient.getConfig();

        // use config setting, otherwise fall back to default setting
        final var remoteAddress = Objects.requireNonNullElse(config.getRemoteControlAddress(), config.getMulticastChannelAddress());
        final var remotePort = config.getRemoteControlPort();

        multicastSocketAddress = new InetSocketAddress(remoteAddress, remotePort);
        log.debug("Multicast Outbox Socket: {}", multicastSocketAddress);
    }

    @Override
    protected void send(final @Nonnull DatagramChannel channel, final @Nonnull ByteBuffer bb) throws IOException {
        channel.send(bb, multicastSocketAddress);
    }
}
