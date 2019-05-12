package li.pitschmann.knx.daemon.v1.controllers;

import li.pitschmann.knx.daemon.v1.json.ReadRequest;
import li.pitschmann.knx.daemon.v1.json.ReadResponse;
import li.pitschmann.knx.daemon.v1.json.Status;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.datapoint.DataPointTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.Consumes;
import ro.pippo.controller.POST;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Body;
import ro.pippo.core.HttpConstants;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Controller for read requests
 */
public final class ReadRequestController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(ReadRequestController.class);

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
        final var groupAddressOptional = getXmlProject().getGroupAddress(groupAddress);
        if (!groupAddressOptional.isPresent()) {
            log.warn("Could not find group address in XML project: {}", groupAddress);
            final var response = new ReadResponse();
            response.setStatus(Status.ERROR);
            getResponse().notFound();
            return response;
        }

        // found - group address is known, send read request
        TunnelingAckBody ackBody = null;
        try {
            ackBody = getKnxClient().readRequest(groupAddress).get();
        } catch (final ExecutionException | InterruptedException ex) {
            log.error("Exception during sending read request", ex);
        }

        final var response = new ReadResponse();

        // acknowledge not received?
        if (ackBody == null) {
            log.warn("No acknowledge received for read request: {}", readRequest);
            response.setStatus(Status.ERROR);
            getResponse().internalError();
        }
        // acknowledge received with error?
        else if (ackBody.getStatus() != li.pitschmann.knx.link.body.Status.E_NO_ERROR) {
            log.warn("Unexpected KNX acknowledge status '{}' received for read request: {}", ackBody.getStatus(), readRequest);
            response.setStatus(Status.ERROR);
            getResponse().internalError();
        }
        // everything OK
        else {
            log.debug("Acknowledge received for read request: {}", readRequest);
            final var xmlGroupAddress = groupAddressOptional.get();

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

            if (containsExpand("raw")) {
                // wait for response from KNX Net/IP device to obtain the most recent raw values
                final var knxStatusData = getKnxClient().getStatusPool().getStatusFor(groupAddress, 3, TimeUnit.SECONDS, true);
                if (knxStatusData != null) {
                    log.debug("Status data found for group address: {}", groupAddress);
                    response.setStatus(Status.OK);
                    response.setRaw(knxStatusData.getApciData());
                    getResponse().ok();
                } else {
                    log.warn("Status data not found for group address: {}", groupAddress);
                    response.setStatus(Status.ERROR);
                    getResponse().status(HttpConstants.StatusCode.GATEWAY_TIMEOUT);
                }
            } else {
                response.setStatus(Status.OK);
                getResponse().ok();
            }
        }

        return response;
    }
}
