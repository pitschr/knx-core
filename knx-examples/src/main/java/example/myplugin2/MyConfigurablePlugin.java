package example.myplugin2;

import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.plugin.Plugin;
import li.pitschmann.knx.link.plugin.config.EnumConfigValue;
import li.pitschmann.knx.link.plugin.config.IntegerConfigValue;
import li.pitschmann.knx.link.plugin.config.LongConfigValue;
import li.pitschmann.knx.link.plugin.config.StringConfigValue;

import java.util.Objects;

public class MyConfigurablePlugin implements Plugin {
    /**
     * Config with string, defaults back to "{@code default value}" value. String value may not be null.
     */
    public static final StringConfigValue STRING = new StringConfigValue("string", () -> "default value", Objects::nonNull);
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

    @Override
    public void onInitialization(final KnxClient client) {
        System.out.println("String: " + client.getConfig(STRING));
        System.out.println("Integer: " + client.getConfig(INTEGER));
        System.out.println("Long: " + client.getConfig(LONG));
        System.out.println("Enum: " + client.getConfig(ENUM));
    }

    public enum MyEnum {
        ZERO, ONE, TWO, THREE
    }

}