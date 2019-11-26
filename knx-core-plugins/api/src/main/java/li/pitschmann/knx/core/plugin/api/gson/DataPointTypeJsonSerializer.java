package li.pitschmann.knx.core.plugin.api.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import li.pitschmann.knx.core.datapoint.DataPointType;

import java.lang.reflect.Type;

/**
 * Serializes the {@link DataPointType} to a JSON format using Gson
 */
public final class DataPointTypeJsonSerializer implements JsonSerializer<DataPointType<?>> {
    public static final DataPointTypeJsonSerializer INSTANCE = new DataPointTypeJsonSerializer();

    private DataPointTypeJsonSerializer() {
        // private-constructor
    }

    @Override
    public JsonElement serialize(final DataPointType<?> dataPointType, final Type type, final JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dataPointType.getId());
    }
}
