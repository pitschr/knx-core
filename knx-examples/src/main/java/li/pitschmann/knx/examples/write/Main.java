/*
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

package li.pitschmann.knx.examples.write;

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.communication.DefaultKnxClient;
import li.pitschmann.knx.core.datapoint.DataPointRegistry;
import li.pitschmann.knx.core.utils.Sleeper;
import li.pitschmann.knx.examples.AbstractKnxMain;

import java.util.Arrays;

/**
 * Demo class how to send a write request to a KNX group address.
 * <p>
 * Examples:
 * <pre>
 * --routing --groupAddress 1/2/50 --dataPointType 1.001 --value on off
 * --ip 192.168.1.16 --nat --groupAddress 1/2/50 --dataPointType 1.001 --value on off
 * </pre>
 *
 * @author PITSCHR
 */
public final class Main extends AbstractKnxMain {
    private static final GroupAddress DEFAULT_GROUP_ADDRESS = GroupAddress.of(1, 2, 50);
    private static final String DEFAULT_DPT = "1.001"; // DPT1.SWITCH
    private static final String[] DEFAULT_VALUES = new String[]{"on", "off"}; // switch on, switch off

    public static void main(final String[] args) {
        final var demo = new Main();
        if (args.length == 0) {
            demo.startAllWriteActions();
        } else {
            demo.startWriteActions(args);
        }
    }

    private void startWriteActions(final String[] args) {
        // Argument: Group Address
        final var groupAddress = getParameterValue(args, "-ga,--groupAddress", GroupAddress::of, DEFAULT_GROUP_ADDRESS);
        log.debug("Group Address: {}", groupAddress);

        // Argument: Get DPT
        final var dpt = getParameterValue(args, "-dpt,--dataPointType", String::valueOf, DEFAULT_DPT);
        log.debug("DPT: {}", dpt);

        // More Arguments: Get Values
        final var values = getParameterValues(args, "-v,--value", String[]::new, DEFAULT_VALUES);
        log.debug("Values: {}", Arrays.toString(values));

        // start KNX communication
        log.trace("START");

        final var config = parseConfigBuilder(args).build();
        try (final var client = DefaultKnxClient.createStarted(config)) {
            Sleeper.seconds(1);
            for (final String value : values) {
                final var dpValue = DataPointRegistry.getDataPointType(dpt).of(value);
                client.writeRequest(groupAddress, dpValue);
                log.debug("=> WRITE: {} - {}", value, dpValue);
                Sleeper.seconds(2);
            }
            log.debug("=> Statistic: {}", client.getStatistic());
        } catch (final Throwable t) {
            log.error("THROWABLE. Reason: {}", t.getMessage(), t);
        } finally {
            log.trace("FINALLY");
        }
    }

    private void startAllWriteActions() {
        // Routing
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" R O U T I N G");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startWriteActions(new String[]{"--routing"});

        // Tunneling
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" T U N N E L I N G");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startWriteActions(new String[0]);

        // Tunneling (NAT Mode)
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" T U N N E L I N G    ( N A T - M O D E )");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startWriteActions(new String[]{"--nat"});

        // Tunneling (endpoint defined)
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" T U N N E L I N G    ( E N D P O I N T )");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startWriteActions(new String[]{"--ip", "192.168.1.16"});
    }
}
