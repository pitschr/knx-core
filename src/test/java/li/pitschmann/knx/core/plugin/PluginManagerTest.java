/*
 * Copyright (C) 2019 Pitschmann Christoph
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package li.pitschmann.knx.core.plugin;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.communication.BaseKnxClient;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.exceptions.KnxPluginException;
import li.pitschmann.knx.core.test.TestHelpers;
import li.pitschmann.knx.core.test.data.TestExtensionPlugin;
import li.pitschmann.knx.core.test.data.TestMethodExceptionPlugin;
import li.pitschmann.knx.core.test.data.TestObserverPlugin;
import li.pitschmann.knx.core.test.data.TestPlugin;
import li.pitschmann.knx.core.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link PluginManager}
 */
public final class PluginManagerTest {

    @Test
    @DisplayName("Instantiation of PluginManager")
    public void testInstantiation() {
        // create plugin manager and close afterwards (no exception expected)
        new PluginManager(TestHelpers.mockConfig()).close();

        // invalid cases
        assertThatThrownBy(() -> new PluginManager(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Test initial initialization of plugins through configuration instance")
    public void testOnInitialization() {
        final var knxClientMock = newKnxClientMockWithPlugins(new TestObserverPlugin(), new TestExtensionPlugin());

        // 2) initialization (two plugins registered)
        final var pluginManager = new PluginManager(knxClientMock.getConfig());
        pluginManager.notifyInitialization(knxClientMock);

        // 3) verify
        final var observerPlugin = Objects.requireNonNull(pluginManager.getPlugin(TestObserverPlugin.class));
        assertThat(observerPlugin.getInitInvocations()).isOne();

        final var extensionPlugin = Objects.requireNonNull(pluginManager.getPlugin(TestExtensionPlugin.class));
        assertThat(extensionPlugin.getInitInvocations()).isOne();
    }

    @Test
    @DisplayName("Test lazy initialization of plugins")
    public void testLazyOnInitialization() {
        // 1) initialization (no plugin registered yet)
        final var pluginManager = newPluginManager();

        // 2 verify
        assertThat(pluginManager.getPlugin(TestObserverPlugin.class)).isNull();
        assertThat(pluginManager.getPlugin(TestExtensionPlugin.class)).isNull();

        // 3) register two plugins
        pluginManager.addPlugin(new TestObserverPlugin());
        pluginManager.addPlugin(new TestExtensionPlugin());

        // 4) verify (init method should be called)
        final var observerPlugin = Objects.requireNonNull(pluginManager.getPlugin(TestObserverPlugin.class));
        assertThat(observerPlugin.getInitInvocations()).isOne();

        final var extensionPlugin = Objects.requireNonNull(pluginManager.getPlugin(TestExtensionPlugin.class));
        assertThat(extensionPlugin.getInitInvocations()).isOne();
    }

    @Test
    @DisplayName("Test notification methods AFTER close")
    public void testNotificationAfterClose() {
        final var knxClientMock = newKnxClientMockWithPlugins(new TestObserverPlugin(), new TestExtensionPlugin());
        final var pluginManager = new PluginManager(knxClientMock.getConfig());

        pluginManager.close();

        // should not be a problem - is silently ignored (log warn lines printed)
        pluginManager.notifyInitialization(knxClientMock);
        pluginManager.notifyClientStart();
        pluginManager.notifyClientShutdown();
        pluginManager.notifyIncomingBody(mock(ResponseBody.class));
        pluginManager.notifyOutgoingBody(mock(RequestBody.class));
        pluginManager.notifyError(mock(Throwable.class));
    }

    @Test
    @DisplayName("ERROR: Test registration by a path that doesn't end with *.jar extension")
    public void testWithoutJarExtension() {
        final var pluginManager = newPluginManager();

        // wrong JAR file path
        final var pathToJAR = Paths.get("file-with-wrong.extension");
        final var className = "my.plugin.MyTestPlugin";
        assertThatThrownBy(() -> pluginManager.addPlugin(pathToJAR, className))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File doesn't end with '.jar' extension: file-with-wrong.extension");
    }

    @Test
    @DisplayName("ERROR: Test registration by a wrong JAR path")
    public void testNonexistentJarPath() {
        final var pluginManager = newPluginManager();

        // wrong JAR file path
        final var pathToJAR = Paths.get("non-existent-file.jar");
        final var className = "my.plugin.MyTestPlugin";
        assertThatThrownBy(() -> pluginManager.addPlugin(pathToJAR, className))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File doesn't exists or is not readable: non-existent-file.jar");
    }

    @Test
    @DisplayName("ERROR: Test registration by a non-existent plugin")
    public void testNonexistentPlugin() {
        final var pluginManager = newPluginManager();

        // wrong JAR file path
        final var pathToJAR = Paths.get("src/test/resources/plugin/my-test-plugin.jar");
        final var className = "my.test.HelloWorldPluginV1_THATDOESNOTEXISTS";
        assertThatThrownBy(() -> pluginManager.addPlugin(pathToJAR, className)).isInstanceOf(KnxException.class);

        // wrong class type
        final var plugin2 = pluginManager.getPlugin(Plugin.class);
        assertThat(plugin2).isNull();
    }

    @Test
    @DisplayName("Test registration of plugin by JAR file")
    public void testJARPlugin() {
        final var pluginManager = newPluginManager();

        final var pathToJAR = Paths.get("src/test/resources/plugin/my-test-plugin.jar");
        final var className = "my.test.HelloWorldPluginV1";

        final var plugin = pluginManager.addPlugin(pathToJAR, className);
        assertThat(plugin).isInstanceOf(Plugin.class);
        assertThat(plugin.getClass().getName()).isEqualTo(className);

        // correct class type
        final var plugin2 = pluginManager.getPlugin(plugin.getClass());
        assertThat(plugin2).isSameAs(plugin);

        // de-register the old plugin and re-register the plugin (new plugin should be born)
        pluginManager.removePlugin(plugin.getClass());
        final var newPlugin = pluginManager.addPlugin(pathToJAR, className);
        assertThat(newPlugin).isInstanceOf(Plugin.class);
        assertThat(newPlugin.getClass().getName()).isEqualTo(className);
        assertThat(newPlugin).isNotSameAs(plugin);
    }

    @Test
    @DisplayName("Register and register of plugin")
    public void testRegisterAndDeregister() {
        final var pluginManager = newPluginManager();

        // 1) check if plugin doesn't exists
        assertThat(pluginManager.getPlugin(TestPlugin.class)).isNull();

        // 2) add plugin and check if plugin exists
        final var testPlugin = pluginManager.addPlugin(new TestPlugin());
        assertThat(pluginManager.getPlugin(TestPlugin.class)).isSameAs(testPlugin);

        // 3) unregister
        pluginManager.removePlugin(TestPlugin.class);

        // 4) check if plugin doesn't exists anymore again
        assertThat(pluginManager.getPlugin(TestPlugin.class)).isNull();
    }

    @Test
    @DisplayName("Unregister a plugin that has already been registered")
    void testUnregisterPlugin() {
        final var pluginManager = newPluginManager();

        // 1) register plugin
        final var plugin = new TestPlugin();
        final var pluginAdded = pluginManager.addPlugin(plugin);
        assertThat(pluginAdded).isSameAs(plugin);

        // 2) unregister plugin
        final var pluginRemoved = pluginManager.removePlugin(TestPlugin.class);
        assertThat(pluginRemoved).isSameAs(plugin);
    }

    @Test
    @DisplayName("Unregister a plugin that has not been added previously")
    void testUnregisterPluginNotAddedPreviously() {
        final var pluginManager = newPluginManager();
        assertThat(pluginManager.removePlugin(TestPlugin.class)).isNull();
    }

    @Test
    @DisplayName("ERROR: Exception in Plugin onInit() method")
    public void testExceptionInPluginMethod() {
        final var pluginManager = newPluginManager();

        // we should be able to create a plugin
        final var plugin = pluginManager.addPlugin(new TestMethodExceptionPlugin());
        assertThat(plugin).isNotNull();

        // calling those methods should not be a problem too!
        pluginManager.notifyClientStart();
        pluginManager.notifyIncomingBody(mock(Body.class));
        pluginManager.notifyOutgoingBody(mock(Body.class));
        pluginManager.notifyError(mock(Throwable.class));
        pluginManager.notifyClientShutdown();
        pluginManager.close();
    }

    @Test
    @DisplayName("ERROR: Exception in registering plugin due e.g. Timeout")
    void testRegisterPluginTimeout() {
        final var knxClientMock = newKnxClientMockWithPlugins();

        // 1) modify the timeout for plugin initialization to 1 millisecond
        doReturn(1L).when(knxClientMock).getConfig(CoreConfigs.Plugin.INITIALIZATION_TIMEOUT); // 1ms

        // 2) initialization (two plugins registered)
        final var pluginManager = new PluginManager(knxClientMock.getConfig());
        pluginManager.notifyInitialization(knxClientMock);

        // 3) create "slow plugin"
        final var slowPlugin = new Plugin() {
            @Override
            public void onInitialization(KnxClient client) {
                Sleeper.seconds(3);
            }
        };

        assertThatThrownBy(() -> pluginManager.addPlugin(slowPlugin))
                .isInstanceOf(KnxPluginException.class)
                .hasRootCauseInstanceOf(TimeoutException.class)
                .hasMessage("Could not initialize plugin: " + slowPlugin);
    }

    /**
     * Creates default plugin manager with mocked client and config
     */
    private PluginManager newPluginManager() {
        final var knxClientMock = TestHelpers.mockKnxClient(
                config -> {
                },
                client -> when(client.isRunning()).thenReturn(true),
                BaseKnxClient.class);
        final var configMock = knxClientMock.getConfig();

        final var pluginManager = new PluginManager(configMock);
        pluginManager.notifyInitialization(knxClientMock);
        return pluginManager;
    }

    /**
     * Creates {@link KnxClient} with customized config that is pre-configured with {@code plugins}
     *
     * @param plugins array of plugins to be registered
     * @return mocked KNX client
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked", "varargs"})
    private KnxClient newKnxClientMockWithPlugins(Plugin... plugins) {
        final var mockConfig = TestHelpers.mockConfig(
                config -> when(config.getPlugins()).thenReturn(List.of(plugins))
        );

        final var knxClientMock = TestHelpers.mockKnxClient(/*dummy*/
                x -> {
                },
                y -> {
                },
                BaseKnxClient.class);
        when(knxClientMock.getConfig()).thenReturn(mockConfig);
        return knxClientMock;
    }

    /**
     * Simulates a plugin that runs into a timeout during calling {@link #onInitialization(KnxClient)}.
     * The PluginManager should reject it.
     */
    private static class SlowPlugin implements Plugin {
        @Override
        public void onInitialization(KnxClient client) {
            Sleeper.seconds(3);
        }
    }
}

