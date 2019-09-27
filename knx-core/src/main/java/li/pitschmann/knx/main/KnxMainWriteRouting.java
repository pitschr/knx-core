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

import com.google.common.collect.Lists;
import li.pitschmann.knx.link.Configuration;
import li.pitschmann.knx.link.Constants;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.datapoint.DataPointTypeRegistry;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Demo class how to send a specific write request to a KNX group address.
 * <ul>
 * <li>1st argument is the address of KNX Net/IP device; default value "192.168.1.16"</li>
 * <li>2nd argument is the DPT of value; default DPT "DPT1.SWITCH"</li>
 * <li>3rd...Nth argument are the values</li>
 * </ul>
 *
 * @author PITSCHR
 */
public class KnxMainWriteRouting extends AbstractKnxMain {
    private static final Logger log = LoggerFactory.getLogger(KnxMainWriteRouting.class);
    private static final List<GroupAddress> DEFAULT_GROUP_ADDRESSES = Lists.newArrayList( //
            GroupAddress.of(1, 2, 50)
    );
    private static final String DEFAULT_DPT = "1.001"; // DPT1.SWITCH
    private static final String[] DEFAULT_VALUES = new String[]{"on", "off"}; // switch on, switch off

    public static void main(final String[] args) {
        // 1st Argument: Get DPT
        final var dpt = getParameterValue(args, "-dpt", String::valueOf, DEFAULT_DPT);
        log.debug("DPT: {}", dpt);

        // 2nd..Nth Arguments: Get Values
        final var values = getParameterValues(args, "-c", String[]::new, DEFAULT_VALUES);
        log.debug("Values: {}", Arrays.toString(values));

        final var config = Configuration.create(Constants.Default.MULTICAST_ADDRESS).build();

        // start KNX communication
        log.trace("START");
        try (final var client = DefaultKnxClient.createStarted(config)) {
            Sleeper.seconds(1);
            for (final String value : values) {
                final var dpValue = DataPointTypeRegistry.getDataPointType(dpt).toValue(new String[]{value});
                log.debug("========================================================================");
                for (final GroupAddress groupAddress : DEFAULT_GROUP_ADDRESSES) {
                    client.writeRequest(groupAddress, dpValue);
                    log.debug("WRITE ROUTING: {} - {}", value, dpValue);
                }
                Sleeper.seconds(2);
                log.debug("========================================================================");
            }
        } catch (final Throwable t) {
            log.error("THROWABLE. Reason: {}", t.getMessage(), t);
        } finally {
            log.trace("FINALLY");
        }
    }
}
