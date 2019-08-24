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
import li.pitschmann.knx.link.datapoint.DPT1;
import li.pitschmann.knx.link.datapoint.DPT2;
import li.pitschmann.knx.test.MockDaemonTest;
import li.pitschmann.knx.test.MockHttpDaemon;
import li.pitschmann.knx.test.MockServerTest;
import org.junit.jupiter.api.DisplayName;
import ro.pippo.controller.Controller;
import ro.pippo.core.HttpConstants;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

import static li.pitschmann.knx.test.TestUtils.asJson;
import static li.pitschmann.knx.test.TestUtils.randomGroupAddress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v14).knxproj"))
    @DisplayName("OK: Write Request for group address 0/0/22")
    public void testWrite(final MockHttpDaemon daemon) throws Exception {
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
        assertThat(responseBody).isEqualTo("{}");
    }

    /**
     * An erroneous Write Request without DPT and raw data.
     */
    @ControllerTest(WriteRequestController.class)
    @DisplayName("Error: Write Request endpoint without DPT and raw data")
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
        assertThat(responseJson).isEqualTo("{}");
    }

    /**
     * Write Request for an existing group address in XML project file,
     * but no ack body could be found (or retrieved yet) due a thrown
     * exception.
     */
    @ControllerTest(WriteRequestController.class)
    @DisplayName("Error: Write Request without found ack body due a thrown exception from KNX client")
    public void testWriteException(final Controller controller) {
        final var writeRequestController = (WriteRequestController) controller;
        final var groupAddress = randomGroupAddress();

        //
        // Mocking
        //

        // mock no ack body was found - an execution exception is thrown instead
        try {
            when(writeRequestController.getKnxClient().writeRequest(groupAddress, new byte[0]).get()).thenThrow(new ExecutionException(null));
        } catch (final Throwable t) {
            fail(t);
        }

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
        assertThat(responseJson).isEqualTo("{}");
    }

    /**
     * Tests the write endpoint for an unknown group address
     */
    @ControllerTest(WriteRequestController.class)
    @DisplayName("Error: Write Request for an unknown group address")
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
        request.setGroupAddress(randomGroupAddress());

        final var response = writeRequestController.writeRequest(request);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.BAD_REQUEST);

        final var responseJson = asJson(response);
        assertThat(responseJson).isEqualTo("{}");
    }
}
