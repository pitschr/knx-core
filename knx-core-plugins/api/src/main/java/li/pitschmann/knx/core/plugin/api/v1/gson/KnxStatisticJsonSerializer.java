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

package li.pitschmann.knx.core.plugin.api.v1.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ConnectResponseBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.ConnectionStateResponseBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DescriptionResponseBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.DisconnectResponseBody;
import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.body.RoutingIndicationBody;
import li.pitschmann.knx.core.body.SearchRequestBody;
import li.pitschmann.knx.core.body.SearchResponseBody;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.communication.KnxStatistic;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Serializes the {@link KnxStatistic} to a JSON format
 */
public final class KnxStatisticJsonSerializer implements JsonSerializer<KnxStatistic> {
    public static final KnxStatisticJsonSerializer INSTANCE = new KnxStatisticJsonSerializer();

    private KnxStatisticJsonSerializer() {
        // private-constructor
    }

    @Override
    public JsonElement serialize(final KnxStatistic src, final Type typeOfSrc, final JsonSerializationContext context) {
        final var jsonStatistic = new JsonObject();

        // received
        final var inboundTotal = new JsonObject();
        inboundTotal.addProperty("packets", src.getNumberOfBodyReceived());
        inboundTotal.addProperty("bytes", src.getNumberOfBytesReceived());

        final var inbound = new JsonObject();
        inbound.add("total", inboundTotal);
        final Function<Class<? extends Body>, Long> receivedFunction = src::getNumberOfBodyReceived;
        inbound.add("search", getRequestResponsePair(receivedFunction, SearchRequestBody.class, SearchResponseBody.class));
        inbound.add("description", getRequestResponsePair(receivedFunction, DescriptionRequestBody.class, DescriptionResponseBody.class));
        inbound.add("connect", getRequestResponsePair(receivedFunction, ConnectRequestBody.class, ConnectResponseBody.class));
        inbound.add("connection_state", getRequestResponsePair(receivedFunction, ConnectionStateRequestBody.class, ConnectionStateResponseBody.class));
        inbound.add("tunneling", getRequestResponsePair(receivedFunction, TunnelingRequestBody.class, TunnelingAckBody.class));
        inbound.add("indication", getRequestResponsePair(receivedFunction, null, RoutingIndicationBody.class));
        inbound.add("disconnect", getRequestResponsePair(receivedFunction, DisconnectRequestBody.class, DisconnectResponseBody.class));

        // sent
        final var outboundTotal = new JsonObject();
        outboundTotal.addProperty("packets", src.getNumberOfBodySent());
        outboundTotal.addProperty("bytes", src.getNumberOfBytesSent());

        final var outbound = new JsonObject();
        outbound.add("total", outboundTotal);
        final Function<Class<? extends Body>, Long> sentFunction = src::getNumberOfBodySent;
        outbound.add("search", getRequestResponsePair(sentFunction, SearchRequestBody.class, SearchResponseBody.class));
        outbound.add("description", getRequestResponsePair(sentFunction, DescriptionRequestBody.class, DescriptionResponseBody.class));
        outbound.add("connect", getRequestResponsePair(sentFunction, ConnectRequestBody.class, ConnectResponseBody.class));
        outbound.add("connection_state", getRequestResponsePair(sentFunction, ConnectionStateRequestBody.class, ConnectionStateResponseBody.class));
        outbound.add("tunneling", getRequestResponsePair(sentFunction, TunnelingRequestBody.class, TunnelingAckBody.class));
        outbound.add("indication", getRequestResponsePair(sentFunction, RoutingIndicationBody.class, null));
        outbound.add("disconnect", getRequestResponsePair(sentFunction, DisconnectRequestBody.class, DisconnectResponseBody.class));

        // error
        final var errorTotal = new JsonObject();
        errorTotal.addProperty("packets", src.getNumberOfErrors());
        errorTotal.addProperty("rate", src.getErrorRate());

        final var error = new JsonObject();
        error.add("total", errorTotal);

        // add to json statistic
        jsonStatistic.add("inbound", inbound);
        jsonStatistic.add("outbound", outbound);
        jsonStatistic.add("error", error);

        return jsonStatistic;
    }

    /**
     * Returns a JSON pair of request and response
     *
     * @param function          function to obtain body related packet number
     * @param requestBodyClass  class of request body
     * @param responseBodyClass class or response body
     * @return json pair of request and response
     */
    private JsonObject getRequestResponsePair(
            final Function<Class<? extends Body>, Long> function,
            final @Nullable Class<? extends RequestBody> requestBodyClass,
            final @Nullable Class<? extends ResponseBody> responseBodyClass) {

        final var pair = new JsonObject();
        pair.addProperty("request", requestBodyClass == null ? 0L : function.apply(requestBodyClass));
        pair.addProperty("response", responseBodyClass == null ? 0L : function.apply(responseBodyClass));
        return pair;
    }
}
