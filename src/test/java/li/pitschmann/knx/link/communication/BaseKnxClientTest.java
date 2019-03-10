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

import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.datapoint.DPT1;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.test.KnxBody;
import li.pitschmann.test.KnxMockServer;
import li.pitschmann.test.KnxTest;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link BaseKnxClient}
 *
 * @author PITSCHR
 */
public class BaseKnxClientTest {
    /**
     * Tests common methods for {@link BaseKnxClient}.
     *
     * <ul>
     * <li>Checks if {@link BaseKnxClient#getStatistic()} is an unmodifiable instance of {@link KnxStatistic}</li>
     * <li>Checks if {@link ExtensionPlugin#onInitialization(KnxClient)} is invoked with an instance of {@link BaseKnxClient}</li>
     * </ul>
     *
     * @param mockServer
     */
    @KnxTest(KnxBody.Sequences.MINIMAL_DISCONNECT_BY_CLIENT)
    @DisplayName("Check Base KNX Client")
    public void testCommonMethods(final KnxMockServer mockServer) {
        // mock extension plugin to verify if the init method is invoked with correct client instance
        final var extensionPlugin = mock(ExtensionPlugin.class);

        final var client = new BaseKnxClient(mockServer.newConfigBuilder().plugin(extensionPlugin).build());
        try (client) {
            assertThat(client.getStatusPool()).isNotNull();
            assertThat(client.getConfig()).isNotNull();
            assertThat(client.isClosed()).isFalse();

            // verify if statistic is an unmodifiable instance
            final var statistic = client.getStatistic();
            assertThat(statistic).isNotNull();
            assertThat(statistic.getClass().getSimpleName()).isEqualTo("UnmodifiableKnxStatistic");

            // verify if the init method of extension plugin has been called and the parameter is
            // the client (and not e.g. internal client)
            final var argCaptor = ArgumentCaptor.forClass(KnxClient.class);
            verify(extensionPlugin).onInitialization(argCaptor.capture());
            assertThat(argCaptor.getValue()).isSameAs(client);
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
                    "WAIT=CONNECT_REQUEST," +
                    // send ConnectResponseBody
                    KnxBody.CONNECT_RESPONSE + "," +
                    // wait for next packet (will be: ConnectionStateRequestBody)
                    "WAIT=CONNECTION_STATE_REQUEST," +
                    // ConnectionStateResponseBody
                    KnxBody.CONNECTION_STATE_RESPONSE + "," +
                    // send three tunneling acknowledges as we are getting three tunneling requests
                    "WAIT=TUNNELING_REQUEST,06100421000a04070000," + // sequence = 0
                    "WAIT=TUNNELING_REQUEST,06100421000a04070100," + // sequence = 1
                    "WAIT=TUNNELING_REQUEST,06100421000a04070200," + // sequence = 2
                    "WAIT=TUNNELING_REQUEST," + KnxBody.TUNNELING_ACK + "," + // sequence = 27
                    "WAIT=TUNNELING_REQUEST," + KnxBody.TUNNELING_ACK_2 + "," + // sequence = 11
                    // send one tunneling acknowledge for read request
                    // wait for packet with type 'DisconnectRequestBody'
                    "WAIT=DISCONNECT_REQUEST," +
                    // send DisconnectResponseBody
                    KnxBody.DISCONNECT_RESPONSE
    )
    @DisplayName("Test write requests (incl. async)")
    public void testWriteRequests(final KnxMockServer mockServer) {
        final var groupAddress = GroupAddress.of(1, 2, 3);

        try (final var client = (BaseKnxClient) mockServer.newKnxClient()) {
            // async read request
            client.readRequest(groupAddress).get();
            // async write request with DPT
            client.writeRequest(groupAddress, DPT1.SWITCH.toValue(false)).get();
            // async write request with APCI data
            client.writeRequest(groupAddress, new byte[]{0x00}).get();
            // send via body
            client.send(KnxBody.TUNNELING_REQUEST_BODY);
            // send via body and timeout
            client.send(KnxBody.TUNNELING_REQUEST_BODY_2, 1000);

            mockServer.waitForReceivedServiceType(ServiceType.TUNNELING_REQUEST, 5);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert if mock server got right sequences
        final var requestBodies = mockServer.getReceivedBodies().stream().filter(b -> b.getServiceType() == ServiceType.TUNNELING_REQUEST).collect(Collectors.toList());
        assertThat(requestBodies).hasSize(5);
        // first two requests are sequences (incremental)
        assertThat(((TunnelingRequestBody) requestBodies.get(0)).getSequence()).isEqualTo(0);
        assertThat(((TunnelingRequestBody) requestBodies.get(1)).getSequence()).isEqualTo(1);
        assertThat(((TunnelingRequestBody) requestBodies.get(2)).getSequence()).isEqualTo(2);
        // 2nd last is pre-defined sequence (taken sample) = 27
        assertThat(((TunnelingRequestBody) requestBodies.get(3)).getSequence()).isEqualTo(27);
        // last is pre-defined sequence (taken sample) = 11
        assertThat(((TunnelingRequestBody) requestBodies.get(4)).getSequence()).isEqualTo(11);
    }
}
