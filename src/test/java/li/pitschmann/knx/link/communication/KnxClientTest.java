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
import li.pitschmann.knx.link.body.TunnellingAckBody;
import li.pitschmann.knx.link.body.TunnellingRequestBody;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.test.KnxBody;
import li.pitschmann.test.KnxMockServer;
import li.pitschmann.test.KnxTest;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

/**
 * Test for {@link InternalKnxClient}
 *
 * @author PITSCHR
 */
public class KnxClientTest {
    private static Logger LOG = LoggerFactory.getLogger(KnxClientTest.class);

    /**
     * Test following methods indirectly (inside {@link InternalKnxClient)}:
     * <ul>
     * <li>{@code InternalKnxClient#notifyPlugins(Object, List, BiConsumer)}
     * (internally used for initialization, start and shutdown)</li>
     * <li>{@link InternalKnxClient#notifyPluginsIncomingBody(Body)}</li>
     * <li>{@link InternalKnxClient#notifyPluginsOutgoingBody(Body)}</li>
     * <li>{@link InternalKnxClient#notifyPluginsError(Throwable)}</li>
     * </ul>
     */
    @KnxTest({
            // On first request send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody)
            "WAIT=NEXT",
            // send ConnectResponseBody
            KnxBody.CONNECT_RESPONSE,
            // wait for next packet (will be: ConnetionStateRequestBody)
            "WAIT=NEXT",
            // ConnectionStateResponseBody
            KnxBody.CONNECTION_STATE_RESPONSE,
            // send TunnellingRequestBody
            KnxBody.TUNNELLING_REQUEST,
            // send an erroneous body
            KnxBody.Failures.CONNECT_RESPONSE_BAD_DATA,
            // send an erroneous body #2
            KnxBody.Failures.CONNECT_RESPONSE_BAD_DATA,
            // wait for packet with type 'DisconnectRequestBody'
            "WAIT=DISCONNECT_REQUEST",
            // send DisconnectResponseBody
            KnxBody.DISCONNECT_RESPONSE
    })
    @DisplayName("Default Client: Test notification of plug-ins")
    public void testPlugins(final KnxMockServer mockServer) {
        var configBuilder = mockServer.newConfigBuilder();

        // observer plug-in
        var observerPlugin = Mockito.mock(ObserverPlugin.class);
        configBuilder.plugin(observerPlugin);

        // extension plug-in
        var extensionPlugin = Mockito.mock(ExtensionPlugin.class);
        configBuilder.plugin(extensionPlugin);

        try (var client = new DefaultKnxClient(configBuilder.build())) {
            // wait for first tunnelling ack body sent by client
            mockServer.waitForReceivedServiceType(ServiceType.TUNNELING_ACK);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // verify number of notifications to observer plug-in
        Mockito.verify(observerPlugin, Mockito.times(1)).onInitialization(Mockito.any());

        Mockito.verify(observerPlugin, Mockito.times(5)).onIncomingBody(Mockito.any());
        Mockito.verify(observerPlugin, Mockito.times(1)).onIncomingBody(Mockito.isA(DescriptionResponseBody.class));
        Mockito.verify(observerPlugin, Mockito.times(1)).onIncomingBody(Mockito.isA(ConnectResponseBody.class));
        Mockito.verify(observerPlugin, Mockito.times(1)).onIncomingBody(Mockito.isA(ConnectionStateResponseBody.class));
        Mockito.verify(observerPlugin, Mockito.times(1)).onIncomingBody(Mockito.isA(TunnellingRequestBody.class));
        Mockito.verify(observerPlugin, Mockito.times(1)).onIncomingBody(Mockito.isA(DisconnectResponseBody.class));

        Mockito.verify(observerPlugin, Mockito.times(5)).onOutgoingBody(Mockito.any());
        Mockito.verify(observerPlugin, Mockito.times(1)).onOutgoingBody(Mockito.isA(DescriptionRequestBody.class));
        Mockito.verify(observerPlugin, Mockito.times(1)).onOutgoingBody(Mockito.isA(ConnectRequestBody.class));
        Mockito.verify(observerPlugin, Mockito.times(1)).onOutgoingBody(Mockito.isA(ConnectionStateRequestBody.class));
        Mockito.verify(observerPlugin, Mockito.times(1)).onOutgoingBody(Mockito.isA(TunnellingAckBody.class));
        Mockito.verify(observerPlugin, Mockito.times(1)).onOutgoingBody(Mockito.isA(DisconnectRequestBody.class));

        Mockito.verify(observerPlugin, Mockito.times(2)).onError(Mockito.any());

        // verify number of notifications to extension plug-in
        Mockito.verify(extensionPlugin, Mockito.times(1)).onInitialization(Mockito.any());
        Mockito.verify(extensionPlugin, Mockito.times(1)).onStart();
        Mockito.verify(extensionPlugin, Mockito.times(1)).onShutdown();

        // assert packets
        mockServer.assertReceivedPackets(
                DescriptionRequestBody.class,
                ConnectRequestBody.class,
                ConnectionStateRequestBody.class,
                TunnellingAckBody.class,
                DisconnectRequestBody.class);
    }

    /**
     * Test {@link InternalKnxClient#notifyPluginsError(Throwable)} after client close
     */
    @KnxTest(KnxBody.Sequences.MINIMAL_DISCONNECT_BY_CLIENT)
    @DisplayName("Internal Client: Test plug-in notification after close")
    public void testPlugInNotificationAfterShutdown(final KnxMockServer mockServer) {
        var configBuilder = mockServer.newConfigBuilder();

        // observer plug-in
        var observerPlugin = Mockito.mock(ObserverPlugin.class);
        configBuilder.plugin(observerPlugin);

        var client = new InternalKnxClient(configBuilder.build());
        try (client) {
            // internal client must be invoked explicitly
            client.start();
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // should not be an issue (silently ignored, plug-in should never be called)
        client.notifyPluginsError(new Throwable("Test from testPlugInNotificationAfterShutdown"));
        Mockito.verify(observerPlugin, Mockito.never()).onError(Mockito.any());
    }

    /**
     * Test {@link InternalKnxClient#notifyPluginsError(Throwable)} when calling an erroneous plug-in.
     * <p/>
     * In case an exception is thrown by plug-in the KNX client should still be alive.
     */
    @KnxTest(KnxBody.Sequences.MINIMAL_DISCONNECT_BY_CLIENT)
    @DisplayName("Internal Client: Test erroneous plug-in")
    public void testErroneousPlugIn(final KnxMockServer mockServer) {
        var configBuilder = mockServer.newConfigBuilder();

        // erroneous plug-in
        var erroneousPlugin = Mockito.mock(ObserverPlugin.class);
        configBuilder.plugin(erroneousPlugin);
        Mockito.doThrow(new RuntimeException()).when(erroneousPlugin).onError(Mockito.any());

        try (var client = new InternalKnxClient(configBuilder.build())) {
            // internal client must be invoked explicitly
            client.start();
            // should not be an issue
            client.notifyPluginsError(new Throwable());
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }
    }

    /**
     * Test {@link InternalKnxClient#getControlHPAI()} and {@link InternalKnxClient#getDataHPAI()}
     */
    @KnxTest(KnxBody.Sequences.MINIMAL_DISCONNECT_BY_CLIENT)
    @DisplayName("Default Client: Test Control and Data HPAI")
    public void testControlAndDataHPAI(final KnxMockServer mockServer) {
        try (var client = new InternalKnxClient(mockServer.newConfigBuilder().build())) {
            // internal client must be invoked explicitly
            client.start();

            var controlHPAI = client.getControlHPAI();
            assertThat(controlHPAI).isNotNull().isEqualTo(mockServer.getClientControlHPAI());

            var dataHPAI = client.getDataHPAI();
            assertThat(dataHPAI).isNotNull().isEqualTo(mockServer.getClientDataHPAI());

            // and should be different
            assertThat(controlHPAI).isNotSameAs(dataHPAI);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }
    }

    /**
     * Verify sending request body without any channel information (neither {@link ControlChannelRelated} nor
     * {@link DataChannelRelated})
     *
     * @param mockServer
     */
    @KnxTest(KnxBody.Sequences.MINIMAL_DISCONNECT_BY_CLIENT)
    @DisplayName("Error: Send body without any channel information")
    public void testBodyWithoutChannel(final KnxMockServer mockServer) {
        try (var client = mockServer.newKnxClient()) {
            var requestBody = Mockito.mock(RequestBody.class);

            // verify if it is returning Illegal Argument Exception
            assertThatThrownBy(() -> client.send(requestBody)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("No channel relation defined for body.");
            assertThatThrownBy(() -> client.send(requestBody, 0)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("No channel relation defined for body.");
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }
    }

    /**
     * Verify if {@link KnxClient#getStatistic()} is an unmodifiable instance of {@link KnxStatistic}
     *
     * @param mockServer
     */
    @KnxTest(KnxBody.Sequences.MINIMAL_DISCONNECT_BY_CLIENT)
    @DisplayName("Check if KNX statistic is an unmodifiable instance")
    public void testIfKnxStatisticIsUnmodifiable(final KnxMockServer mockServer) {
        try (var client = mockServer.newKnxClient()) {
            var knxStatisticUnmodifiable = client.getStatistic();
            assertThat(knxStatisticUnmodifiable.getClass().getSimpleName()).isEqualTo("UnmodifiableKnxStatistic");
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }
    }
}
