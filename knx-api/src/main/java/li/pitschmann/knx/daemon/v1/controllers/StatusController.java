package li.pitschmann.knx.daemon.v1.controllers;

import li.pitschmann.knx.daemon.v1.json.Status;
import li.pitschmann.knx.daemon.v1.json.StatusRequest;
import li.pitschmann.knx.daemon.v1.json.StatusResponse;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.communication.KnxStatusData;
import li.pitschmann.knx.link.datapoint.DataPointTypeRegistry;
import li.pitschmann.knx.parser.XmlGroupAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.Consumes;
import ro.pippo.controller.GET;
import ro.pippo.controller.POST;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Body;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for requesting the KNX client status pool
 */
public final class StatusController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(StatusController.class);

    /**
     * Endpoint for all statuses
     *
     * @return
     */
    @GET("/status")
    @Produces(Produces.JSON)
    public List<StatusResponse> getStatus() {
        log.trace("Http Status request for all available group addresses received");

        final var statusMap = getKnxClient().getStatusPool().copyStatusMap();
        final var responses = new ArrayList<StatusResponse>(statusMap.size());
        for (final var entry : statusMap.entrySet()) {
            // Group Address? If not, skip it!
            if (entry.getKey() instanceof GroupAddress) {
                final var groupAddress = (GroupAddress) entry.getKey();
                final var xmlGroupAddress = getXmlProject().getGroupAddress(groupAddress);
                final var response = new StatusResponse();
                if (xmlGroupAddress != null) {
                    log.debug("Found group address in XML project: {}", groupAddress);
                    fill(response, xmlGroupAddress, groupAddress, entry.getValue());
                } else {
                    log.warn("Could not find group address in XML project: {}", groupAddress);
                    response.setStatus(Status.ERROR);
                }
                responses.add(response);
            }
        }

        // set final http status code "Multi Status"
        getResponse().status(207);

        return responses;
    }


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
        final var xmlGroupAddress = getXmlProject().getGroupAddress(groupAddress);
        if (xmlGroupAddress == null) {
            log.warn("Could not find group address in XML project: {}", groupAddress);
            final var response = new StatusResponse();
            response.setStatus(Status.ERROR);
            getResponse().notFound();
            return response;
        } else {
            // GA is known
            final var response = new StatusResponse();
            final var knxStatusData = getKnxClient().getStatusPool().getStatusFor(groupAddress);
            // fill all relevant properties
            fill(response, xmlGroupAddress, groupAddress, knxStatusData);
            return response;
        }
    }

    /**
     * Fill the given {@link StatusResponse} with data that is requested by {@code expand} parameter
     *
     * @param response
     * @param xmlGroupAddress
     * @param groupAddress
     * @param knxStatusData
     */
    private void fill(final StatusResponse response, final XmlGroupAddress xmlGroupAddress, final GroupAddress groupAddress, final KnxStatusData knxStatusData) {
        if (containsExpand("dpt")) {
            response.setDataPointType(DataPointTypeRegistry.getDataPointType(xmlGroupAddress.getDatapointType()));
        }
        if (containsExpand("name")) {
            response.setName(xmlGroupAddress.getName());
        }
        if (containsExpand("description")) {
            response.setDescription(xmlGroupAddress.getDescription());
        }

        if (knxStatusData == null) {
            log.warn("No status data found for group address: {}", groupAddress);
            response.setStatus(Status.ERROR);
            getResponse().notFound();
        } else {
            log.debug("Status data found for group address: {}", groupAddress);
            response.setStatus(Status.OK);
            getResponse().ok();

            if (containsExpand("groupAddress")) {
                response.setGroupAddress(groupAddress);
            }
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
            if (containsExpand("dirty")) {
                response.setDirty(knxStatusData.isDirty());
            }
        }
    }
}
