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

/**
 * Test class for {@link DoubleConfigValue}
 */
public final class DoubleConfigValueTest {

    @Test
    @DisplayName("OK: Test Double Config Value without predicate")
    public void testWithoutPredicate() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.double-key"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(Double.class);
        assertThat(configValue.getDefaultValue()).isEqualTo(14.13);
        assertThat(configValue.convert("5673.2344")).isEqualTo(5673.2344);
        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid(3.14)).isTrue();
        assertThat(configValue.getPredicate()).isNull();
    }

    @Test
    @DisplayName("OK: Test Double Config Value with predicate")
    public void testWithPredicate() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST_2;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.double-key2"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(Double.class);
        assertThat(configValue.getDefaultValue()).isEqualTo(15.77);
        assertThat(configValue.convert("5673.2344")).isEqualTo(5673.2344);
        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid(3.14)).isFalse();
        assertThat(configValue.isValid(443.17)).isTrue();
        assertThat(configValue.getPredicate()).isNotNull();
    }

    private static class DummyPlugin implements Plugin {
        final DoubleConfigValue TEST = new DoubleConfigValue("double-key", () -> 14.13, null);
        final DoubleConfigValue TEST_2 = new DoubleConfigValue("double-key2", () -> 15.77, (x) -> x > 10);

        @Override
        public void onInitialization(final KnxClient client) {
            // NO-OP
        }
    }
}
