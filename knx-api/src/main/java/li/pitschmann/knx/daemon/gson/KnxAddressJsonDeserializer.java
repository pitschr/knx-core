package li.pitschmann.knx.daemon.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import li.pitschmann.knx.link.body.address.AddressType;
import li.pitschmann.knx.link.body.address.KnxAddress;

import java.lang.reflect.Type;

/**
 * De-Serializes a JSON format of knx address to a sub-type of {@link KnxAddress}
 *
 * <code>
 * {"type":1,"address":[0,22]}
 * </code>
 */
public class KnxAddressJsonDeserializer implements JsonDeserializer<KnxAddress> {
    private static final GroupAddressJsonDeserializer GROUP_ADDRESS_DESERIALIZER = new GroupAddressJsonDeserializer();
    private static final IndividualAddressJsonDeserializer INDIVIDUAL_ADDRESS_DESERIALIZER = new IndividualAddressJsonDeserializer();

    @Override
    public KnxAddress deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        // is json element a json object
        if (jsonElement.isJsonObject()) {
            final var jsonObject = (JsonObject) jsonElement;

            // get address type
            final var addressType = jsonObject.getAsJsonPrimitive("type").getAsInt();

            // deserialize based on address type
            if (addressType == AddressType.GROUP.getCode()) {
                return GROUP_ADDRESS_DESERIALIZER.deserialize(jsonElement, type, jsonDeserializationContext);
            } else if (addressType == AddressType.INDIVIDUAL.getCode()) {
                return INDIVIDUAL_ADDRESS_DESERIALIZER.deserialize(jsonElement, type, jsonDeserializationContext);
            }
        }
        throw new UnsupportedOperationException("Given JSON is not supported: " + jsonElement);
    }
}