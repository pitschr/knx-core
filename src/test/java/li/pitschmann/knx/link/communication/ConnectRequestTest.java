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
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.exceptions.KnxChannelIdNotReceivedException;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.test.KnxBody;
import li.pitschmann.test.KnxMockServer;
import li.pitschmann.test.KnxTest;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.fail;

/**
 * Test for sending {@link ConnectRequestBody} / receiving {@link ConnectResponseBody} via {@link KnxClient}
 *
 * @author PITSCHR
 */
public class ConnectRequestTest {
    /**
     * Test no responding/not available KNX Net/IP device
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody attempt #1)
            "WAIT=NEXT",
            // do nothing (no ConnectResponseBody #1)
            "NO_ACTION",
            // wait for next packet (will be: ConnectRequestBody attempt #2)
            "WAIT=NEXT",
            // do nothing (no ConnectResponseBody #2)
            "NO_ACTION",
            // wait for next packet (will be: ConnectRequestBody attempt #3)
            "WAIT=NEXT",
            // do nothing (no ConnectResponseBody #1)
            "NO_ACTION"})
    @DisplayName("Error: No Channel ID because of no response")
    public void testFailureNoResponse(final KnxMockServer mockServer) {
        try (final var client = mockServer.newKnxClient()) {
            mockServer.waitForCompletion();
            fail("Not the expected state");
        } catch (final KnxChannelIdNotReceivedException e) {
            // OK
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectRequestBody.class, // #3
                ConnectRequestBody.class // #4
        );
    }

    /**
     * Test if all free connection slots are used on KNX Net/IP device. It will return the "no more connections" error.
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody)
            "WAIT=CONNECT_REQUEST",
            // send ConnectResponseBody with "No More Connections" error
            KnxBody.Failures.CONNECT_RESPONSE_NO_MORE_CONNECTIONS})
    @DisplayName("Error: No Channel ID because of no more connections")
    public void testFailureNoMoreConnections(final KnxMockServer mockServer) {
        try (final var client = mockServer.newKnxClient()) {
            mockServer.waitForCompletion();
            fail("Not the expected state");
        } catch (final KnxChannelIdNotReceivedException e) {
            // OK
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class // #2
        );
    }

    /**
     * Test if all free connection slots are used on KNX Net/IP device. It will return the "no more connections" error.
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody attempt #1)
            "WAIT=CONNECT_REQUEST",
            // send ConnectResponseBody (corrupted #1)
            KnxBody.Failures.CONNECT_RESPONSE_BAD_DATA,
            // wait for next packet (will be: ConnectRequestBody attempt #2)
            "WAIT=CONNECT_REQUEST",
            // ConnectResponseBody (OK)
            KnxBody.CONNECT_RESPONSE,
            // wait for Connection State Request
            "WAIT=CONNECTION_STATE_REQUEST",
            // send ConnectionStateResponse
            KnxBody.CONNECTION_STATE_RESPONSE,
            // wait for DisconnectRequestBody from KNX Net/IP Client
            "WAIT=DISCONNECT_REQUEST",
            // send DisconnectResponseBody
            KnxBody.DISCONNECT_RESPONSE
    })
    @DisplayName("Error: Corrupted Connect Response and then OK")
    public void testConnectionCorruptedAndThenOK(final KnxMockServer mockServer) {
        try (final var client = mockServer.newKnxClient()) {
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTION_STATE_REQUEST);
            // OK
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectRequestBody.class, // #3
                ConnectionStateRequestBody.class, // #4
                DisconnectRequestBody.class // #5
        );
    }
}
