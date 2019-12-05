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
import li.pitschmann.knx.core.body.address.IndividualAddress;
import li.pitschmann.knx.core.body.address.KnxAddress;
import li.pitschmann.knx.core.body.cemi.APCI;
import li.pitschmann.knx.core.communication.KnxStatusData;
import li.pitschmann.knx.core.knxproj.XmlGroupAddress;
import li.pitschmann.knx.core.plugin.api.v1.json.Status;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import ro.pippo.controller.Controller;
import ro.pippo.core.HttpConstants;

import java.time.Instant;
import java.util.LinkedHashMap;

import static li.pitschmann.knx.core.plugin.api.TestUtils.asJson;
import static li.pitschmann.knx.core.plugin.api.TestUtils.readJsonFile;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link StatusController}
 */
public class StatusControllerTest {

    /**
     * Tests the status endpoint for a list of status. Here we are testing the
     * case when KNX client doesn't have any status map (empty)
     */
    @ControllerTest(StatusController.class)
    @DisplayName("OK: Get list of status (empty)")
    public void testMultiStatusEmpty(final Controller controller) {
        final var statusController = (StatusController) controller;

        //
        // Verification
        //

        final var response = statusController.statusAll();
        assertThat(controller.getResponse().getStatus()).isEqualTo(207); // http code 207 = Multi Status

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("[]");
    }

    /**
     * Tests the status endpoint for a list of status. Here we are testing the
     * case when KNX client has some entries in the status map
     */
    @ControllerTest(StatusController.class)
    @DisplayName("OK: Get list of status (non-empty)")
    public void testMultiStatus(final Controller controller) {
        final var statusController = (StatusController) controller;

        //
        // Mocking
        //

        // mock status map with some entries
        // - three with known group addresses in the XML project file
        // - one with unknown group address in the XML project file
        final var statusMap = new LinkedHashMap<KnxAddress, KnxStatusData>();

        final var sourceGroupAddress = IndividualAddress.of(15, 15, 255);

        final var knxAddress0 = GroupAddress.of(0, 1, 2);
        final var knxStatusData0 = mock(KnxStatusData.class);
        when(knxStatusData0.getTimestamp()).thenReturn(Instant.ofEpochMilli(123456));
        when(knxStatusData0.getSourceAddress()).thenReturn(sourceGroupAddress);
        when(knxStatusData0.getApci()).thenReturn(APCI.GROUP_VALUE_READ);
        when(knxStatusData0.getApciData()).thenReturn(new byte[]{0x01});
        statusMap.put(knxAddress0, knxStatusData0);

        final var knxAddress1 = GroupAddress.of(1, 2, 3);
        final var knxStatusData1 = mock(KnxStatusData.class);
        when(knxStatusData1.getTimestamp()).thenReturn(Instant.ofEpochMilli(234567));
        when(knxStatusData1.getSourceAddress()).thenReturn(sourceGroupAddress);
        when(knxStatusData1.getApci()).thenReturn(APCI.GROUP_VALUE_WRITE);
        when(knxStatusData1.getApciData()).thenReturn(new byte[]{0x23});
        statusMap.put(knxAddress1, knxStatusData1);

        final var knxAddress2 = GroupAddress.of(2, 3, 4);
        final var knxStatusData2 = mock(KnxStatusData.class);
        when(knxStatusData2.getTimestamp()).thenReturn(Instant.ofEpochMilli(345678));
        when(knxStatusData2.getSourceAddress()).thenReturn(sourceGroupAddress);
        when(knxStatusData2.getApci()).thenReturn(APCI.GROUP_VALUE_RESPONSE);
        when(knxStatusData2.getApciData()).thenReturn(new byte[]{0x56, 0x7E});
        statusMap.put(knxAddress2, knxStatusData2);

        final var knxAddress3 = GroupAddress.of(3, 4, 5);
        final var knxStatusData3 = mock(KnxStatusData.class);
        when(knxStatusData3.getTimestamp()).thenReturn(Instant.ofEpochMilli(456789));
        when(knxStatusData3.getSourceAddress()).thenReturn(sourceGroupAddress);
        when(knxStatusData3.getApci()).thenReturn(APCI.GROUP_VALUE_READ);
        when(knxStatusData3.getApciData()).thenReturn(new byte[]{0x69, 0x0A, 0x4E});
        statusMap.put(knxAddress3, knxStatusData3);
        when(statusController.getKnxClient().getStatusPool().copyStatusMap()).thenReturn(statusMap);

        final var xmlGroupAddress0 = new XmlGroupAddress();
        xmlGroupAddress0.setDataPointType("1.001");
        xmlGroupAddress0.setName("DPT1.Switch Name");
        xmlGroupAddress0.setDescription("DPT1.Switch Description");
        when(statusController.getXmlProject().getGroupAddress(knxAddress0)).thenReturn(xmlGroupAddress0);

        final var xmlGroupAddress1 = new XmlGroupAddress();
        xmlGroupAddress1.setDataPointType("5.010");
        xmlGroupAddress1.setName("DPT5.1-Octet Unsigned Name");
        xmlGroupAddress1.setDescription("DPT5.1-Octet Unsigned Description");
        when(statusController.getXmlProject().getGroupAddress(knxAddress1)).thenReturn(xmlGroupAddress1);

        final var xmlGroupAddress2 = new XmlGroupAddress();
        xmlGroupAddress2.setDataPointType("7.001");
        xmlGroupAddress2.setName("DPT7.2-Octet Unsigned Name");
        xmlGroupAddress2.setDescription("DPT7.2-Octet Unsigned Description");
        when(statusController.getXmlProject().getGroupAddress(knxAddress2)).thenReturn(xmlGroupAddress2);

        // simulate an unknown group address in XML project
        when(statusController.getXmlProject().getGroupAddress(knxAddress3)).thenReturn(null);

        //
        // Verification
        //

        final var response = statusController.statusAll();
        assertThat(controller.getResponse().getStatus()).isEqualTo(207); // http code 207 = Multi Status
        assertThat(response).hasSize(4);
        assertThat(response.get(0).getSourceAddress()).isEqualTo(sourceGroupAddress);
        assertThat(response.get(1).getApci()).isEqualTo(APCI.GROUP_VALUE_WRITE);
        assertThat(response.get(2).getDescription()).isEqualTo("DPT7.2-Octet Unsigned Description");
        assertThat(response.get(3).getStatus()).isEqualTo(Status.ERROR);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/StatusControllerTest-testMultiStatus.json"));
    }

