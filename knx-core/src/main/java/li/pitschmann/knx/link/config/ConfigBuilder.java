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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import li.pitschmann.knx.link.exceptions.KnxConfigurationException;
import li.pitschmann.knx.link.plugin.Plugin;
import li.pitschmann.utils.Networker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * KNX specific configurations like KNX Net/IP device address. This class can be created
 * once time only and is immutable!
 * <p/>
 * At the moment, a change requires a restart of KNX client and its communication.
 *
 * @author PITSCHR
 */
public final class ConfigBuilder {
    private final static Logger log = LoggerFactory.getLogger(ConfigBuilder.class);

    private final List<Plugin> plugins = new LinkedList<>();
    private final Map<String, Object> settings = new HashMap<>(64);
    private InetAddress remoteControlAddress;
    private int remoteControlPort;
    private boolean routingEnabled;

    /**
     * Creates a builder instance that is subject to end up with {@link ConfigBuilder} instance using
     * {@link #build()} method.
     *
     * @param routingEnabled indicates if the routing or tunneling mode should be used for communication
     * @param address        the address of KNX Net/IP device
     * @param port           the port of KNX Net/IP device (must be within range of 1024 .. 65535)
     */
    private ConfigBuilder(final boolean routingEnabled, final @Nonnull InetAddress address, final int port) {
        Preconditions.checkNotNull(address);
        // accept only 1024 .. 65535, other ports are reserved
        Preconditions.checkArgument(port >= 1024 && port <= 65535,
                "Port is outside of range [1024 .. 65535]: %s", port);

        this.routingEnabled = routingEnabled;
        this.remoteControlAddress = address;
        this.remoteControlPort = port;
    }

    /**
     * Creates a Builder from specified {@code filePath}.
     *
     * @param filePath
     * @return a new instance of {@link ConfigBuilder}
     */
    public static ConfigBuilder create(final @Nonnull Path filePath) {
        return ConfigFileUtil.loadFile(filePath);
    }

