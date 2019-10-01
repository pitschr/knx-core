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

import li.pitschmann.knx.link.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    /**
     * Returns the Configuration Builder based on following arguments:
     * <ul>
     * <li>{@code -endpoint} ... defined endpoint in {@code <address>:<port>} format.
     * If the address is a multicast, then routing will be used, otherwise tunneling (no NAT)</li>
     * <li>{@code -routing} ... if the communication should be over multicast (routing)</li>
     * <li>{@code -nat} ... if the communication should be using Network Address Translation (tunneling)</li>
     * </ul>
     *
     * @param args
     * @return
     */
    protected Configuration.Builder getConfigurationBuilder(final @Nonnull String[] args) {
        // Argument: Routing enabled?
        final var routingEnabled = existsParameter(args, "-routing");
        log.debug("Routing: {}", routingEnabled);

        // Argument: NAT? (not to be used in routing mode)
        final var natEnabled = existsParameter(args, "-nat");
        log.debug("NAT: {}", natEnabled);

        // Argument: Get KNX Net/IP Address (<address>:<port>)
        final var ipAddress = getParameterValue(args, "-endpoint", Function.identity(), null);
        log.debug("KNX Net/IP Address: {}", ipAddress);

        final Configuration.Builder configBuilder;
        if (ipAddress != null) {
            // specific endpoint defined
            configBuilder = Configuration.create(ipAddress);
        } else if (routingEnabled) {
            // routing
            configBuilder = Configuration.routing();
        } else {
            // tunneling
            configBuilder = Configuration.tunneling();
        }

        if (natEnabled) {
            // enable NAT mode
            configBuilder.nat();
        }

        return configBuilder;
    }

    /**
     * Returns the value of parameter if supplied
     *
     * @param args
     * @param parameterName
     * @param defaultValue  default value in case the parameter could not be found or not parsed correctly
     * @param function
     * @return the value of parameter, otherwise {@code defaultValue}
     */
    @Nullable
    protected <T> T getParameterValue(final @Nonnull String[] args,
                                      final @Nonnull String parameterName,
                                      final @Nonnull Function<String, T> function,
                                      final @Nullable T defaultValue) {
        for (var i = 0; i < args.length; i++) {
            if (parameterName.equalsIgnoreCase(args[i])) {
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
        // not found
        return defaultValue;
    }

    /**
     * Returns the value if parameter exists
     *
     * @param args
     * @param parameterName
     * @return {@code true} if parameter was found, otherwise {@code false}
     */
    protected boolean existsParameter(final @Nonnull String[] args,
                                      final @Nonnull String parameterName) {
        return Arrays.stream(args).anyMatch(arg -> parameterName.equalsIgnoreCase(arg));
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
    @Nullable
    protected <T> T[] getParameterValues(final @Nonnull String[] args,
                                         final @Nonnull String parameterName,
                                         final @Nonnull IntFunction<T[]> function,
                                         final @Nullable T[] defaultValues
    ) {
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
        // not found
        return defaultValues;
    }
}
