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
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Demo class how to send a read request to a KNX group address.
 * <ul>
 * <li>1st argument is the address of KNX Net/IP device; default value "192.168.1.16"</li>
 * <li>2nd argument is the address of KNX group; default value "1/2/100"</li>
 * <li>Subsequent arguments are ignored.</li>
 * </ul>
 *
 * @author PITSCHR
 */
public class KnxMainRead extends AbstractKnxMain {
    private static final Logger LOG = LoggerFactory.getLogger(KnxMainRead.class);
    private static final String DEFAULT_IP_ADDRESS = "192.168.1.16";
    private static final GroupAddress DEFAULT_GROUP_ADDRESS = GroupAddress.of(1, 2, 100);
    private static final int DEFAULT_LOOPS = 50;

    public static void main(final String[] args) {
        // 1st Argument: Get KNX Net/IP Address
        final var ipAddress = getParameterValue(args, "-r", DEFAULT_IP_ADDRESS, Function.identity());
        LOG.debug("KNX Net/IP Address: {}", ipAddress);

        // 2nd Argument: Get Group Address
        final var groupAddress = getParameterValue(args, "-ga", DEFAULT_GROUP_ADDRESS, AbstractKnxMain::parseGroupAddress);
        LOG.debug("Group Address: {}", groupAddress);

        // 3rd Argument: Number of requests for Group Address
        final var loops = getParameterValue(args, "-n", DEFAULT_LOOPS, Integer::valueOf);
        LOG.debug("Loops: {}", loops);

        // start KNX communication
        LOG.trace("START");
        try (final var client = new DefaultKnxClient(ipAddress)) {
            final var statusPool = client.getStatusPool();
            Sleeper.seconds(1);
            LOG.debug("========================================================================");
            LOG.debug("GROUP ADDRESS: 3-level: {}, 2-level: {}", groupAddress.getAddress(), groupAddress.getAddressLevel2());
            LOG.debug("READ ACK: {}", client.readRequest(groupAddress).get());

            // Wait bit for update (usually few 10ms, but up to 1 sec max)
            // If communication and read flags on KNX group address are set the state of lamp will be forwarded by the
            // KNX Net/IP device and status pool will be updated with the actual lamp status
            statusPool.isUpdated(groupAddress, 1, TimeUnit.SECONDS);
            LOG.debug("STATUS (APCI Data): {}", ByteFormatter.formatHexAsString(statusPool.getStatusFor(groupAddress).getApciData()));
            LOG.debug("========================================================================");
        } catch (final Throwable t) {
            LOG.error("THROWABLE. Reason: {}", t.getMessage(), t);
        } finally {
            LOG.trace("FINALLY");
        }
    }
}
