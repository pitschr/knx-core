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
import li.pitschmann.knx.core.datapoint.value.DataPointValue;
import li.pitschmann.knx.core.plugin.api.v1.json.WriteRequest;
import li.pitschmann.knx.core.plugin.api.v1.json.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller for write requests
 */
public final class WriteRequestController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(WriteRequestController.class);
    private static final WriteResponse EMPTY_RESPONSE = new WriteResponse();

    public WriteRequestController(final KnxClient knxClient) {
        super(knxClient);
    }

    /**
     * Endpoint for write request to be forwarded to KNX Net/IP device
     * <p>
     * As soon we get an acknowledge frame from KNX, our write request action is done!
     *
     * @param ctx          the Javalin context
     * @param writeRequest write request from HTTP client
     */
    public void writeRequest(final Context ctx, final WriteRequest writeRequest) {
        log.trace("Http Write Request received: {}", writeRequest);

        final var groupAddress = writeRequest.getGroupAddress();

        // check if GA is provided
        if (groupAddress == null) {
            log.warn("Could not find group address in request.");
            ctx.status(HttpServletResponse.SC_BAD_REQUEST);
            ctx.json(EMPTY_RESPONSE);
            return;
        }

        final var dpt = writeRequest.getDataPointType();
        if (dpt == null) {
            // TODO: Fallback to DPT defined in ETS project?
            log.error("Could not find suitable data point type in request.");
            ctx.status(HttpServletResponse.SC_BAD_REQUEST);
            ctx.json(EMPTY_RESPONSE);
            return;
        }

        // found - group address is known, resolve the raw data for write request to KNX Net/IP device
        final DataPointValue value;
        byte[] rawToWrite = writeRequest.getRaw();
        if (rawToWrite != null && rawToWrite.length > 0) {
            value = dpt.of(rawToWrite);
        } else {
            final var dptValues = writeRequest.getValues();
            if (dptValues == null || dptValues.length == 0) {
                log.error("No DPT values defined for write request: {}", writeRequest);
                final var response = new WriteResponse();
                ctx.status(HttpServletResponse.SC_BAD_REQUEST);
                ctx.json(response);
                return;
            } else {
                log.debug("DPT values received for write request: {}", writeRequest);
                value = dpt.of(dptValues);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Write request to group address '{}' with: {}", groupAddress, value);
        }

        // send write request
        if (getKnxClient().writeRequest(groupAddress, value)) {
            log.debug("Acknowledge received for write request: {}", writeRequest);
            ctx.status(HttpServletResponse.SC_ACCEPTED);
            ctx.json(EMPTY_RESPONSE);
        }
        // acknowledge not received or received with error?
        else {
            log.warn("No or unexpected acknowledge received for write request: {}", writeRequest);
            ctx.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ctx.json(EMPTY_RESPONSE);
        }
    }
}
