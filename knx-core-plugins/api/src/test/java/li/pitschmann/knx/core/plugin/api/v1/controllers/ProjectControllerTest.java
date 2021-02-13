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
import li.pitschmann.knx.core.knxproj.XmlGroupAddressStyle;
import li.pitschmann.knx.core.plugin.api.ControllerTest;
import li.pitschmann.knx.core.plugin.api.TestUtils;
import li.pitschmann.knx.core.plugin.api.v1.gson.ApiGsonEngine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import javax.servlet.http.HttpServletResponse;

import static li.pitschmann.knx.core.plugin.api.TestUtils.readJsonFile;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ProjectController}
 */
class ProjectControllerTest {
    private static final String FILE_KNXPROJ_THREE_LEVEL = "src/test/resources/Project (3-Level, v20).knxproj";
    private static final String FILE_KNXPROJ_TWO_LEVEL = "src/test/resources/Project (2-Level, v20).knxproj";

    @BeforeAll
    static void setUp() {
        final var gson = ApiGsonEngine.INSTANCE.getGson();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
    }

    @ControllerTest(value = ProjectController.class, mockIfProjectPathIsEmpty = false)
    @DisplayName("ERROR: Try to get data about XML project structure although there is no XML project available")
    void testNoProjectStructure(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Execution
        controller.projectStructure(contextSpy);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_NOT_FOUND);
        verify(contextSpy).result("");
    }

    /**
     * Tests the project structure endpoint that contains project structure metadata
     * <p>
     * Here it doesn't matter which group address style is used: free, two-level or three-level
     */
    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK: Get data about XML project structure")
    void testProjectStructure(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Execution
        controller.projectStructure(contextSpy);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_OK);
        verify(contextSpy).result(readJsonFile("/json/ProjectControllerTest-testProjectStructure.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get all main group ranges from XML project")
    void testMainGroupRanges(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Execution
        controller.getGroupRanges(contextSpy);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_OK);
        verify(contextSpy).result(readJsonFile("/json/ProjectControllerTest-testMainGroupRanges.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("ERROR [Free-Level]: Get main group ranges from XML project")
    void testMainGroupRangesForFreeLevel(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Change group address style from 'three-level' to 'free-level' to
        // reproduce an issue as free level has no group ranges
        final var xmlProject = controller.getKnxClient().getConfig().getProject();
        when(xmlProject.getGroupAddressStyle()).thenReturn(XmlGroupAddressStyle.FREE_LEVEL);

        // Execution
        controller.getGroupRanges(contextSpy);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_FORBIDDEN);
        verify(contextSpy).result("[]");
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get middle group ranges of main group '0' from XML project")
    public void testMiddleGroupRanges(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Execution
        controller.getGroupRanges(contextSpy, 0);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_OK);
        verify(contextSpy).result(readJsonFile("/json/ProjectControllerTest-testMiddleGroupRanges.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("ERROR [Free-Level]: Get middle group ranges of main group '0' from XML project - invalid for free-level")
    public void testMiddleGroupRangesForFreeLevel(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Change group address style from 'three-level' to 'free-level' to
        // reproduce an issue
        final var xmlProject = controller.getKnxClient().getConfig().getProject();
        when(xmlProject.getGroupAddressStyle()).thenReturn(XmlGroupAddressStyle.FREE_LEVEL);

        // Execution
        controller.getGroupRanges(contextSpy, 0);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_FORBIDDEN);
        verify(contextSpy).result("[]");
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_TWO_LEVEL)
    @DisplayName("OK [Two-Level]: Get ALL group addresses of group range (0/*) from XML project")
    public void testTwoLevelGroupAddressesByRange(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();

        //
        // Mocking
        //
        final var xmlProject = controller.getKnxClient().getConfig().getProject();
        when(xmlProject.getGroupAddressStyle()).thenReturn(XmlGroupAddressStyle.TWO_LEVEL);

        // Execution
        controller.getGroupAddresses(contextSpy, 0);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_OK);
        System.out.println(contextSpy.toString());
        verify(contextSpy).result(readJsonFile("/json/ProjectControllerTest-testTwoLevelGroupAddressesByRange.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_TWO_LEVEL)
    @DisplayName("ERROR [Two-Level]: Get ALL group addresses of group range (0/*) from XML project - invalid for free-level")
    public void testTwoLevelGroupAddressesByRangeError(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Change group address style from 'two-level' to 'free-level' to
        // reproduce an issue
        final var xmlProject = controller.getKnxClient().getConfig().getProject();
        when(xmlProject.getGroupAddressStyle()).thenReturn(XmlGroupAddressStyle.FREE_LEVEL);

        // Execution
        controller.getGroupAddresses(contextSpy, 0);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_FORBIDDEN);
        verify(contextSpy).result("[]");
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get ALL group addresses of group range (0/0/*) from XML project")
    public void testThreeLevelGroupAddressesByRange(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Execution
        controller.getGroupAddresses(contextSpy, 0, 0);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_OK);
        verify(contextSpy).result(readJsonFile("/json/ProjectControllerTest-testThreeLevelGroupAddressesByRange.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get 3rd-8th group addresses of range group (0/0/*) from XML project")
    public void testThreeLevelGroupAddressesWithLimit(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();
        when(contextSpy.queryString()).thenReturn("start=1&limit=4");

        // Execution
        controller.getGroupAddresses(contextSpy, 0, 0);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_OK);
        verify(contextSpy).result(readJsonFile("/json/ProjectControllerTest-testThreeLevelGroupAddressesWithLimit.json"));
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("ERROR [Free-Level]: Get ALL group addresses of range group (0/0/*) from XML project - invalid for free-level")
    public void testThreeLevelGroupAddressesByRangeError(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Change group address style from 'three-level' to 'free-level' to
        // reproduce an issue
        final var xmlProject = controller.getKnxClient().getConfig().getProject();
        when(xmlProject.getGroupAddressStyle()).thenReturn(XmlGroupAddressStyle.FREE_LEVEL);

        // Execution
        controller.getGroupAddresses(contextSpy, 0, 0);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_FORBIDDEN);
        verify(contextSpy).result("[]");
    }

    @ControllerTest(value = ProjectController.class, projectPath = FILE_KNXPROJ_THREE_LEVEL)
    @DisplayName("OK [Three-Level]: Get ALL addresses from XML project")
    public void testAllGroupAddresses(final ProjectController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Execution
        controller.getGroupAddresses(contextSpy);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_OK);
        verify(contextSpy).result(readJsonFile("/json/ProjectControllerTest-testAllGroupAddresses.json"));
    }
}
