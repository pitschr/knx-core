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
import li.pitschmann.knx.daemon.gson.DaemonGsonEngine;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.ControllerApplication;
import ro.pippo.core.Pippo;
import ro.pippo.guice.GuiceControllerFactory;

import java.util.concurrent.atomic.AtomicReference;

// TODO Proper Implementation not done yet
public class HttpDaemonApplication extends ControllerApplication {
    private static final Logger logger = LoggerFactory.getLogger(HttpDaemonApplication.class);
    private DefaultKnxClient knxClient;

    public static void main(String[] args) {
        final var pippo = new Pippo(new HttpDaemonApplication());
        pippo.start();
        while (true) {
            // leave it running
            System.out.println("ping ...");
            Sleeper.seconds(10);
        }
    }

    public void setKnxClient(DefaultKnxClient knxClient) {
        this.knxClient = knxClient;
    }

    @Override
    protected void onInit() {
        // sets the customized Gson engine
        getContentTypeEngines().setContentTypeEngine(DaemonGsonEngine.INSTANCE);

        // create guice injector
        final var injector = Guice.createInjector(new AbstractModule() {
            @Provides
            private final DefaultKnxClient providesKnxClient() {
                return knxClient;
            }
        });

        setControllerFactory(new GuiceControllerFactory(injector));

        // adds controller
        addControllers(HttpDaemonController.class);
    }
}
