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

package li.pitschmann.knx.link.config;

import li.pitschmann.knx.link.exceptions.KnxConfigurationException;
import li.pitschmann.utils.Networker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * KNX specific Constants
 *
 * @author PITSCHR
 */
public final class ConfigConstants {
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
            null,
            true
    );
    /**
     * Default port for HTTP Daemon (same as Pippo)
     */
    public static final ConfigValue<Integer> HTTP_DAEMON_PORT = new ConfigValue<>(
            "daemon.port.http",
            Integer.class,
            Integer::valueOf,
            () -> 8338,
            Objects::nonNull,
            true
    );
    /**
     * Path to Project file
     */
    public static final ConfigValue<Path> PROJECT_PATH = new ConfigValue<>(
            "daemon.path.knxproj",
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
            Files::isReadable,
            true
    );
    private static final Logger log = LoggerFactory.getLogger(ConfigConstants.class);
    /**
     * Map of lower-cased {@link String} keys and {@link ConfigValue} values
     */
    private static final Map<String, ConfigValue<?>> CONFIG_CONSTANTS;

    static {
        final var map = getConfigValues(ConfigConstants.class);
        CONFIG_CONSTANTS = Collections.unmodifiableMap(map);
    }

    private ConfigConstants() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Returns {@link ConfigValue} for given {@code key}
     *
     * @param key lower-cased
     * @param <T>
     * @return an instance of {@link ConfigValue}, otherwise {@link NullPointerException} will be thrown
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> ConfigValue<T> getConfigValueByKey(final @Nonnull String key) {
        return (ConfigValue<T>) CONFIG_CONSTANTS.get(key.toLowerCase());
    }

    /**
     * Returns an immutable list of constant {@link ConfigValue}
     *
     * @return immutable list of {@link ConfigValue}
     */
    @Nonnull
    public static List<ConfigValue<?>> getConfigValues() {
        return List.copyOf(CONFIG_CONSTANTS.values());
    }

    /**
     * Returns a map of lower-cased {@link String} keys and {@link ConfigValue} values
     *
     * @param clazz class to be scanned
     * @return map with a pair of key as {@link String} (in lower-case) and value as {@link ConfigValue}
     */
    @Nonnull
    private static Map<String, ConfigValue<?>> getConfigValues(final @Nonnull Class<?> clazz) {
        final var map = new HashMap<String, ConfigValue<?>>();

        // get config value fields from current class
        for (final var field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                try {
                    final var obj = field.get(null);
                    if (obj instanceof ConfigValue) {
                        final var ConfigValue = (ConfigValue<?>) obj;
                        map.put(ConfigValue.getKey(), ConfigValue);
                        log.trace("Field '{}' added to map: {}", field.getName(), ConfigValue);
                    }
                } catch (final ReflectiveOperationException e) {
                    throw new KnxConfigurationException("Could not load field '" + field.getName() + "' from class '" + clazz.getName() + "'");
                }
            } else {
                log.trace("Field '{}' ignored because it is not 'static final'", field.getName());
            }
        }

        // get config constants from sub-class
        for (final var subClass : clazz.getClasses()) {
            map.putAll(getConfigValues(subClass));
        }

        return map;
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
                Objects::nonNull,
                true
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
                Objects::nonNull,
                true
        );
        /**
         * Description Channel Port
         */
        public static final ConfigValue<Integer> PORT = new ConfigValue<>(
                "client.communication.description.port",
                Integer.class,
                Integer::valueOf,
                () -> 0,
                Objects::nonNull,
                true
        );
        /**
         * Timeout for Description Channel Socket
         */
        public static final ConfigValue<Long> SOCKET_TIMEOUT = new ConfigValue<>(
                "client.communication.description.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3),
                Objects::nonNull,
                true
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
                Objects::nonNull,
                true
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
                Objects::nonNull,
                true
        );
        /**
         * KNX client shall wait for 1 seconds after sending a DISCONNECT_RESPONSE frame to KNX Net/IP device.
         */
        public static final ConfigValue<Long> RESPONSE_TIMEOUT = new ConfigValue<>(
                "client.communication.disconnect.responseTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(1),
                Objects::nonNull,
                true
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
                Objects::nonNull,
                true
        );
        /**
         * Number of connection state request attempts before KNX connection will be disconnected.
         */
        public static final ConfigValue<Long> CHECK_INTERVAL = new ConfigValue<>(
                "client.communication.connectionState.checkInterval",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(60),
                Objects::nonNull,
                true
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
                Objects::nonNull,
                true
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
                Objects::nonNull,
                true
        );
        /**
         * Timeout for Control Channel Socket
         */
        public static final ConfigValue<Long> SOCKET_TIMEOUT = new ConfigValue<>(
                "client.communication.control.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3),
                Objects::nonNull,
                true
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
                Objects::nonNull,
                true
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
                Objects::nonNull,
                true
        );
        /**
         * Timeout for Data Channel Socket
         */
        public static final ConfigValue<Long> SOCKET_TIMEOUT = new ConfigValue<>(
                "client.communication.data.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3),
                Objects::nonNull,
                true
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
         */
        public static final ConfigValue<InetAddress> ADDRESS = new ConfigValue<>(
                "client.endpoint.address",
                InetAddress.class,
                Networker::getByAddress,
                () -> Networker.getAddressUnbound(),
                Objects::nonNull,
                false
        );
        /**
         * Endpoint Port (of KNX/Net IP device)
         */
        public static final ConfigValue<Integer> PORT = new ConfigValue<>(
                "client.endpoint.port",
                Integer.class,
                Integer::valueOf,
                () -> KNX_PORT,
                Objects::nonNull,
                false
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
                Objects::nonNull,
                true
        );
        /**
         * Multicast Port
         */
        public static final ConfigValue<Integer> PORT = new ConfigValue<>(
                "client.communication.multicast.port",
                Integer.class,
                Integer::valueOf,
                () -> KNX_PORT,
                Objects::nonNull,
                true
        );
        /**
         * Default Time-To-Live (TTL) for multicast communication
         */
        public static final ConfigValue<Integer> TIME_TO_LIVE = new ConfigValue<>(
                "client.communication.multicast.timeToLive",
                Integer.class,
                Integer::valueOf,
                () -> 4,
                Objects::nonNull,
                true
        );
        /**
         * Timeout for Multicast Channel Socket
         */
        public static final ConfigValue<Long> SOCKET_TIMEOUT = new ConfigValue<>(
                "client.communication.multicast.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3),
                Objects::nonNull,
                true
        );

        private Multicast() {
        }
    }

    public static final class Executor {
        /**
         * Default size for Communicator Executor Pool Size
         */
        public static final ConfigValue<Integer> COMMUNICATION_POOL_SIZE = new ConfigValue<>(
                "client.communication.executorPoolSize",
                Integer.class,
                Integer::valueOf,
                () -> 10,
                Objects::nonNull,
                true
        );
        /**
         * Default size for Plugin Executor Pool Size
         */
        public static final ConfigValue<Integer> PLUGIN_POOL_SIZE = new ConfigValue<>(
                "client.plugin.executorPoolSize",
                Integer.class,
                Integer::valueOf,
                () -> 10,
                Objects::nonNull,
                true
        );

        private Executor() {
        }
    }
}
