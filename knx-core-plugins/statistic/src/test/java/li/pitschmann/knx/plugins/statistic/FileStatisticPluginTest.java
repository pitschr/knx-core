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

package li.pitschmann.knx.plugins.statistic;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.ConnectionStateResponseBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.RoutingIndicationBody;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.communication.KnxStatistic;
import li.pitschmann.knx.link.config.Config;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test {@link FileStatisticPlugin}
 */
public class FileStatisticPluginTest {

    @Test
    @DisplayName("JSON: Test File Statistic")
    public void statisticJson() throws IOException {
        final var path = Paths.get("target/test-FileStatisticPluginTest-statisticJson-" + UUID.randomUUID() + ".log");
        final var plugin = new FileStatisticPlugin();
        final var knxClientMock = mockKnxClient(path, FileStatisticFormat.JSON, TimeUnit.MINUTES.toMillis(1));

        // start
        plugin.onInitialization(knxClientMock);
        plugin.onStart();

        // wait 1 second
        Sleeper.seconds(1);

        // shutdown
        final var statisticAtShutdown = createKnxStatisticMock();
        when(knxClientMock.getStatistic()).thenReturn(statisticAtShutdown);
        plugin.onShutdown();

        // we should have two statistic (one at start up and one at shutdown)
        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(2);
    }

    @Test
    @DisplayName("TEXT: Test File Statistic")
    public void statisticText() throws IOException {
        final var path = Paths.get("target/test-FileStatisticPluginTest-statisticText-" + UUID.randomUUID() + ".log");
        final var plugin = new FileStatisticPlugin();
        final var knxClientMock = mockKnxClient(path, FileStatisticFormat.TEXT, TimeUnit.MINUTES.toMillis(1));

        // start
        plugin.onInitialization(knxClientMock);
        plugin.onStart();

        // wait 1 second
        Sleeper.seconds(1);

        // shutdown
        final var statisticAtShutdown = createKnxStatisticMock();
        when(knxClientMock.getStatistic()).thenReturn(statisticAtShutdown);
        plugin.onShutdown();

        // we should have two statistic (one at start up and one at shutdown)
        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(2 * 16); // 1 statistic output = 16 lines for TEXT
    }


//        final var client = mock(KnxClient.class);
//        when(client.getStatistic()).thenReturn(mock(KnxStatistic.class));
//
//        // on init (will print the first statistic)
//        plugin.onInitialization(client);
//        assertLogLine(appender, 1, 0,
//                // @formatter:off
//                "{" +
//                    "\"inbound\":{" +
//                        "\"total\":{\"packets\":0,\"bytes\":0}," +
//                        "\"description\":{\"request\":0,\"response\":0}," +
//                        "\"connect\":{\"request\":0,\"response\":0}," +
//                        "\"connectionState\":{\"request\":0,\"response\":0}," +
//                        "\"tunneling\":{\"request\":0,\"acknowledge\":0}," +
//                        "\"indication\":{\"request\":0,\"response\":0}," +
//                        "\"disconnect\":{\"request\":0,\"response\":0}" +
//                    "}," +
//                    "\"outbound\":{" +
//                        "\"total\":{\"packets\":0,\"bytes\":0}," +
//                        "\"description\":{\"request\":0,\"response\":0}," +
//                        "\"connect\":{\"request\":0,\"response\":0}," +
//                        "\"connectionState\":{\"request\":0,\"response\":0}," +
//                        "\"tunneling\":{\"request\":0,\"acknowledge\":0}," +
//                        "\"indication\":{\"request\":0,\"response\":0}," +
//                        "\"disconnect\":{\"request\":0,\"response\":0}" +
//                    "}," +
//                    "\"error\":{" +
//                        "\"total\":{" +
//                            "\"packets\":0," +
//                            "\"rate\":\"0.00%\"" +
//                        "}" +
//                    "}" +
//                "}"
//                // @formatter:on
//        );
//
//        // on start (NO-OP)
//        plugin.onStart();
//
//        // modify statistic
//        final var statistic = createKnxStatisticMock();
//        when(client.getStatistic()).thenReturn(statistic);
//
//        // on shutdown (will print the last statistic)
//        plugin.onShutdown();
//        assertLogLine(appender, 2, 1,
//                // @formatter:off
//                "{" +
//                    "\"inbound\":{" +
//                        "\"total\":{\"packets\":10,\"bytes\":11}," +
//                        "\"description\":{\"request\":0,\"response\":16}," +
//                        "\"connect\":{\"request\":0,\"response\":18}," +
//                        "\"connectionState\":{\"request\":0,\"response\":20}," +
//                        "\"tunneling\":{\"request\":22,\"acknowledge\":24}," +
//                        "\"indication\":{\"request\":0,\"response\":30}," +
//                        "\"disconnect\":{\"request\":26,\"response\":28}" +
//                    "}," +
//                    "\"outbound\":{" +
//                        "\"total\":{\"packets\":12,\"bytes\":13}," +
//                        "\"description\":{\"request\":17,\"response\":0}," +
//                        "\"connect\":{\"request\":19,\"response\":0}," +
//                        "\"connectionState\":{\"request\":21,\"response\":0}," +
//                        "\"tunneling\":{\"request\":23,\"acknowledge\":25}," +
//                        "\"indication\":{\"request\":31,\"response\":0}," +
//                        "\"disconnect\":{\"request\":27,\"response\":29}" +
//                    "}," +
//                    "\"error\":{" +
//                        "\"total\":{" +
//                            "\"packets\":14," +
//                            "\"rate\":\"1.50%\"" +
//                        "}" +
//                    "}" +
//                "}"
//                // @formatter:on
//        );
//

    private KnxClient mockKnxClient(final Path path, final FileStatisticFormat format, final long intervalMs) {
        final var knxClientMock = mock(KnxClient.class);
        final var configMock = mock(Config.class);
        final var emptyStatistic = mock(KnxStatistic.class);
        when(configMock.getSetting(eq(FileStatisticPlugin.PATH))).thenReturn(path);
        when(configMock.getSetting(eq(FileStatisticPlugin.FORMAT))).thenReturn(format);
        when(configMock.getSetting(eq(FileStatisticPlugin.INTERVAL))).thenReturn(intervalMs);
        when(knxClientMock.getConfig()).thenReturn(configMock);
        when(knxClientMock.getStatistic()).thenReturn(emptyStatistic);
        return knxClientMock;
    }

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
        // Tunneling
        when(statistic.getNumberOfBodyReceived(TunnelingRequestBody.class)).thenReturn(22L);
        when(statistic.getNumberOfBodySent(TunnelingRequestBody.class)).thenReturn(23L);
        when(statistic.getNumberOfBodyReceived(TunnelingAckBody.class)).thenReturn(24L);
        when(statistic.getNumberOfBodySent(TunnelingAckBody.class)).thenReturn(25L);
        // Disconnect
        when(statistic.getNumberOfBodyReceived(DisconnectRequestBody.class)).thenReturn(26L);
        when(statistic.getNumberOfBodySent(DisconnectRequestBody.class)).thenReturn(27L);
        when(statistic.getNumberOfBodyReceived(DisconnectResponseBody.class)).thenReturn(28L);
        when(statistic.getNumberOfBodySent(DisconnectResponseBody.class)).thenReturn(29L);
        // Indication
        when(statistic.getNumberOfBodyReceived(RoutingIndicationBody.class)).thenReturn(30L);
        when(statistic.getNumberOfBodySent(RoutingIndicationBody.class)).thenReturn(31L);

        return statistic;
    }
}
