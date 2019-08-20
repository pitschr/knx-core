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
import li.pitschmann.knx.daemon.v1.json.ReadResponse;
import li.pitschmann.knx.link.body.Status;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.datapoint.DPT12;
import li.pitschmann.knx.test.MockDaemonTest;
import li.pitschmann.knx.test.MockHttpDaemon;
import li.pitschmann.knx.test.MockServerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.pippo.core.HttpConstants;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ReadRequestController}
 */
public class ReadRequestControllerTest extends AbstractControllerTest {

    /**
     * Tests the /read endpoint for group addresses using KNX mock server
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v14).knxproj"))
    @DisplayName("OK: Read Request for group addresses using KNX mock server")
    public void testRead(final MockHttpDaemon daemon) throws Exception {
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
        assertThat(readResponse.getName()).isEqualTo("Sub Group - DPT 12 (0x80 02 70 FF)");
        assertThat(readResponse.getDescription()).isEqualTo("4-bytes, unsigned (2147643647)");
        assertThat(readResponse.getDataPointType()).isEqualTo(DPT12.VALUE_4_OCTET_UNSIGNED_COUNT);
        assertThat(readResponse.getRaw()).containsExactly(0x80, 0x02, 0x70, 0xFF);
        assertThat(readResponse).hasToString(
                String.format("ReadResponse{name=%s, description=%s, dataPointType=%s, raw=0x80 02 70 FF}",
                        readResponse.getName(), //
                        readResponse.getDescription(), //
                        readResponse.getDataPointType() //
                ));

        // verify json
        assertThat(asJson(readResponse)).isEqualTo(readJsonFile("/json/ReadRequestControllerTest-testRead.json"));
    }

    /**
     * Read Request for an existing group address in XML project file,
     * but no ack body could be found (or retrieved yet) due a thrown
     * exception.
     */
    @Test
    @DisplayName("Error: Read Request without found ack body due a thrown exception from KNX client")
    public void testReadException() {
        final var controller = newController(ReadRequestController.class);
        final var groupAddress = randomGroupAddress();

        //
        // Mocking
        //

        // mock no ack body was found - an execution exception is thrown instead
        try {
            when(controller.getKnxClient().readRequest(groupAddress).get()).thenThrow(new ExecutionException(null));
        } catch (final Throwable t) {
            fail(t);
        }

        //
        // Verification
        //

        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        final var response = controller.readRequest(request);
        final var responseJson = asJson(response);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.INTERNAL_ERROR);
        assertThat(responseJson).isEqualTo("{}");
    }

    /**
     * Read Request causing an internal timeout because KNX client didn't receive a response
     * from KNX Net/IP device yet (and therefore no status data available in the status pool).
     */
    @Test
    @DisplayName("Error: Read Request without available KNX status data from KNX Client (internal timeout)")
    public void testReadInternalTimeout() {
        final var controller = newController(ReadRequestController.class);
        final var groupAddress = randomGroupAddress();

        //
        // Mocking
        //

        // mock retrieve tunneling ack status with no error
        try {
            when(controller.getKnxClient().readRequest(groupAddress).get().getStatus()).thenReturn(Status.E_NO_ERROR);
        } catch (final Throwable t) {
            fail(t);
        }

        //
        // Verification
        //

        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        final var response = controller.readRequest(request);
        final var responseJson = asJson(response);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.NOT_FOUND);
        assertThat(responseJson).isEqualTo("{}");
    }

    /**
     * Tests the read endpoint for an unknown group address
     */
    @Test
    @DisplayName("Error: Read Request for an unknown group address")
    public void testReadUnknownGroupAddress() {
        final var controller = newController(ReadRequestController.class);
        final var groupAddress = randomGroupAddress();

        //
        // Mocking
        //

        // mock an non-existing xml group address
        when(controller.getXmlProject().getGroupAddress(any(GroupAddress.class))).thenReturn(null);

        //
        // Verification
        //

        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        final var response = controller.readRequest(request);
        final var responseJson = asJson(response);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.BAD_REQUEST);
        assertThat(responseJson).isEqualTo("{}");
    }
}
