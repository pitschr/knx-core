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
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.RoutingIndicationBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.config.ConfigConstants;
import li.pitschmann.knx.link.plugin.AuditPlugin;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.link.plugin.StatisticPlugin;
import li.pitschmann.knx.parser.KnxprojParser;
import li.pitschmann.knx.parser.XmlProject;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demo class how to monitor the KNX traffic with support of plug-ins
 *
 * @author PITSCHR
 */
public class KnxMainMonitoring extends AbstractKnxMain {
    public static void main(final String[] args) {
        new KnxMainMonitoring().startMonitoring(args);
    }

    private void startMonitoring(final String[] args1) {
        // final String[] args = new String[]{""};
        // final String[] args = new String[]{"-ip","192.168.1.16"};
        // final String[] args = new String[]{"-ip","192.168.1.16", "-nat", "-l"};
        final String[] args = new String[]{"-routing"};

        // Logging all?
        final var logAll = existsParameter(args, "-l");
        final var rootLogger = ((ch.qos.logback.classic.Logger) logRoot);
        if (logAll) {
            rootLogger.setLevel(ch.qos.logback.classic.Level.ALL);
        } else {
            // change the pattern of console appender
            final var consoleAppender = (ConsoleAppender) rootLogger.getAppender("STDOUT");
            final var encoder = (PatternLayoutEncoder) consoleAppender.getEncoder();
            encoder.setPattern("%date - %msg%n");
            encoder.start();
        }
        log.debug("Log all?: {}", logAll);

        // Get Monitor Time in Seconds
        final var monitorTime = getParameterValue(args, "-t", Long::parseLong, 20L);
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
        private final AtomicInteger numberOfIncomingBodies = new AtomicInteger();
        private XmlProject xmlProject;

        private PrintPlugin(final Logger log) {
            this.log = log;
        }

        private static final String getHeader() {
            return "      # |              Service Type |    Source |   Target |     DPT | Data";
        }

        private static final String getHeaderSeparator() {
            return "--------+---------------------------+-----------+----------+---------+-----------------------------------";
        }

        @Override
        public void onInitialization(KnxClient client) {
            log.debug(String.format("\033[0;37mClient initialized (%s)\033[0m", client));

            final var knxprojFile = client.getConfig().getProjectPath();
            if (Files.isReadable(knxprojFile)) {
                xmlProject = KnxprojParser.parse(knxprojFile);
                log.debug("\033[0;37mKNXPROJ File: {}\033[0m", client.getConfig().getProjectPath());
            } else {
                log.debug("\033[0;37mNo KNXPROJ file provided.\033[0m");
            }
        }

        @Override
        public void onStart() {
            log.debug("\033[0;37mClient started.\033[0m");
        }

        @Override
        public void onShutdown() {
            log.debug("{}{}{}", "\033[0;32m", getHeaderSeparator(), "\033[0m");
            log.debug("\033[0;37mClient stopped.\033[0m");
        }

        @Override
        public void onIncomingBody(@Nonnull Body item) {
            if (numberOfIncomingBodies.getAndIncrement() == 0) {
                log.debug("{}{}{}", "\033[0;32m", getHeaderSeparator(), "\033[0m");
                log.debug("{}{}{}", "\033[1;32m", getHeader(), "\033[0m");
                log.debug("{}{}{}", "\033[0;32m", getHeaderSeparator(), "\033[0m");
            }

            if (item instanceof TunnelingRequestBody
                    || item instanceof RoutingIndicationBody) {
                log.debug("{}{}{}", "\033[0;32m", getLine(item), "\033[0m");
            } else if (item instanceof ConnectRequestBody) {
                log.debug("{}{}{}", "\033[0;32m", item, "\033[0m");
            } else {
                log.debug("{}", item);
            }
        }

        @Override
        public void onOutgoingBody(@Nonnull Body item) {
            // log.debug(String.format("\033[0;37m<- OUTGOING] %s: %s\033[0m", item.getServiceType().getFriendlyName(), item.getRawDataAsHexString()));
        }

        @Override
        public void onError(@Nonnull Throwable throwable) {
            log.debug(String.format("\033[0;31m[ ERROR  ] %s\033[0m", throwable.getMessage()));
        }

        private final String getLine(final Body item) {
            final var sb = new StringBuilder();
            sb.append(String.format("%7s", numberOfIncomingBodies.get()))
                    .append(" | ");

            final CEMI cemi;
            if (item instanceof TunnelingRequestBody) {
                cemi = ((TunnelingRequestBody) item).getCEMI();
            } else if (item instanceof RoutingIndicationBody) {
                cemi = ((RoutingIndicationBody) item).getCEMI();
            } else {
                throw new AssertionError();
            }

            sb.append(String.format("%25s", cemi.getApci().getFriendlyName()))
                    .append(" | ")
                    .append(String.format("%9s", cemi.getSourceAddress().getAddress()))
                    .append(" | ");
            if (cemi.getDestinationAddress() instanceof GroupAddress) {
                final var ga = (GroupAddress) cemi.getDestinationAddress();
                sb.append(String.format("%8s", ga.getAddressLevel3()));
            }
            sb
                    .append(" | ")
                    .append(String.format("%7s", "123.001"))
                    .append(" | ")
                    .append("123456 (")
                    .append(ByteFormatter.formatHexAsString(cemi.getApciData()))
                    .append(')');
            return sb.toString();
        }
    }
}
