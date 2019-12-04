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


import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link PathConfigValue}
 */
public final class PathConfigValueTest {

    @Test
    @DisplayName("OK: Test Path Config Value without predicate")
    public void testWithoutPredicate() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.path-key"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(Path.class);
        assertThat(configValue.getDefaultValue()).isEqualTo(Paths.get("path-value"));
        assertThat(configValue.convert("my-file")).isEqualTo(Paths.get("my-file"));
        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid(Paths.get("my-file"))).isTrue();
        assertThat(configValue.getPredicate()).isNull();
    }

    @Test
    @DisplayName("OK: Test Path Config Value with predicate")
    public void testWithPredicate() {
        final var plugin = new DummyPlugin();

        final var configValue = plugin.TEST_2;
        assertThat(configValue.getKey()).isEqualTo("plugin.config.dummyplugin.path-key2"); // lower-cased!
        assertThat(configValue.getClassType()).isSameAs(Path.class);
        assertThat(configValue.getDefaultValue()).isEqualTo(Paths.get("path-value2"));
        assertThat(configValue.convert("my-file2")).isEqualTo(Paths.get("my-file2"));
        assertThat(configValue.isValid(null)).isFalse();
        assertThat(configValue.isValid(Paths.get("my-file2"))).isFalse();
        assertThat(configValue.isValid(Paths.get("my-file2.txt"))).isTrue();
        assertThat(configValue.getPredicate()).isNotNull();
    }

    private static class DummyPlugin implements Plugin {
        final PathConfigValue TEST = new PathConfigValue("path-key", () -> Paths.get("path-value"), null);
        final PathConfigValue TEST_2 = new PathConfigValue("path-key2", () -> Paths.get("path-value2"), path -> path.toString().endsWith(".txt"));

        @Override
        public void onInitialization(final KnxClient client) {
            // NO-OP
        }
    }
}
