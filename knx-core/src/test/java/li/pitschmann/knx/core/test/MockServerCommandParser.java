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

package li.pitschmann.knx.core.test;

import li.pitschmann.knx.core.cemi.CEMI;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.test.action.MockAction;
import li.pitschmann.knx.core.test.action.RequestMockAction;
import li.pitschmann.knx.core.test.action.WaitDelayMockAction;
import li.pitschmann.knx.core.test.action.WaitServiceTypeMockAction;
import li.pitschmann.knx.core.test.strategy.impl.DefaultDisconnectStrategy;
import li.pitschmann.knx.core.test.strategy.impl.DefaultTunnelingStrategy;
import li.pitschmann.knx.core.utils.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Parses the given commands for KNX Mock Server
 */
public class MockServerCommandParser {
    public static final String DISCONNECT_REQUEST_COMMAND = "$DISCONNECT$";
    private static final Logger log = LoggerFactory.getLogger(MockServerCommandParser.class);
    private final MockServer mockServer;
    private final MockServerCommunicator mockServerCommunicator;

    MockServerCommandParser(final MockServer mockServer, final MockServerCommunicator mockServerCommunicator) {
        this.mockServer = Objects.requireNonNull(mockServer);
        this.mockServerCommunicator = Objects.requireNonNull(mockServerCommunicator);
    }

    /**
     * The commands are hardcoded for now, no reason to do it for now!
     *
     * @param command the command to be parsed
     * @return list of {@link MockAction}
     */
    public List<MockAction> parse(final String command) {
        // One disconnect request
        if (DISCONNECT_REQUEST_COMMAND.equals(command)) {
            final var mockRequest = new DefaultDisconnectStrategy().createRequest(this.mockServer, null);
            return Collections.singletonList(new RequestMockAction(this.mockServer, mockRequest.getBody()));
        }
        // One normal tunnelling request with CEMI bytes
        // (used in TunnelingRequestTest)
        else if ("cemi(1)={2900bce010c84c0f0300800c23}".equals(command)) {
            final var mockRequest = new DefaultTunnelingStrategy().createRequest(this.mockServer, CEMI.of(Bytes.toByteArray("2900bce010c84c0f0300800c23")));
            return Collections.singletonList(new RequestMockAction(this.mockServer, mockRequest.getBody()));
        }
        // 100-times tunnelling request with CEMI bytes
        // (used in PerformanceKnxTest)
        else if ("cemi(260)={2E00BCE010FF0A96010081}".equals(command)) {
            final var actions = new ArrayList<MockAction>(260);
            for (int i = 0; i < 260; i++) {
                final var mockRequest = new DefaultTunnelingStrategy().createRequest(this.mockServer, CEMI.of(Bytes.toByteArray("2E00BCE010FF0A96010081")));
                actions.add(new RequestMockAction(this.mockServer, mockRequest.getBody()));
            }
            return actions;
        }
        // One a tunneling request with CEMI bytes, but routing to
        // wrong control channel (used in TunnelingRequestTest)
        else if ("channel=control,cemi(1)={2900bce010c84c0f0300800c23}".equals(command)) {
            final var mockRequest = new DefaultTunnelingStrategy().createRequest(this.mockServer, CEMI.of(Bytes.toByteArray("2900bce010c84c0f0300800c23")));
            return Collections.singletonList(new RequestMockAction(this.mockServer, new ControlMockBody(mockRequest.getBody().getRawData(true))));
        }
        // Two corrupted bodies (used in KnxClientTest)
        else if ("raw(2)={0610020600140000000100000000000004000000}".equals(command)) {
            final var mockRequest = new MockResponse(Bytes.toByteArray("0610020600140000000100000000000004000000"));
            return List.of(
                    new RequestMockAction(this.mockServer, mockRequest.getBody()), // 1
                    new RequestMockAction(this.mockServer, mockRequest.getBody()) // 2
            );
        }
        // If we should wait for a specific request
        // Syntax: "wait-request(N)=<ServiceType Name>"
        else if ("wait-request(1)=CONNECTION_STATE_REQUEST".equals(command)) {
            return Collections.singletonList(new WaitServiceTypeMockAction(mockServerCommunicator.getServiceTypeCounter(ServiceType.CONNECTION_STATE_REQUEST), 1));
        }
        // If we should add a delay (in milliseconds)
        // Syntax: "wait(N)"
        else if ("wait(1000)".equals(command)) {
            return Collections.singletonList(new WaitDelayMockAction(1000));
        }
        // otherwise we have received an unsupported trigger command
        else {
            log.error("Unknown disconnect trigger command received: {}", command);
            throw new UnsupportedOperationException("Trigger is not supported: " + command);
        }
    }
}
