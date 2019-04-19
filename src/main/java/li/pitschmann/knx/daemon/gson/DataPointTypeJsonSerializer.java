package li.pitschmann.knx.daemon.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import li.pitschmann.knx.link.datapoint.DataPointType;

import java.lang.reflect.Type;

/**
 * Serializes the {@link DataPointType} to a JSON format using Gson
 */
public final class DataPointTypeJsonSerializer implements JsonSerializer<DataPointType<?>> {
    @Override
    public JsonElement serialize(DataPointType<?> dataPointType, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dataPointType.getId());
    }
}