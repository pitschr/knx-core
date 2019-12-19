package li.pitschmann.knx.core.config;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * {@link ConfigValue} for internal purposes only!
 * Initialization of this class is package-protected.
 * <p>
 * This allows us to read / parse the config from a file, but
 * does not allow user to set the configuration directly in code.
 * <p>
 * Used for:
 * <ul>
 * <li>{@link CoreConfigs.Endpoint#ADDRESS}</li>
 * <li>{@link CoreConfigs.Endpoint#PORT}</li>
 * </ul>
 *
 * @param <T> value type of internal config (e.g. Boolean)
 * @author PITSCHR
 */
public final class InternalConfigValue<T> extends ConfigValue<T> {
    InternalConfigValue(
            final String key,
            final Class<T> classType,
            final Function<String, T> converter,
            final Supplier<T> defaultSupplier,
            final @Nullable Predicate<T> predicate) {
        super(key, classType, converter, defaultSupplier, predicate);
    }
}
