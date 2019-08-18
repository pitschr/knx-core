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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public List<StatusResponse> statusAll() {
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
                    fill(response, groupAddress, xmlGroupAddress, entry.getValue());
                } else {
                    fill(response, groupAddress, null, entry.getValue());
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
    public StatusResponse statusRequest(final @Body StatusRequest statusRequest) {
        log.trace("Http Status Request received: {}", statusRequest);

        final var groupAddress = statusRequest.getGroupAddress();

        // check if GA is known
        final var xmlGroupAddress = getXmlProject().getGroupAddress(groupAddress);
        final var response = new StatusResponse();
        if (xmlGroupAddress == null) {
            fill(response, groupAddress, null, null);
            getResponse().notFound();
            return response;
        } else {
            // GA is known
            final var knxStatusData = getKnxClient().getStatusPool().getStatusFor(groupAddress);
            // fill all relevant properties
            fill(response, groupAddress, xmlGroupAddress, knxStatusData);
            getResponse().ok();
            return response;
        }
    }

    /**
     * Fill the given {@link StatusResponse} with data that is requested by {@code expand} parameter
     *
     * @param response
     * @param groupAddress
     * @param xmlGroupAddress
     * @param knxStatusData
     */
    private void fill(final @Nonnull StatusResponse response, final @Nonnull GroupAddress groupAddress, final @Nullable XmlGroupAddress xmlGroupAddress, final @Nullable KnxStatusData knxStatusData) {
        boolean isOK = true;

        if (xmlGroupAddress == null) {
            log.warn("Could not find group address in XML project: {}", groupAddress);
            isOK = false;
        } else {
            if (containsExpand("dpt")) {
                response.setDataPointType(DataPointTypeRegistry.getDataPointType(xmlGroupAddress.getDatapointType()));
            }
            if (containsExpand("name")) {
                response.setName(xmlGroupAddress.getName());
            }
            if (containsExpand("description")) {
                response.setDescription(xmlGroupAddress.getDescription());
            }
        }

        if (knxStatusData == null) {
            log.warn("No status data found for group address: {}", groupAddress);
            isOK = false;
        } else {
            log.debug("Status data found for group address: {}", groupAddress);

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

        if (containsExpand("groupAddress")) {
            response.setGroupAddress(groupAddress);
        }

        if (containsExpand("status")) {
            response.setStatus(isOK ? Status.OK : Status.ERROR);
        }
    }
}
