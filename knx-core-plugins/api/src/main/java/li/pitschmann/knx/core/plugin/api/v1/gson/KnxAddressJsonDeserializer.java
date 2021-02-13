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
import com.google.gson.JsonObject;
import li.pitschmann.knx.core.address.AddressType;
import li.pitschmann.knx.core.address.KnxAddress;
import li.pitschmann.knx.core.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * De-Serializes a JSON format of knx address to a sub-type of {@link KnxAddress}
 * <p>
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
