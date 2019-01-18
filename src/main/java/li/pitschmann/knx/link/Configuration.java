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

import com.google.common.base.*;
import com.google.common.collect.*;
import li.pitschmann.knx.link.plugin.*;
import li.pitschmann.utils.*;

import java.net.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

/**
 * KNX specific configurations like KNX Net/IP router address. This class can be created once time only! A change
 * requires a restart of KNX client and its communication.
 *
 * @author PITSCHR
 */
public final class Configuration {
    private final InetSocketAddress routerEndpoint;
    private final List<Plugin> allPlugins;
    private final List<ExtensionPlugin> extensionPlugins;
    private final List<ObserverPlugin> observerPlugins;
    private final Map<String, String> settings;

    private Configuration(final Builder builder) {
        // remote router end point
        this.routerEndpoint = new InetSocketAddress(builder.routerAddress, builder.routerPort);
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
     * @param routerAddress
     * @return {@link Builder}
     */
    public static Builder create(final String routerAddress) {
        if (routerAddress.contains(":")) {
            String[] routerAddressSplitted = routerAddress.split(":");
            Preconditions.checkArgument(routerAddressSplitted.length == 2, "Illegal Router Address provided.");

            String address = routerAddressSplitted[0];
            int port = Integer.parseInt(routerAddressSplitted[1]);

            return create(Networker.getByAddress(address), port);
        } else {
            return create(Networker.getByAddress(routerAddress));
        }
    }

    /**
     * Creates a Builder for a customized configuration
     *
     * @param routerAddress
     * @return {@link Builder}
     */
    public static Builder create(final InetAddress routerAddress) {
        return create(routerAddress, Constants.Protocol.PORT);
    }

    /**
     * Creates a Builder for a customized configuration
     *
     * @param routerAddress
     * @param routerPort
     * @return {@link Builder}
     */
    public static Builder create(final InetAddress routerAddress, final int routerPort) {
        return new Builder().routerAddress(routerAddress, routerPort);
    }

    /**
     * Remote Router Endpoint
     *
     * @return {@link InetSocketAddress}
     */
    public InetSocketAddress getRouterEndpoint() {
        return this.routerEndpoint;
    }

    /**
     * Returns list of all plug-ins
     *
     * @return unmodifiable list of all {@link Plugin} instances
     */
    public List<Plugin> getAllPlugins() {
        return this.allPlugins;
    }

    /**
     * Returns list of observer plug-ins
     *
     * @return unmodifiable list of {@link ObserverPlugin}
     */
    public List<ObserverPlugin> getObserverPlugins() {
        return this.observerPlugins;
    }

    /**
     * Returns list of extension plug-ins
     *
     * @return unmodifiable list of {@link ExtensionPlugin}
     */
    public List<ExtensionPlugin> getExtensionPlugins() {
        return this.extensionPlugins;
    }

    public <T> T getSetting(final String key, final T defaultValue, Function<String, T> function) {
        String value = this.settings.get(key);
        return value == null ? defaultValue : function.apply(value);
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

    public long getSocketTimeoutControlChannel() {
        return getSetting("timeout.socket.controlchannel", Constants.Timeouts.CONTROL_CHANNEL_SOCKET_TIMEOUT, Long::valueOf);
    }

    public long getSocketTimeoutDataChannel() {
        return getSetting("timeout.socket.datachannel", Constants.Timeouts.DATA_CHANNEL_SOCKET_TIMEOUT, Long::valueOf);
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

    /**
     * Builder for the Configuration
     *
     * @author PITSCHR
     */
    public static class Builder {
        private final List<Plugin> plugins = Lists.newLinkedList();
        private final Map<String, String> settings = Maps.newHashMap();
        private InetAddress routerAddress;
        private int routerPort;
        private String note;

        private Builder() {
        }

        public Builder routerAddress(final InetAddress routerAddress, final int routerPort) {
            Preconditions.checkNotNull(routerAddress);
            Preconditions.checkArgument(routerPort > 0, "Illegal Router Port provided.");

            this.routerAddress = routerAddress;
            this.routerPort = routerPort;
            return this;
        }

        public Builder plugin(final Plugin plugin, final Plugin... morePlugins) {
            this.plugins.add(plugin);
            for (final Plugin morePlugin : morePlugins) {
                this.plugins.add(morePlugin);
            }
            return this;
        }

        public Builder setting(final String key, final String value) {
            this.settings.put(key, value);
            return this;
        }

        public Builder note(final String note) {
            this.note = note;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }
}
