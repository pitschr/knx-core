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

package li.pitschmann.knx.link;

import li.pitschmann.utils.Networker;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * KNX specific Constants
 *
 * @author PITSCHR
 */
public final class Constants {
    private Constants() {
    }

    /**
     * Times Constants
     *
     * @author PITSCHR
     */
    public static final class Times {
        /**
         * KNX client shall wait for 10 seconds for a SEARCH_RESPONSE frame from KNX Net/IP device.
         */
        public static final long SEARCH_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
        /**
         * KNX client shall wait for 10 seconds for a DESCRIPTION_RESPONSE frame from KNX Net/IP device.
         */
        public static final long DESCRIPTION_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
        /**
         * KNX client shall wait for 10 seconds for a CONNECT_RESPONSE frame from KNX Net/IP device.
         */
        public static final long CONNECT_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
        /**
         * KNX client shall wait for 5 seconds for a DISCONNECT_RESPONSE frame from KNX Net/IP device.
         */
        public static final long DISCONNECT_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(5);
        /**
         * KNX client shall wait for 1 seconds after sending a DISCONNECT_RESPONSE frame to KNX Net/IP device.
         */
        public static final long DISCONNECT_RESPONSE_TIMEOUT = TimeUnit.SECONDS.toMillis(1);
        /**
         * KNX client shall wait for 10 seconds for a CONNECTION_STATE_RESPONSE frame from KNX Net/IP device.
         */
        public static final long CONNECTIONSTATE_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
        /**
         * Number of connection state request attempts before KNX connection will be disconnected.
         */
        public static final long CONNECTIONSTATE_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(60);
        /**
         * KNX client shall wait for 1 second for a TUNNELING_ACK response on a TUNNELING_REQUEST frame from
         * KNX Net/IP device.
         */
        public static final long DATA_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(1);
        // /**
        //  * KNX client shall wait for 10 seconds for a DEVICE_CONFIGURATION_RESPONSE frame from KNX Net/IP device.
        //  */
        // public static final long DEVICE_CONFIGURATION_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
        // /**
        //  * KNX client shall wait for 1 second for a control request frame to KNX Net/IP device.
        //  */
        // public static final long CONTROL_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(1);
        /**
         * If the KNX Net/IP device does not receive a heartbeat request within 120 seconds of the last correctly
         * received message frame, the server shall terminate the connection by sending a DISCONNECT_REQUEST to the
         * client’s control endpoint.
         */
        public static final long CONNECTION_ALIVE_TIME = TimeUnit.SECONDS.toMillis(120);
        /**
         * Timeout for Multicast Channel Socket
         */
        public static final long MULTICAST_CHANNEL_SOCKET_TIMEOUT = TimeUnit.SECONDS.toMillis(3);
        /**
         * Timeout for Description Channel Socket
         */
        public static final long DESCRIPTION_CHANNEL_SOCKET_TIMEOUT = TimeUnit.SECONDS.toMillis(3);
        /**
         * Timeout for Control Channel Socket
         */
        public static final long CONTROL_CHANNEL_SOCKET_TIMEOUT = TimeUnit.SECONDS.toMillis(3);
        /**
         * Timeout for Data Channel Socket
         */
        public static final long DATA_CHANNEL_SOCKET_TIMEOUT = TimeUnit.SECONDS.toMillis(3);

        private Times() {}
    }

    /**
     * Event Constants
     */
    public static final class Event {
        /**
         * Interval of look up for event pool (e.g. if request/ack received)
         */
        public static final long CHECK_INTERVAL = 10L;
        /**
         * Number of total attempts to be retried when no requested packet was received
         */
        public static final int TOTAL_ATTEMPTS = 3;

        private Event() {}
    }

    /**
     * Default Constants
     */
    public static final class Default {
        /**
         * KNX/IP Port Number
         */
        public static final int KNX_PORT = 3671;
        /**
         * KNX/IP System Setup Multicast Address
         */
        public static final InetAddress MULTICAST_ADDRESS = Networker.getByAddress(224, 0, 23, 12);
        /**
         * Default size for Communicator Executor Pool Size
         */
        public static final int COMMUNICATION_POOL_SIZE = 10;
        /**
         * Default size for Plugin Executor Pool Size
         */
        public static final int PLUGIN_POOL_SIZE = 10;
        /**
         * Default port for HTTP Daemon (same as Pippo)
         */
        public static final int HTTP_DAEMON_PORT = 8338;
        /**
         * Default value if NAT should be enabled
         */
        public static final boolean NAT_ENABLED = false;

        private Default() {}
    }

    public enum ConfigurationKey {
        // not reloadable
        ENDPOINT_ADDRESS("client.endpoint.address", false),
        ENDPOINT_PORT("client.endpoint.port", false),
        NAT("client.nat.enabled", false),
        PLUGIN_EXECUTOR_POOL_SIZE("client.plugin.executorPoolSize", false),
        COMMUNICATION_EXECUTOR_POOL_SIZE("client.communication.executorPoolSize", false),
        CONTROL_CHANNEL_PORT("client.communication.control.port", false),
        CONTROL_CHANNEL_SOCKET_TIMEOUT("client.communication.control.socketTimeout", false),
        DATA_CHANNEL_PORT("client.communication.data.port", false),
        DATA_CHANNEL_SOCKET_TIMEOUT("client.communication.data.socketTimeout", false),
        MULTICAST_ADDRESS("client.communication.multicast.address", false),
        MULTICAST_PORT("client.communication.multicast.port", false),
        MULTICAST_SOCKET_TIMEOUT("client.communication.multicast.socketTimeout", false),
        MULTICAST_TTL("client.communication.multicast.timeToLive", false),
        DESCRIPTION_CHANNEL_PORT("client.communication.description.port", false),
        DESCRIPTION_SOCKET_TIMEOUT("client.communication.description.socketTimeout", false),
        DAEMON_PORT("daemon.port.http", false),
        DAEMON_PROJECT_PATH("daemon.path.knxproj", false),
        // reloadable
        DISCOVERY_REQUEST_TIMEOUT("client.communication.discovery.requestTimeout", true),
        DESCRIPTION_REQUEST_TIMEOUT("client.communication.description.requestTimeout", true),
        DISCONNECT_REQUEST_TIMEOUT("client.communication.disconnect.requestTimeout", true),
        DISCONNECT_RESPONSE_TIMEOUT("client.communication.disconnect.responseTimeout", true),
        CONNECT_REQUEST_TIMEOUT("client.communication.connect.requestTimeout", true),
        CONNECTIONSTATE_REQUEST_TIMEOUT("client.communication.connectionState.requestTimeout", true),
        CONNECTIONSTATE_ALIVE_TIMEOUT("client.communication.connectionState.aliveTimeout", true),
        CONNECTIONSTATE_CHECK_INTERVAL("client.communication.connectionState.checkInterval", true);

        private final String key;
        private final boolean reloadable;
        ConfigurationKey(final @Nonnull String key, final boolean reloadable) {
            this.key = key;
            this.reloadable = reloadable;
        }

        public String getKey() {
            return key;
        }

        public boolean isReloadable() {
            return reloadable;
        }
    }
}
