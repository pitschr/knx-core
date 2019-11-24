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

package li.pitschmann.knx.core.communication.communicator;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.communication.InternalKnxClient;
import li.pitschmann.knx.core.communication.event.KnxEvent;
import li.pitschmann.knx.core.communication.event.KnxMultiEvent;
import li.pitschmann.knx.core.communication.queue.AbstractInboxQueue;
import li.pitschmann.knx.core.communication.queue.AbstractOutboxQueue;
import li.pitschmann.knx.core.communication.queue.DefaultInboxQueue;
import li.pitschmann.knx.core.communication.queue.DefaultOutboxQueue;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.utils.Closeables;
import li.pitschmann.knx.core.utils.Executors;
import li.pitschmann.knx.core.utils.Sleeper;
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
import java.util.function.Predicate;

/**
 * Abstract channel communicator. It coordinates the communication for given channel
 * ( created by {@link #newChannel(InternalKnxClient)} method) and creates an inbox
 * and an outbox queue for non-blocking communication.
 * <p/>
 * It also controls the lifecycle of channel (opening, closing) and all receiving
 * KNX packets are forwarded to all subscribers.
 *
 * @author PITSCHR
 */
public abstract class AbstractChannelCommunicator extends SubmissionPublisher<Body> implements Runnable {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final InternalKnxClient client;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final ExecutorService queueExecutor;
    private final ExecutorService communicationExecutor;

    private final SelectableChannel channel;
    private final AbstractInboxQueue<? extends ByteChannel> inboxQueue;
    private final AbstractOutboxQueue<? extends ByteChannel> outboxQueue;

    protected AbstractChannelCommunicator(final @Nonnull InternalKnxClient client) {
        this.client = Objects.requireNonNull(client);

        this.channel = Objects.requireNonNull(newChannel(this.client));
        log.info("Channel registered: {} (open: {}, registered: {}, blocking: {})", channel, channel.isOpen(), channel.isRegistered(), channel.isBlocking());

        // creates inbox and outbox queues
        this.inboxQueue = createInboxQueue(this.client, this.channel);
        this.outboxQueue = createOutboxQueue(this.client, this.channel);
        log.trace("Inbox and Outbox Queues created: InboxQueue={}, OutboxQueue={}.", this.inboxQueue, this.outboxQueue);

        // creates queue executor
        this.queueExecutor = Executors.newFixedThreadPool(2, true);
        this.queueExecutor.submit(inboxQueue);
        this.queueExecutor.submit(outboxQueue);
        this.queueExecutor.shutdown();
        log.info("Queue Executor created: {}", this.queueExecutor);

        // creates executor for communication
        final var poolSize = this.client.getConfig(CoreConfigs.Communication.EXECUTOR_POOL_SIZE);
        this.communicationExecutor = Executors.newFixedThreadPool(poolSize, true);
        log.info("Communication Executor created with size of {}: {}", poolSize, this.communicationExecutor);
    }

    /**
     * Creates a new channel to be used by this communicator. It will be called during initialization
     * and only once time.
     *
     * @param client
     * @return A new channel
     */
    @Nonnull
    protected abstract SelectableChannel newChannel(final @Nonnull InternalKnxClient client);

    /**
     * Creates a new instance of {@link AbstractInboxQueue} that should be used by this communicator
     *
     * @param client
     * @param channel
     * @return new instance of {@link AbstractInboxQueue}
     */
    @Nonnull
    protected AbstractInboxQueue<? extends ByteChannel> createInboxQueue(final @Nonnull InternalKnxClient client,
                                                                         final @Nonnull SelectableChannel channel) {
        return new DefaultInboxQueue(client, channel);
    }

    /**
     * Creates a new instance of {@link AbstractOutboxQueue} that should be used by this communicator
     *
     * @param client
     * @param channel
     * @return new instance of {@link AbstractOutboxQueue}
     */
    @Nonnull
    protected AbstractOutboxQueue<? extends ByteChannel> createOutboxQueue(final @Nonnull InternalKnxClient client,
                                                                           final @Nonnull SelectableChannel channel) {
        return new DefaultOutboxQueue(client, channel);
    }

    @Nonnull
    public SelectableChannel getChannel() {
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
        this.outboxQueue.send(Objects.requireNonNull(body));
        log.debug("Body added to outbox queue: {}", body);
    }

