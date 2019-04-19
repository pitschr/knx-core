package li.pitschmann.knx.daemon.json;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.datapoint.DataPointType;
import li.pitschmann.utils.ByteFormatter;

/**
 * JSON read response
 */
public class ReadResponse extends AbstractResponse {
    private DataPointType<?> dataPointType;
    private byte[] raw;

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
                .add("dataPointType", dataPointType) //
                .add("raw", ByteFormatter.formatHexAsString(raw)) //
                .toString();
    }
}
