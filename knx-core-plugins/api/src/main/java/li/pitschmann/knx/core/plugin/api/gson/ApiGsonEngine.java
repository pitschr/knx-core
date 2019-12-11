package li.pitschmann.knx.core.plugin.api.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.address.KnxAddress;
import li.pitschmann.knx.core.communication.KnxStatistic;
import li.pitschmann.knx.core.datapoint.DataPointType;
import li.pitschmann.knx.core.knxproj.XmlGroupAddress;
import li.pitschmann.knx.core.knxproj.XmlGroupRange;
import ro.pippo.gson.GsonEngine;

import java.time.Instant;

/**
 * A customized {@link GsonEngine} with that adds needs for web server
 */
public final class ApiGsonEngine extends GsonEngine {
    public static final ApiGsonEngine INSTANCE = new ApiGsonEngine();
    private final Gson gson;

    private ApiGsonEngine() {
        gson = new GsonBuilder()
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

    @Override
    public String toString(Object object) {
        return gson.toJson(object);
    }

    @Override
    public <T> T fromString(String content, Class<T> classOfT) {
        return gson.fromJson(content, classOfT);
    }
}
