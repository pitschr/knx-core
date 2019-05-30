package li.pitschmann.knx.daemon.gson;

import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.address.IndividualAddress;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DaemonGsonEngine}
 */
public class DaemonGsonEngineTest {

    /**
     * Test {@link GroupAddress} serialization and de-serialization using Daemon Gson
     */
    @Test
    public void groupAddressJson() {
        final var address = GroupAddress.of(1, 7, 59);
        // serialize
        final var json = DaemonGsonEngine.INSTANCE.toString(address);
        // @formatter:off
        assertThat(json).isEqualTo(
                "{" +
                    "\"type\":1," +
                    "\"format\":{" +
                        "\"free_level\":\"3899\"," +
                        "\"two_level\":\"1/1851\"," +
                        "\"three_level\":\"1/7/59\"" +
                    "}," +
                    "\"raw\":[15,59]" +
                "}");
        // @formatter:on

        // deserialize
        assertThat(DaemonGsonEngine.INSTANCE.fromString(json, address.getClass())).isEqualTo(address);
        assertThat(DaemonGsonEngine.INSTANCE.fromString("{\"type\":1,\"raw\":[15,59]}", address.getClass())).isEqualTo(address);
        assertThat(DaemonGsonEngine.INSTANCE.fromString("[15,59]", address.getClass())).isEqualTo(address);
        assertThat(DaemonGsonEngine.INSTANCE.fromString("3899", address.getClass())).isEqualTo(address);
        assertThat(DaemonGsonEngine.INSTANCE.fromString("\"3899\"", address.getClass())).isEqualTo(address);
        assertThat(DaemonGsonEngine.INSTANCE.fromString("\"1/1851\"", address.getClass())).isEqualTo(address);
        assertThat(DaemonGsonEngine.INSTANCE.fromString("\"1/7/59\"", address.getClass())).isEqualTo(address);
    }

    /**
     * Test {@link IndividualAddress} serialization and de-serialization using Daemon Gson
     */
    @Test
    public void individualAddressJson() {
        final var address = IndividualAddress.of(12, 3, 45);

        // serialize
        final var json = DaemonGsonEngine.INSTANCE.toString(address);
        // @formatter:off
        assertThat(json).isEqualTo(
                "{" +
                    "\"type\":0," +
                    "\"format\":\"12.3.45\"," +
                    "\"raw\":[-61,45]" +
                "}");
        // @formatter:on

        // deserialize
        assertThat(DaemonGsonEngine.INSTANCE.fromString(json, address.getClass())).isEqualTo(address);
        assertThat(DaemonGsonEngine.INSTANCE.fromString("{\"type\":0,\"raw\":[-61,45]}", address.getClass())).isEqualTo(address);
        assertThat(DaemonGsonEngine.INSTANCE.fromString("[-61,45]", address.getClass())).isEqualTo(address);
        assertThat(DaemonGsonEngine.INSTANCE.fromString("\"12.3.45\"", address.getClass())).isEqualTo(address);
    }
}
