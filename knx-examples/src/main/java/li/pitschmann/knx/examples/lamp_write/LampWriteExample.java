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

package li.pitschmann.knx.examples.lamp_write;

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.communication.DefaultKnxClient;
import li.pitschmann.knx.core.datapoint.DataPointRegistry;
import li.pitschmann.knx.core.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demo class how to send a sequence of write request to a lamp.
 *
 * @author PITSCHR
 */
public final class LampWriteExample {
    // disable logging as we only want to print System.out.println(..)
    static {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.OFF);
    }

    public static void main(final String[] args) {
        // KNX data point type ID (=DPT1.SWITCH)
        var dpt = "1.001";

        // sequence of values to be set
        var values = new String[]{"on", "off", "on", "off"};

        // this is the group address where the KNX actuator returns the status of lamp
        final var writeGroupAddress = GroupAddress.of(1, 2, 110);

        // create KNX client and connect to KNX Net/IP device using auto-discovery
        try (final var client = DefaultKnxClient.createStarted()) {
            for (final String value : values) {
                // translate the human-friendly text into a KNX compatible data point value of DPT 1.001
                // and send to the KNX actuator
                var knxValue = DataPointRegistry.getDataPointType(dpt).of(value);
                client.writeRequest(writeGroupAddress, knxValue);

                System.out.println("Lamp status sent: " + value);
                Sleeper.seconds(1);
            }
        }

        // auto-closed and disconnected by KNX client
    }
}
