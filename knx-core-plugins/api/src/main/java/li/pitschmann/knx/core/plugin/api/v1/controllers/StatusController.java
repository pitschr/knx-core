package li.pitschmann.knx.core.plugin.api.v1.controllers;

import li.pitschmann.knx.core.plugin.api.v1.json.Status;
import li.pitschmann.knx.core.plugin.api.v1.json.StatusRequest;
import li.pitschmann.knx.core.plugin.api.v1.json.StatusResponse;
import li.pitschmann.knx.core.body.address.GroupAddress;
import li.pitschmann.knx.core.communication.KnxStatusData;
import li.pitschmann.knx.core.datapoint.DataPointTypeRegistry;
import li.pitschmann.knx.core.parser.XmlGroupAddress;
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
    private static final StatusResponse EMPTY_RESPONSE = new StatusResponse();

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
                    fill(response, groupAddress, null, null);
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
        if (xmlGroupAddress == null) {
            getResponse().badRequest();
            return EMPTY_RESPONSE;
        }

        // check if there is status data available in status pool
        final var knxStatusData = getKnxClient().getStatusPool().getStatusFor(groupAddress);

        if (knxStatusData == null) {
            log.warn("Status data not found for group address: {}", groupAddress);
            getResponse().notFound();
            return EMPTY_RESPONSE;
        }

        // group address is known in XML project and there is status data available
        // fill all relevant properties
        final var response = new StatusResponse();
        fill(response, groupAddress, xmlGroupAddress, knxStatusData);
        getResponse().ok();
        return response;
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
        }
        if (knxStatusData == null) {
            log.warn("No status data found for group address: {}", groupAddress);
            isOK = false;
        }

        // status and group address is always displayed
        if (containsExpand("status")) {
            response.setStatus(isOK ? Status.OK : Status.ERROR);
        }
        if (containsExpand("groupAddress")) {
            response.setGroupAddress(groupAddress);
        }

        // other are displayed only when data are available
        if (isOK) {
            if (containsExpand("dpt")) {
                response.setDataPointType(DataPointTypeRegistry.getDataPointType(xmlGroupAddress.getDataPointType()));
            }
            if (containsExpand("name")) {
                response.setName(xmlGroupAddress.getName());
            }
            if (containsExpand("description")) {
                response.setDescription(xmlGroupAddress.getDescription());
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
