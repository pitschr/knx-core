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
import li.pitschmann.knx.core.knxproj.XmlGroupRange;

import java.lang.reflect.Type;

/**
 * Serializes the {@link XmlGroupRange} to a JSON format
 */
public final class XmlGroupRangeJsonSerializer implements JsonSerializer<XmlGroupRange> {
    public static final XmlGroupRangeJsonSerializer INSTANCE = new XmlGroupRangeJsonSerializer();

    private XmlGroupRangeJsonSerializer() {
        // private-constructor
    }

    @Override
    public JsonElement serialize(final XmlGroupRange src, final Type typeOfSrc, final JsonSerializationContext context) {
        final var json = new JsonObject();

        json.addProperty("id", src.getId());
        json.addProperty("name", src.getName());
        json.addProperty("level", src.getLevel());

        final var jsonRange = new JsonObject();
        jsonRange.addProperty("start", src.getRangeStart());
        jsonRange.addProperty("end", src.getRangeEnd());
        json.add("range", jsonRange);

        return json;
    }
}
