package li.pitschmann.knx.daemon.gson;

import li.pitschmann.knx.daemon.json.ReadRequest;
import li.pitschmann.knx.daemon.json.WriteRequest;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.datapoint.DPT1;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DaemonGsonEngine}
 */
public class DaemonGsonEngineTest {
    /**
     * Test {@link ReadRequest} serialization and de-serialization using Gson
     */
    @Test
    public void readRequestJsonTest() {
        final var readRequestObject = new ReadRequest();
        readRequestObject.setGroupAddress(GroupAddress.of(0, 0, 59));

        final var jsonLevelFree = "{\"groupAddress\":\"59\"}";
        final var json = DaemonGsonEngine.INSTANCE.toString(readRequestObject);
        assertThat(json).isEqualTo(jsonLevelFree);
        assertThat(DaemonGsonEngine.INSTANCE.fromString(jsonLevelFree, ReadRequest.class)).hasToString(readRequestObject.toString());

        final var jsonLevel2 = "{\"groupAddress\":\"0/59\"}";
        assertThat(DaemonGsonEngine.INSTANCE.fromString(jsonLevel2, ReadRequest.class)).hasToString(readRequestObject.toString());

        final var jsonLevel3 = "{\"groupAddress\":\"0/0/59\"}";
        assertThat(DaemonGsonEngine.INSTANCE.fromString(jsonLevel3, ReadRequest.class)).hasToString(readRequestObject.toString());
    }

    /**
     * Test {@link WriteRequest} serialization and de-serialization using Gson
     */
    @Test
    public void writeRequestJsonTest() {
        final var writeRequestObject = new WriteRequest();
        writeRequestObject.setGroupAddress(GroupAddress.of(0, 0, 59));
        writeRequestObject.setDataPointType(DPT1.SWITCH);  // TODO: DPT23.xyz not working because of DPTEnum - refactor?
        writeRequestObject.setValues("one", "two", "three");
        writeRequestObject.setRaw(new byte[]{0x74});

        // 1.001
        final var json = "{\"groupAddress\":\"59\",\"dataPointType\":\"1.001\",\"values\":[\"one\",\"two\",\"three\"],\"raw\":[116]}";
        assertThat(json).isEqualTo(DaemonGsonEngine.INSTANCE.toString(writeRequestObject));
        assertThat(DaemonGsonEngine.INSTANCE.fromString(json, WriteRequest.class)).hasToString(writeRequestObject.toString());

        // DPT-1
        final var jsonDpt = "{\"groupAddress\":\"59\",\"dataPointType\":\"DPT-1\",\"values\":[\"one\",\"two\",\"three\"],\"raw\":[116]}";
        assertThat(DaemonGsonEngine.INSTANCE.fromString(jsonDpt, WriteRequest.class)).hasToString(writeRequestObject.toString());

        // DPST-1-1
        final var jsonDpst = "{\"groupAddress\":\"59\",\"dataPointType\":\"DPST-1-1\",\"values\":[\"one\",\"two\",\"three\"],\"raw\":[116]}";
        assertThat(DaemonGsonEngine.INSTANCE.fromString(jsonDpst, WriteRequest.class)).hasToString(writeRequestObject.toString());
    }
}
