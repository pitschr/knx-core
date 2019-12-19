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
    private final String id;
    private final String description;
    private final String unit;

    public AbstractDataPointType(final String id, final String description) {
        this(id, description, null);
    }

    public AbstractDataPointType(final String id, final String description, final @Nullable String unit) {
        this.id = Objects.requireNonNull(id);
        this.description = Objects.requireNonNull(description);
        this.unit = unit;
    }

    @Override
    public final String getId() {
        return this.id;
    }

    @Override
    public final String getDescription() {
        if (this.unit == null) {
            return this.description;
        } else {
            return this.description + " (" + this.unit + ")";
        }
    }

    @Override
    public final String getUnit() {
        return Objects.toString(this.unit, "");
    }

    /**
     * Returns a {@link DataPointValue} for specified byte array.
     *
     * @param bytes
     * @return data point value
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
     * @param bytes
     * @return {@code true} if compatible, otherwise {@code false}
     */
    protected abstract boolean isCompatible(final byte[] bytes);

    /**
     * Parses the {@code bytes} to an instance of {@code <V>}
     *
     * @param bytes
     * @return {@link DataPointValue}
     */
    protected abstract V parse(final byte[] bytes);

    /**
     * Returns a {@link DataPointValue} for specified string arguments.
     * <p>
     * Per default it just parses the string arguments as hex string. This behavior can differ for the specific DPT
     * class.
     *
     * @param args arguments to be parsed
     * @return data point value
     * @throws DataPointTypeIncompatibleSyntaxException to be thrown if the arguments could not be interpreted
     */
    public final V toValue(final String[] args) {
        Preconditions.checkNonNull(args, "No arguments provided for conversion to data point value object.");

        // not compatible?
        boolean isCompatible;
        try {
            isCompatible = this.isCompatible(args);
        } catch (final Throwable t) {
            log.debug("Throwable during isCompatible(String[]) check: {}: {}", t.getClass(), t.getMessage());
            isCompatible = false;
        }
        if (!isCompatible) {
            if (log.isDebugEnabled()) {
                log.debug("Incompatible arguments for parse(String[]). Try with parse as hex string: {}", Arrays.toString(args));
            }
            try {
                // it may be a hex string -> try to parse it!
                return this.tryParseAsHexString(args);
            } catch (final Throwable t) {
                log.warn("Could not parse hex string for following arguments: {}", Arrays.toString(args));
                throw new DataPointTypeIncompatibleSyntaxException(this, args);
            }
        }

        // all OK, now let's parse it
        try {
            return this.parse(args);
        } catch (final Throwable throwable) {
            log.debug("Could not parse following arguments: {}", Arrays.toString(args));
            try {
                return this.tryParseAsHexString(args);
            } catch (final Throwable throwable2) {
                log.warn("Throwable during tryParseAsHexString(String[]): {}", throwable.getClass(), throwable2);
                throw new DataPointTypeIncompatibleSyntaxException(this, args);
            }
        }
    }

    /**
     * Checks if the given {@code args} is compatible with the current DPT
     *
     * @param args
     * @return {@code true} if compatible, otherwise {@code false}
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
     * @param args
     * @param enumClass
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
     * @param args
     * @param pattern
     * @param function if found then convert to desired object instance
     * @return found and converted object instance, otherwise {@code null} if not found
     */
    @Nullable
    protected final <T> T findByPattern(final String[] args, final Pattern pattern, final Function<String, T> function) {
        return findByPattern(args, pattern, function, null);
    }

    /**
     * Returns the first matching by pattern
     *
     * @param args
     * @param pattern
     * @param function     if found then convert to desired object instance
     * @param defaultValue value to be returned in case the pattern could not be found
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
     * @param args
     * @param searchString
     * @param moreSearchStrings
     * @return {@code true} if found, otherwise {@code false}
     */
    protected final boolean findByString(final String[] args, final String searchString, final String... moreSearchStrings) {
        for (var i = 0; i < args.length; i++) {
            if (searchString.equalsIgnoreCase(args[i])) {
                return true;
            }
            for (final String moreSearchString : moreSearchStrings) {
                if (moreSearchString.equalsIgnoreCase(args[i])) {
                    return true;
                }
            }
        }
        return false; // not found
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("id", this.id)
                .add("description", this.getDescription())
                .toString();
        // @formatter:on
    }

    /**
     * When comparing we take care of the id only.
     *
     * @param obj return {@code true} if equals, otherwise {@code false}
     */
    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof AbstractDataPointType) {
            final var other = (AbstractDataPointType) obj;
            return this.id.equals(other.id);
        }
        return false;
    }

    /**
     * Returns the hash code of {@link #id}
     *
     * @return hash code of {@link #id}
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
