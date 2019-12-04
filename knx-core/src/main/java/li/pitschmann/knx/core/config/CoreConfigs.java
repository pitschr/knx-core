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

package li.pitschmann.knx.core.config;

import li.pitschmann.knx.core.exceptions.KnxConfigurationException;
import li.pitschmann.knx.core.utils.Configs;
import li.pitschmann.knx.core.utils.Networker;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * KNX specific Constants
 *
 * @author PITSCHR
 */
public final class CoreConfigs {
    /**
     * Standard KNX/IP Port Number
     */
    public static final int KNX_PORT = 3671;
    /**
     * Standard KNX/IP Multicast Address
     */
    public static final InetAddress MULTICAST_ADDRESS = Networker.getByAddress(224, 0, 23, 12);
    /**
     * Default value if NAT should be enabled
     */
    public static final ConfigValue<Boolean> NAT = new ConfigValue<>(
            "client.nat.enabled",
            Boolean.class,
            Boolean::valueOf,
            () -> Boolean.FALSE,
            null
    );
    /**
     * Path to Project file
     */
    public static final ConfigValue<Path> PROJECT_PATH = new ConfigValue<>(
            "project.path",
            Path.class,
            Paths::get,
            () -> {
                // look up for latest *.knxproj file in current directory
                final var dir = Paths.get(".");
                try {
                    return Files.list(dir)
                            .filter(Files::isRegularFile)
                            // filter for *.knxproj file extensions
                            .filter(f -> f.getFileName().toString().toLowerCase().endsWith(".knxproj"))
                            // take the latest file
                            .max(Comparator.comparingLong(f -> f.toFile().lastModified()))
                            // dummy file (mostly non-readable) just to return a non-null value
                            .orElse(Paths.get("knx-client.knxproj"));
                } catch (final IOException e) {
                    throw new KnxConfigurationException("I/O during getting an applicable *.knxproj: "
                            + dir.toFile().getAbsolutePath(), e);
                }
            },
            Files::isReadable
    );
    /**
     * Immutable map of lower-cased {@link String} keys and {@link ConfigValue} values
     */
    private static final Map<String, ConfigValue<Object>> CONFIG_CONSTANTS = Configs.getConfigMapValues(CoreConfigs.class);

    private CoreConfigs() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Returns an immutable list of constant {@link ConfigValue}
     *
     * @return immutable list of {@link ConfigValue}
     */
    public static Map<String, ConfigValue<Object>> getConfigValues() {
        return CONFIG_CONSTANTS;
    }

    public static final class Search {
        /**
         * KNX client shall wait for 10 seconds for a SEARCH_RESPONSE frame from KNX Net/IP device.
         */
        public static final ConfigValue<Long> REQUEST_TIMEOUT = new ConfigValue<>(
                "client.communication.search.requestTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(10),
                Objects::nonNull
        );

        private Search() {
        }
    }

    public static final class Description {
        /**
         * KNX client shall wait for 10 seconds for a DESCRIPTION_RESPONSE frame from KNX Net/IP device.
         */
        public static final ConfigValue<Long> REQUEST_TIMEOUT = new ConfigValue<>(
                "client.communication.description.requestTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(10),
                Objects::nonNull
        );
        /**
         * Description Channel Port
         */
        public static final ConfigValue<Integer> PORT = new ConfigValue<>(
                "client.communication.description.port",
                Integer.class,
                Integer::valueOf,
                () -> 0,
                Objects::nonNull
        );
        /**
         * Timeout for Description Channel Socket
         */
        public static final ConfigValue<Long> SOCKET_TIMEOUT = new ConfigValue<>(
                "client.communication.description.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3),
                Objects::nonNull
        );

        private Description() {
        }
    }

    public static final class Connect {
        /**
         * KNX client shall wait for 10 seconds for a CONNECT_RESPONSE frame from KNX Net/IP device.
         */
        public static final ConfigValue<Long> REQUEST_TIMEOUT = new ConfigValue<>(
                "client.communication.connect.requestTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(10),
                Objects::nonNull
        );

        private Connect() {
        }
    }

    public static final class Disconnect {
        /**
         * KNX client shall wait for 5 seconds for a DISCONNECT_RESPONSE frame from KNX Net/IP device.
         */
        public static final ConfigValue<Long> REQUEST_TIMEOUT = new ConfigValue<>(
                "client.communication.disconnect.requestTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(5),
                Objects::nonNull
        );
        /**
         * KNX client shall wait for 1 seconds after sending a DISCONNECT_RESPONSE frame to KNX Net/IP device.
         */
        public static final ConfigValue<Long> RESPONSE_TIMEOUT = new ConfigValue<>(
                "client.communication.disconnect.responseTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(1),
                Objects::nonNull
        );

        private Disconnect() {
        }
    }

    public static final class ConnectionState {
        /**
         * KNX client shall wait for 10 seconds for a CONNECTION_STATE_RESPONSE frame from KNX Net/IP device.
         */
        public static final ConfigValue<Long> REQUEST_TIMEOUT = new ConfigValue<>(
                "client.communication.connectionState.requestTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(10),
                Objects::nonNull
        );
        /**
         * Number of connection state request attempts before KNX connection will be disconnected.
         */
        public static final ConfigValue<Long> CHECK_INTERVAL = new ConfigValue<>(
                "client.communication.connectionState.checkInterval",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(60),
                Objects::nonNull
        );

