package example.monitor_plugin;

import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.config.ConfigBuilder;
import li.pitschmann.utils.Sleeper;

/**
 * Demo Application for simple monitor plugin
 */
public class MainClass {

    public static void main(final String[] args) {

        final var config = ConfigBuilder
                .tunneling()                   // communication mode: tunneling
                .plugin(MyMonitorPlugin.class) // register MyMonitorPlugin
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
