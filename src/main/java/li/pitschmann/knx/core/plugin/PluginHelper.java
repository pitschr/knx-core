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

import li.pitschmann.knx.core.exceptions.KnxPluginException;
import li.pitschmann.knx.core.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A helper to load the {@link Plugin} from an external source like
 * a JAR file.
 *
 * @author PITSCHR
 */
public final class PluginHelper {
    private static final Logger log = LoggerFactory.getLogger(PluginHelper.class);
    private PluginHelper() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Reads the {@code filePath} and loads a class from given {@code className}
     * using an internal instance of {@link URLClassLoader}.
     *
     * @param filePath the JAR file that contains the plugin; may not be null
     * @param className fully qualified class name; may not be null
     * @return a new instance of plugin
     * @throws KnxPluginException if a new instance of plugin could not be read or loaded
     */
    public static Plugin load(final Path filePath, final String className) {
        Preconditions.checkArgument(filePath.getFileName().toString().endsWith(".jar"),
                "File doesn't end with '.jar' extension: {}", filePath);
        Preconditions.checkNonNull(className);
        Preconditions.checkArgument(Files.isReadable(filePath),
                "File doesn't exists or is not readable: {}", filePath);

        log.debug("Try to load plugin '{}' from path: {}", className, filePath);
        final Plugin plugin;
        try (var classLoader = new URLClassLoader(new URL[]{filePath.toUri().toURL()})) {
            plugin = newInstance(classLoader.loadClass(className));
            log.debug("Plugin '{}' loaded from url '{}': {}", className, filePath, plugin);
        } catch (final Exception e) {
            throw new KnxPluginException("Could not load plugin '" + className + "' at: " + filePath, e);
        }

        return plugin;
    }

    /**
     * Checks if the given {@code pluginClass} is compliant with the {@link Plugin}
     *
     * @param pluginClass the class to be checked; may not be null
     * @throws KnxPluginException if not compliant with an error message
     */
    public static void checkPluginCompatibility(final Class<?> pluginClass) {
        Objects.requireNonNull(pluginClass);

        // validation if the class implements the Plugin interface
        if (!Plugin.class.isAssignableFrom(pluginClass)) {
            throw new KnxPluginException("Seems the given class is not an instance of {}: {}", Plugin.class, pluginClass);
        }

        // validation if the class has a public null-arg constructor
        if (Stream.of(pluginClass.getConstructors()).noneMatch(c -> c.getParameterCount() == 0)) {
            throw new KnxPluginException("There seems be no public null-arg constructor available for: {}", pluginClass);
        }
    }

    /**
     * Creates a new instance of the plugin for given {@code pluginClass}.
     * It will do some pre-check before loading based on {@link #checkPluginCompatibility(Class)}
     *
     * @param pluginClass the plugin class to create a new instance; may not be null
     * @return a new instance of plugin
     * @throws KnxPluginException if the plugin could not be loaded
     */
    public static Plugin newInstance(final Class<?> pluginClass) {
        checkPluginCompatibility(pluginClass);

        // now try to load the plugin
        try {
            final var plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
            log.debug("Created a new Plugin instance: {}", plugin);
            return plugin;
        } catch (final ReflectiveOperationException e) {
            throw new KnxPluginException("Could not load plugin: {}", pluginClass.getName(), e);
        }
    }
}
