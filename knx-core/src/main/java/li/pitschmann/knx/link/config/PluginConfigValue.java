package li.pitschmann.knx.link.config;

import li.pitschmann.knx.link.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Dedicated {@link ConfigValue} instance for Plugin
 *
 * @param <T>
 */
public class PluginConfigValue<T> extends ConfigValue<T> {
    private final Class<? extends Plugin> pluginClass;

    protected PluginConfigValue(
            final @Nonnull Class<? extends Plugin> pluginClass,
            final @Nonnull String name,
            final @Nonnull Class<T> classType,
            final @Nonnull Function<String, T> converter,
            final @Nonnull Supplier<T> defaultSupplier,
            final @Nullable Predicate<T> predicate) {
        super(
                // key = fully qualified name of plugin + "." + key
                // (in lower-case, to be done in parent class)
                pluginClass.getName() + "." + name,
                // rest remain same like ConfigValue
                classType, converter, defaultSupplier, predicate, false);
        this.pluginClass = pluginClass;
    }

    /**
     * Returns the class of {@link Plugin}
     *
     * @return Instance of Plugin
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public final <T extends Plugin> Class<T> getPluginClass() {
        return (Class<T>) pluginClass;
    }
}
