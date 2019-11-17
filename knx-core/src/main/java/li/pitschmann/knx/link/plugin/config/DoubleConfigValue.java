package li.pitschmann.knx.link.plugin.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Plugin Config Value for {@link Double}
 *
 * @author PITSCHR
 */
public final class DoubleConfigValue extends PluginConfigValue<Double> {

    public DoubleConfigValue(final @Nonnull String configName, final @Nonnull Supplier<Double> defaultSupplier, final @Nullable Predicate<Double> predicate) {
        super(configName, Double.class, Double::valueOf, defaultSupplier, predicate);
    }

}
