package li.pitschmann.knx.daemon.v1.json;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.datapoint.DataPointType;
import li.pitschmann.utils.ByteFormatter;

import java.util.Arrays;

/**
 * JSON for HTTP Write Request to KNX Daemon
 */
public final class WriteRequest {
    private GroupAddress groupAddress;
    private DataPointType<?> dataPointType;
    private String[] values;
    private byte[] raw;

    public GroupAddress getGroupAddress() {
        return groupAddress;
    }

    public void setGroupAddress(GroupAddress groupAddress) {
        this.groupAddress = groupAddress;
    }

    public DataPointType<?> getDataPointType() {
        return dataPointType;
    }

    public void setDataPointType(DataPointType<?> dataPointType) {
        this.dataPointType = dataPointType;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String... values) {
        this.values = values;
    }

    public byte[] getRaw() {
        return raw;
    }

    public void setRaw(byte[] raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("groupAddress", groupAddress) //
                .add("dataPointType", dataPointType) //
                .add("values", Arrays.toString(values)) //
                .add("raw", ByteFormatter.formatHexAsString(raw)) //
                .toString();
    }
}
