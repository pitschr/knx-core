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

import li.pitschmann.knx.daemon.v1.json.Status;
import li.pitschmann.knx.daemon.v1.json.StatusRequest;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.body.address.KnxAddress;
import li.pitschmann.knx.link.body.cemi.APCI;
import li.pitschmann.knx.link.communication.KnxStatusData;
import li.pitschmann.knx.parser.XmlGroupAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.pippo.core.HttpConstants;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link StatusController}
 */
public class StatusControllerTest extends AbstractControllerTest {

    /**
     * Tests the status endpoint for a list of status. Here we are testing the
     * case when KNX client doesn't have any status map (empty)
     */
    @Test
    @DisplayName("OK: Get list of status (empty)")
    public void testMultiStatusEmpty() {
        final var controller = newController(StatusController.class);

        //
        // Verification
        //

        final var response = controller.statusAll();
        final var responseJson = asJson(response);
        assertThat(controller.getResponse().getStatus()).isEqualTo(207); // http code 207 = Multi Status
        assertThat(responseJson).isEqualTo("[]");
    }

    /**
     * Tests the status endpoint for a list of status. Here we are testing the
     * case when KNX client has some entries in the status map
     */
    @Test
    @DisplayName("OK: Get list of status (non-empty)")
    public void testMultiStatus() {
        final var controller = newController(StatusController.class);

        //
        // Mocking
        //

        // mock all expand parameters
        doReturn(true).when(controller).containsExpand(anyString());

        // mock status map with some entries
        // - three with known group addresses in the XML project file
        // - one with unknown group address in the XML project file
        final var statusMap = new LinkedHashMap<KnxAddress, KnxStatusData>();

        final var sourceGroupAddress = IndividualAddress.of(15, 15, 255);

        final var knxAddress0 = GroupAddress.of(0, 1, 2);
        final var knxStatusData0 = new KnxStatusData(sourceGroupAddress, APCI.GROUP_VALUE_READ, new byte[]{0x01});
        statusMap.put(knxAddress0, knxStatusData0);

        final var knxAddress1 = GroupAddress.of(1, 2, 3);
        final var knxStatusData1 = new KnxStatusData(sourceGroupAddress, APCI.GROUP_VALUE_WRITE, new byte[]{0x23});
        statusMap.put(knxAddress1, knxStatusData1);

        final var knxAddress2 = GroupAddress.of(2, 3, 4);
        final var knxStatusData2 = new KnxStatusData(sourceGroupAddress, APCI.GROUP_VALUE_RESPONSE, new byte[]{0x56, 0x78});
        statusMap.put(knxAddress2, knxStatusData2);

        final var knxAddress3 = GroupAddress.of(3, 4, 5);
        final var knxStatusData3 = new KnxStatusData(sourceGroupAddress, APCI.GROUP_VALUE_READ, new byte[]{0x69, 0x0A, 0x4E});
        statusMap.put(knxAddress3, knxStatusData3);

        final var xmlGroupAddress0 = new XmlGroupAddress();
        xmlGroupAddress0.setDatapointType("1.001"); // DPT1.Switch
        xmlGroupAddress0.setName("DPT1.Switch Name");
        xmlGroupAddress0.setDescription("DPT1.Switch Description");
        when(controller.getXmlProject().getGroupAddress(knxAddress0)).thenReturn(xmlGroupAddress0);

        final var xmlGroupAddress1 = new XmlGroupAddress();
        xmlGroupAddress1.setDatapointType("5.010"); // DPT5.1-Octet Unsigned
        xmlGroupAddress1.setName("DPT5.1-Octet Unsigned Name");
        xmlGroupAddress1.setDescription("DPT5.1-Octet Unsigned Description");
        when(controller.getXmlProject().getGroupAddress(knxAddress1)).thenReturn(xmlGroupAddress1);

        final var xmlGroupAddress2 = new XmlGroupAddress();
        xmlGroupAddress2.setDatapointType("7.001"); // DPT7.2-Octet Unsigned
        xmlGroupAddress2.setName("DPT7.2-Octet Unsigned Name");
        xmlGroupAddress2.setDescription("DPT7.2-Octet Unsigned Description");
        when(controller.getXmlProject().getGroupAddress(knxAddress2)).thenReturn(xmlGroupAddress2);

        // simulate an unknown group address in XML project
        when(controller.getXmlProject().getGroupAddress(knxAddress3)).thenReturn(null);


        when(controller.getKnxClient().getStatusPool().copyStatusMap()).thenReturn(statusMap);

        //
        // Verification
        //

        final var response = controller.statusAll();
        assertThat(controller.getResponse().getStatus()).isEqualTo(207); // http code 207 = Multi Status
        assertThat(response).hasSize(4);
        assertThat(response.get(0).getSourceAddress()).isEqualTo(sourceGroupAddress);
        assertThat(response.get(1).getApci()).isEqualTo(APCI.GROUP_VALUE_WRITE);
        assertThat(response.get(2).getDescription()).isEqualTo("DPT7.2-Octet Unsigned Description");
        assertThat(response.get(3).getStatus()).isEqualTo(Status.ERROR);

        // now verify with only one expand parameter
        doReturn(false).when(controller).containsExpand(anyString());
        doReturn(true).when(controller).containsExpand("name");

        assertThat(asJson(controller.statusAll())).isEqualTo(
                // @formatter:off
                "[" +
                    "{\"name\":\"DPT1.Switch Name\"}," +
                    "{\"name\":\"DPT5.1-Octet Unsigned Name\"}," +
                    "{\"name\":\"DPT7.2-Octet Unsigned Name\"}," +
                    "{}" +
                "]"
                // @formatter:on
        );

    }


    /**
     * Tests the status endpoint for a known group address
     */
    @Test
    @DisplayName("OK: Status Request for a known group address")
    public void testSingleStatus() {
        final var controller = newController(StatusController.class);
        final var groupAddress = randomGroupAddress();

        //
        // Mocking
        //

        // mock an existing KNX status data in status pool
        final var knxStatusData = mock(KnxStatusData.class);
        when(controller.getKnxClient().getStatusPool().getStatusFor(any(KnxAddress.class))).thenReturn(knxStatusData);

        //
        // Verification
        //

        final var request = new StatusRequest();
        request.setGroupAddress(groupAddress);

        final var response = controller.statusRequest(request);
        final var responseJson = asJson(response);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(responseJson).isEqualTo("{}");
    }

    /**
     * Tests the status endpoint for an unknown group address
     */
    @Test
    @DisplayName("Error: Status Request an unknown group address")
    public void testWriteUnknownGroupAddress() {
        final var controller = newController(StatusController.class);

        //
        // Mocking
        //

        // mock the expand 'status' parameter
        doReturn(true).when(controller).containsExpand("status");

        // mock an non-existing xml group address
        when(controller.getXmlProject().getGroupAddress(any(GroupAddress.class))).thenReturn(null);

        //
        // Verification
        //

        final var request = new StatusRequest();
        request.setGroupAddress(randomGroupAddress());

        final var response = controller.statusRequest(request);
        final var responseJson = asJson(response);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.NOT_FOUND);
        assertThat(responseJson).isEqualTo("{\"status\":\"ERROR\"}");
    }
}
