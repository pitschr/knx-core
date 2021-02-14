package li.pitschmann.knx.core.plugin;

import li.pitschmann.knx.core.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Plugin Config Value for {@link Path}
 *
 * @author PITSCHR
 */
public final class PathConfigValue extends PluginConfigValue<Path> {

    public PathConfigValue(final String configName, final Supplier<Path> defaultSupplier, final @Nullable Predicate<Path> predicate) {
        super(configName, Path.class, Paths::get, defaultSupplier, predicate);
    }

}
