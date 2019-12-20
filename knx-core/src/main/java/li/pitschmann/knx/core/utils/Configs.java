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

package li.pitschmann.knx.core.utils;

import li.pitschmann.knx.core.config.ConfigValue;
import li.pitschmann.knx.core.config.InternalConfigValue;
import li.pitschmann.knx.core.exceptions.KnxConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility for configuration
 */
public final class Configs {
    private static final Logger log = LoggerFactory.getLogger(Configs.class);

    private Configs() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Parses the given {@link Class} for all public+static+final fields that
     * are instance of {@link ConfigValue}
     *
     * @param clazz the class to be scanned
     * @return immutable map of config key and {@link ConfigValue}
     */
    public static List<ConfigValue<Object>> getConfigValues(final Class<?> clazz) {
        final var list = new LinkedList<ConfigValue<Object>>();

        // get config value fields from current class
        for (final var field : clazz.getFields()) {
            if (Modifier.isPublic(field.getModifiers())
                    && Modifier.isStatic(field.getModifiers())
                    && Modifier.isFinal(field.getModifiers())) {
                try {
                    final var obj = field.get(null);
                    if (obj instanceof InternalConfigValue) {
                        log.debug("Field '{}' is ignored because it is an instance of InternalConfigValue", field.getName());
                    } else if (obj instanceof ConfigValue) {
                        @SuppressWarnings("unchecked")
                        final var configValue = (ConfigValue<Object>) obj;
                        list.add(configValue);
                        log.trace("Field '{}' added to list: {}", field.getName(), configValue);
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
            list.addAll(getConfigValues(subClass));
        }

        return List.copyOf(list);
    }

    /**
     * Parses the given {@link Class} for all public+static+final fields that
     * are instance of {@link ConfigValue} and return them as a unmodifiable map
     * with fully qualified config key as key and config value itself as value.
     *
     * @param clazz the class to be scanned
     * @return immutable map
     */
    public static Map<String, ConfigValue<Object>> getConfigMapValues(final Class<?> clazz) {
        return getConfigValues(clazz).stream().collect(Collectors.toUnmodifiableMap(
                ConfigValue::getKey, // fully qualified config key
                Function.identity()  // element itself
        ));
    }
}
