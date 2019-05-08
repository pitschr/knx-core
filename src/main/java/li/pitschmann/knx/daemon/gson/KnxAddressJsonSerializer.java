package li.pitschmann.knx.daemon.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import li.pitschmann.knx.link.body.address.KnxAddress;

import java.lang.reflect.Type;

/**
 * Serializes the {@link KnxAddress} to a JSON format
 */
public abstract class KnxAddressJsonSerializer<T extends KnxAddress> implements JsonSerializer<T> {

    @Override
    public JsonElement serialize(T address, Type type, JsonSerializationContext jsonSerializationContext) {
        final var jsonObject = new JsonObject();
        // address type
        jsonObject.add("type", new JsonPrimitive(address.getAddressType().getCode()));

        // address in human format
        jsonObject.add("format", createAddressJsonElement(address));

        // raw data
        final var jsonArray = new JsonArray();
        for (final byte b : address.getRawData()) {
            jsonArray.add(new JsonPrimitive(b));
        }
        jsonObject.add("raw", jsonArray);
        return jsonObject;
    }

    /**
     * Creates a JSON Element for given address
     *
     * @param address
     * @return an instance of {@link JsonElement}
     */
    protected abstract JsonElement createAddressJsonElement(final T address);
}