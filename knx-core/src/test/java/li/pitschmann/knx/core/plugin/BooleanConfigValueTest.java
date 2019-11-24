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

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link BooleanConfigValue}
 */
public final class BooleanConfigValueTest {

    @Test
    @DisplayName("OK: Test Boolean Config Value")
    public void testConfigValue() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.bool-key"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(Boolean.class);
        assertThat(configValue.getDefaultValue()).isEqualTo(Boolean.FALSE);

        assertThat(configValue.convert("true")).isTrue();
        assertThat(configValue.convert("True")).isTrue();
        assertThat(configValue.convert("TRUE")).isTrue();
        assertThat(configValue.convert("false")).isFalse();
        assertThat(configValue.convert("False")).isFalse();
        assertThat(configValue.convert("FALSE")).isFalse();

        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid(true)).isTrue();
        assertThat(configValue.isValid(Boolean.TRUE)).isTrue();
        assertThat(configValue.isValid(false)).isTrue();
        assertThat(configValue.isValid(Boolean.FALSE)).isTrue();

        assertThat(configValue.getPredicate()).isNull();
    }

    private static class DummyPlugin implements Plugin {
        final BooleanConfigValue TEST = new BooleanConfigValue("bool-key", () -> Boolean.FALSE);

        @Override
        public void onInitialization(@Nonnull KnxClient client) {
            // NO-OP
        }
    }
}
