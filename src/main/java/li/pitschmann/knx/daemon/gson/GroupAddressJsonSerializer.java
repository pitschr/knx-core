package li.pitschmann.knx.daemon.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import li.pitschmann.knx.link.body.address.GroupAddress;

import java.lang.reflect.Type;

/**
 * Serializes the {@link GroupAddress} to a JSON format using Gson
 */
public final class GroupAddressJsonSerializer implements JsonSerializer<GroupAddress> {
    @Override
    public JsonElement serialize(GroupAddress groupAddress, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(groupAddress.getAddress());
    }
}