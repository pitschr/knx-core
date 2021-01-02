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

import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.DisconnectResponseBody;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.test.KnxBody;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.MockServerTest;
import li.pitschmann.knx.core.test.strategy.impl.TunnelingWrongChannelIdStrategy;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Test for sending/receiving {@link TunnelingRequestBody} and {@link TunnelingAckBody}
 *
 * @author PITSCHR
 */
public class TunnelingRequestTest {
    /**
     * Perform a communication between {@link KnxClient} and the KNX Net/IP device
     * containing following:
     * <p>
     * Normal communication, however no Tunneling acknowledge packet is accepted
     * by KNX client because of wrong channel id. It is simply ignored.
     */
    @MockServerTest(tunnelingStrategy = TunnelingWrongChannelIdStrategy.class)
    @DisplayName("Packet with wrong Channel ID received. Ignore.")
    public void testWrongChannelId(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            // send tunneling request
            final var ackBody = client.send(KnxBody.TUNNELING_REQUEST_BODY, 1000).get();
            // no acknowledge received
            assertThat(ackBody).isNull();
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                TunnelingRequestBody.class, // #4 (wrong channel id acknowledge)
                TunnelingRequestBody.class, // #5 (wrong channel id acknowledge)
                TunnelingRequestBody.class, // #6 (wrong channel id acknowledge)
                DisconnectRequestBody.class // #7
        );
    }

    /**
     * Perform a communication between {@link KnxClient} and the KNX Net/IP device containing following:
     * <p>
     * Normal communication, however no Tunneling acknowledge packet is sent by client because
     * the {@link TunnelingRequestBody} is being to sent to control channel (instead of data channel)
     * It is simply ignored.
     */
    @MockServerTest(
            disconnectTrigger = "after-trigger",
            requests = "channel=control,cemi(1)={2900bce010c84c0f0300800c23}")
    @DisplayName("Packet sent to wrong channel. Ignore.")
    public void testWrongChannel(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            assertThat(client.isRunning());
            // wait until mock server closes the connection
            mockServer.waitDone();
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
