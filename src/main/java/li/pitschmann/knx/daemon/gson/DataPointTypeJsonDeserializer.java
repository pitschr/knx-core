package li.pitschmann.knx.daemon.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import li.pitschmann.knx.link.datapoint.DataPointType;
import li.pitschmann.knx.link.datapoint.DataPointTypeRegistry;

import java.lang.reflect.Type;

/**
 * De-Serializes the {@link DataPointType} to a JSON format using Gson
 */
public final class DataPointTypeJsonDeserializer implements JsonDeserializer<DataPointType<?>> {
    @Override
    public DataPointType<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return DataPointTypeRegistry.getDataPointType(jsonElement.getAsString());
    }
}
