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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;

/**
 * Serializes the {@link Instant} to a JSON format
 */
public final class InstantJsonSerializer implements JsonSerializer<Instant> {
    public static final InstantJsonSerializer INSTANCE = new InstantJsonSerializer();

    private InstantJsonSerializer() {
        // private-constructor
    }

    @Override
    public JsonElement serialize(final Instant src, final Type typeOfSrc, final JsonSerializationContext context) {
        final var json = new JsonObject();

        json.addProperty("seconds", src.getEpochSecond());
        json.addProperty("nanos", src.getNano());

        return json;
    }
}
