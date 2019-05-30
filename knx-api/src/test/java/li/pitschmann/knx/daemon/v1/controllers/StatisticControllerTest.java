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

package li.pitschmann.knx.daemon.v1.controllers;

import li.pitschmann.knx.test.MockDaemonTest;
import li.pitschmann.knx.test.MockHttpDaemon;
import li.pitschmann.knx.test.MockServerTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link StatisticController}
 */
@Disabled
public class StatisticControllerTest {
    /**
     * Test /statistic endpoint
     *
     * @param daemon
     * @throws Exception
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v14).knxproj"))
    @DisplayName("Test /statistic endpoint")
    public void testStatistic(final MockHttpDaemon daemon) throws Exception {
        // get http client for requests
        final var httpClient = HttpClient.newHttpClient();

        // send write request
        final var httpRequest = daemon.newRequestBuilder("/api/v1/statistic").GET().build();
        final var responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();

        // @formatter:off
        assertThat(responseBody).isEqualTo(
                "{" +
                    "\"inbound\":{" +
                        "\"total\":{" +
                            "\"packets\":3," +
                            "\"bytes\":224" +
                        "}," +
                        "\"description\":{\"request\":0,\"response\":1}," +
                        "\"connect\":{\"request\":0,\"response\":1}," +
                        "\"connection_state\":{\"request\":0,\"response\":1}," +
                        "\"disconnect\":{\"request\":0,\"response\":0}," +
                        "\"tunneling\":{\"request\":0,\"response\":0}" +
                    "}," +
                    "\"outbound\":{" +
                        "\"total\":{" +
                            "\"packets\":3," +
                            "\"bytes\":182" +
                        "}," +
                        "\"description\":{\"request\":1,\"response\":0}," +
                        "\"connect\":{\"request\":1,\"response\":0}," +
                        "\"connection_state\":{\"request\":1,\"response\":0}," +
                        "\"disconnect\":{\"request\":0,\"response\":0}," +
                        "\"tunneling\":{\"request\":0,\"response\":0}" +
                    "}," +
                    "\"error\":{" +
                        "\"total\":{" +
                            "\"packets\":0," +
                            "\"rate\":0.0" +
                        "}" +
                    "}" +
                "}"
        );
        // @formatter:on
    }
}
