package li.pitschmann.knx.examples.plugin;

import li.pitschmann.knx.core.communication.DefaultKnxClient;
import li.pitschmann.knx.core.config.ConfigBuilder;
import li.pitschmann.knx.core.utils.Sleeper;

/**
 * Demo Application for simple monitor plugin
 */
public class Main {

    public static void main(final String[] args) {

        final var config = ConfigBuilder
                .tunneling()                   // communication mode: tunneling
                .plugin(MyPlugin.class) // register MyPlugin
                .build();                      // create immutable config

        // create KNX client and connect to KNX Net/IP device using auto-discovery
        try (final var client = DefaultKnxClient.createStarted(config)) {
            // wait 60 seconds
            Sleeper.seconds(60);
            // then
            System.out.println("Time is up!");
        }

        // auto-closed and disconnected by KNX client
    }

}
