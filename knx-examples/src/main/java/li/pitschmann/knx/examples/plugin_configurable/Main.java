package li.pitschmann.knx.examples.plugin_configurable;

import li.pitschmann.knx.core.communication.DefaultKnxClient;
import li.pitschmann.knx.core.config.ConfigBuilder;

/**
 * Demo Application for configurable plugin
 */
public class Main {
    public static void main(final String[] args) {
        final var config = ConfigBuilder
                .tunneling()
                // register the plugin
                .plugin(MyConfigurablePlugin.class)
                // define config for plugin
                .setting(MyConfigurablePlugin.ENUM, MyConfigurablePlugin.MyEnum.TWO)
                .setting(MyConfigurablePlugin.ANIMAL, "cat")
                .setting(MyConfigurablePlugin.ANIMAL_SOUND, "meow!")
                .build();

        // create KNX client and connect to KNX Net/IP device using auto-discovery
        try (final var client = DefaultKnxClient.createStarted(config)) {
            // NO-OP
        }
        // auto-closed and disconnected by KNX client
    }
}
