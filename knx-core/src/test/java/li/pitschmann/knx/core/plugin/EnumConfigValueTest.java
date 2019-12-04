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
 * Test class for {@link EnumConfigValue}
 */
public final class EnumConfigValueTest {

    @Test
    @DisplayName("OK: Test Enum Config Value")
    public void testConfigValue() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.enum-key"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(DummyEnum.class);
        assertThat(configValue.getDefaultValue()).isEqualTo(DummyEnum.ONE);
        assertThat(configValue.convert("ONE")).isEqualTo(DummyEnum.ONE);
        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid(DummyEnum.TWO)).isTrue();
        assertThat(configValue.getPredicate()).isNotNull(); // built-in
    }

    private enum DummyEnum {
        ZERO, ONE, TWO, THREE
    }

    private static class DummyPlugin implements Plugin {
        final EnumConfigValue<DummyEnum> TEST = new EnumConfigValue<>("enum-key", DummyEnum.class, () -> DummyEnum.ONE);

        @Override
        public void onInitialization(final KnxClient client) {
            // NO-OP
        }
    }
}
