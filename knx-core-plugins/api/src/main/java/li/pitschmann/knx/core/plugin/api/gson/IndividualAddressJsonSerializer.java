package li.pitschmann.knx.core.plugin.api.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import li.pitschmann.knx.core.address.IndividualAddress;

/**
 * Serializes the {@link IndividualAddress} to a JSON format
 */
public final class IndividualAddressJsonSerializer extends AbstractKnxAddressJsonSerializer<IndividualAddress> {
    public static final IndividualAddressJsonSerializer INSTANCE = new IndividualAddressJsonSerializer();

    private IndividualAddressJsonSerializer() {
        // private-constructor
    }

    @Override
    protected JsonElement createAddressJsonElement(final IndividualAddress address) {
        return new JsonPrimitive(address.getAddress());
    }
}
