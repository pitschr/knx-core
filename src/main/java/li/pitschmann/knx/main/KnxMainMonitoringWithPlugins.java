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
import li.pitschmann.knx.link.plugin.AuditPlugin;
import li.pitschmann.knx.link.plugin.StatisticPlugin;
import li.pitschmann.knx.link.plugin.experimental.AuditDatabasePlugin;
import li.pitschmann.utils.Networker;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * Demo class how to monitor the KNX traffic with support of plug-ins
 * <ul>
 * <li>1st argument is the address of KNX Net/IP device; default value "192.168.1.16"</li>
 * <li>2nd argument is the monitoring time in seconds; default value is "forever" ({@link Long#MAX_VALUE})</li>
 * <li>Subsequent arguments are ignored.</li>
 * </ul>
 *
 * @author PITSCHR
 */
public class KnxMainMonitoringWithPlugins extends AbstractKnxMain {
    private static final Logger LOG = LoggerFactory.getLogger(KnxMainMonitoringWithPlugins.class);
    private static final Logger ROOT_LOGGER = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static final InetAddress DEFAULT_IP_ADDRESS = Networker.getByAddress("192.168.1.16");

    public static void main(final String[] args) {
        // set level to Level#ALL for ROOT logger implementation
        final var logAll = getParameterValue(args, "-l", false, Boolean::parseBoolean);
        if (logAll) {
            ((ch.qos.logback.classic.Logger) ROOT_LOGGER).setLevel(ch.qos.logback.classic.Level.ALL);
        }
        LOG.debug("Log all: {}", logAll);

        // Get KNX Net/IP Address
        final var ipAddress = getParameterValue(args, "-r", DEFAULT_IP_ADDRESS, Networker::getByAddress);
        LOG.debug("KNX Net/IP Address: {}", ipAddress);

        // Get Monitor Time in Seconds
        final var monitorTime = getParameterValue(args, "-t", Long.MAX_VALUE, Long::parseLong);
        LOG.debug("Monitor Time: {}s", monitorTime);

        // start KNX communication
        LOG.trace("START");

        final var config = Configuration.create(ipAddress)//
                .plugin( //
                        new AuditPlugin(), //
                        new StatisticPlugin(StatisticPlugin.StatisticFormat.TEXT, 30000), //
                        new AuditDatabasePlugin() //
                ) //
                .setting("timeout.request.connectionstate", "10000") //
                .setting("interval.connectionstate", "30000") //
                .setting("timeout.alive.connectionstate", "60000") //
                .build();

        try (final var client = new DefaultKnxClient(config)) {
            LOG.debug("========================================================================");
            LOG.debug("MONITORING WITH PLUGINS for {} minutes and {} seconds", (int) (monitorTime / 60), monitorTime % 60);
            LOG.debug("========================================================================");
            final var sw = Stopwatch.createStarted();
            while (!client.isClosed() && sw.elapsed(TimeUnit.SECONDS) <= monitorTime) {
                Sleeper.seconds(1);
            }
            LOG.debug("========================================================================");
            LOG.debug("STOP MONITORING WITH PLUGINS");
            LOG.debug("========================================================================");
        } catch (final Throwable t) {
            LOG.error("THROWABLE. Reason: {}", t.getMessage(), t);
        } finally {
            LOG.trace("FINALLY");
        }
    }
}
