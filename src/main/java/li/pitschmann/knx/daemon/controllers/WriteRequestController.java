package li.pitschmann.knx.daemon.controllers;

import com.google.inject.Inject;
import li.pitschmann.knx.daemon.json.Status;
import li.pitschmann.knx.daemon.json.WriteRequest;
import li.pitschmann.knx.daemon.json.WriteResponse;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.parser.XmlProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.Consumes;
import ro.pippo.controller.POST;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Body;
import ro.pippo.core.HttpConstants;

import java.util.concurrent.ExecutionException;

/**
 * Controller for write requests
 */
public final class WriteRequestController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(WriteRequestController.class);

    @Inject
    private XmlProject xmlProject;

    @Inject
    private DefaultKnxClient knxClient;

    /**
     * Endpoint for write request to be forwarded to KNX Net/IP device by KNX Daemon.
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
        log.debug("Http Write Request received: {}", writeRequest);

        final var groupAddress = writeRequest.getGroupAddress();
        final var dpt = writeRequest.getDataPointType();
        final var dptValues = writeRequest.getValues();

        final var response = new WriteResponse();
        try {
            final var ackBody = knxClient.writeRequest(groupAddress, dpt.toValue(dptValues)).get();
            if (ackBody == null) {
                log.warn("No acknowledge received for write request: {}", writeRequest);
                response.setStatus(Status.ERROR);
                getResponse().status(HttpConstants.StatusCode.GATEWAY_TIMEOUT);
            } else if (ackBody.getStatus() != li.pitschmann.knx.link.body.Status.E_NO_ERROR) {
                log.warn("Unexpected KNX status '{}' received for write request: {}", ackBody.getStatus(), writeRequest);
                response.setStatus(Status.ERROR);
                getResponse().internalError();
            } else {
                response.setStatus(Status.OK);
                getResponse().ok();
            }
        } catch (ExecutionException executionEx) {
            log.error("Exception during sending write request", executionEx);
            response.setStatus(Status.ERROR);
            getResponse().internalError();
        } catch (InterruptedException ie) {
            log.debug("Interrupted signal received while write request: {}", writeRequest);
            response.setStatus(Status.ERROR);
            getResponse().serviceUnavailable();
        }

        return response;
    }
}
