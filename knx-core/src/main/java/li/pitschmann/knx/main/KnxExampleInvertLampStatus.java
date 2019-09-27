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

import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.datapoint.DPT1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Demo class how to read a DPT1.SWITCH value from a KNX group address and inverse the value
 * <p/>
 * <u>Example:</u><br/>
 * When lamp is <strong>on</strong>, then lamp will be switched <strong>off</strong>.<br/>
 * When lamp is <strong>off</strong>, then lamp will be switched <strong>on</strong>.<br/>
 * <p/>
 * To use it, make sure that you have proper KNX flag (communication, read and write) set for given group address.
 *
 * <ul>
 * <li>1st argument is the address of KNX Net/IP device; default value "192.168.1.16"</li>
 * <li>2nd argument is the address of KNX group; default value "1/2/100"</li>
 * </ul>
 *
 * @author PITSCHR
 */
public class KnxExampleInvertLampStatus extends AbstractKnxMain {
    private static final Logger log = LoggerFactory.getLogger(KnxExampleInvertLampStatus.class);
    private static final GroupAddress DEFAULT_GROUP_ADDRESS = GroupAddress.of(1, 2, 110);
    private static final GroupAddress DEFAULT_RM_GROUP_ADDRESS = GroupAddress.of(1, 2, 113);

    public static void main(final String[] args) {
        // 1st Argument: Get KNX Net/IP Address
        final var address = getParameterValue(args, "-r", Function.identity(), null);
        log.debug("KNX Net/IP Address: {}", address);

        try (final var client = DefaultKnxClient.createStarted(address)) {
            // Sends the read request
            // The returned instance is the acknowledge sent by KNX Net/IP device indicating that read request was received
            client.readRequest(DEFAULT_RM_GROUP_ADDRESS);

            // Wait bit for update (usually few 10ms, but up to 1 sec max)
            // If communication and read flags on KNX group address are set the state of lamp will be forwarded by the
            // KNX Net/IP device and status pool will be updated by KNX client with the actual lamp status
            client.getStatusPool().isUpdated(DEFAULT_RM_GROUP_ADDRESS, 1, TimeUnit.SECONDS);

            // read lamp state
            final var lampStatus = client.getStatusPool().getValue(DEFAULT_RM_GROUP_ADDRESS, DPT1.SWITCH).getBooleanValue();
            log.debug("STATUS BEFORE SWITCH: {}", lampStatus);

            // Sends the write request
            // The returned instance is the acknowledge sent by KNX Net/IP device indicating that write request was received
            client.writeRequest(DEFAULT_GROUP_ADDRESS, DPT1.SWITCH.toValue(!lampStatus));

            // Wait bit for update (usually few 10ms, but up to 1 sec max)
            // If communication and write flags on KNX group address are set the state of lamp will be changed.
            // The state of lamp will be forwarded by the KNX Net/IP device and status pool will be updated by KNX client
            client.getStatusPool().isUpdated(DEFAULT_GROUP_ADDRESS, 1, TimeUnit.SECONDS);

            log.debug("STATUS AFTER SWITCH: {}", client.getStatusPool().getValue(DEFAULT_GROUP_ADDRESS, DPT1.SWITCH).getBooleanValue());
        } catch (final Throwable t) {
            log.error("THROWABLE. Reason: {}", t.getMessage(), t);
        }
    }
}
