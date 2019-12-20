/*
 * KNX Link - A library for KNX Net/IP communication
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
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.exceptions.KnxPluginException;
import li.pitschmann.knx.core.test.TestHelpers;
import li.pitschmann.knx.core.test.data.TestConstructorExceptionPlugin;
import li.pitschmann.knx.core.test.data.TestExtensionPlugin;
import li.pitschmann.knx.core.test.data.TestMethodExceptionPlugin;
import li.pitschmann.knx.core.test.data.TestObserverPlugin;
import li.pitschmann.knx.core.test.data.TestPlugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        final var knxClientMock = newKnxClientMockWithPlugins(TestObserverPlugin.class, TestExtensionPlugin.class);

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
        pluginManager.addPlugin(TestObserverPlugin.class);
        pluginManager.addPlugin(TestExtensionPlugin.class);

        // 4) verify (init method should be called)
        final var observerPlugin = Objects.requireNonNull(pluginManager.getPlugin(TestObserverPlugin.class));
        assertThat(observerPlugin.getInitInvocations()).isOne();

        final var extensionPlugin = Objects.requireNonNull(pluginManager.getPlugin(TestExtensionPlugin.class));
        assertThat(extensionPlugin.getInitInvocations()).isOne();
    }

    @Test
    @DisplayName("Test notification methods AFTER close")
    public void testNotificationAfterClose() {
        final var knxClientMock = newKnxClientMockWithPlugins(TestObserverPlugin.class, TestExtensionPlugin.class);
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
        pluginManager.unregisterPlugin(plugin.getClass());
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
        final var testPlugin = pluginManager.addPlugin(TestPlugin.class);
        assertThat(pluginManager.getPlugin(TestPlugin.class)).isSameAs(testPlugin);

        // 3) unregister
        pluginManager.unregisterPlugin(TestPlugin.class);

        // 4) check if plugin doesn't exists anymore again
        assertThat(pluginManager.getPlugin(TestPlugin.class)).isNull();
    }

    @Test
    @DisplayName("ERROR: Exception in Plugin Constructor")
    public void testExceptionInPluginConstructor() {
        final var pluginManager = newPluginManager();

        assertThatThrownBy(() -> pluginManager.addPlugin(TestConstructorExceptionPlugin.class))
                .isInstanceOf(KnxPluginException.class)
                .hasMessage("Could not load plugin: " + TestConstructorExceptionPlugin.class.getName());
    }

    @Test
    @DisplayName("ERROR: Exception in Plugin onInit() method")
    public void testExceptionInPluginMethod() {
        final var pluginManager = newPluginManager();

        // we should be able to create a plugin
        final var plugin = pluginManager.addPlugin(TestMethodExceptionPlugin.class);
        assertThat(plugin).isNotNull();

        // calling those methods should not be a problem too!
        pluginManager.notifyClientStart();
        pluginManager.notifyIncomingBody(mock(Body.class));
        pluginManager.notifyOutgoingBody(mock(Body.class));
        pluginManager.notifyError(mock(Throwable.class));
        pluginManager.notifyClientShutdown();
        pluginManager.close();
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
     * @param plugins
     * @return
     */
    @SafeVarargs
    private KnxClient newKnxClientMockWithPlugins(Class<? extends Plugin>... plugins) {
        @SuppressWarnings("unchecked")
        final var castedList = (List<Class<Plugin>>) (Object) Arrays.asList(plugins);

        final var mockConfig = TestHelpers.mockConfig(
                config -> when(config.getPlugins()).thenReturn(castedList)
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
}
