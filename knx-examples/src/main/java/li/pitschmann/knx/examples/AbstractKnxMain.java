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

package li.pitschmann.knx.examples;

import li.pitschmann.knx.core.config.ConfigBuilder;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import li.pitschmann.knx.core.annotations.Nullable;
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
    protected final Logger logRoot = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected AbstractKnxMain() {
        ((ch.qos.logback.classic.Logger) log).setLevel(ch.qos.logback.classic.Level.ALL);
        ((ch.qos.logback.classic.Logger) logRoot).setLevel(ch.qos.logback.classic.Level.OFF);
    }

    /**
     * Returns the Configuration Builder based on following arguments:
     * <ul>
     *      <li>{@code --ip} ... defined endpoint in {@code <address>:<port>} format.
     *      If the address is a multicast, then routing will be used, otherwise tunneling (no NAT)</li>
     *      <li>{@code --routing} ... if the communication should be over multicast (routing)</li>
     *      <li>{@code --nat} ... if the communication should be using Network Address Translation (tunneling)</li>
     * </ul>
     *
     * @param args arguments
     * @return a new instance of {@link ConfigBuilder}
     */
    protected ConfigBuilder parseConfigBuilder(final String[] args) {
        // Argument: Routing enabled?
        final var routingEnabled = existsParameter(args, "--routing");
        log.debug("Routing: {}", routingEnabled);

        // Argument: NAT? (not to be used in routing mode)
        final var natEnabled = existsParameter(args, "--nat");
        log.debug("NAT: {}", natEnabled);

        // Argument: Get KNX Net/IP Address (<address>:<port>)
        final var ipAddress = getParameterValue(args, "--ip", Function.identity(), null);
        log.debug("KNX Net/IP Address: {}", ipAddress);

        if (ipAddress != null) {
            Preconditions.checkState(!routingEnabled, "You cannot use tunneling and routing at same time!");
            // specific endpoint defined
            // decision of routing/tunneling will be done based on ip address
            return ConfigBuilder.create(ipAddress).setting(CoreConfigs.NAT, natEnabled);
        } else if (routingEnabled) {
            Preconditions.checkState(!natEnabled, "NAT is available for tunneling only!");
            // routing
            return ConfigBuilder.routing();
        } else {
            // tunneling (with NAT / without NAT)
            return ConfigBuilder.tunneling(natEnabled);
        }
    }

    /**
     * Returns the value of parameter if supplied
     *
     * @param args arguments
     * @param parameterNames parameter names, may be comma-separated
     * @param defaultValue   default value in case the parameter could not be found or not parsed correctly
     * @param function to be used for conversion from String to {@code <T>} value type
     * @param <T> type of value
     * @return the value of parameter, otherwise {@code defaultValue}
     */
    @Nullable
    protected <T> T getParameterValue(final String[] args,
                                      final String parameterNames,
                                      final Function<String, T> function,
                                      final @Nullable T defaultValue) {
        for (final var parameterName : parameterNames.split(",")) {
            for (var i = 0; i < args.length; i++) {
                if (parameterName.equals(args[i])) {
                    // found - next argument should be the value
                    if ((i + 1) < args.length) {
                        try {
                            return function.apply(args[i + 1]);
                        } catch (final Throwable t) {
                            log.info("Could not parse value '{}'. Default value to be returned: {}", args[i + 1], defaultValue);
                            // could not be parsed
                            return defaultValue;
                        }
                    }
                }
            }
        }
        // not found
        return defaultValue;
    }

    /**
     * Returns the value if parameter exists
     *
     * @param args arguments
     * @param parameterNames parameter names, may be comma-separated
     * @return {@code true} if parameter was found, otherwise {@code false}
     */
    protected boolean existsParameter(final String[] args,
                                      final String parameterNames) {
        for (final var parameterName : parameterNames.split(",")) {
            if (Arrays.stream(args).anyMatch(parameterName::equals)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value of parameter if supplied
     *
     * @param args arguments
     * @param parameterNames parameter names, may be comma-separated
     * @param defaultValues  default values in case the parameter could not be found or not parsed correctly
     * @param function to be used for conversion from String to {@code <T>} value type
     * @param <T> type of value
     * @return the value of parameter, otherwise {@code defaultValue}
     */
    @Nullable
    protected <T> T[] getParameterValues(final String[] args,
                                         final String parameterNames,
                                         final IntFunction<T[]> function,
                                         final @Nullable T[] defaultValues
    ) {
        for (final var parameterName : parameterNames.split(",")) {
            for (var i = 0; i < args.length; i++) {
                if (parameterName.equals(args[i])) {
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

                        log.debug("Values [Start: {}, End: {}]", start, end);
                        try {
                            return Stream.of(args).skip(start).limit(end - start).toArray(function::apply);
                        } catch (final Throwable t) {
                            log.info("Could not parse value '{}'. Default value to be returned: {}", args[i + 1], Arrays.toString(defaultValues));
                            // could not be parsed
                            return defaultValues;
                        }
                    }
                }
            }
        }
        // not found
        return defaultValues;
    }
}
