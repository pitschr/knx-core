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
import li.pitschmann.utils.*;
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
                    // return four tunnelling acks as we are getting four tunnelling requests
                    "WAIT=NEXT,06100421000a04070000," +
                    "WAIT=NEXT,06100421000a04070100," +
                    "WAIT=NEXT," + KnxBody.TUNNELLING_ACK + "," +
                    // wait for packet with type 'DisconnectRequestBody'
                    "WAIT=DISCONNECT_REQUEST," +
                    // send DisconnectResponseBody
                    KnxBody.DISCONNECT_RESPONSE
    )
    @DisplayName("Test write requests (incl. async)")
    public void testWriteRequests(final KnxMockServer mockServer) {
        var groupAddress = GroupAddress.of(1, 2, 3);

        try (var client = (BaseKnxClient) mockServer.newKnxClient()) {
            // async write request with DPT
            client.writeRequestAsync(groupAddress, DPT1.SWITCH.toValue(false)).get();
            // async write request with APCI data
            client.writeRequestAsync(groupAddress, new byte[]{0x00}).get();
            // send via body
            client.send(KnxBody.TUNNELLING_REQUEST_BODY);
            // async read request
//            client.readRequestAsync(groupAddress).get();

        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert if mock server got right sequences
        var requestBodies = mockServer.getReceivedBodies().stream().filter(b -> b.getServiceType() == ServiceType.TUNNELING_REQUEST).collect(Collectors.toList());
        assertThat(requestBodies).hasSize(3);
        // first two requests are sequences
        assertThat(((TunnellingRequestBody)requestBodies.get(0)).getSequence()).isEqualTo(0);
        assertThat(((TunnellingRequestBody)requestBodies.get(1)).getSequence()).isEqualTo(1);
        // last one is sequence (taken sample) = 27
        assertThat(((TunnellingRequestBody)requestBodies.get(2)).getSequence()).isEqualTo(27);
    }
}
