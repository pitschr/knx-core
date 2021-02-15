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

package li.pitschmann.knx.examples.lamp_read;

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.communication.DefaultKnxClient;
import li.pitschmann.knx.core.datapoint.DPT1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demo class how to send a read request to a KNX group address.
 *
 * @author PITSCHR
 */
public final class LampReadExample {
    // disable logging as we only want to print System.out.println(..)
    static {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.OFF);
    }

    public static void main(final String[] args) {
        // this is the group address where the KNX actuator read the status of lamp
        final var readGroupAddress = GroupAddress.of(1, 2, 113);

        // create KNX client and connect to KNX Net/IP device using auto-discovery
        try (final var client = DefaultKnxClient.createStarted()) {


            // send a 'read' request to KNX
            client.readRequest(readGroupAddress);

            // this is the status pool that holds all status information which have been requested
            var statusPool = client.getStatusPool();
            // the status pool will translate the byte array from KNX into boolean value
            var isLampOn = statusPool.getValue(readGroupAddress, DPT1.SWITCH).getValue();

            if (isLampOn) {
                System.out.println("Yes, lamp is on!");
            } else {
                System.out.println("No, lamp is off!");
            }
        }

        // auto-closed and disconnected by KNX client
    }
}
