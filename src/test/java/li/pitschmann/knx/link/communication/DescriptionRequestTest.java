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
import li.pitschmann.knx.link.exceptions.KnxNoTunnelingException;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.test.KnxBody;
import li.pitschmann.test.KnxMockServer;
import li.pitschmann.test.KnxTest;
import li.pitschmann.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Test for sending {@link DescriptionRequestBody} / receiving {@link DescriptionResponseBody} via
 * {@link KnxClient}
 *
 * @author PITSCHR
 */
public class DescriptionRequestTest {
    private static Logger LOG = LoggerFactory.getLogger(DescriptionRequestTest.class);

    /**
     * Perform a happy path between {@link KnxClient} and the KNX Net/IP device containing following:
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
        try (final var client = mockServer.newKnxClient()) {
            // after connection state request sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTION_STATE_REQUEST);
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
     * Perform a happy path between {@link KnxClient} and the KNX Net/IP device containing following:
     * <ol>
     * <li>{@link DescriptionResponseBody}</li>
     * <li>{@link ConnectResponseBody}</li>
     * <li>{@link ConnectionStateResponseBody}</li>
     * <li>Wait action</li>
     * <li>{@link DisconnectRequestBody}</li>
     * </ol>
     * <p>
     * Disconnect will be initiated by the KNX Net/IP device.
     */
    @KnxTest(KnxBody.Sequences.MINIMAL_DISCONNECT_BY_REMOTE)
    @DisplayName("Test happy path - disconnect by KNX Net/IP device")
    public void testSuccessDisconnectByRemote(final KnxMockServer mockServer) {
        try (final var client = mockServer.newKnxClient()) {
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
     * Test to fetch the {@link DescriptionResponseBody} from KNX Net/IP device at <strong>third</strong> attempt
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
            "WAIT=CONNECT_REQUEST",
            // ConnectResponseBody
            KnxBody.CONNECT_RESPONSE,
            // wait for next packet (will be: ConnectionStateRequestBody)
            "WAIT=CONNECTION_STATE_REQUEST",
            // ConnectionStateResponseBody
            KnxBody.CONNECTION_STATE_RESPONSE,
            // wait for next packet (will be: DisconnectRequestBody)
            "WAIT=DISCONNECT_REQUEST",
            // DisconnectResponseBody
            KnxBody.DISCONNECT_RESPONSE})
    public void testSuccessDescriptionOnThirdAttempt(final KnxMockServer mockServer) {
        try (final var client = mockServer.newKnxClient()) {
            // after connection state request sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTION_STATE_REQUEST);
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
     * Test to {@link DescriptionResponseBody} that is not offering the required tunneling support
     */
    @KnxTest({
            // On first packet send DescriptionResponseBody without "Tunneling" support
            KnxBody.Failures.DESCRIPTION_RESPONSE_WITHOUT_TUNNELING
    })
    public void testDescriptionWithoutTunneling(final KnxMockServer mockServer) {
        try (final var client = mockServer.newKnxClient()) {
            mockServer.waitForCompletion();
            fail("Not the expected state");
        } catch (final KnxNoTunnelingException noTunnelingException) {
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
        try (final var client = mockServer.newKnxClient()) {
            mockServer.waitForCompletion();
            fail("Unexpected test state");
        } catch (final KnxDescriptionNotReceivedException e) {
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
     * Test no responding/not available KNX Net/IP device
     *
     * @param mockServer
     */
    @KnxTest
    @DisplayName("Error: Test alive but no response from KNX Net/IP device")
    public void testNoCommunication(final KnxMockServer mockServer) {
        // define a specific port because there will be no real port available.
        // the port number 4711 is randomly chosen
        final var mockServerSpy = spy(mockServer);
        when(mockServerSpy.getPort()).thenReturn(4711);

        try (final var client = mockServerSpy.newKnxClient()) {
            // keep client alive until it is closed
            while (!client.isClosed()) {
                Sleeper.milliseconds(10);
            }
            fail("Unexpected test state");
        } catch (final KnxDescriptionNotReceivedException e) {
            // OK
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }
    }
}
