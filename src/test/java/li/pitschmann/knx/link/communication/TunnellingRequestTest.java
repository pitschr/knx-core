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
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.TunnellingAckBody;
import li.pitschmann.knx.link.body.TunnellingRequestBody;
import li.pitschmann.test.KnxBody;
import li.pitschmann.test.KnxMockServer;
import li.pitschmann.test.KnxTest;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.fail;

/**
 * Test for sending/receiving {@link TunnellingRequestBody} and {@link TunnellingAckBody}
 *
 * @author PITSCHR
 */
public class TunnellingRequestTest {
    /**
     * Perform a communication between {@link KnxClient} and the KNX Net/IP router containing following:
     * <p>
     * Normal communication, however no Tunnelling acknowledge packet is sent by client because of wrong channel id. It is simply ignored.
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
            // Send TunnellingRequestBody with wrong channel id = 17 (0x11) instead of 7 (0x07)
            KnxBody.Failures.TUNNELLING_REQUEST_WRONG_CHANNEL_ID,
            // Wait 500ms
            "WAIT=100",
            // and then DisconnectRequestBody
            KnxBody.DISCONNECT_REQUEST,
            // Wait for last response from client and quit mock server gracefully
            "WAIT=NEXT"
    })
    @DisplayName("Packet with wrong Channel ID received. Ignore.")
    public void testWrongChannelId(final KnxMockServer mockServer) {
        try (final KnxClient client = mockServer.newKnxClient()) {
            mockServer.waitForCompletion();
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                // no ACK is being sent because of wrong channel id (=ignored)
                DisconnectResponseBody.class // #4
        );
    }

    /**
     * Perform a communication between {@link KnxClient} and the KNX Net/IP router containing following:
     * <p>
     * Normal communication, however no Tunnelling acknowledge packet is sent by client because
     * the {@link TunnellingRequestBody} is being to sent to control channel (instead of data channel)
     * It is simply ignored.
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody)
            "WAIT=NEXT",
            // send ConnectResponseBody
            KnxBody.CONNECT_RESPONSE,
            // wait for next packet (will be: ConnectionStateRequestBody)
            "WAIT=NEXT",
            // send ConnectionStateResponseBody
            KnxBody.CONNECTION_STATE_RESPONSE,
            // Send TunnellingRequestBody to wrong channel (control instead of data)
            "CHANNEL=CONTROL{" + KnxBody.TUNNELLING_REQUEST + "}",
            // Wait 500ms
            "WAIT=500",
            // and then DisconnectRequestBody
            KnxBody.DISCONNECT_REQUEST,
            // Wait for last response from client and quit mock server gracefully
            "WAIT=NEXT"
    })
    @DisplayName("Packet sent to wrong channel. Ignore.")
    public void testWrongChannel(final KnxMockServer mockServer) {
        try (final KnxClient client = mockServer.newKnxClient()) {
            mockServer.waitForCompletion();
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                // no ACK is being sent because it was sent to wrong channel (=ignored)
                DisconnectResponseBody.class // #4
        );
    }
}
