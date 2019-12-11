package li.pitschmann.knx.core.plugin;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Plugin Config Value for {@link String}
 *
 * @author PITSCHR
 */
public final class StringConfigValue extends PluginConfigValue<String> {

    public StringConfigValue(final String configName, final Supplier<String> defaultSupplier, final @Nullable Predicate<String> predicate) {
        super(configName, String.class, String::valueOf, defaultSupplier, predicate);
    }

}
