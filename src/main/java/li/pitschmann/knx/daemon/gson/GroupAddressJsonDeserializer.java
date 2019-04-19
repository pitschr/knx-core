package li.pitschmann.knx.daemon.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import li.pitschmann.knx.link.body.address.GroupAddress;

import java.lang.reflect.Type;

/**
 * De-Serializes the {@link GroupAddress} to a JSON format using Gson
 */
public final class GroupAddressJsonDeserializer implements JsonDeserializer<GroupAddress> {
    @Override
    public GroupAddress deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return GroupAddress.of(jsonElement.getAsString());
    }
}