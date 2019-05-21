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
import li.pitschmann.knx.daemon.v1.json.ReadRequest;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.server.MockDaemonTest;
import li.pitschmann.knx.server.MockHttpDaemon;
import li.pitschmann.knx.server.MockServerTest;
import org.junit.jupiter.api.DisplayName;
import ro.pippo.core.HttpConstants;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link ReadRequestController}
 */
public class ReadRequestControllerTest {
    /**
     * Tests the /read endpoint for group addresses 0/0/56 and 0/3/47
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/parser/Project (3-Level, v14).knxproj"))
    @DisplayName("Test /read endpoint for group addresses 0/0/59 and 1/3/47")
    public void testReadOnly(final MockHttpDaemon daemon) throws Exception {
        // get http client for requests
        final var httpClient = HttpClient.newHttpClient();

        //
        // Test #1
        //
        // create read request #1
        final var readRequest = new ReadRequest();
        readRequest.setGroupAddress(GroupAddress.of(0, 0, 56));

        // send read request #1
        final var httpRequest = daemon.newRequestBuilder("/api/v1/read").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(readRequest))).build();
        final var responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(responseBody).isEqualTo("{\"status\":\"OK\"}");

        //
        // Test #2 (with dpt and raw data)
        //
        // create read request #2
        final var readRequest2 = new ReadRequest();
        readRequest2.setGroupAddress(GroupAddress.of(0, 3, 47));

        // send read request #2
        final var httpRequest2 = daemon.newRequestBuilder("/api/v1/read?expand=dpt,raw").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(readRequest2))).build();
        final var responseBody2 = httpClient.send(httpRequest2, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(responseBody2).isEqualTo("{" + //
                "\"dataPointType\":\"15.000\"," + //
                "\"raw\":[-64,-80,-96,-25]," + //
                "\"status\":\"OK\"" + //
                "}");
    }

    /**
     * Tests the /read endpoint for an unknown group addresses (0/0/255)
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/parser/Project (3-Level, v14).knxproj"))
    @DisplayName("Test /read endpoint for an unknown group addresses")
    public void testReadUnknownGroupAddress(final MockHttpDaemon daemon) throws Exception {
        // get http client for requests
        final var httpClient = HttpClient.newHttpClient();

        // create read request
        final var readRequest = new ReadRequest();
        readRequest.setGroupAddress(GroupAddress.of(0, 0, 255));

        // send read request
        final var httpRequest = daemon.newRequestBuilder("/api/v1/read").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(readRequest))).build();
        final var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertThat(httpResponse.statusCode()).isEqualTo(HttpConstants.StatusCode.NOT_FOUND);
        assertThat(httpResponse.body()).isEqualTo("{\"status\":\"ERROR\"}");
    }
}
