# KNX Core Plugins

This folder contains plugins which are subject to be injected to the 
KNX Client via plugin manager. By writing own plugins we have a great
possibility to extend the client with some great functionalities which
are not supported ouf the box and also allows you to implement some
integration to 3rd-party applications (e.g. Grafana, Kafka, ...)

## Available Plugins

* [API](api) extends KNX Client with RESTful API which allows integration with third-party applications.
* [FileAuditPlugin](audit) audits all events (packets and signals) to a file. Supports JSON and TSV formats.
* [FileStatisticPlugin](statistic) writes statistic in a pre-defined interval to a file. Supports JSON, TSV and TEXT formats.

## Plugin Development

Each plugin must implement `Plugin` interface that comes with one method
`onInitialization(KnxClient)` that is invoked when plugin is effectively
registered to the KNX Client via internal plugin manager. In most cases
it may make more sense to extend the plugin with `ObservablePlugin` and/or 
`ExtensionPlugin` interfaces.

The `ObserverPlugin` extends the `Plugin` interface and offers additional
methods:
```
onIncomingBody(Body)  // when KNX packet arrives
onOutgoingBody(Body)  // when KNX packet is sent
onError(Throwable)    // when there was a communication error
```

The `ExtensionPlugin` extends the `Plugin` interface and offers additional
methods:
```
onStart()             // when connection with KNX Net/IP device is established
onShutdown()          // when connection with KNX Net/IP device is stopped
```

## Plugin Configuration

The plugin can be also customizable using `PluginConfigValue` class, there are  
some out-of-the-box concrete config value implementations:

| Type     | Class     |
| ---------| --------- |
| Boolean  | [BooleanConfigValue](/knx-core/src/main/java/li/pitschmann/knx/core/plugin/BooleanConfigValue.java) |
| Double   | [DoubleConfigValue](/knx-core/src/main/java/li/pitschmann/knx/core/plugin/DoubleConfigValue.java) |
| Enum<E>  | [EnumConfigValue](/knx-core/src/main/java/li/pitschmann/knx/core/plugin/EnumConfigValue.java) |
| Integer  | [IntegerConfigValue](/knx-core/src/main/java/li/pitschmann/knx/core/plugin/IntegerConfigValue.java) |
| Long     | [LongConfigValue](/knx-core/src/main/java/li/pitschmann/knx/core/plugin/LongConfigValue.java) |
| Path     | [PathConfigValue](/knx-core/src/main/java/li/pitschmann/knx/core/plugin/PathConfigValue.java) |
| String   | [StringConfigValue](/knx-core/src/main/java/li/pitschmann/knx/core/plugin/StringConfigValue.java) |

In case you need a special configuration type, you have two options: 
* Use the `PluginConfigValue` directly
* Write your own implementation by extending the [PluginConfigValue](/knx-core/src/main/java/li/pitschmann/knx/core/plugin/PluginConfigValue.java)

## Example: Simple Monitor

The following example demonstrates how we can implement and register the plugin.

##### Requirement

As an user we want to print out all signals and all packets (incoming and outgoing)
to console. After 60 seconds the client should be shut down.

```bash
Initialized by client: li.pitschmann.knx.core.communication.DefaultKnxClient@4c841c4
Outgoing: Search Request (0x08 01 00 00 00 00 0E 57)
Incoming: Search Response (0x08 01 C0 A8 01 1D 0E 57 36 01 02 00 00 00 00 00 00 00 00 00 ...)
Outgoing: Description Request (0x08 01 00 00 00 00 00 00)
Incoming: Description Response (0x36 01 02 00 00 00 00 00 36 01 02 00 00 00 E0 00 17 0C ...)
Outgoing: Connect Request (0x08 01 C0 A8 01 02 E1 0E 08 01 C0 A8 01 02 DD 43 04 04 02 00)
Incoming: Connect Response (0x01 00 08 01 C0 A8 01 1D 0E 57 04 04 00 00)
Outgoing: Connection State Request (0x01 00 08 01 C0 A8 01 02 E1 0E)
Incoming: Connection State Response (0x01 00)
Start signal received
Incoming: Tunneling Request (0x04 01 00 00 29 00 BC D0 10 D2 4C 10 03 00 80 07 D1)
Outgoing: Tunneling Acknowledgement (0x04 01 00 00)
... lines omitted ...
Incoming: Tunneling Request (0x04 01 0D 00 29 00 BC D0 10 01 5B 02 03 00 80 14 7B)
Outgoing: Tunneling Acknowledgement (0x04 01 0D 00)
Time is up!
Outgoing: Disconnect Request (0x01 00 08 01 C0 A8 01 02 E1 0E)
Incoming: Disconnect Response (0x01 00)
Stop signal received
```

