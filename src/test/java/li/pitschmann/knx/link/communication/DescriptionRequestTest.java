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
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.knx.link.header.*;
import li.pitschmann.test.*;
import li.pitschmann.utils.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.slf4j.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test for sending {@link DescriptionRequestBody} / receiving {@link DescriptionResponseBody} via
 * {@link KnxClient}
 *
 * @author PITSCHR
 */
public class DescriptionRequestTest {
    private static Logger LOG = LoggerFactory.getLogger(DescriptionRequestTest.class);

    /**
     * Perform a happy path between {@link KnxClient} and the KNX Net/IP router containing following:
     * <ol>
     * <li>{@link DescriptionResponseBody}</li>
     * <li>{@link ConnectResponseBody}</li>
     * <li>{@link ConnectionStateResponseBody}</li>
     * <li>{@link DisconnectResponseBody}</li>
     * </ol>
     * <p>
     * Disconnect will be initiated by the KNX client.
     */
    @KnxTest(KnxBody.Sequences.MINIMAL_DISCONNECT_BY_CLIENT)
    @DisplayName("Test happy path - disconnect by KNX client")
    public void testSuccessDisconnectByClient(final KnxMockServer mockServer) {
        try (final KnxClient client = mockServer.newKnxClient()) {
            // after connection state request sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTIONSTATE_REQUEST);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // wait until all packets are sent/received
        mockServer.waitForCompletion();

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                DisconnectRequestBody.class // #4
        );
    }

