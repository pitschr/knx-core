package li.pitschmann.knx.daemon.v1.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;

/**
 * Serializes the {@link Instant} to a JSON format
 */
public final class InstantJsonSerializer implements JsonSerializer<Instant> {
    public static final InstantJsonSerializer INSTANCE = new InstantJsonSerializer();

    private InstantJsonSerializer() {
        // private-constructor
    }

    @Override
    public JsonElement serialize(final Instant src, final Type typeOfSrc, final JsonSerializationContext context) {
        final var json = new JsonObject();

        json.addProperty("seconds", src.getEpochSecond());
        json.addProperty("nanos", src.getNano());

        return json;
    }
}
