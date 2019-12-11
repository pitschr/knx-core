package li.pitschmann.knx.core.plugin;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Plugin Config Value for {@link Double}
 *
 * @author PITSCHR
 */
public final class DoubleConfigValue extends PluginConfigValue<Double> {

    public DoubleConfigValue(final String configName, final Supplier<Double> defaultSupplier, final @Nullable Predicate<Double> predicate) {
        super(configName, Double.class, Double::valueOf, defaultSupplier, predicate);
    }

}
