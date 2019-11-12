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

package li.pitschmann.knx.link.config;

import li.pitschmann.knx.link.exceptions.KnxConfigurationException;
import li.pitschmann.knx.link.plugin.Plugin;
import li.pitschmann.knx.parser.KnxprojParser;
import li.pitschmann.knx.parser.XmlProject;
import li.pitschmann.utils.Networker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable Config
 */
public final class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private final boolean routingEnabled;
    private final InetAddress remoteControlAddress;
    private final int remoteControlPort;
    private final List<Plugin> plugins;
    private final Map<ConfigValue<?>, Object> settings;
    private final XmlProject xmlProject;

    Config(final boolean routingEnabled,
           final @Nonnull InetAddress remoteControlAddress,
           final int remoteControlPort,
           final @Nonnull Map<ConfigValue<?>, Object> settings,
           final @Nonnull List<Class<Plugin>> pluginClasses) {
        // communication type
        this.routingEnabled = routingEnabled;

        // remote endpoint
        this.remoteControlAddress = Objects.requireNonNull(remoteControlAddress);
        this.remoteControlPort = remoteControlPort;

        // plugins
        final var plugins = new ArrayList<Plugin>(pluginClasses.size());
        for (final var pluginClass : pluginClasses) {
            try {
                final var plugin = pluginClass.getDeclaredConstructor().newInstance();
                plugins.add(plugin);
            } catch (final ReflectiveOperationException ex) {
                throw new KnxConfigurationException("Could not instantiate plugin: " + pluginClass);
            }
        }
        this.plugins = List.copyOf(plugins);

        // defensive copy of custom settings
        this.settings = Collections.unmodifiableMap(new HashMap<>(settings));

        // try to parse the project file
        final var projectPath = getProjectPath();
        XmlProject tmpXmlProject;
        try {
            tmpXmlProject = Files.isReadable(projectPath) ? KnxprojParser.parse(projectPath) : null;
        } catch (final Throwable t) {
            log.warn("Could not parse KNX Project file: {}. Omitted!", projectPath, t);
            tmpXmlProject = null;
        }
        this.xmlProject = tmpXmlProject;
    }

    /**
     * Returns the setting for given {@code configValue}. If key is not known, then a
     * {@link KnxConfigurationException} will be thrown.
     *
     * @param configValue the key to be used to find the value
     * @param <T>
     * @return the value of setting (key)
     * @throws KnxConfigurationException if value of setting could not be found
     */
    @Nonnull
    public <T> T getSetting(final @Nonnull ConfigValue<T> configValue) {
        final var value = this.settings.get(Objects.requireNonNull(configValue));
        if (value == null) {
            return configValue.getDefaultValue();
        } else {
            return configValue.getClassType().cast(value);
        }
    }

    /**
     * Remote Control Endpoint address of KNX Net/IP device to be connected.
     *
     * @return {@link InetAddress}, if {@link Networker#getAddressUnbound()} then discovery service will be used
     */
    @Nonnull
    public InetAddress getRemoteControlAddress() {
        return this.remoteControlAddress;
    }

    /**
     * Remote Control Endpoint port of KNX Net/IP device to be connected
     *
     * @return port
     */
    public int getRemoteControlPort() {
        return this.remoteControlPort;
    }

    /**
     * Returns if the routing is enabled.
     *
     * @return {@code true} if routing is enabled, otherwise {@code false}
     */
    public boolean isRoutingEnabled() {
        return routingEnabled;
    }

    /**
     * Returns list of all plug-in classes
     *
     * @return unmodifiable list of all {@link Plugin}
     */
    @Nonnull
    public List<Plugin> getPlugins() {
        return this.plugins;
    }

    //
    // client.plugin
    //

    public int getPluginExecutorPoolSize() {
        return getSetting(ConfigConstants.Executor.PLUGIN_POOL_SIZE);
    }

    //
    // client.communication
    //

    public int getCommunicationExecutorPoolSize() {
        return getSetting(ConfigConstants.Executor.COMMUNICATION_POOL_SIZE);
    }

    public boolean isNatEnabled() {
        return getSetting(ConfigConstants.NAT);
    }

    //
    // client.communication.control
    //

    public int getControlChannelPort() {
        return getSetting(ConfigConstants.Control.PORT);
    }

    public long getSocketTimeoutControlChannel() {
        return getSetting(ConfigConstants.Control.SOCKET_TIMEOUT);
    }

    //
    // client.communication.data
    //

    public int getDataChannelPort() {
        return getSetting(ConfigConstants.Data.PORT);
    }

    public long getTimeoutDataRequest() {
        return getSetting(ConfigConstants.Data.DATA_REQUEST_TIMEOUT);
    }

    public long getSocketTimeoutDataChannel() {
        return getSetting(ConfigConstants.Data.SOCKET_TIMEOUT);
    }

    //
    // client.communication.discovery
    //

    public long getTimeoutDiscoveryRequest() {
        return getSetting(ConfigConstants.Search.REQUEST_TIMEOUT);
    }

    //
    // client.communication.multicast
    //

    @Nonnull
    public InetAddress getMulticastChannelAddress() {
        return getSetting(ConfigConstants.Multicast.ADDRESS);
    }

    public int getMulticastChannelPort() {
        return getSetting(ConfigConstants.Multicast.PORT);
    }

    public long getSocketTimeoutMulticastChannel() {
        return getSetting(ConfigConstants.Multicast.SOCKET_TIMEOUT);
    }

    public int getMulticastTTL() {
        return getSetting(ConfigConstants.Multicast.TIME_TO_LIVE);
    }

    //
    // client.communication.description
    //

    public int getDescriptionChannelPort() {
        return getSetting(ConfigConstants.Description.PORT);
    }

    public long getTimeoutDescriptionRequest() {
        return getSetting(ConfigConstants.Description.REQUEST_TIMEOUT);
    }

    public long getSocketTimeoutDescriptionChannel() {
        return getSetting(ConfigConstants.Description.SOCKET_TIMEOUT);
    }

    //
    // client.communication.disconnect
    //

    public long getTimeoutDisconnectRequest() {
        return getSetting(ConfigConstants.Disconnect.REQUEST_TIMEOUT);
    }

    public long getTimeoutDisconnectResponse() {
        return getSetting(ConfigConstants.Disconnect.RESPONSE_TIMEOUT);
    }

    //
    // client.communication.connect
    //

    public long getTimeoutConnectRequest() {
        return getSetting(ConfigConstants.Connect.REQUEST_TIMEOUT);
    }

    //
    // client.communication.connectionState
    //

    public long getTimeoutConnectionStateRequest() {
        return getSetting(ConfigConstants.ConnectionState.REQUEST_TIMEOUT);
    }

    public long getTimeoutHeartbeatConnectionState() {
        return getSetting(ConfigConstants.ConnectionState.HEARTBEAT_TIMEOUT);
    }

    public long getIntervalConnectionState() {
        return getSetting(ConfigConstants.ConnectionState.CHECK_INTERVAL);
    }

    //
    // DAEMON
    //

    public int getDaemonPort() {
        return getSetting(ConfigConstants.HTTP_DAEMON_PORT);
    }

    /**
     * Returns the path of *.knxproj (KNX Project)
     *
     * @return the path to KNX project
     */
    @Nonnull
    public Path getProjectPath() {
        return getSetting(ConfigConstants.PROJECT_PATH);
    }

    /**
     * Returns the parsed KNX Project file from {@link #getProjectPath()}.
     * May be {@code null} if the file was not parsable.
     *
     * @return an instance of {@link XmlProject} or {@code null} if not readable
     */
    @Nullable
    public XmlProject getProject() {
        return this.xmlProject;
    }
}
