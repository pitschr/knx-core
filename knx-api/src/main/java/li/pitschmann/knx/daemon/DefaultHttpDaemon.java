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

package li.pitschmann.knx.daemon;

import li.pitschmann.knx.link.Configuration;

import javax.annotation.Nonnull;
import java.net.http.HttpClient;

/**
 * Default HTTP Daemon for KNX communication
 * <p/>
 * It will internally start a KNX client to communicate with the KNX Net/IP device.
 */
public final class DefaultHttpDaemon extends AbstractHttpDaemon {
    private DefaultHttpDaemon(final @Nonnull Configuration configuration) {
        super(configuration);
    }

    /**
     * Creates the KNX Daemon and start it immediately
     *
     * @param configuration
     * @return started KNX Daemon
     */
    public static DefaultHttpDaemon createStarted(final Configuration configuration) {
        final var mockDaemon = new DefaultHttpDaemon(configuration);
        mockDaemon.start();
        return mockDaemon;
    }

    // TODO Remove later - it is used for testing purposes only
    public static void main(String[] args) {
        final var daemon = DefaultHttpDaemon.createStarted(Configuration.create("192.168.1.16").build());
        final var httpClient = HttpClient.newHttpClient();
        daemon.toString();
        httpClient.toString(); // todo something
    }
}
