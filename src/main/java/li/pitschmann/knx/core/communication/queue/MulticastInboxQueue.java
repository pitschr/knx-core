/*
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

package li.pitschmann.knx.core.communication.queue;

import li.pitschmann.knx.core.communication.InternalKnxClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;

/**
 * Inbox Queue for KNX receiving multicast packets from KNX Net/IP device.
 * This class is special because the multicast communication is based on broadcast.
 *
 * @author PITSCHR
 */
public final class MulticastInboxQueue extends AbstractInboxQueue<DatagramChannel> {

    /**
     * Constructor for KNX Discovery Inbox Queue
     *
     * @param client  internal KNX client for internal actions like informing plug-ins
     * @param channel channel of communication
     */
    public MulticastInboxQueue(final InternalKnxClient client, final SelectableChannel channel) {
        super(client, channel);
    }

    @Override
    protected void receive(final DatagramChannel channel, final ByteBuffer bb) throws IOException {
        channel.receive(bb);
    }
}
