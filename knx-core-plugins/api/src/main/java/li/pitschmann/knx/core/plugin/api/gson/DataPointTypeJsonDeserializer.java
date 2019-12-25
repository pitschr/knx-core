package li.pitschmann.knx.core.plugin.api.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import li.pitschmann.knx.core.datapoint.DataPointType;
import li.pitschmann.knx.core.datapoint.DataPointRegistry;
import li.pitschmann.knx.core.utils.Preconditions;

import java.lang.reflect.Type;

/**
 * De-Serializes the {@link DataPointType} to a JSON format using Gson
 */
public final class DataPointTypeJsonDeserializer implements JsonDeserializer<DataPointType> {
    public static final DataPointTypeJsonDeserializer INSTANCE = new DataPointTypeJsonDeserializer();

    private DataPointTypeJsonDeserializer() {
        // private constructor
    }

    @Override
    public DataPointType deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) {
        Preconditions.checkArgument(jsonElement.isJsonPrimitive(),
                "Expected JsonElement should be a JsonPrimitive, but I got: {}", jsonElement);

        return DataPointRegistry.getDataPointType(jsonElement.getAsString());
    }
}
