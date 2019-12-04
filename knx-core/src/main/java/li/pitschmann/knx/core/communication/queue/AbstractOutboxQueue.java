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

import li.pitschmann.knx.core.body.Body;
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
import java.util.Collection;

/**
 * Abstract Outbox Queue for KNX packets to be sent to KNX Net/IP device
 *
 * @param <T> ByteChannel as default channel type
 * @author PITSCHR
 */
public abstract class AbstractOutboxQueue<T extends ByteChannel> extends AbstractKnxQueue<T> {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructor for KNX Outbox Queue
     *
     * @param client  internal KNX client for internal actions like informing plug-ins
     * @param channel channel of communication
     */
    public AbstractOutboxQueue(final InternalKnxClient client, final SelectableChannel channel) {
        super(client, channel);
    }

    @Override
    protected int interestOps() {
        return SelectionKey.OP_WRITE;
    }

    @Override
    protected boolean valid(final SelectionKey key) {
        return key.isValid() && key.isWritable();
    }

    /**
     * Sends the packet from queue head and the next packet to channel, the packet , verifies if the packet has correct channel id and adds to the appropriate
     * inbox queue.
     *
     * @param key selection key
     * @throws InterruptedException if interrupted while waiting
     * @throws IOException          exception while writing to {@link ByteChannel}
     */
    protected void action(final SelectionKey key) throws InterruptedException, IOException {
        log.trace("Method 'action(SelectionKey)' called.");

        // get body from queue
        final var body = next();

        // packet: header + body
        final var packetToSend = body.getRawData(true);

        // write to channel
        final var channel = getChannel(key);
        log.debug("Sending packet: {}", body);
        if (!channel.isOpen()) {
            log.warn("Channel is not open. Write aborted.");
            return;
        }
        send(channel, ByteBuffer.wrap(packetToSend));
        log.trace("Packet sent.");
        this.getInternalClient().notifyOutgoingBody(body);

        if (log.isDebugEnabled()) {
            log.debug("SEND: {}\n" + //
                            "----------------------------------------------------------------\n" + //
                            "   Source:  {}\n" + //
                            "   Target:  {}\n" + //
                            "   Raw:     {}\n" + //
                            "   Header:  {}\n" + //
                            "   Body:    {}\n" + //
                            "----------------------------------------------------------------", //
                    body.getServiceType().name(), //
                    Networker.getLocalAddressAsString(channel), //
                    Networker.getRemoteAddressAsString(channel), //
                    ByteFormatter.formatHexAsString(packetToSend), //
                    Header.of(body), //
                    body);
        }
    }

    /**
     * Send {@link Body}. It will adds {@link Body} to the outbox queue for sending and will be internally picked up
     * by the {@link #run()} method.
     *
     * @param body body to be sent
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean send(final Body body) {
        return add(body);
    }


    /**
     * Sends the given byte arrays to {@code channel}
     *
     * @param channel
     * @throws IOException
     */
    protected abstract void send(final T channel, final ByteBuffer bb) throws IOException;
}
