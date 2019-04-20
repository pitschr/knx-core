package li.pitschmann.knx.daemon;

import com.google.inject.Inject;
import li.pitschmann.knx.daemon.json.ReadRequest;
import li.pitschmann.knx.daemon.json.ReadResponse;
import li.pitschmann.knx.daemon.json.Status;
import li.pitschmann.knx.daemon.json.WriteRequest;
import li.pitschmann.knx.daemon.json.WriteResponse;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.datapoint.DPT1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.Consumes;
import ro.pippo.controller.Controller;
import ro.pippo.controller.POST;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Body;
import ro.pippo.core.HttpConstants;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Controller for read and write requests
 */
public final class HttpDaemonController extends Controller {
    private static final Logger log = LoggerFactory.getLogger(HttpDaemonController.class);

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
    public ReadResponse readRequest(@Body ReadRequest readRequest) {
        log.debug("Http Read Request received: {}", readRequest);
        final var groupAddress = readRequest.getGroupAddress();

        final var response = new ReadResponse();
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
                response.setDataPointType(DPT1.SWITCH); // todo --> get DPT from KNX Project
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

        return response;
    }

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
    public WriteResponse writeRequest(@Body WriteRequest writeRequest) {
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
