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
import li.pitschmann.knx.link.body.address.*;
import li.pitschmann.knx.link.datapoint.*;
import li.pitschmann.knx.link.header.*;
import li.pitschmann.test.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;

import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test for {@link InternalKnxClient}
 *
 * @author PITSCHR
 */
public class BaseKnxClientTest {
    private static Logger LOG = LoggerFactory.getLogger(BaseKnxClientTest.class);

    /**
     * Tests common methods for {@link BaseKnxClient}
     *
     * @param mockServer
     */
    @KnxTest(KnxBody.Sequences.MINIMAL_DISCONNECT_BY_CLIENT)
    @DisplayName("Check Base KNX Client common methods")
    public void testCommonMethods(final KnxMockServer mockServer) {
        var client = (BaseKnxClient) mockServer.newKnxClient();
        try (client) {
            assertThat(client.getStatusPool()).isNotNull();
            assertThat(client.getConfig()).isNotNull();
            assertThat(client.isClosed()).isFalse();
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        } finally {
            assertThat(client.isClosed()).isTrue();
        }
    }

    /**
     * Tests common methods for {@link BaseKnxClient}
     *
     * @param mockServer
     */
    @KnxTest(
            // On first request send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE + "," +
                    // wait for next packet (will be: ConnectRequestBody)
                    "WAIT=NEXT," +
                    // send ConnectResponseBody
                    KnxBody.CONNECT_RESPONSE + "," +
                    // wait for next packet (will be: ConnectionStateRequestBody)
                    "WAIT=NEXT," +
                    // ConnectionStateResponseBody
                    KnxBody.CONNECTION_STATE_RESPONSE + "," +
                    // send three tunnelling acknowledges as we are getting three tunnelling requests
                    "WAIT=NEXT,06100421000a04070000," + // sequence = 0
                    "WAIT=NEXT,06100421000a04070100," + // sequence = 1
                    "WAIT=NEXT,06100421000a04070200," + // sequence = 2
                    "WAIT=NEXT," + KnxBody.TUNNELLING_ACK + "," + // sequence = 27
                    // send one tunnelling acknowledge for read request
                    // wait for packet with type 'DisconnectRequestBody'
                    "WAIT=DISCONNECT_REQUEST," +
                    // send DisconnectResponseBody
                    KnxBody.DISCONNECT_RESPONSE
    )
    @DisplayName("Test write requests (incl. async)")
    public void testWriteRequests(final KnxMockServer mockServer) {
        var groupAddress = GroupAddress.of(1, 2, 3);

        try (var client = (BaseKnxClient) mockServer.newKnxClient()) {
            // async read request
            client.readRequest(groupAddress);
            // async write request with DPT
            client.writeRequest(groupAddress, DPT1.SWITCH.toValue(false));
            // async write request with APCI data
            client.writeRequest(groupAddress, new byte[]{0x00});
            // send via body
            client.send(KnxBody.TUNNELLING_REQUEST_BODY);

            mockServer.waitForReceivedServiceType(ServiceType.TUNNELING_REQUEST, 4);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert if mock server got right sequences
        var requestBodies = mockServer.getReceivedBodies().stream().filter(b -> b.getServiceType() == ServiceType.TUNNELING_REQUEST).collect(Collectors.toList());
        assertThat(requestBodies).hasSize(4);
        // first two requests are sequences
        assertThat(((TunnellingRequestBody)requestBodies.get(0)).getSequence()).isEqualTo(0);
        assertThat(((TunnellingRequestBody)requestBodies.get(1)).getSequence()).isEqualTo(1);
        assertThat(((TunnellingRequestBody)requestBodies.get(2)).getSequence()).isEqualTo(2);
        // last one is sequence (taken sample) = 27
        assertThat(((TunnellingRequestBody)requestBodies.get(3)).getSequence()).isEqualTo(27);
    }
}
