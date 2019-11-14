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
    /**
     * Key is the qualified name of
     */
    // key = fully qualified name of plugin + "." + key
    // (in lower-case, to be done in parent class)
    private final static String KEY_PREFIX = "plugin.config.";
    private final String key;

    protected PluginConfigValue(
            final @Nonnull String name,
            final @Nonnull Class<T> classType,
            final @Nonnull Function<String, T> converter,
            final @Nonnull Supplier<T> defaultSupplier,
            final @Nullable Predicate<T> predicate) {
        super(
                // here we only define the name -> the key will be overwritten
                name,
                // rest remain same like ConfigValue
                classType, converter, defaultSupplier, predicate, true);
        this.key = KEY_PREFIX + (findPluginClass().getSimpleName() + super.getKey()).toLowerCase();
    }

    @SuppressWarnings("unchecked")
    private static Class<?> findPluginClass() {
        try {
            for (final var stackTraceElement : Thread.currentThread().getStackTrace()) {
                final var clazz = Class.forName(stackTraceElement.getClassName());
                if (Plugin.class.isAssignableFrom(clazz)) {
                    return clazz;
                }
            }
        } catch (final ClassNotFoundException e) {
            // should not happen!
        }
        throw new AssertionError();
    }

    /**
     * Plugin Config specific key with following format:
     * {@code plugin.config.<pluginName>.<configName>}
     *
     * @return
     */
    @Nonnull
    @Override
    public String getKey() {
        return key;
    }
}
