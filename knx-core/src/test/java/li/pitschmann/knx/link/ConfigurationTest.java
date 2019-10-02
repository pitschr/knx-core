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

import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.link.plugin.Plugin;
import li.pitschmann.utils.Networker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link Configuration}
 */
public class ConfigurationTest {

    @Test
    @DisplayName("Creates a new config with null or empty String")
    public void testCreateStringNoArg() {
        // creates a new one without any configuration -> discovery service and default KNX port will be used
        final var config0 = Configuration.create((String)null).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(config0.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);

        final var config1 = Configuration.create("").build();
        assertThat(config1.isRoutingEnabled()).isFalse();
        assertThat(config1.getRemoteControlAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(config1.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);
    }

    @Test
    @DisplayName("Creates a new config with String")
    public void testCreateString() {
        // creates a new string without
        final var config0 = Configuration.create(":").build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(config0.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);

        // creates a new one without port -> default KNX port will be used
        final var config1 = Configuration.create("127.0.1.1").build();
        assertThat(config1.isRoutingEnabled()).isFalse();
        assertThat(config1.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 1, 1));
        assertThat(config1.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);

        // tunneling (with address and port)
        final var config2 = Configuration.create("127.0.1.2:4710").build();
        assertThat(config2.isRoutingEnabled()).isFalse();
        assertThat(config2.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 1, 2));
        assertThat(config2.getRemoteControlPort()).isEqualTo(4710);

