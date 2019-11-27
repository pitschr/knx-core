package li.pitschmann.knx.core.plugin.api.v1.controllers;

import li.pitschmann.knx.core.plugin.api.v1.json.WriteRequest;
import li.pitschmann.knx.core.plugin.api.v1.json.WriteResponse;
import li.pitschmann.knx.core.utils.ByteFormatter;
import ro.pippo.controller.Consumes;
import ro.pippo.controller.POST;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Body;

/**
 * Controller for write requests
 */
public final class WriteRequestController extends AbstractController {
    private static final WriteResponse EMPTY_RESPONSE = new WriteResponse();

    /**
     * Endpoint for write request to be forwarded to KNX Net/IP device
     * <p/>
     * As soon we get an acknowledge frame from KNX, our write request action is done!
     *
     * @param writeRequest
     * @return a new instance of {@link WriteResponse}
     */
    @POST("/write")
    @Consumes(Consumes.JSON)
    @Produces(Produces.JSON)
    public WriteResponse writeRequest(final @Body WriteRequest writeRequest) {
        log.trace("Http Write Request received: {}", writeRequest);

        final var groupAddress = writeRequest.getGroupAddress();

        // check if GA is provided
        if (groupAddress == null) {
            log.warn("Could not find group address in request.");
            getResponse().badRequest();
            return EMPTY_RESPONSE;
        }

        // found - group address is known, resolve the raw data for write request to KNX Net/IP device
        byte[] rawToWrite = writeRequest.getRaw();
        if (rawToWrite == null || rawToWrite.length == 0) {
            final var dpt = writeRequest.getDataPointType();
            final var dptValues = writeRequest.getValues();
            if (dpt == null || dptValues == null || dptValues.length == 0) {
                log.error("No DPT or/and DPT values defined for write request: {}", writeRequest);
                final var response = new WriteResponse();
                getResponse().badRequest();
                return response;
            } else {
                log.debug("DPT and DPT values received for write request: {}", writeRequest);
                rawToWrite = dpt.toValue(dptValues).toByteArray();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Write request to group address '{}' with bytes: {}", groupAddress, ByteFormatter.formatHexAsString(rawToWrite));
        }

        // send write request
        if (getKnxClient().writeRequest(groupAddress, rawToWrite)) {
            log.debug("Acknowledge received for write request: {}", writeRequest);
            getResponse().accepted();
        }
        // acknowledge not received or received with error?
        else {
            log.warn("No or unexpected acknowledge received for write request: {}", writeRequest);
            getResponse().internalError();
        }

        return EMPTY_RESPONSE;
    }
}
