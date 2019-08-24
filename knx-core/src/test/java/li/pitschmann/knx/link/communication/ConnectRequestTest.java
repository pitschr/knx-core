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
import li.pitschmann.knx.link.body.Status;
import li.pitschmann.knx.link.exceptions.KnxChannelIdNotReceivedException;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.test.MockServer;
import li.pitschmann.knx.test.MockServerTest;
import li.pitschmann.knx.test.strategy.IgnoreStrategy;
import li.pitschmann.knx.test.strategy.impl.ConnectBadDataStrategy;
import li.pitschmann.knx.test.strategy.impl.ConnectNoMoreConnectionsStrategy;
import li.pitschmann.knx.test.strategy.impl.DefaultConnectStrategy;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
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
    @MockServerTest(connectStrategy = IgnoreStrategy.class)
    @DisplayName("Error: No Channel ID because of no response")
    public void testFailureNoResponse(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            mockServer.waitDone();
            fail("Not the expected state");
        } catch (final KnxChannelIdNotReceivedException e) {
            assertThat(e.getRequestBody()).isNotNull();
            assertThat(e.getResponseBody()).isNull(); // null because of no response
            // OK - we can abort mock server
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
    @MockServerTest(connectStrategy = ConnectNoMoreConnectionsStrategy.class)
    @DisplayName("Error: No Channel ID because of no more connections")
    public void testFailureNoMoreConnections(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            mockServer.waitDone();
            fail("Not the expected state");
        } catch (final KnxChannelIdNotReceivedException e) {
            assertThat(e.getRequestBody()).isNotNull();
            assertThat(e.getResponseBody()).isNotNull();
            assertThat(e.getResponseBody().getStatus()).isEqualTo(Status.E_NO_MORE_CONNECTIONS);
            // OK - we can abort mock server
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
    @MockServerTest(connectStrategy = {ConnectBadDataStrategy.class, DefaultConnectStrategy.class})
    @DisplayName("Error: Corrupted Connect Response and then OK")
    public void testConnectionCorruptedAndThenOK(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
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
