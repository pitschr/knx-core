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
 * <li>1st argument is the address of KNX Net/IP router; default value "192.168.1.16"</li>
 * <li>2nd argument is the address of KNX group; default value "1/2/100"</li>
 * </ul>
 *
 * @author PITSCHR
 */
public class KnxMainReadAndWrite extends AbstractKnxMain {
    private static final Logger LOG = LoggerFactory.getLogger(KnxMainReadAndWrite.class);
    private static final String DEFAULT_ROUTER_IP = "192.168.1.16";
    private static final GroupAddress DEFAULT_GROUP_ADDRESS = GroupAddress.of(1, 2, 100);

    public static void main(final String[] args) {
        // 1st Argument: Get Router Address
        final String routerAddress = getParameterValue(args, "-r", DEFAULT_ROUTER_IP, Function.identity());
        LOG.debug("Router Address: {}", routerAddress);

        // 2nd Argument: Get Group Address
        final GroupAddress groupAddress = getParameterValue(args, "-ga", DEFAULT_GROUP_ADDRESS, AbstractKnxMain::parseGroupAddress);
        LOG.debug("Group Address: {} (3-level), {} (2-level)", groupAddress.getAddress(), groupAddress.getAddressLevel2());

        try (final DefaultKnxClient client = new DefaultKnxClient(routerAddress)) {
            // Sends the read request
            // The returned instance is the acknowledge sent by KNX router indicating that read request was received
            final var readRequestAck = client.readRequest(groupAddress).get();
            LOG.debug("READ ACK: {}", readRequestAck);

            // Wait bit for update (up to 1 sec)
            // If communication and read flags on KNX group address are set the state of lamp will be forwarded by the
            // KNX router and status pool will be updated with lamp status
            client.getStatusPool().isUpdated(groupAddress, 1, TimeUnit.SECONDS);

            // read lamp state
            final var lampStatus = client.getStatusPool().getValue(groupAddress, DPT1.SWITCH).getBooleanValue();
            LOG.debug("STATUS BEFORE SWITCH: {}", lampStatus);

            // Sends the write request
            // The returned instance is the acknowledge sent by KNX router indicating that write request was received
            final var writeRequestAck = client.writeRequest(groupAddress, DPT1.SWITCH.toValue(!lampStatus)).get();
            LOG.debug("WRITE ACK: {}", writeRequestAck);

            // Wait bit for update (up to 1 sec)
            // If communication and write flags on KNX group address are set the state of lamp will be changed and
            // the state of lamp will be forwarded by the KNX router which updates the status pool as well
            client.getStatusPool().isUpdated(groupAddress, 1, TimeUnit.SECONDS);

            LOG.debug("STATUS AFTER SWITCH: {}", client.getStatusPool().getValue(groupAddress, DPT1.SWITCH).getBooleanValue());
        } catch (final Throwable t) {
            LOG.error("THROWABLE. Reason: {}", t.getMessage(), t);
        }
    }
}
