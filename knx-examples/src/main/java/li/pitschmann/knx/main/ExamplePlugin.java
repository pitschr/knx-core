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

package li.pitschmann.knx.main;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.config.ConfigBuilder;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * Example Three: Plugin
 * <p/>
 * Here we want to simulate a simple audit that prints out data from KNX.
 *
 * @author PITSCHR
 */
public final class ExamplePlugin {
    // disable logging as we only want to print System.out.println(..)
    static {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.OFF);
    }

    public static void main(final String[] args) {
        // we want to monitor the KNX traffic for 60 seconds
        final var endTimeMillis = System.currentTimeMillis() + 60000;

        final var config = ConfigBuilder
                .tunneling()  // communication mode: tunneling
                .plugin(new MyPlugin()) // register my plugin
                .build(); // create immutable config

        // create KNX client and connect to KNX Net/IP device using auto-discovery
        try (final var client = DefaultKnxClient.createStarted(config)) {
            // loop until the time ends
            while (System.currentTimeMillis() < endTimeMillis) {
                System.out.println("Ping ...");
                Sleeper.seconds(3);
            }
        }

        // auto-closed and disconnected by KNX client
    }

    public static class MyPlugin implements ObserverPlugin {

        @Override
        public void onIncomingBody(@Nonnull Body item) {
            System.out.println("Incoming: " + item.getServiceType().getFriendlyName() + " (" + item.getRawDataAsHexString() + ")");
        }

        @Override
        public void onOutgoingBody(@Nonnull Body item) {
            System.out.println("Outgoing: " + item.getServiceType().getFriendlyName() + " (" + item.getRawDataAsHexString() + ")");
        }

        @Override
        public void onError(@Nonnull Throwable throwable) {
            System.out.println("On Error: " + throwable.getMessage());
        }

        @Override
        public void onInitialization(KnxClient client) {
            System.out.println("Initialized by client: " + client);
        }
    }
}
