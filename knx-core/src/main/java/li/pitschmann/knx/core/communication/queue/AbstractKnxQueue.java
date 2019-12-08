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
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.exceptions.KnxWrongChannelIdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Abstract KNX Queue for KNX packets
 *
 * @author PITSCHR
 */
public abstract class AbstractKnxQueue<T extends ByteChannel> implements Runnable {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final InternalKnxClient client;
    private final SelectableChannel channel;
    private final BlockingQueue<Body> queue = new LinkedBlockingDeque<>();

    /**
     * Constructor for Abstract KNX Queue
     *
     * @param client  internal KNX client for internal actions like informing plug-ins
     * @param channel channel of communication
     */
    protected AbstractKnxQueue(final InternalKnxClient client, final SelectableChannel channel) {
        this.client = Objects.requireNonNull(client);
        this.channel = Objects.requireNonNull(channel);
    }

    /**
     * Iterate the selected keys taken from selector for KNX packets until {@link InternalKnxClient} is closed.
     */
    @Override
    public final void run() {
        log.info("*** START ***");

        try (final var selector = openSelector()) {
            // iterate until current thread is interrupted
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    selector.select();
                    final var selectedKeys = selector.selectedKeys().iterator();
                    while (selectedKeys.hasNext()) {
                        final var key = selectedKeys.next();
                        selectedKeys.remove();

                        // qualified?
                        if (valid(key)) {
                            action(key);
                        }
                    }
                } catch (final KnxWrongChannelIdException wrongChannelIdException) {
                    log.warn("KNX packet with wrong channel retrieved: {}", wrongChannelIdException.getMessage());
                    this.client.notifyError(wrongChannelIdException);
                    // ignore and proceed with next packet
                } catch (final InterruptedException ie) {
                    log.debug("Channel is interrupted: {}", selector);
                    Thread.currentThread().interrupt();
                    // break loop as desired
                } catch (final IOException ioe) {
                    throw ioe;
                    // break loop due exception
                } catch (final Throwable e) {
                    log.error("Error while processing KNX packets.", e);
                    this.client.notifyError(new Throwable("Error while processing KNX packets.", e));
                    // proceed with next packet
                }
            }
        } catch (final IOException ioe) {
            log.error("IOException for channel: {}", channel, ioe);
            throw new KnxException(String.format("IOException in '%s'.", getClass()), ioe);
            // throw to channel communicator
        } finally {
            log.info("*** END ***");
        }
    }

    /**
     * Returns a new instance of {@link Selector} that listens on the given {@link SelectableChannel}
     *
     * @return newly created selector with interest ops taken from {@link #interestOps()}
     * @throws IOException - if IO exception happened while performing the action method
     */
    public final Selector openSelector() throws IOException {
        final var selector = Selector.open();

        // prepare channel for non-blocking and register to selector
        channel.register(selector, interestOps());
        log.trace("Channel {} registered to selector: {}", channel, selector);

        return selector;
    }

    /**
     * Restricted access to {@link InternalKnxClient} for KNX queue implementations.
     *
     * @return Current {@link InternalKnxClient}
     */
    protected final InternalKnxClient getInternalClient() {
        return client;
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
     * Returns the channel from {@link SelectionKey}
     *
     * @param key
     * @return An instance of {@link ByteChannel}
     */
    @SuppressWarnings("unchecked")

    protected T getChannel(final SelectionKey key) {
        return (T) key.channel();
    }

    /**
     * Adds {@link Body} to the queue.
     *
     * @param body body to be sent
     * @return {@code true} (as specified by {@link Collection#add})
     */
    protected boolean add(final Body body) {
        return this.queue.add(Objects.requireNonNull(body));
    }

    /**
     * Returns the head {@link Body} of queue, waiting if necessary until a {@link Body} becomes available.
     *
     * @return the {@link Body} from head of queue
     * @throws InterruptedException - if interrupted while waiting
     */
    public final Body next() throws InterruptedException {
        return Objects.requireNonNull(this.queue.take());
    }
}
