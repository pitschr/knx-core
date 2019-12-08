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

package li.pitschmann.knx.core.plugin;

import li.pitschmann.knx.core.communication.KnxClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for {@link PluginConfigValue}
 */
public final class PluginConfigValueTest {

    @Test
    @DisplayName("Error: Test General Plugin Config Value  in wrong class")
    public void testWrongClass() {
        assertThatThrownBy(() -> new PluginConfigValue<>("a-name", Object.class, x -> x, () -> "a-value", null))
                .isInstanceOf(AssertionError.class)
                .hasMessage("PluginConfigValue may be used in Plugin class only!");
    }

    @Test
    @DisplayName("OK: Test General Plugin Config Value in Plugin class")
    public void testInPluginClass() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.configValueObject;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.b-name"); // lower-cased!
    }


    private static class DummyPlugin implements Plugin {
        private static final PluginConfigValue<Object> configValueObject = new PluginConfigValue<>("b-name", Object.class, x -> x, () -> "", null);
        final PluginConfigValue<Object> TEST = new PluginConfigValue<>("b-name", Object.class, x -> x, () -> "", null);

        @Override
        public void onInitialization(final KnxClient client) {
            // NO-OP
        }
    }
}
