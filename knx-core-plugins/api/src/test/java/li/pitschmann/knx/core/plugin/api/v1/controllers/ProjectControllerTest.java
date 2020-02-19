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

import li.pitschmann.knx.core.knxproj.XmlGroupAddressStyle;
import li.pitschmann.knx.core.knxproj.XmlGroupRange;
import li.pitschmann.knx.core.plugin.api.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import ro.pippo.controller.Controller;
import ro.pippo.core.HttpConstants;
import ro.pippo.core.ParameterValue;

import static li.pitschmann.knx.core.plugin.api.TestUtils.asJson;
import static li.pitschmann.knx.core.plugin.api.TestUtils.readJsonFile;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ProjectController}
 */
public class ProjectControllerTest {
    private static final String FILE_KNXPROJ_THREE_LEVEL = "src/test/resources/Project (3-Level, v20).knxproj";
    private static final String FILE_KNXPROJ_TWO_LEVEL = "src/test/resources/Project (2-Level, v20).knxproj";

    @ControllerTest(value = ProjectController.class, mockIfProjectPathIsEmpty = false)
    @DisplayName("ERROR: Try to get data about XML project structure although there is no XML project available")
    public void testNoProjectStructure(final Controller controller) {
        var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.projectStructure();
        assertThat(response).isNull();
    }

    /**
     * Tests the project structure endpoint that contains project structure metadata
     * <p>
     * Here it doesn't matter which group address style is used: free, two-level or three-level
     */
    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK: Get data about XML project structure")
    public void testProjectStructure(final Controller controller) {
        var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.projectStructure();
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response.getId()).isEqualTo("P-0503");
        assertThat(response.getName()).isEqualTo("Project (3-Level)");
        assertThat(response.getVersion()).isEqualTo(20);
        assertThat(response.getGroupAddressStyle()).isEqualTo("ThreeLevel");
        assertThat(response.getNumberOfGroupAddresses()).isEqualTo(189);
        assertThat(response.getNumberOfGroupRanges()).isEqualTo(18);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testProjectStructure.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get all main group ranges from XML project")
    public void testMainGroupRanges(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.getGroupRanges();
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(3);
        for (XmlGroupRange r : response) {
            assertThat(r.getLevel()).isEqualTo(0);
        }

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testMainGroupRanges.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("ERROR [Free-Level]: Get main group ranges from XML project")
    public void testMainGroupRangesForFreeLevel(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Mocking
        //

        final var xmlProject = projectController.getXmlProject();
        when(xmlProject.getGroupAddressStyle()).thenReturn(XmlGroupAddressStyle.FREE_LEVEL);

        //
        // Verification
        //

        final var response = projectController.getGroupRanges();
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.FORBIDDEN);
        assertThatJson(asJson(response)).isEqualTo("[]");
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get middle group ranges of main group '0' from XML project")
    public void testMiddleGroupRanges(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.getGroupRanges(0);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(8);
        for (XmlGroupRange r : response) {
            assertThat(r.getLevel()).isEqualTo(1);
        }

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testMiddleGroupRanges.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("ERROR [Free-Level]: Get middle group ranges of main group '0' from XML project - invalid for free-level")
    public void testMiddleGroupRangesForFreeLevel(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Mocking
        //

        final var xmlProject = projectController.getXmlProject();
        when(xmlProject.getGroupAddressStyle()).thenReturn(XmlGroupAddressStyle.FREE_LEVEL);

        //
        // Verification
        //

        final var response = projectController.getGroupRanges(0);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.FORBIDDEN);
        assertThatJson(asJson(response)).isEqualTo("[]");
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_TWO_LEVEL)
    @DisplayName("OK [Two-Level]: Get ALL group addresses of group range (0/*) from XML project")
    public void testTwoLevelGroupAddressesByRange(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Mocking
        //

        final var xmlProject = projectController.getXmlProject();
        when(xmlProject.getGroupAddressStyle()).thenReturn(XmlGroupAddressStyle.TWO_LEVEL);

        //
        // Verification
        //

        final var response = projectController.getGroupAddresses(0);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(146);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testTwoLevelGroupAddressesByRange.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_TWO_LEVEL)
    @DisplayName("ERROR [Two-Level]: Get ALL group addresses of group range (0/*) from XML project - invalid for free-level")
    public void testTwoLevelGroupAddressesByRangeError(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Mocking
        //

        final var xmlProject = projectController.getXmlProject();
        when(xmlProject.getGroupAddressStyle()).thenReturn(XmlGroupAddressStyle.FREE_LEVEL);

        //
        // Verification
        //

        final var response = projectController.getGroupAddresses(0);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.FORBIDDEN);
        assertThatJson(asJson(response)).isEqualTo("[]");
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get ALL group addresses of group range (0/0/*) from XML project")
    public void testThreeLevelGroupAddressesByRange(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.getGroupAddresses(0, 0);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(46);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testThreeLevelGroupAddressesByRange.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get 3rd-8th group addresses of range group (0/0/*) from XML project")
    public void testThreeLevelGroupAddressesWithLimit(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Mocking
        //
        when(controller.getRequest().getParameter("start")).thenReturn(new ParameterValue("1"));
        when(controller.getRequest().getParameter("limit")).thenReturn(new ParameterValue("4"));

        //
        // Verification
        //

        final var response = projectController.getGroupAddresses(0, 0);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(4);
        // 0/0/0 .. 0/0/9 not available
        // 0/0/10 skipped
        assertThat(response.get(0).getAddress()).isEqualTo("11"); // 0/0/11
        assertThat(response.get(1).getAddress()).isEqualTo("20"); // 0/0/20
        assertThat(response.get(2).getAddress()).isEqualTo("21"); // 0/0/21
        assertThat(response.get(3).getAddress()).isEqualTo("22"); // 0/0/22
        // 0/0/23 .. and more skipped (or not available)

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testThreeLevelGroupAddressesWithLimit.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("ERROR [Free-Level]: Get ALL group addresses of range group (0/0/*) from XML project - invalid for free-level")
    public void testThreeLevelGroupAddressesByRangeError(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Mocking
        //

        final var xmlProject = projectController.getXmlProject();
        when(xmlProject.getGroupAddressStyle()).thenReturn(XmlGroupAddressStyle.FREE_LEVEL);

        //
        // Verification
        //

        final var response = projectController.getGroupAddresses(0, 0);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.FORBIDDEN);
        assertThatJson(asJson(response)).isEqualTo("[]");
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get ALL addresses from XML project")
    public void testAllGroupAddresses(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.getGroupAddresses();
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(189);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testAllGroupAddresses.json"));
    }
}
