/*
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for {@link Strings} utility class
 *
 * @author PITSCHR
 */
public class StringsTest {

    @Test
    @DisplayName("Test if string is null or empty")
    void testIsNullOrEmpty() {
        assertThat(Strings.isNullOrEmpty(null)).isTrue();
        assertThat(Strings.isNullOrEmpty("")).isTrue();

        assertThat(Strings.isNullOrEmpty(" ")).isFalse();
        assertThat(Strings.isNullOrEmpty("abc")).isFalse();
    }

    @Test
    @DisplayName("Test ToStringHelper with fields")
    void testToStringHelper() {
        final var obj = new Object();

        // with no field
        assertThat(Strings.toStringHelper(obj)).isInstanceOf(Strings.ToStringHelper.class);
        assertThat(Strings.toStringHelper(obj).toString()).isEqualTo("Object{}");

        // with single field
        final var helper1 = Strings.toStringHelper(obj);
        helper1.add("name", "Chris");
        assertThat(helper1.toString()).isEqualTo("Object{name=Chris}");

        // with many fields
        final var helper2 = Strings.toStringHelper(obj);
        helper2.add("key1", "value1")
                .add("key2", "value2")
                .add("key3", "value3");
        assertThat(helper2.toString()).isEqualTo("Object{key1=value1, key2=value2, key3=value3}");
    }

    @Test
    @DisplayName("Test ToStringHelper with an Inner Class")
    void testToStringHelperInnerClass() {
        final var obj = new TestObject();

        assertThat(Strings.toStringHelper(obj)).isInstanceOf(Strings.ToStringHelper.class);
        assertThat(Strings.toStringHelper(obj).toString()).isEqualTo("StringsTest$TestObject{}");
    }

    @Test
    @DisplayName("Test ToStringHelper with an Inner Class (2nd level)")
    void testToStringHelperInnerClass2ndLevel() {
        final var obj = new TestObject.TestInnerClass();

        assertThat(Strings.toStringHelper(obj)).isInstanceOf(Strings.ToStringHelper.class);
        assertThat(Strings.toStringHelper(obj).toString()).isEqualTo("StringsTest$TestObject$TestInnerClass{}");
    }

    @Test
    @DisplayName("Constructor not instantiable")
    void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Strings.class);
    }

    /**
     * Dummy class as we cannot mock 'Class.class'
     */
    private static class TestObject {
        private static class TestInnerClass {
            // empty
        }
    }
}
