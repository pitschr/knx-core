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

package li.pitschmann.knx.test;

import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.ConnectionStateResponseBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.SearchRequestBody;
import li.pitschmann.knx.link.body.SearchResponseBody;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.communication.InternalKnxEventPool;
import li.pitschmann.knx.link.communication.InternalKnxStatusPool;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.communication.event.KnxMultiEvent;
import li.pitschmann.knx.link.communication.event.KnxSingleEvent;
import li.pitschmann.knx.link.config.Config;
import li.pitschmann.knx.link.config.ConfigValue;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TestHelpers {
    private TestHelpers() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Assert that the given {@link Class} is not instantiable and an {@link AssertionError} thrown is expected
     *
     * @param classToTest
     */
    public static void assertThatNotInstantiable(final Class<?> classToTest) {
        // DPT21Value should not be instantiable
        assertThatThrownBy(() -> {
            final var ctor = classToTest.getDeclaredConstructor();
            ctor.trySetAccessible();
            ctor.newInstance();
        }).hasCauseInstanceOf(AssertionError.class);
    }

    /**
     * Returns prepared {@link Config} without special customization.
     *
     * @return
     */
    @Nonnull
    public static Config mockConfig() {
        return mockConfig(x -> {
        });
    }

    /**
     * Returns prepared {@link Config} with ability to customize it.
     *
     * @param configMockConsumer
     * @return
     */
    @Nonnull
    public static Config mockConfig(final @Nonnull Consumer<Config> configMockConsumer) {
        @SuppressWarnings("unchecked")
        final var configValueClass = (Class<ConfigValue<?>>) (Object) ConfigValue.class;

        // create config mock
        final var configMock = mock(Config.class);
        when(configMock.getValue(any(configValueClass))).thenAnswer(i -> ((ConfigValue<?>) i.getArgument(0)).getDefaultValue());
        configMockConsumer.accept(configMock);
        return configMock;
    }

    /**
     * Returns prepared {@link KnxClient} implementation with ability
     * to customize {@link Config}
     *
     * @param configMockConsumer
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends KnxClient> T mockKnxClient(final @Nonnull Consumer<Config> configMockConsumer,
                                                        final @Nonnull Consumer<T> knxClientMockSupplier,
                                                        final @Nonnull Class<T> clazz) {
        final var knxClientMock = mock(Objects.requireNonNull(clazz));
        final var configMock = mockConfig(configMockConsumer);
        final var statusPoolMock = mockInternalStatusPool();

        when(knxClientMock.getConfig()).thenReturn(configMock);
        when(knxClientMock.getConfig(any())).thenCallRealMethod();
        when(knxClientMock.getStatusPool()).thenReturn(statusPoolMock);
        knxClientMockSupplier.accept(knxClientMock);

        return knxClientMock;
    }

    /**
     * Returns prepared {@link InternalKnxClient} with no customization.
     * @return
     */
    @Nonnull
    public static InternalKnxClient mockInternalKnxClient() {
        return mockInternalKnxClient(config -> {}, client -> {});
    }

    /**
     * Returns prepared {@link InternalKnxClient} with ability to customize
     * the {@link Config} and {@link InternalKnxClient}.
     *
     * @param configMockConsumer
     * @param knxClientMockSupplier
     * @return
     */
    @Nonnull
    public static InternalKnxClient mockInternalKnxClient(final @Nonnull Consumer<Config> configMockConsumer,
                                                          final @Nonnull Consumer<InternalKnxClient> knxClientMockSupplier) {
        final var knxClientMock = mock(InternalKnxClient.class);
        final var configMock = mockConfig(configMockConsumer);
        final var statusPoolMock = mockInternalStatusPool();
        final var eventPoolMock = mockInternalEventPool();

        when(knxClientMock.getConfig()).thenReturn(configMock);
        when(knxClientMock.getConfig(any())).thenCallRealMethod();
        when(knxClientMock.getStatusPool()).thenReturn(statusPoolMock);
        when(knxClientMock.getEventPool()).thenReturn(eventPoolMock);

        knxClientMockSupplier.accept(knxClientMock);
        return knxClientMock;
    }

    /**
     * Returns prepared {@link InternalKnxStatusPool}
     * @return
     */
    private static InternalKnxStatusPool mockInternalStatusPool() {
        return mock(InternalKnxStatusPool.class);
    }

    /**
     * Returns prepared {@link InternalKnxEventPool}
     * @return
     */
    private static InternalKnxEventPool mockInternalEventPool() {
        // Events + Event Pool
        final var eventPool = mock(InternalKnxEventPool.class);

        @SuppressWarnings("unchecked")
        final var connectionStateEvent = (KnxSingleEvent<ConnectionStateRequestBody, ConnectionStateResponseBody>) mock(KnxSingleEvent.class);
        when(eventPool.connectionStateEvent()).thenReturn(connectionStateEvent);

        @SuppressWarnings("unchecked")
        final var connectEvent = (KnxSingleEvent<ConnectRequestBody, ConnectResponseBody>) mock(KnxSingleEvent.class);
        when(eventPool.connectEvent()).thenReturn(connectEvent);

        @SuppressWarnings("unchecked")
        final var descriptionEvent = (KnxSingleEvent<DescriptionRequestBody, DescriptionResponseBody>) mock(KnxSingleEvent.class);
        when(eventPool.descriptionEvent()).thenReturn(descriptionEvent);

        @SuppressWarnings("unchecked")
        final var disconnectEvent = (KnxSingleEvent<DisconnectRequestBody, DisconnectResponseBody>) mock(KnxSingleEvent.class);
        when(eventPool.disconnectEvent()).thenReturn(disconnectEvent);

        @SuppressWarnings("unchecked")
        final var searchEvent = (KnxMultiEvent<SearchRequestBody, SearchResponseBody>) mock(KnxMultiEvent.class);
        when(eventPool.searchEvent()).thenReturn(searchEvent);

        @SuppressWarnings("unchecked")
        final var eventData = (KnxSingleEvent<TunnelingRequestBody, TunnelingAckBody>) mock(KnxSingleEvent.class);
        when(eventPool.get(any(TunnelingAckBody.class))).thenReturn(eventData);

        return eventPool;
    }
}
