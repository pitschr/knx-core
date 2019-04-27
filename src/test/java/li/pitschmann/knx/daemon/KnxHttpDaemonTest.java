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

package li.pitschmann.knx.daemon;

import li.pitschmann.knx.daemon.gson.DaemonGsonEngine;
import li.pitschmann.knx.daemon.json.ReadRequest;
import li.pitschmann.knx.daemon.json.WriteRequest;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.datapoint.DPT1;
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
 * Test class for {@link DefaultHttpDaemon}
 */
public class KnxHttpDaemonTest {
    /**
     * Tests the combination of /read and /write requests
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/parser/Project (3-Level, v14).knxproj"))
    @DisplayName("Test /read and /write endpoints for group address 0/0/10")
    public void testReadAndWrite(final MockHttpDaemon daemon) throws Exception {
        // get http client for requests
        final var httpClient = HttpClient.newHttpClient();

        final var groupAddress = GroupAddress.of(0, 0, 10);

        // create read request for before and after write request
        final var readRequest = new ReadRequest();
        readRequest.setGroupAddress(groupAddress);

        // create write request
        final var writeRequest = new WriteRequest();
        writeRequest.setGroupAddress(groupAddress);
        writeRequest.setDataPointType(DPT1.SWITCH);
        writeRequest.setValues("true");
        final var writeHttpRequest = daemon.newRequestBuilder("/write").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(writeRequest))).build();

        // send read request #1
        final var readHttpRequest = daemon.newRequestBuilder("/read").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(readRequest))).build();
        final var responseBody = httpClient.send(readHttpRequest, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(responseBody).isEqualTo("{\"dataPointType\":\"1.001\",\"raw\":[0],\"status\":\"OK\"}");

        // write 0x01
        final var writeBody = httpClient.send(writeHttpRequest, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(writeBody).isEqualTo("{\"status\":\"OK\"}");

        // send read request #2
        // - group address: "1-bit (false)" which has been initialized with "false" contains now "true"
        // - it contains the expand 'name' and 'description' which means that we request for group address name and description as well
        final var readHttpRequestAfterWrite = daemon.newRequestBuilder("/read?expand=name,description").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(readRequest))).build();
        final var responseBodyAfterWrite = httpClient.send(readHttpRequestAfterWrite, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(responseBodyAfterWrite).isEqualTo("{\"name\":\"Sub Group - DPT 1 (0x00)\",\"description\":\"1-bit (false)\",\"dataPointType\":\"1.001\",\"raw\":[1],\"status\":\"OK\"}");
    }

    /**
     * Tests the /read endpoint for group addresses 0/0/59 and 1/3/47
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
        final var httpRequest = daemon.newRequestBuilder("/read").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(readRequest))).build();
        final var responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(responseBody).isEqualTo("{\"dataPointType\":\"5.001\",\"raw\":[-86],\"status\":\"OK\"}");

        //
        // Test #2
        //
        // create read request #2
        final var readRequest2 = new ReadRequest();
        readRequest2.setGroupAddress(GroupAddress.of(0, 3, 47));

        // send read request #2
        final var httpRequest2 = daemon.newRequestBuilder("/read").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(readRequest2))).build();
        final var responseBody2 = httpClient.send(httpRequest2, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(responseBody2).isEqualTo("{\"dataPointType\":\"15.000\",\"raw\":[-64,-80,-96,-25],\"status\":\"OK\"}");
    }

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
        final var httpRequest = daemon.newRequestBuilder("/write").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(writeRequest))).build();
        final var responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(responseBody).isEqualTo("{\"status\":\"OK\"}");
    }
}
