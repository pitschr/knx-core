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

package li.pitschmann.utils;

import org.slf4j.*;

import javax.annotation.*;
import java.lang.reflect.*;

/**
 * Class Helper
 */
public final class ClassHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ClassHelper.class);

    private ClassHelper() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Returns the generic type class (default index: {@code 0}) from super class of given {@code clazz}
     *
     * @param clazz
     * @return {@code Class<?>} if found
     * @throws IllegalArgumentException in case the class has no generic type class
     */
    public static @Nullable
    <T> Class<T> getGenericTypeSuperclass(final Class<?> clazz) {
        return getGenericTypeSuperclass(clazz, 0);
    }

    /**
     * Returns the {@code index}-th generic type class from super class of given {@code clazz}
     *
     * @param clazz
     * @return {@code Class<T>} if found
     * @throws IllegalArgumentException in case the class is not qualified to get the generic type class
     */
    @SuppressWarnings("unchecked")
    public static @Nullable
    <T> Class<T> getGenericTypeSuperclass(final Class<?> clazz, final int index) {
        final Class<T> retClass;

        final Type superClassType = clazz.getGenericSuperclass();
        if (superClassType instanceof ParameterizedType) {
            final ParameterizedType superClassParamType = (ParameterizedType) superClassType;
            final Type[] typeArguments = superClassParamType.getActualTypeArguments();
            if (typeArguments.length > index) {
                final Type typeArgument = typeArguments[index];
                if (typeArgument instanceof ParameterizedType) {
                    LOG.trace("'ParameterizedType' for class {} and argument type index {}", clazz, index);
                    retClass = (Class<T>) ((ParameterizedType) typeArgument).getRawType();
                } else if (typeArgument instanceof Class) {
                    LOG.trace("'Class' for class {} and argument type index {}", clazz, index);
                    retClass = (Class<T>) typeArgument;
                } else {
                    throw new IllegalArgumentException(
                            String.format("Given class '%s' and generic type on index '%s' could not be resolved.", clazz, index));
                }
            } else {
                throw new IllegalArgumentException(String.format("Generic type index '%s' is out of range for class '%s' (%s types).", index, clazz,
                        typeArguments.length));
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("Given class '%s' is not qualified to get the generic type (index: %s).", clazz, index));
        }

        return retClass;
    }
}
