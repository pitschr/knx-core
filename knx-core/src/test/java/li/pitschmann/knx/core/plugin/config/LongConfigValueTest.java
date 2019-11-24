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

package li.pitschmann.knx.core.plugin.config;

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.Plugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link LongConfigValue}
 */
public final class LongConfigValueTest {

    @Test
    @DisplayName("OK: Test Long Config Value without predicate")
    public void testWithoutPredicate() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.long-key"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(Long.class);
        assertThat(configValue.getDefaultValue()).isEqualTo(12345678901L);
        assertThat(configValue.convert("567890912345")).isEqualTo(567890912345L);
        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid(45678901234L)).isTrue();
        assertThat(configValue.getPredicate()).isNull();
    }

    @Test
    @DisplayName("OK: Test Long Config Value with predicate")
    public void testWithPredicate() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST_2;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.long-key2"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(Long.class);
        assertThat(configValue.getDefaultValue()).isEqualTo(2345678901L);
        assertThat(configValue.convert("567890912345")).isEqualTo(567890912345L);
        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid(45678901234L)).isFalse();
        assertThat(configValue.isValid(22251617052L)).isTrue();
        assertThat(configValue.getPredicate()).isNotNull();
    }

    private static class DummyPlugin implements Plugin {
        final LongConfigValue TEST = new LongConfigValue("long-key", () -> 12345678901L, null);
        final LongConfigValue TEST_2 = new LongConfigValue("long-key2", () -> 2345678901L, (x) -> x % 4711 == 0);

        @Override
        public void onInitialization(@Nonnull KnxClient client) {
            // NO-OP
        }
    }
}
