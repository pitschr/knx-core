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

package li.pitschmann.knx.core.config;

import li.pitschmann.knx.core.knxproj.XmlProject;
import li.pitschmann.knx.core.plugin.Plugin;
import li.pitschmann.knx.core.utils.Networker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final List<Class<Plugin>> plugins;
    private final Map<ConfigValue<?>, Object> settings;
    private final XmlProject xmlProject;

    Config(final boolean routingEnabled,
           final InetAddress remoteControlAddress,
           final int remoteControlPort,
           final Map<ConfigValue<?>, Object> settings,
           final List<Class<Plugin>> pluginClasses) {
        // communication type
        this.routingEnabled = routingEnabled;

        // remote endpoint
        this.remoteControlAddress = Objects.requireNonNull(remoteControlAddress);
        this.remoteControlPort = remoteControlPort;

        // plugins
        this.plugins = List.copyOf(pluginClasses);

        // defensive copy of custom settings
        this.settings = Map.copyOf(settings);

        // try to parse the project file
        final var projectPath = getProjectPath();
        XmlProject tmpXmlProject;
        try {
            tmpXmlProject = Files.isReadable(projectPath) ? XmlProject.parse(projectPath) : null;
        } catch (final Throwable t) {
            log.warn("Could not parse KNX Project file: {}. Omitted!", projectPath, t);
            tmpXmlProject = null;
        }
        this.xmlProject = tmpXmlProject;
    }

    /**
     * Returns the actual value for given {@code configValue}
     *
     * @param configValue the key to be used to find the value
     * @param <T>
     * @return the value of {@link ConfigValue}
     */
    public <T> T getValue(final ConfigValue<T> configValue) {
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
     * @return unmodifiable list of all {@link Plugin} classes
     */
    public List<Class<Plugin>> getPlugins() {
        return this.plugins;
    }

    /**
     * Returns if Network Address Translation (NAT) is enabled. Only used when tunneling.
     * @return {@code true} if enabled, otherwise {@code false}
     */
    public boolean isNatEnabled() {
        return getValue(CoreConfigs.NAT);
    }

    /**
     * Returns the path of *.knxproj (KNX Project)
     *
     * @return the path to KNX project
     */
    public Path getProjectPath() {
        return getValue(CoreConfigs.PROJECT_PATH);
    }

    /**
     * Returns the parsed KNX Project file from {@link #getProjectPath()}.
     * May be {@code null} if the file doesn't exists, was not readable nor parsable.
     *
     * @return an instance of {@link XmlProject}, may be {@code null}
     */
    @Nullable
    public XmlProject getProject() {
        return this.xmlProject;
    }
}
