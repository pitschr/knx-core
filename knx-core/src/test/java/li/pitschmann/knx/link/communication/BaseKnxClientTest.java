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

import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.config.Config;
import li.pitschmann.knx.link.config.ConfigConstants;
import li.pitschmann.knx.link.datapoint.DPT1;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.test.KnxBody;
import li.pitschmann.knx.test.MockServer;
import li.pitschmann.knx.test.MockServerTest;
import li.pitschmann.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;

import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verify;

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
        // mock extension plugin to verify if the init method is invoked with correct client instance
        final var extensionPlugin = mock(ExtensionPlugin.class);

        final var client = new BaseKnxClient(mockServer.newConfigBuilder().plugin(extensionPlugin).build());
        try (client) {
            assertThat(client.getStatusPool()).isNotNull();
            assertThat(client.getConfig()).isNotNull();
            assertThat(client.isRunning()).isFalse(); // it is false, because it has not been started yet

            // verify if statistic is an unmodifiable instance
            final var statistic = client.getStatistic();
            assertThat(statistic).isNotNull();
            assertThat(statistic.getClass().getSimpleName()).isEqualTo("UnmodifiableKnxStatistic");

            // wait bit until the extension plugin has been invoked asynchronously
            // it may rarely happen that verify(..) fails because onInitialization(..)
            // was not called in time
            Sleeper.milliseconds(() -> !mockingDetails(extensionPlugin).getInvocations().isEmpty(), 1000);
            // verify if the init method of extension plugin has been called and the parameter is
            // the client (and not e.g. internal client)
            final var argCaptor = ArgumentCaptor.forClass(KnxClient.class);
            verify(extensionPlugin).onInitialization(argCaptor.capture());
            assertThat(argCaptor.getValue()).isInstanceOf(BaseKnxClient.class).isSameAs(client);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }
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
        testReadAndWriteRequests(mockServer, (m) -> m.newConfigBuilder().setting(ConfigConstants.NAT, true).build());
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
