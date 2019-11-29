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

import li.pitschmann.knx.core.plugin.ExtensionPlugin;
import li.pitschmann.knx.core.plugin.ObserverPlugin;
import li.pitschmann.knx.core.test.data.TestExtensionPlugin;
import li.pitschmann.knx.core.test.data.TestObserverPlugin;
import li.pitschmann.knx.core.test.data.TestPlugin;
import li.pitschmann.knx.core.utils.Networker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test for {@link ConfigBuilder}
 */
public class ConfigBuilderTest {
    private static final Path KNX_PROJECT = Paths.get("src/test/resources/parser/Project (3-Level, v20).knxproj");

    @Test
    @DisplayName("Creates a new config with null or empty String")
    public void testCreateStringNoArg() {
        // creates a new one without any configuration -> discovery service and default KNX port will be used
        final var config0 = ConfigBuilder.create((String) null).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(config0.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);

        final var config1 = ConfigBuilder.create("").build();
        assertThat(config1.isRoutingEnabled()).isFalse();
        assertThat(config1.getRemoteControlAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(config1.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);
    }

    @Test
    @DisplayName("Creates a new config with String")
    public void testCreateString() {
        // creates a new string without
        final var config0 = ConfigBuilder.create(":").build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(config0.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);

        // creates a new one without port -> default KNX port will be used
        final var config1 = ConfigBuilder.create("127.0.1.1").build();
        assertThat(config1.isRoutingEnabled()).isFalse();
        assertThat(config1.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 1, 1));
        assertThat(config1.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);

        // tunneling (with address and port)
        final var config2 = ConfigBuilder.create("127.0.1.2:4710").build();
        assertThat(config2.isRoutingEnabled()).isFalse();
        assertThat(config2.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 1, 2));
        assertThat(config2.getRemoteControlPort()).isEqualTo(4710);

