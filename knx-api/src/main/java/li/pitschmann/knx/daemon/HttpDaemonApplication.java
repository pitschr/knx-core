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

package li.pitschmann.knx.daemon;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import li.pitschmann.knx.daemon.v1.gson.DaemonGsonEngine;
import li.pitschmann.knx.daemon.v1.controllers.ProjectController;
import li.pitschmann.knx.daemon.v1.controllers.ReadRequestController;
import li.pitschmann.knx.daemon.v1.controllers.StatisticController;
import li.pitschmann.knx.daemon.v1.controllers.StatusController;
import li.pitschmann.knx.daemon.v1.controllers.WriteRequestController;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.parser.XmlProject;
import ro.pippo.controller.ControllerApplication;
import ro.pippo.guice.GuiceControllerFactory;

/**
 * HTTP Application for KNX Http Daemon
 * <p>
 * This loads the pippo relevant configuration, controllers, etc.
 */
public class HttpDaemonApplication extends ControllerApplication {
    private DefaultKnxClient knxClient;
    private XmlProject xmlProject;

    public void setKnxClient(DefaultKnxClient knxClient) {
        this.knxClient = knxClient;
    }

    public void setXmlProject(XmlProject xmlProject) {
        this.xmlProject = xmlProject;
    }

    @SuppressWarnings("unchecked") // unchecked because of addControllers(..)
    @Override
    protected void onInit() {
        // create guice injector
        final var injector = Guice.createInjector(new AbstractModule() {
            @Provides
            private final DefaultKnxClient providesKnxClient() {
                return knxClient;
            }

            @Provides
            private final XmlProject providesXmlProject() {
                return xmlProject;
            }
        });
        setControllerFactory(new GuiceControllerFactory(injector));

        // sets the customized Gson engine
        getContentTypeEngines().setContentTypeEngine(DaemonGsonEngine.INSTANCE);

        // adds controller for endpoints
        addControllers(
                ReadRequestController.class, //
                WriteRequestController.class, //
                StatusController.class, //
                StatisticController.class, //
                ProjectController.class
        );
    }
}
