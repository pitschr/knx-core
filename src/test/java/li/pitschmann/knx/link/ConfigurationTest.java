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
import li.pitschmann.utils.Networker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link Configuration}
 */
public class ConfigurationTest {

    /**
     * Test creating new instances with given remote address with port
     * and without port
     */
    @Test
    @DisplayName("Creates a new instance of Configuration")
    public void testCreate() {
        // creates a new one without port -> default KNX port will be used
        var config1 = Configuration.create("127.0.1.1").build();
        var config1RouterEndpoint = config1.getRouterEndpoint();
        assertThat(config1RouterEndpoint.getAddress()).isEqualTo(Networker.getByAddress(127, 0, 1, 1));
        assertThat(config1RouterEndpoint.getPort()).isEqualTo(Constants.Protocol.PORT);

        // creates a new with port
        var config2 = Configuration.create("127.0.1.2:4711").build();
        var config2RouterEndpoint = config2.getRouterEndpoint();
        assertThat(config2RouterEndpoint.getAddress()).isEqualTo(Networker.getByAddress(127, 0, 1, 2));
        assertThat(config2RouterEndpoint.getPort()).isEqualTo(4711);
    }

    /**
     * Test of plugin registration using
     */
    @Test
    @DisplayName("Tests configuration with plugins")
    public void testPlugins() {
        var configBuilder = Configuration.create(Networker.getLocalhost());

        // add 13 observer plugins
        var observerPlugin1 = mock(ObserverPlugin.class);
        var observerPlugin2 = mock(ObserverPlugin.class);
        var observerPlugin3 = mock(ObserverPlugin.class);
        configBuilder.plugin(observerPlugin1, observerPlugin2, observerPlugin3);
        for (int i = 0; i < 10; i++) {
            configBuilder.plugin(mock(ObserverPlugin.class));
        }

        // add 20 extension plugins
        for (int i = 0; i < 20; i++) {
            configBuilder.plugin(mock(ExtensionPlugin.class));
        }

        // verify
        var config = configBuilder.build();
        assertThat(config.getAllPlugins()).hasSize(33);
        assertThat(config.getObserverPlugins()).hasSize(13);
        assertThat(config.getExtensionPlugins()).hasSize(20);

        // default size of pool execution should be increased automatically
        // only the number of observer plugins is being considered because
        // extension plugin is only used on start up / shut down phases
        assertThat(config.getPluginExecutorPoolSize()).isEqualTo(13);
    }
}
