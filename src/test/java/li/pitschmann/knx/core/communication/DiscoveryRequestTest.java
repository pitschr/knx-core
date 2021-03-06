/*
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
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.SearchRequestBody;
import li.pitschmann.knx.core.exceptions.KnxDiscoveryNotReceivedException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.MockServerTest;
import li.pitschmann.knx.core.test.strategy.IgnoreStrategy;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Test for sending {@link SearchRequestBody} to discover the remote KNX Net/IP router via
 * {@link KnxClient}
 *
 * @author PITSCHR
 */
public class DiscoveryRequestTest {

    /**
     * Tests a successful discovery mode. Knx NET/IP Device is found
     * and can be established
     *
     * @param mockServer the mock server
     */
    @MockServerTest(useDiscovery = true)
    @DisplayName("Test a successful discovery request")
    public void testSuccessDiscovery(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            assertThat(client.isRunning());
            // after connection state request sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTION_STATE_REQUEST);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                SearchRequestBody.class, // #1
                DescriptionRequestBody.class, // #2
                ConnectRequestBody.class, // #3
                ConnectionStateRequestBody.class, // #4
                DisconnectRequestBody.class // #5
        );
    }

    /**
     * Tests a failed discovery. KNX Net/IP device is not found. Here we are simulating that
     * the mock server is not responding on search request
     *
     * @param mockServer the mock server
     */
    @MockServerTest(useDiscovery = true, discoveryStrategy = IgnoreStrategy.class)
    @DisplayName("Test a failed discovery request (mock server not responding on search request)")
    public void testFailedDiscovery(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            assertThat(client.isRunning());
            // keep client alive until it is closed
            mockServer.waitDone();
            fail("Unexpected test state");
        } catch (final KnxDiscoveryNotReceivedException e) {
            assertThat(e.getRequestBody()).isNotNull();
            assertThat(e.getResponseBody()).isNull(); // null because of no response
            // OK - abort mock server
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets( //
                SearchRequestBody.class, // #1 (ignored)
                SearchRequestBody.class, // #2 (ignored)
                SearchRequestBody.class // #3 (ignored)
        );
    }
}
