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

import li.pitschmann.knx.core.body.BodyFactory;
import li.pitschmann.knx.core.communication.InternalKnxClient;
import li.pitschmann.knx.core.header.Header;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Networker;
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
     * @param client  internal KNX client for internal actions like informing plug-ins
     * @param channel channel of communication
     */
    protected AbstractInboxQueue(final InternalKnxClient client, final SelectableChannel channel) {
        super(client, channel);
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
            buff.flip();
            receivedBytes = new byte[buff.limit()];
            buff.get(receivedBytes);
            if (log.isTraceEnabled()) {
                log.trace("Receiving packet: {}", ByteFormatter.formatHexAsString(receivedBytes));
            }
            System.out.println("PITSCHR (" + System.currentTimeMillis() + "): Received Bytes: " + receivedBytes.length);
        } finally {
            buff.clear();
        }

        final var body = BodyFactory.of(receivedBytes);

        // verify the channel id
        if (getInternalClient().verifyChannelId(body)) {
            // channel id is correct
            if (log.isDebugEnabled()) {
                final var header = Header.of(receivedBytes);
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
            add(body);
            getInternalClient().notifyIncomingBody(body);
        }
    }

    /**
     * Reads the bytes from given {@code channel} into {@link ByteBuffer}
     *
     * @param channel the channel
     * @param bb      byte buffer that should keep received data
     * @throws IOException If an I/O error occurs
     */
    protected abstract void receive(final T channel, final ByteBuffer bb) throws IOException;
}
