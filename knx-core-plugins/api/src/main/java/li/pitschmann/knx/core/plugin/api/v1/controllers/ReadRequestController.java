package li.pitschmann.knx.core.plugin.api.v1.controllers;

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.datapoint.DataPointRegistry;
import li.pitschmann.knx.core.plugin.api.v1.json.ReadRequest;
import li.pitschmann.knx.core.plugin.api.v1.json.ReadResponse;
import ro.pippo.controller.Consumes;
import ro.pippo.controller.POST;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Body;

/**
 * Controller for read requests
 */
public final class ReadRequestController extends AbstractController {
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
     * @param readRequest the read request from HTTP client
     * @return a new instance of {@link ReadResponse}
     */
    @POST("/read")
    @Consumes(Consumes.JSON)
    @Produces(Produces.JSON)
    public ReadResponse readRequest(final @Body ReadRequest readRequest) {
        log.trace("Http Read Request received: {}", readRequest);

        final var groupAddress = readRequest.getGroupAddress();

        // check if GA is provided
        if (groupAddress == null) {
            log.warn("Could not find group address in request.");
            getResponse().badRequest();
            return EMPTY_RESPONSE;
        }

        // send read request
        if (!getKnxClient().readRequest(groupAddress)) {
            log.warn("No or unexpected acknowledge received for read request: {}", readRequest);
            getResponse().internalError();
            return EMPTY_RESPONSE;
        }

        // wait for response from KNX Net/IP device to obtain the most recent raw values
        final var knxStatusData = getKnxClient().getStatusPool().getStatusFor(groupAddress);
        if (knxStatusData == null) {
            log.warn("Status data not found for group address: {}", groupAddress);
            getResponse().notFound();
            return EMPTY_RESPONSE;
        }

        // everything OK
        log.debug("Status data found for group address: {}", groupAddress);
        final var response = new ReadResponse();

        // we add group address, dpt, name and description only if requested
        response.setGroupAddress(groupAddress);
        response.setRaw(knxStatusData.getApciData());

        final var xmlGroupAddress = getKnxClient().getConfig().getProject().getGroupAddress(groupAddress);
        if (xmlGroupAddress != null) {
            final var dpt = DataPointRegistry.getDataPointType(xmlGroupAddress.getDataPointType());
            response.setName(xmlGroupAddress.getName());
            response.setDescription(xmlGroupAddress.getDescription());
            response.setDataPointType(dpt);
            response.setValue(dpt.of(knxStatusData.getApciData()).toText());
            response.setUnit(dpt.getUnit());
        } else {
            log.warn("Could not find group address in XML project: {}", groupAddress);
        }

        getResponse().ok();

        return response;
    }
}
