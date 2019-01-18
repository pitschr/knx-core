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

import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.header.*;
import li.pitschmann.test.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test for sending {@link ConnectRequestBody} / receiving {@link ConnectResponseBody} via {@link KnxClient}
 *
 * @author PITSCHR
 */
public class ConnectionStateRequestTest {
    /**
     * Test successful connection state communication with KNX Net/IP router
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody)
            "WAIT=NEXT",
            // send ConnectResponseBody
            KnxBody.CONNECT_RESPONSE,
            // wait for ConnectionStateRequestBody #1
            "WAIT=NEXT",
            // send ConnectionStateResponseBody #1
            KnxBody.CONNECTION_STATE_RESPONSE,
            // wait for ConnectionStateRequestBody #2
            "WAIT=NEXT",
            // send ConnectionStateResponseBody #2
            KnxBody.CONNECTION_STATE_RESPONSE,
            // wait for next packet (will be: DisconnectRequestBody)
            "WAIT=NEXT",
            // DisconnectResponseBody
            KnxBody.DISCONNECT_RESPONSE})
    @DisplayName("Successful Connection State Request communication")
    public void testSuccessful(final KnxMockServer mockServer) {
        try (final KnxClient client = mockServer.newKnxClient()) {
            // after 2-nd connection state request sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTIONSTATE_REQUEST, 2);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

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
     * Test no response of ConnectionStateResponseBody from KNX Net/IP router
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody)
            "WAIT=NEXT",
            // send ConnectResponseBody
            KnxBody.CONNECT_RESPONSE,
            //  ait for next packet (will be: ConnectionStateRequestBody)
            "WAIT=NEXT",
            // no connection state response, and then wait for next packet
            // will be eepeated for 12 times
            "REPEAT=12{NO_ACTION,WAIT=NEXT}",
            // DisconnectResponseBody
            KnxBody.DISCONNECT_RESPONSE})
    @DisplayName("Error: No Connection State Request received")
    public void testFailureNoResponse(final KnxMockServer mockServer) {
        try (final KnxClient client = mockServer.newKnxClient()) {
            mockServer.waitForCompletion();
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3 (1/12)
                ConnectionStateRequestBody.class, // #4 (2/12)
                ConnectionStateRequestBody.class, // #5 (3/12)
                ConnectionStateRequestBody.class, // #6 (4/12)
                ConnectionStateRequestBody.class, // #7 (5/12)
                ConnectionStateRequestBody.class, // #8 (6/12)
                ConnectionStateRequestBody.class, // #9 (7/12)
                ConnectionStateRequestBody.class, // #10 (8/12)
                ConnectionStateRequestBody.class, // #11 (9/12)
                ConnectionStateRequestBody.class, // #12 (10/12)
                ConnectionStateRequestBody.class, // #13 (11/12)
                ConnectionStateRequestBody.class, // #14 (12/12)
                DisconnectRequestBody.class // #15
        );
    }
}
