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
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.communication.KnxStatusData;
import li.pitschmann.knx.core.datapoint.DataPointRegistry;
import li.pitschmann.knx.core.knxproj.XmlGroupAddress;
import li.pitschmann.knx.core.plugin.api.v1.json.Status;
import li.pitschmann.knx.core.plugin.api.v1.json.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * Controller for requesting the KNX client status pool
 */
public final class StatusController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(StatusController.class);
    private static final StatusResponse EMPTY_RESPONSE = new StatusResponse();

    public StatusController(final KnxClient knxClient) {
        super(knxClient);
    }

    /**
     * Endpoint to get all KNX status
     *
     * @param ctx the Javalin context
     */
    public void statusAll(final Context ctx) {
        log.trace("Http Status request for all available group addresses received");

        final var xmlProject = getKnxClient().getConfig().getProject();
        final var statusMap = getKnxClient().getStatusPool().copyStatusMap();
        final var responses = new ArrayList<StatusResponse>(statusMap.size());
        for (final var entry : statusMap.entrySet()) {
            // Group Address? If not, skip it!
            if (entry.getKey() instanceof GroupAddress) {
                final var groupAddress = (GroupAddress) entry.getKey();
                final var xmlGroupAddress = xmlProject.getGroupAddress(groupAddress);
                final var response = new StatusResponse();
                if (xmlGroupAddress != null) {
                    log.debug("Found group address in XML project: {}", groupAddress);
                    fill(response, groupAddress, xmlGroupAddress, entry.getValue());
                } else {
                    fill(response, groupAddress, null, null);
                }
                responses.add(response);
            }
        }

        final var list = limitAndGetAsList(ctx, responses);

        // set final http status code "Multi Status"
        ctx.status(207);
        ctx.json(list);
    }

    /**
     * Endpoint to get status of a single {@link GroupAddress}
     *
     * @param ctx          the Javalin context
     * @param groupAddress the group address
     */
    public void statusOne(final Context ctx, final GroupAddress groupAddress) {
        log.trace("Http Status Request received for: {}", groupAddress);

        // check if there is status data available in status pool
        final var knxStatusData = getKnxClient().getStatusPool().getStatusFor(groupAddress);
        if (knxStatusData == null) {
            log.warn("Status data not found for group address: {}", groupAddress);
            ctx.status(HttpServletResponse.SC_NOT_FOUND);
            ctx.json(EMPTY_RESPONSE);
            return;
        }

        // group address is known in XML project and there is status data available
        // fill all relevant properties
        final var response = new StatusResponse();
        final var xmlGroupAddress = getKnxClient().getConfig().getProject().getGroupAddress(groupAddress);
        fill(response, groupAddress, xmlGroupAddress, knxStatusData);

        ctx.status(HttpServletResponse.SC_OK);
        ctx.json(response);
    }

    /**
     * Fill the given {@link StatusResponse} with data that is requested by {@code $expand} parameter
     *
     * @param response        the status repsonse
     * @param groupAddress    KNX group address
     * @param xmlGroupAddress XML group address
     * @param knxStatusData   KNX status data
     */
    private void fill(final StatusResponse response,
                      final GroupAddress groupAddress,
                      final @Nullable XmlGroupAddress xmlGroupAddress,
                      final @Nullable KnxStatusData knxStatusData) {
        if (knxStatusData != null) {
            response.setTimestamp(knxStatusData.getTimestamp());
            response.setSourceAddress(knxStatusData.getSourceAddress());
            response.setApci(knxStatusData.getAPCI());
            response.setRaw(knxStatusData.getData());
            response.setDirty(knxStatusData.isDirty());
        } else {
            log.warn("No status data found for group address: {}", groupAddress);
        }

        if (xmlGroupAddress != null) {
            response.setDataPointType(DataPointRegistry.getDataPointType(xmlGroupAddress.getDataPointType()));
            response.setName(xmlGroupAddress.getName());
            response.setDescription(xmlGroupAddress.getDescription());
        } else {
            log.warn("Could not find group address in XML project: {}", groupAddress);
        }

        // status and group address is always displayed
        response.setStatus(knxStatusData == null ? Status.ERROR : Status.OK);
        response.setGroupAddress(groupAddress);
    }
}
