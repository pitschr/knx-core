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

package li.pitschmann.knx.core.plugin.api.v1.controllers;

import li.pitschmann.knx.core.plugin.api.MockApiPlugin;
import li.pitschmann.knx.core.plugin.api.test.MockApiTest;
import li.pitschmann.knx.core.test.MockServerTest;
import org.junit.jupiter.api.DisplayName;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static li.pitschmann.knx.core.plugin.api.TestUtils.readJsonFile;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link StatisticController}
 */
public class StatisticControllerTest {
    /**
     * Test /statistic endpoint
     *
     * @param mockPlugin
     * @throws Exception
     */
    @MockApiTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v20).knxproj"))
    @DisplayName("Test /statistic endpoint")
    public void testStatistic(final MockApiPlugin mockPlugin) throws Exception {
        // get http client for requests
        final var httpClient = HttpClient.newHttpClient();

        // send write request
        final var httpRequest = mockPlugin.newRequestBuilder("/api/v1/statistic").GET().build();
        final var responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();

        assertThat(responseBody).isEqualTo(readJsonFile("/json/StatisticControllerTest-testStatistic.json"));
    }
}
