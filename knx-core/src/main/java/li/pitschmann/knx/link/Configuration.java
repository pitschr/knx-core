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
import com.google.common.base.Strings;
import li.pitschmann.knx.link.exceptions.KnxConfigurationException;
import li.pitschmann.knx.link.plugin.Plugin;
import li.pitschmann.utils.Networker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * KNX specific configurations like KNX Net/IP device address. This class can be created
 * once time only and is immutable!
 * <p/>
 * At the moment, a change requires a restart of KNX client and its communication.
 *
 * @author PITSCHR
 */
public final class Configuration {
    private final boolean routingEnabled;
    private final InetAddress remoteControlAddress;
    private final int remoteControlPort;
    private final List<Plugin> plugins;
    private final Map<String, String> settings;

    private Configuration(final @Nonnull Builder builder) {
        // communication type
        this.routingEnabled = builder.routingEnabled;

        // remote endpoint
        this.remoteControlAddress = Objects.requireNonNull(builder.remoteControlAddress);
        this.remoteControlPort = builder.remoteControlPort;

        this.settings = Collections.unmodifiableMap(new HashMap<>(builder.settings));
        this.plugins = Collections.unmodifiableList(new ArrayList<>(builder.plugins));
    }

//    public class ConfigurationEntry {
//        // key   (mandatory)
//        // value  (default: null)
//        // defaultValue (default: null)
//        // string to type conversion  (default: null)
//        // boolean canRuntimeChanged  (default: no)
//        // equals / hashcode / toString (based on key)
//    }

    /**
     * Creates a Builder from specified {@code filePath}.
     *
     * @param filePath
     * @return a new instance of {@link Builder}
     */
    public static Builder create(final @Nonnull Path filePath) {
        Preconditions.checkArgument(Files.isReadable(filePath),
                "The file doesn't exists or is not readable: %s", filePath);

        // read + parse file
        try {
            final var lines = Files.readAllLines(filePath);

            // load them in Properties file?

            // try to figure out endpoint address / port if possible
            // "client.endpoint.address" / "client.endpoint.port"


        } catch (final IOException e) {
            throw new KnxConfigurationException("Cannot read file: " + filePath, e);
        }

        // parse file

        // create Builder
        // TODO: implement  (YAML, JSON, other format?) - e.g. SnakeYaml
        return null;
    }

    /**
     * Creates a Builder for a customized configuration
     *
     * It supports a lot of different patterns:<br>
     * <ul>
     *     <li><strong>No-arg:</strong><br><i>null</i><br><i>empty</i><br>{@code :}</li>
     *     <li><strong>Host and port</strong><br>{@code <host>:<port>"}</li>
     *     <li><strong>Host only</strong><br>{@code <host>}<br>{@code <host>:}</li>
     *     <li><strong>Port only</strong><br>{@code :<port>}</li>
     * </ul>
     *
     * @param address remote control address (and port)
     * @return a new instance of {@link Builder}
     */
    @Nonnull
    public static Builder create(final @Nullable String address) {
        // if address is null/blank then no address is provided and will be picked up using discovery approach
        if (address == null || address.isBlank() || address.trim().equals(":")) {
            return tunneling();
        }
        // address format is: '<host>:'
        else if (address.endsWith(":")) {
            final var addressSplitted = address.split(":");
            Preconditions.checkArgument(addressSplitted.length == 1,
                    "Unsupported Address format provided (expected: '<host>:'): %s", address);
            return create(Networker.getByAddress(addressSplitted[0]));
        }
        // address contains ':' character -> assuming it is <host>:<port>
        else if (address.contains(":")) {
            final var addressSplitted = address.split(":");
            Preconditions.checkArgument(addressSplitted.length == 2,
                    "Unsupported Address format provided (expected: '<host>:<port>'): %s", address);

            final var host = Strings.isNullOrEmpty(addressSplitted[0]) ? Networker.getAddressUnbound() : Networker.getByAddress(addressSplitted[0]);
            final var port = Integer.valueOf(addressSplitted[1]);

            return create(host, port);
        }
        // otherwise assume it is full address (the default KNX port will be used)
        else {
            return create(Networker.getByAddress(address));
        }
    }

    /**
     * Creates a Builder for customized configuration with specific address.
     * <p/>
     * If the given address is a multicast, then routing mode will be used.
     * Otherwise the communication is using the tunneling mode.
     * <p/>
     * Default Port: {@link Constants.Default#KNX_PORT}<br/>
     *
     * @param address a specified address of KNX Net/IP device
     * @return a new instance of {@link Builder}
     */
    @Nonnull
    public static Builder create(final @Nonnull InetAddress address) {
        Preconditions.checkNotNull(address);
        if (address.isMulticastAddress()) {
            return routing(address);
        } else {
            return tunneling(address);
        }
    }

