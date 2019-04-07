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

import com.google.gson.annotations.SerializedName;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.datapoint.DPT1;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.Application;
import ro.pippo.core.Pippo;

import java.util.concurrent.TimeUnit;

// TODO Proper Implementation not done yet
public class KnxHttpApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(KnxHttpApplication.class);
    private DefaultKnxClient knxClient;

    public static void main(String[] args) {
        final var pippo = new Pippo(new KnxHttpApplication());
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
//        Preconditions.checkNotNull(knxClient, "Please set KNX Client first!");
//
//        GET("/switchOn", routeContext -> {
//            knxClient.writeRequest(GroupAddress.of(1, 2, 50), DPT1.SWITCH.toValue(true));
//        });
//
//        GET("/switchOff", routeContext -> {
//            knxClient.writeRequest(GroupAddress.of(1, 2, 50), DPT1.SWITCH.toValue(false));
//        });
//        GET("/read/{id: [0-9]+/[0-9]+/[0-9]+", routeContext -> {
//            logger.debug("Got READ: {} {}", routeContext, routeContext.getParameter("id"));
//            final var groupAddress = GroupAddress.of(0, 0, 10);
//            knxClient.readRequest(groupAddress);
//            if (knxClient.getStatusPool().isUpdated(groupAddress, 1, TimeUnit.SECONDS)) {
//                routeContext.text().send(knxClient.getStatusPool().getStatusFor(groupAddress));
//            } else {
//                routeContext.text().send("NO REPOSNSE!");
//            }
//        });

        GET("/read", routeContext -> {
            logger.debug("Got READ: {}", routeContext);
            final var groupAddress = GroupAddress.of(0, 0, 10);
            knxClient.readRequest(groupAddress);
            if (knxClient.getStatusPool().isUpdated(groupAddress, 3, TimeUnit.SECONDS)) {
                routeContext.text().send(knxClient.getStatusPool().getStatusFor(groupAddress));
            } else {
                routeContext.text().send("NO REPOSNSE READ!");
            }
        });

        POST("/write", routeContext -> {
            logger.debug("Got WRITE: {}", routeContext);
            final var groupAddress = GroupAddress.of(0, 0, 10);
            knxClient.writeRequest(groupAddress, DPT1.SWITCH.toValue(true));
            if (knxClient.getStatusPool().isUpdated(groupAddress, 3, TimeUnit.SECONDS)) {
                routeContext.text().send("OK");
            } else {
                routeContext.text().send("NO REPOSNSE WRITE!");
            }
        });

        GET("/status", routeContext -> {
            routeContext.json().send(new JsonTest());
        });

        GET("/text", routeContext -> {
            routeContext.text().send("Hello World!");
        });
    }

    private class JsonTest {
        private String status = "OK";

        @SerializedName("MyOK")
        private String statusA = "OK1";

        private String statusB = "OK2";

        private String statusC = "OK3";

        public String getStatus() {
            return status;
        }
    }
}
