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

package li.pitschmann.knx.core.communication.communicator;

import li.pitschmann.knx.core.communication.InternalKnxClient;
import li.pitschmann.knx.core.communication.task.ConnectResponseTask;
import li.pitschmann.knx.core.communication.task.ConnectionStateResponseTask;
import li.pitschmann.knx.core.communication.task.DescriptionResponseTask;
import li.pitschmann.knx.core.communication.task.DisconnectRequestTask;
import li.pitschmann.knx.core.communication.task.DisconnectResponseTask;
import li.pitschmann.knx.core.communication.task.RoutingIndicationTask;
import li.pitschmann.knx.core.communication.task.SearchResponseTask;
import li.pitschmann.knx.core.communication.task.TunnelingAckTask;
import li.pitschmann.knx.core.communication.task.TunnelingRequestTask;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.test.TestHelpers;
import li.pitschmann.knx.core.utils.Networker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link CommunicatorFactory}
 *
 * @author PITSCHR
 */
class CommunicatorFactoryTest {

    @Test
    @DisplayName("Constructor not instantiable")
    void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(CommunicatorFactory.class);
    }

    @Test
    @DisplayName("Test creating new discovery channel communicator")
    void testNewDiscoveryCommunicator() {
        final var communicator = CommunicatorFactory.newDiscoveryChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator).isNotNull();
        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(1);
        assertThat(communicator.getSubscribers().get(0)).isInstanceOf(SearchResponseTask.class);
    }

    @Test
    @DisplayName("Test creating new description channel communicator")
    void testNewDescriptionCommunicator() {
        final var communicator = CommunicatorFactory.newDescriptionChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator).isNotNull();
        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(1);
        assertThat(communicator.getSubscribers().get(0)).isInstanceOf(DescriptionResponseTask.class);
    }

    @Test
    @DisplayName("Test creating new control channel communicator (no NAT)")
    void testNewControlCommunicator() {
        final var communicator = CommunicatorFactory.newControlChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator).isNotNull();
        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(4);
        assertThat(communicator.getSubscribers().stream().map(Object::getClass).toArray()).containsExactly(
                ConnectResponseTask.class,
                ConnectionStateResponseTask.class,
                DisconnectRequestTask.class,
                DisconnectResponseTask.class
        );
    }

    @Test
    @DisplayName("Test creating new data channel communicator (no NAT)")
    void testNewDataCommunicator() {
        final var communicator = CommunicatorFactory.newDataChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(2);
        assertThat(communicator.getSubscribers().stream().map(Object::getClass).toArray()).containsExactly(
                TunnelingRequestTask.class,
                TunnelingAckTask.class
        );
    }

    @Test
    @DisplayName("Test creating new control and data channel communicator (with NAT)")
    void testNewControlAndDataCommunicator() {
        final var communicator = CommunicatorFactory.newControlAndDataChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(6);
        assertThat(communicator.getSubscribers().stream().map(Object::getClass).toArray()).containsExactly(
                TunnelingRequestTask.class,
                TunnelingAckTask.class,
                ConnectResponseTask.class,
                ConnectionStateResponseTask.class,
                DisconnectRequestTask.class,
                DisconnectResponseTask.class
        );
    }

    @Test
    @DisplayName("Test creating new routing channel communicator")
    void testNewRoutingCommunicator() {
        final var communicator = CommunicatorFactory.newRoutingChannelCommunicator(mockInternalKnxClient());

        assertThat(communicator.getNumberOfSubscribers()).isEqualTo(1);
        assertThat(communicator.getSubscribers().get(0)).isInstanceOf(RoutingIndicationTask.class);
    }

    /**
     * Return a mocked instance of {@link InternalKnxClient}
     *
     * @return knx client
     */
    private InternalKnxClient mockInternalKnxClient() {
        return TestHelpers.mockInternalKnxClient(
                configMock -> {
                    when(configMock.getValue(CoreConfigs.Multicast.PORT)).thenReturn(0);
                    when(configMock.getValue(CoreConfigs.Multicast.ADDRESS)).thenReturn(
                            Networker.getByAddress(224, 0, 1, 0)
                    );
                },
                clientMock -> {
                }
        );
    }
}
