package li.pitschmann.knx.core.plugin;


import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Plugin Config Value for {@link Integer}
 *
 * @author PITSCHR
 */
public final class IntegerConfigValue extends PluginConfigValue<Integer> {

    public IntegerConfigValue(final String configName, final Supplier<Integer> defaultSupplier, final @Nullable Predicate<Integer> predicate) {
        super(configName, Integer.class, Integer::valueOf, defaultSupplier, predicate);
    }

}
