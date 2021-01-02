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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for {@link Exceptions} utility class
 *
 * @author PITSCHR
 */
class ExceptionsTest {

    @Test
    @DisplayName("Test format using #toErrorMessage(..)")
    void testToErrorMessage() {
        assertThat(Exceptions.toErrorMessage("Null: {}", (Object) null)).isEqualTo("Null: <null>");
        assertThat(Exceptions.toErrorMessage("Null: {}", (Object[]) null)).isEqualTo("Null: <null>");
        assertThat(Exceptions.toErrorMessage("Null: {} {}", null, null)).isEqualTo("Null: <null> <null>");

        assertThat(Exceptions.toErrorMessage("My Message")).isEqualTo("My Message");
        assertThat(Exceptions.toErrorMessage("{} {}!", "Hello", "World")).isEqualTo("Hello World!");
        assertThat(Exceptions.toErrorMessage("1+2 = {}", 1 + 2)).isEqualTo("1+2 = 3");
        assertThat(Exceptions.toErrorMessage("My Message: {} {}!", "Hallo", "Erde")).isEqualTo("My Message: Hallo Erde!");

        final var bytes = new byte[]{(byte) 0xFF, (byte) 0xCE, 0x56};
        assertThat(Exceptions.toErrorMessage("Bytes: {}", bytes)).isEqualTo("Bytes: 0xFF CE 56");
    }

    @Test
    @DisplayName("Test #toErrorMessage(String, Object..) with wrong number of arguments")
    void testToErrorMessageWrongArguments() {
        assertThatThrownBy(() -> Exceptions.toErrorMessage("Null-Arg Error Message", "dummy"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Difference detected between error message and number of arguments" +
                        " [errorMessage: Null-Arg Error Message, args: [dummy]]");
    }


    @Test
    @DisplayName("Constructor not instantiable")
    void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Exceptions.class);
    }
}
