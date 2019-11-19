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

package li.pitschmann.knx.link.plugin.config;

import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.plugin.Plugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link IntegerConfigValue}
 */
public final class IntegerConfigValueTest {

    @Test
    @DisplayName("OK: Test Integer Config Value without predicate")
    public void testWithoutPredicate() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.int-key"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(Integer.class);
        assertThat(configValue.getDefaultValue()).isEqualTo(13);
        assertThat(configValue.convert("4711")).isEqualTo(4711);
        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid(9)).isTrue();
        assertThat(configValue.getPredicate()).isNull();
    }

    @Test
    @DisplayName("OK: Test Integer Config Value with predicate")
    public void testWithPredicate() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST_2;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.int-key2"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(Integer.class);
        assertThat(configValue.getDefaultValue()).isEqualTo(17);
        assertThat(configValue.convert("4711")).isEqualTo(4711);
        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid(9)).isFalse();
        assertThat(configValue.isValid(10)).isTrue();
        assertThat(configValue.getPredicate()).isNotNull();
    }

    private static class DummyPlugin implements Plugin {
        final IntegerConfigValue TEST = new IntegerConfigValue("int-key", () -> 13, null);
        final IntegerConfigValue TEST_2 = new IntegerConfigValue("int-key2", () -> 17, (x) -> x % 2 == 0);

        @Override
        public void onInitialization(@Nonnull KnxClient client) {
            // NO-OP
        }
    }
}
