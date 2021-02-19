/*
 * Copyright (C) 2021 Pitschmann Christoph
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

import li.pitschmann.knx.core.annotations.Nullable;
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

import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * Abstract channel communicator. It coordinates the communication for given channel
 * ( created by {@link #newChannel(InternalKnxClient)} method) and creates an inbox
 * and an outbox queue for non-blocking communication.
 * <p>
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

    protected AbstractChannelCommunicator(final InternalKnxClient client) {
        this.client = Objects.requireNonNull(client);

        this.channel = Objects.requireNonNull(newChannel(this.client));
        log.debug("Channel registered: {} (open: {}, registered: {}, blocking: {})", channel, channel.isOpen(), channel.isRegistered(), channel.isBlocking());

        // creates inbox and outbox queues
        this.inboxQueue = createInboxQueue(this.client, this.channel);
        this.outboxQueue = createOutboxQueue(this.client, this.channel);
        log.debug("Inbox and Outbox Queues created: InboxQueue={}, OutboxQueue={}.", this.inboxQueue, this.outboxQueue);

        // creates queue executor
        this.queueExecutor = Executors.newFixedThreadPool(2, true);
        this.queueExecutor.submit(inboxQueue);
        this.queueExecutor.submit(outboxQueue);
        this.queueExecutor.shutdown();
        log.debug("Queue Executor created: {}", this.queueExecutor);

        // creates executor for communication
        final var poolSize = this.client.getConfig(CoreConfigs.Communication.EXECUTOR_POOL_SIZE);
        this.communicationExecutor = Executors.newFixedThreadPool(poolSize, true);
        log.debug("Communication Executor created with size of {}: {}", poolSize, this.communicationExecutor);
    }

    /**
     * Creates a new channel to be used by this communicator. It will be called during initialization
     * and only once time.
     *
     * @param client the internal KNX client
     * @return A new channel
     */
    protected abstract SelectableChannel newChannel(final InternalKnxClient client);

    /**
     * Creates a new instance of {@link AbstractInboxQueue} that should be used by this communicator
     *
     * @param client  the internal KNX client
     * @param channel channel where packets are received
     * @return new instance of {@link AbstractInboxQueue}
     */
    protected AbstractInboxQueue<? extends ByteChannel> createInboxQueue(final InternalKnxClient client,
                                                                         final SelectableChannel channel) {
        return new DefaultInboxQueue(client, channel);
    }

    /**
     * Creates a new instance of {@link AbstractOutboxQueue} that should be used by this communicator
     *
     * @param client  the internal KNX client
     * @param channel channel where packets are sent
     * @return new instance of {@link AbstractOutboxQueue}
     */
    protected AbstractOutboxQueue<? extends ByteChannel> createOutboxQueue(final InternalKnxClient client,
                                                                           final SelectableChannel channel) {
        return new DefaultOutboxQueue(client, channel);
    }

    public SelectableChannel getChannel() {
        return channel;
    }

    @Override
    public void run() {
        log.trace("*** START ***");

        while (!Thread.interrupted() && !isClosed()) {
            try {
                log.debug("Waiting for next packet from channel");
                final var body = this.inboxQueue.next();
                // accepted body
                if (this.isCompatible(body)) {
                    if (isClosed()) {
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
                log.debug("Channel receiver is cancelled");
                Thread.currentThread().interrupt();
            } catch (final Throwable t) {
                // race condition: log issue only when it is not closed
                if (!isClosed()) {
                    log.error("Throwable caught during running communicator", t);
                    throw t;
                }
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
     * @param body to be checked if it is compatible
     * @return {@code true} if compatible, otherwise {@code false}
     */
    public abstract boolean isCompatible(final Body body);

    /**
     * Send {@link Body} to the outbox queue
     *
     * @param body {@link Body} that shall be sent
     */
    public final void send(final Body body) {
        this.outboxQueue.send(Objects.requireNonNull(body));
        System.out.println("PITSCHR (" + System.currentTimeMillis() + "): sentbyknxclient: " + sentByKnxClient.incrementAndGet() + ", time: " + System.currentTimeMillis());
        log.debug("Body added to outbox queue: {}", body);
    }

    public static final AtomicInteger sentByKnxClient = new AtomicInteger(0);

    /**
     * Send {@link RequestBody} packet <strong>asynchronously</strong> to the appropriate channel.
     * It returns a {@link Future} for further processing.
     *
     * @param requestBody request body to be sent
     * @param msTimeout   timeout in milliseconds waiting until the expected response body is fetched
     * @param <T>         an instance of {@link ResponseBody
     * @return a {@link CompletableFuture} representing pending completion of the task containing
     * either an instance of {@link ResponseBody}, or {@code null} if no response was received
     */
    public final <T extends ResponseBody> CompletableFuture<T> send(final RequestBody requestBody,
                                                                    final long msTimeout) {
        return send(requestBody, null, msTimeout);
    }

    /**
     * Send {@link RequestBody} packet <strong>asynchronously</strong> to the appropriate channel.
     * It returns a {@link Future} for further processing.
     *
     * @param requestBody request body to be sent
     * @param predicate   predicates if the condition of {@link KnxEvent} was meet ({@code true}) or not ({@code false}),
     *                    {@code null} means that no predicate check
     * @param msTimeout   timeout in milliseconds waiting until the expected response body is fetched
     * @param <T>         an instance of {@link ResponseBody}
     * @return a {@link CompletableFuture} representing pending completion of the task containing
     * either an instance of {@link ResponseBody}, or {@code null} if no response was received
     */
    public final <T extends ResponseBody> CompletableFuture<T> send(final RequestBody requestBody,
                                                                    final @Nullable Predicate<T> predicate,
                                                                    final long msTimeout) {
        return CompletableFuture
                .supplyAsync(() -> sendAndWaitInternal(requestBody, predicate, msTimeout), this.communicationExecutor)
                .exceptionally(throwable -> {
                    this.client.notifyError(throwable);
                    return null;
                });
    }

    /**
     * Sends the {@link RequestBody} packet to the appropriate channel and then finally wait for the expected response
     * that meets the {@link Predicate} criteria and is an an instance of {@link ResponseBody}.
     *
     * @param requestBody request body to be sent
     * @param predicate   predicates if the condition of {@link KnxEvent} was meet ({@code true}) or not ({@code false}),
     *                    {@code null} means that no predicate check
     * @param msTimeout   timeout in milliseconds waiting until expected response body is fetched
     * @param <T>         an instance of {@link ResponseBody
     * @return an instance of {@link ResponseBody}, or {@code null} if no response was received (timeout) or no response that meets the preconditions was received
     */
    @Nullable
    private final <T extends ResponseBody> T sendAndWaitInternal(final RequestBody requestBody,
                                                                 final @Nullable Predicate<T> predicate,
                                                                 final long msTimeout) {
        final var eventPool = this.client.getEventPool();

        // override the last knx event with the most recent request body
        // the response will be cleared to ensure the response is not linked with the request anymore
        final var lastKnxEvent = eventPool.get(requestBody);
        lastKnxEvent.setRequest(requestBody);
        lastKnxEvent.clearResponse();
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
        System.out.println("PITSCHR (" + System.currentTimeMillis() + "): " + getClass() + " close() invoked.");
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
