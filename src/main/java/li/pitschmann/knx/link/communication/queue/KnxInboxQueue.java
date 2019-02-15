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

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.BodyFactory;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.header.Header;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Networker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * Inbox Queue for KNX receiving packets from KNX Net/IP router
 *
 * @author PITSCHR
 */
public final class KnxInboxQueue extends AbstractKnxQueue {
    private static final Logger LOG = LoggerFactory.getLogger(KnxInboxQueue.class);
    private final ByteBuffer buff = ByteBuffer.allocate(256);

    /**
     * Constructor for KNX Inbox Queue
     *
     * @param id             the identifier for queue
     * @param internalClient internal KNX client for internal actions like informing plug-ins
     * @param channel        channel of communication
     */
    public KnxInboxQueue(final String id, final InternalKnxClient internalClient, final SelectableChannel channel) {
        super("KnxInboxQueue[" + id + "]", internalClient, channel);
    }

    @Override
    protected int interestOps() {
        return SelectionKey.OP_READ;
    }

    @Override
    protected boolean valid(final SelectionKey key) {
        return key.isValid() && key.isReadable();
    }

    /**
     * Receives the next packet from channel, verifies if the packet has correct channel id and adds to the appropriate
     * inbox queue.
     *
     * @param key selection key
     * @throws IOException exception while reading from {@link ByteChannel}
     */
    protected void action(final SelectionKey key) throws IOException {
        LOG.trace("{}: Method 'action(SelectionKey)' called.", getId());

        final byte[] receivedBytes;
        final ByteChannel channel = (ByteChannel) key.channel();
        try {
            LOG.trace("{}: Receiving packet.", getId());
            channel.read(buff);
            receivedBytes = buff.array();
            if (LOG.isTraceEnabled()) {
                LOG.trace("{}: Receiving packet: {}", getId(), ByteFormatter.formatHexAsString(receivedBytes));
            }
        } finally {
            buff.rewind();
        }

        final Body body = BodyFactory.valueOf(receivedBytes);

        // verify the channel id
        if (this.getInternalClient().verifyChannelId(body)) {
            // channel id is correct
            if (LOG.isDebugEnabled()) {
                final Header header = Header.valueOf(receivedBytes);
                LOG.debug("RECEIVE\n" + //
                                "----------------------------------------------------------------\n" + //
                                "   Source: {}\n" + //
                                "   Target: {} ({})\n" + //
                                "   Raw:    {}\n" + //
                                "   Header: {}\n" + //
                                "   Body:   {}\n" + //
                                "----------------------------------------------------------------", //
                        Networker.getRemoteAddressAsString(channel), Networker.getLocalAddressAsString(channel), getId(),
                        ByteFormatter.formatHexAsString(receivedBytes), header, body);
            }

            // add body to queue
            this.add(body);
            this.getInternalClient().notifyPluginsIncomingBody(body);
        }
    }
}