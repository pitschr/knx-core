package li.pitschmann.knx.daemon.v1.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import li.pitschmann.knx.link.body.address.AddressType;
import li.pitschmann.knx.link.body.address.KnxAddress;
import li.pitschmann.knx.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * De-Serializes a JSON format of knx address to a sub-type of {@link KnxAddress}
 * <p/>
 * Currently supported are {@link AddressType#INDIVIDUAL} and {@link AddressType#GROUP} which will
 * call either {@link IndividualAddressJsonDeserializer} or {@link GroupAddressJsonDeserializer}, respectively.
 *
 * <code>
 * {"type":1, ... }
 * </code>
 */
public final class KnxAddressJsonDeserializer implements JsonDeserializer<KnxAddress> {
    public static final KnxAddressJsonDeserializer INSTANCE = new KnxAddressJsonDeserializer();
    private static final Logger log = LoggerFactory.getLogger(KnxAddressJsonDeserializer.class);

    public KnxAddressJsonDeserializer() {
        // private constructor
    }

    @Override
    public KnxAddress deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) {
        Preconditions.checkArgument(jsonElement.isJsonObject(), "JSON Element should be a JsonObject.");

        final var jsonObject = (JsonObject) jsonElement;

        // get address type
        final var addressType = jsonObject.getAsJsonPrimitive("type").getAsInt();
        log.debug("Address Type is: {}", addressType);

        // deserialize based on address type
        if (addressType == AddressType.GROUP.getCode()) {
            return GroupAddressJsonDeserializer.INSTANCE.deserialize(jsonElement, type, jsonDeserializationContext);
        } else if (addressType == AddressType.INDIVIDUAL.getCode()) {
            return IndividualAddressJsonDeserializer.INSTANCE.deserialize(jsonElement, type, jsonDeserializationContext);
        }

        throw new UnsupportedOperationException("Given JSON type is not supported: " + jsonElement);
    }
}
