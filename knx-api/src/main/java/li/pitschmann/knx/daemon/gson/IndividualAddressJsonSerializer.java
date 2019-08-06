package li.pitschmann.knx.daemon.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import li.pitschmann.knx.link.body.address.IndividualAddress;

/**
 * Serializes the {@link IndividualAddress} to a JSON format
 */
public final class IndividualAddressJsonSerializer extends AbstractKnxAddressJsonSerializer<IndividualAddress> {
    @Override
    protected JsonElement createAddressJsonElement(final IndividualAddress address) {
        return new JsonPrimitive(address.getAddress());
    }
}