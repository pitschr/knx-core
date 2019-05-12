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
import li.pitschmann.knx.link.communication.KnxEventData;
import li.pitschmann.knx.link.communication.queue.KnxInboxQueue;
import li.pitschmann.knx.link.communication.queue.KnxOutboxQueue;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Executors;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.nio.channels.SelectableChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract channel communicator. It coordinates the communication for given channel
 * ( created by {@link #newChannel()} method) and creates an inbox and an outbox queue
 * for non-blocking communication.
 * <p/>
 * It also controls the lifecycle of channel (opening, closing) and all receiving
 * KNX packets are forwarded to all subscribers.
 *
 * @author PITSCHR
 */
public abstract class AbstractChannelCommunicator extends SubmissionPublisher<Body> implements Runnable {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final String id;
    private final InternalKnxClient internalClient;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final ExecutorService queueExecutor;
    private final ExecutorService communicationExecutor;

    private final SelectableChannel channel;
    private final KnxInboxQueue inboxQueue;
    private final KnxOutboxQueue outboxQueue;

    protected AbstractChannelCommunicator(final String id, final InternalKnxClient client) {
        this.id = "Communicator[" + id + "]";
        this.internalClient = client;

        this.channel = newChannel();
        log.info("{}: Channel registered: {} (open: {}, registered: {}, blocking: {})", id, channel, channel.isOpen(), channel.isRegistered(), channel.isBlocking());

        // creates inbox and outbox queues
        this.inboxQueue = new KnxInboxQueue(id, internalClient, channel);
        this.outboxQueue = new KnxOutboxQueue(id, internalClient, channel);
        log.trace("{}: Inbox and Outbox Queues created.", id);

        // creates queue executor
        this.queueExecutor = Executors.newFixedThreadPool(2, true);
        this.queueExecutor.submit(inboxQueue);
        this.queueExecutor.submit(outboxQueue);
        this.queueExecutor.shutdown();
        log.info("{}: Queue Executor created: {}", id, this.queueExecutor);

        // creates executor for communication
        this.communicationExecutor = Executors.newFixedThreadPool(internalClient.getConfig().getCommunicationExecutorPoolSize(), true);
        log.info("{}: Communication Executor created with size of {}: {}", id, internalClient.getConfig().getCommunicationExecutorPoolSize(), this.communicationExecutor);
    }

    @Nonnull
    protected abstract SelectableChannel newChannel();

    @Nonnull
    public InternalKnxClient getInternalClient() {
        return internalClient;
    }

    @Nonnull
    public SelectableChannel getChannel() {
        return channel;
    }

    @Override
    public void run() {
        log.trace("*** {}: START ***", id);

        while (!Thread.interrupted()) {
            try {
                log.debug("{}: Waiting for next packet from channel", id);
                final var body = this.inboxQueue.next();
                // accepted body
                if (this.isCompatible(body)) {
                    log.debug("{}: Body from channel to be sent to subscribers: {}", id, body);
                    this.submit(body);
                }
                // not accepted body
                else {
                    log.warn("{}: Body is not expected for this channel and therefore ignored: {}", id, body);
                }
            } catch (final InterruptedException ex) {
                log.debug("{}: Channel receiver is cancelled.", id);
                Thread.currentThread().interrupt();
            }
        }

        log.trace("*** {}: END ***", id);
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
    protected abstract boolean isCompatible(final Body body);

    /**
     * Send {@link Body} to the outbox queue
     *
     * @param body
     */
    public final void send(final @Nonnull Body body) {
        this.outboxQueue.send(body);
        log.debug("{}: Body added to outbox queue: {}", id, body);
    }

    /**
     * Send {@link RequestBody} packet <strong>asynchronously</strong> to the appropriate channel. It returns a {@link Future} for further processing.
     *
     * @param requestBody
     * @param msTimeout   timeout in milliseconds waiting until the expected response body is fetched
     * @return a {@link CompletableFuture} representing pending completion of the task containing either an instance of {@link ResponseBody},
     * or {@code null} if no response was received because of e.g. timeout
     */
    public final <T extends ResponseBody> CompletableFuture<T> send(final @Nonnull RequestBody requestBody, final long msTimeout) {
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
    private final <T extends ResponseBody> T sendAndWaitInternal(final RequestBody requestBody, final long msTimeout) {
        final var eventPool = this.internalClient.getEventPool();

        // add request body to event pool
        eventPool.add(requestBody);
        log.trace("{}: Request Body added to event pool.", id);

        // mark as dirty
        if (requestBody instanceof TunnelingRequestBody) {
            this.internalClient.getStatusPool().setDirty(((TunnelingRequestBody) requestBody).getCEMI().getDestinationAddress());
        }

        var attempts = 1;
        final var totalAttempts = 3; // hard-coded (up to 3 times will be retried in case of no response)
        final var eventWaiting = this.internalClient.getConfig().getIntervalEvent();
        T responseBody;

        do {
            // send packet
            send(requestBody);

            // iterate for event response
            final var start = System.currentTimeMillis();
            KnxEventData<RequestBody, T> event;
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
                log.warn("{}: No response received yet for request ({}/{}): {}", id, attempts, totalAttempts, requestBody);
            } else {
                log.debug("{}: Response received for request ({}/{}): {}, Response: {}", id, attempts, totalAttempts, requestBody, responseBody);
            }

            // if no response and not interrupted try to repeat this step up to 'totalAttempts'
        } while (responseBody == null && attempts++ < totalAttempts && !Thread.currentThread().isInterrupted());

        return responseBody;
    }

    /**
     * Closes the channel and executor services.
     */
    @Override
    public void close() {
        log.trace("{}: Method 'close()' invoked.", id);
        if (!closed.getAndSet(true)) {
            super.close();

            // close channel and executors
            Closeables.closeQuietly(this.channel);
            Closeables.shutdownQuietly(this.queueExecutor);
            Closeables.shutdownQuietly(this.communicationExecutor);
            log.debug("{}: Method 'close()' called.", id);
        }
    }
}
