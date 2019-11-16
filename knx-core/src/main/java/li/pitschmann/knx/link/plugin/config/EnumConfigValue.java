package li.pitschmann.knx.link.plugin.config;

import li.pitschmann.knx.link.plugin.config.PluginConfigValue;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Plugin Config Value for {@link Enum}
 *
 * @author PITSCHR
 */
public final class EnumConfigValue<E extends Enum<E>> extends PluginConfigValue<E> {

    public EnumConfigValue(final @Nonnull String configName,
                           final @Nonnull Class<E> enumClass,
                           final @Nonnull Supplier<E> defaultSupplier) {
        super(configName, enumClass, createConverter(enumClass), defaultSupplier, Objects::nonNull);
    }

    /**
     * Create default converter for {@link Enum} instances
     *
     * @param enumClass
     * @param <T>
     * @return
     */
    private static <T extends Enum<T>> Function<String, T> createConverter(final Class<T> enumClass) {
        return s -> Enum.valueOf(enumClass, s);
    }

}