        // tunneling (with address, no port)
        final var config3 = ConfigBuilder.create("127.0.1.3:").build();
        assertThat(config3.isRoutingEnabled()).isFalse();
        assertThat(config3.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 1, 3));
        assertThat(config3.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);

        // tunneling (no address, with port)
        final var config4 = ConfigBuilder.create(":4711").build();
        assertThat(config4.isRoutingEnabled()).isFalse();
        assertThat(config4.getRemoteControlAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(config4.getRemoteControlPort()).isEqualTo(4711);

        // routing (no port)
        final var config5 = ConfigBuilder.create("224.0.1.4").build();
        assertThat(config5.isRoutingEnabled()).isTrue();
        assertThat(config5.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224, 0, 1, 4));
        assertThat(config5.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);

        // routing (with port)
        final var config6 = ConfigBuilder.create("224.0.1.5:4712").build();
        assertThat(config6.isRoutingEnabled()).isTrue();
        assertThat(config6.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224, 0, 1, 5));
        assertThat(config6.getRemoteControlPort()).isEqualTo(4712);
    }

    @Test
    @DisplayName("Creates a new config with address")
    public void testCreateAddress() {
        // valid cases
        final var config0 = ConfigBuilder.create(Networker.getByAddress(127, 0, 2, 1)).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 2, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);

        final var config1 = ConfigBuilder.create(Networker.getByAddress(224, 0, 2, 2)).build();
        assertThat(config1.isRoutingEnabled()).isTrue();
        assertThat(config1.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224, 0, 2, 2));
        assertThat(config1.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);

        // invalid cases
        assertThatThrownBy(() -> ConfigBuilder.create((InetAddress) null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Creates a new config with address and port")
    public void testCreateAddressWithPort() {
        // valid cases
        final var config0 = ConfigBuilder.create(Networker.getByAddress(127, 0, 3, 1), 4713).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 3, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(4713);

        final var config1 = ConfigBuilder.create(Networker.getByAddress(224, 0, 3, 2), 4714).build();
        assertThat(config1.isRoutingEnabled()).isTrue();
        assertThat(config1.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224, 0, 3, 2));
        assertThat(config1.getRemoteControlPort()).isEqualTo(4714);

        // invalid cases
        assertThatThrownBy(() -> ConfigBuilder.create(null, 4712))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConfigBuilder.create(Networker.getAddressUnbound(), 1023))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Port is outside of range [1024 .. 65535]: 1023");
        assertThatThrownBy(() -> ConfigBuilder.create(Networker.getAddressUnbound(), 65536))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Port is outside of range [1024 .. 65535]: 65536");
    }

    @Test
    @DisplayName("Creates a new config in routing mode (no-arg)")
    public void testRoutingNoArg() {
        final var config0 = ConfigBuilder.routing().build();
        assertThat(config0.isRoutingEnabled()).isTrue();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(CoreConfigs.MULTICAST_ADDRESS);
        assertThat(config0.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);
    }

    @Test
    @DisplayName("Creates a new config in routing mode with address")
    public void testRoutingAddress() {
        // valid cases
        final var config0 = ConfigBuilder.routing(Networker.getByAddress(224, 0, 4, 1)).build();
        assertThat(config0.isRoutingEnabled()).isTrue();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224, 0, 4, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);

        // invalid cases
        assertThatThrownBy(() -> ConfigBuilder.routing(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConfigBuilder.routing(Networker.getByAddress(127, 0, 4, 2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given address is not suitable for routing: 127.0.4.2");
    }

    @Test
    @DisplayName("Creates a new config in routing mode with address and port")
    public void testRoutingAddressAndPort() {
        // valid cases
        final var config0 = ConfigBuilder.routing(Networker.getByAddress(224, 0, 5, 1), 4716).build();
        assertThat(config0.isRoutingEnabled()).isTrue();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(224, 0, 5, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(4716);

        // invalid cases
        assertThatThrownBy(() -> ConfigBuilder.routing(null, 4717))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConfigBuilder.routing(Networker.getByAddress(127, 0, 5, 2), 4718))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given address is not suitable for routing: 127.0.5.2");

    }

    @Test
    @DisplayName("Creates a new config in tunneling mode (no-arg)")
    public void testTunnelingNoArg() {
        final var config0 = ConfigBuilder.tunneling().build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(config0.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);
        assertThat(config0.isNatEnabled()).isFalse();
    }

    @Test
    @DisplayName("Creates a new config in tunneling mode with address")
    public void testTunnelingAddress() {
        // valid cases
        final var config0 = ConfigBuilder.tunneling(Networker.getByAddress(127, 0, 6, 1)).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 6, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);
        assertThat(config0.isNatEnabled()).isFalse();

        // invalid cases
        assertThatThrownBy(() -> ConfigBuilder.tunneling(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConfigBuilder.tunneling(Networker.getByAddress(224, 0, 7, 2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given address is not suitable for tunneling: 224.0.7.2");
    }

    @Test
    @DisplayName("Creates a new config in tunneling mode with address and port")
    public void testTunnelingAddressAndPort() {
        // valid cases
        final var config0 = ConfigBuilder.tunneling(Networker.getByAddress(127, 0, 8, 1), 4719).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 8, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(4719);
        assertThat(config0.isNatEnabled()).isFalse();

        // invalid cases
        assertThatThrownBy(() -> ConfigBuilder.tunneling(null, 4718))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConfigBuilder.tunneling(Networker.getByAddress(224, 0, 8, 2), 4720))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given address is not suitable for tunneling: 224.0.8.2");
    }

    @Test
    @DisplayName("Creates a new config in tunneling mode with NAT flag")
    public void testTunnelingNAT() {
        // valid cases
        final var config0 = ConfigBuilder.tunneling(true).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getAddressUnbound());
        assertThat(config0.getRemoteControlPort()).isEqualTo(CoreConfigs.KNX_PORT);
        assertThat(config0.isNatEnabled()).isTrue();
    }

    @Test
    @DisplayName("Creates a new config in tunneling mode with address, port and NAT flag")
    public void testTunnelingAddressAndPortAndNAT() {
        // valid cases
        final var config0 = ConfigBuilder.tunneling(Networker.getByAddress(127, 0, 9, 1), 4721, true).build();
        assertThat(config0.isRoutingEnabled()).isFalse();
        assertThat(config0.getRemoteControlAddress()).isEqualTo(Networker.getByAddress(127, 0, 9, 1));
        assertThat(config0.getRemoteControlPort()).isEqualTo(4721);
        assertThat(config0.isNatEnabled()).isTrue();

        // invalid cases
        assertThatThrownBy(() -> ConfigBuilder.tunneling(null, 4718, true))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConfigBuilder.tunneling(Networker.getByAddress(224, 0, 9, 2), 4722, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given address is not suitable for tunneling: 224.0.9.2");
    }

    @Test
    @DisplayName("Every config build should create a new instance")
    public void testNewInstance() {
        final var configBuilder = ConfigBuilder.tunneling();

        final var configOne = configBuilder.build();
        final var configTwo = configBuilder.build();
        assertThat(configOne).isNotSameAs(configTwo);
    }

    @Test
    @DisplayName("Tests config with plugins")
    public void testPlugins() {
        final var configBuilder = ConfigBuilder.tunneling();

        // add plugins
        configBuilder
                .plugin(TestPlugin.class)
                .plugin(TestObserverPlugin.class)
                .plugin(TestExtensionPlugin.class);

        // verify
        final var config = configBuilder.build();
        assertThat(config.getPlugins()).hasSize(3);
        assertThat(config.getPlugins().stream().filter(ObserverPlugin.class::isAssignableFrom).count()).isOne();
        assertThat(config.getPlugins().stream().filter(ExtensionPlugin.class::isAssignableFrom).count()).isOne();

        // invalid cases because of "null" reference
        assertThatThrownBy(() -> configBuilder.plugin(null))
                .isInstanceOf(NullPointerException.class);
        // invalid case when re-try adding same plugin
        assertThatThrownBy(() -> configBuilder.plugin(TestPlugin.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Plugin already added: li.pitschmann.knx.core.test.data.TestPlugin");
    }

    @Test
    @DisplayName("Test config with settings")
    public void testSettings() {
        final var configBuilder = ConfigBuilder.tunneling();

        // add settings
        configBuilder.setting(CoreConfigs.Plugin.EXECUTOR_POOL_SIZE, 7);
        configBuilder.setting(CoreConfigs.NAT, true);
        configBuilder.setting(CoreConfigs.Multicast.ADDRESS, Networker.getByAddress("224.0.0.1"));
        configBuilder.setting(CoreConfigs.Multicast.TIME_TO_LIVE, null);

        final var config = configBuilder.build();
        assertThat(config.getValue(CoreConfigs.Multicast.ADDRESS)).isEqualTo(Networker.getByAddress(224, 0, 0, 1));
        assertThat(config.getValue(CoreConfigs.Multicast.TIME_TO_LIVE)).isEqualTo(4); // fallback to default value

        // overwriting setting should be allowed
        configBuilder.setting(CoreConfigs.Plugin.EXECUTOR_POOL_SIZE, 4);
        configBuilder.setting(CoreConfigs.NAT, null); // reset to default value
        final var configNew = configBuilder.build();

        // old and new value should be still present
        assertThat(config.getValue(CoreConfigs.Plugin.EXECUTOR_POOL_SIZE)).isEqualTo(7);
        assertThat(configNew.getValue(CoreConfigs.Plugin.EXECUTOR_POOL_SIZE)).isEqualTo(4);

        assertThat(config.isNatEnabled()).isTrue();
        assertThat(configNew.isNatEnabled()).isFalse();

        // invalid cases
        assertThatThrownBy(() -> configBuilder.setting(null, null)).isInstanceOf(NullPointerException.class);

        // protected cases
        assertThatThrownBy(() -> configBuilder.setting(CoreConfigs.Endpoint.ADDRESS, Networker.getAddressUnbound())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> configBuilder.setting(CoreConfigs.Endpoint.PORT, 4711)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Test config with null-value settings")
    public void testSettingsNullValue() {
        final var configBuilder = ConfigBuilder.tunneling(true);

        // 1) verify if it is "enabled" as we set above
        assertThat(configBuilder.build().isNatEnabled()).isTrue();
        // 2) re-set the value of NAT (should be "false" as default value)
        configBuilder.setting(CoreConfigs.NAT, null);
        // 3) verify if it is "disabled"
        assertThat(configBuilder.build().isNatEnabled()).isFalse();
    }

    @Test
    @DisplayName("Test config with validator/predicate")
    public void testSettingWithPredicate() {
        final var configBuilder = ConfigBuilder.tunneling();

        // test with invalid path
        final var invalidPath = Paths.get("path/to/file/doesn't exists.xml");
        assertThatThrownBy(() -> configBuilder.setting(CoreConfigs.PROJECT_PATH, invalidPath))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("The value seems not be applicable for config");

        // test with valid path
        assertThat(configBuilder.setting(CoreConfigs.PROJECT_PATH, KNX_PROJECT).build()).isNotNull();
    }
}
