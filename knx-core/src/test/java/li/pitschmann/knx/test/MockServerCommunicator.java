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

package li.pitschmann.knx.test;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.test.action.MockAction;
import li.pitschmann.knx.test.strategy.IgnoreStrategy;
import li.pitschmann.knx.test.strategy.ResponseStrategy;
import li.pitschmann.knx.test.strategy.impl.DefaultDisconnectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Logic for KNX mock server (package-protected)
 */
public class MockServerCommunicator implements Flow.Subscriber<Body> {
    private static final Logger logger = LoggerFactory.getLogger(MockServerCommunicator.class);
    private final Map<ServiceType, List<ResponseStrategy>> responseStrategies = new HashMap<>();
    private final Map<ServiceType, AtomicInteger> serviceTypeCounter = new HashMap<>();
    private final MockServer mockServer;

    private final List<MockAction> disconnectTriggers;
    private final MockServerCommandParser commandParser;
    private final List<String> requests;

    private boolean requestRunnableStarted;


    /**
     * Package-protected constructor for logic thread
     *
     * @param mockServer
     */
    MockServerCommunicator(final MockServer mockServer, final MockServerTest testAnnotation) {
        Preconditions.checkNotNull(testAnnotation);
        this.mockServer = Objects.requireNonNull(mockServer);
        this.commandParser = new MockServerCommandParser(mockServer, this);

        // registering response strategy
        registerResponseStrategies(ServiceType.SEARCH_REQUEST, testAnnotation.discoveryStrategy());
        registerResponseStrategies(ServiceType.DESCRIPTION_REQUEST, testAnnotation.descriptionStrategy());
        registerResponseStrategies(ServiceType.CONNECT_REQUEST, testAnnotation.connectStrategy());
        registerResponseStrategies(ServiceType.CONNECTION_STATE_REQUEST, testAnnotation.connectionStateStrategy());
        registerResponseStrategies(ServiceType.DISCONNECT_REQUEST, testAnnotation.disconnectStrategy());
        registerResponseStrategies(ServiceType.TUNNELING_REQUEST, testAnnotation.tunnelingStrategy());

        // Logic for Disconnect Trigger
        // --------------------------------
        var shouldDisconnectAfterTrigger = false;
        if (testAnnotation.disconnectTrigger().length > 0) {
            disconnectTriggers = Lists.newArrayListWithCapacity(testAnnotation.disconnectTrigger().length);
            // hardcoded for now, to make it more dynamic the command must be parsed
            // no reason to do it for now!
            for (final var command : testAnnotation.disconnectTrigger()) {
                // special command 'after-trigger' to fire the disconnect
                // as soon the trigger runnable is finished.
                if ("after-trigger".equals(command)) {
                    shouldDisconnectAfterTrigger = true;
                } else {
                    disconnectTriggers.addAll(commandParser.parse(command));
                }
            }
        } else {
            // no disconnect triggers
            disconnectTriggers = Collections.emptyList();
        }

        // Runnable for Requests
        // --------------------------------
        requests = Lists.newArrayListWithExpectedSize(testAnnotation.requests().length + 1);
        for (final var command : testAnnotation.requests()) {
            requests.add(command);
        }
        // Send disconnect request after trigger if specified in MockServerTest#disconnectTrigger()
        if (shouldDisconnectAfterTrigger) {
            requests.add(MockServerCommandParser.DISCONNECT_REQUEST_COMMAND);
        }
    }

    /**
     * Registers the response strategies
     *
     * @param serviceType
     * @param strategyClasses
     */
    private void registerResponseStrategies(final ServiceType serviceType, final Class<? extends ResponseStrategy>[] strategyClasses) {
        // re-init values for given service types
        responseStrategies.put(serviceType, Lists.newLinkedList());
        serviceTypeCounter.put(serviceType, new AtomicInteger());
        // add strategies
        try {
            for (final var strategyClass : strategyClasses) {
                final var strategyInstance = strategyClass.getDeclaredConstructor().newInstance();
                responseStrategies.get(serviceType).add(strategyInstance);
            }
        } catch (final Exception e) {
            logger.error("Exception happened during creating strategy instance", e);
        }
    }