        // tunneling (with address, no port)
        final var config3 = Configuration.create("127.0.1.3:").build();
        assertThat(config3.isRoutingEnabled()).isFalse();
        assertThat(config3.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 1, 3));
        assertThat(config3.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);

        // tunneling (no address, with port)
        final var config4 = Configuration.create(":4711").build();
        assertThat(config4.isRoutingEnabled()).isFalse();
        assertThat(config4.getRemoteControlAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(config4.getRemoteControlPort()).isEqualTo(4711);

        // routing (no port)
        final var config5 = Configuration.create("224.0.1.4").build();
        assertThat(config5.isRoutingEnabled()).isTrue();
        assertThat(config5.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224,0,1,4));
        assertThat(config5.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);

        // routing (with port)
        final var config6 = Configuration.create("224.0.1.5:4712").build();
        assertThat(config6.isRoutingEnabled()).isTrue();
        assertThat(config6.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224,0,1,5));
        assertThat(config6.getRemoteControlPort()).isEqualTo(4712);
    }

    @Test
    @DisplayName("Creates a new config with address")
    public void testCreateAddress() {
        // valid cases
        final var config0 = Configuration.create(Networker.getByAddress(127, 0, 2, 1)).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 2, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);

        final var config1 = Configuration.create(Networker.getByAddress(224, 0, 2, 2)).build();
        assertThat(config1.isRoutingEnabled()).isTrue();
        assertThat(config1.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224, 0, 2, 2));
        assertThat(config1.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);

        // invalid cases
        assertThatThrownBy(() -> Configuration.create((InetAddress) null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Creates a new config with address and port")
    public void testCreateAddressWithPort() {
        // valid cases
        final var config0 = Configuration.create(Networker.getByAddress(127, 0, 3, 1), 4713).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 3, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(4713);

        final var config1 = Configuration.create(Networker.getByAddress(224, 0, 3, 2), 4714).build();
        assertThat(config1.isRoutingEnabled()).isTrue();
        assertThat(config1.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224, 0, 3, 2));
        assertThat(config1.getRemoteControlPort()).isEqualTo(4714);

        // invalid cases
        assertThatThrownBy(() -> Configuration.create(null, 4712))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Configuration.create(Networker.getAddressUnbound(), 1023))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Port is outside of range [1024 .. 65535]: 1023");
        assertThatThrownBy(() -> Configuration.create(Networker.getAddressUnbound(), 65536))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Port is outside of range [1024 .. 65535]: 65536");
    }

    @Test
    @DisplayName("Creates a new config in routing mode (no-arg)")
    public void testRoutingNoArg() {
        final var config0 = Configuration.routing().build();
        assertThat(config0.isRoutingEnabled()).isTrue();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Constants.Default.MULTICAST_ADDRESS);
        assertThat(config0.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);
    }

    @Test
    @DisplayName("Creates a new config in routing mode with address")
    public void testRoutingAddress() {
        // valid cases
        final var config0 = Configuration.routing(Networker.getByAddress(224, 0, 4, 1)).build();
        assertThat(config0.isRoutingEnabled()).isTrue();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224, 0, 4, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);

        // invalid cases
        assertThatThrownBy(() -> Configuration.routing(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Configuration.routing(Networker.getByAddress(127, 0, 4, 2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given address is not suitable for routing: 127.0.4.2");
    }

    @Test
    @DisplayName("Creates a new config in routing mode with address and port")
    public void testRoutingAddressAndPort() {
        // valid cases
        final var config0 = Configuration.routing(Networker.getByAddress(224, 0, 5, 1), 4716).build();
        assertThat(config0.isRoutingEnabled()).isTrue();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224, 0, 5, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(4716);

        // invalid cases
        assertThatThrownBy(() -> Configuration.routing(null, 4717))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Configuration.routing(Networker.getByAddress(127, 0, 5, 2), 4718))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given address is not suitable for routing: 127.0.5.2");

    }

    @Test
    @DisplayName("Creates a new config in tunneling mode (no-arg)")
    public void testTunnelingNoArg() {
        final var config0 = Configuration.tunneling().build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(config0.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);
    }

    @Test
    @DisplayName("Creates a new config in tunneling mode with address")
    public void testTunnelingAddress() {
        // valid cases
        final var config0 = Configuration.tunneling(Networker.getByAddress(127, 0, 6, 1)).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 6, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(Constants.Default.KNX_PORT);

        // invalid cases
        assertThatThrownBy(() -> Configuration.tunneling(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Configuration.tunneling(Networker.getByAddress(224, 0, 7, 2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given address is not suitable for tunneling: 224.0.7.2");
    }

    @Test
    @DisplayName("Creates a new config in tunneling mode with address and port")
    public void testTunnelingAddressAndPort() {
        // valid cases
        final var config0 = Configuration.tunneling(Networker.getByAddress(127, 0, 8, 1), 4719).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 8, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(4719);

        // invalid cases
        assertThatThrownBy(() -> Configuration.tunneling(null, 4718))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Configuration.tunneling(Networker.getByAddress(224, 0, 8, 2), 4720))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given address is not suitable for tunneling: 224.0.8.2");
    }

    @Test
    @DisplayName("Every config build should create a new instance")
    public void testNewInstance() {
        final var configBuilder = Configuration.tunneling();

        final var configOne = configBuilder.build();
        final var configTwo = configBuilder.build();
        assertThat(configOne).isNotSameAs(configTwo);
    }

    @Test
    @DisplayName("Tests config with plugins")
    public void testPlugins() {
        final var configBuilder = Configuration.tunneling();

        // add 12 observer plugins
        final var observerPlugin1 = mock(ObserverPlugin.class);
        final var observerPlugin2 = mock(ObserverPlugin.class);
        configBuilder.plugin(observerPlugin1, observerPlugin2);
        for (var i = 0; i < 10; i++) {
            configBuilder.plugin(mock(ObserverPlugin.class));
        }

        // add 24 extension plugins
        final var extensionPlugin1 = mock(ExtensionPlugin.class);
        final var extensionPlugin2 = mock(ExtensionPlugin.class);
        final var extensionPlugin3 = mock(ExtensionPlugin.class);
        final var extensionPlugin4 = mock(ExtensionPlugin.class);
        configBuilder.plugin(extensionPlugin1, (Plugin[])null);
        configBuilder.plugin(extensionPlugin2, new ExtensionPlugin[0]);
        configBuilder.plugin(extensionPlugin3, new ExtensionPlugin[]{extensionPlugin4});
        for (var i = 0; i < 20; i++) {
            configBuilder.plugin(mock(ExtensionPlugin.class));
        }

        // verify
        final var config = configBuilder.build();
        assertThat(config.getPlugins()).hasSize(36);
        assertThat(config.getPlugins().stream().filter(ObserverPlugin.class::isInstance).count()).isEqualTo(12);
        assertThat(config.getPlugins().stream().filter(ExtensionPlugin.class::isInstance).count()).isEqualTo(24);

        // invalid cases because of "null" reference
        assertThatThrownBy(() -> configBuilder.plugin(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> configBuilder.plugin(observerPlugin1, new ObserverPlugin[]{observerPlugin2, null}))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Test config with settings")
    public void testSettings() {
        final var configBuilder = Configuration.tunneling();

        // add settings
        configBuilder.setting(Constants.ConfigurationKey.PLUGIN_EXECUTOR_POOL_SIZE, "7");
        configBuilder.setting(Constants.ConfigurationKey.NAT, "true");
        configBuilder.setting(Constants.ConfigurationKey.MULTICAST_ADDRESS, "224.0.0.1");
        configBuilder.setting(Constants.ConfigurationKey.MULTICAST_TTL, null);
        configBuilder.setting("foo", "bar");

        final var config = configBuilder.build();
        assertThat(config.getMulticastChannelAddress()).isEqualTo(Networker.getByAddress(224, 0, 0, 1));
        assertThat(config.getMulticastTTL()).isEqualTo(4); // fallback to default value

        // overwriting setting should be allowed
        configBuilder.setting(Constants.ConfigurationKey.PLUGIN_EXECUTOR_POOL_SIZE, "4");
        configBuilder.setting(Constants.ConfigurationKey.NAT, null); // reset to default value
        configBuilder.setting("foo", "barNEW");
        final var configNew = configBuilder.build();

        // old and new value should be still present
        assertThat(config.getPluginExecutorPoolSize()).isEqualTo(7);
        assertThat(configNew.getPluginExecutorPoolSize()).isEqualTo(4);

        assertThat(config.isNatEnabled()).isTrue();
        assertThat(configNew.isNatEnabled()).isFalse();

        assertThat(config.getSetting("foo", null, Function.identity())).isEqualTo("bar");
        assertThat(configNew.getSetting("foo", null, Function.identity())).isEqualTo("barNEW");

        // invalid cases
        assertThatThrownBy(() -> configBuilder.setting((String)null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> configBuilder.setting((Constants.ConfigurationKey) null, null)).isInstanceOf(NullPointerException.class);

        // protected cases
        assertThatThrownBy(() -> configBuilder.setting("client.endpoint.address", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> configBuilder.setting("client.endpoint.port", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> configBuilder.setting(Constants.ConfigurationKey.ENDPOINT_ADDRESS, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> configBuilder.setting(Constants.ConfigurationKey.ENDPOINT_PORT, null)).isInstanceOf(IllegalArgumentException.class);
    }
}
