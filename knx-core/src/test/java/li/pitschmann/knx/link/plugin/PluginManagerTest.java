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

package li.pitschmann.knx.link.plugin;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;
import li.pitschmann.knx.link.communication.BaseKnxClient;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.config.Config;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.knx.link.exceptions.KnxPluginException;
import li.pitschmann.knx.test.data.TestConstructorExceptionPlugin;
import li.pitschmann.knx.test.data.TestExtensionPlugin;
import li.pitschmann.knx.test.data.TestMethodExceptionPlugin;
import li.pitschmann.knx.test.data.TestObserverPlugin;
import li.pitschmann.knx.test.data.TestPlugin;
import li.pitschmann.utils.Sleeper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
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
        new PluginManager(newConfigMock()).close();

        // invalid cases
        assertThatThrownBy(() -> new PluginManager(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Test initial initialization of plugins through configuration instance")
    public void testOnInitialization() {
        // 1) register two plugins (via config)
        final var configMock = newConfigMock();
        @SuppressWarnings("unchecked") final var uncheckedList = (List<Class<Plugin>>) (List<?>) List.of(TestObserverPlugin.class, TestExtensionPlugin.class);
        when(configMock.getPlugins()).thenReturn(uncheckedList);

        // 2) initialization
        final var knxClientMock = newKnxClientMock(configMock);
        final var pluginManager = new PluginManager(configMock);
        pluginManager.notifyInitialization(knxClientMock);
        Sleeper.milliseconds(500); // wait bit, as plugin executor is notifying the plugins

        // 3) verify
        final var observerPlugin = Objects.requireNonNull(pluginManager.getPlugin(TestObserverPlugin.class));
        assertThat(observerPlugin.getInitInvocations()).isOne();

        final var extensionPlugin = Objects.requireNonNull(pluginManager.getPlugin(TestExtensionPlugin.class));
        assertThat(extensionPlugin.getInitInvocations()).isOne();
    }

    @Test
    @DisplayName("Test lazy initialization of plugins")
    public void testLazyOnInitialization() {
        final var configMock = newConfigMock();
        final var pluginManager = new PluginManager(configMock);
        final var knxClientMock = newKnxClientMock(configMock);

        // 1) initialization (no plugin registered yet)
        pluginManager.notifyInitialization(knxClientMock);
        Sleeper.milliseconds(500); // wait bit, as plugin executor is notifying the plugins

        // 2 verify
        assertThat(pluginManager.getPlugin(TestObserverPlugin.class)).isNull();
        assertThat(pluginManager.getPlugin(TestExtensionPlugin.class)).isNull();

        // 3) register two plugins
        pluginManager.addPlugin(TestObserverPlugin.class);
        pluginManager.addPlugin(TestExtensionPlugin.class);
        Sleeper.milliseconds(500); // wait bit, as plugin executor is notifying the plugins

        // 4) verify (init method should be called)
        final var observerPlugin = Objects.requireNonNull(pluginManager.getPlugin(TestObserverPlugin.class));
        assertThat(observerPlugin.getInitInvocations()).isOne();

        final var extensionPlugin = Objects.requireNonNull(pluginManager.getPlugin(TestExtensionPlugin.class));
        assertThat(extensionPlugin.getInitInvocations()).isOne();
    }

    @Test
    @DisplayName("Test notification methods AFTER close")
    public void testNotificationAfterClose() {
        final var configMock = newConfigMock();
        final var pluginManager = new PluginManager(configMock);

        pluginManager.close();

        // should not be a problem - is silently ignored (log warn lines printed)
        pluginManager.notifyInitialization(newKnxClientMock(configMock));
        pluginManager.notifyClientStart();
        pluginManager.notifyClientShutdown();
        pluginManager.notifyIncomingBody(mock(ResponseBody.class));
        pluginManager.notifyOutgoingBody(mock(RequestBody.class));
        pluginManager.notifyError(mock(Throwable.class));
    }

    @Test
    @DisplayName("Error: Test registration by a path that doesn't end with *.jar extension")
    public void testWithoutJarExtension() {
        final var configMock = newConfigMock();
        final var pluginManager = new PluginManager(configMock);

        // wrong JAR file path
        final var pathToJAR = Paths.get("file-with-wrong.extension");
        final var className = "my.plugin.MyTestPlugin";
        assertThatThrownBy(() -> pluginManager.addPlugin(pathToJAR, className))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File doesn't end with '.jar' extension: file-with-wrong.extension");
    }

    @Test
    @DisplayName("Error: Test registration by a wrong JAR path")
    public void testNonexistentJarPath() {
        final var configMock = newConfigMock();
        final var pluginManager = new PluginManager(configMock);

        // wrong JAR file path
        final var pathToJAR = Paths.get("non-existent-file.jar");
        final var className = "my.plugin.MyTestPlugin";
        assertThatThrownBy(() -> pluginManager.addPlugin(pathToJAR, className))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File doesn't exists or is not readable: non-existent-file.jar");
    }

    @Test
    @DisplayName("Error: Test registration by a non-existent plugin")
    public void testNonexistentPlugin() {
        final var pluginManager = new PluginManager(newConfigMock());

        // wrong JAR file path
        final var pathToJAR = Paths.get("src/test/resources/plugin/my-test-plugin.jar");
        final var className = "my.test.HelloWorldPluginV1_THATDOESNOTEXISTS";
        assertThatThrownBy(() -> pluginManager.addPlugin(pathToJAR, className)).isInstanceOf(KnxException.class);

        // wrong class type
        final var plugin2 = pluginManager.getPlugin(Plugin.class);
        assertThat(plugin2).isNull();
    }

    @Test
    @DisplayName("OK: Test registration of plugin by JAR file")
    public void testJARPlugin() {
        final var pluginManager = new PluginManager(newConfigMock());

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
    @DisplayName("OK: Register and register of plugin")
    public void testRegisterAndDeregister() {
        final var pluginManager = new PluginManager(newConfigMock());

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
        final var pluginManager = new PluginManager(newConfigMock());

        assertThatThrownBy(() -> pluginManager.addPlugin(TestConstructorExceptionPlugin.class))
                .isInstanceOf(KnxPluginException.class)
                .hasMessage("Could not load plugin: " + TestConstructorExceptionPlugin.class.getName());
    }

    @Test
    @DisplayName("ERROR: Exception in Plugin onInit() method")
    public void testExceptionInPluginMethod() {
        final var configMock = newConfigMock();
        final var knxClientMock = newKnxClientMock(configMock);
        final var pluginManager = new PluginManager(newConfigMock());
        pluginManager.notifyInitialization(knxClientMock);

        // we should be able to create a plugin
        final var plugin = pluginManager.addPlugin(TestMethodExceptionPlugin.class);
        assertThat(plugin).isNotNull();

        // calling those methods should not be a problem too!
        pluginManager.notifyClientStart();
        pluginManager.notifyIncomingBody(mock(Body.class));
        pluginManager.notifyOutgoingBody(mock(Body.class));
        pluginManager.notifyError(mock(Throwable.class));
        pluginManager.notifyClientShutdown();
    }

    private KnxClient newKnxClientMock(final Config config) {
        final var knxClientMock = mock(BaseKnxClient.class);
        when(knxClientMock.getConfig()).thenReturn(config);
        when(knxClientMock.isRunning()).thenReturn(true);
        return knxClientMock;
    }

    private Config newConfigMock() {
        final var configMock = mock(Config.class);
        when(configMock.getPluginExecutorPoolSize()).thenReturn(4);
        when(configMock.getPlugins()).thenReturn(Lists.emptyList());
        return configMock;
    }
}
