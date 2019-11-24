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

package li.pitschmann.knx.core.enums;

import li.pitschmann.knx.core.KnxByteEnum;
import li.pitschmann.knx.core.KnxEnum;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Abstract Test for {@link KnxEnum} classes
 *
 * @param <E>
 * @author PITSCHR
 */
abstract class AbstractKnxEnumTest<E extends Enum<E> & KnxEnum> {
    @SuppressWarnings("unchecked")
    private final Class<E> currentClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    /**
     * Returns the current class of {@link KnxEnum}
     *
     * @return
     */
    protected Class<E> getCurrentClass() {
        return this.currentClass;
    }

    /**
     * Number of enum elements
     */
    protected abstract int numberOfElements();

    /**
     * Tests if the number of enum elements equals to {@link #numberOfElements()}. This method is more used to ensure
     * that we won't forget to update the test class when the enum class will be modified.
     */
    @Test
    public void testNumberOfElements() {
        assertThat(EnumSet.allOf(this.currentClass)).hasSize(this.numberOfElements());
    }

    /**
     * Tests the all {@link KnxEnum#getCode()} static method
     */
    @Test
    public void allValueOf() {
        for (final E identifier : EnumSet.allOf(this.currentClass)) {
            assertThat(this.invokeValueOf(identifier.getCode())).isEqualTo(identifier);
        }
    }

    /**
     * Tests the {@code valueOf(int)} static method with <strong>valid</strong> samples
     */
    @Test
    abstract void validValueOf();

    /**
     * Tests the {@code valueOf(int)} static method with <strong>invalid</strong> samples
     */
    @Test
    abstract void invalidValueOf();

    /**
     * Tests the {@link KnxByteEnum#getFriendlyName()}
     */
    @Test
    abstract void friendlyName();

    /**
     * Tests the {@link #toString()}
     */
    @Test
    abstract void testToString();

    /**
     * Invokes the static {@code valueOf(int)} method of KNX Enum class.
     *
     * @param value
     * @return {@code KnxEnum} if found, otherwise {@link KnxException} is thrown
     */
    protected KnxEnum invokeValueOf(final Object value) {
        try {
            return (KnxEnum) this.currentClass.getDeclaredMethod("valueOf", int.class).invoke(null, value);
        } catch (final InvocationTargetException ex) {
            if (ex.getCause() instanceof KnxEnumNotFoundException) {
                throw (KnxEnumNotFoundException) ex.getCause();
            } else {
                fail("Could not invoke the 'valueOf'", ex);
            }
        } catch (final Throwable t) {
            fail("Could not invoke the 'valueOf'", t);
        }
        return null;
    }
}
