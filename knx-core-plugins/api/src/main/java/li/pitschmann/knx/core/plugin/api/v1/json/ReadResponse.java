package li.pitschmann.knx.core.plugin.api.v1.json;

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.datapoint.DataPointType;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

/**
 * JSON read response
 */
public class ReadResponse {
    private GroupAddress groupAddress;
    private String name;
    private String description;
    private DataPointType dataPointType;
    private String unit;
    private String value;
    private byte[] raw;

    public GroupAddress getGroupAddress() {
        return groupAddress;
    }

    public void setGroupAddress(GroupAddress groupAddress) {
        this.groupAddress = groupAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DataPointType getDataPointType() {
        return dataPointType;
    }

    public void setDataPointType(DataPointType dataPointType) {
        this.dataPointType = dataPointType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public byte[] getRaw() {
        return raw;
    }

    public void setRaw(byte[] raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("name", name) //
                .add("description", description) //
                .add("dataPointType", dataPointType) //
                .add("raw", ByteFormatter.formatHexAsString(raw)) //
                .toString();
    }
}
