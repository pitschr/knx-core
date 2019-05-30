package li.pitschmann.knx.daemon.v1.json;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.datapoint.DataPointType;
import li.pitschmann.utils.ByteFormatter;

/**
 * JSON read response
 */
public class ReadResponse extends AbstractResponse {
    private String name;
    private String description;
    private DataPointType<?> dataPointType;
    private byte[] raw;

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

    public DataPointType<?> getDataPointType() {
        return dataPointType;
    }

    public void setDataPointType(DataPointType<?> dataPointType) {
        this.dataPointType = dataPointType;
    }

    public byte[] getRaw() {
        return raw;
    }

    public void setRaw(byte[] raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name) //
                .add("description", description) //
                .add("dataPointType", dataPointType) //
                .add("raw", ByteFormatter.formatHexAsString(raw)) //
                .toString();
    }
}
