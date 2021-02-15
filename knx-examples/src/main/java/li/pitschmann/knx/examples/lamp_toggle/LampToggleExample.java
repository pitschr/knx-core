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

package li.pitschmann.knx.examples.lamp_toggle;

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.communication.DefaultKnxClient;
import li.pitschmann.knx.core.datapoint.DPT1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example Two: Inverse Lamp Status
 * <p>
 * If the lamp is <strong>on</strong>, the lamp should be <strong>off</strong>.<br>
 * If the lamp is <strong>off</strong>, the lamp should be <strong>on</strong>.<br>
 * <p>
 * Note:
 * This works only when write flag is enabled for group address (1/2/110) and
 * group address (1/2/113) has a read flag enabled.
 *
 * @author PITSCHR
 */
public final class LampToggleExample {
    // disable logging as we only want to print System.out.println(..)
    static {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.OFF);
    }

    public static void main(final String[] args) {
        // this is the group address where the KNX actuator returns the status of lamp
        final var readGroupAddress = GroupAddress.of(1, 2, 113);

        // this is the group address where the KNX actuator listens to switch on/off the lamp
        final var writeGroupAddress = GroupAddress.of(1, 2, 110);

        // create KNX client and connect to KNX Net/IP device using auto-discovery
        try (final var client = DefaultKnxClient.createStarted()) {
            // send a 'read' request to KNX
            client.readRequest(readGroupAddress);

            // wait a bit (usually few milliseconds, but up to 1 second maximum)
            // KNX actuator will send a response to the KNX client with actual lamp status
            final var lampStatus = client.getStatusPool().getValue(readGroupAddress, DPT1.SWITCH).getValue();

            // lamp status will be inverted (on -> off / off -> on)
            final var lampStatusInverted = !lampStatus;

            // send a 'write' request to KNX
            client.writeRequest(writeGroupAddress, DPT1.SWITCH.of(lampStatusInverted));
        }

        // auto-closed and disconnected by KNX client
    }
}
