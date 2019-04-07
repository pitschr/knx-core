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
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.server.MockServer;
import li.pitschmann.knx.server.MockServerTest;
import li.pitschmann.test.KnxBody;
import li.pitschmann.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Test for receiving {@link TunnelingAckBody}
 *
 * @author PITSCHR
 */
public class TunnelingAckTest {
    /**
     * Perform a communication between {@link KnxClient} and the KNX Net/IP device
     * with a tunneling request packet
     */
    @MockServerTest
    @DisplayName("Send a Tunneling Request packet and receive Tunneling Ack packet")
    public void testReceivingAckOnceTime(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            // wait bit to avoid racing condition between ConnectionStateRequest and TunnelingRequest
            // In real world, it doesn't matter but makes the verification (see below) stable
            Sleeper.milliseconds(100);
            // send tunneling request
            final var ackBody = client.send(KnxBody.TUNNELING_REQUEST_BODY, 1000).get();
            assertThat(ackBody).isNotNull();

            mockServer.waitForReceivedServiceType(ServiceType.TUNNELING_REQUEST);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // wait until mock server is done
        mockServer.waitDone();

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                TunnelingRequestBody.class, // #4
                DisconnectRequestBody.class // #5
        );
    }
}
