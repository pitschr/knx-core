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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.address.KnxAddress;
import li.pitschmann.knx.core.communication.KnxStatistic;
import li.pitschmann.knx.core.datapoint.DataPointType;
import li.pitschmann.knx.core.knxproj.XmlGroupAddress;
import li.pitschmann.knx.core.knxproj.XmlGroupRange;

import java.time.Instant;

/**
 * A customized {@link Gson} with that adds needs for web server
 */
public final class ApiGsonEngine {
    public static final ApiGsonEngine INSTANCE = new ApiGsonEngine();
    private final Gson gson;

    private ApiGsonEngine() {
        gson = new GsonBuilder()
                .disableHtmlEscaping()
                // serializers
                .registerTypeAdapter(Instant.class, InstantJsonSerializer.INSTANCE)
                .registerTypeAdapter(DataPointType.class, DataPointTypeJsonSerializer.INSTANCE)
                .registerTypeAdapter(GroupAddress.class, GroupAddressJsonSerializer.INSTANCE)
                .registerTypeAdapter(IndividualAddress.class, IndividualAddressJsonSerializer.INSTANCE)
                .registerTypeAdapter(XmlGroupAddress.class, XmlGroupAddressJsonSerializer.INSTANCE)
                .registerTypeAdapter(XmlGroupRange.class, XmlGroupRangeJsonSerializer.INSTANCE)
                .registerTypeHierarchyAdapter(KnxStatistic.class, KnxStatisticJsonSerializer.INSTANCE)
                // de-serializers
                .registerTypeAdapter(DataPointType.class, DataPointTypeJsonDeserializer.INSTANCE)
                .registerTypeAdapter(GroupAddress.class, GroupAddressJsonDeserializer.INSTANCE)
                .registerTypeAdapter(IndividualAddress.class, IndividualAddressJsonDeserializer.INSTANCE)
                .registerTypeAdapter(KnxAddress.class, KnxAddressJsonDeserializer.INSTANCE)
                .create();
    }

    public Gson getGson() {
        return gson;
    }
}
