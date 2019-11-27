package li.pitschmann.knx.core.plugin.api.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
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
import li.pitschmann.knx.core.body.SearchRequestBody;
import li.pitschmann.knx.core.body.SearchResponseBody;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.communication.KnxStatistic;

import javax.annotation.Nonnull;
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
        inbound.add("disconnect", getRequestResponsePair(receivedFunction, DisconnectRequestBody.class, DisconnectResponseBody.class));
        inbound.add("tunneling", getRequestResponsePair(receivedFunction, TunnelingRequestBody.class, TunnelingAckBody.class));

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
        outbound.add("disconnect", getRequestResponsePair(sentFunction, DisconnectRequestBody.class, DisconnectResponseBody.class));
        outbound.add("tunneling", getRequestResponsePair(sentFunction, TunnelingRequestBody.class, TunnelingAckBody.class));

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
     * @param function
     * @param requestBodyClass
     * @param responseBodyClass
     * @return json pair of request and response
     */
    private JsonObject getRequestResponsePair(
            final @Nonnull Function<Class<? extends Body>, Long> function,
            final @Nonnull Class<? extends RequestBody> requestBodyClass,
            final @Nonnull Class<? extends ResponseBody> responseBodyClass) {

        final var pair = new JsonObject();
        pair.addProperty("request", function.apply(requestBodyClass));
        pair.addProperty("response", function.apply(responseBodyClass));
        return pair;
    }
}