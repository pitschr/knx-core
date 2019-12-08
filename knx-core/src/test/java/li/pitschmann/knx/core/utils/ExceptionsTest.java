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

/**
 * Test cases for {@link Exceptions} utility class
 *
 * @author PITSCHR
 */
public class ExceptionsTest {

    @Test
    @DisplayName("Test format using #toErrorMessage(..)")
    public void testToErrorMessage() {
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
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Exceptions.class);
    }
}