    /**
     * Creates a Builder for customized configuration with specific address and port.
     * <p/>
     * If the given address is a multicast, then routing mode will be used.
     * Otherwise the communication is using the tunneling mode.
     *
     * @param address a specified address of KNX Net/IP device
     * @param port    a specific port of KNX Net/IP device
     * @return a new instance of {@link Builder}
     */
    @Nonnull
    public static Builder create(final @Nonnull InetAddress address, final int port) {
        Preconditions.checkNotNull(address);
        if (address.isMulticastAddress()) {
            return routing(address, port);
        } else {
            return tunneling(address, port);
        }
    }

    /**
     * Creates a Builder for customized configuration using <strong>ROUTING</strong> mode.
     * <p/>
     * Default Address: {@link Constants.Default#MULTICAST_ADDRESS}<br/>
     * Default Port: {@link Constants.Default#KNX_PORT}<br/>
     *
     * @return new builder for routing mode with standard settings according to the KNX specification
     */
    @Nonnull
    public static Builder routing() {
        return routing(Constants.Default.MULTICAST_ADDRESS);
    }

    /**
     * Creates a Builder for customized configuration using <strong>ROUTING</strong> mode with
     * customized multicast {@code address} and default KNX port.
     * address
     * <p/>
     * Default Port: {@link Constants.Default#KNX_PORT}<br/>
     *
     * @param address a specified multicast address of KNX Net/IP device
     * @return new builder for routing mode with customized multicast address
     */
    @Nonnull
    public static Builder routing(final @Nonnull InetAddress address) {
        return routing(address, Constants.Default.KNX_PORT);
    }

