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

import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.ConnectionStateResponseBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.TunnellingAckBody;
import li.pitschmann.knx.link.body.TunnellingRequestBody;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.communication.KnxStatistic;
import li.pitschmann.test.MemoryAppender;
import li.pitschmann.test.MemoryLog;
import li.pitschmann.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test {@link StatisticPlugin}
 */
public class StatisticPluginTest {
    private final StatisticPlugin plugin = new StatisticPlugin();

    @Test
    @DisplayName("Test KNX statistic with JSON format")
    @MemoryLog(StatisticPlugin.class)
    public void testStatisticWithJsonFormat(final MemoryAppender appender) {
        final var client = mock(KnxClient.class);
        when(client.getStatistic()).thenReturn(mock(KnxStatistic.class));

        // on init (will print the first statistic)
        plugin.onInitialization(client);
        assertLogLine(appender, 1, 0,
                // @formatter:off
                "{" +
                    "\"inbound\":{" +
                        "\"total\":{\"packets\":0,\"bytes\":0}," +
                        "\"description\":{\"request\":0,\"response\":0}," +
                        "\"connect\":{\"request\":0,\"response\":0}," +
                        "\"connectionState\":{\"request\":0,\"response\":0}," +
                        "\"tunnelling\":{\"request\":0,\"acknowledge\":0}," +
                        "\"disconnect\":{\"request\":0,\"response\":0}" +
                    "}," +
                    "\"outbound\":{"+
                        "\"total\":{\"packets\":0,\"bytes\":0}," +
                        "\"description\":{\"request\":0,\"response\":0}," +
                        "\"connect\":{\"request\":0,\"response\":0}," +
                        "\"connectionState\":{\"request\":0,\"response\":0}," +
                        "\"tunnelling\":{\"request\":0,\"acknowledge\":0}," +
                        "\"disconnect\":{\"request\":0,\"response\":0}" +
                    "}," +
                    "\"error\":{" +
                        "\"total\":{" +
                            "\"packets\":0," +
                            "\"rate\":\"0.00%\"" +
                        "}" +
                    "}" +
                "}"
                // @formatter:on
        );

        // on start (NO-OP)
        plugin.onStart();

        // modify statistic
        final var statistic = createKnxStatisticMock();
        when(client.getStatistic()).thenReturn(statistic);

        // on shutdown (will print the last statistic)
        plugin.onShutdown();
        assertLogLine(appender, 2, 1,
                // @formatter:off
                "{" +
                    "\"inbound\":{" +
                        "\"total\":{\"packets\":10,\"bytes\":11}," +
                        "\"description\":{\"request\":0,\"response\":16}," +
                        "\"connect\":{\"request\":0,\"response\":18}," +
                        "\"connectionState\":{\"request\":0,\"response\":20}," +
                        "\"tunnelling\":{\"request\":22,\"acknowledge\":24}," +
                        "\"disconnect\":{\"request\":26,\"response\":28}" +
                    "}," +
                    "\"outbound\":{"+
                        "\"total\":{\"packets\":12,\"bytes\":13}," +
                        "\"description\":{\"request\":17,\"response\":0}," +
                        "\"connect\":{\"request\":19,\"response\":0}," +
                        "\"connectionState\":{\"request\":21,\"response\":0}," +
                        "\"tunnelling\":{\"request\":23,\"acknowledge\":25}," +
                        "\"disconnect\":{\"request\":27,\"response\":29}" +
                    "}," +
                    "\"error\":{" +
                        "\"total\":{" +
                            "\"packets\":14," +
                            "\"rate\":\"1.50%\"" +
                        "}" +
                    "}" +
                "}"
                // @formatter:on
        );
    }

    /**
     * Creates a {@link KnxStatistic} mock object
     *
     * @return a mocked version of {@link KnxStatistic}
     */
    private KnxStatistic createKnxStatisticMock() {
        final var statistic = mock(KnxStatistic.class);
        when(statistic.getNumberOfBodyReceived()).thenReturn(10L);
        when(statistic.getNumberOfBytesReceived()).thenReturn(11L);

        when(statistic.getNumberOfBodySent()).thenReturn(12L);
        when(statistic.getNumberOfBytesSent()).thenReturn(13L);

        when(statistic.getNumberOfErrors()).thenReturn(14L);
        when(statistic.getErrorRate()).thenReturn(1.5d);

        // Description
        when(statistic.getNumberOfBodyReceived(DescriptionResponseBody.class)).thenReturn(16L);
        when(statistic.getNumberOfBodySent(DescriptionRequestBody.class)).thenReturn(17L);
        // Connect
        when(statistic.getNumberOfBodyReceived(ConnectResponseBody.class)).thenReturn(18L);
        when(statistic.getNumberOfBodySent(ConnectRequestBody.class)).thenReturn(19L);
        // Connection State
        when(statistic.getNumberOfBodyReceived(ConnectionStateResponseBody.class)).thenReturn(20L);
        when(statistic.getNumberOfBodySent(ConnectionStateRequestBody.class)).thenReturn(21L);
        // Tunnelling
        when(statistic.getNumberOfBodyReceived(TunnellingRequestBody.class)).thenReturn(22L);
        when(statistic.getNumberOfBodySent(TunnellingRequestBody.class)).thenReturn(23L);
        when(statistic.getNumberOfBodyReceived(TunnellingAckBody.class)).thenReturn(24L);
        when(statistic.getNumberOfBodySent(TunnellingAckBody.class)).thenReturn(25L);
        // Disconnect
        when(statistic.getNumberOfBodyReceived(DisconnectRequestBody.class)).thenReturn(26L);
        when(statistic.getNumberOfBodySent(DisconnectRequestBody.class)).thenReturn(27L);
        when(statistic.getNumberOfBodyReceived(DisconnectResponseBody.class)).thenReturn(28L);
        when(statistic.getNumberOfBodySent(DisconnectResponseBody.class)).thenReturn(29L);

        return statistic;
    }

    /**
     * Assert the first log line in {@link MemoryAppender}
     *
     * @param appender        appender that contains the log lines$
     * @param expectedSize    the expected size of log lines
     * @param index           the index of log line which we want to assert
     * @param expectedLogLine the expected log line
     */
    private void assertLogLine(final MemoryAppender appender, final int expectedSize, final int index, final String expectedLogLine) {
        Sleeper.milliseconds(() -> !appender.all().isEmpty(), 1000);
        final var logLines = appender.all();
        assertThat(logLines).hasSize(expectedSize);
        assertThat(logLines.get(index)).isEqualTo(expectedLogLine);
    }
}
