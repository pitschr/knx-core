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

import li.pitschmann.knx.parser.XmlGroupRange;
import org.junit.jupiter.api.DisplayName;
import ro.pippo.controller.Controller;
import ro.pippo.core.HttpConstants;
import ro.pippo.core.ParameterValue;

import static li.pitschmann.knx.test.TestUtils.asJson;
import static li.pitschmann.knx.test.TestUtils.readJsonFile;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ProjectController}
 */
public class ProjectControllerTest {
    private static final String FILE_KNXPROJ_THREE_LEVEL = "src/test/resources/Project (3-Level, v14).knxproj";

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
        assertThat(response.getId()).isEqualTo("P-0501");
        assertThat(response.getName()).isEqualTo("Project (3-Level)");
        assertThat(response.getGroupAddressStyle()).isEqualTo("ThreeLevel");
        assertThat(response.getNumberOfGroupAddresses()).isEqualTo(189);
        assertThat(response.getNumberOfGroupRanges()).isEqualTo(18);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testProjectStructure.json"));
    }

    /**
     * Tests endpoint to fetch main groups from XML project file
     */
    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get main groups from XML project")
    public void testMainGroups(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.mainGroups();
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(3);
        for (XmlGroupRange r : response) {
            assertThat(r.getLevel()).isEqualTo(0);
        }

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testMainGroups.json"));
    }

    /**
     * Tests endpoint to fetch middle groups (of main group 0) from XML project file
     */
    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get middle groups of main group '0' from XML project")
    public void testMiddleGroups(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.getGroups(0);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(8);
        for (XmlGroupRange r : response) {
            assertThat(r.getLevel()).isEqualTo(1);
        }

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testMiddleGroups.json"));
    }

    /**
     * Tests endpoint to fetch all group addresses of group 0/0/* (main group 0, middle group 0) from XML project file
     */
    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get group addresses of range group (0/0/*) from XML project")
    public void testGroupAddresses(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.getAddresses(0, 0);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(46);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testGroupAddresses.json"));
    }

    /**
     * Tests endpoint to fetch 3rd - 8th group addresses of group 0/0/* (main group 0, middle group 0)
     * from XML project file
     */
    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get 3rd-8th group addresses of range group (0/0/*) from XML project")
    public void testGroupAddressesWithLimit(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Mocking
        //
        when(controller.getRequest().getParameter("start")).thenReturn(new ParameterValue("1"));
        when(controller.getRequest().getParameter("limit")).thenReturn(new ParameterValue("4"));

        //
        // Verification
        //

        final var response = projectController.getAddresses(0, 0);
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
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testGroupAddressesWithLimit.json"));
    }
}
