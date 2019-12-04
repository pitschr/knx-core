package li.pitschmann.knx.core.utils;

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.config.InternalConfigValue;
import li.pitschmann.knx.core.plugin.BooleanConfigValue;
import li.pitschmann.knx.core.plugin.IntegerConfigValue;
import li.pitschmann.knx.core.plugin.Plugin;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;



import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Testing for {@link Configs} instance
 */
public class ConfigsTest {

    @Test
    @DisplayName("Return config values as list from class")
    public void testConfigValues() {
        final var configValues = Configs.getConfigValues(TestPluginWithConfigs.class);
        assertThat(configValues).hasSize(2);
        assertThat(configValues.get(0)).isSameAs(TestPluginWithConfigs.OK_BOOLEAN);
        assertThat(configValues.get(1)).isSameAs(TestPluginWithConfigs.OK_INTEGER);
    }

    @Test
    @DisplayName("Return config values as map from class")
    public void testConfigValueMap() {
        final var configValueMap = Configs.getConfigMapValues(TestPluginWithConfigs.class);
        assertThat(configValueMap).hasSize(2);

        final var okBoolean = configValueMap.get("plugin.config.testpluginwithconfigs.ok-boolean");
        assertThat(okBoolean).isNotNull().isSameAs(TestPluginWithConfigs.OK_BOOLEAN);

        final var okInteger = configValueMap.get("plugin.config.testpluginwithconfigs.ok-integer");
        assertThat(okInteger).isNotNull().isSameAs(TestPluginWithConfigs.OK_INTEGER);
    }

    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Configs.class);
    }


    public static class TestPluginWithConfigs implements Plugin {
        // OK (Boolean)
        public static final BooleanConfigValue OK_BOOLEAN = new BooleanConfigValue(
                "ok-boolean", () -> Boolean.TRUE
        );
        // OK (Integer)
        public static final IntegerConfigValue OK_INTEGER = new IntegerConfigValue(
                "ok-integer", () -> 4711, null
        );
        // INTERNAL_CONFIG
        public static final InternalConfigValue INTERNAL_BOOLEAN = mock(InternalConfigValue.class);
        // MISSING_PUBLIC
        static final BooleanConfigValue MISSING_PUBLIC = new BooleanConfigValue(
                "missing-public", () -> Boolean.TRUE
        );
        // MISSING_FINAL
        public static BooleanConfigValue MISSING_FINAL = new BooleanConfigValue(
                "missing-final", () -> Boolean.TRUE
        );
        // MISSING_STATIC
        public final BooleanConfigValue MISSING_STATIC = new BooleanConfigValue(
                "missing-static", () -> Boolean.TRUE
        );

        @Override
        public void onInitialization(final KnxClient client) {
            // NOOP
        }
    }
}
