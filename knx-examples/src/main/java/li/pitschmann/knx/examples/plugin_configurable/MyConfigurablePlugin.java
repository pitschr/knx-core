package li.pitschmann.knx.examples.plugin_configurable;

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.EnumConfigValue;
import li.pitschmann.knx.core.plugin.IntegerConfigValue;
import li.pitschmann.knx.core.plugin.LongConfigValue;
import li.pitschmann.knx.core.plugin.Plugin;
import li.pitschmann.knx.core.plugin.PluginConfigValue;

import java.util.Objects;

/**
 * Demo implementation for configurable plugin
 */
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
