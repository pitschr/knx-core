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

import li.pitschmann.knx.core.body.address.GroupAddress;
import li.pitschmann.knx.core.datapoint.DPT1;
import li.pitschmann.knx.core.datapoint.DPT2;
import li.pitschmann.knx.core.plugin.api.gson.ApiGsonEngine;
import li.pitschmann.knx.core.plugin.api.MockApiPlugin;
import li.pitschmann.knx.core.plugin.api.test.MockApiTest;
import li.pitschmann.knx.core.plugin.api.v1.json.WriteRequest;
import li.pitschmann.knx.core.test.MockServerTest;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import ro.pippo.controller.Controller;
import ro.pippo.core.HttpConstants;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static li.pitschmann.knx.core.plugin.api.TestUtils.asJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link WriteRequestController}
 */
public class WriteRequestControllerTest {
    /**
     * Test /write endpoint for group address 0/0/22
     *
     * @param mockPlugin
     * @throws Exception
     */
    @MockApiTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v20).knxproj"))
    @DisplayName("OK: Write Request for group address 0/0/22")
    public void testWrite(final MockApiPlugin mockPlugin) throws Exception {
        // get http client for requests
        final var httpClient = HttpClient.newHttpClient();

        // create write request
        final var writeRequest = new WriteRequest();
        writeRequest.setGroupAddress(GroupAddress.of(0, 0, 22));
        writeRequest.setDataPointType(DPT2.ALARM_CONTROL);
        writeRequest.setValues("control", "false");

        // send write request
        final var httpRequest = mockPlugin.newRequestBuilder("/api/v1/write").POST(HttpRequest.BodyPublishers.ofString(ApiGsonEngine.INSTANCE.toString(writeRequest))).build();
        final var responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        assertThat(responseBody).isEqualTo("{}");
    }

    /**
     * Test /write request for group 0/0/23 with raw data. Using raw data we do not need DPT information.
     */
    @ControllerTest(WriteRequestController.class)
    @DisplayName("OK: Write Request endpoint using raw data")
    public void testWriteUsingRawData(final Controller controller) {
        final var writeRequestController = (WriteRequestController) controller;
        final var groupAddress = GroupAddress.of(0, 0, 23);

        //
        // Verification
        //

        final var request = new WriteRequest();
        request.setGroupAddress(groupAddress);
        request.setRaw(new byte[]{0x01});

        final var response = writeRequestController.writeRequest(request);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.ACCEPTED);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }

    @ControllerTest(WriteRequestController.class)
    @DisplayName("ERROR: Write Request endpoint without DPT and raw data")
    public void testWriteMissingDptAndRawData(final Controller controller) {
        final var writeRequestController = (WriteRequestController) controller;
        final var groupAddress = GroupAddress.of(4, 7, 28);

        //
        // Verification
        //

        final var request = new WriteRequest();
        request.setGroupAddress(groupAddress);

        final var response = writeRequestController.writeRequest(request);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.BAD_REQUEST);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }

    @ControllerTest(WriteRequestController.class)
    @DisplayName("ERROR: Write Request without ack body from KNX client")
    public void testWriteException(final Controller controller) {
        final var writeRequestController = (WriteRequestController) controller;
        final var groupAddress = TestHelpers.randomGroupAddress();

        //
        // Mocking
        //

        // mock no ack body was found
        when(writeRequestController.getKnxClient().writeRequest(groupAddress, new byte[]{0x01})).thenReturn(false);

        //
        // Verification
        //

        final var request = new WriteRequest();
        request.setGroupAddress(groupAddress);
        request.setDataPointType(DPT1.SWITCH);
        request.setValues("true");

        final var response = writeRequestController.writeRequest(request);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.INTERNAL_ERROR);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }

    @ControllerTest(WriteRequestController.class)
    @DisplayName("ERROR: Write Request for an unknown group address")
    public void testWriteUnknownGroupAddress(final Controller controller) {
        final var writeRequestController = (WriteRequestController) controller;

        //
        // Mocking
        //

        // mock an non-existing xml group address
        when(writeRequestController.getXmlProject().getGroupAddress(any(GroupAddress.class))).thenReturn(null);

        //
        // Verification
        //

        final var request = new WriteRequest();
        request.setGroupAddress(TestHelpers.randomGroupAddress());

        final var response = writeRequestController.writeRequest(request);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.BAD_REQUEST);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }

    @ControllerTest(WriteRequestController.class)
    @DisplayName("ERROR: Write Request without group address")
    public void testReadNoGroupAddress(final Controller controller) {
        final var writeRequestController = (WriteRequestController) controller;

        //
        // Verification
        //

        final var response = writeRequestController.writeRequest(new WriteRequest());
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.BAD_REQUEST);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }
}
