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

package li.pitschmann.knx.daemon.controllers;

import li.pitschmann.knx.server.MockDaemonTest;
import li.pitschmann.knx.server.MockHttpDaemon;
import li.pitschmann.knx.server.MockServerTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link StatisticController}
 */
public class StatisticControllerTest {
    /**
     * Test /statistic endpoint
     *
     * @param daemon
     * @throws Exception
     */
    @Disabled
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/parser/Project (3-Level, v14).knxproj"))
    @DisplayName("Test /statistic endpoint")
    public void testStatistic(final MockHttpDaemon daemon) throws Exception {
        // get http client for requests
        final var httpClient = HttpClient.newHttpClient();

        // send write request
        final var httpRequest = daemon.newRequestBuilder("/statistic").GET().build();
        final var responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();

        // TODO: JSON should be more pretty!
        // TODO: we should not rely on the size of bytes (regular expression?)

        // @formatter:off
        assertThat(responseBody).isEqualTo(
                "{" +
                    "\"numberOfBodyReceivedMap\":{" +
                        "\"class li.pitschmann.knx.link.body.DescriptionResponseBody\":1," +
                        "\"class li.pitschmann.knx.link.body.ConnectionStateResponseBody\":1," +
                        "\"class li.pitschmann.knx.link.body.ConnectResponseBody\":1" +
                    "}," +
                    "\"numberOfBodySentMap\":{" +
                        "\"class li.pitschmann.knx.link.body.DescriptionRequestBody\":1," +
                        "\"class li.pitschmann.knx.link.body.ConnectRequestBody\":1," +
                        "\"class li.pitschmann.knx.link.body.ConnectionStateRequestBody\":1" +
                    "}," +
                    "\"numberOfBodyReceived\":3," +
                    "\"numberOfBodySent\":3," +
                    "\"numberOfBytesReceived\":224," +
                    "\"numberOfBytesSent\":182," +
                    "\"numberOfErrors\":0," +
                    "\"errorRate\":0.0" +
                "}");
        // @formatter:on
    }
}
