package li.pitschmann.knx.core.plugin.api.v1.controllers;

import li.pitschmann.knx.core.body.address.GroupAddress;
import li.pitschmann.knx.core.communication.KnxStatusData;
import li.pitschmann.knx.core.datapoint.DataPointTypeRegistry;
import li.pitschmann.knx.core.knxproj.XmlGroupAddress;
import li.pitschmann.knx.core.plugin.api.v1.json.Status;
import li.pitschmann.knx.core.plugin.api.v1.json.StatusResponse;
import ro.pippo.controller.GET;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Param;


import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for requesting the KNX client status pool
 */
public final class StatusController extends AbstractController {
    private static final StatusResponse EMPTY_RESPONSE = new StatusResponse();

    /**
     * Endpoint to get all KNX status
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

        // TODO: this may be improved because we are filling first, and then return a sub-list only so it might be a big waste of resource
        return limitAndGetAsList(responses);
    }

    @GET("/status/{ga: \\d+(\\/\\d+)?(\\/\\d+)?}")  // supports: 1, 1/2, 1/2/3
    @Produces(Produces.JSON)
    public StatusResponse statusOne(final @Param("ga") String groupAddressStr) {
        log.trace("Http Status Request received for: {}", groupAddressStr);

        // check if there is status data available in status pool
        final var groupAddress = GroupAddress.of(groupAddressStr);
        final var knxStatusData = getKnxClient().getStatusPool().getStatusFor(groupAddress);
        if (knxStatusData == null) {
            log.warn("Status data not found for group address: {}", groupAddress);
            getResponse().notFound();
            return EMPTY_RESPONSE;
        }

        // group address is known in XML project and there is status data available
        // fill all relevant properties
        final var response = new StatusResponse();
        final var xmlGroupAddress = getXmlProject().getGroupAddress(groupAddress);
        fill(response, groupAddress, xmlGroupAddress, knxStatusData);
        getResponse().ok();
        return response;
    }

    /**
     * Fill the given {@link StatusResponse} with data that is requested by {@code $expand} parameter
     *
     * @param response
     * @param groupAddress
     * @param xmlGroupAddress
     * @param knxStatusData
     */
    private void fill(final StatusResponse response,
                      final GroupAddress groupAddress,
                      final @Nullable XmlGroupAddress xmlGroupAddress,
                      final @Nullable KnxStatusData knxStatusData) {
        if (knxStatusData != null) {
            response.setTimestamp(knxStatusData.getTimestamp());
            response.setSourceAddress(knxStatusData.getSourceAddress());
            response.setApci(knxStatusData.getApci());
            response.setRaw(knxStatusData.getApciData());
            response.setDirty(knxStatusData.isDirty());
        } else {
            log.warn("No status data found for group address: {}", groupAddress);
        }

        if (xmlGroupAddress != null) {
            response.setDataPointType(DataPointTypeRegistry.getDataPointType(xmlGroupAddress.getDataPointType()));
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
