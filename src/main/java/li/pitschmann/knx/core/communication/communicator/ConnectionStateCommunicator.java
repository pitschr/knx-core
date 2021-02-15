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

import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ConnectResponseBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.communication.InternalKnxClient;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Task to send connection frames from local to KNX Net/IP device. This class will
 * send {@link ConnectRequestBody} to the KNX Net/IP device and waits for
 * {@link ConnectResponseBody} to obtain the channel id which is used as identifier for
 * further communications.
 * <p>
 * The connection state communicator is used in Tunneling mode only!
 *
 * @author PITSCHR
 */
public final class ConnectionStateCommunicator implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ConnectionStateCommunicator.class);
    /**
     * 500ms additional time buffer for response to minimize chance for an unnecessary loop
     */
    private static final int CONNECTIONSTATE_REQUEST_SLEEP_TIME = 500;
    /**
     * 100ms time for looping in case we are waiting for response (request time > response) time for faster checking
     */
    private static final int WAITING_FOR_RESPONSE_CHECK_INTERVAL = 100;
    private final InternalKnxClient client;

    /**
     * KNX Connection State Monitor (package protected)
     */
    ConnectionStateCommunicator(final InternalKnxClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public void run() {
        log.trace("*** START ***");

        // send first packet
        this.sendConnectionStateRequest();

        while (this.client.getState() == InternalKnxClient.State.STARTED
                || this.client.getState() == InternalKnxClient.State.START_REQUEST) {
            final var lastRequestTime = this.getLastRequestTime();
            final var lastResponseTime = this.getLastResponseTime();

            if (lastRequestTime.isAfter(lastResponseTime)) {
                // request time > response time -> we are waiting for response
                final var now = Instant.now();
                final var offsetLastRequest = Duration.between(lastRequestTime, now).toMillis();
                final var offsetLastResponse = Duration.between(lastResponseTime, now).toMillis();

                // duration between last response and now is bigger than heartbeat -> disconnect
                if (offsetLastResponse > this.client.getConfig(CoreConfigs.ConnectionState.HEARTBEAT_TIMEOUT)) {
                    log.error("Could not get connection state response since {} ms. Disconnection will be initiated. Last heartbeat was received at {}.",
                            offsetLastResponse, DateTimeFormatter.ISO_INSTANT.format(lastResponseTime));
                    this.client.close();
                    break;
                }
                // is last request offset bigger than connection state request timeout?
                else if (offsetLastRequest > this.client.getConfig(CoreConfigs.ConnectionState.REQUEST_TIMEOUT)) {
                    log.warn("Connection State Request to be sent again, last heartbeat was received at {}: {} ms.",
                            DateTimeFormatter.ISO_INSTANT.format(lastResponseTime), offsetLastResponse);
                    // re-send request
                    this.sendConnectionStateRequest();
                }
                // offset is small to wait more...
                else {
                    if (log.isDebugEnabled()) {
                        log.debug("We are waiting for response (request: {}, response: {}): {} ms.",
                                DateTimeFormatter.ISO_INSTANT.format(lastRequestTime),
                                DateTimeFormatter.ISO_INSTANT.format(lastResponseTime), offsetLastResponse);
                    }
                    Sleeper.milliseconds(WAITING_FOR_RESPONSE_CHECK_INTERVAL);
                }
            } else {
                // request time < response time -> we already got response
                final var sleepTimeInMillis = this.client.getConfig(CoreConfigs.ConnectionState.HEARTBEAT_INTERVAL) - Duration.between(lastRequestTime, lastResponseTime).toMillis();
                log.debug("Next connection state check will be done in {} ms.", sleepTimeInMillis);
                if (Sleeper.milliseconds(sleepTimeInMillis)) {
                    this.sendConnectionStateRequest();
                }
            }
        }

        log.trace("*** END *** (Client state: {})", this.client.getState());
    }

    /**
     * Returns the last request time of connection state response.
     *
     * @return last request time
     */
    private Instant getLastRequestTime() {
        return this.client.getEventPool().connectionStateEvent().getRequestTime();
    }

    /**
     * Returns the last response time of connection state response. If no connection state response was received yet,
     * the response time from connect response is taken.
     *
     * @return last response time
     */
    private Instant getLastResponseTime() {
        final var lastResponseTime = this.client.getEventPool().connectionStateEvent().getResponseTime();
        if (lastResponseTime == null) {
            return this.client.getEventPool().connectEvent().getResponseTime();
        } else {
            return lastResponseTime;
        }
    }

    /**
     * Sends out the {@link ConnectionStateRequestBody} packet.
     */
    private void sendConnectionStateRequest() {
        log.trace("Send connection state request now.");

        // create body
        final var requestBody = ConnectionStateRequestBody.of(this.client.getChannelId(), this.client.getControlHPAI());

        // send and register set the last request body
        // the response won't be cleared because we need to keep the last time of response
        this.client.getEventPool().connectionStateEvent().setRequest(requestBody);
        this.client.send(requestBody);

        // wait bit
        Sleeper.milliseconds(CONNECTIONSTATE_REQUEST_SLEEP_TIME);
    }
}
