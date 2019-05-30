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

import com.google.common.base.Stopwatch;
import li.pitschmann.knx.link.Configuration;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.utils.Networker;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * Demo class how to monitor the KNX traffic
 * <ul>
 * <li>1st argument is the address of KNX Net/IP device; default value "192.168.1.16"</li>
 * <li>2nd argument is the monitoring time in seconds; default value is "forever" ({@link Long#MAX_VALUE})</li>
 * <li>Subsequent arguments are ignored.</li>
 * </ul>
 *
 * @author PITSCHR
 */
public class KnxMainMonitoring extends AbstractKnxMain {
    private static final Logger log = LoggerFactory.getLogger(KnxMainMonitoring.class);
    private static final Logger logRoot = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static final InetAddress DEFAULT_IP_ADDRESS = Networker.getByAddress("192.168.1.16");

    public static void main(final String[] args) {
        // set level to Level#ALL for ROOT log implementation
        final var logAll = getParameterValue(args, "-l", false, Boolean::parseBoolean);
        if (logAll) {
            ((ch.qos.logback.classic.Logger) logRoot).setLevel(ch.qos.logback.classic.Level.ALL);
        }
        log.debug("Log all: {}", logAll);

        // Get KNX Net/IP Address
        final var ipAddress = getParameterValue(args, "-r", DEFAULT_IP_ADDRESS, Networker::getByAddress);
        log.debug("KNX Net/IP Address: {}", ipAddress);

        // Get Monitor Time in Seconds
        final var monitorTime = getParameterValue(args, "-t", Long.MAX_VALUE, Long::parseLong);
        log.debug("Monitor Time: {}s", monitorTime);

        // start KNX communication
        log.trace("START");

        final var config = Configuration.create(ipAddress)//
                .setting("timeout.request.connectionstate", "10000") //
                .setting("interval.connectionstate", "30000") //
                .setting("timeout.alive.connectionstate", "60000") //
                .build();

        try (final var client = DefaultKnxClient.createStarted(config)) {
            log.debug("========================================================================");
            log.debug("MONITORING for {} minutes and {} seconds", (int) (monitorTime / 60), monitorTime % 60);
            log.debug("========================================================================");
            final var sw = Stopwatch.createStarted();
            while (!client.isClosed() && sw.elapsed(TimeUnit.SECONDS) <= monitorTime) {
                Sleeper.seconds(1);
            }
            log.debug("========================================================================");
            log.debug("STOP MONITORING");
            log.debug("========================================================================");
        } catch (final Throwable t) {
            log.error("THROWABLE. Reason: {}", t.getMessage(), t);
        } finally {
            log.trace("FINALLY");
        }
    }
}
