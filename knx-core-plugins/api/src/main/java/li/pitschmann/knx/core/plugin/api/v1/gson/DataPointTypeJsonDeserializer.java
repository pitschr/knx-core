/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2021 Pitschmann Christoph
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package li.pitschmann.knx.core.plugin.api.v1.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import li.pitschmann.knx.core.datapoint.DataPointRegistry;
import li.pitschmann.knx.core.datapoint.DataPointType;
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
