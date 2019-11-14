package li.pitschmann.knx.link.plugin;

import li.pitschmann.knx.link.config.PluginConfigValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public PathConfigValue(final @Nonnull String configName, final @Nonnull Supplier<Path> defaultSupplier, final @Nullable Predicate<Path> predicate) {
        super(configName, Path.class, Paths::get, defaultSupplier, predicate);
    }

}
