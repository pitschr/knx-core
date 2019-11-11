package li.pitschmann.knx.link.plugin;

import li.pitschmann.knx.link.config.PluginConfigValue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class PathConfigValue extends PluginConfigValue<Path> {

    public PathConfigValue(final Class<? extends Plugin> pluginClass, final String key, final Supplier<Path> defaultSupplier, final Predicate<Path> predicate) {
        super(pluginClass, key, Path.class, Paths::get, defaultSupplier, predicate);
    }
}
