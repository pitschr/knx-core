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

package li.pitschmann.knx.link.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import li.pitschmann.knx.link.exceptions.KnxConfigurationException;
import li.pitschmann.knx.link.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Supporter for Configuration for file-based solutions
 * <p/>
 * This class is package-protected and should be called by {@link ConfigBuilder} instance only.
 */
final class ConfigUtil {
    private static final Logger log = LoggerFactory.getLogger(ConfigUtil.class);

    private ConfigUtil() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Creates a {@link ConfigBuilder} from specified {@code filePath}.
     *
     * @param filePath
     * @return a new instance of {@link ConfigBuilder}
     */
    public static ConfigBuilder loadFile(final @Nonnull Path filePath) {
        Preconditions.checkArgument(Files.isReadable(filePath),
                "The file doesn't exists or is not readable: %s", filePath);

        try {
            final var lines = Files.readAllLines(filePath);
            final var allPlugins = asPluginList(lines);
            final var allSettings = asSettingMap(lines);

            // first try to find: "endpoint address" and "endpoint port"
            final var endpointAddress = Strings.nullToEmpty(allSettings.get(ConfigConstants.Endpoint.ADDRESS.getKey()));
            final var endpointPort = Strings.nullToEmpty(allSettings.get(ConfigConstants.Endpoint.PORT.getKey()));

            final var configBuilder = ConfigBuilder.create(endpointAddress + ":" + endpointPort);

            // add plugins
            for (final var plugin : allPlugins) {
                configBuilder.plugin(plugin);
            }

            // add settings
            for (final var setting : allSettings.entrySet()) {
                final var configConstant = ConfigConstants.getConfigConstantByKey(setting.getKey());
                // exclude which are config constants and NOT settable
                if (configConstant == null || configConstant.isSettable()) {
                    configBuilder.setting(setting.getKey(), setting.getValue());
                }
            }
            return configBuilder;
        } catch (final IOException e) {
            throw new KnxConfigurationException("Cannot read or parse file: " + filePath, e);
        }
    }

    /**
     * Returns the list of String lines which should follow the fully qualified class name pattern
     * like {@code my.package.MyClass}
     *
     * @param lines
     * @return list of plugin instances
     */
    private static List<Plugin> asPluginList(final @Nonnull List<String> lines) {
        final var filteredLines = filterBySection(lines, "plugins");
        final var plugins = new ArrayList<Plugin>(filteredLines.size());

        for (final var line : filteredLines) {
            try {
                final var plugin = (Plugin) Class.forName(line).getDeclaredConstructor().newInstance();
                log.info("Plugin loaded: {}", plugin);
                plugins.add(plugin);
            } catch (final Exception notFoundException) {
                throw new KnxConfigurationException("Could not load plugin: " + line);
            }
        }
        return plugins;
    }

    /**
     * Returns the list of String lines which should follow the pattern {@code key=value}
     *
     * @param lines
     * @return map of settings
     */
    @Nonnull
    private static Map<String, String> asSettingMap(final @Nonnull List<String> lines) {
        final var filteredLines = filterBySection(lines, "settings");
        final var settings = Maps.<String, String>newHashMapWithExpectedSize(filteredLines.size());

        for (final var line : filteredLines) {
            final var keyAndValue = line.split("=", 1);
            Preconditions.checkArgument(keyAndValue.length == 2,
                    "It must be a key=value pair, but what I got is: %s", line);
            final var key = keyAndValue[0].trim().toLowerCase();
            final var value = keyAndValue[1].trim();
            settings.put(key, value);
        }

        return settings;
    }

    /**
     * Returns a list of lines for a specific section. Lines which are outside of this section
     * are filtered. Blank lines are also not considered.
     *
     * @param lines
     * @param sectionName
     * @return filtered list
     */
    private static List<String> filterBySection(final @Nonnull List<String> lines, final @Nonnull String sectionName) {
        final var filteredLines = new ArrayList<String>(lines.size());
        boolean sectionFound = false;
        for (final var line : lines) {
            if (line.equals("[" + sectionName + "]")) {
                sectionFound = true;
                continue;
            } else if (line.isBlank() || !sectionFound) {
                continue;
            } else if (line.startsWith("[")) {
                // another section starts
                break;
            } else {
                filteredLines.add(line);
            }
        }
        return filteredLines;
    }
}
