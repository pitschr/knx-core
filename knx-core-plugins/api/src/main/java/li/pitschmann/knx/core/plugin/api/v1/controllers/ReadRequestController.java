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
import li.pitschmann.knx.core.datapoint.DataPointRegistry;
import li.pitschmann.knx.core.plugin.api.v1.json.ReadRequest;
import li.pitschmann.knx.core.plugin.api.v1.json.ReadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller for read requests
 */
public final class ReadRequestController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(ReadRequestController.class);
    private static final ReadResponse EMPTY_RESPONSE = new ReadResponse();

    public ReadRequestController(final KnxClient knxClient) {
        super(knxClient);
    }

    /**
     * Endpoint for read request to be forwarded to KNX Net/IP device
     * <p>
     * As soon we get an acknowledge frame from KNX Net/IP device, we will wait for
     * a read request frame up to 3 seconds. If successful, the API will return the
     * actual value with details like data point type. Otherwise, we simply return
     * a JSON response with error state.
     *
     * @param ctx         the Javalin context
     * @param readRequest the read request from HTTP client
     */
    public void readRequest(final Context ctx, final ReadRequest readRequest) {
        log.trace("Http Read Request received: {}", readRequest);

        final var groupAddress = readRequest.getGroupAddress();

        // check if GA is provided
        if (groupAddress == null) {
            log.warn("Could not find group address in request.");
            ctx.status(HttpServletResponse.SC_BAD_REQUEST);
            ctx.json(EMPTY_RESPONSE);
            return;
        }

        // send read request
        if (!getKnxClient().readRequest(groupAddress)) {
            log.warn("No or unexpected acknowledge received for read request: {}", readRequest);
            ctx.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ctx.json(EMPTY_RESPONSE);
            return;
        }

        // wait for response from KNX Net/IP device to obtain the most recent raw values
        final var knxStatusData = getKnxClient().getStatusPool().getStatusFor(groupAddress);
        if (knxStatusData == null) {
            log.warn("Status data not found for group address: {}", groupAddress);
            ctx.status(HttpServletResponse.SC_NOT_FOUND);
            ctx.json(EMPTY_RESPONSE);
            return;
        }

        // everything OK
        log.debug("Status data found for group address: {}", groupAddress);
        final var response = new ReadResponse();

        // we add group address, dpt, name and description only if requested
        response.setGroupAddress(groupAddress);
        response.setRaw(knxStatusData.getData());

        final var xmlGroupAddress = getKnxClient().getConfig().getProject().getGroupAddress(groupAddress);
        if (xmlGroupAddress != null) {
            final var dpt = DataPointRegistry.getDataPointType(xmlGroupAddress.getDataPointType());
            response.setName(xmlGroupAddress.getName());
            response.setDescription(xmlGroupAddress.getDescription());
            response.setDataPointType(dpt);
            response.setValue(dpt.of(knxStatusData.getData()).toText());
            response.setUnit(dpt.getUnit());
        } else {
            log.warn("Could not find group address in XML project: {}", groupAddress);
        }

        ctx.status(HttpServletResponse.SC_OK);
        ctx.json(response);
    }
}
