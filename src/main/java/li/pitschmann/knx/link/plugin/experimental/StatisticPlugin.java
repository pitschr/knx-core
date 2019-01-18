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

package li.pitschmann.knx.link.plugin.experimental;

import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.communication.*;
import li.pitschmann.knx.link.plugin.*;
import li.pitschmann.utils.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Statistic Plug-in that prints the statistic of KNX client every interval.
 *
 * @author PITSCHR
 */
public final class StatisticPlugin implements ExtensionPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticPlugin.class);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final long DEFAULT_INTERVAL_MILLISECONDS = TimeUnit.MINUTES.toMillis(1);
    private final long intervalMilliseconds;
    private KnxClient client;

    /**
     * Starts Statistic Plug-In with default interval setting.
     */
    public StatisticPlugin() {
        this(DEFAULT_INTERVAL_MILLISECONDS);
    }

    /**
     * Starts Statistic Plug-In with specific interval setting. Choose the interval (ms) wisely. Smaller the interval
     * (ms) more frequently the statistic will be updated and resulting into more CPU resources.
     *
     * @param intervalMilliseconds
     */
    public StatisticPlugin(final long intervalMilliseconds) {
        this.intervalMilliseconds = intervalMilliseconds;
    }

    /**
     * Converts byte into a SI unit for human-friendly text
     *
     * @param bytes
     * @return human-friendly byte unit
     */
    private static String getBytesAsSIUnit(long bytes) {
        int unit = 1000;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char c = "kMGTPE".charAt(exp - 1);
        return String.format("%.2f %sB", bytes / Math.pow(unit, exp), c);
    }

    @Override
    public void onInitialization(KnxClient client) {
        this.client = client;
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
        Objects.requireNonNull(this.client, "No KNX Client set yet!");
        final KnxStatistic statistics = this.client.getStatistic();

        // @formatter:off
        return String.format( //
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
                        "%n\t%5$s errors (%6$.2f%%)", //

                statistics.getNumberOfBodyReceived(), // %1
                getBytesAsSIUnit(statistics.getNumberOfBytesReceived()), // %2
                statistics.getNumberOfBodySent(), // %3
                getBytesAsSIUnit(statistics.getNumberOfBytesSent()), // %4
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
                statistics.getNumberOfBodySent(TunnellingRequestBody.class),// %14
                statistics.getNumberOfBodyReceived(TunnellingAckBody.class), // %15
                statistics.getNumberOfBodySent(TunnellingAckBody.class), // %16
                // Disconnect
                statistics.getNumberOfBodyReceived(DisconnectRequestBody.class), // %17
                statistics.getNumberOfBodySent(DisconnectRequestBody.class),// %18
                statistics.getNumberOfBodyReceived(DisconnectResponseBody.class), // %19
                statistics.getNumberOfBodySent(DisconnectResponseBody.class)// %20

        );
        // @formatter:on
    }

    /**
     * Runnable to print statistic in every interval
     *
     * @author PITSCHR
     */
    private class StatisticRunnable implements Runnable {
        private final long intervalMilliseconds;

        public StatisticRunnable(final long intervalMilliseconds) {
            this.intervalMilliseconds = intervalMilliseconds;
        }

        @Override
        public void run() {
            while (true) {
                LOG.info(StatisticPlugin.this.getStatisticAsText());
                if (!Sleeper.milliseconds(this.intervalMilliseconds)) {
                    LOG.debug("Statistic Plug-In has been interrupted");
                    break;
                }
            }
        }
    }
}
