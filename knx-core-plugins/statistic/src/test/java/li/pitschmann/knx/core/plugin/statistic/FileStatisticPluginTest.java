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
import li.pitschmann.knx.core.body.SearchRequestBody;
import li.pitschmann.knx.core.body.SearchResponseBody;
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
                        "\"search\":{\"request\":0,\"response\":0}," +
                        "\"description\":{\"request\":0,\"response\":0}," +
                        "\"connect\":{\"request\":0,\"response\":0}," +
                        "\"connectionState\":{\"request\":0,\"response\":0}," +
                        "\"tunneling\":{\"request\":0,\"acknowledge\":0}," +
                        "\"indication\":{\"request\":0,\"response\":0}," +
                        "\"disconnect\":{\"request\":0,\"response\":0}" +
                    "}," +
                    "\"outbound\":{" +
                        "\"total\":{\"packets\":0,\"bytes\":0}," +
                        "\"search\":{\"request\":0,\"response\":0}," +
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
                        "\"search\":{\"request\":100,\"response\":110}," +
                        "\"description\":{\"request\":0,\"response\":210}," +
                        "\"connect\":{\"request\":0,\"response\":310}," +
                        "\"connectionState\":{\"request\":0,\"response\":410}," +
                        "\"tunneling\":{\"request\":500,\"acknowledge\":510}," +
                        "\"indication\":{\"request\":0,\"response\":600}," +
                        "\"disconnect\":{\"request\":700,\"response\":710}" +
                    "}," +
                    "\"outbound\":{" +
                        "\"total\":{\"packets\":12,\"bytes\":13}," +
                        "\"search\":{\"request\":120,\"response\":130}," +
                        "\"description\":{\"request\":220,\"response\":0}," +
                        "\"connect\":{\"request\":320,\"response\":0}," +
                        "\"connectionState\":{\"request\":420,\"response\":0}," +
                        "\"tunneling\":{\"request\":520,\"acknowledge\":530}," +
                        "\"indication\":{\"request\":610,\"response\":0}," +
                        "\"disconnect\":{\"request\":720,\"response\":730}" +
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
                "Inbound Search Requests\tInbound Search Responses\t" +
                "Inbound Description Requests\tInbound Description Responses\t" +
                "Inbound Connect Requests\tInbound Connect Responses\t" +
                "Inbound Connection State Requests\tInbound Connection State Responses\t" +
                "Inbound Disconnect Requests\tInbound Disconnect Responses\t" +
                "Inbound Tunneling Requests\tInbound Tunneling Acknowledges\t" +
                "Inbound Indication Requests\tInbound Indication Responses\t" +
                // outbound
                "Outbound Search Requests\tOutbound Search Responses\t" +
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
                "0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t" +    // inbound search, description, connect, connectionState, disconnect
                "0\t0\t0\t0\t" +                      // inbound tunneling, indication
                "0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t" +    // outbound search, description, connect, connectionState, disconnect
                "0\t0\t0\t0"                          // outbound tunneling, indication
                // @formatter:on
        );
        assertThat(lines.get(2)).isEqualTo(
                // @formatter:off
                "10\t11\t" +                           // inbound total
                "12\t13\t" +                           // outbound total
                "14\t1.50\t" +                         // error total
                "100\t110\t0\t210\t0\t310\t0\t410\t700\t710\t" +      // inbound search, description, connect, connectionState, disconnect
                "500\t510\t0\t600\t" +                              // inbound tunneling, indication
                "120\t130\t220\t0\t320\t0\t420\t0\t720\t730\t" +      // outbound search, description, connect, connectionState, disconnect
                "520\t530\t610\t0"                                  // outbound tunneling, indication
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
        assertThat(lines).hasSize(2 * 18); // 1 statistic output = 18 lines for TEXT
        var i = 0;
        assertThat(lines.get(i++)).isEqualTo("0 packets received (0 bytes)");
        assertThat(lines.get(i++)).isEqualTo("\t[Search          ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Description     ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connect         ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connection State] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Tunneling       ] Request: 0, Acknowledge: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Indication      ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Disconnect      ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("0 packets sent (0 bytes)");
        assertThat(lines.get(i++)).isEqualTo("\t[Search          ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Description     ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connect         ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connection State] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Tunneling       ] Request: 0, Acknowledge: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Indication      ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Disconnect      ] Request: 0, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("0 errors (0.00%)");
        assertThat(lines.get(i++)).isEqualTo("-----------------------------------------------------------------");
        assertThat(lines.get(i++)).isEqualTo("10 packets received (11 bytes)");
        assertThat(lines.get(i++)).isEqualTo("\t[Search          ] Request: 100, Response: 110");
        assertThat(lines.get(i++)).isEqualTo("\t[Description     ] Request: 0, Response: 210");
        assertThat(lines.get(i++)).isEqualTo("\t[Connect         ] Request: 0, Response: 310");
        assertThat(lines.get(i++)).isEqualTo("\t[Connection State] Request: 0, Response: 410");
        assertThat(lines.get(i++)).isEqualTo("\t[Tunneling       ] Request: 500, Acknowledge: 510");
        assertThat(lines.get(i++)).isEqualTo("\t[Indication      ] Request: 0, Response: 600");
        assertThat(lines.get(i++)).isEqualTo("\t[Disconnect      ] Request: 700, Response: 710");
        assertThat(lines.get(i++)).isEqualTo("12 packets sent (13 bytes)");
        assertThat(lines.get(i++)).isEqualTo("\t[Search          ] Request: 120, Response: 130");
        assertThat(lines.get(i++)).isEqualTo("\t[Description     ] Request: 220, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connect         ] Request: 320, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Connection State] Request: 420, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Tunneling       ] Request: 520, Acknowledge: 530");
        assertThat(lines.get(i++)).isEqualTo("\t[Indication      ] Request: 610, Response: 0");
        assertThat(lines.get(i++)).isEqualTo("\t[Disconnect      ] Request: 720, Response: 730");
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

        // Search
        when(statistic.getNumberOfBodyReceived(SearchRequestBody.class)).thenReturn(100L);
        when(statistic.getNumberOfBodyReceived(SearchResponseBody.class)).thenReturn(110L);
        when(statistic.getNumberOfBodySent(SearchRequestBody.class)).thenReturn(120L);
        when(statistic.getNumberOfBodySent(SearchResponseBody.class)).thenReturn(130L);
        // Description
        when(statistic.getNumberOfBodyReceived(DescriptionRequestBody.class)).thenReturn(20L); // not used
        when(statistic.getNumberOfBodyReceived(DescriptionResponseBody.class)).thenReturn(210L);
        when(statistic.getNumberOfBodySent(DescriptionRequestBody.class)).thenReturn(220L);
        when(statistic.getNumberOfBodySent(DescriptionResponseBody.class)).thenReturn(230L); // not used
        // Connect
        when(statistic.getNumberOfBodyReceived(ConnectRequestBody.class)).thenReturn(300L); // not used
        when(statistic.getNumberOfBodyReceived(ConnectResponseBody.class)).thenReturn(310L);
        when(statistic.getNumberOfBodySent(ConnectRequestBody.class)).thenReturn(320L);
        when(statistic.getNumberOfBodySent(ConnectResponseBody.class)).thenReturn(330L); // not used
        // Connection State
        when(statistic.getNumberOfBodyReceived(ConnectionStateRequestBody.class)).thenReturn(400L); // not used
        when(statistic.getNumberOfBodyReceived(ConnectionStateResponseBody.class)).thenReturn(410L);
        when(statistic.getNumberOfBodySent(ConnectionStateRequestBody.class)).thenReturn(420L);
        when(statistic.getNumberOfBodySent(ConnectionStateResponseBody.class)).thenReturn(430L); // not used
        // Tunneling
        when(statistic.getNumberOfBodyReceived(TunnelingRequestBody.class)).thenReturn(500L);
        when(statistic.getNumberOfBodyReceived(TunnelingAckBody.class)).thenReturn(510L);
        when(statistic.getNumberOfBodySent(TunnelingRequestBody.class)).thenReturn(520L);
        when(statistic.getNumberOfBodySent(TunnelingAckBody.class)).thenReturn(530L);
        // Indication
        when(statistic.getNumberOfBodyReceived(RoutingIndicationBody.class)).thenReturn(600L);
        when(statistic.getNumberOfBodySent(RoutingIndicationBody.class)).thenReturn(610L);
        // Disconnect
        when(statistic.getNumberOfBodyReceived(DisconnectRequestBody.class)).thenReturn(700L);
        when(statistic.getNumberOfBodyReceived(DisconnectResponseBody.class)).thenReturn(710L);
        when(statistic.getNumberOfBodySent(DisconnectRequestBody.class)).thenReturn(720L);
        when(statistic.getNumberOfBodySent(DisconnectResponseBody.class)).thenReturn(730L);

        return statistic;
    }
}