    /**
     * Creates a Builder for a customized configuration
     * <p>
     * It supports a lot of different patterns:<br>
     * <ul>
     * <li><strong>Config-arg:</strong><br><i>null</i><br><i>empty</i><br>{@code :}</li>
     * <li><strong>Host and port</strong><br>{@code <host>:<port>"}</li>
     * <li><strong>Host only</strong><br>{@code <host>}<br>{@code <host>:}</li>
     * <li><strong>Port only</strong><br>{@code :<port>}</li>
     * </ul>
     *
     * @param address remote control address (and port)
     * @return a new instance of {@link ConfigBuilder}
     */
    @Nonnull
    public static ConfigBuilder create(final @Nullable String address) {
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
     * Default Port: {@link ConfigConstants#KNX_PORT}<br/>
     *
     * @param address a specified address of KNX Net/IP device
     * @return a new instance of {@link ConfigBuilder}
     */
    @Nonnull
    public static ConfigBuilder create(final @Nonnull InetAddress address) {
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
     * @return a new instance of {@link ConfigBuilder}
     */
    @Nonnull
    public static ConfigBuilder create(final @Nonnull InetAddress address, final int port) {
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
     * Default Address: {@link ConfigConstants#MULTICAST_ADDRESS}<br/>
     * Default Port: {@link ConfigConstants#KNX_PORT}<br/>
     *
     * @return new builder for routing mode with standard settings according to the KNX specification
     */
    @Nonnull
    public static ConfigBuilder routing() {
        return routing(ConfigConstants.MULTICAST_ADDRESS);
    }

    /**
     * Creates a Builder for customized configuration using <strong>ROUTING</strong> mode with
     * customized multicast {@code address} and default KNX port.
     * address
     * <p/>
     * Default Port: {@link ConfigConstants#KNX_PORT}<br/>
     *
     * @param address a specified multicast address of KNX Net/IP device
     * @return new builder for routing mode with customized multicast address
     */
    @Nonnull
    public static ConfigBuilder routing(final @Nonnull InetAddress address) {
        return routing(address, ConfigConstants.KNX_PORT);
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
    public static ConfigBuilder routing(final @Nonnull InetAddress address, final int port) {
        Preconditions.checkNotNull(address);
        Preconditions.checkArgument(address.isMulticastAddress(),
                "Given address is not suitable for routing: %s", address.getHostAddress());
        return new ConfigBuilder(true, address, port);
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode.
     * <p/>
     * Default Address: {@link Networker#getAddressUnbound()} (=will use discovery service)<br/>
     * Default Port: {@link ConfigConstants#KNX_PORT}<br/>
     * Default NAT Flag: {@link ConfigConstants.Executor#NAT}<br/>
     *
     * @return new builder for tunneling mode with standard settings according to the KNX specification
     */
    @Nonnull
    public static ConfigBuilder tunneling() {
        return tunneling(Networker.getAddressUnbound());
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode.
     * Use {@code natEnabled} flag if the Network Address Translation feature should be activated.
     * <p/>
     * Default Address: {@link Networker#getAddressUnbound()} (=will use discovery service)<br/>
     * Default Port: {@link ConfigConstants#KNX_PORT}<br/>
     *
     * @param natEnabled {@code true} if NAT should be enabled
     * @return new builder for tunneling mode with standard settings according to the KNX specification
     */
    @Nonnull
    public static ConfigBuilder tunneling(final boolean natEnabled) {
        return tunneling(Networker.getAddressUnbound(), ConfigConstants.KNX_PORT, natEnabled);
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode with
     * customized {@code address} and default KNX port.
     * address
     * <p/>
     * Default Port: {@link ConfigConstants#KNX_PORT}<br/>
     * Default NAT Flag: {@link ConfigConstants.Executor#NAT}<br/>
     *
     * @param address a specified address of KNX Net/IP device
     * @return new builder for tunneling mode with customized address
     */
    @Nonnull
    public static ConfigBuilder tunneling(final @Nonnull InetAddress address) {
        return tunneling(address, ConfigConstants.KNX_PORT);
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode with
     * customized {@code address} and customized KNX port.
     * <p/>
     * Default NAT Flag: {@link ConfigConstants.Executor#NAT}<br/>
     *
     * @param address a specified address of KNX Net/IP device
     * @param port    a specific port of KNX Net/IP device
     * @return new builder for tunneling mode with customized address
     */
    @Nonnull
    public static ConfigBuilder tunneling(final @Nonnull InetAddress address, final int port) {
        return tunneling(address, port, ConfigConstants.NAT.getDefaultValue());
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode with
     * customized {@code address}, customized KNX port and {@code natEnabled} if the Network
     * Address Translation feature should be used/not used.
     *
     * @param address    a specified address of KNX Net/IP device
     * @param port       a specific port of KNX Net/IP device
     * @param natEnabled {@code true} if NAT should be enabled
     * @return new builder for tunneling mode with customized address
     */
    @Nonnull
    public static ConfigBuilder tunneling(final @Nonnull InetAddress address, final int port, final boolean natEnabled) {
        Preconditions.checkNotNull(address);
        Preconditions.checkArgument(address.isAnyLocalAddress() || !address.isMulticastAddress(),
                "Given address is not suitable for tunneling: %s", address.getHostAddress());
        return new ConfigBuilder(false, address, port).setting(ConfigConstants.NAT, natEnabled);
    }

    /**
     * Adds plugin to be used by KNX client
     *
     * @param plugin      plugin to be provided
     * @param morePlugins optionally an array of plugins can be provided, the array may not contain {@code null} element.
     * @return myself
     */
    @Nonnull
    public ConfigBuilder plugin(final @Nonnull Plugin plugin, final @Nullable Plugin... morePlugins) {
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
     * @param key   key of setting
     * @param value if {@code null}, then default value should be used
     * @return myself
     */
    @Nonnull
    public ConfigBuilder setting(final @Nonnull String key, final @Nullable Object value) {
        Preconditions.checkNotNull(key);
        final var configConstant = ConfigConstants.getConfigConstantByKey(key);
        if (configConstant == null) {
            this.settings.put(key.toLowerCase(), value);
        } else if (value == null) {
            setting(configConstant, null);
        } else {
            // additional check because we want to be ensure that value has right instance type
            final var classType = configConstant.getClassType();
            final var valueClassType = value.getClass();
            if (classType.isInstance(value)) {
                // all good
                setting(configConstant, value);
            } else if (value instanceof String) {
                // try with conversion
                setting(configConstant, configConstant.convert((String) value));
            } else {
                throw new KnxConfigurationException("Instance type of value is '" + valueClassType.getName() + "'. Expected: " + classType.getName());
            }
        }
        return this;
    }

    /**
     * Adds setting to be used by KNX client (and plugins)
     *
     * @param key   key of setting
     * @param value if {@code null}, then default value should be used
     * @return myself
     */
    @Nonnull
    public <T> ConfigBuilder setting(final @Nonnull ConfigConstant<T> key, final @Nullable T value) {
        Preconditions.checkArgument(key.isSettable(),
                "This key is protected and cannot be used for setting: %s", key.getKey());
        Preconditions.checkArgument(value == null || key.isValid(value),
                "The value seems not be applicable for config '%s': %s", key.getKey(), value);

        this.settings.put(key.getKey(), value);
        return this;
    }

    /**
     * Converts the {@link ConfigBuilder} instance into an immutable {@link Config} instance.
     *
     * @return a new instance of {@link Config}
     */
    @Nonnull
    public Config build() {
        return new Config(
                routingEnabled,
                remoteControlAddress,
                remoteControlPort,
                settings,
                plugins
        );
    }
}
