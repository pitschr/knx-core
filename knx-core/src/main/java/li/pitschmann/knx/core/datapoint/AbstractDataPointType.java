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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.value.DataPointValue;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base {@link DataPointType} containing common id and description data
 *
 * @author PITSCHR
 */
public abstract class AbstractDataPointType<V extends DataPointValue<?>> implements DataPointType<V> {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final String description;
    private final String unit;

    public AbstractDataPointType(final String description) {
        this(description, null);
    }

    public AbstractDataPointType(final String description, final @Nullable String unit) {
        this.description = Objects.requireNonNull(description);
        this.unit = unit;
    }

    @Override
    public String getId() {
        return DataPointRegistry.getDataPointIdentifiers(this)[0];
    }

    @Override
    public String getDescription() {
        if (this.unit == null) {
            return this.description;
        } else {
            return this.description + " (" + this.unit + ")";
        }
    }

    @Override
    public String getUnit() {
        return Objects.toString(this.unit, "");
    }

    /**
     * Returns a {@link DataPointValue} for specified byte array.
     *
     * @param bytes raw bytes
     * @return data point value from raw bytes
     * @throws DataPointTypeIncompatibleBytesException to be thrown if wrong byte array structure was provided
     */
    public final V toValue(final byte[] bytes) {
        if (bytes == null) {
            throw new KnxNullPointerException("bytes");
        }
        // up to 255 bytes supported only
        else if (bytes.length > 0xFF) {
            throw new KnxNumberOutOfRangeException("bytes.length", 0, 0xFF, bytes.length);
        }
        // not compatible?
        else if (!this.isCompatible(bytes)) {
            throw new DataPointTypeIncompatibleBytesException(this, bytes);
        }

        // all OK, now let's parse it
        return this.parse(bytes);
    }

    /**
     * Checks if the given {@code bytes} is compatible with the current DPT
     *
     * @param bytes raw bytes
     * @return {@code true} if raw bytes are compatible, otherwise {@code false}
     */
    protected abstract boolean isCompatible(final byte[] bytes);

    /**
     * Parses the {@code bytes} to an instance of {@code <V>}
     *
     * @param bytes raw bytes to be parsed
     * @return {@link DataPointValue} from raw bytes
     */
    protected abstract V parse(final byte[] bytes);

    /**
     * Returns a {@link DataPointValue} for specified string arguments.
     * <p>
     * Per default it just parses the string arguments as hex string.
     * This behavior can differ for the specific DPT class.
     *
     * @param args arguments to be parsed
     * @return data point value
     * @throws DataPointTypeIncompatibleSyntaxException to be thrown if the arguments could not be interpreted
     */
    public final V toValue(final String[] args) {
        Preconditions.checkArgument(args!=null && args.length > 0,
                "No arguments provided for conversion to data point value object.");

        // check if it is a hex string
        if (args[0].startsWith("0x")) {
            // looks like a hex-string
            return tryParseAsHexString(args);
        }
        // not a string
        else if (this.isCompatible(args)) {
            // seems be ok -> try parse it!
            try {
                return parse(args);
            } catch (final Exception ex) {
                log.debug("Incompatible arguments for parse(String[]): {}", Arrays.toString(args));
            }
        }
        throw new DataPointTypeIncompatibleSyntaxException(this, args);
    }

    /**
     * Checks if the given {@code args} is compatible with the current DPT
     *
     * @param args arguments
     * @return {@code true} if arguments are compatible, otherwise {@code false}
     */
    protected boolean isCompatible(final String[] args) {
        return false;
    }

    /**
     * Parses the {@code args} to an instance of {@code <V>}
     *
     * @param args arguments to be parsed
     * @return data point value
     */
    protected V parse(final String[] args) {
        throw new UnsupportedOperationException();
    }

    /**
     * Try to parse the hex-decimal string arguments. The hex string must start with {@code 0x} prefix then it will be
     * considered as hex string.
     *
     * @param args hex string as array
     * @return data point value if success. It may throw a {@link IllegalArgumentException} in case the {@code args} is
     * not well-formatted for hex string
     */
    private V tryParseAsHexString(final String[] args) {
        Preconditions.checkArgument(args[0].startsWith("0x"), "Hex string should start with '0x'. Actual: {}", args[0]);
        final var joinedString = Stream.of(args).map(arg -> arg.replaceFirst("0x", "")).collect(Collectors.joining());
        return this.toValue(Bytes.toByteArray(joinedString));
    }

    /**
     * Returns the first matching enumeration constant
     *
     * @param args      array of arguments
     * @param enumClass the enumeration class that should be used for finding enum constant
     * @param <E>       the enum type we are looking for
     * @return first matching enumeration constant, {@code null} if not found
     */
    @Nullable
    protected final <E extends Enum<E>> E findByEnumConstant(final String[] args, final Class<E> enumClass) {
        for (var i = 0; i < args.length; i++) {
            final var arg = args[i].toUpperCase();
            for (final var enumConstant : enumClass.getEnumConstants()) {
                if (enumConstant.name().equals(arg)) {
                    return enumConstant;
                }
            }
        }
        return null; // not found
    }

    /**
     * Returns the first matching by pattern
     *
     * @param args     array of arguments
     * @param pattern  pattern to find the suitable argument
     * @param function if found then convert to desired object instance
     * @param <T>      the value type we are looking for
     * @return found and converted object instance, otherwise {@code null} if not found
     */
    @Nullable
    protected final <T> T findByPattern(final String[] args, final Pattern pattern, final Function<String, T> function) {
        return findByPattern(args, pattern, function, null);
    }

    /**
     * Returns the first matching by pattern
     *
     * @param args         array of arguments
     * @param pattern      pattern to find the suitable argument
     * @param function     if found then convert to desired object instance
     * @param defaultValue value to be returned in case the pattern could not be found
     * @param <T>          the value type we are looking for
     * @return found and converted object instance, otherwise {@code defaultValue} if not found
     */
    @Nullable
    protected final <T> T findByPattern(final String[] args, final Pattern pattern, final Function<String, T> function, final @Nullable T defaultValue) {
        for (var i = 0; i < args.length; i++) {
            if (pattern.matcher(args[i]).matches()) {
                return function.apply(args[i]);
            }
        }
        return defaultValue;
    }

    /**
     * Returns if the given {@code searchString} or {@code moreSearchStrings} was found in argument array.
     *
     * @param args              array of arguments
     * @param searchString      first search string to find the suitable argument
     * @param moreSearchStrings alternative search strings to find the suitable argument
     * @return {@code true} if found, otherwise {@code false}
     */
    protected final boolean findByString(final String[] args, final String searchString, final @Nullable String... moreSearchStrings) {
        for (var i = 0; i < args.length; i++) {
            if (searchString.equalsIgnoreCase(args[i])) {
                return true;
            }
            if (moreSearchStrings != null) {
                for (final String moreSearchString : moreSearchStrings) {
                    if (moreSearchString.equalsIgnoreCase(args[i])) {
                        return true;
                    }
                }
            }
        }
        return false; // not found
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("id", this.getId())
                .add("description", this.getDescription())
                .toString();
        // @formatter:on
    }
}
