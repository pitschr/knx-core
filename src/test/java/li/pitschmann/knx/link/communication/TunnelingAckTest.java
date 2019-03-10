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
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.test.KnxBody;
import li.pitschmann.test.KnxMockServer;
import li.pitschmann.test.KnxTest;
import li.pitschmann.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;

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
    @KnxTest({
            // On first packet send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody)
            "WAIT=CONNECT_REQUEST",
            // send ConnectResponseBody with channel id = 7
            KnxBody.CONNECT_RESPONSE,
            // wait for next packet (will be: ConnectionStateRequestBody)
            "WAIT=CONNECTION_STATE_REQUEST",
            // send ConnectionStateResponseBody
            KnxBody.CONNECTION_STATE_RESPONSE,
            // wait for next packet (will be: TunnelingRequestBody)
            "WAIT=TUNNELING_REQUEST",
            // send TunnelingAckBody
            KnxBody.TUNNELING_ACK,
            // wait for next packet (will be: DisconnectRequestBody)
            "WAIT=DISCONNECT_REQUEST",
            // and then DisconnectResponseBody
            KnxBody.DISCONNECT_RESPONSE
    })
    @DisplayName("Send a Tunneling Request packet and receive Tunneling Ack packet")
    public void testReceivingAckOnceTime(final KnxMockServer mockServer) {
        try (final var client = mockServer.newKnxClient()) {
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

        mockServer.waitForCompletion();

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                TunnelingRequestBody.class, // #4
                DisconnectRequestBody.class // #5
        );
    }

    /**
     * Perform a communication between {@link KnxClient} and the KNX Net/IP device
     * with a tunneling request packet and mock server re-sent the same tunneling
     * request packet which should be ignored by the KNX Net/IP client.
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody)
            "WAIT=CONNECT_REQUEST",
            // send ConnectResponseBody with channel id = 7
            KnxBody.CONNECT_RESPONSE,
            // wait for next packet (will be: ConnectionStateRequestBody)
            "WAIT=CONNECTION_STATE_REQUEST",
            // send ConnectionStateResponseBody
            KnxBody.CONNECTION_STATE_RESPONSE,
            // wait for next packet (will be: TunnelingRequestBody)
            "WAIT=TUNNELING_REQUEST",
            // send TunnelingAckBody
            KnxBody.TUNNELING_ACK,
            // re-send TunnelingAckBody
            KnxBody.TUNNELING_ACK,
            // and then DisconnectRequestBody
            KnxBody.DISCONNECT_REQUEST,
            // Wait for last response from client and quit mock server gracefully
            "WAIT=NEXT"
    })
    @DisplayName("Receiving Tunneling Ack packet twice times")
    public void testReceivingAckTwiceTimes(final KnxMockServer mockServer) {
        try (final var client = mockServer.newKnxClient()) {
            // wait bit to avoid racing condition between ConnectionStateRequest and TunnelingRequest
            // In real world, it doesn't matter but makes the verification (see below) stable
            Sleeper.milliseconds(100);
            // send tunneling request
            final var ackBody = client.send(KnxBody.TUNNELING_REQUEST_BODY, 1000).get();
            assertThat(ackBody).isInstanceOf(TunnelingAckBody.class);

            // wait until Disconnect response arrived KNX mock server
            mockServer.waitForCompletion();
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                TunnelingRequestBody.class, // #4
                DisconnectResponseBody.class // #5
        );
    }
}
