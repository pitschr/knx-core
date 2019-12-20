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

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.ControlChannelRelated;
import li.pitschmann.knx.core.body.DataChannelRelated;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.MockServerTest;
import li.pitschmann.knx.core.test.TestHelpers;
import li.pitschmann.knx.core.test.data.TestExtensionPlugin;
import li.pitschmann.knx.core.test.data.TestObserverPlugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link InternalKnxClient}
 *
 * @author PITSCHR
 */
public class KnxClientTest {
    /**
     * Test following methods indirectly (inside {@link InternalKnxClient)}:
     * <ul>
     *      <li>{@code InternalKnxClient#notifyPlugins(Object, List, BiConsumer)}
     *          (internally used for initialization, start and shutdown)</li>
     *      <li>{@link InternalKnxClient#notifyIncomingBody(Body)}</li>
     *      <li>{@link InternalKnxClient#notifyOutgoingBody(Body)}</li>
     *      <li>{@link InternalKnxClient#notifyError(Throwable)}</li>
     * </ul>
     */
    @MockServerTest(requests = {
            "cemi(1)={2900bce010c84c0f0300800c23}",
            "raw(2)={0610020600140000000100000000000004000000}"} // corrupted body
    )
    @DisplayName("Default Client: Test notification of plug-ins")
    public void testPlugins(final MockServer mockServer) {
        final var config = mockServer
                .newConfigBuilder()
                .plugin(TestObserverPlugin.class)
                .plugin(TestExtensionPlugin.class)
                .build();

        final TestObserverPlugin observerPlugin;
        final TestExtensionPlugin extensionPlugin;
        try (final var client = DefaultKnxClient.createStarted(config)) {
            observerPlugin = Objects.requireNonNull(client.getInternalClient().getPluginManager().getPlugin(TestObserverPlugin.class));
            extensionPlugin = Objects.requireNonNull(client.getInternalClient().getPluginManager().getPlugin(TestExtensionPlugin.class));

            // wait for first tunneling ack body sent by client
            mockServer.waitForReceivedServiceType(ServiceType.TUNNELING_ACK);
        } catch (final Throwable t) {
            throw new AssertionError("Unexpected test state", t);
        }

        // assert packets
        mockServer.assertReceivedPackets(
                DescriptionRequestBody.class,
                ConnectRequestBody.class,
                ConnectionStateRequestBody.class,
                TunnelingAckBody.class,
                DisconnectRequestBody.class);

        // wait until mock server is done (replied disconnect response to KNX client)
        mockServer.waitDone();

        // verify number of notifications to observer plug-in
        assertThat(observerPlugin.getInitInvocations()).isOne();
        assertThat(observerPlugin.getIncomingBodies()).hasSize(5);
        assertThat(observerPlugin.getOutgoingBodies()).hasSize(5);
        assertThat(observerPlugin.getErrors()).hasSize(2);

        // verify number of notifications to extension plug-in
        assertThat(extensionPlugin.getInitInvocations()).isOne();
        assertThat(extensionPlugin.getStartInvocations()).isOne();
        assertThat(extensionPlugin.getShutdownInvocations()).isOne();
    }

    /**
     * Test {@link InternalKnxClient#getControlHPAI()} and {@link InternalKnxClient#getDataHPAI()}
     */
    @MockServerTest
    @DisplayName("Internal Client: Test Control and Data HPAI")
    public void testControlAndDataHPAI(final MockServer mockServer) {
        try (final var client = new InternalKnxClient(mockServer.newConfigBuilder().build())) {
            // internal client must be invoked explicitly
            client.start();

            // get connect request body that was received by mock server
            final var connectRequestBody = (ConnectRequestBody) mockServer.getReceivedBodies()
                    .stream()
                    .filter(p -> p instanceof ConnectRequestBody)
                    .findFirst()
                    .get();

            final var controlHPAI = client.getControlHPAI();
            assertThat(controlHPAI).isNotNull().isEqualTo(connectRequestBody.getControlEndpoint());

            final var dataHPAI = client.getDataHPAI();
            assertThat(dataHPAI).isNotNull().isEqualTo(connectRequestBody.getDataEndpoint());

            // and should be different
            assertThat(controlHPAI).isNotSameAs(dataHPAI);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }
    }

    /**
     * Test {@link InternalKnxClient#notifyError(Throwable)} when calling an erroneous plug-in.
     * <p>
     * In case an exception is thrown by plug-in the KNX client should still be alive.
     */
    @Test
    @DisplayName("Internal Client: Test erroneous plug-in")
    public void testErroneousPlugIn() {
        final var client = new InternalKnxClient(TestHelpers.mockConfig());

        try (client) {
            // should not be an issue
            client.notifyError(new Throwable());
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // verify that onError has been invoked
        assertThat(client.getStatistic().getNumberOfErrors()).isOne();
    }

    /**
     * Verify sending request body without any channel information (neither {@link ControlChannelRelated} nor
     * {@link DataChannelRelated})
     */
    @Test
    @DisplayName("Error: Send body without any channel information")
    public void testBodyWithoutChannel() {
        final var client = new InternalKnxClient(TestHelpers.mockConfig());
        final var requestBody = mock(RequestBody.class);

        // verify if it is returning Illegal Argument Exception
        assertThatThrownBy(() -> client.send(requestBody))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No channel relation defined for body.");
        assertThatThrownBy(() -> client.send(requestBody, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No channel relation defined for body.");
    }
}
