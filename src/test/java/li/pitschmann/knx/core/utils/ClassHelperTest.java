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

package li.pitschmann.knx.core.utils;

import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for {@link ClassHelper} class
 *
 * @author PITSCHR
 */
public class ClassHelperTest {

    /**
     * Test {@link ClassHelper#getGenericTypeSuperclass(Class)}
     */
    @Test
    public void getGenericClass() {
        final var str = new TestStringClass();
        assertThat(str.getClassType().getTypeName()).isEqualTo(String.class.getName());
        assertThat(str.getClassType2().getTypeName()).isEqualTo(CharSequence.class.getName());
        assertThat(str.getClassType3().getTypeName()).isEqualTo(Comparable.class.getName());

        final var num = new TestNumberClass();
        assertThat(num.getClassType().getTypeName()).isEqualTo(Number.class.getName());
        assertThat(num.getClassType2().getTypeName()).isEqualTo(Integer.class.getName());
        assertThat(num.getClassType3().getTypeName()).isEqualTo(Void.class.getName());
    }

    /**
     * Test {@link ClassHelper#getGenericTypeSuperclass(Class)} with some arguments causing failure.
     */
    @Test
    public void getGenericClassInvalid() {
        // TestStringClass supports only three generics, a fourth generic is not available
        assertThatThrownBy(() -> ClassHelper.getGenericTypeSuperclass(TestStringClass.class, 3)).isInstanceOf(IllegalArgumentException.class);

        // no generic class at all
        assertThatThrownBy(() -> ClassHelper.getGenericTypeSuperclass(String.class)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ClassHelper.getGenericTypeSuperclass(Object.class)).isInstanceOf(IllegalArgumentException.class);

        // unknown T generic type
        assertThatThrownBy(() -> ClassHelper.getGenericTypeSuperclass(TestFailureClass.class, 2)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test constructor of {@link ClassHelper}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(ClassHelper.class);
    }

    private class TestStringClass extends AbstractClass<String, CharSequence, Comparable<CharSequence>> {
        // empty
    }

    private class TestNumberClass extends AbstractClass<Number, Integer, Void> {
        // empty
    }

    private class TestFailureClass<T extends Object> extends AbstractClass<Object, Supplier<Comparable<Object>>, T> {
        // empty
    }

    private abstract class AbstractClass<O, O2, O3> {
        private final Class<O> classType = ClassHelper.getGenericTypeSuperclass(this.getClass());
        private final Class<O2> classType2 = ClassHelper.getGenericTypeSuperclass(this.getClass(), 1);
        private final Class<O2> classType3 = ClassHelper.getGenericTypeSuperclass(this.getClass(), 2);

        public Class<O> getClassType() {
            return this.classType;
        }

        public Class<O2> getClassType2() {
            return this.classType2;
        }

        public Class<O2> getClassType3() {
            return this.classType3;
        }
    }
}
