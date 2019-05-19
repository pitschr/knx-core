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

import li.pitschmann.knx.server.MockDaemonTest;
import li.pitschmann.knx.server.MockHttpDaemon;
import li.pitschmann.knx.server.MockServerTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

/**
 * Test class for {@link ProjectController}
 */
@Disabled
public class ProjectControllerTest {
    /**
     * Test {@code /} endpoint to get project related data (overview)
     *
     * @param daemon
     * @throws Exception
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/parser/Project (3-Level, v14).knxproj"))
    @DisplayName("Test / endpoint to obtain project related data (overview)")
    public void testProjectOverview(final MockHttpDaemon daemon) throws Exception {
        // TODO
    }

    /**
     * Test {@code /group} endpoint to get the main address group (1st level)
     *
     * @param daemon
     * @throws Exception
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/parser/Project (3-Level, v14).knxproj"))
    @DisplayName("Test /group endpoint to get main address group")
    public void testMainGroups(final MockHttpDaemon daemon) throws Exception {
        // TODO
    }


    /**
     * Test {@code /group/{mainId} endpoint to get the middle address group of
     * given main address group (2nd level)
     *
     * @param daemon
     * @throws Exception
     */
    @MockDaemonTest(@MockServerTest(projectPath = "src/test/resources/parser/Project (3-Level, v14).knxproj"))
    @DisplayName("Test /group/{mainId} endpoint to get main address group")
    public void testMiddleGroups(final MockHttpDaemon daemon) throws Exception {
        // TODO
    }
}
