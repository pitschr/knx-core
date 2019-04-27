package li.pitschmann.knx.daemon.controllers;

import com.google.inject.Inject;
import li.pitschmann.knx.daemon.json.ReadRequest;
import li.pitschmann.knx.daemon.json.ReadResponse;
import li.pitschmann.knx.daemon.json.Status;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.datapoint.DataPointTypeRegistry;
import li.pitschmann.knx.parser.XmlProject;
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

    @Inject
    private XmlProject xmlProject;

    @Inject
    private DefaultKnxClient knxClient;

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
        log.debug("Http Read Request received: {}", readRequest);
        getRequest().getParameter("expand");
        final var groupAddress = readRequest.getGroupAddress();

        final var response = new ReadResponse();

        // check if GA is known
        final var groupAddressObj = xmlProject.getGroupAddresses().stream().filter(x -> x.getAddress().equals(groupAddress.getAddress())).findFirst();
        if (groupAddressObj.isPresent()) {
            // found
            final var xmlGroupAddress = groupAddressObj.get();
            response.setDataPointType(DataPointTypeRegistry.getDataPointType(xmlGroupAddress.getDatapointType()));
            // we add name and description only if requested
            if (containsExpand("name")) {
                response.setName(xmlGroupAddress.getName());
            }
            if (containsExpand("description")) {
                response.setDescription(xmlGroupAddress.getDescription());
            }

            try {
                final var ackBody = knxClient.readRequest(groupAddress).get();
                if (ackBody == null) {
                    log.warn("No acknowledge received for read request: {}", readRequest);
                    response.setStatus(Status.ERROR);
                    getResponse().status(HttpConstants.StatusCode.GATEWAY_TIMEOUT);
                } else if (ackBody.getStatus() != li.pitschmann.knx.link.body.Status.E_NO_ERROR) {
                    log.warn("Unexpected KNX status '{}' received for read request: {}", ackBody.getStatus(), readRequest);
                    response.setStatus(Status.ERROR);
                    getResponse().internalError();
                } else if (knxClient.getStatusPool().isUpdated(groupAddress, 3, TimeUnit.SECONDS)) {
                    final var knxStatus = knxClient.getStatusPool().getStatusFor(groupAddress);
                    log.debug("KNX Status received for read request: {}", knxStatus);
                    response.setStatus(Status.OK);
                    response.setRaw(knxStatus.getApciData());
                    getResponse().ok();
                } else {
                    log.warn("No response received for read request: {}", readRequest);
                    response.setStatus(Status.ERROR);
                    getResponse().status(HttpConstants.StatusCode.GATEWAY_TIMEOUT);
                }
            } catch (ExecutionException executionEx) {
                log.error("Exception during sending read request", executionEx);
                response.setStatus(Status.ERROR);
                getResponse().internalError();
            } catch (InterruptedException ie) {
                log.debug("Interrupted signal received while read request: {}", readRequest);
                response.setStatus(Status.ERROR);
                getResponse().serviceUnavailable();
            }

        } else {
            log.warn("Could not find group address in XML project: {}", readRequest);
            log.debug("Group Addresses: {}", xmlProject.getGroupAddresses());
            response.setStatus(Status.ERROR);
            getResponse().notFound();
        }

        return response;
    }
}
