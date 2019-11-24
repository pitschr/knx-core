package examples.configurable_plugin;

import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.config.ConfigBuilder;

/**
 * Demo Application for configurable plugin
 */
public class MainClass {

    public static void main(final String[] args) {

        final var config = ConfigBuilder
                .tunneling()
                // register the plugin
                .plugin(MyConfigurablePlugin.class)
                // define config for plugin
                .setting(MyConfigurablePlugin.ENUM, MyConfigurablePlugin.MyEnum.TWO)
                .setting(MyConfigurablePlugin.ANIMAL, "cat")
                .setting(MyConfigurablePlugin.LOUD, "meow!")
                .build();


        // create KNX client and connect to KNX Net/IP device using auto-discovery
        try (final var client = DefaultKnxClient.createStarted(config)) {
            // NO-OP
        }

        // auto-closed and disconnected by KNX client
    }

}
