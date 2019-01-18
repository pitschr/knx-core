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

import com.google.common.base.*;
import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.communication.*;
import li.pitschmann.knx.link.exceptions.*;
import org.slf4j.*;

import javax.annotation.*;
import java.io.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Abstract KNX Queue for KNX packets
 *
 * @author PITSCHR
 */
public abstract class AbstractKnxQueue implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractKnxQueue.class);
    private final String id;
    private final InternalKnxClient internalClient;
    private final SelectableChannel channel;
    private final BlockingQueue<Body> queue = new LinkedBlockingDeque<>();
    private final AtomicInteger number = new AtomicInteger(); // TODO: remove?

    /**
     * Constructor for Abstract KNX Queue
     *
     * @param id             the identifier for queue
     * @param internalClient internal KNX client for internal actions like informing plug-ins
     * @param channel        channel of communication
     */
    protected AbstractKnxQueue(final String id, final InternalKnxClient internalClient, final SelectableChannel channel) {
        this.id = id;
        this.internalClient = internalClient;
        this.channel = channel;
    }

    /**
     * Iterate the selected keys taken from selector for KNX packets until {@link InternalKnxClient} is closed.
     */
    @Override
    public final void run() {
        LOG.info("*** {}: START ***", id);

        try (final Selector selector = Selector.open()) {
            // prepare channel for non-blocking and register to selector
            this.channel.register(selector, interestOps());
            LOG.trace("{}: Channel {} registered to selector: {}", this.id, this.channel, selector);

            // iterate until current thread is interrupted
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    selector.select();
                    final Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                    while (selectedKeys.hasNext()) {
                        SelectionKey key = selectedKeys.next();
                        selectedKeys.remove();

                        // qualified?
                        if (valid(key)) {
                            action(key);
                            number.incrementAndGet();
                        }
                    }
                } catch (final KnxWrongChannelIdException wrongChannelIdException) {
                    LOG.warn("{}: KNX packet with wrong channel retrieved: {}", id, wrongChannelIdException.getMessage());
                    // silently ignore and proceed with next packet
                } catch (final InterruptedException ie) {
                    LOG.debug("{}: Channel is interrupted: {}", id, channel);
                    Thread.currentThread().interrupt();
                    // break loop as desired
                } catch (final IOException ioe) {
                    throw ioe;
                    // break loop due exception
                } catch (final Throwable e) {
                    LOG.error("{}: Error while processing KNX packets.", id, e);
                    this.internalClient.notifyPluginsError(e);
                    // proceed with next packet
                }
            }

        } catch (final IOException ex) {
            LOG.error("{}: Exception during open a new selector for channel: {}", id, channel, ex);
            throw new KnxException(String.format("Could not open a new selector for %s.", id), ex);
            // throw to channel communicator
        } finally {
            LOG.info("*** {}: END ***", id);
        }
    }

    /**
     * Returns the ID of current queue
     *
     * @return ID of current queue
     */
    protected final String getId() {
        return id;
    }

    /**
     * Restricted access to {@link InternalKnxClient} for KNX queue implementations.
     *
     * @return Current {@link InternalKnxClient}
     */
    @Nonnull
    protected final InternalKnxClient getInternalClient() {
        return internalClient;
    }

    /**
     * Returns the interests of operations for the selector key (e.g. read, write) for current thread queue
     *
     * @return The interest set for the resulting key
     */
    protected abstract int interestOps();

    /**
     * Returns if the given {@link SelectionKey} is valid for {@link #action(SelectionKey)} method.
     *
     * @param key a selection key representing the registration of a channel with a selector
     * @return {@code true} if the key is valid to perform the {@link #action(SelectionKey)} method, otherwise no.
     */
    protected abstract boolean valid(final SelectionKey key);

    /**
     * Action to be performed by the {@link SelectionKey}
     *
     * @param key a selection key representing the registration of a channel with a selector
     * @throws InterruptedException - if interrupted while waiting
     * @throws IOException          - if IO exception happened while performing the action method
     */
    protected abstract void action(final SelectionKey key) throws InterruptedException, IOException;

    /**
     * Adds {@link Body} to the queue.
     *
     * @param body body to be sent
     * @return {@code true} (as specified by {@link Collection#add})
     */
    protected boolean add(final @Nonnull Body body) {
        Preconditions.checkNotNull(body);
        return this.queue.add(body);
    }

    /**
     * Returns the head {@link Body} of queue, waiting if necessary until a {@link Body} becomes available.
     *
     * @return the {@link Body} from head of queue
     * @throws InterruptedException - if interrupted while waiting
     */
    @Nonnull
    public final Body next() throws InterruptedException {
        return this.queue.take();
    }
}