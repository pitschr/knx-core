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

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * KNX specific Constants
 *
 * @author PITSCHR
 */
public interface Constants {
    /**
     * Timeout Constants
     *
     * @author PITSCHR
     */
    interface Timeouts {
        /**
         * KNX client shall wait for 10 seconds for a SEARCH_RESPONSE frame from KNX Net/IP device.
         */
        long SEARCH_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
        /**
         * KNX client shall wait for 10 seconds for a DESCRIPTION_RESPONSE frame from KNX Net/IP device.
         */
        long DESCRIPTION_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
        /**
         * KNX client shall wait for 10 seconds for a CONNECT_RESPONSE frame from KNX Net/IP device.
         */
        long CONNECT_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
        /**
         * KNX client shall wait for 5 seconds for a DISCONNECT_RESPONSE frame from KNX Net/IP device.
         */
        long DISCONNECT_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(5);
        /**
         * KNX client shall wait for 1 seconds after sending a DISCONNECT_RESPONSE frame to KNX Net/IP device.
         */
        long DISCONNECT_RESPONSE_TIMEOUT = TimeUnit.SECONDS.toMillis(1);
        /**
         * KNX client shall wait for 10 seconds for a CONNECTION_STATE_RESPONSE frame from KNX Net/IP device.
         */
        long CONNECTIONSTATE_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
        // /**
        //  * KNX client shall wait for 10 seconds for a DEVICE_CONFIGURATION_RESPONSE frame from KNX Net/IP device.
        //  */
        // long DEVICE_CONFIGURATION_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
        // /**
        //  * KNX client shall wait for 1 second for a control request frame to KNX Net/IP device.
        //  */
        // long CONTROL_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(1);
        /**
         * KNX client shall wait for 1 second for a TUNNELING_ACK response on a TUNNELING_REQUEST frame from
         * KNX Net/IP device.
         */
        long DATA_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(1);
        /**
         * If the KNX Net/IP device does not receive a heartbeat request within 120 seconds of the last correctly
         * received message frame, the server shall terminate the connection by sending a DISCONNECT_REQUEST to the
         * client’s control endpoint.
         */
        long CONNECTION_ALIVE_TIME = TimeUnit.SECONDS.toMillis(120);
        /**
         * Timeout for Description Channel Socket
         */
        long DISCOVERY_CHANNEL_SOCKET_TIMEOUT = TimeUnit.SECONDS.toMillis(3);
        /**
         * Timeout for Description Channel Socket
         */
        long DESCRIPTION_CHANNEL_SOCKET_TIMEOUT = TimeUnit.SECONDS.toMillis(3);
        /**
         * Timeout for Control Channel Socket
         */
        long CONTROL_CHANNEL_SOCKET_TIMEOUT = TimeUnit.SECONDS.toMillis(3);
        /**
         * Timeout for Data Channel Socket
         */
        long DATA_CHANNEL_SOCKET_TIMEOUT = TimeUnit.SECONDS.toMillis(3);
    }

    /**
     * Interval Constants
     */
    interface Interval {
        /**
         * Number of connection state request attempts before KNX connection will be disconnected.
         */
        long CONNECTIONSTATE = TimeUnit.SECONDS.toMillis(60);
        /**
         * Interval of look up for event pool (e.g. if request/ack received)
         */
        long EVENT = 10L;
    }

    /**
     * Default Constants
     */
    interface Default {
        /**
         * KNX/IP Port Number
         */
        int KNX_PORT = 3671;
        /**
         * KNX/IP System Setup Multicast Address
         */
        InetAddress KNX_MULTICAST_ADDRESS = Networker.getByAddress(224, 0, 23, 12);
        /**
         * Default size for Communicator Executor Pool Size
         */
        int COMMUNICATION_POOL_SIZE = 10;
        /**
         * Default size for Plugin Executor Pool Size
         */
        int PLUGIN_POOL_SIZE = 10;
        /**
         * Default port for HTTP Daemon (same as Pippo)
         */
        int HTTP_DAEMON_PORT = 8338;
    }
}
