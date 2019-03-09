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
import li.pitschmann.knx.link.body.TunnellingAckBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.datapoint.DataPointTypeRegistry;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;

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
public class KnxMainWrite extends AbstractKnxMain {
    private static final Logger LOG = LoggerFactory.getLogger(KnxMainWrite.class);
    private static final String DEFAULT_IP_ADDRESS = "192.168.1.16";
    private static final List<GroupAddress> DEFAULT_GROUP_ADDRESSES = Lists.newArrayList( //
            GroupAddress.of(1, 2, 0), //
            GroupAddress.of(1, 2, 50)
    );
    private static final String DEFAULT_DPT = "1.001"; // DPT1.SWITCH
    private static final String[] DEFAULT_VALUES = new String[]{"on", "off"}; // switch on, switch off

    public static void main(final String[] args) {
        // 1st Argument: Get KNX Net/IP Address
        final var ipAddress = getParameterValue(args, "-r", DEFAULT_IP_ADDRESS, Function.identity());
        LOG.debug("KNX Net/IP Address: {}", ipAddress);

        // 2nd Argument: Get DPT
        final var dpt = getParameterValue(args, "-dpt", DEFAULT_DPT, String::valueOf);
        LOG.debug("DPT: {}", dpt);

        // 3rd..Nth Arguments: Get Values
        final var values = getParameterValues(args, "-c", DEFAULT_VALUES, String[]::new);
        LOG.debug("Values: {}", Arrays.toString(values));

        // start KNX communication
        LOG.trace("START");
        try (final var client = new DefaultKnxClient(ipAddress)) {
            final var ackBodies = Lists.<Future<TunnellingAckBody>>newArrayList();
            Sleeper.seconds(1);
            for (final String value : values) {
                final var dpValue = DataPointTypeRegistry.getDataPointType(dpt).toValue(new String[]{value});
                LOG.debug("========================================================================");
                for (final GroupAddress groupAddress : DEFAULT_GROUP_ADDRESSES) {
                    final var future = client.writeRequest(groupAddress, dpValue);
                    ackBodies.add(future);
                    LOG.debug("WRITE: {} - {}\nACK: {}", value, dpValue, future);
                }
                Sleeper.seconds(2);
                LOG.debug("========================================================================");
            }

            // wait until completed
            LOG.debug("WAIT UNTIL COMPLETED");

            for (var i = 0; i < ackBodies.size(); i++) {
                LOG.debug("DONE: {}", ackBodies.get(i).isDone());
                LOG.debug("GET : {}", ackBodies.get(i).get());
            }
            LOG.debug("Statistic: {}", client.getStatistic());
            LOG.debug("COMPLETED!");
        } catch (final Throwable t) {
            LOG.error("THROWABLE. Reason: {}", t.getMessage(), t);
        } finally {
            LOG.trace("FINALLY");
        }
    }
}
