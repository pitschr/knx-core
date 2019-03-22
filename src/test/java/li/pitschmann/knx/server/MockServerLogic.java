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

package li.pitschmann.knx.server;

import com.google.common.collect.Lists;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;
import li.pitschmann.knx.link.exceptions.KnxUnknownBodyException;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.server.strategy.IgnoreStrategy;
import li.pitschmann.knx.server.strategy.ResponseStrategy;
import li.pitschmann.knx.server.strategy.impl.DefaultDisconnectStrategy;
import li.pitschmann.knx.server.trigger.TriggerRule;
import li.pitschmann.knx.server.trigger.WaitDelayTriggerRule;
import li.pitschmann.knx.server.trigger.WaitRequestTriggerRule;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Logic thread for KNX mock server (package-protected)
 */
public class MockServerLogic implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MockServerLogic.class);
    private final Map<ServiceType, List<ResponseStrategy>> responseStrategies = new HashMap<>();
    private final Map<ServiceType, AtomicInteger> serviceTypeCounter = new HashMap<>();
    private final MockServer mockServer;

    private final List<TriggerRule> disconnectTriggerTasks = Lists.newLinkedList();
    private final MockServerTunnelingLogic tunnelingLogicRunnable;
    private boolean tunnelingTriggered;

    /**
     * Package-protected constructor for logic thread
     *
     * @param mockServer
     */
    MockServerLogic(final MockServer mockServer, final MockServerTest mockServerTest) {
        this.mockServer = mockServer;

        var tunnelingTrigger = mockServerTest.tunnelingTrigger();

        // Logic for Disconnect Trigger
        // --------------------------------
        if (mockServerTest.disconnectTrigger().length > 0) {
            // hardcoded for now, to make it more dynamic the command must be parsed
            // no reason to do it for now!
            for (String cmd : mockServerTest.disconnectTrigger()) {
                // special command 'tunnelingTrigger' to fire the disconnect
                // as soon the tunnelling trigger is finished.
                if ("after-tunnelingTrigger".equals(cmd)) {
                    if (tunnelingTrigger.length > 0) {
                        tunnelingTrigger = new String[mockServerTest.tunnelingTrigger().length + 1];
                        System.arraycopy(mockServerTest.tunnelingTrigger(), 0, tunnelingTrigger, 0, mockServerTest.tunnelingTrigger().length);
                        tunnelingTrigger[mockServerTest.tunnelingTrigger().length] = "$DISCONNECT$";
                    } else {
                        tunnelingTrigger = new String[]{"$DISCONNECT$"};
                    }
                }
                // if we should wait for a specific request
                // Syntax: "wait-request(N)=<ServiceType Name>"
                else if ("wait-request(1)=CONNECTION_STATE_REQUEST".equals(cmd)) {
                    disconnectTriggerTasks.add(new WaitRequestTriggerRule(this, ServiceType.CONNECTION_STATE_REQUEST, 1));
                }
                // if we should add a delay (in milliseconds)
                // Syntax: "wait(N)"
                else if ("wait(1000)".equals(cmd)) {
                    disconnectTriggerTasks.add(new WaitDelayTriggerRule(1000));
                }
                // otherwise we have received an unsupported trigger
                else {
                    logger.error("Unknown disconnect trigger received: {}", cmd);
                    throw new UnsupportedOperationException("Trigger is not supported: " + cmd);
                }
            }
        }

        // Logic for Tunneling Trigger
        // --------------------------------
        if (tunnelingTrigger.length > 0) {
            // we have some triggers, define the runnable
            tunnelingLogicRunnable = new MockServerTunnelingLogic(this.mockServer, tunnelingTrigger);
        } else {
            // no trigger
            tunnelingLogicRunnable = null;
        }

        // registering response strategy
        registerResponseStrategies(ServiceType.DESCRIPTION_REQUEST, mockServerTest.descriptionStrategy());
        registerResponseStrategies(ServiceType.CONNECT_REQUEST, mockServerTest.connectStrategy());
        registerResponseStrategies(ServiceType.CONNECTION_STATE_REQUEST, mockServerTest.connectionStateStrategy());
        registerResponseStrategies(ServiceType.DISCONNECT_REQUEST, mockServerTest.disconnectStrategy());
        registerResponseStrategies(ServiceType.TUNNELING_REQUEST, mockServerTest.tunnelingStrategy());
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
            for (Class<? extends ResponseStrategy> strategyClass : strategyClasses) {
                final ResponseStrategy strategyInstance = strategyClass.getDeclaredConstructor().newInstance();
                responseStrategies.get(serviceType).add(strategyInstance);
            }
        } catch (final Exception e) {
            logger.error("Exception happened during creating strategy instance", e);
        }
    }

    @Override
    public void run() {
        logger.info("*** KNX Mock Server [logic] START ***");

        final var triggerExecutorServices = Executors.newFixedThreadPool(2, true);

        try {
            while (!Thread.currentThread().isInterrupted()) {
                final var body = this.mockServer.getInbox().take();

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
                        // if the request body was a connect state request then tunnelling trigger may start (if defined)
                        else if (requestBody instanceof ConnectionStateRequestBody && this.tunnelingLogicRunnable != null && !tunnelingTriggered) {
                            tunnelingTriggered = true;
                            triggerExecutorServices.execute(this.tunnelingLogicRunnable);
                        }
                    }
                } else if (body instanceof ResponseBody) {
                    logger.debug("Response body received. Do nothing. Body: {}", body);
                } else {
                    logger.warn("Unknown body received. Do nothing. Body: {}", body);
                    throw new KnxUnknownBodyException(body.getRawData());
                }

            }
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (final Throwable t) {
            logger.error("Throwable caught", t);
            Thread.currentThread().interrupt();
        } finally {
            Closeables.shutdownQuietly(triggerExecutorServices);
            logger.info("*** KNX Mock Server [logic] END ***");
        }
    }

    /**
     * Returns the occurrence of {@link ServiceType}
     *
     * @param serviceType
     * @return occurrence of {@link ServiceType} that were passed through logic
     */
    public int getServiceTypeOccurrence(final ServiceType serviceType) {
        return this.serviceTypeCounter.get(serviceType).get();
    }

    /**
     * Returns if the disconnect should be initiated from KNX mock server.
     * <p/>
     * Using {@link MockServerTest#disconnectTrigger()} we can define several triggers
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
        if (!disconnectTriggerTasks.isEmpty()) {
            final var triggerTask = disconnectTriggerTasks.get(0);
            if (triggerTask.apply()) {
                disconnectTriggerTasks.remove(triggerTask);
                if (disconnectTriggerTasks.isEmpty()) {
                    return true;
                }
                return shouldDisconnectTrigger();
            }
        }
        return false;
    }
}
