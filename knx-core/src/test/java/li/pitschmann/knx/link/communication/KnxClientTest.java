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

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.ConnectionStateResponseBody;
import li.pitschmann.knx.link.body.ControlChannelRelated;
import li.pitschmann.knx.link.body.DataChannelRelated;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.config.Config;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.test.MockServer;
import li.pitschmann.knx.test.MockServerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link InternalKnxClient}
 *
 * @author PITSCHR
 */
public class KnxClientTest {
    /**
     * Test following methods indirectly (inside {@link InternalKnxClient)}:
     * <ul>
     * <li>{@code InternalKnxClient#notifyPlugins(Object, List, BiConsumer)}
     * (internally used for initialization, start and shutdown)</li>
     * <li>{@link InternalKnxClient#notifyIncomingBody(Body)}</li>
     * <li>{@link InternalKnxClient#notifyOutgoingBody(Body)}</li>
     * <li>{@link InternalKnxClient#notifyError(Throwable)}</li>
     * </ul>
     */
    @MockServerTest(requests = {
            "cemi(1)={2900bce010c84c0f0300800c23}",
            "raw(2)={0610020600140000000100000000000004000000}"} // corrupted body
    )
    @DisplayName("Default Client: Test notification of plug-ins")
    public void testPlugins(final MockServer mockServer) {
        final var configBuilder = mockServer.newConfigBuilder();

        // observer plug-in
        final var observerPlugin = mock(ObserverPlugin.class);
        configBuilder.plugin(observerPlugin);

        // extension plug-in
        final var extensionPlugin = mock(ExtensionPlugin.class);
        configBuilder.plugin(extensionPlugin);

        try (final var client = DefaultKnxClient.createStarted(configBuilder.build())) {
            // wait for first tunneling ack body sent by client
            mockServer.waitForReceivedServiceType(ServiceType.TUNNELING_ACK);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
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
        verify(observerPlugin, times(1)).onInitialization(any());

        verify(observerPlugin).onIncomingBody(isA(DescriptionResponseBody.class));
        verify(observerPlugin).onIncomingBody(isA(ConnectResponseBody.class));
        verify(observerPlugin).onIncomingBody(isA(ConnectionStateResponseBody.class));
        verify(observerPlugin).onIncomingBody(isA(TunnelingRequestBody.class));
        verify(observerPlugin).onIncomingBody(isA(DisconnectResponseBody.class));
        verify(observerPlugin, times(5)).onIncomingBody(any());

        verify(observerPlugin).onOutgoingBody(isA(DescriptionRequestBody.class));
        verify(observerPlugin).onOutgoingBody(isA(ConnectRequestBody.class));
        verify(observerPlugin).onOutgoingBody(isA(ConnectionStateRequestBody.class));
        verify(observerPlugin).onOutgoingBody(isA(TunnelingAckBody.class));
        verify(observerPlugin).onOutgoingBody(isA(DisconnectRequestBody.class));
        verify(observerPlugin, times(5)).onOutgoingBody(any());

        verify(observerPlugin, times(2)).onError(any());

        // verify number of notifications to extension plug-in
        verify(extensionPlugin).onInitialization(any());
        verify(extensionPlugin).onStart();
        verify(extensionPlugin).onShutdown();
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
     * Test {@link InternalKnxClient#notifyError(Throwable)} after client close
     */
    @Test
    @DisplayName("Internal Client: Test plug-in notification after close")
    public void testPlugInNotificationAfterShutdown() {
        // observer plug-in
        final var observerPlugin = mock(ObserverPlugin.class);

        final var configMock = createConfigMock();
        when(configMock.getPlugins()).thenReturn(Collections.singletonList(observerPlugin));

        final var client = new InternalKnxClient(configMock);
        client.close();

        // should not be an issue (silently ignored, plug-in should never be called)
        client.notifyError(new Throwable("Test from testPlugInNotificationAfterShutdown"));
        verify(observerPlugin, never()).onError(any());
    }

    /**
     * Test {@link InternalKnxClient#notifyError(Throwable)} when calling an erroneous plug-in.
     * <p/>
     * In case an exception is thrown by plug-in the KNX client should still be alive.
     */
    @Test
    @DisplayName("Internal Client: Test erroneous plug-in")
    public void testErroneousPlugIn() {
        // erroneous plug-in
        final var erroneousPlugin = mock(ObserverPlugin.class);
        doThrow(new RuntimeException()).when(erroneousPlugin).onError(any());

        final var configMock = createConfigMock();
        when(configMock.getPlugins()).thenReturn(Collections.singletonList(erroneousPlugin));

        final var client = new InternalKnxClient(configMock);

        try (client) {
            // should not be an issue
            client.notifyError(new Throwable());
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // verify that onError has been invoked
        assertThat(client.getStatistic().getNumberOfErrors()).isEqualTo(1);
    }

    /**
     * Verify sending request body without any channel information (neither {@link ControlChannelRelated} nor
     * {@link DataChannelRelated})
     */
    @Test
    @DisplayName("Error: Send body without any channel information")
    public void testBodyWithoutChannel() {
        final var client = new InternalKnxClient(createConfigMock());
        final var requestBody = mock(RequestBody.class);

        // verify if it is returning Illegal Argument Exception
        assertThatThrownBy(() -> client.send(requestBody))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No channel relation defined for body.");
        assertThatThrownBy(() -> client.send(requestBody, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No channel relation defined for body.");
    }

    /**
     * Creates a {@link Config} for testing
     *
     * @return a mocked instance of {@link Config}
     */
    private Config createConfigMock() {
        final var configMock = mock(Config.class);
        when(configMock.getCommunicationExecutorPoolSize()).thenReturn(1);
        when(configMock.getPluginExecutorPoolSize()).thenReturn(1);
        return configMock;
    }
}
