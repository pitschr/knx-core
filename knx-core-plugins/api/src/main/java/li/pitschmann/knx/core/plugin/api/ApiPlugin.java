/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2021 Pitschmann Christoph
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

import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.ExtensionPlugin;
import li.pitschmann.knx.core.plugin.IntegerConfigValue;
import li.pitschmann.knx.core.plugin.api.v1.controllers.HeartbeatController;
import li.pitschmann.knx.core.plugin.api.v1.controllers.ProjectController;
import li.pitschmann.knx.core.plugin.api.v1.controllers.ReadRequestController;
import li.pitschmann.knx.core.plugin.api.v1.controllers.StatisticController;
import li.pitschmann.knx.core.plugin.api.v1.controllers.StatusController;
import li.pitschmann.knx.core.plugin.api.v1.controllers.WriteRequestController;
import li.pitschmann.knx.core.plugin.api.v1.gson.ApiGsonEngine;
import li.pitschmann.knx.core.plugin.api.v1.json.ReadRequest;
import li.pitschmann.knx.core.plugin.api.v1.json.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Plugin for RESTful API (web server)
 */
public final class ApiPlugin implements ExtensionPlugin {
    /**
     * Default port for Pippo Micro Web Server (re-using the default port value)
     */
    public static final IntegerConfigValue PORT = new IntegerConfigValue(
            "port",
            () -> 8338,
            Objects::nonNull
    );

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final Javalin javalin = Javalin.create();
    private KnxClient client;
    private int serverPort;

    @Override
    public void onInitialization(final KnxClient client) {
        this.client = Objects.requireNonNull(client);
        this.serverPort = client.getConfig(PORT);
    }

    @Override
    public void onStart() {
        // start already, api should be ready when communication starts
        javalin.start(this.serverPort);

        final var gson = ApiGsonEngine.INSTANCE.getGson();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);

        /*
         * Heartbeat Controller
         * Endpoint: /api/v1/ping
         */
        final var heartbeatController = new HeartbeatController(client);
        javalin.get("/api/v1/ping", heartbeatController::ping);

        /*
         * Project Controller
         * Endpoints:
         *   /api/v1/project
         *   /api/v1/project/..
         */
        final var projectController = new ProjectController(client);
        javalin.get("/api/v1/project", projectController::projectStructure);
        javalin.get("/api/v1/project/ranges", projectController::getGroupRanges);
        javalin.get("/api/v1/project/ranges/:main", ctx -> {
            final int main = Integer.parseInt(ctx.pathParam("main"));
            projectController.getGroupRanges(ctx, main);
        });
        javalin.get("/api/v1/project/addresses", projectController::getGroupAddresses);
        javalin.get("/api/v1/project/addresses/:main", ctx -> {
            final int main = Integer.parseInt(ctx.pathParam("main"));
            projectController.getGroupAddresses(ctx, main);
        });
        javalin.get("/api/v1/project/addresses/:main/:middle", ctx -> {
            final int main = Integer.parseInt(ctx.pathParam("main"));
            final int middle = Integer.parseInt(ctx.pathParam("middle"));
            projectController.getGroupAddresses(ctx, main, middle);
        });

        /*
         * Read Request Controller
         * Endpoint: /api/v1/read
         */
        final var readRequestController = new ReadRequestController(client);
        javalin.post("/api/v1/read", ctx -> {
            final var readRequest = JavalinJson.fromJson(ctx.body(), ReadRequest.class);
            readRequestController.readRequest(ctx, readRequest);
        });

        /*
         * Statistic Controller
         * Endpoint: /api/v1/statistic
         */
        final var statisticController = new StatisticController(client);
        javalin.get("/api/v1/statistic", statisticController::getStatistic);

        /*
         * Status Controller
         * Endpoints:
         *   /api/v1/status
         *   /api/v1/status/..
         */
        final var statusController = new StatusController(client);
        javalin.get("/api/v1/status", statusController::statusAll);
        javalin.get("/api/v1/status/:ga", ctx -> {
            // supports: 1, 1/2, 1/2/3
            final var groupAddress = GroupAddress.of(ctx.pathParam("ga"));
            statusController.statusOne(ctx, groupAddress);
        });

        final var writeRequestController = new WriteRequestController(client);
        javalin.post("/api/v1/write", ctx -> {
            final var writeRequest = JavalinJson.fromJson(ctx.body(), WriteRequest.class);
            writeRequestController.writeRequest(ctx, writeRequest);
        });

        log.debug("API Plugin and Web Server started at port {}: {}", serverPort, client);
    }

    @Override
    public void onShutdown() {
        javalin.stop();
        log.debug("API Plugin and Web Server stopped.");
    }

    /**
     * Returns the configured port
     *
     * @return the server port
     */
    public final int getPort() {
        return serverPort;
    }

}
