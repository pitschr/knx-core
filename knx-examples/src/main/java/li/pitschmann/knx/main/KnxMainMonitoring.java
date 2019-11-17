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
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.config.ConfigConstants;
import li.pitschmann.knx.link.plugin.monitor.TTYMonitorPlugin;
import li.pitschmann.knx.plugins.audit.FileAuditPlugin;
import li.pitschmann.knx.plugins.statistic.FileStatisticFormat;
import li.pitschmann.knx.plugins.statistic.FileStatisticPlugin;
import li.pitschmann.utils.Sleeper;
import li.pitschmann.utils.Stopwatch;

import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Demo class how to monitor the KNX traffic with support of plug-ins
 *
 * @author PITSCHR
 */
public class KnxMainMonitoring extends AbstractKnxMain {
    private static final String[] EXAMPLE_DEFAULT = new String[0];
    private static final String[] EXAMPLE_TUNNELING = new String[]{"--ip", "192.168.1.16"};
    private static final String[] EXAMPLE_TUNNELING_WITH_NAT = new String[]{"--ip", "192.168.1.16", "--nat"};
    private static final String[] EXAMPLE_ROUTING = new String[]{"--routing"};

    public static void main(final String[] args) {
        new KnxMainMonitoring().startMonitoring(
                // if arguments is null or empty - fall back to use EXAMPLE_*
                (args != null && args.length > 0) ? args : EXAMPLE_ROUTING
        );
    }

    /**
     * Checks if the time is overdue
     *
     * @param stopwatch            the current timer
     * @param monitorTimeInSeconds maximum monitor time in seconds
     * @return {@code true} if overdue, otherwise {@code false}
     */
    private static boolean isOverdue(final Stopwatch stopwatch, long monitorTimeInSeconds) {
        return stopwatch.elapsed(TimeUnit.SECONDS) > monitorTimeInSeconds;
    }

    /**
     * Returns given {@code seconds} into a human readable format
     *
     * @param seconds number of seconds to be converted
     * @return human readable time format in 0 days, 0 hours, 0 minutes, 0 seconds
     */
    private static String toHumanReadableTimeFormat(final long seconds) {
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        long days = seconds / 86400;

        return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, sec);
    }

    /**
     * Start KNX Monitor
     *
     * @param args
     */
    private void startMonitoring(final String[] args) {
        // Detach Console Appender
        final var rootLogger = (Logger) logRoot;
        final var currentLogger = (Logger) log;
        final var consoleAppender = rootLogger.getAppender("STDOUT");
        rootLogger.detachAppender(consoleAppender);

        // Logging all?
        final var logAll = existsParameter(args, "--log");
        if (logAll) {
            rootLogger.setLevel(Level.ALL);
            currentLogger.setLevel(Level.ALL);
        } else {
            rootLogger.setLevel(Level.OFF);
            currentLogger.setLevel(Level.OFF);
        }
        log.debug("Log all?: {}", logAll);

        // Get Monitor Time in Seconds
        final var monitorTime = getParameterValue(args, "-t,--time", Long::parseLong, 300L);
        log.debug("Monitor Time: {}", toHumanReadableTimeFormat(monitorTime));

        // Get XML Project Path
        final var projectPath = getParameterValue(args, "-p,--knxproj", Paths::get, null);
        log.debug("KNX Project Path: {}", Objects.requireNonNullElse(projectPath, "<empty>"));

        // Create Config
        final var config = parseConfigBuilder(args) //
                .setting(ConfigConstants.PROJECT_PATH, projectPath)
                .plugin(FileAuditPlugin.class)
                .plugin(TTYMonitorPlugin.class)
                .plugin(FileStatisticPlugin.class)
                .setting(ConfigConstants.ConnectionState.CHECK_INTERVAL, 30000L) // instead of 60s
                .setting(ConfigConstants.ConnectionState.HEARTBEAT_TIMEOUT, 60000L) // instead of 120s
                .setting(ConfigConstants.Description.PORT, 40001) //
                .setting(ConfigConstants.Control.PORT, 40002) //
                .setting(ConfigConstants.Data.PORT, 40003) //
                .setting(FileAuditPlugin.PATH, Paths.get("knx-audit.log"))
                .setting(FileStatisticPlugin.PATH, Paths.get("knx-statistic.log"))
                .setting(FileStatisticPlugin.FORMAT, FileStatisticFormat.TEXT)
                .build();

        final var sw = Stopwatch.createStarted();
        final var maxAttempts = 10;
        var attempts = 1;
        try {
            log.debug("===================================================================================");
            log.debug("MONITORING WITH PLUGINS for: {}", toHumanReadableTimeFormat(monitorTime));
            log.debug("===================================================================================");

            // loop in case the KNX client loses the connection for some reasons (e.g. power outage?, connection/firewall issue)
            do {

                // create connection and keep alive until monitor time is not overdue
                try (final var client = DefaultKnxClient.createStarted(config)) {
                    while (client.isRunning() && !isOverdue(sw, monitorTime)) {
                        Sleeper.seconds(1);
                    }
                } catch (final Throwable t) {
                    log.error("THROWABLE. Reason: {}", t.getMessage(), t);
                }

                // add small delay in re-connect in case the time is not overdue
                if (!isOverdue(sw, monitorTime)) {
                    log.warn("Re-Connecting ...");
                    Sleeper.seconds(5);
                }

                // quit this loop if max attempts exceeded or if time is overdue
            } while (attempts++ < maxAttempts && !isOverdue(sw, monitorTime));
        } finally {
            log.debug("===================================================================================");
            log.debug("STOP MONITORING WITH PLUGINS after {} attempts: {}", attempts, toHumanReadableTimeFormat(sw.elapsed(TimeUnit.SECONDS)));
            log.debug("===================================================================================");
        }
    }
}