        /**
         * If the KNX Net/IP device does not receive a heartbeat request within 120 seconds of the last correctly
         * received message frame, the server shall terminate the connection by sending a DISCONNECT_REQUEST to the
         * client’s control endpoint.
         */
        public static final ConfigValue<Long> HEARTBEAT_TIMEOUT = new ConfigValue<>(
                "client.communication.connectionState.heartbeatTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(120),
                Objects::nonNull
        );

        private ConnectionState() {
        }
    }

    public static final class Control {
        /**
         * Control Channel Port
         */
        public static final ConfigValue<Integer> PORT = new ConfigValue<>(
                "client.communication.control.port",
                Integer.class,
                Integer::valueOf,
                () -> 0,
                Objects::nonNull
        );
        /**
         * Timeout for Control Channel Socket
         */
        public static final ConfigValue<Long> SOCKET_TIMEOUT = new ConfigValue<>(
                "client.communication.control.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3),
                Objects::nonNull
        );

        private Control() {
        }
    }

    public static final class Data {
        /**
         * Data Channel Port
         */
        public static final ConfigValue<Integer> PORT = new ConfigValue<>(
                "client.communication.data.port",
                Integer.class,
                Integer::valueOf,
                () -> 0,
                Objects::nonNull
        );
        /**
         * KNX client shall wait for 1 second for a TUNNELING_ACK response on a TUNNELING_REQUEST frame from
         * KNX Net/IP device.
         */
        public static final ConfigValue<Long> DATA_REQUEST_TIMEOUT = new ConfigValue<>(
                "client.communication.data.requestTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(1),
                Objects::nonNull
        );
        /**
         * Timeout for Data Channel Socket
         */
        public static final ConfigValue<Long> SOCKET_TIMEOUT = new ConfigValue<>(
                "client.communication.data.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3),
                Objects::nonNull
        );

        private Data() {
        }
    }

    public static final class Event {
        /**
         * Interval in milliseconds of look up for event pool (e.g. if request/ack received)
         */
        public static final long CHECK_INTERVAL = 10L;
        /**
         * Number of total attempts to be retried when no requested packet was received
         */
        public static final int TOTAL_ATTEMPTS = 3;
        /**
         * Timeout for status look up in milliseconds
         */
        public static final long STATUS_LOOKUP_TIMEOUT = 3000L;

        private Event() {
        }
    }

    public static final class Endpoint {
        /**
         * Endpoint Address (of KNX/Net IP device)
         * <p/>
         * <strong>This setting is reserved for internal purpose only!</strong>
         */
        public static final InternalConfigValue<InetAddress> ADDRESS = new InternalConfigValue<>(
                "client.endpoint.address",
                InetAddress.class,
                Networker::getByAddress,
                Networker::getAddressUnbound,
                Objects::nonNull
        );
        /**
         * Endpoint Port (of KNX/Net IP device)
         * <p/>
         * <strong>This setting is reserved for internal purpose only!</strong>
         */
        public static final InternalConfigValue<Integer> PORT = new InternalConfigValue<>(
                "client.endpoint.port",
                Integer.class,
                Integer::valueOf,
                () -> KNX_PORT,
                Objects::nonNull
        );

        private Endpoint() {
        }
    }

    public static final class Multicast {
        /**
         * Multicast Address
         */
        public static final ConfigValue<InetAddress> ADDRESS = new ConfigValue<>(
                "client.communication.multicast.address",
                InetAddress.class,
                Networker::getByAddress,
                () -> MULTICAST_ADDRESS,
                Objects::nonNull
        );
        /**
         * Multicast Port
         */
        public static final ConfigValue<Integer> PORT = new ConfigValue<>(
                "client.communication.multicast.port",
                Integer.class,
                Integer::valueOf,
                () -> KNX_PORT,
                Objects::nonNull
        );
        /**
         * Default Time-To-Live (TTL) for multicast communication
         */
        public static final ConfigValue<Integer> TIME_TO_LIVE = new ConfigValue<>(
                "client.communication.multicast.timeToLive",
                Integer.class,
                Integer::valueOf,
                () -> 4,
                Objects::nonNull
        );
        /**
         * Timeout for Multicast Channel Socket
         */
        public static final ConfigValue<Long> SOCKET_TIMEOUT = new ConfigValue<>(
                "client.communication.multicast.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3),
                Objects::nonNull
        );

        private Multicast() {
        }
    }

    public static final class Communication {
        /**
         * Default size for Communicator Executor Pool Size
         */
        public static final ConfigValue<Integer> EXECUTOR_POOL_SIZE = new ConfigValue<>(
                "client.communication.executorPoolSize",
                Integer.class,
                Integer::valueOf,
                () -> 10,
                Objects::nonNull
        );

        private Communication() {
        }
    }

    public static final class Plugin {
        /**
         * Default size for Plugin Executor Pool Size
         */
        public static final ConfigValue<Integer> EXECUTOR_POOL_SIZE = new ConfigValue<>(
                "client.plugin.executorPoolSize",
                Integer.class,
                Integer::valueOf,
                () -> 10,
                Objects::nonNull
        );
        /**
         * Timeout in milliseconds until when the Plugin should be initialized
         * before accepted by PluginManager
         */
        public static final ConfigValue<Long> INITIALIZATION_TIMEOUT = new ConfigValue<>(
                "client.plugin.initializationTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(10),
                Objects::nonNull
        );

        private Plugin() {
        }
    }
}