##### Implementation

We implement our plugin with `ObserverPlugin` interface as we want to be notified 
when KNX packets arrived or sent. In case of communication error the stack trace
message should be printed out. To catch the start and stop signals the plugin also 
implements the `ExtensionPlugin`:

```java
public class MyMonitorPlugin implements ObserverPlugin, ExtensionPlugin {

    @Override
    public void onInitialization(final KnxClient client) {
        System.out.println("Initialized by client: " + client);
    }

    @Override
    public void onIncomingBody(final Body item) {
        System.out.println("Incoming: " + item.getServiceType().getFriendlyName() + " (" + item.getRawDataAsHexString() + ")");
    }

    @Override
    public void onOutgoingBody(final Body item) {
        System.out.println("Outgoing: " + item.getServiceType().getFriendlyName() + " (" + item.getRawDataAsHexString() + ")");
    }

    @Override
    public void onError(final Throwable throwable) {
        System.out.println("On Error: " + throwable.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("Start signal received");
    }

    @Override
    public void onShutdown() {
        System.out.println("Stop signal received");
    }

}
````

Next step is to create a class that configures our KNX Client with our plugin class:

```java
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
```

## Example: Configurable Plugin

Given example shows how to implement a plugin that accepts configuration.

```java
public class MyConfigurablePlugin implements Plugin {
    /**
     * Config with integer, defaults back to {@code 0}. No predication.
     */
    public static final IntegerConfigValue INTEGER = new IntegerConfigValue("int", () -> 0, null);
    /**
     * Config with long, defaults back to {@code 4711}. Value must be between 4000 and 5000
     */
    public static final LongConfigValue LONG = new LongConfigValue("long", () -> 4711L, l -> l >= 4000 && l <= 5000);
    /**
     * Config with enumeration, defaults back to {@link MyEnum#ZERO}.
     */
    public static final EnumConfigValue<MyEnum> ENUM = new EnumConfigValue<>("enum", MyEnum.class, () -> MyEnum.ZERO);
    /**
     * Config for special purposes, defaults back to "dog".
     */
    public static final PluginConfigValue<String> ANIMAL = new PluginConfigValue<>("animal", String.class, String::valueOf, () -> "dog", Objects::nonNull);
    /**
     * Config for special purposes extended by an inner class, defaults back to "woof!"
     */
    public static final AnimalSound ANIMAL_SOUND = new AnimalSound();

    @Override
    public void onInitialization(final KnxClient client) {
        System.out.println("Integer: " + client.getConfig(INTEGER));
        System.out.println("Long: " + client.getConfig(LONG));
        System.out.println("Enum: " + client.getConfig(ENUM));
        System.out.println("Animal: " + client.getConfig(ANIMAL));
        System.out.println("Animal Sound: " + client.getConfig(ANIMAL_SOUND));
    }

    public enum MyEnum {
        ZERO, ONE, TWO, THREE
    }

    public static class AnimalSound extends PluginConfigValue<String> {
        public AnimalSound() {
            super("animal-sound", String.class, String::valueOf, () -> "woof!", null);
        }
    }
}
````

```java
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
```

For plugin the pattern of configuration key and value is hardcoded with 
```
plugin.config.<nameOfPlugin>.<nameOfConfig>=<valueOfConfig>
```

The `<nameOfPlugin>` is the simple name of Java class and the `<nameOfConfig>` 
is the hardcoded name you define in 1st argument of `*ConfigValue`. 
The `<valueOfConfig>` must be parsable according to the Java type.

Based on `MyConfigurablePlugin` example above, the config file would look like:

```
plugin.config.MyConfigurablePlugin.enum=TWO
plugin.config.MyConfigurablePlugin.animal=cat
plugin.config.MyConfigurablePlugin.animal-sound=meow!
```

Console Output from plugin:
```
Integer: 0
Long: 4711
Enum: TWO
Animal: cat
Animal Sound: meow!
```
