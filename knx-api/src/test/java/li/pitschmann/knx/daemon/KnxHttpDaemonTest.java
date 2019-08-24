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

import li.pitschmann.knx.daemon.v1.gson.DaemonGsonEngine;
import li.pitschmann.knx.daemon.v1.json.ReadRequest;
import li.pitschmann.knx.daemon.v1.json.WriteRequest;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.datapoint.DPT1;
import li.pitschmann.knx.test.MockDaemonTest;
import li.pitschmann.knx.test.MockHttpDaemon;
import li.pitschmann.knx.test.MockServerTest;
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
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v14).knxproj"))
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
        final var writeHttpRequest = daemon.newRequestBuilder("/api/v1/write").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(writeRequest))).build();

        // send read request #1
        final var readHttpRequest = daemon.newRequestBuilder("/api/v1/read").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(readRequest))).build();
        final var responseBody = httpClient.send(readHttpRequest, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(responseBody).isEqualTo("{}");

        // write 0x01
        final var writeBody = httpClient.send(writeHttpRequest, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(writeBody).isEqualTo("{}");

        // send read request #2
        // - group address: "1-bit (false)" which has been initialized with "false" contains now "true"
        // - it contains the expand 'name' and 'description' which means that we request for group address name and description as well
        final var readHttpRequestAfterWrite = daemon.newRequestBuilder("/api/v1/read?expand=name,description,dpt,raw").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(readRequest))).build();
        final var responseBodyAfterWrite = httpClient.send(readHttpRequestAfterWrite, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(responseBodyAfterWrite).isEqualTo("{" + //
                "\"name\":\"Sub Group - DPT 1 (0x00)\"," + //
                "\"description\":\"1-bit (false)\"," + //
                "\"dataPointType\":\"1.001\"," + //
                "\"raw\":[1]" + //
                "}");
    }
}
