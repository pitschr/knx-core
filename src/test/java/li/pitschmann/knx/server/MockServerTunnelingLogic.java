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
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.server.strategy.impl.DefaultTunnelingStrategy;
import li.pitschmann.knx.server.trigger.DisconnectRequestTriggerRule;
import li.pitschmann.knx.server.trigger.RequestTriggerRule;
import li.pitschmann.knx.server.trigger.TriggerRule;
import li.pitschmann.utils.Bytes;

/**
 * Runnable for requests in tunneling phase
 */
public class MockServerTunnelingLogic implements Runnable {
    private final DefaultTunnelingStrategy tunnelingStrategy = new DefaultTunnelingStrategy();
    private final MockServer mockServer;
    private final String[] triggerCommands;

    public MockServerTunnelingLogic(final MockServer mockServer, final String[] triggers) {
        this.mockServer = mockServer;
        this.triggerCommands = triggers.clone();
    }

    @Override
    public void run() {
        // hardcoded for now, to make it more dynamic the command must be parsed
        // no reason to do it for now!
        final var triggers = Lists.<TriggerRule>newLinkedList();
        for (String cmd : triggerCommands) {
            // Add one normal tunnelling request with CEMI bytes
            // (used in TunnelingRequestTest)
            if ("cemi(1)={2900bce010c84c0f0300800c23}".equals(cmd)) {
                final var mockRequest = tunnelingStrategy.createRequest(this.mockServer, CEMI.valueOf(Bytes.toByteArray("2900bce010c84c0f0300800c23")));
                triggers.add(new RequestTriggerRule(this.mockServer, mockRequest.getBody()));
            }
            // Add tunnelling request with CEMI bytes 100 times
            // (used in PerformanceKnxTest)
            else if ("cemi(260)={2E00BCE010FF0A96010081}".equals(cmd)) {
                for (int i = 0; i < 260; i++) {
                    final var mockRequest = tunnelingStrategy.createRequest(this.mockServer, CEMI.valueOf(Bytes.toByteArray("2E00BCE010FF0A96010081")));
                    triggers.add(new RequestTriggerRule(this.mockServer, mockRequest.getBody()));
                }
            }
            // Adds a tunneling request with CEMI bytes, but routing to
            // wrong control channel (used in TunnelingRequestTest)
            else if ("channel=control,cemi(1)={2900bce010c84c0f0300800c23}".equals(cmd)) {
                final var mockRequest = tunnelingStrategy.createRequest(this.mockServer, CEMI.valueOf(Bytes.toByteArray("2900bce010c84c0f0300800c23")));
                triggers.add(new RequestTriggerRule(this.mockServer, new ControlBytesBody(mockRequest.getBody().getRawData(true))));
            }
            // Adds two corrupted bodies (used in KnxClientTest)
            else if ("raw(2)={0610020600140000000100000000000004000000}".equals(cmd)) {
                final var mockRequest = new MockResponse(Bytes.toByteArray("0610020600140000000100000000000004000000"));
                triggers.add(new RequestTriggerRule(this.mockServer, mockRequest.getBody())); // 1
                triggers.add(new RequestTriggerRule(this.mockServer, mockRequest.getBody())); // 2
            }
            // Add disconnect body if it should be triggered at the end of tunneling requests
            else if (cmd.equals("$DISCONNECT$")) {
                triggers.add(new DisconnectRequestTriggerRule(this.mockServer));
            }
        }

        // Execute
        triggers.stream().forEach(TriggerRule::apply);
    }
}
