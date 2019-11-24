package li.pitschmann.knx.core.plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Plugin Config Value for {@link Long}
 *
 * @author PITSCHR
 */
public final class LongConfigValue extends PluginConfigValue<Long> {

    public LongConfigValue(final @Nonnull String configName, final @Nonnull Supplier<Long> defaultSupplier, final @Nullable Predicate<Long> predicate) {
        super(configName, Long.class, Long::valueOf, defaultSupplier, predicate);
    }

}
