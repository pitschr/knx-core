package li.pitschmann.knx.core.plugin;


import java.util.function.Supplier;

/**
 * Plugin Config Value for {@link Boolean}
 *
 * @author PITSCHR
 */
public final class BooleanConfigValue extends PluginConfigValue<Boolean> {

    public BooleanConfigValue(final String configName, final Supplier<Boolean> defaultSupplier) {
        super(configName, Boolean.class, Boolean::valueOf, defaultSupplier, null);
    }

}
