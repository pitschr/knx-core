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

package li.pitschmann.knx.link.communication;

import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Task to send connection frames from local to remote router. This class will send {@link ConnectRequestBody} to the
 * KNX Net/IP router and waits for {@link ConnectResponseBody} to obtain the channel id which is used as identifier for
 * further communications.
 *
 * @author PITSCHR
 */
public final class ConnectionStateMonitor implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionStateMonitor.class);
    /**
     * 500ms additional time buffer for response to minimize chance for an unnecessary loop
     */
    private static final int CONNECTIONSTATE_REQUEST_SLEEP_TIME = 500;
    /**
     * 100ms time for looping in case we are waiting for response (request time > response) time for faster checking
     */
    private static final int WAITING_FOR_RESPONSE_CHECK_INTERVAL = 100;
    private final InternalKnxClient client;

    public ConnectionStateMonitor(final InternalKnxClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        LOG.trace("*** START ***");

        // send first packet
        this.sendConnectionStateRequest();

        while (!this.client.isClosed()) {
            final Instant lastRequestTime = this.getLastRequestTime();
            final Instant lastResponseTime = this.getLastResponseTime();

            if (lastRequestTime.isAfter(lastResponseTime)) {
                // request time > response time -> we are waiting for response
                final Instant now = Instant.now();
                final long offsetLastRequest = Duration.between(lastRequestTime, now).toMillis();
                final long offsetLastResponse = Duration.between(lastResponseTime, now).toMillis();

                // duration between last response and now is bigger than connection alive -> disconnect
                if (offsetLastResponse > this.client.getConfig().getTimeoutAliveConnection()) {
                    LOG.error("Could not get connection state response since {} ms. Disconnection will be initiated. Last heartbeat was received at {}.",
                            offsetLastResponse, DateTimeFormatter.ISO_INSTANT.format(lastResponseTime));
                    this.client.close();
                    break;
                }
                // is last request offset bigger than connection state request timeout?
                else if (offsetLastRequest > this.client.getConfig().getTimeoutConnectionStateRequest()) {
                    LOG.warn("Connection State Request to be sent again, last heartbeat was received at {}: {} ms.",
                            DateTimeFormatter.ISO_INSTANT.format(lastResponseTime), offsetLastResponse);
                    // re-send request
                    this.sendConnectionStateRequest();
                }
                // offset is small to wait more...
                else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("We are waiting for response (request: {}, response: {}): {} ms.",
                                DateTimeFormatter.ISO_INSTANT.format(lastRequestTime),
                                DateTimeFormatter.ISO_INSTANT.format(lastResponseTime), offsetLastResponse);
                    }
                    Sleeper.milliseconds(WAITING_FOR_RESPONSE_CHECK_INTERVAL);
                }
            } else {
                // request time < response time -> we already got response
                long sleepTimeInMillis = this.client.getConfig().getIntervalConnectionState() - Duration.between(lastRequestTime, lastResponseTime).toMillis();
                LOG.debug("Next connection state check will be done in {} ms.", sleepTimeInMillis);
                if (Sleeper.milliseconds(sleepTimeInMillis)) {
                    this.sendConnectionStateRequest();
                }
            }
        }

        LOG.trace("*** END *** (Client closed: {})", this.client.isClosed());
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
        final Instant lastResponseTime = this.client.getEventPool().connectionStateEvent().getResponseTime();
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
        LOG.trace("Send connection state request now.");

        // create body
        final ConnectionStateRequestBody requestBody = ConnectionStateRequestBody.create(this.client.getChannelId(), this.client.getControlHPAI());

        // send
        this.client.getEventPool().add(requestBody);
        this.client.send(requestBody);

        // wait bit
        Sleeper.milliseconds(CONNECTIONSTATE_REQUEST_SLEEP_TIME);
    }
}
