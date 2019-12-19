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

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.config.Config;
import li.pitschmann.knx.core.config.ConfigBuilder;

/**
 * Default KNX client implementation
 *
 * @author PITSCHR
 */
public final class DefaultKnxClient extends BaseKnxClient {

    /**
     * Starts Default KNX client with {@link Config}
     *
     * @param config
     */
    private DefaultKnxClient(final Config config) {
        super(config);
    }

    /**
     * Creates the Default KNX Client without endpoint address. The look up for an
     * applicable KNX Net/IP device will be using KNX discovery service on a broadcast
     * address according to the KXN specification.
     *
     * @return
     */
    public static DefaultKnxClient createStarted() {
        return createStarted("");
    }

    /**
     * Creates the Default KNX Client with pre-defined endpoint {@code address}
     *
     * @param address
     * @return
     */
    public static DefaultKnxClient createStarted(final @Nullable String address) {
        return createStarted(ConfigBuilder.create(address).build());
    }

    /**
     * Creates the Default KNX Client with {@link Config} instance
     *
     * @param config
     * @return
     */
    public static DefaultKnxClient createStarted(final Config config) {
        final var client = new DefaultKnxClient(config);
        // start communication
        client.getInternalClient().start();
        return client;
    }
}
