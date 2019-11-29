package li.pitschmann.knx.core.plugin.api.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import li.pitschmann.knx.core.body.address.GroupAddress;
import li.pitschmann.knx.core.datapoint.DataPointTypeRegistry;
import li.pitschmann.knx.core.parser.XmlGroupAddress;
import li.pitschmann.knx.core.utils.Strings;

import java.lang.reflect.Type;

/**
 * Serializes the {@link XmlGroupAddress} to a JSON format
 */
public final class XmlGroupAddressJsonSerializer implements JsonSerializer<XmlGroupAddress> {
    public static final XmlGroupAddressJsonSerializer INSTANCE = new XmlGroupAddressJsonSerializer();

    private XmlGroupAddressJsonSerializer() {
        // private-constructor
    }

    @Override
    public JsonElement serialize(final XmlGroupAddress src, final Type typeOfSrc, final JsonSerializationContext context) {
        final var json = new JsonObject();

        json.addProperty("id", src.getId());
        json.addProperty("name", src.getName());
        json.addProperty("description", src.getDescription());

        final var groupAddress = GroupAddress.of(src.getAddress());
        final var groupAddressJson = GroupAddressJsonSerializer.INSTANCE.serialize(groupAddress, typeOfSrc, context);
        json.add("address", groupAddressJson);

        final var dataPointTypeStr = src.getDataPointType();
        if (!Strings.isNullOrEmpty(dataPointTypeStr)) {
            final var dpt = DataPointTypeRegistry.getDataPointType(dataPointTypeStr);
            final var dptJson = DataPointTypeJsonSerializer.INSTANCE.serialize(dpt, typeOfSrc, context);
            json.add("dataPointType", dptJson);
        }

        return json;
    }
}
