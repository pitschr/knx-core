package li.pitschmann.knx.link.plugin.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Plugin Config Value for {@link Integer}
 *
 * @author PITSCHR
 */
public final class IntegerConfigValue extends PluginConfigValue<Integer> {

    public IntegerConfigValue(final @Nonnull String configName, final @Nonnull Supplier<Integer> defaultSupplier, final @Nullable Predicate<Integer> predicate) {
        super(configName, Integer.class, Integer::valueOf, defaultSupplier, predicate);
    }

}
