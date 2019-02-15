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
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Networker;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Demo class how to send a read request to a KNX group address.
 * <ul>
 * <li>1st argument is the address of KNX Net/IP router; default value "192.168.1.16"</li>
 * <li>2nd argument is the address of KNX group; default value "1/2/100"</li>
 * <li>Subsequent arguments are ignored.</li>
 * </ul>
 *
 * @author PITSCHR
 */
public class KnxMainRead extends AbstractKnxMain {
    private static final Logger LOG = LoggerFactory.getLogger(KnxMainRead.class);
    private static final InetAddress DEFAULT_ROUTER_IP = Networker.getByAddress("192.168.1.16");
    private static final GroupAddress DEFAULT_GROUP_ADDRESS = GroupAddress.of(1, 2, 100);
    private static final int DEFAULT_LOOPS = 50;

    public static void main(final String[] args) {
        // 1st Argument: Get Router Address
        final InetAddress routerAddress = getParameterValue(args, "-r", DEFAULT_ROUTER_IP, Networker::getByAddress);
        LOG.debug("Router Address: {}", routerAddress);

        // 2nd Argument: Get Group Address
        final GroupAddress groupAddress = getParameterValue(args, "-ga", DEFAULT_GROUP_ADDRESS, AbstractKnxMain::parseGroupAddress);
        LOG.debug("Group Address: {}", groupAddress);

        // 3rd Argument: Number of requests for Group Address
        final int loops = getParameterValue(args, "-n", DEFAULT_LOOPS, Integer::valueOf);
        LOG.debug("Loops: {}", loops);

        // start KNX communication
        LOG.trace("START");
        try (final DefaultKnxClient client = new DefaultKnxClient(routerAddress)) {
            Sleeper.seconds(1);
            LOG.debug("========================================================================");
            LOG.debug("READ ACK: {}", client.readRequest(groupAddress).get());
            for (int i = 0; i < loops; i++) {
                Sleeper.seconds(1);
                LOG.debug("STATUS ({}/{}) on (3-level: {}, 2-level: {}): {}", i + 1, loops, groupAddress.getAddress(), groupAddress.getAddressLevel2(), ByteFormatter.formatHexAsString(client.getStatusPool().getStatusFor(groupAddress).getApciData()));
            }
            LOG.debug("========================================================================");
        } catch (final Throwable t) {
            LOG.error("THROWABLE. Reason: {}", t.getMessage(), t);
        } finally {
            LOG.trace("FINALLY");
        }
    }
}
