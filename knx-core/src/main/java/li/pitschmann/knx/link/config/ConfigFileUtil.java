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

import li.pitschmann.knx.link.exceptions.KnxConfigurationException;
import li.pitschmann.knx.link.plugin.Plugin;
import li.pitschmann.utils.Maps;
import li.pitschmann.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Supporter for Configuration for file-based solutions
 * <p/>
 * This class is package-protected and should be called by {@link ConfigBuilder} instance only.
 */
final class ConfigFileUtil {
    private static final Logger log = LoggerFactory.getLogger(ConfigFileUtil.class);

    private ConfigFileUtil() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Creates a {@link ConfigBuilder} from specified {@code filePath}.
     *
     * @param filePath
     * @return a new instance of {@link ConfigBuilder}
     */
    static ConfigBuilder loadFile(final @Nonnull Path filePath) {
        Preconditions.checkArgument(Files.isReadable(filePath),
                "The file doesn't exists or is not readable: {}", filePath);

        try {
            final var lines = Files.readAllLines(filePath);
            final var allPlugins = asPluginList(lines);
            final var allSettings = asSettingMap(lines);

            // first try to find: "endpoint address" and "endpoint port"
            final var endpointAddress = Objects.requireNonNullElse(allSettings.get(ConfigConstants.Endpoint.ADDRESS.getKey()), "");
            final var endpointPort = Objects.requireNonNullElse(allSettings.get(ConfigConstants.Endpoint.PORT.getKey()), "");

            final var configBuilder = ConfigBuilder.create(endpointAddress + ":" + endpointPort);

            // all registered config values
            final var allRegisteredConfigValues = Maps.<String, ConfigValue<Object>>newHashMap(allPlugins.size() * 32);
            allRegisteredConfigValues.putAll(ConfigConstants.getConfigValues());

            // add plugins
            for (final var plugin : allPlugins) {
                configBuilder.plugin(plugin);
                allRegisteredConfigValues.putAll(getConfigValues(plugin));
            }

            // add settings
            for (final var setting : allSettings.entrySet()) {
                final var configValue = allRegisteredConfigValues.get(setting.getKey());
                if (configValue != null) {
                    final var settingValue = configValue.convert(setting.getValue());
                    configBuilder.setting(configValue, settingValue);
                    log.debug("Config '{}' loaded with value: {}", configValue.getKey(), settingValue);
                } else {
                    log.debug("Config '{}' is not registered. Ignored!", setting.getKey());
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
     * @return list of {@link Plugin} classes
     */
    private static List<Class<Plugin>> asPluginList(final @Nonnull List<String> lines) {
        final var filteredLines = filterBySection(lines, "plugins");
        final var plugins = new ArrayList<Class<Plugin>>(filteredLines.size());

        for (final var line : filteredLines) {
            try {
                @SuppressWarnings("unchecked")
                final var pluginClass = (Class<Plugin>) Class.forName(line);
                log.info("Plugin class: {}", pluginClass);
                plugins.add(pluginClass);
            } catch (final Exception ex) {
                throw new KnxConfigurationException("Could not load plugin: " + line, ex);
            }
        }
        return plugins;
    }

    /**
     * Returns the list of String lines which should follow the pattern {@code key=value}
     * <p/>
     *
     * @param lines
     * @return map of settings, key is a lower-cased and trimmed {@link String}, value is trimmed {@link String}
     */
    @Nonnull
    private static Map<String, String> asSettingMap(final @Nonnull List<String> lines) {
        final var filteredLines = filterBySection(lines, "settings");
        final var settings = Maps.<String, String>newHashMap(filteredLines.size());

        for (final var line : filteredLines) {
            final var keyAndValue = line.split("=", 2);
            if (keyAndValue.length != 2) {
                throw new KnxConfigurationException("It must be a key=value pair, but what I got is: " + line);
            }
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

    /**
     * Parses the given {@link Class} for all public+static+final fields that are instanec of {@link ConfigValue}
     *
     * @param clazz
     * @return map of config key and {@link ConfigValue}
     */
    static Map<String, ConfigValue<Object>> getConfigValues(final @Nonnull Class<?> clazz) {
        final var map = new HashMap<String, ConfigValue<Object>>();

        // get config value fields from current class
        for (final var field : clazz.getFields()) {
            if (Modifier.isPublic(field.getModifiers())
                    && Modifier.isStatic(field.getModifiers())
                    && Modifier.isFinal(field.getModifiers())) {
                try {
                    final var obj = field.get(null);
                    if (obj instanceof ConfigValue) {
                        @SuppressWarnings("unchecked") final var configValue = (ConfigValue<Object>) obj;
                        map.put(configValue.getKey(), configValue);
                        log.trace("Field '{}' added to map: {}", field.getName(), configValue);
                    }
                } catch (final ReflectiveOperationException e) {
                    throw new KnxConfigurationException("Could not load field '" + field.getName() + "' from class '" + clazz.getName() + "'");
                }
            } else {
                log.trace("Field '{}' ignored because it is not 'public static final'", field.getName());
            }
        }

        // get config constants from sub-class
        for (final var subClass : clazz.getClasses()) {
            map.putAll(getConfigValues(subClass));
        }

        return map;
    }
}
