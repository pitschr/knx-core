package li.pitschmann.knx.core.plugin.api.v1.controllers;

import li.pitschmann.knx.core.communication.KnxStatistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.Consumes;
import ro.pippo.controller.GET;
import ro.pippo.controller.Produces;

/**
 * Controller for requesting the statistic from KNX client
 */
public final class StatisticController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(StatisticController.class);

    /**
     * Endpoint for status request to return the current statistic of KNX client
     *
     * @return a new instance of {@link KnxStatistic}
     */
    @GET("/statistic")
    @Consumes(Consumes.JSON)
    @Produces(Produces.JSON)
    public KnxStatistic getStatistic() {
        log.trace("Http Statistic Request received");

        return getKnxClient().getStatistic();
    }
}