    /**
     * Send {@link RequestBody} packet <strong>asynchronously</strong> to the appropriate channel.
     * It returns a {@link Future} for further processing.
     *
     * @param requestBody
     * @param msTimeout   timeout in milliseconds waiting until the expected response body is fetched
     * @return a {@link CompletableFuture} representing pending completion of the task containing
     * either an instance of {@link ResponseBody}, or {@code null} if no response was received
     */
    @Nonnull
    public final <T extends ResponseBody> CompletableFuture<T> send(final @Nonnull RequestBody requestBody,
                                                                    final long msTimeout) {
        return send(requestBody, null, msTimeout);
    }

    /**
     * Send {@link RequestBody} packet <strong>asynchronously</strong> to the appropriate channel.
     * It returns a {@link Future} for further processing.
     *
     * @param requestBody
     * @param predicate   predicates if the condition of {@link KnxEvent} was meet ({@code true}) or not ({@code false}),
     *                    {@code null} means that no predicate check
     * @param msTimeout   timeout in milliseconds waiting until the expected response body is fetched
     * @return a {@link CompletableFuture} representing pending completion of the task containing
     * either an instance of {@link ResponseBody}, or {@code null} if no response was received
     */
    @Nonnull
    public final <T extends ResponseBody> CompletableFuture<T> send(final @Nonnull RequestBody requestBody,
                                                                    final @Nullable Predicate<T> predicate,
                                                                    final long msTimeout) {
        return CompletableFuture
                .supplyAsync(() -> sendAndWaitInternal(requestBody, predicate, msTimeout), this.communicationExecutor)
                .exceptionally((throwable) -> {
                    this.client.notifyError(throwable);
                    return null;
                });
    }

    /**
     * Sends the {@link RequestBody} packet to the appropriate channel and then finally wait for the expected response
     * that meets the {@link Predicate} criteria and is an an instance of {@link ResponseBody}.
     *
     * @param requestBody
     * @param predicate   predicates if the condition of {@link KnxEvent} was meet ({@code true}) or not ({@code false}),
     *                    {@code null} means that no predicate check
     * @param msTimeout   timeout in milliseconds waiting until expected response body is fetched
     * @return an instance of {@link ResponseBody}, or {@code null} if no response was received (timeout) or no response that meets the preconditions was received
     */
    @Nullable
    private final <T extends ResponseBody> T sendAndWaitInternal(final @Nonnull RequestBody requestBody,
                                                                 final @Nullable Predicate<T> predicate,
                                                                 final long msTimeout) {
        final var eventPool = this.client.getEventPool();

        // add request body to event pool
        eventPool.add(requestBody);
        log.trace("Request Body added to event pool.");

        // mark as dirty (if possible)
        this.client.getStatusPool().setDirty(requestBody);

        // send packet
        var attempts = 1;
        final var totalAttempts = CoreConfigs.Event.TOTAL_ATTEMPTS;
        final var checkInterval = CoreConfigs.Event.CHECK_INTERVAL;
        T responseBody = null;

        do {
            send(requestBody);

            // iterate for event response
            final var start = System.currentTimeMillis();
            KnxEvent<RequestBody, T> event;
            do {
                event = eventPool.get(requestBody);

                if (event.hasResponse()) {
                    // any response received
                    if (predicate == null) {
                        // no predicate defined -> just take the first response
                        responseBody = event.getResponse();
                    } else if (event instanceof KnxMultiEvent) {
                        // multi knx event -> try to find the response that meets the predicate
                        // may be null
                        final var multiEvent = (KnxMultiEvent<RequestBody, T>) event;
                        responseBody = multiEvent.getResponse(predicate);
                    } else {
                        // predicate defined -> test it, if it is meet then returns the response
                        // otherwise null
                        final var tmpResponse = event.getResponse();
                        responseBody = predicate.test(tmpResponse) ? tmpResponse : null;
                    }

                    if (responseBody != null) {
                        log.debug("Response received for request ({}/{}): {}", attempts, totalAttempts, responseBody);
                        break;
                    }
                }
            } while ( // true = not interrupted
                    Sleeper.milliseconds(checkInterval)
                            // true = request timeout not reached yet
                            && (System.currentTimeMillis() - start) < msTimeout);

            // additional check, as it may happen that there was a timeout
            if (responseBody == null) {
                log.warn("No response received yet for request ({}/{}): {}", attempts, totalAttempts, requestBody);
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
