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
    private static final Logger LOG = LoggerFactory.getLogger(KnxExampleInvertLampStatus.class);
    private static final String DEFAULT_IP_ADDRESS = "192.168.1.16";
    private static final GroupAddress DEFAULT_GROUP_ADDRESS = GroupAddress.of(1, 2, 100);

    public static void main(final String[] args) {
        // 1st Argument: Get KNX Net/IP Address
        final var address = getParameterValue(args, "-r", DEFAULT_IP_ADDRESS, Function.identity());
        LOG.debug("KNX Net/IP Address: {}", address);

        // 2nd Argument: Get Group Address
        final var groupAddress = getParameterValue(args, "-ga", DEFAULT_GROUP_ADDRESS, GroupAddress::of);
        LOG.debug("Group Address: {} (3-level), {} (2-level)", groupAddress.getAddress(), groupAddress.getAddressLevel2());

        try (final var client = DefaultKnxClient.createStarted(address)) {
            // Sends the read request
            // The returned instance is the acknowledge sent by KNX Net/IP device indicating that read request was received
            final var readRequestAck = client.readRequest(groupAddress).get();
            LOG.debug("READ ACK: {}", readRequestAck);

            // Wait bit for update (usually few 10ms, but up to 1 sec max)
            // If communication and read flags on KNX group address are set the state of lamp will be forwarded by the
            // KNX Net/IP device and status pool will be updated by KNX client with the actual lamp status
            client.getStatusPool().isUpdated(groupAddress, 1, TimeUnit.SECONDS);

            // read lamp state
            final var lampStatus = client.getStatusPool().getValue(groupAddress, DPT1.SWITCH).getBooleanValue();
            LOG.debug("STATUS BEFORE SWITCH: {}", lampStatus);

            // Sends the write request
            // The returned instance is the acknowledge sent by KNX Net/IP device indicating that write request was received
            final var writeRequestAck = client.writeRequest(groupAddress, DPT1.SWITCH.toValue(!lampStatus)).get();
            LOG.debug("WRITE ACK: {}", writeRequestAck);

            // Wait bit for update (usually few 10ms, but up to 1 sec max)
            // If communication and write flags on KNX group address are set the state of lamp will be changed.
            // The state of lamp will be forwarded by the KNX Net/IP device and status pool will be updated by KNX client
            client.getStatusPool().isUpdated(groupAddress, 1, TimeUnit.SECONDS);

            LOG.debug("STATUS AFTER SWITCH: {}", client.getStatusPool().getValue(groupAddress, DPT1.SWITCH).getBooleanValue());
        } catch (final Throwable t) {
            LOG.error("THROWABLE. Reason: {}", t.getMessage(), t);
        }
    }
}
