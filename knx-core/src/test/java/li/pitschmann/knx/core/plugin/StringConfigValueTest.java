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
 * Test class for {@link StringConfigValue}
 */
public final class StringConfigValueTest {

    @Test
    @DisplayName("OK: Test String Config Value without predicate")
    public void testWithoutPredicate() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.str-key"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(String.class);
        assertThat(configValue.getDefaultValue()).isEqualTo("str-value");
        assertThat(configValue.convert("Abc")).isEqualTo("Abc");
        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid("Abc")).isTrue();
        assertThat(configValue.getPredicate()).isNull();
    }

    @Test
    @DisplayName("OK: Test String Config Value with predicate")
    public void testWithPredicate() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST_2;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.str-key2"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(String.class);
        assertThat(configValue.getDefaultValue()).isEqualTo("str-value2");
        assertThat(configValue.convert("Abc")).isEqualTo("Abc");
        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid("Abc")).isFalse();
        assertThat(configValue.isValid("Abcd")).isTrue();
        assertThat(configValue.getPredicate()).isNotNull();
    }

    private static class DummyPlugin implements Plugin {
        final StringConfigValue TEST = new StringConfigValue("str-key", () -> "str-value", null);
        final StringConfigValue TEST_2 = new StringConfigValue("str-key2", () -> "str-value2", (x) -> x.length() == 4);

        @Override
        public void onInitialization(final KnxClient client) {
            // NO-OP
        }
    }
}