    @ControllerTest(StatusController.class)
    @DisplayName("OK: Status Request for a known group address and is registered in KNX Project File")
    public void testFullSingleStatus(final Controller controller) {
        final var statusController = (StatusController) controller;
        final var groupAddress = GroupAddress.of(7, 7, 78);
        final var sourceAddress = IndividualAddress.of(15, 14, 13);

        //
        // Mocking
        //

        final var xmlGroupAddress = new XmlGroupAddress();
        xmlGroupAddress.setDataPointType("1.001");
        xmlGroupAddress.setName("DPT1.Switch Name");
        xmlGroupAddress.setDescription("DPT1.Switch Description");
        when(statusController.getXmlProject().getGroupAddress(groupAddress)).thenReturn(xmlGroupAddress);

        // mock an existing KNX status data in status pool
        final var knxStatusData = mock(KnxStatusData.class);
        when(knxStatusData.getTimestamp()).thenReturn(Instant.ofEpochMilli(9876543));
        when(knxStatusData.getSourceAddress()).thenReturn(sourceAddress);
        when(knxStatusData.getApci()).thenReturn(APCI.GROUP_VALUE_READ);
        when(knxStatusData.getApciData()).thenReturn(new byte[]{0x77, 0x43, 0x21});
        when(statusController.getKnxClient().getStatusPool().getStatusFor(any(KnxAddress.class))).thenReturn(knxStatusData);

        //
        // Verification
        //

        final var response = statusController.statusOne(groupAddress.getAddressLevel3());
        assertThat(statusController.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getTimestamp()).isNotNull();
        assertThat(response.getSourceAddress()).isEqualTo(sourceAddress);
        assertThat(response.getApci()).isEqualTo(APCI.GROUP_VALUE_READ);
        assertThat(response.isDirty()).isFalse();

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/StatusControllerTest-testFullSingleStatus.json"));
    }

