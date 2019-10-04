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

import java.util.concurrent.TimeUnit;

/**
 * Demo class how to send a read request to a KNX group address.
 * <p/>
 * Examples:
 * <pre>
 * -routing -ga 1/2/113
 * -endpoint 192.168.1.16 -nat -ga 1/2/113 -n 10
 * </pre>
 *
 * @author PITSCHR
 */
public final class KnxMainRead extends AbstractKnxMain {
    private static final GroupAddress DEFAULT_GROUP_ADDRESS = GroupAddress.of(1, 2, 113);
    private static final int DEFAULT_LOOPS = 3;

    private KnxMainRead() {
        ((ch.qos.logback.classic.Logger) log).setLevel(ch.qos.logback.classic.Level.ALL);
        ((ch.qos.logback.classic.Logger) logRoot).setLevel(ch.qos.logback.classic.Level.OFF);
    }

    public static void main(final String[] args) {
        final var demo = new KnxMainRead();
        if (args.length == 0) {
            demo.startAllReadActions();
        } else {
            demo.startReadActions(args);
        }
    }

    private void startReadActions(final String[] args) {
        // Argument: Group Address
        final var groupAddress = getParameterValue(args, "-ga", GroupAddress::of, DEFAULT_GROUP_ADDRESS);
        log.debug("Group Address: {}", groupAddress);
        log.debug("=> free-level: {}", groupAddress.getAddress());
        log.debug("=> 2-level   : {}", groupAddress.getAddressLevel2());
        log.debug("=> 3-level   : {}", groupAddress.getAddressLevel3());

        // Argument: Number of requests for Group Address
        final var loops = getParameterValue(args, "-n", Integer::valueOf, DEFAULT_LOOPS);
        log.debug("Number of Requests: {}", loops);

        // start KNX communication
        log.trace("START");

        final var config = parseConfigBuilder(args).build();
        try (final var client = DefaultKnxClient.createStarted(config)) {
            final var statusPool = client.getStatusPool();
            for (int i = 0; i < loops; i++) {
                // send read request to KNX
                client.readRequest(groupAddress);

                // Wait bit for update (usually few 10ms, but up to 1 sec max)
                // If communication and read flags on KNX group address are set the state of lamp will be forwarded by the
                // KNX Net/IP device and status pool will be updated with the actual lamp status
                statusPool.isUpdated(groupAddress, 1, TimeUnit.SECONDS);
                log.debug("=> STATUS (APCI Data): {}", ByteFormatter.formatHexAsString(statusPool.getStatusFor(groupAddress).getApciData()));
                Sleeper.seconds(1);
            }
        } catch (final Throwable t) {
            log.error("THROWABLE. Reason: {}", t.getMessage(), t);
        } finally {
            log.trace("FINALLY");
        }
    }

    private void startAllReadActions() {
        // Routing
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" R O U T I N G");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startReadActions(new String[]{"-routing"});

        // Tunneling
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" T U N N E L I N G");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startReadActions(new String[0]);

        // Tunneling (NAT Mode)
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" T U N N E L I N G    ( N A T - M O D E )");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startReadActions(new String[]{"-nat"});

        // Tunneling (endpoint defined)
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" T U N N E L I N G    ( E N D P O I N T )");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startReadActions(new String[]{"-endpoint", "192.168.1.16"});
    }
}
