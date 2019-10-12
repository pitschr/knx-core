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
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    public static final ConfigConstant<Boolean> NAT = new ConfigConstant<>(
            "client.nat.enabled",
            Boolean.class,
            Boolean::valueOf,
            () -> Boolean.FALSE
    );
    /**
     * Default port for HTTP Daemon (same as Pippo)
     */
    public static final ConfigConstant<Integer> HTTP_DAEMON_PORT = new ConfigConstant<>(
            "daemon.port.http",
            Integer.class,
            Integer::valueOf,
            () -> 8338
    );
    /**
     * Path to Project file
     */
    public static final ConfigConstant<Path> PROJECT_PATH = new ConfigConstant<>(
            "daemon.path.knxproj",
            Path.class,
            Paths::get,
            () -> Paths.get("knx-client.knxproj")
    );
    private static final Logger log = LoggerFactory.getLogger(ConfigConstants.class);
    /**
     * Map of lower-cased {@link String} keys and {@link ConfigConstant} values
     */
    private static final Map<String, ConfigConstant<?>> CONFIG_CONSTANTS;
    /**
     * Map of lower-cased {@link String} keys and {@link Object} default values
     */
    private static final Map<String, Object> CONFIG_CONSTANT_DEFAULT_VALUES;

    static {
        final var map = getConfigConstants(ConfigConstants.class);
        CONFIG_CONSTANTS = Collections.unmodifiableMap(map);
        CONFIG_CONSTANT_DEFAULT_VALUES = map.entrySet().stream().collect(
                Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getDefaultValue()
                )
        );
    }

    private ConfigConstants() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Returns {@link ConfigConstant} for given {@code key}
     *
     * @param key lower-cased
     * @param <T>
     * @return an instance of {@link ConfigConstant}, otherwise {@link NullPointerException} will be thrown
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> ConfigConstant<T> getConfigConstantByKey(final @Nonnull String key) {
        return (ConfigConstant<T>) CONFIG_CONSTANTS.get(key.toLowerCase());
    }

    /**
     * Returns a map of key/values of {@link ConfigConstant}
     *
     * @return map with config constant values
     */
    @Nonnull
    public static Map<String, Object> getConfigConstants() {
        return CONFIG_CONSTANT_DEFAULT_VALUES;
    }

    /**
     * Returns a map of lower-cased {@link String} keys and {@link ConfigConstant} values
     *
     * @param clazz class to be scanned
     * @return map with a pair of key as {@link String} (in lower-case) and value as {@link ConfigConstant}
     */
    @Nonnull
    private static Map<String, ConfigConstant<?>> getConfigConstants(final @Nonnull Class<?> clazz) {
        final var map = new HashMap<String, ConfigConstant<?>>();

        // get config value fields from current class
        for (final var field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                try {
                    final var obj = field.get(null);
                    if (obj instanceof ConfigConstant) {
                        final var configConstant = (ConfigConstant<?>) obj;
                        map.put(configConstant.getKey(), configConstant);
                        log.trace("Field '{}' added to map: {}", field.getName(), configConstant);
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
            map.putAll(getConfigConstants(subClass));
        }

        return map;
    }

    public static final class Search {
        /**
         * KNX client shall wait for 10 seconds for a SEARCH_RESPONSE frame from KNX Net/IP device.
         */
        public static final ConfigConstant<Long> REQUEST_TIMEOUT = new ConfigConstant<>(
                "client.communication.search.requestTimeout",
                Long.class,

                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(10)
        );

        private Search() {
        }
    }

    public static final class Description {
        /**
         * KNX client shall wait for 10 seconds for a DESCRIPTION_RESPONSE frame from KNX Net/IP device.
         */
        public static final ConfigConstant<Long> REQUEST_TIMEOUT = new ConfigConstant<>(
                "client.communication.description.requestTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(10)
        );
        /**
         * Description Channel Port
         */
        public static final ConfigConstant<Integer> PORT = new ConfigConstant<>(
                "client.communication.description.port",
                Integer.class,
                Integer::valueOf,
                () -> 0
        );
        /**
         * Timeout for Description Channel Socket
         */
        public static final ConfigConstant<Long> SOCKET_TIMEOUT = new ConfigConstant<>(
                "client.communication.description.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3)
        );

        private Description() {
        }
    }

    public static final class Connect {
        /**
         * KNX client shall wait for 10 seconds for a CONNECT_RESPONSE frame from KNX Net/IP device.
         */
        public static final ConfigConstant<Long> REQUEST_TIMEOUT = new ConfigConstant<>(
                "client.communication.connect.requestTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(10)
        );

        private Connect() {
        }
    }

    public static final class Disconnect {
        /**
         * KNX client shall wait for 5 seconds for a DISCONNECT_RESPONSE frame from KNX Net/IP device.
         */
        public static final ConfigConstant<Long> REQUEST_TIMEOUT = new ConfigConstant<>(
                "client.communication.disconnect.requestTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(5)
        );
        /**
         * KNX client shall wait for 1 seconds after sending a DISCONNECT_RESPONSE frame to KNX Net/IP device.
         */
        public static final ConfigConstant<Long> RESPONSE_TIMEOUT = new ConfigConstant<>(
                "client.communication.disconnect.responseTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(1)
        );

        private Disconnect() {
        }
    }

    public static final class ConnectionState {
        /**
         * KNX client shall wait for 10 seconds for a CONNECTION_STATE_RESPONSE frame from KNX Net/IP device.
         */
        public static final ConfigConstant<Long> REQUEST_TIMEOUT = new ConfigConstant<>(
                "client.communication.connectionState.requestTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(10)
        );
        /**
         * Number of connection state request attempts before KNX connection will be disconnected.
         */
        public static final ConfigConstant<Long> CHECK_INTERVAL = new ConfigConstant<>(
                "client.communication.connectionState.checkInterval",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(60)
        );

        /**
         * If the KNX Net/IP device does not receive a heartbeat request within 120 seconds of the last correctly
         * received message frame, the server shall terminate the connection by sending a DISCONNECT_REQUEST to the
         * client’s control endpoint.
         */
        public static final ConfigConstant<Long> HEARTBEAT_TIMEOUT = new ConfigConstant<>(
                "client.communication.connectionState.heartbeatTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(120)
        );

        private ConnectionState() {
        }
    }

    public static final class Control {
        /**
         * Control Channel Port
         */
        public static final ConfigConstant<Integer> PORT = new ConfigConstant<>(
                "client.communication.control.port",
                Integer.class,
                Integer::valueOf,
                () -> 0
        );
        /**
         * Timeout for Control Channel Socket
         */
        public static final ConfigConstant<Long> SOCKET_TIMEOUT = new ConfigConstant<>(
                "client.communication.control.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3)
        );

        private Control() {
        }
    }

    public static final class Data {
        /**
         * Data Channel Port
         */
        public static final ConfigConstant<Integer> PORT = new ConfigConstant<>(
                "client.communication.data.port",
                Integer.class,
                Integer::valueOf,
                () -> 0
        );
        /**
         * KNX client shall wait for 1 second for a TUNNELING_ACK response on a TUNNELING_REQUEST frame from
         * KNX Net/IP device.
         */
        public static final ConfigConstant<Long> DATA_REQUEST_TIMEOUT = new ConfigConstant<>(
                "client.communication.data.requestTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(1)
        );
        /**
         * Timeout for Data Channel Socket
         */
        public static final ConfigConstant<Long> SOCKET_TIMEOUT = new ConfigConstant<>(
                "client.communication.data.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3)
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
        public static final ConfigConstant<InetAddress> ADDRESS = new ConfigConstant<>(
                "client.endpoint.address",
                InetAddress.class,
                Networker::getByAddress,
                () -> Networker.getAddressUnbound(),
                false
        );
        /**
         * Endpoint Port (of KNX/Net IP device)
         */
        public static final ConfigConstant<Integer> PORT = new ConfigConstant<>(
                "client.endpoint.port",
                Integer.class,
                Integer::valueOf,
                () -> KNX_PORT,
                false
        );

        private Endpoint() {
        }
    }

    public static final class Multicast {
        /**
         * Multicast Address
         */
        public static final ConfigConstant<InetAddress> ADDRESS = new ConfigConstant<>(
                "client.communication.multicast.address",
                InetAddress.class,
                Networker::getByAddress,
                () -> MULTICAST_ADDRESS
        );
        /**
         * Multicast Port
         */
        public static final ConfigConstant<Integer> PORT = new ConfigConstant<>(
                "client.communication.multicast.port",
                Integer.class,
                Integer::valueOf,
                () -> KNX_PORT
        );
        /**
         * Default Time-To-Live (TTL) for multicast communication
         */
        public static final ConfigConstant<Integer> TIME_TO_LIVE = new ConfigConstant<>(
                "client.communication.multicast.timeToLive",
                Integer.class,
                Integer::valueOf,
                () -> 4
        );
        /**
         * Timeout for Multicast Channel Socket
         */
        public static final ConfigConstant<Long> SOCKET_TIMEOUT = new ConfigConstant<>(
                "client.communication.multicast.socketTimeout",
                Long.class,
                Long::valueOf,
                () -> TimeUnit.SECONDS.toMillis(3)
        );

        private Multicast() {
        }
    }

    public static final class Executor {
        /**
         * Default size for Communicator Executor Pool Size
         */
        public static final ConfigConstant<Integer> COMMUNICATION_POOL_SIZE = new ConfigConstant<>(
                "client.communication.executorPoolSize",
                Integer.class,
                Integer::valueOf,
                () -> 10
        );
        /**
         * Default size for Plugin Executor Pool Size
         */
        public static final ConfigConstant<Integer> PLUGIN_POOL_SIZE = new ConfigConstant<>(
                "client.plugin.executorPoolSize",
                Integer.class,
                Integer::valueOf,
                () -> 10
        );

        private Executor() {
        }
    }
}
