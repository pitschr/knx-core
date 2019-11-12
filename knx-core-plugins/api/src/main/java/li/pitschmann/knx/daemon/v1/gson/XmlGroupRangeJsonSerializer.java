package li.pitschmann.knx.daemon.v1.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import li.pitschmann.knx.parser.XmlGroupRange;

import java.lang.reflect.Type;

/**
 * Serializes the {@link XmlGroupRange} to a JSON format
 */
public final class XmlGroupRangeJsonSerializer implements JsonSerializer<XmlGroupRange> {
    public static final XmlGroupRangeJsonSerializer INSTANCE = new XmlGroupRangeJsonSerializer();

    private XmlGroupRangeJsonSerializer() {
        // private-constructor
    }

    @Override
    public JsonElement serialize(final XmlGroupRange src, final Type typeOfSrc, final JsonSerializationContext context) {
        final var json = new JsonObject();

        json.addProperty("id", src.getId());
        json.addProperty("name", src.getName());
        json.addProperty("level", src.getLevel());

        final var jsonRange = new JsonObject();
        jsonRange.addProperty("start", src.getRangeStart());
        jsonRange.addProperty("end", src.getRangeEnd());
        json.add("range", jsonRange);

        return json;
    }
}
