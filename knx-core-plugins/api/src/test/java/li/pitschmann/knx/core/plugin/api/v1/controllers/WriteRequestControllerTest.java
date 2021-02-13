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
import li.pitschmann.knx.core.datapoint.DPT1;
import li.pitschmann.knx.core.datapoint.value.DataPointValue;
import li.pitschmann.knx.core.plugin.api.ControllerTest;
import li.pitschmann.knx.core.plugin.api.TestUtils;
import li.pitschmann.knx.core.plugin.api.v1.gson.ApiGsonEngine;
import li.pitschmann.knx.core.plugin.api.v1.json.WriteRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link WriteRequestController}
 */
public class WriteRequestControllerTest {

    @BeforeAll
    static void setUp() {
        final var gson = ApiGsonEngine.INSTANCE.getGson();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
    }

    @ControllerTest(WriteRequestController.class)
    @DisplayName("OK: Write Request endpoint using raw data")
    public void testWriteUsingRawData(final WriteRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();

        final var request = new WriteRequest();
        request.setGroupAddress(GroupAddress.of(0, 0, 23));
        request.setDataPointType(DPT1.SWITCH);
        request.setRaw(new byte[]{0x01});

        // Execution
        controller.writeRequest(contextSpy, request);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_ACCEPTED);
        verify(contextSpy).result("{}");
    }

    @ControllerTest(WriteRequestController.class)
    @DisplayName("OK: Write Request endpoint using DPT information")
    public void testWriteUsingDpt(final WriteRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();

        final var request = new WriteRequest();
        request.setGroupAddress(GroupAddress.of(0, 0, 23));
        request.setDataPointType(DPT1.SWITCH);
        request.setValues("on");

        // Execution
        controller.writeRequest(contextSpy, request);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_ACCEPTED);
        verify(contextSpy).result("{}");
    }

    @ControllerTest(WriteRequestController.class)
    @DisplayName("ERROR: Write Request endpoint without DPT and raw data")
    public void testWriteMissingDptAndRawData(final WriteRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();

        final var request = new WriteRequest();
        request.setGroupAddress(GroupAddress.of(4, 7, 28));

        // Execution
        controller.writeRequest(contextSpy, request);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_BAD_REQUEST);
        verify(contextSpy).result("{}");
    }

    @ControllerTest(WriteRequestController.class)
    @DisplayName("ERROR: Write Request without ack body from KNX client")
    public void testWriteException(final WriteRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();
        final var groupAddress = TestUtils.randomGroupAddress();

        // mock no ack body was found
        when(controller.getKnxClient().writeRequest(eq(groupAddress), any(DataPointValue.class))).thenReturn(false);

        final var request = new WriteRequest();
        request.setGroupAddress(groupAddress);
        request.setDataPointType(DPT1.SWITCH);
        request.setValues("true");

        // Execution
        controller.writeRequest(contextSpy, request);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(contextSpy).result("{}");
    }

    @ControllerTest(WriteRequestController.class)
    @DisplayName("ERROR: Write Request for an unknown group address")
    public void testWriteUnknownGroupAddress(final WriteRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // mock an non-existing xml group address
        final var xmlProject = controller.getKnxClient().getConfig().getProject();
        when(xmlProject.getGroupAddress(any(GroupAddress.class))).thenReturn(null);

        final var request = new WriteRequest();
        request.setGroupAddress(TestUtils.randomGroupAddress());

        // Execution
        controller.writeRequest(contextSpy, request);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_BAD_REQUEST);
        verify(contextSpy).result("{}");
    }

    @ControllerTest(WriteRequestController.class)
    @DisplayName("ERROR: Write Request without group address")
    public void testWriteNoGroupAddress(final WriteRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Execution
        controller.writeRequest(contextSpy, new WriteRequest());

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_BAD_REQUEST);
        verify(contextSpy).result("{}");
    }

    @ControllerTest(WriteRequestController.class)
    @DisplayName("ERROR: Write Request without DPT id and with values")
    void testWriteNoDPT(final WriteRequestController controller) {
        final var contextSpy = TestUtils.contextSpy();

        final var request = new WriteRequest();
        request.setDataPointType(DPT1.SWITCH);
        request.setGroupAddress(TestUtils.randomGroupAddress());

        // Execution
        controller.writeRequest(contextSpy, request);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_BAD_REQUEST);
        verify(contextSpy).result("{}");
    }
}
