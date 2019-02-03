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

package li.pitschmann.knx.link.plugin;

import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.communication.*;
import li.pitschmann.utils.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Statistic Plug-in that prints the statistic of KNX client for a given interval (in milliseconds)
 *
 * @author PITSCHR
 */
public final class StatisticPlugin implements ExtensionPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticPlugin.class);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final long DEFAULT_INTERVAL_MILLISECONDS = TimeUnit.MINUTES.toMillis(1); // 60000 ms
    private static final StatisticFormat DEFAULT_FORMAT = StatisticFormat.JSON;
    private final long intervalMilliseconds;
    private final StatisticFormat format;
    private KnxClient client;

    /**
     * Starts Statistic Plug-In with default format and interval setting.
     * <p/>
     * Default format is {@link #DEFAULT_FORMAT} <br/>
     * Default interval is {@link #DEFAULT_INTERVAL_MILLISECONDS}<br/>
     */
    public StatisticPlugin() {
        this(DEFAULT_FORMAT, DEFAULT_INTERVAL_MILLISECONDS);
    }

    /**
     * Starts Statistic Plug-In with specific format and interval setting. Choose the interval (ms) wisely.
     * Smaller the interval (ms) more frequently the statistic will be updated and resulting into more CPU resources.
     *
     * @param format               the format how statistic should look like (see: {@link StatisticFormat})
     * @param intervalMilliseconds interval in milliseconds
     */
    public StatisticPlugin(final StatisticFormat format, final long intervalMilliseconds) {
        this.format = format;
        this.intervalMilliseconds = intervalMilliseconds;
    }

    @Override
    public void onInitialization(final KnxClient client) {
        this.client = Objects.requireNonNull(client);
        executor.execute(new StatisticRunnable(this.intervalMilliseconds));
        executor.shutdown();
    }

    @Override
    public void onStart() {
        // NO-OP
    }

    @Override
    public void onShutdown() {
        executor.shutdownNow();
        // print last statistic
        LOG.info(StatisticPlugin.this.getStatisticAsText());
    }

    /**
     * Returns the statistic in human-friendly format to be logged
     *
     * @return human-friendly statistic string
     */
    private String getStatisticAsText() {
        final KnxStatistic statistics = this.client.getStatistic();

        return String.format( //
                format.getTemplate(),

                statistics.getNumberOfBodyReceived(), // %1
                statistics.getNumberOfBytesReceived(), // %2
                statistics.getNumberOfBodySent(), // %3
                statistics.getNumberOfBytesSent(), // %4
                statistics.getNumberOfErrors(), // %5
                statistics.getErrorRate(), // %6
                // Description
                statistics.getNumberOfBodyReceived(DescriptionResponseBody.class), // %7
                statistics.getNumberOfBodySent(DescriptionRequestBody.class), // %8
                // Connect
                statistics.getNumberOfBodyReceived(ConnectResponseBody.class), // %9
                statistics.getNumberOfBodySent(ConnectRequestBody.class), // %10
                // Connection State
                statistics.getNumberOfBodyReceived(ConnectionStateResponseBody.class), // %11
                statistics.getNumberOfBodySent(ConnectionStateRequestBody.class), // %12
                // Tunnelling
                statistics.getNumberOfBodyReceived(TunnellingRequestBody.class), // %13
                statistics.getNumberOfBodySent(TunnellingRequestBody.class), // %14
                statistics.getNumberOfBodyReceived(TunnellingAckBody.class), // %15
                statistics.getNumberOfBodySent(TunnellingAckBody.class), // %16
                // Disconnect
                statistics.getNumberOfBodyReceived(DisconnectRequestBody.class), // %17
                statistics.getNumberOfBodySent(DisconnectRequestBody.class), // %18
                statistics.getNumberOfBodyReceived(DisconnectResponseBody.class), // %19
                statistics.getNumberOfBodySent(DisconnectResponseBody.class) // %20
        );
        // @formatter:on
    }

    /**
     * Several statistic formats to format the statistic of KNX Net/IP communication
     */
    public enum StatisticFormat {
        // @formatter:off
        /**
         * TEXT Statistic Template
         */
        TEXT( "" + //
            "%n\t%1$s packets received (%2$s)" + //
            "%n\t\t[Description     ] Request: 0, Response: %7$s" + //
            "%n\t\t[Connect         ] Request: 0, Response: %9$s" + //
            "%n\t\t[Connection State] Request: 0, Response: %11$s" + //
            "%n\t\t[Tunnelling      ] Request: %13$s, Response: %15$s" + //
            "%n\t\t[Disconnect      ] Request: %17$s, Response: %19$s" + //
            "%n\t%3$s packets sent (%4$s)" + //
            "%n\t\t[Description     ] Request: %8$s, Response: 0" + //
            "%n\t\t[Connect         ] Request: %10$s, Response: 0" + //
            "%n\t\t[Connection State] Request: %12$s, Response: 0" + //
            "%n\t\t[Tunnelling      ] Request: %14$s, Response: %16$s" + //
            "%n\t\t[Disconnect      ] Request: %18$s, Response: %20$s" + //
            "%n\t%5$s errors (%6$.2f%%)"),
         /**
         * JSON Statistic Template
         */
        JSON("" + //
            "{" + //
                "\"inbound\":{" + //
                    "\"total\":{" + //
                        "\"packets\":%1$s," + //
                        "\"bytes\":%2$s" + //
                    "}," + //
                    "\"description\":{" + //
                        "\"request\":0," + //
                        "\"response\":%7$s" + //
                    "}," + //
                    "\"connect\":{" + //
                        "\"request\":0," + //
                        "\"response\":%9$s" + //
                    "}," + //
                    "\"connectionState\":{" + //
                        "\"request\":0," + //
                        "\"response\":%11$s" + //
                    "}," + //
                    "\"tunnelling\":{" + //
                        "\"request\":%13$s," + //
                        "\"acknowledge\":%15$s" + //
                    "}," + //
                    "\"disconnect\":{" + //
                        "\"request\":%17$s," + //
                        "\"response\":%19$s" + //
                    "}" + //
                "}," + //
                "\"outbound\":{" + //
                    "\"total\":{" + //
                        "\"packets\":%3$s," + //
                        "\"bytes\":%4$s" + //
                    "}," + //
                    "\"description\":{" + //
                        "\"request\":%8$s," + //
                        "\"response\":0" + //
                    "}," + //
                    "\"connect\":{" + //
                        "\"request\":%10$s," + //
                        "\"response\":0" + //
                    "}," + //
                    "\"connectionState\":{" + //
                        "\"request\":%12$s," + //
                        "\"response\":0" + //
                    "}," + //
                    "\"tunnelling\":{" + //
                        "\"request\":%14$s," + //
                        "\"acknowledge\":%16$s" + //
                    "}," + //
                    "\"disconnect\":{" + //
                        "\"request\":%18$s," + //
                        "\"response\":%20$s" + //
                    "}" + //
                "}," + //
                "\"error\":{" + //
                    "\"total\":{" + //
                        "\"packets\":%5$s," + //
                        "\"rate\":\"%6$.2f%%\"" + //
                    "}" + //
                "}" + //
            "}"
        );
        // @formatter:on

        private final String template;

        StatisticFormat(final String template) {
            this.template = template;
        }

        private String getTemplate() {
            return template;
        }
    }

    /**
     * Runnable to print statistic in every interval
     *
     * @author PITSCHR
     */
    private class StatisticRunnable implements Runnable {
        private final long intervalMilliseconds;

        private StatisticRunnable(final long intervalMilliseconds) {
            this.intervalMilliseconds = intervalMilliseconds;
        }

        @Override
        public void run() {
            do {
                LOG.info(StatisticPlugin.this.getStatisticAsText());
            } while (Sleeper.milliseconds(this.intervalMilliseconds));
        }
    }
}