package example.myplugin2;

import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.config.ConfigBuilder;
import li.pitschmann.utils.Sleeper;

public class MainClass {

    public static void main(final String[] args) {

        final var config = ConfigBuilder
                .tunneling()
                // register the plugin
                .plugin(MyConfigurablePlugin.class)
                // define config for plugin
                .setting(MyConfigurablePlugin.STRING, "Hello World!")
                .setting(MyConfigurablePlugin.ENUM, MyConfigurablePlugin.MyEnum.TWO)
                .build();


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
