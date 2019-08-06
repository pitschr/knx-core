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
import li.pitschmann.knx.daemon.v1.json.StatusRequest;
import li.pitschmann.knx.daemon.v1.json.StatusResponse;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.body.cemi.APCI;
import li.pitschmann.knx.link.datapoint.DPT2;
import li.pitschmann.knx.test.MockDaemonTest;
import li.pitschmann.knx.test.MockHttpDaemon;
import li.pitschmann.knx.test.MockServerTest;
import org.junit.jupiter.api.DisplayName;
import ro.pippo.core.HttpConstants;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link StatusController}
 */
public class StatusControllerTest {
    /**
     * Test /status endpoint for all available status in KNX client pool
     *
     * @param daemon
     * @throws Exception
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v14).knxproj"))
    @DisplayName("Test /status endpoint for all available status in KNX client pool")
    public void testAllStatus(final MockHttpDaemon daemon) throws Exception {
        // get http client for requests
        final var httpClient = HttpClient.newHttpClient();
        final var request = daemon.newRequestBuilder("/api/v1/status?expand=*").GET().build();
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // verify response
        assertThat(response.statusCode()).isEqualTo(207);
        assertThat(response.body()).isEqualTo("[]");
    }

    /**
     * Test /status endpoint for group address 0/0/22
     *
     * @param daemon
     * @throws Exception
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v14).knxproj"))
    @DisplayName("Test /status endpoint for group address 0/0/22")
    public void testStatus(final MockHttpDaemon daemon) throws Exception {
        // get http client for requests
        final var httpClient = HttpClient.newHttpClient();
        final var groupAddress = GroupAddress.of(0, 0, 22);

        // create status and read requests
        final var statusRequest = new StatusRequest();
        statusRequest.setGroupAddress(groupAddress);

        final var readRequest = new ReadRequest();
        readRequest.setGroupAddress(groupAddress);

        //
        // Request #1
        //

        // send status request #1 - we will get an error (not found) because we never requested for
        // this group address yet and therefore the status doesn't exists in the status pool yet
        final var httpRequest = daemon.newRequestBuilder("/api/v1/status").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(statusRequest))).build();
        final var httpResponseBeforeRead = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertThat(httpResponseBeforeRead.statusCode()).isEqualTo(HttpConstants.StatusCode.NOT_FOUND);
        assertThat(httpResponseBeforeRead.body()).isEqualTo("{\"status\":\"ERROR\"}");

        // send /read request
        final var httpReadRequest = daemon.newRequestBuilder("/api/v1/read?expand=raw").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(readRequest))).build();
        httpClient.send(httpReadRequest, HttpResponse.BodyHandlers.ofString());

        // re-request for status - we should get an successful message here
        final var httpResponseAfterRead = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertThat(httpResponseAfterRead.statusCode()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(httpResponseAfterRead.body()).isEqualTo("{\"status\":\"OK\"}");

        //
        // Request #2
        //

        // re-request for status but this time with all expand parameters
        final var httpRequestAllExpands = daemon.newRequestBuilder("/api/v1/status?expand=*").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(statusRequest))).build();
        final var httpResponseAllExpands = httpClient.send(httpRequestAllExpands, HttpResponse.BodyHandlers.ofString());
        assertThat(httpResponseAllExpands.statusCode()).isEqualTo(HttpConstants.StatusCode.OK);

        final var httpResponseAllExpandsObj = DaemonGsonEngine.INSTANCE.fromString(httpResponseAllExpands.body(), StatusResponse.class);
        assertThat(httpResponseAllExpandsObj.getName()).isEqualTo("Sub Group - DPT 2 (0x02)");
        assertThat(httpResponseAllExpandsObj.getDescription()).isEqualTo("1-bit, controlled (control, false)");
        assertThat(httpResponseAllExpandsObj.getDataPointType()).isEqualTo(DPT2.SWITCH_CONTROL);
        assertThat(httpResponseAllExpandsObj.getRaw()).containsExactly(0x02);
        assertThat(httpResponseAllExpandsObj.getGroupAddress().getAddressLevel3()).isEqualTo("0/0/22");
        assertThat(httpResponseAllExpandsObj.getTimestamp()).isNotNull();
        assertThat(httpResponseAllExpandsObj.getSourceAddress()).isInstanceOf(IndividualAddress.class);
        assertThat(httpResponseAllExpandsObj.getApci()).isEqualTo(APCI.GROUP_VALUE_RESPONSE);
        assertThat(httpResponseAllExpandsObj.isDirty()).isFalse();

        //
        // Request #3
        //

        final var httpRequestAllStatus = daemon.newRequestBuilder("/api/v1/status?expand=groupAddress,name").GET().build();
        final var httpResponseAllStatus = httpClient.send(httpRequestAllStatus, HttpResponse.BodyHandlers.ofString());
        assertThat(httpResponseAllStatus.statusCode()).isEqualTo(207);

        @SuppressWarnings("unchecked")
        final var httpResponseAllStatusObj = (List<StatusResponse>)DaemonGsonEngine.INSTANCE.fromString(httpResponseAllStatus.body(), List.class);
        assertThat(httpResponseAllStatusObj).hasSize(1);

        // assert the body
        final var bodyAllStatus = httpResponseAllStatus.body();
        // @formatter:off
        assertThat(bodyAllStatus).isEqualTo(
                "[" +
                    "{" +
                        "\"groupAddress\":{" +
                            "\"type\":1," +
                            "\"format\":{" +
                                "\"free_level\":\"22\"," +
                                "\"two_level\":\"0/22\"," +
                                "\"three_level\":\"0/0/22\"" +
                            "}," +
                            "\"raw\":[0,22]" +
                        "}," +
                        "\"name\":\"Sub Group - DPT 2 (0x02)\"," +
                        "\"status\":\"OK\"" +
                    "}" +
                "]");
        // @formatter:on
    }

    /**
     * Test /status endpoint for an unknown group address (0/0/255)
     *
     * @param daemon
     * @throws Exception
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v14).knxproj"))
    @DisplayName("Test /status endpoint for unknown group address")
    public void testStatusUnknownGroupAddress(final MockHttpDaemon daemon) throws Exception {
        // get http client for requests
        final var httpClient = HttpClient.newHttpClient();
        final var groupAddress = GroupAddress.of(0, 0, 255); // unknown group address!

        // create status request
        final var statusRequest = new StatusRequest();
        statusRequest.setGroupAddress(groupAddress);

        // send status request for an unknown group address in XML project
        final var httpRequest = daemon.newRequestBuilder("/api/v1/status").POST(HttpRequest.BodyPublishers.ofString(DaemonGsonEngine.INSTANCE.toString(statusRequest))).build();
        final var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertThat(httpResponse.statusCode()).isEqualTo(HttpConstants.StatusCode.NOT_FOUND);
        assertThat(httpResponse.body()).isEqualTo("{\"status\":\"ERROR\"}");
    }
}
