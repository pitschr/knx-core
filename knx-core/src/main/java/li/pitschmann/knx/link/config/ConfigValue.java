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

import li.pitschmann.utils.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An immutable Config Value Holder for Configuration containing
 * <ul>
 * <li>{@code key} ... fully qualified name of key, will be lower-cased</li>
 * <li>{@code classType} ... for type of class, also used for casting</li>
 * <li>{@code defaultSupplier} ... supplier for default value</li>
 * <li>{@code settable} ... flag if the value of config key can be set
 * </ul>
 *
 * @param <T>
 */
public class ConfigValue<T> {
    private final String key;
    private final Class<T> classType;
    private final Supplier<T> defaultSupplier;
    private final Function<String, T> converter;
    private final Predicate<T> predicate;
    private final boolean settable;

    protected ConfigValue(
            final @Nonnull String key,
            final @Nonnull Class<T> classType,
            final @Nonnull Function<String, T> converter,
            final @Nonnull Supplier<T> defaultSupplier,
            final @Nullable Predicate<T> predicate,
            final boolean settable) {
        this.key = Objects.requireNonNull(key).toLowerCase();
        this.classType = Objects.requireNonNull(classType);
        this.converter = Objects.requireNonNull(converter);
        this.defaultSupplier = Objects.requireNonNull(defaultSupplier);
        this.predicate = predicate;
        this.settable = settable;
    }

    /**
     * Key in lower-case
     *
     * @return
     */
    @Nonnull
    public String getKey() {
        return key;
    }

    @Nonnull
    public Class<T> getClassType() {
        return classType;
    }

    @Nonnull
    public T getDefaultValue() {
        return defaultSupplier.get();
    }

    public Function<String, T> getConverter() {
        return this.converter;
    }

    public Predicate<T> getPredicate() {
        return this.predicate;
    }

    public boolean isSettable() {
        return settable;
    }


    /**
     * Returns {@code true} if the given value is valid/applicable for the config.
     *
     * @param value value to be tested
     * @return {@code true} if valid/applicable, otherwise {@code false}
     */
    public boolean isValid(final T value) {
        return predicate == null || predicate.test(value);
    }

    /**
     * Converts from a {@link String} representation to an instance of type {@code <T>}
     *
     * @param value
     * @return value with an instance type of {@code <T>}
     */
    @Nonnull
    public T convert(final String value) {
        return this.converter.apply(value);
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("key", this.key)
                .add("settable", this.settable)
                .add("classType", this.classType)
                .add("converter", this.converter)
                .add("defaultValue", this.getDefaultValue())
                .add("predicate", this.predicate)
                .toString();
    }
}

