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
 * Abstract Inbox Queue for KNX receiving packets from KNX Net/IP device
 *
 * @param <T> ByteChannel as default channel type
 * @author PITSCHR
 */
public abstract class AbstractInboxQueue<T extends ByteChannel> extends AbstractKnxQueue<T> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ByteBuffer buff = ByteBuffer.allocate(256);

    /**
     * Constructor for KNX Inbox Queue
     *
     * @param internalClient internal KNX client for internal actions like informing plug-ins
     * @param channel        channel of communication
     */
    public AbstractInboxQueue(final InternalKnxClient internalClient, final SelectableChannel channel) {
        super(internalClient, channel);
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
        log.trace("Method 'action(SelectionKey)' called.");

        final byte[] receivedBytes;
        final var channel = getChannel(key);
        try {
            log.trace("Receiving packet.");
            receive(channel, buff);
            receivedBytes = buff.array();
            if (log.isTraceEnabled()) {
                log.trace("Receiving packet: {}", ByteFormatter.formatHexAsString(receivedBytes));
            }
        } finally {
            buff.rewind();
        }

        final var body = BodyFactory.valueOf(receivedBytes);

        // verify the channel id
        if (this.getInternalClient().verifyChannelId(body)) {
            // channel id is correct
            if (log.isDebugEnabled()) {
                final var header = Header.valueOf(receivedBytes);
                log.debug("RECEIVE: {}\n" + //
                                "----------------------------------------------------------------\n" + //
                                "   Source: {}\n" + //
                                "   Target: {}\n" + //
                                "   Raw:    {}\n" + //
                                "   Header: {}\n" + //
                                "   Body:   {}\n" + //
                                "----------------------------------------------------------------", //
                        body.getServiceType().name(), //
                        Networker.getRemoteAddressAsString(channel), //
                        Networker.getLocalAddressAsString(channel), //
                        ByteFormatter.formatHexAsString(receivedBytes), //
                        header, //
                        body);
            }

            // add body to queue
            this.add(body);
            this.getInternalClient().notifyPluginsIncomingBody(body);
        }
    }

    /**
     * Reads the bytes from given {@code channel} into {@link ByteBuffer}
     *
     * @param channel
     * @param bb
     * @throws IOException
     */
    protected abstract void receive(final T channel, final ByteBuffer bb) throws IOException;
}
