package li.pitschmann.knx.daemon.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.datapoint.DataPointType;
import ro.pippo.gson.GsonEngine;

/**
 * A customized {@link GsonEngine} with that adds needs for KNX Daemon
 */
public class DaemonGsonEngine extends GsonEngine {
    public static final DaemonGsonEngine INSTANCE = new DaemonGsonEngine();
    private final Gson gson;

    private DaemonGsonEngine() {
        gson = new GsonBuilder()
                .registerTypeAdapter(GroupAddress.class, new GroupAddressJsonSerializer())
                .registerTypeAdapter(GroupAddress.class, new GroupAddressJsonDeserializer())
                .registerTypeAdapter(DataPointType.class, new DataPointTypeJsonSerializer())
                .registerTypeAdapter(DataPointType.class, new DataPointTypeJsonDeserializer())
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
