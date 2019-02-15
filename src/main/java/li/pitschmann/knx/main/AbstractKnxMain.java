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

import li.pitschmann.knx.link.body.address.GroupAddress;
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
        for (int i = 0; i < args.length; i++) {
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
        for (int i = 0; i < args.length; i++) {
            if (parameterName.equalsIgnoreCase(args[i])) {
                // found - next arguments should be the values
                if ((i + 1) < args.length) {
                    int start = i + 1;
                    int end = args.length;
                    for (int j = start; j < args.length; j++) {
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

    /**
     * Converts from either {@code x/y/z} to a <strong>3-level</strong> {@link GroupAddress} or {@code x/y} to a
     * <strong>2-level</strong> {@link GroupAddress}
     * <p>
     * For 3-level address the value range is {@code [0/0/0, 15/7/255]}<br>
     * For 2-level address the value range is {@code [0/0, 0/2047]}<br>
     *
     * @param groupAddress
     * @return {@link GroupAddress}
     */
    protected static GroupAddress parseGroupAddress(final String groupAddress) {
        final String[] groupAddressAreas = groupAddress.split("/");
        if (groupAddressAreas.length == 3) {
            return GroupAddress.of( //
                    Integer.valueOf(groupAddressAreas[0]), //
                    Integer.valueOf(groupAddressAreas[1]), //
                    Integer.valueOf(groupAddressAreas[2]) //
            );
        } else if (groupAddressAreas.length == 2) {
            return GroupAddress.of( //
                    Integer.valueOf(groupAddressAreas[0]), //
                    Integer.valueOf(groupAddressAreas[1]) //
            );
        }
        throw new IllegalArgumentException("Invalid Group Address provided: " + groupAddress);
    }
}
