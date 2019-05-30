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
import li.pitschmann.knx.test.MockServer;
import li.pitschmann.knx.test.MockServerTest;
import li.pitschmann.knx.test.strategy.IgnoreStrategy;
import li.pitschmann.knx.test.strategy.impl.DefaultDescriptionStrategy;
import li.pitschmann.knx.test.strategy.impl.DescriptionBadDataStrategy;
import li.pitschmann.knx.test.strategy.impl.DescriptionInvalidServiceStrategy;
import li.pitschmann.knx.test.strategy.impl.DescriptionNoTunnelingStrategy;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.fail;

/**
 * Test for sending {@link DescriptionRequestBody} / receiving {@link DescriptionResponseBody} via
 * {@link KnxClient}
 *
 * @author PITSCHR
 */
public class DescriptionRequestTest {
    /**
     * Test to fetch the {@link DescriptionResponseBody} from KNX Net/IP device at <strong>third</strong> attempt
     * <p>
     * {@link ConnectResponseBody}, {@link ConnectionStateResponseBody} and {@link DisconnectResponseBody} are not a
     * part of this test, but added to make the test faster (otherwise they would have been sent x-times as well)
     */
    @MockServerTest(descriptionStrategy = {IgnoreStrategy.class, IgnoreStrategy.class, DefaultDescriptionStrategy.class})
    @DisplayName("Partial: Success description response on third attempt")
    public void testSuccessDescriptionOnThirdAttempt(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            // after connection state request sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.CONNECTION_STATE_REQUEST);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // wait until all packets are sent/received
        mockServer.waitDone();

        // assert packets
        mockServer.assertReceivedPackets(//
                DescriptionRequestBody.class, // #1 (no response, ignored by remote)
                DescriptionRequestBody.class, // #2 (no response, ignored by remote)
                DescriptionRequestBody.class, // #3
                ConnectRequestBody.class, // #4
                ConnectionStateRequestBody.class, // #5
                DisconnectRequestBody.class // #6
        );
    }

    /**
     * Test to {@link DescriptionResponseBody} that is not offering the required tunneling support
     */
    @MockServerTest(descriptionStrategy = DescriptionNoTunnelingStrategy.class)
    @DisplayName("Error: Test KNX connection without tunneling service offer on remote side")
    public void testDescriptionWithoutTunneling(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            mockServer.waitDone();
            fail("Not the expected state");
        } catch (final KnxNoTunnelingException noTunnelingException) {
            // OK - we can abort mock server
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets(DescriptionRequestBody.class);
    }

    /**
     * Test to {@link DescriptionResponseBody} that are corrupted.
     */
    @MockServerTest(descriptionStrategy = {DescriptionInvalidServiceStrategy.class, DescriptionBadDataStrategy.class})
    @DisplayName("Error: Invalid Description responses from remote")
    public void testDescriptionCorrupted(final MockServer mockServer) {
        try (final var client = mockServer.createTestClient()) {
            mockServer.waitDone();
            fail("Unexpected test state");
        } catch (final KnxDescriptionNotReceivedException e) {
            // OK - we can cancel mock server
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets(//
                DescriptionRequestBody.class, // #1 (corrupted response, DescriptionInvalidServiceStrategy)
                DescriptionRequestBody.class, // #2 (corrupted response, DescriptionBadDataStrategy)
                DescriptionRequestBody.class // #3 (corrupted response, DescriptionInvalidServiceStrategy)
        );
    }
}