    @Override
    public void onNext(final Body body) {
        if (body instanceof RequestBody) {
            final var requestBody = (RequestBody) body;
            final var requestServiceType = requestBody.getServiceType();

            // get response strategy
            // - if there are e.g. 2 response strategy (A, B) for same request defined
            //   then they are rotating A, B, A, B, ...
            // - for each body the count will be incremented
            final var serviceTypeCount = serviceTypeCounter.get(requestServiceType).getAndIncrement();
            final var responseStrategiesList = responseStrategies.get(requestServiceType);
            final var index = serviceTypeCount % responseStrategiesList.size();
            final var responseStrategy = responseStrategiesList.get(index);

            if (responseStrategy instanceof IgnoreStrategy) {
                logger.debug("Request being ignored because of IgnoreStrategy: {}", requestBody);
            } else {
                logger.debug("Request processed by '{}': {}", responseStrategy.getClass().getName(), requestBody);
                // get response body
                final var mockResponse = responseStrategy.createResponse(this.mockServer, new MockRequest(requestBody));

                // add to outbox
                this.mockServer.addToOutbox(mockResponse.getBody());

                // checks if the disconnect should be done by KNX mock server
                if (shouldDisconnectTrigger()) {
                    logger.debug("KNX mock server disconnect triggered");
                    this.mockServer.addToOutbox(new DefaultDisconnectStrategy().createRequest(this.mockServer, null).getBody());
                }
                // if the request body was a connect state request then internal runnable services may start
                else if (!requestRunnableStarted && requestBody instanceof ConnectionStateRequestBody) {
                    requestRunnableStarted = true;
                    logger.debug("Start with Mock Actions: {}", requests);
                    CompletableFuture
                            .runAsync(() -> requests.stream().map(commandParser::parse).flatMap(Collection::stream).forEach(MockAction::apply))
                            .thenRun(() -> logger.debug("Mock Actions fully performed."));
                }
            }

        } else if (body instanceof ResponseBody) {
            logger.trace("Response body received. Do nothing. Body: {}", body);
        } else {
            logger.warn("Unknown body received. Do nothing. Body: {}", body);
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        logger.error("Error during KNX Mock Server Logic class", throwable);
        // here we do not any error handling
        // call on complete to close mock server properly
        onComplete();
    }

    @Override
    public void onComplete() {
        this.mockServer.cancel();
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    /**
     * Returns if the disconnect should be initiated from KNX mock server.
     * <p/>
     * Using {@link MockServerTest#disconnectTrigger()} we can define several requests
     * that must be matched before the disconnect request is sent out from KNX mock server.
     * <p/>
     * <u>Example:</u><br>
     * <ol>
     * <li>wait for connection state request</li>
     * <li>wait for tunnelling request</li>
     * <li>wait for connection state request</li>
     * </ol>
     * The disconnect will be initiated by KNX mock server as soon as possible when
     * connection state, tunnelling request and connection state packets arrived.
     * <p/>
     * It doesn't matter if there were other packets arrived or sent in the meantime.
     *
     * @return {@code true} if mock server should send out a disconnect frame to
     * KNX client, otherwise {@code false}
     */
    protected boolean shouldDisconnectTrigger() {
        if (!disconnectTriggers.isEmpty()) {
            final var trigger = disconnectTriggers.get(0);
            if (trigger.apply()) {
                disconnectTriggers.remove(trigger);
                if (disconnectTriggers.isEmpty()) {
                    return true;
                }
                return shouldDisconnectTrigger();
            }
        }
        return false;
    }

    /**
     * Get counter of service type (how many service types was received by the mock server communicator)
     *
     * @param serviceType
     * @return counter of given service type
     */
    protected AtomicInteger getServiceTypeCounter(final ServiceType serviceType) {
        return this.serviceTypeCounter.get(serviceType);
    }
}
