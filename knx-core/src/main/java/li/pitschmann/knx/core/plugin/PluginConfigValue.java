package li.pitschmann.knx.core.plugin;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.config.ConfigValue;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Dedicated {@link ConfigValue} instance for Plugin
 *
 * @param <T> value type of plugin config (e.g. Boolean)
 * @author PITSCHR
 */
public class PluginConfigValue<T> extends ConfigValue<T> {
    /**
     * Key is the name of {@code "plugin.config.<pluginName>.<configName>"}
     * (in lower-case, to be done in parent class)
     */
    private final static String KEY_PATTERN = "plugin.config.%s.%s";
    private final String key;

    public PluginConfigValue(
            final String configName,
            final Class<T> classType,
            final Function<String, T> converter,
            final Supplier<T> defaultSupplier,
            final @Nullable Predicate<T> predicate) {
        super(
                // leave it empty -> we will overwrite the key anyway!
                "",
                // rest remain same like ConfigValue
                classType, converter, defaultSupplier, predicate);
        key = String.format(KEY_PATTERN, findPluginClass().getSimpleName(), configName).toLowerCase();
    }

    /**
     * Returns the {@link Class} of Plugin where this
     *
     * @return
     */
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
    @Override
    public String getKey() {
        return key;
    }
}
