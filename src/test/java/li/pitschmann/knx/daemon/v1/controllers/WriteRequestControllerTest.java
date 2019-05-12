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

import li.pitschmann.knx.daemon.gson.DaemonGsonEngine;
import li.pitschmann.knx.daemon.v1.json.WriteRequest;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.datapoint.DPT2;
import li.pitschmann.knx.server.MockDaemonTest;
import li.pitschmann.knx.server.MockHttpDaemon;
import li.pitschmann.knx.server.MockServerTest;
import org.junit.jupiter.api.DisplayName;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link WriteRequestController}
 */
public class WriteRequestControllerTest {
    /**
     * Test /write endpoint for group address 0/0/22
     *
     * @param daemon
     * @throws Exception
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/parser/Project (3-Level, v14).knxproj"))
    @DisplayName("Test /write endpoint for group address 0/0/22")
    public void testWriteOnly(final MockHttpDaemon daemon) throws Exception {
        // get http client for requests
        final var httpClient = HttpClient.newHttpClient();

        // create write request
        final var writeRequest = new WriteRequest();
        writeRequest.setGroupAddress(GroupAddress.of(0, 0, 22));
        writeRequest.setDataPointType(DPT2.ALARM_CONTROL);
        writeRequest.setValues("control", "false");

        // send write request
        final var httpRequest = daemon.newRequestBuilder("/api/v1/write").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(writeRequest))).build();
        final var responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(responseBody).isEqualTo("{\"status\":\"OK\"}");
    }
}
