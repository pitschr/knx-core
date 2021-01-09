package li.pitschmann.knx.examples.load_from_configfile;

import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.exceptions.KnxConfigurationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testing for {@link ConfigFileUtil} instance
 */
public class ConfigFileUtilTest {

    @Test
    @DisplayName("Test non-existent file import")
    public void testImportNonexistentFile() {
        assertThatThrownBy(() -> ConfigFileUtil.loadFile(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConfigFileUtil.loadFile(Paths.get("/invalid/path")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Test empty file import")
    public void testImportEmptyFile() {
        final var configBuilder = ConfigFileUtil.loadFile(Paths.get("src/test/resources/config/empty.config"));
        final var config = configBuilder.build();
        assertThat(config.isNatEnabled()).isFalse();
        assertThat(config.getRemoteControlAddress()).isEqualTo(CoreConfigs.Endpoint.ADDRESS.getDefaultValue());
        assertThat(config.getValue(CoreConfigs.Multicast.TIME_TO_LIVE)).isEqualTo(CoreConfigs.Multicast.TIME_TO_LIVE.getDefaultValue());
    }

    @Test
    @DisplayName("Test file with wrong section name")
    public void testImportWrongSectionName() {
        final var configBuilder = ConfigFileUtil.loadFile(Paths.get("src/test/resources/config/wrongSection.config"));
        final var config = configBuilder.build();
        assertThat(config.isNatEnabled()).isFalse();
        assertThat(config.getRemoteControlAddress()).isEqualTo(CoreConfigs.Endpoint.ADDRESS.getDefaultValue());
        assertThat(config.getValue(CoreConfigs.Multicast.TIME_TO_LIVE)).isEqualTo(CoreConfigs.Multicast.TIME_TO_LIVE.getDefaultValue());
    }

    @Test
    @DisplayName("Test config file with settings only")
    public void testImportFileWithSettingsOnly() {
        final var configBuilder = ConfigFileUtil.loadFile(Paths.get("src/test/resources/config/settingsOnly.config"));
        final var config = configBuilder.build();
        assertThat(config.isNatEnabled()).isTrue();
        assertThat(config.getRemoteControlAddress().getAddress()).containsExactly(123, 45, 67, 89);
        assertThat(config.getValue(CoreConfigs.Multicast.TIME_TO_LIVE)).isEqualTo(255);
        assertThat(config.getPlugins()).isEmpty();
    }

    @Test
    @DisplayName("Test file with non-existent plugin")
    public void testImportNonExistentPlugin() {
        assertThatThrownBy(() -> ConfigFileUtil.loadFile(Paths.get("src/test/resources/config/wrongPlugins.config")))
                .isInstanceOf(KnxConfigurationException.class)
                .hasMessage("Could not load plugin: li.pitschmann.knx.examples.load_from_configfile.TestPluginDOESNOTEXISTS");
    }

    @Test
    @DisplayName("Test config file with plugins only")
    public void testImportFileWithPluginsOnly() {
        final var configBuilder = ConfigFileUtil.loadFile(Paths.get("src/test/resources/config/pluginsOnly.config"));
        final var plugins = configBuilder.build().getPlugins();
        assertThat(plugins).hasSize(3);
        assertThat(plugins.get(0)).isExactlyInstanceOf(TestPlugin.class);
        assertThat(plugins.get(1)).isExactlyInstanceOf(TestObserverPlugin.class);
        assertThat(plugins.get(2)).isExactlyInstanceOf(TestExtensionPlugin.class);
    }

    @Test
    @DisplayName("Test import file with all data (settings, plugin, ...)")
    public void testImportCompleteFile() {
        final var configBuilder = ConfigFileUtil.loadFile(Paths.get("src/test/resources/config/complete.config"));
        final var config = configBuilder.build();
        assertThat(config.isNatEnabled()).isTrue();
        assertThat(config.getRemoteControlAddress().getAddress()).containsExactly(43, 123, 32, 213);
        assertThat(config.getValue(CoreConfigs.Multicast.TIME_TO_LIVE)).isEqualTo(127);

        final var plugins = config.getPlugins();
        assertThat(plugins).hasSize(3);
        assertThat(plugins.get(0)).isExactlyInstanceOf(TestPlugin.class);
        assertThat(plugins.get(1)).isExactlyInstanceOf(TestObserverPlugin.class);
        assertThat(plugins.get(2)).isExactlyInstanceOf(TestExtensionPlugin.class);
    }

    @Test
    @DisplayName("Test config file with wrong setting format")
    public void testImportFileWithWrongSettingsFormat() {
        assertThatThrownBy(() -> ConfigFileUtil.loadFile(Paths.get("src/test/resources/config/wrongFormat.config")))
                .isInstanceOf(KnxConfigurationException.class)
                .hasMessageStartingWith("It must be a key=value pair");
    }

    @Test
    @DisplayName("Test config file with wrong file type format")
    public void testImportFileWithWrongFileType() {
        assertThatThrownBy(() -> ConfigFileUtil.loadFile(Paths.get("src/test/resources/config/wrongFileType.config")))
                .isInstanceOf(KnxConfigurationException.class)
                .hasMessageStartingWith("Cannot read or parse file:");
    }

    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        assertThatThrownBy(() -> {
            final var ctor = ConfigFileUtil.class.getDeclaredConstructor();
            ctor.trySetAccessible();
            ctor.newInstance();
        }).hasCauseInstanceOf(AssertionError.class);
    }
}
