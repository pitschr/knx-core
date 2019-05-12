package li.pitschmann.knx.daemon.v1.controllers;

import li.pitschmann.knx.daemon.v1.json.Status;
import li.pitschmann.knx.daemon.v1.json.StatusRequest;
import li.pitschmann.knx.daemon.v1.json.StatusResponse;
import li.pitschmann.knx.link.datapoint.DataPointTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.Consumes;
import ro.pippo.controller.POST;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Body;

/**
 * Controller for requesting the KNX client status pool
 */
public final class StatusController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(StatusController.class);

    /**
     * Endpoint for status request to check the status pool of KNX client
     *
     * @param statusRequest
     * @return a new instance of {@link StatusResponse}
     */
    @POST("/status")
    @Consumes(Consumes.JSON)
    @Produces(Produces.JSON)
    public StatusResponse getStatus(final @Body StatusRequest statusRequest) {
        log.trace("Http Status Request received: {}", statusRequest);

        final var groupAddress = statusRequest.getGroupAddress();

        // check if GA is known
        final var groupAddressOptional = getXmlProject().getGroupAddress(groupAddress);
        if (!groupAddressOptional.isPresent()) {
            log.warn("Could not find group address in XML project: {}", groupAddress);
            final var response = new StatusResponse();
            response.setStatus(Status.ERROR);
            getResponse().notFound();
            return response;
        }

        // found - group address is known
        final var xmlGroupAddress = groupAddressOptional.get();
        final var response = new StatusResponse();

        // we add dpt, name and description only if requested
        if (containsExpand("dpt")) {
            response.setDataPointType(DataPointTypeRegistry.getDataPointType(xmlGroupAddress.getDatapointType()));
        }
        if (containsExpand("name")) {
            response.setName(xmlGroupAddress.getName());
        }
        if (containsExpand("description")) {
            response.setDescription(xmlGroupAddress.getDescription());
        }

        final var knxStatusData = getKnxClient().getStatusPool().getStatusFor(groupAddress);
        if (knxStatusData == null) {
            log.warn("No status data found for group address: {}", groupAddress);
            response.setStatus(Status.ERROR);
            getResponse().notFound();
        } else {
            log.debug("Status data found for group address: {}", groupAddress);
            response.setStatus(Status.OK);
            getResponse().ok();

            if (containsExpand("timestamp")) {
                response.setTimestamp(knxStatusData.getTimestamp());
            }
            if (containsExpand("source")) {
                response.setSourceAddress(knxStatusData.getSourceAddress());
            }
            if (containsExpand("apci")) {
                response.setApci(knxStatusData.getApci());
            }
            if (containsExpand("raw")) {
                response.setRaw(knxStatusData.getApciData());
            }
        }

        return response;
    }
}
