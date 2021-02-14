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

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.plugin.Plugin;
import li.pitschmann.knx.core.utils.Maps;
import li.pitschmann.knx.core.utils.Networker;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * KNX specific configurations like KNX Net/IP device address. This class can be created
 * once time only and is immutable!
 * <p>
 * At the moment, a change requires a restart of KNX client and its communication.
 *
 * @author PITSCHR
 */
public final class ConfigBuilder {
    private static final Logger log = LoggerFactory.getLogger(ConfigBuilder.class);
    private final List<Plugin> plugins = new LinkedList<>();
    private final Map<ConfigValue<?>, Object> settings = Maps.newHashMap(100);
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
    private ConfigBuilder(final boolean routingEnabled, final InetAddress address, final int port) {
        Preconditions.checkNonNull(address);
        // accept only 1024 .. 65535, other ports are reserved
        Preconditions.checkArgument(port >= 1024 && port <= 65535,
                "Port is outside of range [1024 .. 65535]: {}", port);

        this.routingEnabled = routingEnabled;
        this.remoteControlAddress = address;
        this.remoteControlPort = port;
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
    public static ConfigBuilder create(final @Nullable String address) {
        // if address is null/blank then no address is provided and will be picked up using discovery approach
        if (address == null || address.isBlank() || address.trim().equals(":")) {
            return tunneling();
        }
        // address format is: '<host>:'
        else if (address.endsWith(":")) {
            final var addressSplitted = address.split(":");
            Preconditions.checkArgument(addressSplitted.length == 1,
                    "Unsupported Address format provided (expected: '<host>:'): {}", address);
            return create(Networker.getByAddress(addressSplitted[0]));
        }
        // address contains ':' character -> assuming it is <host>:<port>
        else if (address.contains(":")) {
            final var addressSplitted = address.split(":");
            Preconditions.checkArgument(addressSplitted.length == 2,
                    "Unsupported Address format provided (expected: '<host>:<port>'): {}", address);

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
     * <p>
     * If the given address is a multicast, then routing mode will be used.
     * Otherwise the communication is using the tunneling mode.
     * <p>
     * Default Port: {@link CoreConfigs#KNX_PORT}<br>
     *
     * @param address a specified address of KNX Net/IP device
     * @return a new instance of {@link ConfigBuilder}
     */
    public static ConfigBuilder create(final InetAddress address) {
        if (address.isMulticastAddress()) {
            return routing(address);
        } else {
            return tunneling(address);
        }
    }

    /**
     * Creates a Builder for customized configuration with specific address and port.
     * <p>
     * If the given address is a multicast, then routing mode will be used.
     * Otherwise the communication is using the tunneling mode.
     *
     * @param address a specified address of KNX Net/IP device
     * @param port    a specific port of KNX Net/IP device
     * @return a new instance of {@link ConfigBuilder}
     */
    public static ConfigBuilder create(final InetAddress address, final int port) {
        if (address.isMulticastAddress()) {
            return routing(address, port);
        } else {
            return tunneling(address, port);
        }
    }

    /**
     * Creates a Builder for customized configuration using <strong>ROUTING</strong> mode.
     * <p>
     * Default Address: {@link CoreConfigs#MULTICAST_ADDRESS}<br>
     * Default Port: {@link CoreConfigs#KNX_PORT}<br>
     *
     * @return new builder for routing mode with standard settings according to the KNX specification
     */
    public static ConfigBuilder routing() {
        return routing(CoreConfigs.MULTICAST_ADDRESS);
    }

    /**
     * Creates a Builder for customized configuration using <strong>ROUTING</strong> mode with
     * customized multicast {@code address} and default KNX port.
     * address
     * <p>
     * Default Port: {@link CoreConfigs#KNX_PORT}<br>
     *
     * @param address a specified multicast address of KNX Net/IP device
     * @return new builder for routing mode with customized multicast address
     */
    public static ConfigBuilder routing(final InetAddress address) {
        return routing(address, CoreConfigs.KNX_PORT);
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
    public static ConfigBuilder routing(final InetAddress address, final int port) {
        Preconditions.checkArgument(address.isMulticastAddress(),
                "Given address is not suitable for routing: {}", address.getHostAddress());
        return new ConfigBuilder(true, address, port);
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode.
     * <p>
     * Default Address: {@link Networker#getAddressUnbound()} (=will use discovery service)<br>
     * Default Port: {@link CoreConfigs#KNX_PORT}<br>
     * Default NAT Flag: {@link CoreConfigs#NAT}<br>
     *
     * @return new builder for tunneling mode with standard settings according to the KNX specification
     */
    public static ConfigBuilder tunneling() {
        return tunneling(Networker.getAddressUnbound());
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode.
     * Use {@code natEnabled} flag if the Network Address Translation feature should be activated.
     * <p>
     * Default Address: {@link Networker#getAddressUnbound()} (=will use discovery service)<br>
     * Default Port: {@link CoreConfigs#KNX_PORT}<br>
     *
     * @param natEnabled {@code true} if NAT should be enabled
     * @return new builder for tunneling mode with standard settings according to the KNX specification
     */
    public static ConfigBuilder tunneling(final boolean natEnabled) {
        return tunneling(Networker.getAddressUnbound(), natEnabled);
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode with
     * customized {@code address} and default KNX port.
     * address
     * <p>
     * Default Port: {@link CoreConfigs#KNX_PORT}<br>
     * Default NAT Flag: {@link CoreConfigs#NAT}<br>
     *
     * @param address a specified address of KNX Net/IP device
     * @return new builder for tunneling mode with customized address
     */
    public static ConfigBuilder tunneling(final InetAddress address) {
        return tunneling(address, CoreConfigs.KNX_PORT);
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode.
     * Use {@code natEnabled} flag if the Network Address Translation feature should be activated.
     * <p>
     * Default Port: {@link CoreConfigs#KNX_PORT}<br>
     *
     * @param address    a specified address of KNX Net/IP device
     * @param natEnabled {@code true} if NAT should be enabled
     * @return new builder for tunneling mode with standard settings according to the KNX specification
     */
    public static ConfigBuilder tunneling(final InetAddress address, final boolean natEnabled) {
        return tunneling(address, CoreConfigs.KNX_PORT, natEnabled);
    }

    /**
     * Creates a Builder for customized configuration using <strong>TUNNELING</strong> mode with
     * customized {@code address} and customized KNX port.
     * <p>
     * Default NAT Flag: {@link CoreConfigs#NAT}<br>
     *
     * @param address a specified address of KNX Net/IP device
     * @param port    a specific port of KNX Net/IP device
     * @return new builder for tunneling mode with customized address
     */
    public static ConfigBuilder tunneling(final InetAddress address, final int port) {
        return tunneling(address, port, CoreConfigs.NAT.getDefaultValue());
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
    public static ConfigBuilder tunneling(final InetAddress address, final int port, final boolean natEnabled) {
        Preconditions.checkArgument(address.isAnyLocalAddress() || !address.isMulticastAddress(),
                "Given address is not suitable for tunneling: {}", address.getHostAddress());
        return new ConfigBuilder(false, address, port).setting(CoreConfigs.NAT, natEnabled);
    }

    /**
     * Adds plugin to be used by KNX client
     *
     * @param plugin plugin to be provided for registering the plugin
     * @return myself
     */
    public ConfigBuilder plugin(final Plugin plugin) {
        final var pluginClass = plugin.getClass();
        Preconditions.checkArgument(plugins.stream().noneMatch(p -> pluginClass == p.getClass()),
                "There is already a plugin added with the class: {}", pluginClass.getName());

        this.plugins.add(plugin);
        return this;
    }

    /**
     * Adds setting to be used by KNX client (and plugins)
     *
     * @param key   key of setting
     * @param value if {@code null}, then default value should be used
     * @param <T>   instance of config value (e.g. Boolean)
     * @return myself
     */
    public <T> ConfigBuilder setting(final ConfigValue<T> key, final @Nullable T value) {
        Preconditions.checkNonNull(key);
        if (value == null) {
            final var oldValue = this.settings.remove(key);
            log.debug("Customized setting removed for key '{}'. Old Value: {}", key.getKey(), oldValue);
        } else {
            Preconditions.checkArgument(!(key instanceof InternalConfigValue),
                    "Internal Config Value cannot be used here: {}", key.getKey());
            Preconditions.checkArgument(key.isValid(value),
                    "The value seems not be applicable for config '{}': {}", key.getKey(), value);
            this.settings.put(key, value);
            log.debug("Customized setting added for key '{}'. New Value: {}", key.getKey(), value);
        }
        return this;
    }

    /**
     * Converts the {@link ConfigBuilder} instance into an immutable {@link Config} instance.
     *
     * @return a new instance of {@link Config}
     */
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
