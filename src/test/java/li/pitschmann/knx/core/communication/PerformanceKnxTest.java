/*
 * Copyright (C) 2021 Pitschmann Christoph
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

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DescriptionResponseBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.MockServerTest;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Test for sending {@link DescriptionRequestBody} / receiving {@link DescriptionResponseBody} via
 * {@link KnxClient}
 *
 * @author PITSCHR
 */
class PerformanceKnxTest {
    /**
     * How many times the TUNNELING_REQUEST and TUNNELING_ACK packets should be
     * sent between KNX Net/IP device and clients.
     * <p>
     * When doing a high number (10000 times) - then ensure that you set
     * the minimum log level at 'INFO'. Logging with DEBUG or lower will slow down
     * the system due I/O writing. Alter the log level in /test/resources/logback.xml
     * by setting the root level from to 'INFO'. Or start JUnit class with "-Droot-level=INFO"
     *
     * Update the value in MockServerCommandParser manually.
     * CoreConfigs.Event#CHECK_INTERVAL needs to be set to "0L" to remove the delay
     */
    private static final int TIMES = 1000;

    /**
     * Perform a happy path between {@link KnxClient} and the KNX Net/IP device with N packets.
     */
    @MockServerTest(requests = "cemi(" + TIMES + ")={2E00BCE010FF0A96010081}")
    @DisplayName("KNX Mock Server sending " + TIMES + "x Tunneling requests")
    void testSendByMockServer(final MockServer mockServer) {

        // Adjust JUnit specific configuration
        final var config = mockServer.newConfigBuilder() //
                // Use default setting (for unit testing it is set 1 seconds - instead of 10 seconds)
                .setting(CoreConfigs.ConnectionState.REQUEST_TIMEOUT, CoreConfigs.ConnectionState.REQUEST_TIMEOUT.getDefaultValue())
                // Use default setting (for unit testing it is set 6 seconds - instead of 60 seconds)
                .setting(CoreConfigs.ConnectionState.HEARTBEAT_INTERVAL, CoreConfigs.ConnectionState.HEARTBEAT_INTERVAL.getDefaultValue())
                .build();

        try (final var client = DefaultKnxClient.createStarted(config)) {
            assertThat(client.isRunning()).isTrue();
            // after N-times tunneling acknowledge sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.TUNNELING_ACK, TIMES);
            System.out.println("PITSCHR: " + mockServer.getReceivedBodies().size() + ", " + mockServer.isCancelled());
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // wait until all packets are sent/received
        mockServer.waitDone();

        // assert packets
        final var expectedClasses = new LinkedList<Class<? extends Body>>();
        expectedClasses.add(DescriptionRequestBody.class);
        expectedClasses.add(ConnectRequestBody.class);
        expectedClasses.add(ConnectionStateRequestBody.class);
        for (var i = 0; i < TIMES; i++) {
            expectedClasses.add(TunnelingAckBody.class);
        }
        expectedClasses.add(DisconnectRequestBody.class);
        mockServer.assertReceivedPackets(expectedClasses);

        // check sequence
        final var tunnelingAckBodies = mockServer.getReceivedBodies().stream()
                .filter(TunnelingAckBody.class::isInstance)
                .map(TunnelingAckBody.class::cast)
                .collect(Collectors.toUnmodifiableList());
        assertThat(tunnelingAckBodies).hasSize(TIMES);
        assertThat(tunnelingAckBodies.get(0).getSequence()).isEqualTo(0);
        assertThat(tunnelingAckBodies.get(31).getSequence()).isEqualTo(31);
        assertThat(tunnelingAckBodies.get(178).getSequence()).isEqualTo(178);
        assertThat(tunnelingAckBodies.get(255).getSequence()).isEqualTo(255);
        // after 0xFF it should start back with 0
        assertThat(tunnelingAckBodies.get(256).getSequence()).isEqualTo(0);
        assertThat(tunnelingAckBodies.get(258).getSequence()).isEqualTo(2);
    }

    /**
     * Perform a happy path between {@link KnxClient} and the KNX Net/IP device with N packets.
     * <p>
     * Here the client is sending {@code TIMES} tunneling requests to KNX mock server
     */
    @MockServerTest
    @DisplayName("KNX Client sending " + TIMES + "x Tunneling requests")
    void testSendByClient(final MockServer mockServer) {

        // Adjust JUnit specific configuration
        final var config = mockServer.newConfigBuilder() //
                // Use default setting (for unit testing it is set 1 seconds - instead of 10 seconds)
                .setting(CoreConfigs.ConnectionState.REQUEST_TIMEOUT, CoreConfigs.ConnectionState.REQUEST_TIMEOUT.getDefaultValue())
                // Use default setting (for unit testing it is set 6 seconds - instead of 60 seconds)
                .setting(CoreConfigs.ConnectionState.HEARTBEAT_INTERVAL, CoreConfigs.ConnectionState.HEARTBEAT_INTERVAL.getDefaultValue())
                .build();

        try (final var client = DefaultKnxClient.createStarted(config)) {
            assertThat(client.isRunning()).isTrue();
            final var groupAddress = GroupAddress.of(1, 2, 3);
            for (int i = 0; i < TIMES; i++) {
                assertThat(client.readRequest(groupAddress))
                        .succeedsWithin(Duration.ofSeconds(1)).isEqualTo(Boolean.TRUE);
            }
            mockServer.waitForReceivedServiceType(ServiceType.TUNNELING_REQUEST, TIMES);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert packets
        final var expectedClasses = new LinkedList<Class<? extends Body>>();
        expectedClasses.add(DescriptionRequestBody.class);
        expectedClasses.add(ConnectRequestBody.class);
        expectedClasses.add(ConnectionStateRequestBody.class);
        for (var i = 0; i < TIMES; i++) {
            expectedClasses.add(TunnelingRequestBody.class);
        }
        expectedClasses.add(DisconnectRequestBody.class);
        mockServer.assertReceivedPackets(expectedClasses);

        // check sequence
        final var tunnelingRequestBodies = mockServer.getReceivedBodies().stream()
                .filter(TunnelingRequestBody.class::isInstance)
                .map(TunnelingRequestBody.class::cast)
                .collect(Collectors.toUnmodifiableList());
        assertThat(tunnelingRequestBodies).hasSize(TIMES);
        assertThat(tunnelingRequestBodies.get(0).getSequence()).isEqualTo(0);
        assertThat(tunnelingRequestBodies.get(56).getSequence()).isEqualTo(56);
        assertThat(tunnelingRequestBodies.get(198).getSequence()).isEqualTo(198);
        assertThat(tunnelingRequestBodies.get(255).getSequence()).isEqualTo(255);
        // after 0xFF it should start back with 0
        assertThat(tunnelingRequestBodies.get(256).getSequence()).isEqualTo(0);
        assertThat(tunnelingRequestBodies.get(258).getSequence()).isEqualTo(2);
    }
}
