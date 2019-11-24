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

package li.pitschmann.knx.utils;

import li.pitschmann.knx.test.TestHelpers;
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
    public void testIsNullOrEmpty() {
        assertThat(Strings.isNullOrEmpty(null)).isTrue();
        assertThat(Strings.isNullOrEmpty("")).isTrue();

        assertThat(Strings.isNullOrEmpty(" ")).isFalse();
        assertThat(Strings.isNullOrEmpty("abc")).isFalse();
    }

    @Test
    @DisplayName("Test ToStringHelper")
    public void testToStringHelper() {
        final var testObject = new TestObject();

        // with no field
        assertThat(Strings.toStringHelper(testObject)).isInstanceOf(Strings.ToStringHelper.class);
        assertThat(Strings.toStringHelper(testObject).toString()).isEqualTo("TestObject{}");

        // with single field
        final var helper1 = Strings.toStringHelper(testObject);
        helper1.add("name", "Chris");
        assertThat(helper1.toString()).isEqualTo("TestObject{name=Chris}");

        // with many fields
        final var helper2 = Strings.toStringHelper(testObject);
        helper2.add("key1", "value1")
                .add("key2", "value2")
                .add("key3", "value3");
        assertThat(helper2.toString()).isEqualTo("TestObject{key1=value1, key2=value2, key3=value3}");
    }

    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Strings.class);
    }


    /**
     * Dummy class as we cannot mock 'Class.class'
     */
    private static class TestObject {
        // empty
    }
}
