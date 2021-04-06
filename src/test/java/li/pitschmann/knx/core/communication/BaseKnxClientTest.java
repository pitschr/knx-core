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
import li.pitschmann.knx.core.body.RoutingIndicationBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.config.Config;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.datapoint.DPT1;
import li.pitschmann.knx.core.datapoint.DPT5;
import li.pitschmann.knx.core.datapoint.value.DataPointValue;
import li.pitschmann.knx.core.exceptions.KnxCommunicationException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.plugin.ExtensionPlugin;
import li.pitschmann.knx.core.test.KnxBody;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.MockServerTest;
import li.pitschmann.knx.core.test.TestHelpers;
import li.pitschmann.knx.core.test.data.TestExtensionPlugin;
import li.pitschmann.knx.core.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

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
     * @param mockServer the mock server
     */
    @MockServerTest
    @DisplayName("OK: Check Base KNX Client")
    public void testCommonMethods(final MockServer mockServer) {
        final var config = mockServer.newConfigBuilder()
                .plugin(new TestExtensionPlugin())
                .build();

        try (final var client = new BaseKnxClient(config)) {
            assertThat(client.getStatusPool()).isNotNull();
            assertThat(client.getConfig()).isNotNull();
            assertThat(client.isRunning()).isFalse(); // it is false, because it has not been started yet

            // verify if statistic is an unmodifiable instance
            final var statistic = client.getStatistic();
            assertThat(statistic).isNotNull();
            assertThat(statistic.getClass().getSimpleName()).isEqualTo("UnmodifiableKnxStatistic");

            // verify extension plugin and if it has been invoked correctly
            final var extensionPlugin = client.getInternalClient().getPluginManager().getPlugin(TestExtensionPlugin.class);
            assertThat(extensionPlugin).isNotNull();

            // wait bit until the extension plugin has been invoked asynchronously
            // it may rarely happen that verify(..) fails because onInitialization(..)
            // was not called in time
            Sleeper.milliseconds(() -> extensionPlugin.getInitInvocations() > 0, 1000);

            // verify if the init method of extension plugin has been called and the parameter is
            // the client (and not e.g. internal client)
            assertThat(extensionPlugin.getKnxClient()).isSameAs(client);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }
    }

    /**
     * Tests read and write requests methods for {@link BaseKnxClient} using
     * default behavior (NAT = not enabled)
     *
     * @param mockServer the mock server
     */
    @MockServerTest
    @DisplayName("OK: Tunneling: Test read and write requests asynchronously")
    public void testReadAndWriteRequestsTunneling(final MockServer mockServer) {
        readAndWriteTunnelingRequests(mockServer, (m) -> m.newConfigBuilder().build());
    }

    /**
     * Tests read and write requests methods for {@link BaseKnxClient} using
     * enabled NAT (non-default behavior)
     *
     * @param mockServer the mock server
     */
    @MockServerTest
    @DisplayName("OK: Tunneling + NAT: Test read and write requests asynchronously")
    public void testReadAndWriteRequestsTunnelingAndNAT(final MockServer mockServer) {
        readAndWriteTunnelingRequests(mockServer, (m) -> m.newConfigBuilder().setting(CoreConfigs.NAT, true).build());
    }

    @MockServerTest(useRouting = true)
    @DisplayName("OK: Routing: Test read and write requests asynchronously")
    public void testReadAndWriteRequestsRouting(final MockServer mockServer) {
        final var groupAddress = GroupAddress.of(1, 2, 3);

        try (final var client = mockServer.createTestClient()) {
            // read request
            assertThat(client.readRequest(groupAddress))
                    .succeedsWithin(Duration.ofSeconds(1)).isEqualTo(Boolean.TRUE);
            // write request
            assertThat(client.writeRequest(groupAddress, DPT1.SWITCH.of(false)))
                    .succeedsWithin(Duration.ofSeconds(1)).isEqualTo(Boolean.TRUE);
            // 2nd write request
            assertThat(client.writeRequest(groupAddress, DPT5.SCALING.of(100)))
                    .succeedsWithin(Duration.ofSeconds(1)).isEqualTo(Boolean.TRUE);

            mockServer.waitForReceivedServiceType(ServiceType.ROUTING_INDICATION, 3);
        }

        // assert if mock server got right sequences
        final var routingIndications = mockServer.getReceivedBodies()
                .stream()
                .filter(RoutingIndicationBody.class::isInstance)
                .map(RoutingIndicationBody.class::cast)
                .collect(Collectors.toList());
        assertThat(routingIndications).hasSize(3);
        assertThat(routingIndications.get(0).getCEMI().getData()).isEmpty(); // empty because of read request
        assertThat(routingIndications.get(1).getCEMI().getData()).containsExactly(0x00); // 0x00 = Switch(false)
        assertThat(routingIndications.get(2).getCEMI().getData()).containsExactly(0xFF); // 0xFF = Scaling(100%)
    }

    @Test
    @DisplayName("ERROR: Test read request throwing exceptions")
    void testReadRequestsWithExceptions() throws ExecutionException, InterruptedException, TimeoutException {
        final var groupAddress = GroupAddress.of(1, 2, 3);

        final var config = TestHelpers.mockConfig();
        final var baseKnxClient = spy(new BaseKnxClient(config));
        final var completableFutureMock = mock(CompletableFuture.class);
        doReturn(completableFutureMock).when(baseKnxClient).readRequest(any(GroupAddress.class));

        // throwing TimeoutException
        doThrow(new TimeoutException()).when(completableFutureMock).get(anyLong(), any(TimeUnit.class));
        assertThatThrownBy(() -> baseKnxClient.readRequest(groupAddress, 1000))
                .isInstanceOf(KnxCommunicationException.class)
                .hasMessage("Time (1000 ms) exceeded for read request to group address: " + groupAddress);

        // throwing ExecutionException
        doThrow(new ExecutionException(null)).when(completableFutureMock).get(anyLong(), any(TimeUnit.class));
        assertThatThrownBy(() -> baseKnxClient.readRequest(groupAddress, 1000))
                .isInstanceOf(KnxCommunicationException.class)
                .hasMessage("Read request and wait for acknowledge failed for group address: " + groupAddress);

        // throwing InterruptedException
        // run this test in a sub-thread because it may interrupt parallel JUnit test cases otherwise
        doThrow(new InterruptedException()).when(completableFutureMock).get(anyLong(), any(TimeUnit.class));
        final var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            assertThat(baseKnxClient.readRequest(groupAddress, 1000)).isFalse();
        });
        executor.shutdown();
    }

    @Test
    @DisplayName("ERROR: Test write request throwing exceptions")
    void testWriteRequestsWithExceptions() throws ExecutionException, InterruptedException, TimeoutException {
        final var groupAddress = GroupAddress.of(1, 2, 3);
        final var dataPointValue = DPT1.SWITCH.of(true);

        final var config = TestHelpers.mockConfig();
        final var baseKnxClient = spy(new BaseKnxClient(config));
        final var completableFutureMock = mock(CompletableFuture.class);
        doReturn(completableFutureMock).when(baseKnxClient).writeRequest(any(GroupAddress.class), any(DataPointValue.class));

        // throwing TimeoutException
        doThrow(new TimeoutException()).when(completableFutureMock).get(anyLong(), any(TimeUnit.class));
        assertThatThrownBy(() -> baseKnxClient.writeRequest(groupAddress, dataPointValue, 1000))
                .isInstanceOf(KnxCommunicationException.class)
                .hasMessage("Time (1000 ms) exceeded for write request to group address: " + groupAddress);

        // throwing ExecutionException
        doThrow(new ExecutionException(null)).when(completableFutureMock).get(anyLong(), any(TimeUnit.class));
        assertThatThrownBy(() -> baseKnxClient.writeRequest(groupAddress, dataPointValue, 1000))
                .isInstanceOf(KnxCommunicationException.class)
                .hasMessage("Write request and wait for acknowledge failed for group address: " + groupAddress);

        // throwing InterruptedException
        // run this test in a sub-thread because it may interrupt parallel JUnit test cases otherwise
        doThrow(new InterruptedException()).when(completableFutureMock).get(anyLong(), any(TimeUnit.class));
        final var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            assertThat(baseKnxClient.writeRequest(groupAddress, dataPointValue, 1000)).isFalse();
        });
        executor.shutdown();
    }

    /**
     * Tests read and write requests methods for {@link BaseKnxClient} using a
     * specific configuration which is defined via {@code configFunction} function.
     *
     * @param mockServer     the mock server
     * @param configFunction defines which configuration should be used
     */
    private void readAndWriteTunnelingRequests(final MockServer mockServer, final Function<MockServer, Config> configFunction) {
        final var groupAddress = GroupAddress.of(1, 2, 3);

        final var config = configFunction.apply(mockServer);
        try (final var client = DefaultKnxClient.createStarted(config)) {
            // read request
            assertThat(client.readRequest(groupAddress))
                    .succeedsWithin(Duration.ofSeconds(1)).isEqualTo(Boolean.TRUE);
            // write request
            assertThat(client.writeRequest(groupAddress, DPT1.SWITCH.of(false)))
                    .succeedsWithin(Duration.ofSeconds(1)).isEqualTo(Boolean.TRUE);
            // 2nd write request
            assertThat(client.writeRequest(groupAddress, DPT5.SCALING.of(100)))
                    .succeedsWithin(Duration.ofSeconds(1)).isEqualTo(Boolean.TRUE);
            // send via body
            client.send(KnxBody.TUNNELING_REQUEST_BODY);
            // send via body and timeout
            assertThat(client.send(KnxBody.TUNNELING_REQUEST_BODY_2, 1000))
                    .succeedsWithin(Duration.ofSeconds(1)).matches(response -> response.getServiceType() == ServiceType.TUNNELING_ACK);

            mockServer.waitForReceivedServiceType(ServiceType.TUNNELING_REQUEST, 5);
        }

        // assert if mock server got right sequences
        final var tunnelingRequestBodies = mockServer.getReceivedBodies()
                .stream()
                .filter(TunnelingRequestBody.class::isInstance)
                .map(TunnelingRequestBody.class::cast)
                .collect(Collectors.toList());
        assertThat(tunnelingRequestBodies).hasSize(5);
        // first two requests are sequences (incremental)
        assertThat(tunnelingRequestBodies.get(0).getSequence()).isEqualTo(0);
        assertThat(tunnelingRequestBodies.get(1).getSequence()).isEqualTo(1);
        assertThat(tunnelingRequestBodies.get(2).getSequence()).isEqualTo(2);
        // 2nd last is pre-defined sequence (taken sample) = 27
        assertThat(tunnelingRequestBodies.get(3).getSequence()).isEqualTo(27);
        // last is pre-defined sequence (taken sample) = 11
        assertThat(tunnelingRequestBodies.get(4).getSequence()).isEqualTo(11);
    }
}
