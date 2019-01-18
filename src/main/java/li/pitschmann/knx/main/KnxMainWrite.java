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

import li.pitschmann.knx.link.body.address.*;
import li.pitschmann.knx.link.communication.*;
import li.pitschmann.knx.link.datapoint.*;
import li.pitschmann.knx.link.datapoint.value.*;
import li.pitschmann.utils.*;
import org.slf4j.*;

import java.net.*;
import java.util.*;

/**
 * Demo class how to send a specific write request to a KNX group address.
 * <ul>
 * <li>1st argument is the address of KNX Net/IP router; default value "192.168.1.16"</li>
 * <li>2nd argument is the address of KNX group; default value "1/2/100"</li>
 * <li>3rd argument is the DPT of value; default DPT "DPT1.SWITCH"</li>
 * <li>4th argument is the 1st write value; default value "1" (on)</li>
 * <li>5th argument is the 2nd write value; default value "0" (off)</li>
 * <li>Subsequent arguments are values as well; no further default values</li>
 * </ul>
 *
 * @author PITSCHR
 */
public class KnxMainWrite extends AbstractKnxMain {
    private static final Logger LOG = LoggerFactory.getLogger(KnxMainWrite.class);
    private static final InetAddress DEFAULT_ROUTER_IP = Networker.getByAddress("192.168.1.16");
    private static final GroupAddress DEFAULT_GROUP_ADDRESS = GroupAddress.of(1, 2, 100);
    private static final String DEFAULT_DPT = "1.001"; // DPT1.SWITCH
    private static final String[] DEFAULT_VALUES = new String[]{"on", "off"}; // switch on, switch off

    public static void main(final String[] args) {
        // 1st Argument: Get Router Address
        final InetAddress routerAddress = getParameterValue(args, "-r", DEFAULT_ROUTER_IP, Networker::getByAddress);
        LOG.debug("Router Address: {}", routerAddress);

        // 2nd Argument: Get Group Address
        final GroupAddress groupAddress = getParameterValue(args, "-ga", DEFAULT_GROUP_ADDRESS, AbstractKnxMain::parseGroupAddress);
        LOG.debug("Group Address: {} (3-level), {} (2-level)", groupAddress.getAddress(), groupAddress.getAddressLevel2());

        // 3rd Argument: Get DPT
        final String dpt = getParameterValue(args, "-dpt", DEFAULT_DPT, String::valueOf);
        LOG.debug("DPT: {}", dpt);

        // 4th..Nth Arguments: Get Values
        final String[] values = getParameterValues(args, "-c", DEFAULT_VALUES, String[]::new);
        LOG.debug("Values: {}", Arrays.toString(values));

        // start KNX communication
        LOG.trace("START");
        try (final DefaultKnxClient client = new DefaultKnxClient(routerAddress)) {
            Sleeper.seconds(1);
            for (final String value : values) {
                DataPointValue<?> dpValue = DataPointTypeRegistry.getDataPointType(dpt).toValue(new String[]{value});
                LOG.debug("========================================================================");
                LOG.debug("WRITE: {} - {}\nACK: {}", value, dpValue, client.writeRequest(groupAddress, dpValue));
                Sleeper.seconds(2);
                LOG.debug("========================================================================");
            }
            KnxStatusData knxStatus = client.getStatusPool().getStatusFor(groupAddress);
            LOG.debug("Knx Status: {}", knxStatus);
        } catch (final Throwable t) {
            LOG.error("THROWABLE. Reason: {}", t.getMessage(), t);
        } finally {
            LOG.trace("FINALLY");
        }
    }
}
