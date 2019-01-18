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

import com.google.common.base.*;
import li.pitschmann.knx.link.*;
import li.pitschmann.knx.link.communication.*;
import li.pitschmann.knx.link.plugin.experimental.*;
import li.pitschmann.utils.*;
import org.slf4j.*;

import java.net.*;
import java.util.concurrent.*;

/**
 * Demo class how to monitor the KNX traffic with support of plug-ins
 * <ul>
 * <li>1st argument is the address of KNX Net/IP router; default value "192.168.1.16"</li>
 * <li>2nd argument is the monitoring time in seconds; default value is "forever" ({@link Long#MAX_VALUE})</li>
 * <li>Subsequent arguments are ignored.</li>
 * </ul>
 *
 * @author PITSCHR
 */
public class KnxMainMonitoringWithPlugins extends AbstractKnxMain {
    private static final Logger LOG = LoggerFactory.getLogger(KnxMainMonitoringWithPlugins.class);
    private static final Logger ROOT_LOGGER = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static final InetAddress DEFAULT_ROUTER_IP = Networker.getByAddress("192.168.1.16");

    public static void main(final String[] args) {
        // set level to Level#ALL for ROOT logger implementation
        final boolean logAll = getParameterValue(args, "-l", false, Boolean::parseBoolean);
        if (logAll) {
            ((ch.qos.logback.classic.Logger) ROOT_LOGGER).setLevel(ch.qos.logback.classic.Level.ALL);
        }
        LOG.debug("Log all: {}", logAll);

        // Get Router Address
        final InetAddress routerAddress = getParameterValue(args, "-r", DEFAULT_ROUTER_IP, Networker::getByAddress);
        LOG.debug("Router Address: {}", routerAddress);

        // Get Monitor Time in Seconds
        final long monitorTime = getParameterValue(args, "-t", Long.MAX_VALUE, Long::parseLong);
        LOG.debug("Monitor Time: {}s", monitorTime);

        // start KNX communication
        LOG.trace("START");

        final Configuration config = Configuration.create(routerAddress)//
                .plugin( //
                        new AuditTextPlugin(), //
                        new StatisticPlugin(), //
                        new AuditDatabasePlugin() //
                ) //
                .setting("timeout.request.connectionstate", "10000") //
                .setting("interval.connectionstate", "30000") //
                .setting("timeout.alive.connectionstate", "60000") //
                .build();

        try (DefaultKnxClient client = new DefaultKnxClient(config)) {
            LOG.debug("========================================================================");
            LOG.debug("MONITORING WITH PLUGINS for {} minutes and {} seconds", (int) (monitorTime / 60), monitorTime % 60);
            LOG.debug("========================================================================");
            Stopwatch sw = Stopwatch.createStarted();
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