    /**
     * Creates a Builder for customized configuration using <strong>ROUTING</strong> mode with
     * customized multicast {@code address} and customized KNX port.
     * address
     *
     * @param address a specified multicast address of KNX Net/IP device
     * @param port    a specific port of KNX Net/IP device
     * @return new builder for routing mode with customized multicast address and port
     */
    @Nonnull
    public static Builder routing(final @Nonnull InetAddress address, final int port) {
        Preconditions.checkNotNull(address);
        Preconditions.checkArgument(address.isMulticastAddress(),
                "Given address is not suitable for routing: %s", address.getHostAddress());
        return new Builder(true, address, port);
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode.
     * <p/>
     * Default Address: {@link Networker#getAddressUnbound()} (=will use discovery service)<br/>
     * Default Port: {@link Constants.Default#KNX_PORT}<br/>
     *
     * @return new builder for tunneling mode with standard settings according to the KNX specification
     */
    @Nonnull
    public static Builder tunneling() {
        return tunneling(Networker.getAddressUnbound());
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode with
     * customized {@code address} and default KNX port.
     * address
     * <p/>
     * Default Port: {@link Constants.Default#KNX_PORT}<br/>
     *
     * @param address a specified address of KNX Net/IP device
     * @return new builder for tunneling mode with customized address
     */
    @Nonnull
    public static Builder tunneling(final @Nonnull InetAddress address) {
        return tunneling(address, Constants.Default.KNX_PORT);
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode with
     * customized {@code address} and customized KNX port.
     *
     * @param address a specified address of KNX Net/IP device
     * @param port    a specific port of KNX Net/IP device
     * @return new builder for tunneling mode with customized address
     */
    @Nonnull
    public static Builder tunneling(final @Nonnull InetAddress address, final int port) {
        Preconditions.checkNotNull(address);
        Preconditions.checkArgument(address.isAnyLocalAddress() || !address.isMulticastAddress(),
                "Given address is not suitable for tunneling: %s", address.getHostAddress());
        return new Builder(false, address, port);
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
     * Returns list of all plug-ins
     *
     * @return unmodifiable list of all {@link Plugin} instances
     */
    @Nonnull
    public List<Plugin> getPlugins() {
        return this.plugins;
    }

    /**
     * Returns the setting for given {@code key}. Defaults back to {@code defaultValue} in case the
     * value of key is not defined or unknown.
     *
     * @param key          configuration key
     * @param defaultValue used if value for key is absent
     * @param function     for conversion from {@link String} to an instance of {@code T}
     * @param <T>
     * @return the value of setting (key), may be {@code null} if undefined
     */
    @Nullable
    public <T> T getSetting(final @Nonnull String key, final @Nullable T defaultValue, final @Nonnull Function<String, T> function) {
        final var value = this.settings.get(Objects.requireNonNull(key));
        return value == null ? defaultValue : function.apply(value);
    }

    /**
     * Returns the setting for given {@code key}. Defaults back to {@code defaultValue} in case the
     * value of key is not defined or unknown.
     *
     * @param key          configuration key
     * @param defaultValue used if value for key is absent
     * @param function     for conversion from {@link String} to an instance of {@code T}
     * @param <T>
     * @return the value of setting (key), may be {@code null} if undefined
     */
    @Nullable
    public <T> T getSetting(final @Nonnull Constants.ConfigurationKey key, final @Nullable T defaultValue, final @Nonnull Function<String, T> function) {
        return getSetting(key.getKey(), defaultValue, function);
    }

    //
    // client.plugin
    //

    public int getPluginExecutorPoolSize() {
        return getSetting(Constants.ConfigurationKey.PLUGIN_EXECUTOR_POOL_SIZE, Constants.Default.PLUGIN_POOL_SIZE, Integer::valueOf);
    }

    //
    // client.communication
    //

    public int getCommunicationExecutorPoolSize() {
        return getSetting(Constants.ConfigurationKey.COMMUNICATION_EXECUTOR_POOL_SIZE, Constants.Default.COMMUNICATION_POOL_SIZE, Integer::valueOf);
    }

    public boolean isNatEnabled() {
        return getSetting(Constants.ConfigurationKey.NAT, Constants.Default.NAT_ENABLED, Boolean::valueOf);
    }

    //
    // client.communication.control
    //

    public int getControlChannelPort() {
        return getSetting(Constants.ConfigurationKey.CONTROL_CHANNEL_PORT, 0, Integer::valueOf);
    }

    public long getSocketTimeoutControlChannel() {
        return getSetting(Constants.ConfigurationKey.CONTROL_CHANNEL_SOCKET_TIMEOUT, Constants.Times.CONTROL_CHANNEL_SOCKET_TIMEOUT, Long::valueOf);
    }

    //
    // client.communication.data
    //

    public int getDataChannelPort() {
        return getSetting(Constants.ConfigurationKey.DATA_CHANNEL_PORT, 0, Integer::valueOf);
    }

    public long getSocketTimeoutDataChannel() {
        return getSetting(Constants.ConfigurationKey.DATA_CHANNEL_SOCKET_TIMEOUT, Constants.Times.DATA_CHANNEL_SOCKET_TIMEOUT, Long::valueOf);
    }

    //
    // client.communication.discovery
    //

    public long getTimeoutDiscoveryRequest() {
        return getSetting(Constants.ConfigurationKey.DISCOVERY_REQUEST_TIMEOUT, Constants.Times.SEARCH_REQUEST_TIMEOUT, Long::valueOf);
    }

    //
    // client.communication.multicast
    //

    @Nonnull
    public InetAddress getMulticastChannelAddress() {
        return getSetting(Constants.ConfigurationKey.MULTICAST_ADDRESS, Constants.Default.MULTICAST_ADDRESS, Networker::getByAddress);
    }

    public int getMulticastChannelPort() {
        return getSetting(Constants.ConfigurationKey.MULTICAST_PORT, Constants.Default.KNX_PORT, Integer::valueOf);
    }

    public long getSocketTimeoutMulticastChannel() {
        return getSetting(Constants.ConfigurationKey.MULTICAST_SOCKET_TIMEOUT, Constants.Times.MULTICAST_CHANNEL_SOCKET_TIMEOUT, Long::valueOf);
    }

    public int getMulticastTTL() {
        return getSetting(Constants.ConfigurationKey.MULTICAST_TTL, 4, Integer::valueOf);
    }

    //
    // client.communication.description
    //

    public int getDescriptionChannelPort() {
        return getSetting(Constants.ConfigurationKey.DESCRIPTION_CHANNEL_PORT, 0, Integer::valueOf);
    }

    public long getTimeoutDescriptionRequest() {
        return getSetting(Constants.ConfigurationKey.DESCRIPTION_REQUEST_TIMEOUT, Constants.Times.DESCRIPTION_REQUEST_TIMEOUT, Long::valueOf);
    }

    public long getSocketTimeoutDescriptionChannel() {
        return getSetting(Constants.ConfigurationKey.DESCRIPTION_SOCKET_TIMEOUT, Constants.Times.DESCRIPTION_CHANNEL_SOCKET_TIMEOUT, Long::valueOf);
    }

    //
    // client.communication.disconnect
    //

    public long getTimeoutDisconnectRequest() {
        return getSetting(Constants.ConfigurationKey.DISCONNECT_REQUEST_TIMEOUT, Constants.Times.DISCONNECT_REQUEST_TIMEOUT, Long::valueOf);
    }

    public long getTimeoutDisconnectResponse() {
        return getSetting(Constants.ConfigurationKey.DISCONNECT_RESPONSE_TIMEOUT, Constants.Times.DISCONNECT_RESPONSE_TIMEOUT, Long::valueOf);
    }

    //
    // client.communication.connect
    //

    public long getTimeoutConnectRequest() {
        return getSetting(Constants.ConfigurationKey.CONNECT_REQUEST_TIMEOUT, Constants.Times.CONNECT_REQUEST_TIMEOUT, Long::valueOf);
    }

    //
    // client.communication.connectionState
    //

    public long getTimeoutConnectionStateRequest() {
        return getSetting(Constants.ConfigurationKey.CONNECTIONSTATE_REQUEST_TIMEOUT, Constants.Times.CONNECTIONSTATE_REQUEST_TIMEOUT, Long::valueOf);
    }

    public long getTimeoutAliveConnection() {
        return getSetting(Constants.ConfigurationKey.CONNECTIONSTATE_ALIVE_TIMEOUT, Constants.Times.CONNECTION_ALIVE_TIME, Long::valueOf);
    }

    public long getIntervalConnectionState() {
        return getSetting(Constants.ConfigurationKey.CONNECTIONSTATE_CHECK_INTERVAL, Constants.Times.CONNECTIONSTATE_CHECK_INTERVAL, Long::valueOf);
    }

    //
    // DAEMON
    //

    public int getDaemonPort() {
        return getSetting(Constants.ConfigurationKey.DAEMON_PORT, Constants.Default.HTTP_DAEMON_PORT, Integer::valueOf);
    }

    @Nullable
    public Path getProjectPath() {
        return getSetting(Constants.ConfigurationKey.DAEMON_PROJECT_PATH, null, Paths::get);
    }

    /**
     * Builder for the Configuration
     *
     * @author PITSCHR
     */
    public static class Builder {
        private final List<Plugin> plugins = new LinkedList<>();
        private final Map<String, String> settings = new HashMap<>(64);
        private InetAddress remoteControlAddress;
        private int remoteControlPort;
        private boolean routingEnabled;

        /**
         * Creates a builder instance that is subject to end up with {@link Configuration} instance using
         * {@link #build()} method.
         *
         * @param routingEnabled indicates if the routing or tunneling mode should be used for communication
         * @param address the address of KNX Net/IP device
         * @param port the port of KNX Net/IP device (must be within range of 1024 .. 65535)
         */
        private Builder(final boolean routingEnabled, final @Nonnull InetAddress address, final int port) {
            // accept only 1024 .. 65535, other ports are reserved
            Preconditions.checkArgument(port >= 1024 && port <= 65535,
                    "Port is outside of range [1024 .. 65535]: %s", port);

            this.routingEnabled = routingEnabled;
            this.remoteControlAddress = Objects.requireNonNull(address);
            this.remoteControlPort = port;
        }

        /**
         * Enables the Network Address Translation (NAT). This can be used with tunneling mode only.
         *
         * @return myself
         */
        @Nonnull
        public Builder nat() {
            Preconditions.checkState(!this.routingEnabled, "NAT can be enabled in tunneling mode only!");
            setting(Constants.ConfigurationKey.NAT, "true");
            return this;
        }

        /**
         * Adds plugin to be used by KNX client
         *
         * @param plugin plugin to be provided
         * @param morePlugins optionally an array of plugins can be provided, the array may not contain {@code null} element.
         * @return myself
         */
        @Nonnull
        public Builder plugin(final @Nonnull Plugin plugin, final @Nullable Plugin... morePlugins) {
            this.plugins.add(Objects.requireNonNull(plugin));
            if (morePlugins != null) {
                for (final Plugin morePlugin : morePlugins) {
                    this.plugins.add(Objects.requireNonNull(morePlugin));
                }
            }
            return this;
        }

        /**
         * Adds setting to be used by KNX client (and plugins)
         *
         * @param key key of setting
         * @param value if {@code null}, then default value should be used
         * @return myself
         */
        @Nonnull
        public Builder setting(final @Nonnull String key, final @Nullable String value) {
            // we have following protected keys and those cannot be set using this method
            Preconditions.checkArgument(!(
                            Constants.ConfigurationKey.ENDPOINT_ADDRESS.getKey().equalsIgnoreCase(key)
                    || Constants.ConfigurationKey.ENDPOINT_PORT.getKey().equalsIgnoreCase(key)
                    ), "This setting is protected: %s", key);

            this.settings.put(Objects.requireNonNull(key), value);
            return this;
        }

        /**
         * Adds setting to be used by KNX client (and plugins)
         *
         * @param key key of setting
         * @param value if {@code null}, then default value should be used
         * @return myself
         */
        @Nonnull
        public Builder setting(final @Nonnull Constants.ConfigurationKey key, final @Nullable String value) {
            return setting(key.getKey(), value);
        }

        /**
         * Converts the {@link Builder} instance into an immutable {@link Configuration} instance.
         *
         * @return a new instance of {@link Configuration}
         */
        @Nonnull
        public Configuration build() {
            return new Configuration(this);
        }
    }
}
