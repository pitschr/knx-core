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
import li.pitschmann.knx.link.body.ConnectionStateResponseBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.exceptions.KnxDescriptionNotReceivedException;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.test.MockServer;
import li.pitschmann.knx.test.MockServerTest;
import li.pitschmann.knx.test.strategy.IgnoreStrategy;
import org.junit.jupiter.api.DisplayName;

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
    @DisplayName("Disconnect by KNX client after 3 disconnect request attempts")
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
    @DisplayName("Test KNX client instantiation using host address as string")
    public void testInstantiationViaHostAddress(final MockServer mockServer) {
        try (final var client = DefaultKnxClient.createStarted("localhost:" + mockServer.getPort())) {
            // ok
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }
    }
}
