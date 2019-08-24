package li.pitschmann.knx.daemon.v1.gson;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import li.pitschmann.knx.link.body.address.AddressType;
import li.pitschmann.knx.link.body.address.KnxAddress;

import java.lang.reflect.Type;

/**
 * De-Serializes a JSON format of knx address to an instance of {@link KnxAddress}
 *
 * <code>
 * {"type":1,"address":[0,22]}
 * </code>
 */
public abstract class AbstractKnxAddressJsonDeserializer<T extends KnxAddress> implements JsonDeserializer<T> {
    @Override
    public T deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) {
        // is json element a json object
        if (jsonElement.isJsonObject()) {
            final var jsonObject = (JsonObject) jsonElement;

            // validate
            Preconditions.checkArgument(jsonObject.getAsJsonPrimitive("type").getAsInt() == supportedAddressType().getCode(),
                    "The type of KnxAddress format is not supported!");

            // convert
            return convert(convertJsonArrayToByteArray(jsonObject.getAsJsonArray("raw")));
        }
        // or a array?
        else if (jsonElement.isJsonArray()) {
            return convert(convertJsonArrayToByteArray(jsonElement.getAsJsonArray()));
        }
        // or a primitive?
        else if (jsonElement.isJsonPrimitive()) {
            final var jsonPrimitive = jsonElement.getAsJsonPrimitive();
            // if it is a string or an integer we may try to convert it
            if (jsonPrimitive.isString()) {
                return convert(jsonPrimitive.getAsString());
            } else if (jsonPrimitive.isNumber()) {
                return convert(String.valueOf(jsonPrimitive.getAsInt()));
            }
        }
        // otherwise give up...
        throw new UnsupportedOperationException("Given JSON format is not supported: " + jsonElement);
    }

    private byte[] convertJsonArrayToByteArray(final JsonArray jsonArray) {
        final var array = new byte[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            array[i] = jsonArray.get(i).getAsByte();
        }
        return array;
    }

    /**
     * Returns the AddressType that is supported for this de-serialization
     *
     * @return an instance of {@link AddressType}
     */
    protected abstract AddressType supportedAddressType();

    /**
     * Creates a new instance of given {@code byte} array
     *
     * @param address
     * @return an instance of KnxAddress (Individual or Group Address)
     */
    protected abstract T convert(final byte[] address);

    /**
     * Creates a new instance of given {@code String}
     *
     * @param address
     * @return address instance of KnxAddress (Individual or Group Address)
     */
    protected abstract T convert(final String address);
}
