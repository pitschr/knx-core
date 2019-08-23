package li.pitschmann.knx.daemon.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.body.address.KnxAddress;
import li.pitschmann.knx.link.communication.KnxStatistic;
import li.pitschmann.knx.link.datapoint.DataPointType;
import li.pitschmann.knx.parser.XmlGroupAddress;
import li.pitschmann.knx.parser.XmlGroupRange;
import ro.pippo.gson.GsonEngine;

import java.time.Instant;

/**
 * A customized {@link GsonEngine} with that adds needs for KNX Daemon
 */
public final class DaemonGsonEngine extends GsonEngine {
    public static final DaemonGsonEngine INSTANCE = new DaemonGsonEngine();
    private final Gson gson;

    private DaemonGsonEngine() {
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