    /**
     * Perform a happy path between {@link KnxClient} and the KNX Net/IP router containing following:
     * <ol>
     * <li>{@link DescriptionResponseBody}</li>
     * <li>{@link ConnectResponseBody}</li>
     * <li>{@link ConnectionStateResponseBody}</li>
     * </ol>
     * <p>
     * Disconnect will be initiated by the KNX client.
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
            // wait for next packet and then quit immediately
            "WAIT=NEXT"})
    @DisplayName("Test happy path - disconnect by KNX client (but no Disconnect response from Router)")
    public void testSuccessDisconnectByClientButNoDisconnectResponseFromRouter(final KnxMockServer mockServer) {
        try (final KnxClient client = mockServer.newKnxClient()) {
            // after connection state request sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTIONSTATE_REQUEST);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // wait until all packets are sent/received
        mockServer.waitForCompletion();

        // assert packets
        mockServer.assertReceivedPackets( //
                DescriptionRequestBody.class, // #1
                ConnectRequestBody.class, // #2
                ConnectionStateRequestBody.class, // #3
                DisconnectRequestBody.class // #4
        );
    }

    /**
     * Perform a happy path between {@link KnxClient} and the KNX Net/IP router containing following:
     * <ol>
     * <li>{@link DescriptionResponseBody}</li>
     * <li>{@link ConnectResponseBody}</li>
     * <li>{@link ConnectionStateResponseBody}</li>
     * <li>Wait action</li>
     * <li>{@link DisconnectRequestBody}</li>
     * </ol>
     * <p>
     * Disconnect will be initiated by the KNX Net/IP router.
     */
    @KnxTest(KnxBody.Sequences.MINIMAL_DISCONNECT_BY_ROUTER)
    @DisplayName("Test happy path - disconnect by KNX Net/IP router")
    public void testSuccessDisconnectByRouter(final KnxMockServer mockServer) {
        try (final KnxClient client = mockServer.newKnxClient()) {
            // just wait until mock server is closed
            mockServer.waitForCompletion();
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
     * Test to fetch the {@link DescriptionResponseBody} from KNX Net/IP router at <strong>third</strong> attempt
     * <p>
     * {@link ConnectResponseBody}, {@link ConnectionStateResponseBody} and {@link DisconnectResponseBody} are not a
     * part of this test, but added to make the test faster (otherwise they would have been sent x-times as well)
     */
    @KnxTest({
            // On first packet do nothing (no DescriptionResponseBody #1)
            "NO_ACTION",
            // wait for next packet (will be: DescriptionRequestBody attempt #2)
            "WAIT=NEXT",
            // do nothing (no DescriptionResponseBody #2)
            "NO_ACTION",
            // wait for next packet (will be: DescriptionRequestBody attempt #3)
            "WAIT=NEXT",
            // send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody)
            "WAIT=NEXT",
            // ConnectResponseBody
            KnxBody.CONNECT_RESPONSE,
            // wait for next packet (will be: ConnectionStateRequestBody)
            "WAIT=NEXT",
            // ConnectionStateResponseBody
            KnxBody.CONNECTION_STATE_RESPONSE,
            // wait for next packet (will be: DisconnectRequestBody)
            "WAIT=NEXT",
            // DisconnectResponseBody
            KnxBody.DISCONNECT_RESPONSE})
    public void testSuccessDescriptionOnThirdAttempt(final KnxMockServer mockServer) {
        try (final KnxClient client = mockServer.newKnxClient()) {
            // after connection state request sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTIONSTATE_REQUEST);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // wait until all packets are sent/received
        mockServer.waitForCompletion();

        // assert packets
        mockServer.assertReceivedPackets(//
                DescriptionRequestBody.class, // #1 (no response)
                DescriptionRequestBody.class, // #2 (no response)
                DescriptionRequestBody.class, // #3
                ConnectRequestBody.class, // #4
                ConnectionStateRequestBody.class, // #5
                DisconnectRequestBody.class // #6
        );
    }

    /**
     * Test to {@link DescriptionResponseBody} that is not offering the required tunnelling support
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody without "Tunnelling" support
            KnxBody.Failures.DESCRIPTION_RESPONSE_WITHOUT_TUNNELLING
    })
    public void testDescriptionWithoutTunnelling(final KnxMockServer mockServer) {
        try (final KnxClient client = mockServer.newKnxClient()) {
            mockServer.waitForCompletion();
            fail("Not the expected state");
        } catch (final KnxNoTunnellingException noTunnellingException) {
            // OK
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets(DescriptionRequestBody.class);
    }

    /**
     * Test to {@link DescriptionResponseBody} that are corrupted.
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody (corrupted #1, invalid service type)
            KnxBody.Failures.DESCRIPTION_RESPONSE_INVALID_SERVICE_TYPE,
            // wait for next packet (will be: DescriptionRequestBody attempt #2)
            "WAIT=NEXT",
            // DescriptionResponseBody (corrupted #2, no supported device families DIB)
            KnxBody.Failures.DESCRIPTION_RESPONSE_NO_SUPPORTED_DEVICE_DIB,
            // wait for next packet (will be: DescriptionRequestBody attempt #3)
            "WAIT=NEXT",
            // DescriptionResponseBody (corrupted #3, bad data -> endless loop)
            KnxBody.Failures.DESCRIPTION_RESPONSE_BAD_DATA
    })
    public void testDescriptionCorrupted(final KnxMockServer mockServer) {
        try (final KnxClient client = mockServer.newKnxClient()) {
            mockServer.waitForCompletion();
            fail("Not the expected state");
        } catch (final KnxBodyNotReceivedException e) {
            // OK
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets(//
                DescriptionRequestBody.class, // #1 (corrupted response)
                DescriptionRequestBody.class, // #2 (corrupted response)
                DescriptionRequestBody.class // #3 (corrupted response)
        );
    }

    /**
     * Test no responding/not available KNX Net/IP router
     *
     * @param mockServer
     */
    @KnxTest
    @DisplayName("Error: Test alive but no response from KNX Net/IP router")
    public void testNoCommunication(final KnxMockServer mockServer) {
        // define a specific port because there will be no real port available.
        // the port number 4711 is randomly chosen
        KnxMockServer mockServerSpy = Mockito.spy(mockServer);
        Mockito.when(mockServerSpy.getPort()).thenReturn(4711);

        try (final KnxClient client = mockServerSpy.newKnxClient()) {
            // keep client alive until it is closed
            while (!client.isClosed()) {
                Sleeper.milliseconds(10);
            }
            fail("Not the expected state");
        } catch (final KnxBodyNotReceivedException e) {
            // we didn't received the description body
            assertThat(e).hasMessageContaining(DescriptionResponseBody.class.toString());
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }
    }
}
