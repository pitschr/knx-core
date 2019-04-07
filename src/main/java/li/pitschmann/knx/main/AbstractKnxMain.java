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

package li.pitschmann.knx.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * Abstract class for KNX Main classes
 *
 * @author PITSCHR
 */
public abstract class AbstractKnxMain {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractKnxMain.class);

    /**
     * Returns the value of parameter if supplied
     *
     * @param args
     * @param parameterName
     * @param defaultValue  default value in case the parameter could not be found or not parsed correctly
     * @param function
     * @return the value of parameter, otherwise {@code defaultValue}
     */
    protected static <T> T getParameterValue(final String[] args, final String parameterName, final T defaultValue,
                                             final Function<String, T> function) {
        for (var i = 0; i < args.length; i++) {
            if (parameterName.equalsIgnoreCase(args[i])) {
                // found - next argument should be the value
                if ((i + 1) < args.length) {
                    try {
                        return function.apply(args[i + 1]);
                    } catch (final Throwable t) {
                        LOG.info("Could not parse value '{}'. Default value to be returned: {}", args[i + 1], defaultValue);
                        // could not be parsed
                        return defaultValue;
                    }
                }
            }
        }
        // not found
        return defaultValue;
    }

    /**
     * Returns the value of parameter if supplied
     *
     * @param args
     * @param parameterName
     * @param defaultValues default values in case the parameter could not be found or not parsed correctly
     * @param function
     * @return the value of parameter, otherwise {@code defaultValue}
     */
    // @SuppressWarnings("unchecked")
    protected static <T> T[] getParameterValues(final String[] args, final String parameterName, final T[] defaultValues,
                                                final IntFunction<T[]> function) {
        for (var i = 0; i < args.length; i++) {
            if (parameterName.equalsIgnoreCase(args[i])) {
                // found - next arguments should be the values
                if ((i + 1) < args.length) {
                    final var start = i + 1;
                    var end = args.length;
                    for (var j = start; j < args.length; j++) {
                        if (args[j].startsWith("-")) {
                            // next argument name found
                            end = j - 1;
                        }
                    }

                    LOG.debug("Values [Start: {}, End: {}]", start, end);
                    try {
                        return Stream.of(args).skip(start).limit(end - start).toArray(function::apply);
                    } catch (final Throwable t) {
                        LOG.info("Could not parse value '{}'. Default value to be returned: {}", args[i + 1], Arrays.toString(defaultValues));
                        // could not be parsed
                        return defaultValues;
                    }
                }
            }
        }
        // not found
        return defaultValues;
    }
}
