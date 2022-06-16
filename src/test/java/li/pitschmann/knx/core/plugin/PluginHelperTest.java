/*
 * Copyright (C) 2022 Pitschmann Christoph
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

import li.pitschmann.knx.core.body.BodyFactory;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.exceptions.KnxPluginException;
import li.pitschmann.knx.core.test.TestHelpers;
import li.pitschmann.knx.core.test.data.TestPlugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link PluginHelper}
 */
class PluginHelperTest {

    @Test
    @DisplayName("ERROR: Test registration by a wrong JAR path")
    void testNonexistentJarPath() {
        // wrong JAR file path
        final var pathToJAR = Paths.get("non-existent-file.jar");
        final var className = "my.plugin.MyTestPlugin";
        assertThatThrownBy(() -> PluginHelper.load(pathToJAR, className))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File doesn't exists or is not readable: non-existent-file.jar");
    }

    @Test
    @DisplayName("OK: Load an existing Plugin")
    void testLoadPlugin() {
        final var pathToJAR = Paths.get("src/test/resources/plugin/my-test-plugin.jar");
        final var className = "my.test.HelloWorldPluginV1";

        final var helloWorldPlugin = PluginHelper.load(pathToJAR, className);
        assertThat(helloWorldPlugin).isInstanceOf(Plugin.class);
    }

    @Test
    @DisplayName("ERROR: Load plugin from an existing JAR, but class name does not exist")
    void testLoadNonexistentPlugin() {
        // wrong JAR file path
        final var pathToJAR = Paths.get("src/test/resources/plugin/my-test-plugin.jar");
        final var className = "my.test.HelloWorldPluginV1_THATDOESNOTEXISTS";
        assertThatThrownBy(() -> PluginHelper.load(pathToJAR, className)).isInstanceOf(KnxException.class);
    }

    @Test
    @DisplayName("OK: Check Plugin Compatibility")
    void testCompatibility() {
        PluginHelper.checkPluginCompatibility(TestPlugin.class);
    }

    @Test
    @DisplayName("ERROR: Check Plugin Compatibility")
    void testCompatibilityErr() {
        // check null
        assertThatThrownBy(() -> PluginHelper.checkPluginCompatibility(null))
                .isInstanceOf(NullPointerException.class);

        // wrong class
        assertThatThrownBy(() -> PluginHelper.checkPluginCompatibility(Object.class))
                .isInstanceOf(KnxPluginException.class)
                .hasMessage("Seems the given class is not an instance of %s: %s", Plugin.class, Object.class);

        // no constructor
        assertThatThrownBy(() -> PluginHelper.checkPluginCompatibility(Plugin.class))
                .isInstanceOf(KnxPluginException.class)
                .hasMessage("There seems be no public null-arg constructor available for: %s", Plugin.class);
    }


    @Test
    @DisplayName("Constructor not instantiable")
    void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(PluginHelper.class);
    }
}