    @ControllerTest(StatusController.class)
    @DisplayName("OK: Status Request for a known group address but not registered in XML Project File")
    public void testPartialSingleStatus(final Controller controller) {
        final var statusController = (StatusController) controller;
        final var groupAddress = GroupAddress.of(8, 7, 79);
        final var sourceAddress = IndividualAddress.of(15, 14, 12);

        //
        // Mocking
        //

        when(statusController.getXmlProject().getGroupAddress(groupAddress)).thenReturn(null);

        // mock an existing KNX status data in status pool
        final var knxStatusData = mock(KnxStatusData.class);
        when(knxStatusData.getTimestamp()).thenReturn(Instant.ofEpochMilli(19876543));
        when(knxStatusData.getSourceAddress()).thenReturn(sourceAddress);
        when(knxStatusData.getApci()).thenReturn(APCI.GROUP_VALUE_READ);
        when(knxStatusData.getApciData()).thenReturn(new byte[]{0x38, 0x55});
        when(statusController.getKnxClient().getStatusPool().getStatusFor(any(KnxAddress.class))).thenReturn(knxStatusData);

        //
        // Verification
        //

        final var response = statusController.statusOne(groupAddress.getAddressLevel3());
        assertThat(statusController.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getTimestamp()).isNotNull();
        assertThat(response.getSourceAddress()).isEqualTo(sourceAddress);
        assertThat(response.getApci()).isEqualTo(APCI.GROUP_VALUE_READ);
        assertThat(response.isDirty()).isFalse();

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/StatusControllerTest-testPartialSingleStatus.json"));
    }

    @ControllerTest(StatusController.class)
    @DisplayName("ERROR: Status Request for a known group address but unknown to status pool yet")
    public void testSingleStatusNoStatus(final Controller controller) {
        final var statusController = (StatusController) controller;
        final var groupAddress = GroupAddress.of(7, 7, 88);

        //
        // Mocking
        //

        // mock a non-existing KNX status data in status pool
        when(statusController.getKnxClient().getStatusPool().getStatusFor(any(KnxAddress.class))).thenReturn(null);

        //
        // Verification
        //

        final var response = statusController.statusOne(groupAddress.getAddressLevel3());
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.NOT_FOUND);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }

    @ControllerTest(StatusController.class)
    @DisplayName("ERROR: Status Request an unknown group address")
    public void testStatusUnknownGroupAddress(final Controller controller) {
        final var statusController = (StatusController) controller;
        final var groupAddress = TestHelpers.randomGroupAddress();

        //
        // Mocking
        //

        // mock an non-existing xml group address
        when(statusController.getXmlProject().getGroupAddress(any(GroupAddress.class))).thenReturn(null);

        //
        // Verification
        //

        final var response = statusController.statusOne(groupAddress.getAddressLevel3());
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.NOT_FOUND);
        assertThat(response.getStatus()).isNull();
        assertThat(response.getTimestamp()).isNull();
        assertThat(response.getSourceAddress()).isNull();
        assertThat(response.getApci()).isNull();
        assertThat(response.isDirty()).isNull();

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo("{}");
    }
//
//    @ControllerTest(StatusController.class)
//    @DisplayName("ERROR: Status Request with no group address")
//    public void testStatusNoGroupAddress(final Controller controller) {
//        final var statusController = (StatusController) controller;
//
//        //
//        // Verification
//        //
//
//        final var response = statusController.statusOne("");
//        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.BAD_REQUEST);
//        assertThat(response.getStatus()).isNull();
//        assertThat(response.getTimestamp()).isNull();
//        assertThat(response.getSourceAddress()).isNull();
//        assertThat(response.getApci()).isNull();
//        assertThat(response.isDirty()).isNull();
//
//        final var responseJson = asJson(response);
//        assertThatJson(responseJson).isEqualTo("{}");
//    }
}
