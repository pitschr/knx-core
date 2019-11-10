package li.pitschmann.knx.link.plugin;

import li.pitschmann.knx.link.config.PluginConfigValue;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class StringConfigValue extends PluginConfigValue<String> {

    public StringConfigValue(final Class<? extends Plugin> pluginClass, final String key, final Supplier<String> defaultSupplier, final Predicate<String> predicate) {
        super(pluginClass, key, String.class, String::valueOf, defaultSupplier, predicate);
    }
}
