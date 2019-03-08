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
import li.pitschmann.knx.link.body.TunnellingAckBody;
import li.pitschmann.knx.link.body.TunnellingRequestBody;
import li.pitschmann.test.KnxBody;
import li.pitschmann.test.KnxMockServer;
import li.pitschmann.test.KnxTest;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Test for receiving {@link TunnellingAckBody}
 *
 * @author PITSCHR
 */
public class TunnellingAckTest {
    /**
     * Perform a communication between {@link KnxClient} and the KNX Net/IP device
     * with a tunnelling request packet
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody)
            "WAIT=NEXT",
            // send ConnectResponseBody with channel id = 7
            KnxBody.CONNECT_RESPONSE,
            // wait for next packet (will be: ConnectionStateRequestBody)
            "WAIT=NEXT",
            // send ConnectionStateResponseBody
            KnxBody.CONNECTION_STATE_RESPONSE,
            // wait for next packet (will be: TunnellingRequestBody)
            "WAIT=NEXT",
            // send TunnellingAckBody
            KnxBody.TUNNELLING_ACK,
            // wait for next packet (will be: DisconnectRequestBody)
            "WAIT=NEXT",
            // and then DisconnectResponseBody
            KnxBody.DISCONNECT_RESPONSE
    })
    @DisplayName("Send a Tunnelling Request packet and receive Tunnelling Ack packet")
    public void testReceivingAckOnceTime(final KnxMockServer mockServer) {
        try (final var client = mockServer.newKnxClient()) {
            // send tunnelling request
            final var ackBody = client.send(KnxBody.TUNNELLING_REQUEST_BODY, 500).get();
            assertThat(ackBody).isNotNull();
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                TunnellingRequestBody.class, // #4
                DisconnectRequestBody.class // #5
        );
    }

    /**
     * Perform a communication between {@link KnxClient} and the KNX Net/IP device
     * with a tunnelling request packet and mock server re-sent the same tunnelling
     * request packet which should be ignored by the KNX Net/IP client.
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody)
            "WAIT=NEXT",
            // send ConnectResponseBody with channel id = 7
            KnxBody.CONNECT_RESPONSE,
            // wait for next packet (will be: ConnectionStateRequestBody)
            "WAIT=NEXT",
            // send ConnectionStateResponseBody
            KnxBody.CONNECTION_STATE_RESPONSE,
            // wait for next packet (will be: TunnellingRequestBody)
            "WAIT=NEXT",
            // send TunnellingAckBody
            KnxBody.TUNNELLING_ACK,
            // re-send TunnellingAckBody
            KnxBody.TUNNELLING_ACK,
            // and then DisconnectRequestBody
            KnxBody.DISCONNECT_REQUEST,
            // Wait for last response from client and quit mock server gracefully
            "WAIT=NEXT"
    })
    @DisplayName("Receiving Tunnelling Ack packet twice times")
    public void testReceivingAckTwiceTimes(final KnxMockServer mockServer) {
        try (final var client = mockServer.newKnxClient()) {
            // send tunnelling request
            final var ackBody = client.send(KnxBody.TUNNELLING_REQUEST_BODY, 500).get();
            assertThat(ackBody).isNotNull();

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
                TunnellingRequestBody.class, // #4
                DisconnectResponseBody.class // #5
        );
    }
}
