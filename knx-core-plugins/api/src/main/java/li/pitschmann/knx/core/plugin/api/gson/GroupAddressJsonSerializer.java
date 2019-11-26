package li.pitschmann.knx.core.plugin.api.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import li.pitschmann.knx.core.body.address.GroupAddress;

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
        jsonObject.addProperty("free_level", address.getAddress());
        jsonObject.addProperty("two_level", address.getAddressLevel2());
        jsonObject.addProperty("three_level", address.getAddressLevel3());
        return jsonObject;
    }
}
