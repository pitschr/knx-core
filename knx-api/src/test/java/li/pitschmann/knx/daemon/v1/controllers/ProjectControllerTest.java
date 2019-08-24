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

import org.junit.jupiter.api.DisplayName;
import ro.pippo.controller.Controller;
import ro.pippo.core.HttpConstants;

import static li.pitschmann.knx.test.TestUtils.asJson;
import static li.pitschmann.knx.test.TestUtils.readJsonFile;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link ProjectController}
 */
public class ProjectControllerTest {

    /**
     * Tests the project overview endpoint for general project information
     */
    @ControllerTest(value = ProjectController.class, projectPath = "src/test/resources/Project (3-Level, v14).knxproj")
    @DisplayName("OK: Get XML project related data overview")
    public void testOverview(final Controller controller) {
        var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.projectOverview();
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response.getId()).isEqualTo("P-0501");
        assertThat(response.getName()).isEqualTo("Project (3-Level)");
        assertThat(response.getGroupAddressStyle()).isEqualTo("ThreeLevel");
        assertThat(response.getNumberOfGroupAddresses()).isEqualTo(189);
        assertThat(response.getNumberOfGroupRanges()).isEqualTo(18);

        final var responseJson = asJson(response);
        assertThat(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testOverview.json"));
    }

    /**
     * Tests the returned main groups from XML project
     */
    @ControllerTest(value = ProjectController.class, projectPath = "src/test/resources/Project (3-Level, v14).knxproj")
    @DisplayName("OK: Get all main groups from XML project")
    public void testMainGroupRanges(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.mainGroups();
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(3);

        final var responseJson = asJson(response);
        assertThat(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testMainGroupRanges.json"));
    }

    /**
     * Tests the returned range groups of range group (0/*) from XML project
     */
    @ControllerTest(value = ProjectController.class, projectPath = "src/test/resources/Project (3-Level, v14).knxproj")
    @DisplayName("OK: Get all child range groups of main range group from XML project")
    public void testSubGroupRanges(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.getGroups(0);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(8);

        final var responseJson = asJson(response);
        assertThat(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testSubGroupRanges.json"));
    }

    /**
     * Tests the returned group addresses of range group (0/0/*) from XML project
     */
    @ControllerTest(value = ProjectController.class, projectPath = "src/test/resources/Project (3-Level, v14).knxproj")
    @DisplayName("OK: Get all group addresses of range group (0/0/*) from XML project")
    public void testGroupAddresses(final Controller controller) {
        final var projectController = (ProjectController) controller;

        //
        // Verification
        //

        final var response = projectController.getAddresses(0, 0);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(response).hasSize(46);

        final var responseJson = asJson(response);
        assertThat(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testGroupAddresses.json"));
    }
}
