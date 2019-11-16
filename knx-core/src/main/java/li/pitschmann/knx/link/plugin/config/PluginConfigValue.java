package li.pitschmann.knx.link.plugin.config;

import li.pitschmann.knx.link.config.ConfigValue;
import li.pitschmann.knx.link.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    // key = fully qualified name of "plugin.config.<pluginName>.<configName>"
    // (in lower-case, to be done in parent class)
    private final static String KEY_PATTERN = "plugin.config.%s.%s";
    private final String key;

    public PluginConfigValue(
            final @Nonnull String configName,
            final @Nonnull Class<T> classType,
            final @Nonnull Function<String, T> converter,
            final @Nonnull Supplier<T> defaultSupplier,
            final @Nullable Predicate<T> predicate) {
        super(
                // leave it empty -> we will overwrite the key anyway!
                "",
                // rest remain same like ConfigValue
                classType, converter, defaultSupplier, predicate, true);
        key = String.format(KEY_PATTERN, findPluginClass().getSimpleName(), configName).toLowerCase();
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
            // cannot happen!
        }
        throw new AssertionError("PluginConfigValue may be used in Plugin class only!");
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
