package li.pitschmann.knx.core.plugin.api.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import li.pitschmann.knx.core.address.KnxAddress;

import java.lang.reflect.Type;

/**
 * Serializes the {@link KnxAddress} to a JSON format
 */
public abstract class AbstractKnxAddressJsonSerializer<T extends KnxAddress> implements JsonSerializer<T> {

    @Override
    public JsonElement serialize(final T address, final Type type, final JsonSerializationContext jsonSerializationContext) {
        final var jsonObject = new JsonObject();
        // address type
        jsonObject.addProperty("type", address.getAddressType().getCode());

        // address in human format
        jsonObject.add("format", createAddressJsonElement(address));

        // raw data
        final var jsonArray = new JsonArray();
        for (final byte b : address.toByteArray()) {
            jsonArray.add(new JsonPrimitive(b));
        }
        jsonObject.add("raw", jsonArray);
        return jsonObject;
    }

    /**
     * Creates a JSON element for given address
     *
     * @param address address to be converted to JSON element
     * @return an instance of {@link JsonElement}
     */
    protected abstract JsonElement createAddressJsonElement(final T address);
}
