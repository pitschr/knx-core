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

import li.pitschmann.knx.link.communication.communicator.CommunicatorFactory;
import li.pitschmann.knx.link.communication.task.ConnectResponseTask;
import li.pitschmann.knx.link.communication.task.ConnectionStateResponseTask;
import li.pitschmann.knx.link.communication.task.DescriptionResponseTask;
import li.pitschmann.knx.link.communication.task.DisconnectRequestTask;
import li.pitschmann.knx.link.communication.task.DisconnectResponseTask;
import li.pitschmann.knx.link.communication.task.RoutingIndicationTask;
import li.pitschmann.knx.link.communication.task.SearchResponseTask;
import li.pitschmann.knx.link.communication.task.TunnelingAckTask;
import li.pitschmann.knx.link.communication.task.TunnelingRequestTask;
import li.pitschmann.knx.link.config.Config;
import li.pitschmann.knx.test.TestHelpers;
import li.pitschmann.utils.Networker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link CommunicatorFactory}
 *
 * @author PITSCHR
 */
public class CommunicatorFactoryTest {
    /**
     * Test constructor of {@link CommunicatorFactory}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(CommunicatorFactory.class);
    }

    /**
     * Test {@link CommunicatorFactory#newDiscoveryChannelCommunicator(InternalKnxClient)}
     */
    @Test
    @DisplayName("Test creating new discovery channel communicator")
    public void testNewDiscoveryCommunicator() {
        final var communicator = CommunicatorFactory.newDiscoveryChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator).isNotNull();
        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(1);
        assertSubscriberClass(communicator.getSubscribers().get(0), SearchResponseTask.class);
    }

    /**
     * Test {@link CommunicatorFactory#newDescriptionChannelCommunicator(InternalKnxClient)}
     */
    @Test
    @DisplayName("Test creating new description channel communicator")
    public void testNewDescriptionCommunicator() {
        final var communicator = CommunicatorFactory.newDescriptionChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator).isNotNull();
        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(1);
        assertSubscriberClass(communicator.getSubscribers().get(0), DescriptionResponseTask.class);
    }

    /**
     * Test {@link CommunicatorFactory#newControlChannelCommunicator(InternalKnxClient)}
     */
    @Test
    @DisplayName("Test creating new control channel communicator (no NAT)")
    public void testNewControlCommunicator() {
        final var communicator = CommunicatorFactory.newControlChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator).isNotNull();
        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(4);
        assertSubscriberClass(communicator.getSubscribers().get(0), ConnectResponseTask.class);
        assertSubscriberClass(communicator.getSubscribers().get(1), ConnectionStateResponseTask.class);
        assertSubscriberClass(communicator.getSubscribers().get(2), DisconnectRequestTask.class);
        assertSubscriberClass(communicator.getSubscribers().get(3), DisconnectResponseTask.class);
    }

    /**
     * Test {@link CommunicatorFactory#newDataChannelCommunicator(InternalKnxClient)}
     */
    @Test
    @DisplayName("Test creating new data channel communicator (no NAT)")
    public void testNewDataCommunicator() {
        final var communicator = CommunicatorFactory.newDataChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator).isNotNull();
        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(2);
        assertSubscriberClass(communicator.getSubscribers().get(0), TunnelingRequestTask.class);
        assertSubscriberClass(communicator.getSubscribers().get(1), TunnelingAckTask.class);
    }

    /**
     * Test {@link CommunicatorFactory#newControlAndDataChannelCommunicator(InternalKnxClient)}
     */
    @Test
    @DisplayName("Test creating new control and data channel communicator (with NAT)")
    public void testNewControlAndDataCommunicator() {
        final var communicator = CommunicatorFactory.newControlAndDataChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator).isNotNull();
        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(6);
        assertSubscriberClass(communicator.getSubscribers().get(0), TunnelingRequestTask.class);
        assertSubscriberClass(communicator.getSubscribers().get(1), TunnelingAckTask.class);
        assertSubscriberClass(communicator.getSubscribers().get(2), ConnectResponseTask.class);
        assertSubscriberClass(communicator.getSubscribers().get(3), ConnectionStateResponseTask.class);
        assertSubscriberClass(communicator.getSubscribers().get(4), DisconnectRequestTask.class);
        assertSubscriberClass(communicator.getSubscribers().get(5), DisconnectResponseTask.class);

    }

    /**
     * Test {@link CommunicatorFactory#newRoutingChannelCommunicator(InternalKnxClient)}
     */
    @Test
    @DisplayName("Test creating new routing channel communicator")
    public void testNewRoutingCommunicator() {
        final var communicator = CommunicatorFactory.newRoutingChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator).isNotNull();
        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(1);
        assertSubscriberClass(communicator.getSubscribers().get(0), RoutingIndicationTask.class);
    }

    /**
     * Return a mocked instance of {@link InternalKnxClient}
     *
     * @return knx client
     */
    private InternalKnxClient mockInternalKnxClient() {
        final var knxClient = mock(InternalKnxClient.class);
        final var config = mock(Config.class);
        final var inetAddress = Networker.getByAddress(224, 0, 1, 0);
        when(knxClient.getConfig()).thenReturn(config);
        when(config.getMulticastChannelAddress()).thenReturn(inetAddress);
        when(config.getCommunicationExecutorPoolSize()).thenReturn(3);
        return knxClient;
    }

    /**
     * Assert the class of subscriber against class {@code type}
     */
    private void assertSubscriberClass(final Flow.Subscriber<?> subscriber, final Class<?> type) {
        try {
            final var field = subscriber.getClass().getDeclaredField("subscriber");
            field.trySetAccessible();
            assertThat(field.get(subscriber)).isInstanceOf(type);
        } catch (ReflectiveOperationException e) {
            fail(e);
            throw new AssertionError(e);
        }
    }
}
