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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.base.Stopwatch;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.config.ConfigConstants;
import li.pitschmann.knx.link.plugin.AuditPlugin;
import li.pitschmann.knx.link.plugin.StatisticPlugin;
import li.pitschmann.knx.link.plugin.monitor.TTYMonitorPlugin;
import li.pitschmann.utils.Sleeper;

import java.util.concurrent.TimeUnit;

/**
 * Demo class how to monitor the KNX traffic with support of plug-ins
 *
 * @author PITSCHR
 */
public class KnxMainMonitoring extends AbstractKnxMain {
    public static void main(final String[] args) {
        final String[] newArgs;
        if (args == null || args.length == 0) {
            // newArgs = new String[]{""};
            // newArgs = new String[]{"-ip","192.168.1.16"};
            // newArgs = new String[]{"-ip","192.168.1.16", "-nat", "-l"};
            newArgs = new String[]{"-routing"};
        } else {
            newArgs = args;
        }

        new KnxMainMonitoring().startMonitoring(newArgs);
    }

    private void startMonitoring(final String[] args) {
        // Detach Console Appender
        final var rootLogger = (Logger) logRoot;
        final var consoleAppender = rootLogger.getAppender("STDOUT");
        rootLogger.detachAppender(consoleAppender);

        // Logging all?
        final var logAll = existsParameter(args, "-log");
        if (logAll) {
            rootLogger.setLevel(Level.ALL);
            ((Logger) log).setLevel(Level.ALL);
        } else {
            rootLogger.setLevel(Level.OFF);
            ((Logger) log).setLevel(Level.OFF);
        }
        log.debug("Log all?: {}", logAll);

        // Get Monitor Time in Seconds
        final var monitorTime = getParameterValue(args, "-t", Long::parseLong, 300L);
        log.debug("Monitor Time: {}s", monitorTime);

        // start KNX communication
        log.trace("START");

        final var config = parseConfigBuilder(args) //
                .plugin( //
                        new AuditPlugin(), //
                        new StatisticPlugin(StatisticPlugin.StatisticFormat.TEXT, 30000), //
                        new TTYMonitorPlugin()
                ) //
                .setting(ConfigConstants.ConnectionState.REQUEST_TIMEOUT, 10000L) //
                .setting(ConfigConstants.ConnectionState.CHECK_INTERVAL, 30000L) //
                .setting(ConfigConstants.ConnectionState.HEARTBEAT_TIMEOUT, 60000L) //
                .setting(ConfigConstants.Description.PORT, 40001) //
                .setting(ConfigConstants.Control.PORT, 40002) //
                .setting(ConfigConstants.Data.PORT, 40003) //
                .build();

        log.debug("========================================================================");
        log.debug("MONITORING WITH PLUGINS for {} minutes and {} seconds", (int) (monitorTime / 60), monitorTime % 60);
        log.debug("========================================================================");
        try (final var client = DefaultKnxClient.createStarted(config)) {
            final var sw = Stopwatch.createStarted();
            while (client.isRunning() && sw.elapsed(TimeUnit.SECONDS) <= monitorTime) {
                Sleeper.seconds(1);
            }
        } catch (final Throwable t) {
            log.error("THROWABLE. Reason: {}", t.getMessage(), t);
        } finally {
            log.debug("========================================================================");
            log.debug("STOP MONITORING WITH PLUGINS");
            log.debug("========================================================================");
        }
    }
}
