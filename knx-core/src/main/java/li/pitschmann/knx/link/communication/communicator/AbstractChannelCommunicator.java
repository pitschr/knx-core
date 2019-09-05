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

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ControlChannelRelated;
import li.pitschmann.knx.link.body.DataChannelRelated;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.communication.event.KnxEvent;
import li.pitschmann.knx.link.communication.queue.AbstractInboxQueue;
import li.pitschmann.knx.link.communication.queue.AbstractOutboxQueue;
import li.pitschmann.knx.link.communication.queue.DefaultInboxQueue;
import li.pitschmann.knx.link.communication.queue.DefaultOutboxQueue;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Executors;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract channel communicator. It coordinates the communication for given channel
 * ( created by {@link #newChannel(InternalKnxClient)} method) and creates an inbox
 * and an outbox queue for non-blocking communication.
 * <p/>
 * It also controls the lifecycle of channel (opening, closing) and all receiving
 * KNX packets are forwarded to all subscribers.
 *
 * @param <C> Instance of {@link SelectableChannel}
 * @author PITSCHR
 */
public abstract class AbstractChannelCommunicator<C extends SelectableChannel> extends SubmissionPublisher<Body> implements Runnable {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final InternalKnxClient internalClient;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final ExecutorService queueExecutor;
    private final ExecutorService communicationExecutor;

    private final C channel;
    private final AbstractInboxQueue<? extends ByteChannel> inboxQueue;
    private final AbstractOutboxQueue<? extends ByteChannel> outboxQueue;

    protected AbstractChannelCommunicator(final @Nonnull InternalKnxClient client) {
        this.internalClient = Objects.requireNonNull(client);

        this.channel = Objects.requireNonNull(newChannel(this.internalClient));
        log.info("Channel registered: {} (open: {}, registered: {}, blocking: {})", channel, channel.isOpen(), channel.isRegistered(), channel.isBlocking());

        // creates inbox and outbox queues
        this.inboxQueue = createInboxQueue(this.internalClient, this.channel);
        this.outboxQueue = createOutboxQueue(this.internalClient, this.channel);
        log.trace("Inbox and Outbox Queues created: InboxQueue={}, OutboxQueue={}.", this.inboxQueue, this.outboxQueue);

        // creates queue executor
        this.queueExecutor = Executors.newFixedThreadPool(2, true);
        this.queueExecutor.submit(inboxQueue);
        this.queueExecutor.submit(outboxQueue);
        this.queueExecutor.shutdown();
        log.info("Queue Executor created: {}", this.queueExecutor);

        // creates executor for communication
        this.communicationExecutor = Executors.newFixedThreadPool(internalClient.getConfig().getCommunicationExecutorPoolSize(), true);
        log.info("Communication Executor created with size of {}: {}", internalClient.getConfig().getCommunicationExecutorPoolSize(), this.communicationExecutor);
    }

    /**
     * Creates a new channel to be used by this communicator. It will be called during initialization
     * and only once time.
     *
     * @param internalClient
     * @return A new channel
     */
    @Nonnull
    protected abstract C newChannel(final @Nonnull InternalKnxClient internalClient);

    /**
     * Creates a new instance of {@link AbstractInboxQueue} that should be used by this communicator
     *
     * @param internalClient
     * @param channel
     * @return new instance of {@link AbstractInboxQueue}
     */
    @Nonnull
    protected AbstractInboxQueue<? extends ByteChannel> createInboxQueue(final @Nonnull InternalKnxClient internalClient, final @Nonnull C channel) {
        return new DefaultInboxQueue(internalClient, channel);
    }

    /**
     * Creates a new instance of {@link AbstractOutboxQueue} that should be used by this communicator
     *
     * @param internalClient
     * @param channel
     * @return new instance of {@link AbstractOutboxQueue}
     */
    @Nonnull
    protected AbstractOutboxQueue<? extends ByteChannel> createOutboxQueue(final @Nonnull InternalKnxClient internalClient, final @Nonnull C channel) {
        return new DefaultOutboxQueue(internalClient, channel);
    }

    @Nonnull
    public C getChannel() {
        return channel;
    }

    @Override
    public void run() {
        log.trace("*** START ***");

        while (!Thread.interrupted()) {
            try {
                log.debug("Waiting for next packet from channel");
                final var body = this.inboxQueue.next();
                // accepted body
                if (this.isCompatible(body)) {
                    if (this.isClosed()) {
                        log.warn("Body not sent to subscribers because submission publisher is closed: {}", body);
                    } else {
                        log.debug("Body from channel to be sent to subscribers: {}", body);
                        this.submit(body);
                    }
                }
                // not accepted body
                else {
                    log.warn("Body is not expected for this channel and therefore ignored: {}", body);
                }
            } catch (final InterruptedException ex) {
                log.debug("Channel receiver is cancelled.");
                Thread.currentThread().interrupt();
            }
        }

        log.trace("*** END ***");
    }

    @Override
    public void subscribe(Flow.Subscriber<? super Body> subscriber) {
        log.debug("Subscriber added with MDC: {}", subscriber);
        super.subscribe(Executors.wrapSubscriberWithMDC(subscriber));
    }

    /**
     * Returns if given {@link Body} is compatible
     *
     * @param body
     * @return {@code true} if compatible, otherwise {@code false}
     */
    public abstract boolean isCompatible(final @Nonnull Body body);

    /**
     * Send {@link Body} to the outbox queue
     *
     * @param body
     */
    public final void send(final @Nonnull Body body) {
        this.outboxQueue.send(body);
        log.debug("Body added to outbox queue: {}", body);
    }

    /**
     * Send {@link RequestBody} packet <strong>asynchronously</strong> to the appropriate channel. It returns a {@link Future} for further processing.
     *
     * @param requestBody
     * @param msTimeout   timeout in milliseconds waiting until the expected response body is fetched
     * @return a {@link CompletableFuture} representing pending completion of the task containing either an instance of {@link ResponseBody},
     * or {@code null} if no response was received because of e.g. timeout
     */
    @Nonnull
    public final <U extends ResponseBody> CompletableFuture<U> send(final @Nonnull RequestBody requestBody, final long msTimeout) {
        return CompletableFuture.supplyAsync(() -> sendAndWaitInternal(requestBody, msTimeout), this.communicationExecutor);
    }

    /**
     * Sends the {@link RequestBody} packet to the appropriate channel and then finally wait for the expected body which
     * is an instance of {@link ResponseBody}.
     * <p>
     * The appropriate channel will be chosen by {@link ControlChannelRelated} and {@link DataChannelRelated} marker
     * interfaces.
     *
     * @param requestBody
     * @param msTimeout   timeout in milliseconds waiting until expected response body is fetched
     * @return an instance of {@link ResponseBody}, or {@code null} if no response was received because of e.g. timeout
     */
    @Nullable
    private final <U extends ResponseBody> U sendAndWaitInternal(final @Nonnull RequestBody requestBody, final long msTimeout) {
        final var eventPool = this.internalClient.getEventPool();

        // add request body to event pool
        eventPool.add(requestBody);
        log.trace("Request Body added to event pool.");

        // mark as dirty
        if (requestBody instanceof TunnelingRequestBody) {
            this.internalClient.getStatusPool().setDirty(((TunnelingRequestBody) requestBody).getCEMI().getDestinationAddress());
        }

        var attempts = 1;
        final var totalAttempts = 3; // hard-coded (up to 3 times will be retried in case of no response)
        final var eventWaiting = this.internalClient.getConfig().getIntervalEvent();
        U responseBody;

        do {
            // send packet
            send(requestBody);

            // iterate for event response
            final var start = System.currentTimeMillis();
            KnxEvent<RequestBody, U> event;
            do {
                event = eventPool.get(requestBody);
            }
            // no null check required here because we know that eventData is always present here
            while ( // true = no response yet
                    !event.hasResponse()
                            // true = not interrupted
                            && Sleeper.milliseconds(eventWaiting)
                            // true = request timeout not reached yet
                            && (System.currentTimeMillis() - start) < msTimeout);

            responseBody = event.getResponse();
            if (responseBody == null) {
                log.warn("No response received yet for request ({}/{}): {}", attempts, totalAttempts, requestBody);
            } else {
                log.debug("Response received for request ({}/{}): {}, Response: {}", attempts, totalAttempts, requestBody, responseBody);
            }

            // if no response and not interrupted try to repeat this step up to 'totalAttempts'
        } while (responseBody == null && attempts++ < totalAttempts && !Thread.currentThread().isInterrupted());

        return responseBody;
    }

    /**
     * Closes the channel and executor services.
     */
    @Override
    public final void close() {
        log.trace("Method 'close()' invoked.");
        if (!closed.getAndSet(true)) {
            super.close();

            // clean up actions
            try {
                cleanUp();
            } finally {
                // close channel and executors
                Closeables.closeQuietly(this.channel);
                Closeables.shutdownQuietly(this.queueExecutor);
                Closeables.shutdownQuietly(this.communicationExecutor);
                log.debug("Method 'close()' called.");
            }

        }
    }

    /**
     * Method to be invoked before closing the communicator
     */
    protected void cleanUp() {
        // NO-OP
    }
}
