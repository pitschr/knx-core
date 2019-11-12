package li.pitschmann.knx.daemon.v1.json;

import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.datapoint.DataPointType;

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