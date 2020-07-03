/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2019 Pitschmann Christoph
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package li.pitschmann.knx.core.plugin.api;

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.api.gson.ApiGsonEngine;
import li.pitschmann.knx.core.plugin.api.v1.controllers.ProjectController;
import li.pitschmann.knx.core.plugin.api.v1.controllers.ReadRequestController;
import li.pitschmann.knx.core.plugin.api.v1.controllers.StatisticController;
import li.pitschmann.knx.core.plugin.api.v1.controllers.StatusController;
import li.pitschmann.knx.core.plugin.api.v1.controllers.WriteRequestController;
import ro.pippo.controller.Controller;
import ro.pippo.controller.ControllerApplication;
import ro.pippo.controller.GET;
import ro.pippo.controller.Produces;

import java.util.Objects;

/**
 * API Application for web server to serve KNX requests over HTTP
 * <p>
 * This loads the pippo relevant configuration, controllers, etc.
 */
public class ApiApplication extends ControllerApplication {
    private KnxClient knxClient;

    public ApiApplication(final KnxClient knxClient) {
        this.knxClient = Objects.requireNonNull(knxClient);
    }

    @SuppressWarnings("unchecked") // unchecked because of addControllers(..)
    @Override
    protected void onInit() {
        // sets the customized Gson engine
        getContentTypeEngines().setContentTypeEngine(ApiGsonEngine.INSTANCE);

        // adds controller for
        addControllers(
                new HeartbeatController(),
                new ReadRequestController(knxClient), //
                new WriteRequestController(knxClient), //
                new StatusController(knxClient), //
                new StatisticController(knxClient), //
                new ProjectController(knxClient) //
        );
    }

    /**
     * Heartbeat Controller
     * <p>
     * This one can be used by external application to see
     * if this plugin and web server are available
     */
    public static final class HeartbeatController extends Controller {

        /**
         * Returns the healthCheck
         *
         * @return empty body
         */
        @GET("/api/ping")
        @Produces(Produces.TEXT)
        public String healthCheck() {
            getResponse().noCache().text().ok();
            return "OK";
        }

    }
}
