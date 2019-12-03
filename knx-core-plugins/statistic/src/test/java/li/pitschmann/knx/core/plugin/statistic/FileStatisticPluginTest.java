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

package li.pitschmann.knx.core.plugin.statistic;

import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ConnectResponseBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.ConnectionStateResponseBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DescriptionResponseBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.DisconnectResponseBody;
import li.pitschmann.knx.core.body.RoutingIndicationBody;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.communication.KnxStatistic;
import li.pitschmann.knx.core.config.Config;
import li.pitschmann.knx.core.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

        // we should have two statistics (one at start up and one at shutdown)
        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(2);
        assertThat(lines.get(0)).isEqualTo(
                // @formatter:off
                "{" +
                    "\"inbound\":{" +
                        "\"total\":{\"packets\":0,\"bytes\":0}," +
                        "\"description\":{\"request\":0,\"response\":0}," +
                        "\"connect\":{\"request\":0,\"response\":0}," +
                        "\"connectionState\":{\"request\":0,\"response\":0}," +
                        "\"tunneling\":{\"request\":0,\"acknowledge\":0}," +
                        "\"indication\":{\"request\":0,\"response\":0}," +
                        "\"disconnect\":{\"request\":0,\"response\":0}" +
                    "}," +
                    "\"outbound\":{" +
                        "\"total\":{\"packets\":0,\"bytes\":0}," +
                        "\"description\":{\"request\":0,\"response\":0}," +
                        "\"connect\":{\"request\":0,\"response\":0}," +
                        "\"connectionState\":{\"request\":0,\"response\":0}," +
                        "\"tunneling\":{\"request\":0,\"acknowledge\":0}," +
                        "\"indication\":{\"request\":0,\"response\":0}," +
                        "\"disconnect\":{\"request\":0,\"response\":0}" +
                    "}," +
                    "\"error\":{" +
                        "\"total\":{\"packets\":0,\"rate\":0.00}" +
                    "}" +
                "}"
                // @formatter:on
        );
        assertThat(lines.get(1)).isEqualTo(
                // @formatter:off
                "{" +
                    "\"inbound\":{" +
                        "\"total\":{\"packets\":10,\"bytes\":11}," +
                        "\"description\":{\"request\":0,\"response\":21}," +
                        "\"connect\":{\"request\":0,\"response\":31}," +
                        "\"connectionState\":{\"request\":0,\"response\":41}," +
                        "\"tunneling\":{\"request\":50,\"acknowledge\":51}," +
                        "\"indication\":{\"request\":0,\"response\":60}," +
                        "\"disconnect\":{\"request\":70,\"response\":71}" +
                    "}," +
                    "\"outbound\":{" +
                        "\"total\":{\"packets\":12,\"bytes\":13}," +
                        "\"description\":{\"request\":22,\"response\":0}," +
                        "\"connect\":{\"request\":32,\"response\":0}," +
                        "\"connectionState\":{\"request\":42,\"response\":0}," +
                        "\"tunneling\":{\"request\":52,\"acknowledge\":53}," +
                        "\"indication\":{\"request\":61,\"response\":0}," +
                        "\"disconnect\":{\"request\":72,\"response\":73}" +
                    "}," +
                    "\"error\":{" +
                        "\"total\":{\"packets\":14,\"rate\":1.50}" +
                    "}" +
                "}"
                // @formatter:on
        );
    }

    @Test
    @DisplayName("TSV: Test File Statistic")
    public void statisticTsv() throws IOException {
        final var path = Paths.get("target/test-FileStatisticPluginTest-statisticTsv-" + UUID.randomUUID() + ".log");
        final var plugin = new FileStatisticPlugin();
        final var knxClientMock = mockKnxClient(path, FileStatisticFormat.TSV, TimeUnit.MINUTES.toMillis(1));

        // start
        plugin.onInitialization(knxClientMock);
        plugin.onStart();

        // wait 1 second
        Sleeper.seconds(1);

        // shutdown
        final var statisticAtShutdown = createKnxStatisticMock();
        when(knxClientMock.getStatistic()).thenReturn(statisticAtShutdown);
        plugin.onShutdown();

        // we should have two statistics (one at start up and one at shutdown)
        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(3);
        assertThat(lines.get(0)).isEqualTo(
                // @formatter:off
                // total
                "Inbound Packets\tInbound Bytes\t" +
                "Outbound Packets\tOutbound Bytes\t" +
                "Error Packets\tError Rate (%)\t" +
                // Inbound
                "Inbound Description Requests\tInbound Description Responses\t" +
                "Inbound Connect Requests\tInbound Connect Responses\t" +
                "Inbound Connection State Requests\tInbound Connection State Responses\t" +
                "Inbound Disconnect Requests\tInbound Disconnect Responses\t" +
                "Inbound Tunneling Requests\tInbound Tunneling Acknowledges\t" +
                "Inbound Indication Requests\tInbound Indication Responses\t" +
                // outbound
                "Outbound Description Requests\tOutbound Description Responses\t" +
                "Outbound Connect Requests\tOutbound Connect Responses\t" +
                "Outbound Connection State Requests\tOutbound Connection State Responses\t" +
                "Outbound Disconnect Requests\tOutbound Disconnect Responses\t" +
                "Outbound Tunneling Requests\tOutbound Tunneling Acknowledges\t" +
                "Outbound Indication Requests\tOutbound Indication Responses"
                // @formatter:on

        );
        assertThat(lines.get(1)).isEqualTo(
                // @formatter:off
                "0\t0\t" +                            // inbound total
                "0\t0\t" +                            // outbound total
                "0\t0.00\t" +                         // error total
                "0\t0\t0\t0\t0\t0\t0\t0\t" +          // inbound description, connect, connectionState, disconnect
                "0\t0\t0\t0\t" +                      // inbound tunneling, indication
                "0\t0\t0\t0\t0\t0\t0\t0\t" +          // outbound description, connect, connectionState, disconnect
                "0\t0\t0\t0"                          // outbound tunneling, indication
                // @formatter:on
        );
        assertThat(lines.get(2)).isEqualTo(
                // @formatter:off
                "10\t11\t" +                           // inbound total
                "12\t13\t" +                           // outbound total
                "14\t1.50\t" +                         // error total
                "0\t21\t0\t31\t0\t41\t70\t71\t" +      // inbound description, connect, connectionState, disconnect
                "50\t51\t0\t60\t" +                    // inbound tunneling, indication
                "22\t0\t32\t0\t42\t0\t72\t73\t" +      // outbound description, connect, connectionState, disconnect
                "52\t53\t61\t0"                        // outbound tunneling, indication
                // @formatter:on
        );
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

        // we should have two statistics (one at start up and one at shutdown)
        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(2 * 16); // 1 statistic output = 16 lines for TEXT
        var i = 0;
        assertThat(lines.get(i++)).isEqualTo("0 packets received (0 bytes)");
        assertThat(lines.get(i++)).isEqualTo("\t[Description     ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connect         ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connection State] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Tunneling       ] Request: 0, Acknowledge: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Indication      ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Disconnect      ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("0 packets sent (0 bytes)");
        assertThat(lines.get(i++)).isEqualTo("\t[Description     ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connect         ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connection State] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Tunneling       ] Request: 0, Acknowledge: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Indication      ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Disconnect      ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("0 errors (0.00%)");
        assertThat(lines.get(i++)).isEqualTo("-----------------------------------------------------------------");
        assertThat(lines.get(i++)).isEqualTo("10 packets received (11 bytes)");
        assertThat(lines.get(i++)).isEqualTo("\t[Description     ] Request: 0, Response: 21");
        assertThat(lines.get(i++)).isEqualTo("\t[Connect         ] Request: 0, Response: 31");
        assertThat(lines.get(i++)).isEqualTo("\t[Connection State] Request: 0, Response: 41");
        assertThat(lines.get(i++)).isEqualTo("\t[Tunneling       ] Request: 50, Acknowledge: 51");
        assertThat(lines.get(i++)).isEqualTo("\t[Indication      ] Request: 0, Response: 60");
        assertThat(lines.get(i++)).isEqualTo("\t[Disconnect      ] Request: 70, Response: 71");
        assertThat(lines.get(i++)).isEqualTo("12 packets sent (13 bytes)");
        assertThat(lines.get(i++)).isEqualTo("\t[Description     ] Request: 22, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connect         ] Request: 32, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connection State] Request: 42, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Tunneling       ] Request: 52, Acknowledge: 53");
        assertThat(lines.get(i++)).isEqualTo("\t[Indication      ] Request: 61, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Disconnect      ] Request: 72, Response: 73");
        assertThat(lines.get(i++)).isEqualTo("14 errors (1.50%)");
        assertThat(lines.get(i++)).isEqualTo("-----------------------------------------------------------------");
    }

    private KnxClient mockKnxClient(final Path path, final FileStatisticFormat format, final long intervalMs) {
        final var knxClientMock = mock(KnxClient.class);
        final var configMock = mock(Config.class);
        final var emptyStatistic = mock(KnxStatistic.class);

        when(knxClientMock.getConfig()).thenReturn(configMock);
        when(knxClientMock.getConfig(any())).thenCallRealMethod();

        when(configMock.getValue(eq(FileStatisticPlugin.PATH))).thenReturn(path);
        when(configMock.getValue(eq(FileStatisticPlugin.FORMAT))).thenReturn(format);
        when(configMock.getValue(eq(FileStatisticPlugin.INTERVAL_MS))).thenReturn(intervalMs);

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
        when(statistic.getNumberOfBodyReceived(DescriptionRequestBody.class)).thenReturn(20L); // not used
        when(statistic.getNumberOfBodyReceived(DescriptionResponseBody.class)).thenReturn(21L);
        when(statistic.getNumberOfBodySent(DescriptionRequestBody.class)).thenReturn(22L);
        when(statistic.getNumberOfBodySent(DescriptionResponseBody.class)).thenReturn(23L); // not used
        // Connect
        when(statistic.getNumberOfBodyReceived(ConnectRequestBody.class)).thenReturn(30L); // not used
        when(statistic.getNumberOfBodyReceived(ConnectResponseBody.class)).thenReturn(31L);
        when(statistic.getNumberOfBodySent(ConnectRequestBody.class)).thenReturn(32L);
        when(statistic.getNumberOfBodySent(ConnectResponseBody.class)).thenReturn(33L); // not used
        // Connection State
        when(statistic.getNumberOfBodyReceived(ConnectionStateRequestBody.class)).thenReturn(40L); // not used
        when(statistic.getNumberOfBodyReceived(ConnectionStateResponseBody.class)).thenReturn(41L);
        when(statistic.getNumberOfBodySent(ConnectionStateRequestBody.class)).thenReturn(42L);
        when(statistic.getNumberOfBodySent(ConnectionStateResponseBody.class)).thenReturn(43L); // not used
        // Tunneling
        when(statistic.getNumberOfBodyReceived(TunnelingRequestBody.class)).thenReturn(50L);
        when(statistic.getNumberOfBodyReceived(TunnelingAckBody.class)).thenReturn(51L);
        when(statistic.getNumberOfBodySent(TunnelingRequestBody.class)).thenReturn(52L);
        when(statistic.getNumberOfBodySent(TunnelingAckBody.class)).thenReturn(53L);
        // Indication
        when(statistic.getNumberOfBodyReceived(RoutingIndicationBody.class)).thenReturn(60L);
        when(statistic.getNumberOfBodySent(RoutingIndicationBody.class)).thenReturn(61L);
        // Disconnect
        when(statistic.getNumberOfBodyReceived(DisconnectRequestBody.class)).thenReturn(70L);
        when(statistic.getNumberOfBodyReceived(DisconnectResponseBody.class)).thenReturn(71L);
        when(statistic.getNumberOfBodySent(DisconnectRequestBody.class)).thenReturn(72L);
        when(statistic.getNumberOfBodySent(DisconnectResponseBody.class)).thenReturn(73L);

        return statistic;
    }
}
