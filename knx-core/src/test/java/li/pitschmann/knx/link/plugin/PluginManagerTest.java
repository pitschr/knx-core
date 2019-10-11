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

import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;
import li.pitschmann.knx.link.communication.BaseKnxClient;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.config.Config;
import li.pitschmann.knx.link.config.ConfigBuilder;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.utils.Sleeper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        final var observerPluginMock = mock(ObserverPlugin.class);
        final var extensionPluginMock = mock(ExtensionPlugin.class);

        // 1) register two plugins (via config)
        final var config = ConfigBuilder.tunneling().plugin(observerPluginMock, extensionPluginMock).build();
        final var knxClientMock = newKnxClientMock(config);
        final var pluginManager = new PluginManager(config);

        // 2) initialization
        pluginManager.notifyInitialization(knxClientMock);
        Sleeper.milliseconds(50); // wait bit, as plugin executor is notifying the plugins

        // 3) verify
        verify(observerPluginMock).onInitialization(any(KnxClient.class));
        verify(extensionPluginMock).onInitialization(any(KnxClient.class));
    }

    @Test
    @DisplayName("Test lazy initialization of plugins")
    public void testLazyOnInitialization() {
        final var observerPluginMock = mock(ObserverPlugin.class);
        final var extensionPluginMock = mock(ExtensionPlugin.class);

        final var configMock = newConfigMock();
        final var pluginManager = new PluginManager(configMock);
        final var knxClientMock = newKnxClientMock(configMock);

        // 1) initialization (no plugin registered yet)
        pluginManager.notifyInitialization(knxClientMock);
        Sleeper.milliseconds(50); // wait bit, as plugin executor is notifying the plugins

        // 2 verify (init method never called)
        verify(observerPluginMock, never()).onInitialization(any(KnxClient.class));
        verify(extensionPluginMock, never()).onInitialization(any(KnxClient.class));

        // 3) register two plugins
        pluginManager.registerPlugin(observerPluginMock);
        pluginManager.registerPlugin(extensionPluginMock);
        Sleeper.milliseconds(50); // wait bit, as plugin executor is notifying the plugins

        // 4) verify (init method should be called)
        verify(observerPluginMock).onInitialization(any(KnxClient.class));
        verify(extensionPluginMock).onInitialization(any(KnxClient.class));
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
    @DisplayName("Error: Test registration by a wrong JAR path")
    public void testNonexistentJARPath() {
        final var configMock = newConfigMock();
        final var pluginManager = new PluginManager(configMock);

        // wrong JAR file path
        final var pathToJAR = Paths.get("src/test/resources/plugin/this-file-does-not-exists.jar");
        final var className = "my.plugin.MyTestPlugin";
        assertThatThrownBy(() -> pluginManager.registerPlugin(pathToJAR, className)).isInstanceOf(KnxException.class);
    }

    // @Test
    @DisplayName("Error: Test registration by a non-existent plugin")
    public void testNonexistentPlugin() {
        final var configMock = newConfigMock();
        final var pluginManager = new PluginManager(configMock);

        // wrong JAR file path
        final var pathToJAR = Paths.get("src/test/resources/plugin/my-test-plugin.jar");
        final var className = "my.plugin.MyTestPluginTHATDOESNOTEXISTS";
        assertThatThrownBy(() -> pluginManager.registerPlugin(pathToJAR, className)).isInstanceOf(KnxException.class);
    }

    // @Test
    @DisplayName("OK: Test registration of plugin by JAR file")
    public void testJARPlugin() {
        final var pathToJAR = Paths.get("src/test/resources/plugin/my-test-plugin.jar");
        final var className = "my.plugin.MyTestPlugin";

        final var configMock = newConfigMock();
        final var pluginManager = new PluginManager(configMock);

        pluginManager.registerPlugin(pathToJAR, className);
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
