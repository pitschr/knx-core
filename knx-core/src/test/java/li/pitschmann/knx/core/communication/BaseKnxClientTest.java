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

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.config.Config;
import li.pitschmann.knx.core.config.ConfigBuilder;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.datapoint.DPT1;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

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
    @MockServerTest
    @DisplayName("OK: Check Base KNX Client")
    public void testCommonMethods(final MockServer mockServer) {
        final var config = mockServer.newConfigBuilder()
                .plugin(TestExtensionPlugin.class)
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

    @Test
    @DisplayName("ERROR: Test read and write request throwing exceptions")
    @SuppressWarnings("unchecked")
    public void testReadAndWriteRequestsWithExceptions() throws ExecutionException, InterruptedException {
        final var groupAddress = GroupAddress.of(1, 2, 3);

        final var config = ConfigBuilder.tunneling().build();
        final var baseKnxClient = spy(new BaseKnxClient(config));
        final var internalKnxClientMock = TestHelpers.mockInternalKnxClient();
        final var completableFutureMock = (CompletableFuture<ResponseBody>) mock(CompletableFuture.class);

        when(baseKnxClient.isRunning()).thenReturn(true);
        when(baseKnxClient.getInternalClient()).thenReturn(internalKnxClientMock);
        when(internalKnxClientMock.send(any(RequestBody.class), anyLong())).thenReturn(completableFutureMock);

        // throwing ExecutionException
        doThrow(new ExecutionException(new Throwable())).when(completableFutureMock).get();
        assertThat(baseKnxClient.readRequest(groupAddress)).isFalse();
        assertThat(baseKnxClient.writeRequest(groupAddress, new byte[1])).isFalse();

        // throwing InterruptedException
        // run this test in a sub-thread because it may interrupt parallel JUnit test cases otherwise
        doThrow(new InterruptedException()).when(completableFutureMock).get();
        final var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            assertThat(baseKnxClient.readRequest(groupAddress)).isFalse();
            assertThat(baseKnxClient.writeRequest(groupAddress, new byte[1])).isFalse();
        });
        executor.shutdown();
    }

    /**
     * Tests read and write requests methods for {@link BaseKnxClient} using
     * default behavior (NAT = not enabled)
     *
     * @param mockServer
     */
    @MockServerTest
    @DisplayName("OK: Test read and write requests (incl. async)")
    public void testReadAndWriteRequests(final MockServer mockServer) {
        testReadAndWriteRequests(mockServer, (m) -> m.newConfigBuilder().build());
    }

    /**
     * Tests read and write requests methods for {@link BaseKnxClient} using
     * enabled NAT (non-default behavior)
     *
     * @param mockServer
     */
    @MockServerTest
    @DisplayName("OK: Test read and write requests (incl. async) over NAT")
    public void testReadAndWriteRequestsOverNAT(final MockServer mockServer) {
        testReadAndWriteRequests(mockServer, (m) -> m.newConfigBuilder().setting(CoreConfigs.NAT, true).build());
    }

    /**
     * Tests read and write requests methods for {@link BaseKnxClient} using a
     * specific configuration which is defined via {@code configFunction} function.
     *
     * @param mockServer
     * @param configFunction defines which configuration should be used
     */
    private void testReadAndWriteRequests(final MockServer mockServer, final Function<MockServer, Config> configFunction) {
        final var groupAddress = GroupAddress.of(1, 2, 3);

        final var config = configFunction.apply(mockServer);
        try (final var client = DefaultKnxClient.createStarted(config)) {
            // async read request
            client.readRequest(groupAddress);
            // async write request with DPT
            client.writeRequest(groupAddress, DPT1.SWITCH.toValue(false));
            // async write request with APCI data
            client.writeRequest(groupAddress, new byte[]{0x00});
            // send via body
            client.send(KnxBody.TUNNELING_REQUEST_BODY);
            // send via body and timeout
            client.send(KnxBody.TUNNELING_REQUEST_BODY_2, 1000);

            mockServer.waitForReceivedServiceType(ServiceType.TUNNELING_REQUEST, 5);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // assert if mock server got right sequences
        final var tunnelingRequestBodies = mockServer.getReceivedBodies().stream().filter(TunnelingRequestBody.class::isInstance).map(TunnelingRequestBody.class::cast).collect(Collectors.toList());
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
