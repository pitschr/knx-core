package li.pitschmann.knx.daemon.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import li.pitschmann.knx.link.body.address.GroupAddress;

/**
 * Serializes the {@link GroupAddress} to a JSON format
 */
public final class GroupAddressJsonSerializer extends AbstractKnxAddressJsonSerializer<GroupAddress> {
    public static final GroupAddressJsonSerializer INSTANCE = new GroupAddressJsonSerializer();

    private GroupAddressJsonSerializer() {
        // private-constructor
    }

    @Override
    protected JsonElement createAddressJsonElement(final GroupAddress address) {
        final var jsonObject = new JsonObject();
        jsonObject.add("free_level", new JsonPrimitive(address.getAddress()));
        jsonObject.add("two_level", new JsonPrimitive(address.getAddressLevel2()));
        jsonObject.add("three_level", new JsonPrimitive(address.getAddressLevel3()));
        return jsonObject;
    }
}
