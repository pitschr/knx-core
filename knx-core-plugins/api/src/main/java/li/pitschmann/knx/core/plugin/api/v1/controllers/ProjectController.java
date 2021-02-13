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

import io.javalin.http.Context;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.knxproj.XmlGroupAddressStyle;
import li.pitschmann.knx.core.plugin.api.v1.json.ProjectStructureResponse;
import li.pitschmann.knx.core.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * Controller for project-specific endpoints to return some data
 * from KNX Project file
 */
public final class ProjectController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    public ProjectController(final KnxClient knxClient) {
        super(knxClient);
    }

    /**
     * Returns the project structure containing metadata from *.knxproj file
     *
     * @param ctx the Javalin context
     */
    public void projectStructure(final Context ctx) {
        log.trace("Request for project overview");

        final var xmlProject = getKnxClient().getConfig().getProject();
        if (xmlProject == null) {
            log.error("No project file found.");
            ctx.status(HttpServletResponse.SC_NOT_FOUND);
            ctx.result("");
            return;
        }

        // get project data
        final var response = new ProjectStructureResponse();
        response.setId(xmlProject.getId());
        response.setName(xmlProject.getName());
        response.setGroupAddressStyle(xmlProject.getGroupAddressStyle().getCode());
        response.setVersion(xmlProject.getVersion());
        response.setNumberOfGroupRanges(xmlProject.getGroupRanges().size());
        response.setNumberOfGroupAddresses(xmlProject.getGroupAddresses().size());

        ctx.status(HttpServletResponse.SC_OK);
        ctx.json(response);
    }

    /**
     * Returns the all group main ranges for a two-level project ({@code main}/{@code sub})
     * or a three-level project ({@code main}/{@code middle}/{@code sub})
     * <p>
     * Not supported for: free-level projects
     *
     * @param ctx the Javalin context
     */
    public void getGroupRanges(final Context ctx) {
        log.trace("Request for all main group ranges in project");

        final var xmlProject = getKnxClient().getConfig().getProject();
        final var groupAddressStyle = xmlProject.getGroupAddressStyle();

        if (groupAddressStyle == XmlGroupAddressStyle.THREE_LEVEL ||
                groupAddressStyle == XmlGroupAddressStyle.TWO_LEVEL) {
            // two-level or three-level
            final var mainRanges = xmlProject.getMainGroupRanges();
            log.debug("All main group ranges found: {}", mainRanges);

            final var list = limitAndGetAsList(ctx, mainRanges);
            ctx.status(HttpServletResponse.SC_OK);
            ctx.json(list);
        } else {
            // not supported for free-level
            ctx.status(HttpServletResponse.SC_FORBIDDEN);
            ctx.json(Collections.emptyList());
        }
    }

    /**
     * Returns the middle group ranges for a three-level project ({@code main}/{@code middle}/{@code sub})
     * <ul>
     * <li>Main: 0 .. 31</li>
     * </ul>
     * Not supported for: two-level and free-level projects
     *
     * @param ctx  the Javalin context
     * @param main the main group
     */
    public void getGroupRanges(final Context ctx, int main) {
        log.trace("Request for middle group ranges of main group range: {}", main);
        checkArgumentMainGroupRange(main);

        final var xmlProject = getKnxClient().getConfig().getProject();
        if (xmlProject.getGroupAddressStyle() == XmlGroupAddressStyle.THREE_LEVEL) {
            // only three-level
            final var mainRange = xmlProject.getGroupRange(main);
            log.debug("Main group range '{}' found: {}", main, mainRange);

            final var list = limitAndGetAsList(ctx, mainRange.getChildGroupRanges());

            ctx.status(HttpServletResponse.SC_OK);
            ctx.json(list);
        } else {
            // not supported for free-level and two-level
            ctx.status(HttpServletResponse.SC_FORBIDDEN);
            ctx.json(List.of());
        }
    }

    /**
     * Returns all group addresses
     * <p>
     * Supported for all projects: free-level, two-level and three-level
     *
     * @param ctx the Javalin context
     */
    public void getGroupAddresses(final Context ctx) {
        log.trace("Request all group addresses");

        final var list = limitAndGetAsList(ctx, getKnxClient().getConfig().getProject().getGroupAddresses());

        ctx.status(HttpServletResponse.SC_OK);
        ctx.json(list);
    }

    /**
     * Returns the group addresses for a two-level project ({@code main}/{@code sub})
     * <ul>
     * <li>Main: 0 .. 31</li>
     * </ul>
     * Not supported for: three-level and free-level projects
     *
     * @param ctx  the Javalin context
     * @param main the main group
     */
    public void getGroupAddresses(final Context ctx, int main) {
        log.trace("Request addresses for main group range: {}", main);
        checkArgumentMainGroupRange(main);

        final var xmlProject = getKnxClient().getConfig().getProject();
        if (xmlProject.getGroupAddressStyle() == XmlGroupAddressStyle.TWO_LEVEL) {
            final var middleGroup = xmlProject.getGroupRange(main);
            log.debug("Middle Group Range for main group range '{}' found: {}", main, middleGroup);

            final var list = limitAndGetAsList(ctx, middleGroup.getGroupAddresses());
            ctx.status(HttpServletResponse.SC_OK);
            ctx.json(list);
        } else {
            ctx.status(HttpServletResponse.SC_FORBIDDEN);
            ctx.json(List.of());
        }
    }

    /**
     * Returns the group addresses for a three-level project ({@code main}/{@code middle}/{@code sub})
     * <ul>
     * <li>Main: 0 .. 31</li>
     * <li>Middle: 0 .. 7</li>
     * </ul>
     * Not supported for: two-level and free-level projects
     *
     * @param ctx    the Javalin context
     * @param main   the main group
     * @param middle the middle group
     */
    public void getGroupAddresses(final Context ctx, int main, int middle) {
        log.trace("Request addresses for middle group: {}/{}", main, middle);
        checkArgumentMainGroupRange(main);
        checkArgumentMiddleGroupRange(middle);

        final var xmlProject = getKnxClient().getConfig().getProject();
        if (xmlProject.getGroupAddressStyle() == XmlGroupAddressStyle.THREE_LEVEL) {
            final var middleGroup = xmlProject.getGroupRange(main, middle);
            log.debug("Middle Group Range for main group range '{}/{}' found: {}", main, middle, middleGroup);

            final var list = limitAndGetAsList(ctx, middleGroup.getGroupAddresses());
            ctx.status(HttpServletResponse.SC_OK);
            ctx.json(list);
        } else {
            ctx.status(HttpServletResponse.SC_FORBIDDEN);
            ctx.json(List.of());
        }
    }

    private void checkArgumentMainGroupRange(final int main) {
        Preconditions.checkArgument(main >= 0 && main <= 31,
                "Invalid number of main group provided, should be within range [0-31]: {}", main);
    }

    private void checkArgumentMiddleGroupRange(final int middle) {
        Preconditions.checkArgument(middle >= 0 && middle <= 7,
                "Invalid number of middle group provided, should be within range [0-7]: {}", middle);
    }
}
