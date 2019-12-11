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
import li.pitschmann.knx.core.body.ConnectResponseBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.ConnectionStateResponseBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DescriptionResponseBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.DisconnectResponseBody;
import li.pitschmann.knx.core.body.RoutingIndicationBody;
import li.pitschmann.knx.core.body.SearchRequestBody;
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.cemi.APCI;
import li.pitschmann.knx.core.datapoint.DPT1;
import li.pitschmann.knx.core.exceptions.KnxDescriptionNotReceivedException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.MockServerTest;
import li.pitschmann.knx.core.test.strategy.IgnoreStrategy;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Test for {@link DefaultKnxClient}
 *
 * @author PITSCHR
 */
public class DefaultKnxClientTest {

    /**
     * Perform a happy path between {@link KnxClient} and the KNX Net/IP device returns following:
     * <ol>
     * <li>{@link DescriptionResponseBody}</li>
     * <li>{@link ConnectResponseBody}</li>
     * <li>{@link ConnectionStateResponseBody}</li>
     * <li>{@link DisconnectResponseBody}</li>
     * </ol>
     * <p>
     * Disconnect will be initiated by the KNX client.
     */
    @MockServerTest
    @DisplayName("Success: Disconnect by KNX client")
    public void testSuccessDisconnectByClient(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            // after connection state request sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTION_STATE_REQUEST);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                DisconnectRequestBody.class // #4
        );
    }

    /**
     * Perform a happy path between {@link KnxClient} and the KNX Net/IP device returns following:
     * <ol>
     * <li>{@link DescriptionResponseBody}</li>
     * <li>{@link ConnectResponseBody}</li>
     * <li>{@link ConnectionStateResponseBody}</li>
     * <li>{@link DisconnectResponseBody}</li>
     * </ol>
     * <p>
     * Disconnect will be initiated by the KNX client.
     */
    @MockServerTest(disconnectStrategy = IgnoreStrategy.class)
    @DisplayName("Success: Disconnect by KNX client after 3 disconnect request attempts")
    public void testSuccessDisconnectByClientNoDisconnectResponse(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            // after connection state request sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTION_STATE_REQUEST);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets (up to 3 disconnect requests from client, then continue with client closing)
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                DisconnectRequestBody.class, // #4 (ignored)
                DisconnectRequestBody.class, // #5 (ignored)
                DisconnectRequestBody.class // #6 (ignored)
        );
    }

    /**
     * Perform a happy path between {@link KnxClient} and the KNX Net/IP device returns following:
     * <ol>
     * <li>{@link DescriptionResponseBody}</li>
     * <li>{@link ConnectResponseBody}</li>
     * <li>{@link ConnectionStateResponseBody}</li>
     * <li>{@link DisconnectRequestBody}</li>
     * </ol>
     * <p>
     * Disconnect will be initiated by the KNX Net/IP device.
     */
    @MockServerTest(disconnectTrigger = "wait-request(1)=CONNECTION_STATE_REQUEST")
    @DisplayName("Success: Disconnect by remote")
    public void testSuccessDisconnectByRemote(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            // just wait until mock server closes the connection
            mockServer.waitDone();
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets(//
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                DisconnectResponseBody.class // #4
        );
    }

    /**
     * Test no responding/not available KNX Net/IP device
     *
     * @param mockServer
     */
    @MockServerTest
    @DisplayName("Error: Test alive but no response from KNX Net/IP device")
    public void testNoCommunication(final MockServer mockServer) {
        // define a specific port because there will be no real port available.
        // the port 4711 is randomly chosen
        final var mockServerSpy = spy(mockServer);
        when(mockServerSpy.getPort()).thenReturn(4711);

        try (final var client = mockServerSpy.createTestClient()) {
            // keep client alive until it is closed
            mockServer.waitDone();
            fail("Unexpected test state");
        } catch (final KnxDescriptionNotReceivedException e) {
            // OK - abort mock server
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        mockServer.assertReceivedPackets(); // nothing received
    }

    /**
     * Test {@link DefaultKnxClient#createStarted(String)}
     *
     * @param mockServer
     */
    @MockServerTest
    @DisplayName("Success: Test KNX client instantiation using host address as string")
    public void testHostAddressString(final MockServer mockServer) {
        try (final var client = DefaultKnxClient.createStarted("localhost:" + mockServer.getPort())) {
            // ok
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTION_STATE_REQUEST);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        mockServer.waitDone();

        // assert packets
        mockServer.assertReceivedPackets(//
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                DisconnectRequestBody.class // #4
        );
    }

    /**
     * Test {@link DefaultKnxClient#createStarted()} which will find mock server using
     * discovery service. Here we are testing the official KNX port number.
     *
     * @param mockServer
     */
    @MockServerTest(useDiscovery = true)
    @DisplayName("Success: Test KNX client instantiation using discovery service and default KNX port")
    public void testDiscovery(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            // ok
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTION_STATE_REQUEST);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        mockServer.waitDone();

        // assert packets
        mockServer.assertReceivedPackets(//
                SearchRequestBody.class, // #1
                DescriptionRequestBody.class, // #2
                ConnectRequestBody.class, // #3
                ConnectionStateRequestBody.class, // #4
                DisconnectRequestBody.class // #5
        );
    }

    /**
     * Test {@link DefaultKnxClient#createStarted(String)} with multicast address.
     * Here we are testing the routing feature.
     *
     * @param mockServer
     */
    @MockServerTest(useRouting = true)
    @DisplayName("Success: Test KNX client instantiation using routing service (via multicast)")
    public void testRouting(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            client.readRequest(GroupAddress.of(11, 4, 67));
            client.writeRequest(GroupAddress.of(11, 4, 67), DPT1.SWITCH.toValue(true));
            mockServer.waitForReceivedServiceType(ServiceType.ROUTING_INDICATION, 2);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets(//
                RoutingIndicationBody.class, // #1 (read request)
                RoutingIndicationBody.class // #2 (write request)
        );

        final var readPacket = (RoutingIndicationBody) mockServer.getReceivedBodies().get(0);
        assertThat(readPacket.getCEMI().getApci()).isEqualTo(APCI.GROUP_VALUE_READ);

        final var writePacket = (RoutingIndicationBody) mockServer.getReceivedBodies().get(1);
        assertThat(writePacket.getCEMI().getApci()).isEqualTo(APCI.GROUP_VALUE_WRITE);
    }
}
