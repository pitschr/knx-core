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

package li.pitschmann.knx.core.communication.queue;

import li.pitschmann.knx.core.communication.InternalKnxClient;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;

/**
 * Default Outbox Queue for KNX packets to be sent to KNX Net/IP device
 *
 * @author PITSCHR
 */
public final class DefaultOutboxQueue extends AbstractOutboxQueue<ByteChannel> {
    /**
     * Constructor for Default KNX Outbox Queue
     *
     * @param client  internal KNX client for internal actions like informing plug-ins
     * @param channel channel of communication
     */
    public DefaultOutboxQueue(final @Nonnull InternalKnxClient client, final @Nonnull SelectableChannel channel) {
        super(client, channel);
    }

    @Override
    protected void send(final @Nonnull ByteChannel channel, final @Nonnull ByteBuffer bb) throws IOException {
        channel.write(bb);
    }
}