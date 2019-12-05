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
import li.pitschmann.knx.core.datapoint.DPT12;
import li.pitschmann.knx.core.plugin.api.ControllerTest;
import li.pitschmann.knx.core.plugin.api.TestUtils;
import li.pitschmann.knx.core.plugin.api.v1.json.ReadRequest;
import org.junit.jupiter.api.DisplayName;
import ro.pippo.controller.Controller;
import ro.pippo.core.HttpConstants;

import static li.pitschmann.knx.core.plugin.api.TestUtils.asJson;
import static li.pitschmann.knx.core.plugin.api.TestUtils.readJsonFile;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ReadRequestController}
 */
public class ReadRequestControllerTest {
    private static final String FILE_KNXPROJ_THREE_LEVEL = "src/test/resources/Project (3-Level, v20).knxproj";

    /**
     * Tests the /read endpoint for group addresses using KNX mock server
     */
    @ControllerTest(value = ReadRequestController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK: Read Request for group address")
    public void testRead(final Controller controller) {
        final var readRequestController = (ReadRequestController) controller;
        final var groupAddress = GroupAddress.of(0, 3, 18);

        //
        // Verification
        //

        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        final var response = readRequestController.readRequest(request);
        assertThat(response.getGroupAddress()).isEqualTo(groupAddress);
        assertThat(response.getName()).isEqualTo("Sub Group - DPT 12 (0x80 02 70 FF)");
        assertThat(response.getDescription()).isEqualTo("4-bytes, unsigned (2147643647)");
        assertThat(response.getDataPointType()).isEqualTo(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT);
        assertThat(response.getValue()).isEqualTo("2147643647");
        assertThat(response.getUnit()).isEqualTo("pulses");
        assertThat(response.getRaw()).containsExactly(0x80, 0x02, 0x70, 0xFF);
        assertThat(response).hasToString(
                String.format("ReadResponse{name=%s, description=%s, dataPointType=%s, raw=0x80 02 70 FF}",
                        response.getName(), //
                        response.getDescription(), //
                        response.getDataPointType() //
                ));

        // verify json
        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ReadRequestControllerTest-testRead.json"));
    }

    @ControllerTest(value = ReadRequestController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK: Read Request for an unknown group address in XML project")
    public void testReadUnknownXmlGroupAddress(final Controller controller) {
        final var readRequestController = (ReadRequestController) controller;
        final var groupAddress = GroupAddress.of(0, 3, 18);

        //
        // Mocking
        //

        // mock an non-existing xml group address, but status available in status pool
        final var xmlProject = readRequestController.getXmlProject();
        doReturn(null).when(xmlProject).getGroupAddress(eq(groupAddress));

        //
        // Verification
        //

        // create read request
        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        final var response = readRequestController.readRequest(request);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response.getGroupAddress()).isEqualTo(groupAddress);
        assertThat(response.getName()).isNull();
        assertThat(response.getDescription()).isNull();
        assertThat(response.getDataPointType()).isNull();
        assertThat(response.getValue()).isNull();
        assertThat(response.getRaw()).containsExactly(0x80, 0x02, 0x70, 0xFF);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ReadRequestControllerTest-testReadUnknownXmlGroupAddress.json"));
    }

    /**
     * Read Request for an existing group address in XML project file,
     * but no ack body could be found (or retrieved yet) due a thrown
     * exception.
     */
    @ControllerTest(ReadRequestController.class)
    @DisplayName("ERROR: Read Request without found ack body due a thrown exception from KNX client")
    public void testReadException(final Controller controller) {
        final var readRequestController = (ReadRequestController) controller;
        final var groupAddress = TestUtils.randomGroupAddress();

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
        assertThat(response.getGroupAddress()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getDescription()).isNull();
        assertThat(response.getDataPointType()).isNull();
        assertThat(response.getValue()).isNull();
        assertThat(response.getRaw()).isNull();

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }

    /**
     * Read Request causing an internal timeout because KNX client didn't receive a response
     * from KNX Net/IP device yet (and therefore no status data available in the status pool).
     */
    @ControllerTest(ReadRequestController.class)
    @DisplayName("ERROR: Read Request without available KNX status data from KNX Client (internal timeout)")
    public void testReadInternalTimeout(final Controller controller) {
        final var readRequestController = (ReadRequestController) controller;
        final var groupAddress = TestUtils.randomGroupAddress();

        //
        // Verification
        //

        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        final var response = readRequestController.readRequest(request);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.NOT_FOUND);
        assertThat(response.getGroupAddress()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getDescription()).isNull();
        assertThat(response.getDataPointType()).isNull();
        assertThat(response.getValue()).isNull();
        assertThat(response.getRaw()).isNull();

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }

    @ControllerTest(ReadRequestController.class)
    @DisplayName("ERROR: Read Request for an unknown group address")
    public void testReadUnknownGroupAddress(final Controller controller) {
        final var readRequestController = (ReadRequestController) controller;
        final var groupAddress = TestUtils.randomGroupAddress();

        //
        // Mocking
        //


        // mock an non-existing xml group address, but status available in status pool
        when(readRequestController.getXmlProject().getGroupAddress(any(GroupAddress.class))).thenReturn(null);

        //
        // Verification
        //

        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        final var response = readRequestController.readRequest(request);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.NOT_FOUND);
        assertThat(response.getGroupAddress()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getDescription()).isNull();
        assertThat(response.getDataPointType()).isNull();
        assertThat(response.getValue()).isNull();
        assertThat(response.getRaw()).isNull();

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }

    @ControllerTest(ReadRequestController.class)
    @DisplayName("ERROR: Read Request without group address")
    public void testReadNoGroupAddress(final Controller controller) {
        final var readRequestController = (ReadRequestController) controller;

        //
        // Verification
        //

        final var response = readRequestController.readRequest(new ReadRequest());
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.BAD_REQUEST);
        assertThat(response.getGroupAddress()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getDescription()).isNull();
        assertThat(response.getDataPointType()).isNull();
        assertThat(response.getValue()).isNull();
        assertThat(response.getRaw()).isNull();

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }


}
