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

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import com.google.common.base.Stopwatch;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.config.ConfigConstants;
import li.pitschmann.knx.link.plugin.AuditPlugin;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.link.plugin.StatisticPlugin;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * Demo class how to monitor the KNX traffic with support of plug-ins
 *
 * @author PITSCHR
 */
public class KnxMainMonitoring extends AbstractKnxMain {
    public static void main(final String[] args) {
        new KnxMainMonitoring().startMonitoring(args);
    }

    private void startMonitoring(final String[] args) {
        // final String[] args = new String[]{""};
        // final String[] args = new String[]{"-ip","192.168.1.16"};
        // final String[] args = new String[]{"-ip","192.168.1.16", "-nat", "-l"};

        // Logging all?
        final var logAll = existsParameter(args, "-l");
        final var rootLogger = ((ch.qos.logback.classic.Logger) logRoot);
        if (logAll) {
            rootLogger.setLevel(ch.qos.logback.classic.Level.ALL);
        } else {
            // change the pattern of console appender
            final var consoleAppender = (ConsoleAppender)rootLogger.getAppender("STDOUT");
            final var encoder = (PatternLayoutEncoder)consoleAppender.getEncoder();
            encoder.setPattern("%date - %msg%n");
            encoder.start();
        }
        log.debug("Log all?: {}", logAll);

        // Get Monitor Time in Seconds
        final var monitorTime = getParameterValue(args, "-t", Long::parseLong, 30L);
        log.debug("Monitor Time: {}s", monitorTime);

        // start KNX communication
        log.trace("START");

        final var config = parseConfigBuilder(args) //
                .plugin( //
                        new AuditPlugin(), //
                        new StatisticPlugin(StatisticPlugin.StatisticFormat.TEXT, 30000), //
                        new PrintPlugin(log)
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

        Sleeper.seconds(1);
    }

    private static final class PrintPlugin implements ObserverPlugin, ExtensionPlugin {
        private final Logger log;

        private PrintPlugin(final Logger log) {
            this.log = log;
        }

        @Override
        public void onStart() {
            log.debug("\033[0;35m[ START  ] KNX Client started.\033[0m");
        }

        @Override
        public void onShutdown() {
            log.debug("\033[0;35m[  STOP  ] KNX Client stopped.\033[0m");
        }

        @Override
        public void onIncomingBody(@Nonnull Body item) {
            log.debug(String.format("\033[0;32m[INCOMING] %s: %s\033[0m", item.getServiceType().getFriendlyName(), item.getRawDataAsHexString()));
        }

        @Override
        public void onOutgoingBody(@Nonnull Body item) {
            log.debug(String.format("\033[0;37m[OUTGOING] %s: %s\033[0m", item.getServiceType().getFriendlyName(), item.getRawDataAsHexString()));
        }

        @Override
        public void onError(@Nonnull Throwable throwable) {
            log.debug(String.format("\033[0;31m[ ERROR  ] %s\033[0m", throwable.getMessage()));
        }

        @Override
        public void onInitialization(KnxClient client) {
            log.debug(String.format("\033[0;34m[  INIT  ] Client initialized (%s)\033[0m", client));
        }
    }
}
