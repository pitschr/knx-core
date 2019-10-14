package li.pitschmann.knx.daemon.v1.controllers;

import li.pitschmann.knx.daemon.v1.json.ReadRequest;
import li.pitschmann.knx.daemon.v1.json.ReadResponse;
import li.pitschmann.knx.link.datapoint.DataPointTypeRegistry;
import ro.pippo.controller.Consumes;
import ro.pippo.controller.POST;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Body;

/**
 * Controller for read requests
 */
public final class ReadRequestController extends AbstractController {
    private static final ReadResponse EMPTY_RESPONSE = new ReadResponse();

    /**
     * Endpoint for read request to be forwarded to KNX Net/IP device by KNX Daemon
     * <p/>
     * As soon we get an acknowledge frame from KNX Net/IP device, we will wait for
     * a read request frame up to 3 seconds. If successful, the KNX Daemon will
     * return the actual value with details like data point type. Otherwise, we simply
     * return a JSON with error state.
     *
     * @param readRequest
     * @return a new instance of {@link ReadResponse}
     */
    @POST("/read")
    @Consumes(Consumes.JSON)
    @Produces(Produces.JSON)
    public ReadResponse readRequest(final @Body ReadRequest readRequest) {
        log.trace("Http Read Request received: {}", readRequest);

        final var groupAddress = readRequest.getGroupAddress();

        // check if GA is known
        final var xmlGroupAddress = getXmlProject().getGroupAddress(groupAddress);
        if (xmlGroupAddress == null) {
            log.warn("Could not find group address in XML project: {}", groupAddress);
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
        final var dpt = DataPointTypeRegistry.getDataPointType(xmlGroupAddress.getDataPointType());

        // we add group address, dpt, name and description only if requested
        if (containsExpand("groupAddress")) {
            response.setGroupAddress(groupAddress);
        }
        if (containsExpand("dpt")) {
            response.setDataPointType(dpt);
        }
        if (containsExpand("name")) {
            response.setName(xmlGroupAddress.getName());
        }
        if (containsExpand("description")) {
            response.setDescription(xmlGroupAddress.getDescription());
        }
        if (containsExpand("value")) {
            response.setValue(dpt.toValue(knxStatusData.getApciData()).toText());
        }
        if (containsExpand("unit")) {
            response.setUnit(dpt.getUnit());
        }
        if (containsExpand("raw")) {
            response.setRaw(knxStatusData.getApciData());
        }

        getResponse().ok();

        return response;
    }
}
