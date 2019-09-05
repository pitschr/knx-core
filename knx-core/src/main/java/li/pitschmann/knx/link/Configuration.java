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

package li.pitschmann.knx.link;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.link.plugin.Plugin;
import li.pitschmann.utils.Networker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * KNX specific configurations like KNX Net/IP device address. This class can be created
 * once time only and is immutable!
 * <p/>
 * At the moment, a change requires a restart of KNX client and its communication.
 *
 * @author PITSCHR
 */
public final class Configuration {
    private final InetAddress remoteControlAddress;
    private final Integer remoteControlPort;
    private final List<Plugin> allPlugins;
    private final List<ExtensionPlugin> extensionPlugins;
    private final List<ObserverPlugin> observerPlugins;
    private final Map<String, String> settings;

    private Configuration(final @Nonnull Builder builder) {
        // control endpoint
        this.remoteControlAddress = builder.remoteControlAddress;
        this.remoteControlPort = builder.remoteControlPort;
        // settings
        this.settings = Collections.unmodifiableMap(builder.settings);
        // plugins
        this.allPlugins = Collections.unmodifiableList(builder.plugins);
        this.extensionPlugins = builder.plugins.stream().filter(ExtensionPlugin.class::isInstance).map(ExtensionPlugin.class::cast)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        this.observerPlugins = builder.plugins.stream().filter(ObserverPlugin.class::isInstance).map(ObserverPlugin.class::cast)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    /**
     * Creates a Builder for a customized configuration
     *
     * @param address remote control address (and port)
     * @return {@link Builder}
     */
    public static Builder create(final @Nullable String address) {
        // if address is null/blank then no address is provided and will be picked up using discovery approach
        if (address == null || address.isBlank()) {
            return create();
        }
        // address contains ':' character -> assuming it is <host>:<port>
        else if (address.contains(":")) {
            final var addressSplitted = address.split(":");
            Preconditions.checkArgument(addressSplitted.length == 2, "Unsupported Address provided.");

            final var host = addressSplitted[0];
            final var port = Integer.parseInt(addressSplitted[1]);

            return create(Networker.getByAddress(host), port);
        }
        // otherwise assume it is full address (the default KNX port will be used)
        else {
            return create(Networker.getByAddress(address));
        }
    }

    /**
     * Creates a Builder for a customized configuration
     *
     * @param address remote control address
     * @return {@link Builder}
     */
    public static Builder create(final @Nullable InetAddress address) {
        return create(address, Constants.Default.KNX_PORT);
    }

    /**
     * Creates a Builder for a customized configuration
     *
     * @param address remote control address
     * @param port    remote control port
     * @return {@link Builder}
     */
    public static Builder create(final @Nullable InetAddress address, final int port) {
        if (address == null) {
            return create();
        } else {
            return new Builder().control(address, port);
        }
    }

    /**
     * Creates a Builder for a customized configuration.
     * The endpoint of KNX Net/IP device will be discovered and the first applicable device is taken
     *
     * @return {@link Builder}
     */
    public static Builder create() {
        return new Builder();
    }

    /**
     * Remote Control Endpoint address of KNX Net/IP device to be connected
     *
     * @return {@link InetAddress}
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
        return getSetting(this.remoteControlPort, Constants.Default.KNX_PORT);
    }

    /**
     * Returns list of all plug-ins
     *
     * @return unmodifiable list of all {@link Plugin} instances
     */
    @Nonnull
    public List<Plugin> getAllPlugins() {
        return this.allPlugins;
    }

    /**
     * Returns list of observer plug-ins
     *
     * @return unmodifiable list of {@link ObserverPlugin}
     */
    @Nonnull
    public List<ObserverPlugin> getObserverPlugins() {
        return this.observerPlugins;
    }

    /**
     * Returns list of extension plug-ins
     *
     * @return unmodifiable list of {@link ExtensionPlugin}
     */
    @Nonnull
    public List<ExtensionPlugin> getExtensionPlugins() {
        return this.extensionPlugins;
    }

    /**
     * Returns the setting for given {@code key}. Defaults back to {@code defaultValue} in case the
     * key is not defined or unknown.
     *
     * @param key          configuration key
     * @param defaultValue used if value for key is absent
     * @param function     for conversion from {@link String} to an instance of {@code T}
     * @param <T>
     * @return the value of setting (key)
     */
    @Nullable
    private <T> T getSetting(final @Nonnull String key, final @Nullable T defaultValue, final @Nonnull Function<String, T> function) {
        final var value = this.settings.get(Objects.requireNonNull(key));
        return value == null ? defaultValue : function.apply(value);
    }

    /**
     * Returns the setting for given {@code value}. Defaults back to {@code defaultValue} in case the
     * value is {@code null}
     *
     * @param value        configuration value
     * @param defaultValue used if configuration value is absent
     * @param <T>
     * @return the value of configuration
     */
    @Nullable
    private <T> T getSetting(final @Nullable T value, final @Nullable T defaultValue) {
        return value == null ? defaultValue : value;
    }


    /**
     * Remote Discovery Endpoint address to find KNX Net/IP devices
     *
     * @return {@link InetAddress}
     */
    @Nonnull
    public InetAddress getRemoteDiscoveryAddress() {
        return getSetting("endpoint.discovery.address", Constants.Default.KNX_MULTICAST_ADDRESS, Networker::getByAddress);
    }

    /**
     * Remote Discovery Endpoint port to find KNX Net/IP devices
     *
     * @return port
     */
    public int getRemoteDiscoveryPort() {
        return getSetting("endpoint.discovery.port", Constants.Default.KNX_PORT, Integer::valueOf);
    }

    public int getPluginExecutorPoolSize() {
        return Math.max(this.getSetting("executor.pool.plugin", Constants.Default.PLUGIN_POOL_SIZE, Integer::valueOf), this.observerPlugins.size());
    }

    public int getCommunicationExecutorPoolSize() {
        return this.getSetting("executor.pool.communication", Constants.Default.COMMUNICATION_POOL_SIZE, Integer::valueOf);
    }

    public long getIntervalConnectionState() {
        return getSetting("interval.connectionstate", Constants.Interval.CONNECTIONSTATE, Long::valueOf);
    }

    public long getIntervalEvent() {
        return getSetting("interval.event", Constants.Interval.EVENT, Long::valueOf);
    }

    public long getSocketTimeoutDiscoveryChannel() {
        return getSetting("timeout.socket.discovery", Constants.Timeouts.DISCOVERY_CHANNEL_SOCKET_TIMEOUT, Long::valueOf);
    }

    public long getSocketTimeoutDescriptionChannel() {
        return getSetting("timeout.socket.description", Constants.Timeouts.DESCRIPTION_CHANNEL_SOCKET_TIMEOUT, Long::valueOf);
    }

    public long getSocketTimeoutControlChannel() {
        return getSetting("timeout.socket.control", Constants.Timeouts.CONTROL_CHANNEL_SOCKET_TIMEOUT, Long::valueOf);
    }

    public long getSocketTimeoutDataChannel() {
        return getSetting("timeout.socket.data", Constants.Timeouts.DATA_CHANNEL_SOCKET_TIMEOUT, Long::valueOf);
    }

    public long getTimeoutDiscoveryRequest() {
        return getSetting("timeout.request.discovery", Constants.Timeouts.SEARCH_REQUEST_TIMEOUT, Long::valueOf);
    }

    public long getTimeoutDescriptionRequest() {
        return getSetting("timeout.request.description", Constants.Timeouts.DESCRIPTION_REQUEST_TIMEOUT, Long::valueOf);
    }

    public long getTimeoutConnectRequest() {
        return getSetting("timeout.request.connect", Constants.Timeouts.CONNECT_REQUEST_TIMEOUT, Long::valueOf);
    }

    public long getTimeoutDisconnectRequest() {
        return getSetting("timeout.request.disconnect", Constants.Timeouts.DISCONNECT_REQUEST_TIMEOUT, Long::valueOf);
    }

    public long getTimeoutDisconnectResponse() {
        return getSetting("timeout.response.disconnect", Constants.Timeouts.DISCONNECT_RESPONSE_TIMEOUT, Long::valueOf);
    }

    public long getTimeoutConnectionStateRequest() {
        return getSetting("timeout.request.connectionstate", Constants.Timeouts.CONNECTIONSTATE_REQUEST_TIMEOUT, Long::valueOf);
    }

    public long getTimeoutAliveConnection() {
        return getSetting("timeout.alive.connectionstate", Constants.Timeouts.CONNECTION_ALIVE_TIME, Long::valueOf);
    }

    public int getDaemonPort() {
        return getSetting("daemon.port.http", Constants.Default.HTTP_DAEMON_PORT, Integer::valueOf);
    }

    @Nullable
    public Path getProjectPath() {
        return getSetting("daemon.path.knxproj", null, Paths::get);
    }

    public int getDiscoveryChannelPort() {
        return getSetting("client.channel.discovery.port", 0, Integer::valueOf);
    }

    public int getDiscoveryMulticastTTL() {
        return getSetting("client.channel.discovery.ttl", 4, Integer::valueOf);
    }

    public int getDescriptionChannelPort() {
        return getSetting("client.channel.description.port", 0, Integer::valueOf);
    }

    public int getControlChannelPort() {
        return getSetting("client.channel.control.port", 0, Integer::valueOf);
    }

    public int getDataChannelPort() {
        return getSetting("client.channel.data.port", 0, Integer::valueOf);
    }

    public boolean isNatEnabled() {
        return getSetting("client.nat.enabled", Constants.Default.NAT_ENABLED, Boolean::valueOf);
    }

    /**
     * Builder for the Configuration
     *
     * @author PITSCHR
     */
    public static class Builder {
        private final List<Plugin> plugins = Lists.newLinkedList();
        private final Map<String, String> settings = Maps.newHashMap();
        private InetAddress remoteControlAddress;
        private Integer remoteControlPort;

        private Builder() {
            // empty
        }

        /**
         * Defines the control endpoint of KNX Net/IP device
         *
         * @param address remote control endpoint address
         * @param port    remote control endpoint port
         * @return myself
         */
        @Nonnull
        private Builder control(final @Nonnull InetAddress address, final int port) {
            Preconditions.checkNotNull(address);
            // accept only 1024 .. 65535, other ports are reserved
            Preconditions.checkArgument(port >= 1024 && port <= 65535, "Illegal Port for endpoint provided.");

            this.remoteControlAddress = address;
            this.remoteControlPort = port;
            return this;
        }

        @Nonnull
        public Builder plugin(final @Nonnull Plugin plugin, final @Nullable Plugin... morePlugins) {
            this.plugins.add(Objects.requireNonNull(plugin));
            for (final Plugin morePlugin : morePlugins) {
                this.plugins.add(Objects.requireNonNull(morePlugin));
            }
            return this;
        }

        @Nonnull
        public Builder setting(final @Nonnull String key, final @Nullable String value) {
            this.settings.put(Objects.requireNonNull(key), value);
            return this;
        }

        @Nonnull
        public Configuration build() {
            return new Configuration(this);
        }
    }
}
