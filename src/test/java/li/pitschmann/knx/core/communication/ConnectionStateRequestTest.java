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

package li.pitschmann.knx.core.communication;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ConnectResponseBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.MockServerTest;
import li.pitschmann.knx.core.test.strategy.IgnoreStrategy;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Test for sending {@link ConnectRequestBody} / receiving {@link ConnectResponseBody} via {@link KnxClient}
 *
 * @author PITSCHR
 */
public class ConnectionStateRequestTest {
    /**
     * Test successful connection state communication with KNX Net/IP device
     */
    @MockServerTest
    @DisplayName("Successful Connection State Request communication")
    public void testSuccessful(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            assertThat(client.isRunning());
            // after 2-nd connection state request sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTION_STATE_REQUEST, 2);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // wait until done
        mockServer.waitDone();

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                ConnectionStateRequestBody.class, // #4
                DisconnectRequestBody.class // #6
        );
    }

    /**
     * Test no response of ConnectionStateResponseBody from KNX Net/IP device
     */
    @MockServerTest(connectionStateStrategy = IgnoreStrategy.class)
    @DisplayName("Error: No Connection State Request received")
    public void testFailureNoResponse(final MockServer mockServer) {
        final var client = mockServer.createTestClient();
        try (client) {
            assertThat(client.isRunning()).isTrue();
            mockServer.waitDone();
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // asserts that client is not running anymore
        assertThat(client.isRunning()).isFalse();

        // assert packets (it may happen that 6 or 7 ConnectionStateRequestBody
        // are received by mock - depending how busy the machine is under test)
        try {
            mockServer.assertReceivedPackets(generateExpectedReceivedBodies(6));
        } catch (final AssertionError error) {
            mockServer.assertReceivedPackets(generateExpectedReceivedBodies(7));
        }
    }

    /**
     * Generate list of expected received bodies for assertion
     *
     * @param expectedNumber expected number of connection state request bodies to be generated
     * @return list of body classes
     */
    private List<Class<? extends Body>> generateExpectedReceivedBodies(final int expectedNumber) {
        final var list = new ArrayList<Class<? extends Body>>(expectedNumber + 3);
        list.add(DescriptionRequestBody.class); // first request
        list.add(ConnectRequestBody.class); // second request
        for (var i = 0; i < expectedNumber; i++) {
            list.add(ConnectionStateRequestBody.class);
        }
        list.add(DisconnectRequestBody.class); // last request
        return list;
    }
}
