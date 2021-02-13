/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2021 Pitschmann Christoph
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

import io.javalin.plugin.json.JavalinJson;
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.plugin.api.ControllerTest;
import li.pitschmann.knx.core.plugin.api.TestUtils;
import li.pitschmann.knx.core.plugin.api.v1.gson.ApiGsonEngine;
import li.pitschmann.knx.core.plugin.api.v1.json.ReadRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import javax.servlet.http.HttpServletResponse;

import static li.pitschmann.knx.core.plugin.api.TestUtils.readJsonFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ReadRequestController}
 */
public class ReadRequestControllerTest {
    private static final String FILE_KNXPROJ_THREE_LEVEL = "src/test/resources/Project (3-Level, v20).knxproj";

    @BeforeAll
    static void setUp() {
        final var gson = ApiGsonEngine.INSTANCE.getGson();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
    }

    /**
     * Tests the /read endpoint for group addresses using KNX mock server
     */
    @ControllerTest(value = ReadRequestController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK: Read Request for group address")
    public void testRead(final ReadRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();

        final var request = new ReadRequest();
        request.setGroupAddress(GroupAddress.of(0, 3, 18));

        // Execution
        controller.readRequest(contextSpy, request);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_OK);
        verify(contextSpy).result(readJsonFile("/json/ReadRequestControllerTest-testRead.json"));
    }

    @ControllerTest(value = ReadRequestController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK: Read Request for an unknown group address in XML project")
    public void testReadUnknownXmlGroupAddress(final ReadRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();
        final var groupAddress = GroupAddress.of(0, 3, 18);

        final var request = new ReadRequest();
        request.setGroupAddress(groupAddress);

        // mock an non-existing xml group address, but status available in status pool
        final var xmlProject = controller.getKnxClient().getConfig().getProject();
        doReturn(null).when(xmlProject).getGroupAddress(eq(groupAddress));

        // Execution
        controller.readRequest(contextSpy, request);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_OK);
        verify(contextSpy).result(readJsonFile("/json/ReadRequestControllerTest-testReadUnknownXmlGroupAddress.json"));
    }

    /**
     * Read Request for an existing group address in XML project file,
     * but no ack body could be found (or retrieved yet) due a thrown
     * exception.
     */
    @ControllerTest(ReadRequestController.class)
    @DisplayName("ERROR: Read Request without found ack body due a thrown exception from KNX client")
    public void testReadException(final ReadRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();

        final var request = new ReadRequest();
        request.setGroupAddress(TestUtils.randomGroupAddress());

        // mock no ack body was found
        when(controller.getKnxClient().readRequest(any(GroupAddress.class))).thenReturn(false);

        // Execution
        controller.readRequest(contextSpy, request);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(contextSpy).result("{}");
    }

    /**
     * Read Request causing an internal timeout because KNX client didn't receive a response
     * from KNX Net/IP device yet (and therefore no status data available in the status pool).
     */
    @ControllerTest(ReadRequestController.class)
    @DisplayName("ERROR: Read Request without available KNX status data from KNX Client (internal timeout)")
    public void testReadInternalTimeout(final ReadRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();

        final var request = new ReadRequest();
        request.setGroupAddress(TestUtils.randomGroupAddress());

        // Execution
        controller.readRequest(contextSpy, request);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_NOT_FOUND);
        verify(contextSpy).result("{}");
    }

    @ControllerTest(ReadRequestController.class)
    @DisplayName("ERROR: Read Request for an unknown group address")
    public void testReadUnknownGroupAddress(final ReadRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();

        final var request = new ReadRequest();
        request.setGroupAddress(TestUtils.randomGroupAddress());

        // mock an non-existing xml group address, but status available in status pool
        final var xmlProject = controller.getKnxClient().getConfig().getProject();
        when(xmlProject.getGroupAddress(any(GroupAddress.class))).thenReturn(null);

        // Execution
        controller.readRequest(contextSpy, request);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_NOT_FOUND);
        verify(contextSpy).result("{}");
    }

    @ControllerTest(ReadRequestController.class)
    @DisplayName("ERROR: Read Request without group address")
    public void testReadNoGroupAddress(final ReadRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Execution
        controller.readRequest(contextSpy, new ReadRequest());

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_BAD_REQUEST);
        verify(contextSpy).result("{}");
    }

}
