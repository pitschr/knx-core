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

import li.pitschmann.knx.core.plugin.api.v1.gson.DaemonGsonEngine;
import li.pitschmann.knx.core.plugin.api.v1.json.ReadRequest;
import li.pitschmann.knx.core.plugin.api.v1.json.ReadResponse;
import li.pitschmann.knx.core.body.address.GroupAddress;
import li.pitschmann.knx.core.datapoint.DPT12;
import li.pitschmann.knx.core.plugin.api.test.MockDaemonTest;
import li.pitschmann.knx.core.plugin.api.test.MockHttpDaemonPlugin;
import li.pitschmann.knx.core.test.MockServerTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import ro.pippo.controller.Controller;
import ro.pippo.core.HttpConstants;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static li.pitschmann.knx.core.plugin.api.test.TestUtils.asJson;
import static li.pitschmann.knx.core.plugin.api.test.TestUtils.randomGroupAddress;
import static li.pitschmann.knx.core.plugin.api.test.TestUtils.readJsonFile;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ReadRequestController}
 */
public class ReadRequestControllerTest {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Tests the /read endpoint for group addresses using KNX mock server
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v14).knxproj"))
    @DisplayName("OK: Read Request for group addresses using KNX mock server")
    public void testRead(final MockHttpDaemonPlugin daemon) throws Exception {
        final var groupAddress = GroupAddress.of(0, 3, 18);

        // create read request
        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        // do a call with all parameters
        final var httpRequest = daemon.newRequestBuilder("/api/v1/read?expand=*").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(request))).build();
        final var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertThat(httpResponse.statusCode()).isEqualTo(HttpConstants.StatusCode.OK);

        // verify ReadResponse
        final var readResponse = DaemonGsonEngine.INSTANCE.fromString(httpResponse.body(), ReadResponse.class);
        assertThat(readResponse.getGroupAddress()).isEqualTo(groupAddress);
        assertThat(readResponse.getName()).isEqualTo("Sub Group - DPT 12 (0x80 02 70 FF)");
        assertThat(readResponse.getDescription()).isEqualTo("4-bytes, unsigned (2147643647)");
        assertThat(readResponse.getDataPointType()).isEqualTo(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT);
        assertThat(readResponse.getValue()).isEqualTo("2147643647");
        assertThat(readResponse.getUnit()).isEqualTo("pulses");
        assertThat(readResponse.getRaw()).containsExactly(0x80, 0x02, 0x70, 0xFF);
        assertThat(readResponse).hasToString(
                String.format("ReadResponse{name=%s, description=%s, dataPointType=%s, raw=0x80 02 70 FF}",
                        readResponse.getName(), //
                        readResponse.getDescription(), //
                        readResponse.getDataPointType() //
                ));

        // verify json
        final var responseJson = asJson(readResponse);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ReadRequestControllerTest-testRead.json"));
    }

    /**
     * Read Request for an existing group address in XML project file,
     * but no ack body could be found (or retrieved yet) due a thrown
     * exception.
     */
    @ControllerTest(ReadRequestController.class)
    @DisplayName("Error: Read Request without found ack body due a thrown exception from KNX client")
    public void testReadException(final Controller controller) {
        final var readRequestController = (ReadRequestController) controller;
        final var groupAddress = randomGroupAddress();

        //
        // Mocking
        //

        // mock no ack body was found
        when(readRequestController.getKnxClient().readRequest(groupAddress)).thenReturn(false);

        //
        // Verification
        //

        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        final var response = readRequestController.readRequest(request);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.INTERNAL_ERROR);
        Assertions.assertThat(response.getGroupAddress()).isNull();
        Assertions.assertThat(response.getName()).isNull();
        Assertions.assertThat(response.getDescription()).isNull();
        Assertions.assertThat(response.getDataPointType()).isNull();
        Assertions.assertThat(response.getValue()).isNull();
        Assertions.assertThat(response.getRaw()).isNull();

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }

    /**
     * Read Request causing an internal timeout because KNX client didn't receive a response
     * from KNX Net/IP device yet (and therefore no status data available in the status pool).
     */
    @ControllerTest(ReadRequestController.class)
    @DisplayName("Error: Read Request without available KNX status data from KNX Client (internal timeout)")
    public void testReadInternalTimeout(final Controller controller) {
        final var readRequestController = (ReadRequestController) controller;
        final var groupAddress = randomGroupAddress();

        //
        // Verification
        //

        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        final var response = readRequestController.readRequest(request);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.NOT_FOUND);
        Assertions.assertThat(response.getGroupAddress()).isNull();
        Assertions.assertThat(response.getName()).isNull();
        Assertions.assertThat(response.getDescription()).isNull();
        Assertions.assertThat(response.getDataPointType()).isNull();
        Assertions.assertThat(response.getValue()).isNull();
        Assertions.assertThat(response.getRaw()).isNull();

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }

    /**
     * Tests the read endpoint for an unknown group address
     */
    @ControllerTest(ReadRequestController.class)
    @DisplayName("Error: Read Request for an unknown group address")
    public void testReadUnknownGroupAddress(final Controller controller) {
        final var readRequestController = (ReadRequestController) controller;
        final var groupAddress = randomGroupAddress();

        //
        // Mocking
        //

        // mock an non-existing xml group address
        when(readRequestController.getXmlProject().getGroupAddress(any(GroupAddress.class))).thenReturn(null);

        //
        // Verification
        //

        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        final var response = readRequestController.readRequest(request);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.BAD_REQUEST);
        Assertions.assertThat(response.getGroupAddress()).isNull();
        Assertions.assertThat(response.getName()).isNull();
        Assertions.assertThat(response.getDescription()).isNull();
        Assertions.assertThat(response.getDataPointType()).isNull();
        Assertions.assertThat(response.getValue()).isNull();
        Assertions.assertThat(response.getRaw()).isNull();

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }
}
