package li.pitschmann.knx.core.plugin.api.v1.json;

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.datapoint.DataPointType;

/**
 * JSON for HTTP Write Request
 */
public final class WriteRequest {
    private GroupAddress groupAddress;
    private DataPointType<?> dataPointType;
    private String[] values;
    private byte[] raw;

    public GroupAddress getGroupAddress() {
        return groupAddress;
    }

    public void setGroupAddress(final GroupAddress groupAddress) {
        this.groupAddress = groupAddress;
    }

    public DataPointType<?> getDataPointType() {
        return dataPointType;
    }

    public void setDataPointType(final DataPointType<?> dataPointType) {
        this.dataPointType = dataPointType;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(final String... values) {
        this.values = values;
    }

    public byte[] getRaw() {
        return raw;
    }

    public void setRaw(byte[] raw) {
        this.raw = raw;
    }
}
