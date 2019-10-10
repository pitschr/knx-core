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

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An immutable Constant Value Holder for Configuration containing
 * <ul>
 *     <li>{@code key} ... fully qualified name of key, will be lower-cased</li>
 *     <li>{@code classType} ... for type of class, also used for casting</li>
 *     <li>{@code defaultValue} ... default Value</li>
 *     <li>{@code settable} ... flag if the value of config key can be set
 *           via {@link ConfigBuilder#setting(String, Object)}</li>
 * </ul>
 *
 * @param <T>
 */
public final class ConfigConstant<T> {
    private final String key;
    private final Class<T> classType;
    private final T defaultValue;
    private final Function<String, T> conversion;
    private final boolean settable;

    public ConfigConstant(final @Nonnull String key, final @Nonnull Class<T> classType, final @Nonnull T defaultValue, final @Nonnull Function<String, T> conversion) {
        this(key, classType, defaultValue, conversion, true);
    }

    public ConfigConstant(final @Nonnull String key, final @Nonnull Class<T> classType, final @Nonnull T defaultValue, final @Nonnull Function<String, T> conversion, final boolean settable) {
        this.key = Objects.requireNonNull(key).toLowerCase();
        this.classType = Objects.requireNonNull(classType);
        this.defaultValue = Objects.requireNonNull(defaultValue);
        this.conversion = Objects.requireNonNull(conversion);
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
        return defaultValue;
    }

    /**
     * Converts from a {@link String} representation to an instance of type {@code <T>}
     *
     * @param value
     * @return value with an instance type of {@code <T>}
     */
    @Nonnull
    public T convert(final String value) {
        return this.conversion.apply(value);
    }

    public boolean isSettable() {
        return settable;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", this.key)
                .add("settable", this.settable)
                .add("classType", this.classType)
                .add("defaultValue", this.defaultValue)
                .toString();
    }
}

