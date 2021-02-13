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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import li.pitschmann.knx.core.address.KnxAddress;

import java.lang.reflect.Type;

/**
 * Serializes the {@link KnxAddress} to a JSON format
 */
public abstract class AbstractKnxAddressJsonSerializer<T extends KnxAddress> implements JsonSerializer<T> {

    @Override
    public JsonElement serialize(final T address, final Type type, final JsonSerializationContext jsonSerializationContext) {
        final var jsonObject = new JsonObject();
        // address type
        jsonObject.addProperty("type", address.getAddressType().getCode());

        // address in human format
        jsonObject.add("format", createAddressJsonElement(address));

        // raw data
        final var jsonArray = new JsonArray();
        for (final byte b : address.toByteArray()) {
            jsonArray.add(new JsonPrimitive(b));
        }
        jsonObject.add("raw", jsonArray);
        return jsonObject;
    }

    /**
     * Creates a JSON element for given address
     *
     * @param address address to be converted to JSON element
     * @return an instance of {@link JsonElement}
     */
    protected abstract JsonElement createAddressJsonElement(final T address);
}
