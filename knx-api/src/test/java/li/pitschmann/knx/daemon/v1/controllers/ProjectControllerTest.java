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
import org.junit.jupiter.api.Test;
import ro.pippo.core.HttpConstants;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link ProjectController}
 */
public class ProjectControllerTest extends AbstractControllerTest {

    /**
     * Tests the project overview endpoint for general project information
     */
    @Test
    @DisplayName("OK: Get XML project related data overview")
    public void testOverview() {
        final var controller = newController(ProjectController.class);

        //
        // Verification
        //

        final var response = controller.projectOverview();
        assertThat(response.getId()).isEqualTo("P-012F");
        assertThat(response.getName()).isEqualTo("Test Project Name");
        assertThat(response.getGroupAddressStyle()).isEqualTo("ThreeLevel");
        assertThat(response.getNumberOfGroupAddresses()).isEqualTo(12);
        assertThat(response.getNumberOfGroupRanges()).isEqualTo(3);

        final var responseJson = asJson(response);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testOverview.json"));
    }

    /**
     * Tests the returned main groups from XML project
     */
    @Test
    @DisplayName("OK: Get all main groups from XML project")
    public void testMainGroupRanges() {
        final var controller = newController(ProjectController.class);

        //
        // Verification
        //

        final var response = controller.mainGroups();
        assertThat(response).hasSize(3);

        final var responseJson = asJson(response);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testMainGroupRanges.json"));
    }

    /**
     * Tests the returned range groups of range group (0/*) from XML project
     */
    @Test
    @DisplayName("OK: Get all child range groups of main range group from XML project")
    public void testSubGroupRanges() {
        final var controller = newController(ProjectController.class);

        //
        // Verification
        //

        final var response = controller.getGroups(0);
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getChildGroupRanges()).hasSize(0);
        assertThat(response.get(0).getGroupAddresses()).hasSize(4);

        final var responseJson = asJson(response);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testSubGroupRanges.json"));
    }

    /**
     * Tests the returned group addresses of range group (0/0/*) from XML project
     */
    @Test
    @DisplayName("OK: Get all group addresses of range group (0/0/*) from XML project")
    public void testGroupAddresses() {
        final var controller = newController(ProjectController.class);

        //
        // Verification
        //

        final var response = controller.getAddresses(0, 0);
        assertThat(response).hasSize(4);

        final var responseJson = asJson(response);
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);
        assertThat(responseJson).isEqualTo(readJsonFile("/json/ProjectControllerTest-testGroupAddresses.json"));
    }

//    /**
//     * Test {@code /project/groups} endpoint to get the main address group (1st level)
//     *
//     * @param daemon
//     * @throws Exception
//     */
//    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v14).knxproj"))
//    @DisplayName("Test /project/groups endpoint to get main address group")
//    public void testMainGroups(final MockHttpDaemon daemon) throws Exception {
//        // get http client for requests
//        final var httpClient = HttpClient.newHttpClient();
//
//        // send request
//        final var httpRequest = daemon.newRequestBuilder("/api/v1/project/groups").GET().build();
//        final var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//
//        // validate status and the first group range IDs
//        assertThat(httpResponse.statusCode()).isEqualTo(HttpConstants.StatusCode.OK);
//        final var body = httpResponse.body();
//        assertThatJson(body).isArray().hasSize(3);
//        assertThatJson(body).node("[0].id").isEqualTo("P-0501-0_GR-47");
//        assertThatJson(body).node("[1].id").isEqualTo("P-0501-0_GR-67");
//        assertThatJson(body).node("[2].id").isEqualTo("P-0501-0_GR-69");
//    }
//
//
//    /**
//     * Test {@code /project/groups/<main>} endpoint to get the middle address group of
//     * given main address group (2nd level)
//     *
//     * @param daemon
//     * @throws Exception
//     */
//    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v14).knxproj"))
//    @DisplayName("Test /project/groups/{main} endpoint to get main address group")
//    public void testMainGroup(final MockHttpDaemon daemon) throws Exception {
//        // get http client for requests
//        final var httpClient = HttpClient.newHttpClient();
//
//        // send request (for main group 2/-/-)
//        final var httpRequest = daemon.newRequestBuilder("/api/v1/project/groups/2").GET().build();
//        final var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//
//        // validate status and the first group range IDs
//        assertThat(httpResponse.statusCode()).isEqualTo(HttpConstants.StatusCode.OK);
//        final var body = httpResponse.body();
//        assertThatJson(body).isObject();
//        assertThatJson(body).node("id").isEqualTo("P-0501-0_GR-69");
//        assertThatJson(body).node("childGroupRanges").isArray().hasSize(2);
//        assertThatJson(body).node("childGroupRanges[0].id").isEqualTo("P-0501-0_GR-70");
//        assertThatJson(body).node("childGroupRanges[1].id").isEqualTo("P-0501-0_GR-72");
//        assertThatJson(body).node("groupAddresses").isArray().isEmpty();
//    }
//
//    /**
//     * Test {@code /project/groups/<main>/<middle>} endpoint to get the middle address group of
//     * given main address group (2nd level)
//     *
//     * @param daemon
//     * @throws Exception
//     */
//    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/Project (3-Level, v14).knxproj"))
//    @DisplayName("Test /project/groups/{main}/{middle} endpoint to get main address group")
//    public void testMiddleGroups(final MockHttpDaemon daemon) throws Exception {
//        // get http client for requests
//        final var httpClient = HttpClient.newHttpClient();
//
//        // send request (for main group 0/3/-)
//        final var httpRequest = daemon.newRequestBuilder("/api/v1/project/groups/0/3").GET().build();
//        final var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//
//        // validate status and the first group range IDs
//        assertThat(httpResponse.statusCode()).isEqualTo(HttpConstants.StatusCode.OK);
//        final var body = httpResponse.body();
//
//        assertThatJson(body).isObject();
//        assertThatJson(body).node("id").isEqualTo("P-0501-0_GR-51");
//        assertThatJson(body).node("childGroupRanges").isArray().isEmpty();
//        assertThatJson(body).node("groupAddresses").isArray().hasSize(40);
//    }
}
